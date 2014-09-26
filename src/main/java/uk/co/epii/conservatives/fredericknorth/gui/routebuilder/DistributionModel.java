package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import java.util.Date;

/**
 * User: James Robinson
 * Date: 25/09/2014
 * Time: 22:10
 */
public class DistributionModel {

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
