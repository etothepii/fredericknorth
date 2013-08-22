package uk.co.epii.conservatives.fredericknorth;

import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.BuilderMapFrame;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.BuilderMapFrameModel;
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

    private ApplicationContext applicationContext;
    private BuilderMapFrame routableAreaBuilderMapFrame;
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
        if (routableAreaBuilderMapFrame == null) {
            BuilderMapFrameModel builderMapFrameModel =
                    new BuilderMapFrameModel(applicationContext);
            routableAreaBuilderMapFrame =
                    new BuilderMapFrame(applicationContext, builderMapFrameModel);
            routableAreaBuilderMapFrame.setLocationRelativeTo(null);
            routableAreaBuilderMapFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            routableAreaBuilderMapFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    childWindowClosed();
                }
            });
        }
        routableAreaBuilderMapFrame.setVisible(true);

    }

    private void openRouteBuilderMapFrame() {
        if (routeBuilderMapFrame == null) {
            RouteBuilderMapFrameModel routeBuilderMapFrameModel = new RouteBuilderMapFrameModel(applicationContext);
            routeBuilderMapFrame = new RouteBuilderMapFrame(routeBuilderMapFrameModel, applicationContext);
            routeBuilderMapFrame.setLocationRelativeTo(null);
            routableAreaBuilderMapFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
