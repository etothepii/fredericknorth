package uk.co.epii.conservatives.fredericknorth.routes;

import java.util.Date;

/**
 * User: James Robinson
 * Date: 25/09/2014
 * Time: 22:10
 */
public interface DistributionModel {

  public String getTitle();
  public void setTitle(String title);
  public String getDescription();
  public void setDescription(String description);
  public Date getDistributionStart();
  public void setDistributionStart(Date distributionStart);
  public byte[] getLeaflet();
  public void setLeaflet(byte[] leaflet);

}
