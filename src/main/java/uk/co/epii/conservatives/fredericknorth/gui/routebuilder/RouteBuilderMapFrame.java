package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.BoundedAreaSelectionPanel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.EnabledStateChangedEvent;
import uk.co.epii.conservatives.fredericknorth.utilities.EnabledStateChangedListener;
import uk.co.epii.conservatives.fredericknorth.utilities.gui.ProgressTrackerJProgressBar;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * User: James Robinson
 * Date: 06/07/2013
 * Time: 14:00
 */
public class RouteBuilderMapFrame extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(RouteBuilderMapFrame.class);
    private static final String ZoomRateKey = "MapPanelZoomRate";
    private static final String DwellingCountColumnWidthKey = "DwellingCountColumnWidth";
    private static final String WorkingDirectoryKey = "WorkingDirectory";
    private static final String RouteDataFilesFilterKey = "RouteDataFilesFilter";
    private static final String RouteMapFilesFilterKey = "RouteMapFilesFilter";
    private static final String RouteDataFilesFilterDescriptionKey = "RouteDataFilesFilterDescription";
    private static final String RouteMapFilesFilterDescriptionKey = "RouteMapFilesFilterDescription";

    private static final Point mouseOffset = new Point(10, 10);

    private final RouteBuilderMapFrameModel routeBuilderMapFrameModel;
    private final MapPanel mapPanel;
    private final JTable selectedDwellingGroups;
    private final JTable unselectedDwellingGroups;
    private final JButton moveInToRoute;
    private final JButton moveOutOfRoute;
    private final JButton addRoute;
    private final JButton renameRoute;
    private final JButton deleteRoute;
    private final JButton save;
    private final JButton load;
    private final JButton export;
    private final ProgressTrackerJProgressBar progressTracker;
    private final JButton autoGenerate;
    private final BoundedAreaSelectionPanel boundedAreaSelectionPanel;
    private final JComboBox routes;
    private final JScrollPane unselectedDwellingGroupsScrollPane;
    private final JScrollPane selectedDwellingGroupsScrollPane;
    private final RoutedAndUnroutedToolTipFrame routedAndUnroutedToolTipFrame;
    private final JFileChooser fileChooser;
    private final File workingDirectory;
    private final FileFilter routeDataFilesFilter;
    private final FileFilter routeMapFilesFilter;
    private final LogarithmicJSlider targetSizeSlider;
    private final JTextField targetSizeField;

    public RouteBuilderMapFrame(RouteBuilderMapFrameModel RouteBuilderMapFrameModel, ApplicationContext applicationContext) throws HeadlessException {
        progressTracker = new ProgressTrackerJProgressBar(1);
        routeBuilderMapFrameModel = RouteBuilderMapFrameModel;
        routeBuilderMapFrameModel.setProgressTracker(progressTracker);
        double zoomRate = Double.parseDouble(applicationContext.getProperty(ZoomRateKey));
        int dwellingCountColumnWidth = Integer.parseInt(applicationContext.getProperty(DwellingCountColumnWidthKey));
        mapPanel = new MapPanel(this.routeBuilderMapFrameModel.getMapPanelModel(), zoomRate);
        routeDataFilesFilter = new FileNameExtensionFilter(
                applicationContext.getProperty(RouteDataFilesFilterDescriptionKey),
                applicationContext.getProperty(RouteDataFilesFilterKey));
        routeMapFilesFilter = new FileNameExtensionFilter(
                applicationContext.getProperty(RouteMapFilesFilterDescriptionKey),
                applicationContext.getProperty(RouteMapFilesFilterKey));
        routedAndUnroutedToolTipFrame = new RoutedAndUnroutedToolTipFrame(
                routeBuilderMapFrameModel.getRoutedAndUnroutedToolTipModel(), dwellingCountColumnWidth);
        workingDirectory = createWorkingDirectory(applicationContext);
        this.routeBuilderMapFrameModel.getMapPanelModel().addMapPanelDataListener(new MapPanelDataListener() {
            @Override
            public void universeChanged(MapPanelDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        routeBuilderMapFrameModel.getMapPanelModel().zoomToFitUniverse(mapPanel.getSize());
                        setEnabled(true);
                    }
                });
            }
            @Override
            public void mapChanged(MapPanelDataEvent e) {
                mapPanel.repaint();
            }

            @Override
            public void overlaysChanged(MapPanelDataEvent e) {
                mapPanel.repaint();
            }

            @Override
            public void overlaysMouseOverChanged(MapPanelDataEvent e) {
                MapPanelModel mapPanelModel = (MapPanelModel)e.getSource();
                Point mouseAt = mapPanelModel.getMouseAt();
                Point panelLocationOnScreen = mapPanel.getLocationOnScreen();
                Point mouseLocationOnScreen = new Point(panelLocationOnScreen.x + mouseAt.x, panelLocationOnScreen.y + mouseAt.y);
                LOG.debug("Setting location of tool tip frame");
                Point drawAt = new Point(mouseLocationOnScreen.x + mouseOffset.x,
                            mouseLocationOnScreen.y + mouseOffset.y);
                routedAndUnroutedToolTipFrame.setLocation(drawAt.x, drawAt.y);
                routedAndUnroutedToolTipFrame.repaint();
            }
        });
        routeBuilderMapFrameModel.addEnableStateChangedListener(new EnabledStateChangedListener<RouteBuilderMapFrameModel>() {
            @Override
            public void enabledStateChanged(final EnabledStateChangedEvent<RouteBuilderMapFrameModel> e) {
                if (isEnabled() == e.isEnabled()) return;
                LOG.debug("enabledStateChanged: {}", e.isEnabled());
                if (SwingUtilities.isEventDispatchThread()) {
                    LOG.debug("Setting enabled on EventDispatchThread: {}", e.isEnabled());
                    setEnabled(e.isEnabled());
                }
                else {
                    try {
                        LOG.debug("Waiting to set enabled on EventDispatchThread: {}", e.isEnabled());
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                setEnabled(e.isEnabled());
                            }
                        });
                        LOG.debug("Enabled set on EventDispatchThread: {}", e.isEnabled());
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
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals("enabled")) {
                    boolean enabled = (Boolean) propertyChangeEvent.getNewValue();
                    LOG.debug("The enabled status of the Frame has been changed: {}", enabled);
                    mapPanel.setEnabled(enabled);
                    selectedDwellingGroups.setEnabled(enabled);
                    unselectedDwellingGroups.setEnabled(enabled);
                    moveInToRoute.setEnabled(enabled);
                    moveOutOfRoute.setEnabled(enabled);
                    addRoute.setEnabled(enabled);
                    renameRoute.setEnabled(enabled);
                    deleteRoute.setEnabled(enabled);
                    save.setEnabled(enabled);
                    load.setEnabled(enabled);
                    export.setEnabled(enabled);
                    autoGenerate.setEnabled(enabled);
                    boundedAreaSelectionPanel.setEnabled(enabled);
                    routes.setEnabled(enabled);
                    unselectedDwellingGroupsScrollPane.setEnabled(enabled);
                    selectedDwellingGroupsScrollPane.setEnabled(enabled);
                    targetSizeSlider.setEnabled(enabled);
                    targetSizeField.setEnabled(enabled);
                    routeBuilderMapFrameModel.setEnabled(enabled);
                }
            }
        });
        routeBuilderMapFrameModel.getMapPanelModel().addMapPanelDataListener(new MapPanelDataAdapter() {
            @Override
            public void universeChanged(MapPanelDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        routeBuilderMapFrameModel.getMapPanelModel().zoomToFitUniverse(mapPanel.getSize());
                        mapPanel.repaint();
                        progressTracker.finish();
                        setEnabled(true);
                    }
                });
            }
        });
        targetSizeSlider = new LogarithmicJSlider(50, 1000);
        targetSizeField = new JTextField(4);
        fileChooser = new JFileChooser(workingDirectory);
        fileChooser.addChoosableFileFilter(routeDataFilesFilter);
        fileChooser.addChoosableFileFilter(routeMapFilesFilter);
        routeBuilderMapFrameModel.getMapPanelModel().setOverlayRenderer(DottedDwellingGroup.class, new DottedDwellingGroupOverlayRenderer());
        selectedDwellingGroups = createDwellingGroupTable(this.routeBuilderMapFrameModel.getRoutedDwellingGroups());
        unselectedDwellingGroups = createDwellingGroupTable(this.routeBuilderMapFrameModel.getUnroutedDwellingGroups());
        unselectedDwellingGroupsScrollPane = new JScrollPane(unselectedDwellingGroups,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        selectedDwellingGroupsScrollPane = new JScrollPane(selectedDwellingGroups,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        TableHelper.forceColumnWidth(unselectedDwellingGroups.getColumnModel().getColumn(1), dwellingCountColumnWidth);
        TableHelper.forceColumnWidth(selectedDwellingGroups.getColumnModel().getColumn(1), dwellingCountColumnWidth);
        boundedAreaSelectionPanel = new BoundedAreaSelectionPanel(routeBuilderMapFrameModel.getBoundedAreaSelectionModel());
        routes = new JComboBox(this.routeBuilderMapFrameModel.getRoutesModel());
        addRoute = new JButton("Add");
        renameRoute = new JButton("Rename");
        deleteRoute = new JButton("Delete");
        save = new JButton("Save");
        load = new JButton("Load");
        export = new JButton("Export");
        autoGenerate = new JButton("Auto Generate Routes");
        moveInToRoute = new JButton("\u2191");
        moveOutOfRoute = new JButton("\u2193");
        layoutContent();
        addListeners(this);
        updateRouteButtonsEnabledState();
        updateInOutButtonsEnabledState();
        routeBuilderMapFrameModel.updateOverlays();
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
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

    private JTable createDwellingGroupTable(DwellingGroupModel dwellingGroupModel) {
        JTable dwellingGroupTable = new JTable(dwellingGroupModel);
        dwellingGroupTable.setSelectionModel(dwellingGroupModel.getListSelectionModel());
        dwellingGroupTable.setRowSorter(dwellingGroupModel.getRowSorter());
        return dwellingGroupTable;
    }

    private void addListeners(final RouteBuilderMapFrame routeBuilderMapFrame) {
        addRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                routeBuilderMapFrameModel.getRoutesModel().add(getRouteName(routeBuilderMapFrameModel.getRoutesModel().getNextSuggestedRouteName()));
                if (routeBuilderMapFrameModel.getRoutesModel().getSize() == 1) {
                    updateInOutButtonsEnabledState();
                }
            }
        });
        renameRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                routeBuilderMapFrameModel.getRoutesModel().rename(getRouteName(routeBuilderMapFrameModel.getRoutesModel().getSelectedItem().getName()));
            }
        });
        deleteRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                routeBuilderMapFrameModel.getRoutesModel().delete();
            }
        });
        moveInToRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                routeBuilderMapFrameModel.moveSelectedUnroutedDwellingGroupsInToRoute();
                routes.repaint();
                boundedAreaSelectionPanel.repaint();
            }
        });
        moveOutOfRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                routeBuilderMapFrameModel.moveSelectedRoutedDwellingGroupsOutOfRoute();
                routes.repaint();
                boundedAreaSelectionPanel.repaint();
            }
        });
        routes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateRouteButtonsEnabledState();
                routeBuilderMapFrameModel.updateOverlays();
            }
        });
        selectedDwellingGroups.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateInOutButtonsEnabledState();
                    routeBuilderMapFrameModel.updateOverlays();
                }
            }
        });
        unselectedDwellingGroups.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateInOutButtonsEnabledState();
                    routeBuilderMapFrameModel.updateOverlays();
                }
            }
        });
        save.addActionListener(new ActionListener() {
            public File saveTo;

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileFilter(routeDataFilesFilter);
                if (saveTo == null) {
                    fileChooser.setCurrentDirectory(workingDirectory);
                } else {
                    fileChooser.setSelectedFile(saveTo);
                }
                int returnValue = fileChooser.showSaveDialog(routeBuilderMapFrame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    saveTo = fileChooser.getSelectedFile();
                    routeBuilderMapFrameModel.save(fileChooser.getSelectedFile());
                }
            }
        });
        load.addActionListener(new ActionListener() {
            public File loadFrom;

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileFilter(routeDataFilesFilter);
                if (loadFrom == null) {
                    fileChooser.setCurrentDirectory(workingDirectory);
                } else {
                    fileChooser.setSelectedFile(loadFrom);
                }
                int returnValue = fileChooser.showOpenDialog(routeBuilderMapFrame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    loadFrom = fileChooser.getSelectedFile();
                    routeBuilderMapFrameModel.load(loadFrom);
                }
            }
        });
        targetSizeField.getDocument().addDocumentListener(new DocumentListener() {
            boolean updating = false;


            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (updating) {
                    return;
                }
                int value;
                try {
                    value = Integer.parseInt(e.getDocument().getText(0, e.getDocument().getLength()));
                } catch (NumberFormatException nfe) {
                    return;
                } catch (BadLocationException ble) {
                    throw new RuntimeException(ble);
                }
                updating = true;
                targetSizeSlider.setValue(value);
                updating = false;
            }
        });
        targetSizeSlider.addChangeListener(new ChangeListener() {
            boolean updating = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                if (updating) {
                    return;
                }
                updating = true;
                targetSizeField.setText(targetSizeSlider.getValue() + "");
                updating = false;
            }
        });
        autoGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                routeBuilderMapFrameModel.autoGenerate(targetSizeSlider.getValue(), true);
            }
        });
        export.addActionListener(new ActionListener() {
            public File exportTo;

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileFilter(routeMapFilesFilter);
                if (exportTo == null) {
                    fileChooser.setCurrentDirectory(workingDirectory);
                }
                else {
                    fileChooser.setSelectedFile(exportTo);
                }
                int returnValue = fileChooser.showSaveDialog(routeBuilderMapFrame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    exportTo = fileChooser.getSelectedFile();
                    routeBuilderMapFrameModel.export(exportTo);
                }
            }
        });
        mapPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (routeBuilderMapFrameModel.getMapPanelModel().isMouseOverItems()) {
                    for (MouseWheelListener l : routedAndUnroutedToolTipFrame.getScrollerListeners()) {
                        l.mouseWheelMoved(e);
                    }
                }
            }
        });
        targetSizeSlider.setValue(200);
    }

    private void updateInOutButtonsEnabledState() {
        moveOutOfRoute.setEnabled(selectedDwellingGroups.getSelectedRows().length > 0);
        moveInToRoute.setEnabled(routeBuilderMapFrameModel.getRoutesModel().getSelectedItem() != null &&
                unselectedDwellingGroups.getSelectedRows().length > 0);
    }

    private void updateRouteButtonsEnabledState() {
        renameRoute.setEnabled(routeBuilderMapFrameModel.getRoutesModel().getSelectedItem() != null);
        deleteRoute.setEnabled(routeBuilderMapFrameModel.getRoutesModel().getSelectedItem() != null);
    }

    private void layoutContent() {
        getContentPane().setLayout(new GridBagLayout());
        routes.setRenderer(new RouteRenderer());
        JPanel routesAndButtons = new JPanel(new GridBagLayout());
        JPanel ioButtons = new JPanel(new GridBagLayout());
        JPanel inOutButtons = new JPanel(new GridBagLayout());
        routesAndButtons.add(routes, new GridBagConstraints(0, 0, 1, 1, 1d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        routesAndButtons.add(addRoute, new GridBagConstraints(1, 0, 1, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        routesAndButtons.add(renameRoute, new GridBagConstraints(2, 0, 1, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        routesAndButtons.add(deleteRoute, new GridBagConstraints(3, 0, 1, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(boundedAreaSelectionPanel, new GridBagConstraints(0, 0, 3, 1, 1d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        getContentPane().add(routesAndButtons, new GridBagConstraints(0, 1, 3, 1, 1d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
        getContentPane().add(mapPanel, new GridBagConstraints(0, 2, 1, 6, 1d, 1d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 5, 5, 0), 0, 0));
        getContentPane().add(progressTracker, new GridBagConstraints(0, 8, 3, 1, 1d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
        getContentPane().add(selectedDwellingGroupsScrollPane,
                new GridBagConstraints(1, 2, 2, 1, 0d, 1d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        getContentPane().add(unselectedDwellingGroupsScrollPane,
                new GridBagConstraints(1, 4, 2, 1, 0d, 1d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        getContentPane().add(inOutButtons, new GridBagConstraints(1, 3, 2, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        inOutButtons.add(moveInToRoute, new GridBagConstraints(0, 0, 1, 1, 1d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        inOutButtons.add(moveOutOfRoute, new GridBagConstraints(1, 0, 1, 1, 1d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        ioButtons.add(save, new GridBagConstraints(0, 0, 1, 1, 1d, 1d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        ioButtons.add(load, new GridBagConstraints(1, 0, 1, 1, 1d, 1d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        ioButtons.add(export, new GridBagConstraints(2, 0, 1, 1, 1d, 1d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(autoGenerate, new GridBagConstraints(1, 7, 2, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(ioButtons, new GridBagConstraints(1, 5, 2, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        JPanel sliderPanel = new JPanel(new GridBagLayout());
        sliderPanel.add(targetSizeSlider, new GridBagConstraints(0, 0, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        sliderPanel.add(targetSizeField, new GridBagConstraints(1, 0, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(sliderPanel, new GridBagConstraints(1, 6, 2, 1, 0d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        selectedDwellingGroupsScrollPane.setPreferredSize(new Dimension(300, 250));
        unselectedDwellingGroupsScrollPane.setPreferredSize(new Dimension(300, 250));
    }

    private String getRouteName(String proposed) {
        return (String)JOptionPane.showInputDialog(this, "What should this route be called?", "Route",
                JOptionPane.QUESTION_MESSAGE, null, null, proposed);
    }
}
