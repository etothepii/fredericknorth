package uk.co.epii.conservatives.fredericknorth.routeableareabuildergui;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundaryLineController;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.boundedarea.BoundedAreaComboBoxModel;
import uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.boundedarea.BoundedAreaExtensions;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;

/**
 * User: James Robinson
 * Date: 26/07/2013
 * Time: 00:42
 */
public class DefaultBoundedAreaSelectionModel extends AbstractBoundedAreaSelectionModel {

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
                    public void masterParentSelectionChanged(SelectedBoundedAreaChangedEvent e) {}

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
        for (BoundedAreaType boundedAreaType : getRootSelectionTypes()) {
            loadOSKnownInstances(boundedAreaType);
        }
    }

    private void loadOSKnownInstances(BoundedAreaType boundedAreaType) {
        BoundedAreaComboBoxModel boundedAreaComboBoxModel = comboBoxModels.get(boundedAreaType);
            boundedAreaComboBoxModel.addAll(boundaryLineController.getAllOSKnownLazyBoundaryLineFeatures(boundedAreaType));
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
    public void setMasterParentType(BoundedAreaType boundedAreaType) {
        masterBoundedAreaType = boundedAreaType;
        fireMasterParentChangedEvent();
    }

    @Override
    public BoundedArea getSelected(BoundedAreaType boundedAreaType) {
        return comboBoxModels.get(boundedAreaType).getSelectedItem();
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
        return boundedAreaType.getName() + " " + 1;
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
        Element boundedAreasElt = document.createElement("BoundedAreas");
        for (int i = 0; i < comboBoxModel.getSize(); i++) {
            BoundedArea boundedArea = comboBoxModel.getElementAt(i);
            if (boundedArea == null) continue;
            boundedAreasElt.appendChild(boundedArea.toXml(document));
        }
        document.appendChild(boundedAreasElt);
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
        Element boundedAreasElt = document.getDocumentElement();
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
