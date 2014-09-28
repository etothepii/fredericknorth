package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.routes.DistributionModel;

import java.util.Date;

/**
 * User: James Robinson
 * Date: 28/09/2014
 * Time: 20:38
 */

public class DistributionModelImpl implements DistributionModel {

  private String title;
  private String description;
  private Date distributionStart;
  private byte[] leaflet;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getDistributionStart() {
    return distributionStart;
  }

  public void setDistributionStart(Date distributionStart) {
    this.distributionStart = distributionStart;
  }

  public byte[] getLeaflet() {
    return leaflet;
  }

  public void setLeaflet(byte[] leaflet) {
    this.leaflet = leaflet;
  }
}
