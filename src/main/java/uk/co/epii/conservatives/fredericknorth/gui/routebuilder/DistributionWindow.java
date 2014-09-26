package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

/**
 * User: James Robinson
 * Date: 25/09/2014
 * Time: 23:13
 */
public class DistributionWindow extends JDialog {

  private DistributionModel distributionModel;
  private JTextField title;
  private JTextArea description;
  private JButton yes;
  private JButton no;
  private boolean cancelled;

  public DistributionWindow(Window owner, DistributionModel distributionModel) {
    super(owner, "Would you like to attach these route maps to a Leaflet?", ModalityType.APPLICATION_MODAL);
    getContentPane().setBackground(Color.WHITE);
    title = new JTextField();
    description = new JTextArea();
    description.setBorder(title.getBorder());
    description.setPreferredSize(new Dimension(400, 200));
    yes = new JButton("Yes");
    no = new JButton("No");
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    buttonPanel.setBackground(Color.WHITE);
    getContentPane().setLayout(new GridBagLayout());
    getContentPane().add(title, new GridBagConstraints(0, 3, 1, 1, 1d, 0d,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    getContentPane().add(description, new GridBagConstraints(0, 5, 1, 1, 1d, 1d,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    getContentPane().add(new JLabel("Title"), new GridBagConstraints(0, 2, 1, 1, 1d, 0d,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    getContentPane().add(new JLabel("Description"), new GridBagConstraints(0, 4, 1, 1, 1d, 0d,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    getContentPane().add(buttonPanel, new GridBagConstraints(0, 6, 1, 1, 0d, 0d,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
    buttonPanel.add(yes, new GridBagConstraints(0, 0, 1, 1, 1d, 1d,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(no, new GridBagConstraints(1, 0, 1, 1, 1d, 1d,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 0), 0, 0));
    yes.setPreferredSize(new Dimension(100, 50));
    no.setPreferredSize(new Dimension(100, 50));
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    yes.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        yes();
      }
    });
    no.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        no();
      }
    });
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        no();
      }
    });
    setDistributionModel(distributionModel);
  }

  public void setDistributionModel(DistributionModel distributionModel) {
    this.distributionModel = distributionModel;
    title.setText(distributionModel.getTitle());
    description.setText(distributionModel.getDescription());
  }

  public DistributionModel getDistributionModel() {
    if (cancelled) {
      return null;
    }
    return distributionModel;
  }

  public void yes() {
    cancelled = false;
    close();
  }

  public void no() {
    cancelled = true;
    close();
  }

  private void close() {
    distributionModel.setTitle(title.getText());
    distributionModel.setDescription(description.getText());
    distributionModel.setDistributionStart(new Date());
    setVisible(false);
  }

}
