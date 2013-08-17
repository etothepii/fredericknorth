package uk.co.epii.conservatives.fredericknorth.routeableareabuildergui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.boundedarea.BoundedAreaConstructor;
import uk.co.epii.conservatives.fredericknorth.utilities.EnabledStateChangedEvent;
import uk.co.epii.conservatives.fredericknorth.utilities.EnabledStateChangedListener;
import uk.co.epii.conservatives.fredericknorth.utilities.gui.ProgressTrackerJProgressBar;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * User: James Robinson
 * Date: 20/07/2013
 * Time: 17:19
 */
public class BuilderMapFrame extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(BuilderMapFrame.class);

    private static final String ZoomRateKey = "MapPanelZoomRate";
    private static final String BoundedAreaDataFilesFilterDescriptionKey = "BoundedAreaDataFilesFilterDescription";
    private static final String BoundedAreaDataFilesFilterKey = "BoundedAreaDataFilesFilter";
    private static final String BoundedAreaReportCSVFilesFilterDescriptionKey = "BoundedAreaReportCSVFilesFilterDescription";
    private static final String BoundedAreaReportCSVFilesFilterKey = "BoundedAreaReportCSVFilesFilter";
    private static final String BoundedAreaReportMapFilesFilterDescriptionKey = "BoundedAreaReportMapFilesFilterDescription";
    private static final String boundedAreaReportMapFilesFilterKey = "boundedAreaReportMapFilesFilter";
    private static final String WorkingDirectoryKey = "WorkingDirectory";

    private final BuilderMapFrameModel builderMapFrameModel;
    private final BoundedAreaSelectionPanel boundedAreaSelectionPanel;
    private final MapPanel mapPanel;
    private final ManipulateBoundedAreaPopupMenu manipulateBoundedAreaPopupMenu;
    private final JButton saveButton;
    private final JButton loadButton;
    private final JButton report;
    private final ProgressTrackerJProgressBar progressTracker;
    private final JFileChooser fileChooser;
    private final FileFilter boundedAreaDataFilesFilter;
    private final File workingDirectory;
    private final FileFilter boundedAreaReportCSVFilesFilter;
    private final FileFilter boundedAreaReportMapFilesFilter;

    public BuilderMapFrame(ApplicationContext applicationContext,
                           BuilderMapFrameModel builderMapFrameModeL) {
        this.builderMapFrameModel = builderMapFrameModeL;
        boundedAreaDataFilesFilter = new FileNameExtensionFilter(
                applicationContext.getProperty(BoundedAreaDataFilesFilterDescriptionKey),
                applicationContext.getProperty(BoundedAreaDataFilesFilterKey));
        boundedAreaReportCSVFilesFilter = new FileNameExtensionFilter(
                applicationContext.getProperty(BoundedAreaReportCSVFilesFilterDescriptionKey),
                applicationContext.getProperty(BoundedAreaReportCSVFilesFilterKey));
        boundedAreaReportMapFilesFilter = new FileNameExtensionFilter(
                applicationContext.getProperty(BoundedAreaReportMapFilesFilterDescriptionKey),
                applicationContext.getProperty(boundedAreaReportMapFilesFilterKey));
        fileChooser = new JFileChooser();
        manipulateBoundedAreaPopupMenu = new ManipulateBoundedAreaPopupMenu(BoundedAreaType.UNITARY_DISTRICT);
        boundedAreaSelectionPanel = new BoundedAreaSelectionPanel(
                builderMapFrameModel.getBoundedAreaSelectionModel());
        double zoomRate = Double.parseDouble(applicationContext.getProperty(ZoomRateKey));
        this.mapPanel = new MapPanel(this.builderMapFrameModel.getMapPanelModel(), zoomRate);
        builderMapFrameModel.getMapPanelModel().addMapPanelDataListener(new MapPanelDataAdapter() {
            @Override
            public void universeChanged(MapPanelDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        builderMapFrameModel.getMapPanelModel().zoomToFitUniverse(mapPanel.getSize());
                        mapPanel.repaint();
                        progressTracker.finish();
                        setEnabled(true);
                    }
                });
            }
        });
        builderMapFrameModel.addEnableStateChangedListener(new EnabledStateChangedListener<BuilderMapFrameModel>() {
            @Override
            public void enabledStateChanged(final EnabledStateChangedEvent<BuilderMapFrameModel> e) {
                if (isEnabled() == e.isEnabled()) return;
                if (SwingUtilities.isEventDispatchThread()) {
                    setEnabled(e.isEnabled());
                }
                else {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                setEnabled(e.isEnabled());
                            }
                        });
                    }
                    catch (InterruptedException ie) {
                        throw new RuntimeException(ie);
                    }
                    catch (InvocationTargetException ite) {
                        throw new RuntimeException(ite);
                    }
                }
            }
        });
        saveButton = new JButton("Save");
        loadButton = new JButton("Load");
        report = new JButton("Build Report");
        progressTracker = new ProgressTrackerJProgressBar(1);
        builderMapFrameModel.setProgressTracker(progressTracker);
        workingDirectory = createWorkingDirectory(applicationContext);
        initateLayout();
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        addListeners(this);
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals("enabled")) {
                    boolean enabled = (Boolean)propertyChangeEvent.getNewValue();
                    mapPanel.setEnabled(enabled);
                    boundedAreaSelectionPanel.setEnabled(enabled);
                    saveButton.setEnabled(enabled);
                    loadButton.setEnabled(enabled);
                    report.setEnabled(enabled);
                    builderMapFrameModel.setEnabled(enabled);
                }
            }
        });
    }

    private void addListeners(final BuilderMapFrame builderMapFrame) {
        builderMapFrameModel.getMapPanelModel().addMapPanelDataListener(new MapPanelDataAdapter() {
            @Override
            public void overlaysChanged(MapPanelDataEvent e) {
                mapPanel.repaint();
            }
        });
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                LOG.debug("Clicked: {}", e.getButton());
                LOG.debug("Clicked Button 3: {}", e.getButton() == MouseEvent.BUTTON3);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    BoundedArea mouseOver = builderMapFrameModel.getBoundedAreaOver();
                    manipulateBoundedAreaPopupMenu.setBoundedArea(mouseOver == null ? null : mouseOver);
                    manipulateBoundedAreaPopupMenu.show(mapPanel, e.getX(), e.getY());
                }
            }
        });
        manipulateBoundedAreaPopupMenu.addCreateChildActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                constructOverlay(true);
            }
        });
        manipulateBoundedAreaPopupMenu.addCreateSiblingActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                constructOverlay(false);
            }
        });
        manipulateBoundedAreaPopupMenu.addRenameActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BoundedArea selection = manipulateBoundedAreaPopupMenu.getBoundedArea();
                BoundedAreaType type = selection.getBoundedAreaType();
                String name = getNameToUse(type, selection.getName());
                if (name != null) {
                    selection.setName(name);
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            public File saveTo = new File(workingDirectory.getAbsolutePath() + "/boundaries.xml");

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileFilter(boundedAreaDataFilesFilter);
                if (saveTo == null) {
                    fileChooser.setCurrentDirectory(workingDirectory);
                } else {
                    fileChooser.setSelectedFile(saveTo);
                }
                int returnValue = fileChooser.showSaveDialog(boundedAreaSelectionPanel);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    saveTo = fileChooser.getSelectedFile();
                    builderMapFrameModel.save(saveTo);
                }
            }
        });
        loadButton.addActionListener(new ActionListener() {
            public File loadFrom = new File(workingDirectory.getAbsolutePath() + "/boundaries.xml");

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileFilter(boundedAreaDataFilesFilter);
                if (loadFrom == null) {
                    fileChooser.setCurrentDirectory(workingDirectory);
                } else {
                    fileChooser.setSelectedFile(loadFrom);
                }
                int returnValue = fileChooser.showOpenDialog(boundedAreaSelectionPanel);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    loadFrom = fileChooser.getSelectedFile();
                    builderMapFrameModel.load(loadFrom);
                }
            }
        });
        report.addActionListener(new ActionListener() {
            public File largeMap = new File(workingDirectory.getAbsolutePath() + "/report.png") ;
            public File csvFile = new File(workingDirectory.getAbsolutePath() + "/report.csv");

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileFilter(boundedAreaReportMapFilesFilter);
                if (largeMap == null) {
                    fileChooser.setCurrentDirectory(workingDirectory);
                } else {
                    fileChooser.setSelectedFile(largeMap);
                }
                int returnValue = fileChooser.showSaveDialog(boundedAreaSelectionPanel);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    largeMap = fileChooser.getSelectedFile();
                }
                else {
                    return;
                }
                fileChooser.setFileFilter(boundedAreaReportCSVFilesFilter);
                if (csvFile == null) {
                    fileChooser.setCurrentDirectory(workingDirectory);
                } else {
                    fileChooser.setSelectedFile(csvFile);
                }
                returnValue = fileChooser.showSaveDialog(boundedAreaSelectionPanel);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    csvFile = fileChooser.getSelectedFile();
                }
                else {
                    return;
                }
                builderMapFrameModel.buildReport(largeMap, csvFile);
            }
        });
    }

    private void constructOverlay(boolean child) {
        BoundedArea selection = manipulateBoundedAreaPopupMenu.getBoundedArea();
        BoundedAreaType type = child ? selection.getBoundedAreaType().getChildType() : selection.getBoundedAreaType();
        String name = getNameToUse(type,
                builderMapFrameModel.getBoundedAreaSelectionModel().getNextSuggestedName(type));
        if (name != null) {
            BoundedArea parent = child ? selection :
            builderMapFrameModel.getBoundedAreaSelectionModel().getParent(selection);
            builderMapFrameModel.getConstructorOverlay().setBoundedAreaConstructor(
                    new BoundedAreaConstructor(parent, type, name));
            builderMapFrameModel.getMapPanelModel().addOverlay(builderMapFrameModel.getConstructorOverlay());
        }
    }

    private void initateLayout() {
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(boundedAreaSelectionPanel, new GridBagConstraints(
                0, 0, 2, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(mapPanel, new GridBagConstraints(
                0, 1, 2, 1, 1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        JPanel saveLoadPanel = new JPanel(new GridBagLayout());
        saveLoadPanel.add(report, new GridBagConstraints(0, 0, 1, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        saveLoadPanel.add(saveButton, new GridBagConstraints(1, 0, 1, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        saveLoadPanel.add(loadButton, new GridBagConstraints(2, 0, 1, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(saveLoadPanel, new GridBagConstraints(1, 2, 1, 1, 0d, 0d, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(progressTracker, new GridBagConstraints(0, 2, 1, 1, 1d, 0d, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
    }

    private File createWorkingDirectory(ApplicationContext applicationContext) {
        String workingDirectoryString = System.getProperty("user.home");
        workingDirectoryString +=
                (workingDirectoryString.endsWith("/") || workingDirectoryString.endsWith("\\")) ? "" : "/";
        workingDirectoryString += applicationContext.getProperty(WorkingDirectoryKey);
        LOG.debug(String.format("Creating directory: %s", workingDirectoryString));
        File workingDirectory = new File(workingDirectoryString);
        workingDirectory.mkdirs();
        return workingDirectory;
    }

    private String getNameToUse(BoundedAreaType type, String proposed) {
        return (String)JOptionPane.showInputDialog(this, String.format("What should this %s be called?",
                type.getName()), "Route", JOptionPane.QUESTION_MESSAGE, null, null, proposed);
    }
}
