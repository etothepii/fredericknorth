package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundaryLineController;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.BoundedAreaComboBoxModel;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.BoundedAreaExtensions;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;
import uk.co.epii.conservatives.fredericknorth.utilities.StringExtentions;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * User: James Robinson
 * Date: 26/07/2013
 * Time: 00:42
 */
public class DefaultBoundedAreaSelectionModel extends AbstractBoundedAreaSelectionModel {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBoundedAreaSelectionModel.class);

    private final EnumMap<BoundedAreaType, BoundedAreaComboBoxModel> comboBoxModels;
    private final BoundaryLineController boundaryLineController;
    private BoundedAreaType[] masterBoundedAreaTypes;
    private BoundedAreaType masterBoundedAreaType;
    private XMLSerializer xmlSerializer;

    public DefaultBoundedAreaSelectionModel(ApplicationContext applicationContext) {
        this(applicationContext, null, null);
    }

    public DefaultBoundedAreaSelectionModel(ApplicationContext applicationContext,
                                            BoundedAreaType[] masterBoundedAreaTypes,
                                            BoundedAreaType selectedMasterBoundedAreaType) {
        this.xmlSerializer = applicationContext.getDefaultInstance(XMLSerializer.class);
        this.boundaryLineController = applicationContext.getDefaultInstance(BoundaryLineController.class);
        this.masterBoundedAreaTypes =
                masterBoundedAreaTypes == null ? getDefaultRootSelectionTypes() : masterBoundedAreaTypes;
        masterBoundedAreaType = selectedMasterBoundedAreaType == null ?
                getRootSelectionTypes()[0] : selectedMasterBoundedAreaType;
        comboBoxModels = new EnumMap<BoundedAreaType, BoundedAreaComboBoxModel>(BoundedAreaType.class);
        for (BoundedAreaType boundedAreaType : getSelectionTypes()) {
            comboBoxModels.put(boundedAreaType, BoundedAreaExtensions.getComboBoxModel(boundedAreaType, null));
        }
        for (final BoundedAreaType boundedAreaType : getSelectionTypes()) {
            comboBoxModels.get(boundedAreaType).addSelectedBoundedAreaChangedListener(
                    new SelectedBoundedAreaChangedListener() {
                        @Override
                        public void masterParentSelectionChanged(SelectedBoundedAreaChangedEvent e) {
                        }

                        @Override
                        public void selectionChanged(SelectedBoundedAreaChangedEvent e) {
                            if (boundedAreaType.getChildType() != null) {
                                BoundedAreaType child = boundedAreaType.getChildType();
                                BoundedAreaComboBoxModel comboBoxModel = comboBoxModels.get(child);
                                BoundedArea selected = e.getSelected();
                                comboBoxModel.setParent(selected);
                            }
                            fireSelectionChangedEvent(e.getSelected());
                        }
                    }
            );
        }
    }

    @Override
    public void loadOSKnownInstances() {
        for (BoundedAreaType boundedAreaType : getRootSelectionTypes()) {
            LOG.debug("Loading OS known instances of {}", boundedAreaType.getName());
            loadOSKnownInstances(boundedAreaType);
            LOG.debug("Loaded OS known instances of {}", boundedAreaType.getName());
        }
    }

    @Override
    public Map<BoundedAreaType, BoundedArea> getAllSelected() {
        BoundedAreaType currentType = masterBoundedAreaType;
        EnumMap<BoundedAreaType, BoundedArea> selected = new EnumMap<BoundedAreaType, BoundedArea>(BoundedAreaType.class);
        do {
            selected.put(currentType, getSelected(currentType));
        } while ((currentType = currentType.getChildType()) != null);
        return selected;
    }

    @Override
    public BoundedArea getSelected() {
        BoundedAreaType boundedAreaType = getMasterSelectedType();
        BoundedArea selected = getSelected(boundedAreaType);
        while ((boundedAreaType = boundedAreaType.getChildType()) != null) {
            BoundedArea selectedChild = getSelected(boundedAreaType);
            if (selectedChild != null) {
                selected = selectedChild;
            }
            else {
                break;
            }
        }
        return selected;
    }

    private void loadOSKnownInstances(BoundedAreaType boundedAreaType) {
        BoundedAreaComboBoxModel boundedAreaComboBoxModel = comboBoxModels.get(boundedAreaType);
        if (boundaryLineController != null) {
            boundedAreaComboBoxModel.addAll(
                    boundaryLineController.getAllOSKnownLazyBoundaryLineFeatures(boundedAreaType));
        }
    }

    private BoundedAreaType[] calculateSelectionTypes(BoundedAreaType masterBoundedAreaType) {
        List<BoundedAreaType> boundedAreaTypes = new ArrayList<BoundedAreaType>(16);
        BoundedAreaType child = masterBoundedAreaType;
        do {
            boundedAreaTypes.add(child);
        }
        while ((child = child.getChildType()) != null);
        return boundedAreaTypes.toArray(new BoundedAreaType[boundedAreaTypes.size()]);
    }

    @Override
    public BoundedAreaType[] getSelectionTypes() {
        return BoundedAreaType.values();
    }

    private BoundedAreaType[] getDefaultRootSelectionTypes() {
        return BoundedAreaType.orphans;
    }

    @Override
    public BoundedAreaType[] getRootSelectionTypes() {
        if (masterBoundedAreaType == null) {
            return getDefaultRootSelectionTypes();
        }
        return masterBoundedAreaTypes;
    }

    @Override
    public ComboBoxModel getComboBoxModel(BoundedAreaType type) {
        return comboBoxModels.get(type);
    }

    @Override
    public int getChildTypes() {
        int children = 1;
        BoundedAreaType boundedAreaType = masterBoundedAreaType;
        while ((boundedAreaType = boundedAreaType.getChildType()) != null) {
            children++;
        }
        return children;
    }

    @Override
    public void setMasterParentType(BoundedAreaType masterBoundedAreaType) {
        this.masterBoundedAreaType = masterBoundedAreaType;
        if (masterBoundedAreaType.getChildType() != null) {
            BoundedAreaType parentType = masterBoundedAreaType;
            for (BoundedAreaType childType : masterBoundedAreaType.getChildType().getAllPossibleDecendentTypes()) {
                BoundedArea parent = comboBoxModels.get(parentType).getSelectedItem();
                comboBoxModels.get(childType).setParent(parent);
                parentType = childType;
            }
        }
        fireMasterParentChangedEvent();
    }

    @Override
    public BoundedArea getSelected(BoundedAreaType boundedAreaType) {
        return comboBoxModels.get(boundedAreaType).getSelectedItem();
    }

    @Override
    public BoundedArea getMasterSelected() {
        return getSelected(getMasterSelectedType());
    }

    @Override
    public BoundedAreaType getMasterSelectedType() {
        return masterBoundedAreaType;
    }

    @Override
    public int getMaximumBoundedAreaGenerations() {
        int max = Integer.MIN_VALUE;
        for (BoundedAreaType boundedAreaType : getSelectionTypes()) {
            max = Math.max(max, boundedAreaType.getMaximumGenerations());
        }
        return max;
    }

    @Override
    public BoundedAreaType[] getVisibleBoundedAreaSelectorTypes() {
        List<BoundedAreaType> visibleTypes = new ArrayList<BoundedAreaType>(getMaximumBoundedAreaGenerations());
        visibleTypes.add(masterBoundedAreaType);
        BoundedAreaType child = masterBoundedAreaType;
        while ((child = child.getChildType()) != null) {
            visibleTypes.add(child);
        }
        return visibleTypes.toArray(new BoundedAreaType[visibleTypes.size()]);
    }

    @Override
    public String getNextSuggestedName(BoundedAreaType boundedAreaType) {
        return getNextSuggestedName(boundedAreaType, getParentType(boundedAreaType));
    }

    private String getNextSuggestedName(BoundedAreaType boundedAreaType, BoundedAreaType parentType) {
        switch (boundedAreaType) {
            case POLLING_DISTRICT:
                String name = getSelected(parentType).getName();
                String initials = StringExtentions.getAbbreviation(name.substring(0, name.length() - 5), 2);
                for (int i = 1; true; i++) {
                    String testing = initials + i;
                    ListModel model = getComboBoxModel(boundedAreaType);
                    boolean found = false;
                    for (int n = 1; n < model.getSize() && !found; n++) {
                        found = ((BoundedArea)model.getElementAt(n)).getName().equals(testing);
                    }
                    if (!found) {
                        return testing;
                    }
                }
            default:
                return boundedAreaType.getName() + " " + 1;
        }
    }

    private BoundedAreaType getParentType(BoundedAreaType boundedAreaType) {
        if (boundedAreaType == masterBoundedAreaType) return null;
        BoundedAreaType testing = masterBoundedAreaType;
        while (testing != null && testing.getChildType() != boundedAreaType) {
            testing = testing.getChildType();
        }
        return testing;
    }

    @Override
    public BoundedArea getParent(BoundedArea selection) {
        BoundedAreaType parentType = null;
        BoundedAreaType childType = masterBoundedAreaType;
        do {
            if (selection.getBoundedAreaType() == childType) {
                return parentType == null ? null : getSelected(parentType);
            }
            parentType = childType;
        }
        while ((childType = parentType.getChildType()) != null);
        throw new IllegalArgumentException("No parent can be found");
    }

    @Override
    public void add(BoundedArea parent, BoundedArea boundedArea) {
        if (parent != null) {
            parent.addChild(boundedArea);
        }
        BoundedAreaComboBoxModel boundedAreaComboBoxModel = comboBoxModels.get(boundedArea.getBoundedAreaType());
        if (parent == null) {
            boundedAreaComboBoxModel.add(boundedArea);
        }
        else {
            boundedAreaComboBoxModel.refresh();
        }
        boundedAreaComboBoxModel.setSelectedItem(boundedArea);
    }

    @Override
    public void saveAll(File selectedFile) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = documentBuilder.newDocument();
        BoundedAreaComboBoxModel comboBoxModel = comboBoxModels.get(masterBoundedAreaType);
        Element dataElt = document.createElement("Data");
        Element boundedAreasElt = document.createElement("BoundedAreas");
        BoundedArea boundedArea = comboBoxModel.getSelectedItem();
        boundedAreasElt.appendChild(boundedArea.toXml(document));
        document.appendChild(dataElt);
        Element meetingPointsElt = document.createElement("MeetingPoints");
        for (MeetingPoint meetingPoint : getMeetingPoints()) {
            meetingPointsElt.appendChild(meetingPoint.toXml(document));
        }
        dataElt.appendChild(boundedAreasElt);
        dataElt.appendChild(meetingPointsElt);
        String toWrite = xmlSerializer.toString(document);
        try {
            FileUtils.writeStringToFile(selectedFile, toWrite);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadFrom(File selectedFile, ApplicationContext applicationContext) {
        Document document = xmlSerializer.fromFile(selectedFile);
        Element dataElt = document.getDocumentElement();
        if (dataElt.getTagName().equals("BoundedAreas")) {
            loadBoundedAreas(applicationContext, dataElt);
            getMeetingPoints().clear();
        }
        else {
            Element boundedAreasElt = (Element)dataElt.getElementsByTagName("BoundedAreas").item(0);
            Element meetingPointsElt = (Element)dataElt.getElementsByTagName("MeetingPoints").item(0);
            loadBoundedAreas(applicationContext, boundedAreasElt);
            loadMeetingPoints(meetingPointsElt);
        }
    }

    private void loadMeetingPoints(Element meetingPointsElt) {
        NodeList meetingPointsNodeList = meetingPointsElt.getElementsByTagName("MeetingPoint");
        getMeetingPoints().clear();
        for (int i = 0; i < meetingPointsNodeList.getLength(); i++) {
            getMeetingPoints().add(MeetingPoint.parse((Element)meetingPointsNodeList.item(i)));
        }
    }

    private void loadBoundedAreas(ApplicationContext applicationContext, Element boundedAreasElt) {
        if (!boundedAreasElt.getTagName().equals("BoundedAreas")) throw new IllegalArgumentException(
                "The element provided is not a Council Tag");
        NodeList routesNodeList = boundedAreasElt.getElementsByTagName("BoundedArea");
        BoundedAreaFactory boundedAreaFactory =
                applicationContext.getDefaultInstance(BoundedAreaFactory.class);
        HashSet<BoundedAreaType> typesWitnessed = new HashSet<BoundedAreaType>();
        List<BoundedArea> masterAreas = new ArrayList<BoundedArea>(routesNodeList.getLength());
        for (int i = 0; i < routesNodeList.getLength(); i++) {
            Element elt = (Element)routesNodeList.item(i);
            if (elt.getParentNode() != boundedAreasElt) {
                continue;
            }
            BoundedArea boundedArea = boundedAreaFactory.load(elt);
            typesWitnessed.add(boundedArea.getBoundedAreaType());
            masterAreas.add(boundedArea);
        }
        if (typesWitnessed.size() != 1) {
            throw new IllegalArgumentException(
                    "The file you have tried to load is incorrectly formatted it " +
                            "does not contain precisely one type at the root level");
        }
        setMasterParentType(typesWitnessed.iterator().next());
        comboBoxModels.get(masterBoundedAreaType).setParentlessBoundedArea(masterAreas);
    }
}
