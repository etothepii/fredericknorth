package uk.co.epii.conservatives.fredericknorth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.RoutableAreaBuilderMapFrame;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.RoutableAreaBuilderPanelModel;
import uk.co.epii.conservatives.fredericknorth.gui.routebuilder.RouteBuilderMapFrame;
import uk.co.epii.conservatives.fredericknorth.gui.routebuilder.RouteBuilderMapFrameModel;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * User: James Robinson
 * Date: 22/08/2013
 * Time: 10:34
 */
public class MainMenu extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(MainMenu.class);

    private ApplicationContext applicationContext;
    private RoutableAreaBuilderMapFrame routableAreaRoutableAreaBuilderMapFrame;
    private RouteBuilderMapFrame routeBuilderMapFrame;
    private JButton routableAreaBuilder;
    private JButton routeBuilder;
    private JButton exit;

    public MainMenu(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        exit = new JButton("Exit");
        routeBuilder = new JButton("Build Routes");
        routableAreaBuilder = new JButton("Build Routable Area");
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(routableAreaBuilder, new GridBagConstraints(0, 0, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 20));
        getContentPane().add(routeBuilder, new GridBagConstraints(0, 1, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 20));
        getContentPane().add(exit, new GridBagConstraints(0, 2, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0), 0, 20));
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        routableAreaBuilder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                openRoutableAreaBuilderMapFrame();
                LOG.debug("Opened Routable Area Builder Map Frame");
            }
        });
        routeBuilder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                openRouteBuilderMapFrame();
            }
        });
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void openRoutableAreaBuilderMapFrame() {
        if (routableAreaRoutableAreaBuilderMapFrame == null) {
            RoutableAreaBuilderPanelModel routableAreaBuilderPanelModel =
                    new RoutableAreaBuilderPanelModel(applicationContext);
            routableAreaRoutableAreaBuilderMapFrame =
                    new RoutableAreaBuilderMapFrame(applicationContext, routableAreaBuilderPanelModel);
            routableAreaRoutableAreaBuilderMapFrame.setLocationRelativeTo(null);
            routableAreaRoutableAreaBuilderMapFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            routableAreaRoutableAreaBuilderMapFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    childWindowClosed();
                }
            });
        }
        routableAreaRoutableAreaBuilderMapFrame.setVisible(true);

    }

    private void openRouteBuilderMapFrame() {
        if (routeBuilderMapFrame == null) {
            RouteBuilderMapFrameModel routeBuilderMapFrameModel = new RouteBuilderMapFrameModel(applicationContext);
            routeBuilderMapFrame = new RouteBuilderMapFrame(routeBuilderMapFrameModel, applicationContext);
            routeBuilderMapFrame.setLocationRelativeTo(null);
            routeBuilderMapFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            routeBuilderMapFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    childWindowClosed();
                }
            });
        }
        routeBuilderMapFrame.setVisible(true);

    }

    private void childWindowClosed() {
        setVisible(true);
    }
}
