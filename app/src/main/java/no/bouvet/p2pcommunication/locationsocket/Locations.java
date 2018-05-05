package no.bouvet.p2pcommunication.locationsocket;


/**
 * Created by sabamahbub on 10/20/17.
 */

public class Locations {

  private final String DEVICE_ADDRESS;
  private final double[] LOCATIONS = new double[6];
  private double angle = 0;
  private String heading = "";
  private double distance = 0;

  public Locations(String device, double latitude, double longitude) {
    this.DEVICE_ADDRESS = device;
    this.LOCATIONS[5] = 0;
    this.LOCATIONS[4] = 0;
    this.LOCATIONS[3] = 0;
    this.LOCATIONS[2] = 0;
    this.LOCATIONS[0] = latitude;
    this.LOCATIONS[1] = longitude;
  }

  public Locations(String device) {
    this.DEVICE_ADDRESS = device;
  }

  public void update(String deviceAddress, double latitude, double longitude) {
    if (this.DEVICE_ADDRESS.equals(deviceAddress)) {
      this.LOCATIONS[5] = this.LOCATIONS[3];
      this.LOCATIONS[4] = this.LOCATIONS[2];
      this.LOCATIONS[3] = this.LOCATIONS[1];
      this.LOCATIONS[2] = this.LOCATIONS[0];
      this.LOCATIONS[1] = longitude;
      this.LOCATIONS[0] = latitude;
      updateAngle();
    }
  }

  public void update(String deviceAddress, double[] location) {
    if (this.DEVICE_ADDRESS.equals(deviceAddress) && location.length == 2) {
      this.LOCATIONS[5] = this.LOCATIONS[3];
      this.LOCATIONS[4] = this.LOCATIONS[2];
      this.LOCATIONS[3] = this.LOCATIONS[1];
      this.LOCATIONS[2] = this.LOCATIONS[0];
      this.LOCATIONS[1] = location[1];
      this.LOCATIONS[0] = location[0];
    }
  }

  public int getLength() {
    return LOCATIONS.length;
  }

  private void updateAngle() {
    if (LOCATIONS[2] != 0) {
      angle = Direction
          .getBearings(this.LOCATIONS[2], this.LOCATIONS[3], this.LOCATIONS[0], this.LOCATIONS[1]);
      heading = Direction.getBearingsString(angle);
    }
  }

  public double getCurrentLongitude() {
    return LOCATIONS[1];
  }

  public double getCurrentLatitude() {
    return LOCATIONS[0];
  }

  public double getPreviousLongitude() {
    return LOCATIONS[3];
  }

  public double getPreviousLatitude() {
    return LOCATIONS[2];
  }

  private double getOldestLongitude() {
    return LOCATIONS[5];
  }

  private double getOldestLatitude() {
    return LOCATIONS[4];
  }

  private String getCurrent() {
    return "CurrentLongitude: " + getCurrentLongitude() + " CurrentLatitude: "
        + getCurrentLatitude();
  }

  public double[] getCurrentArray() {
    double[] mLocation = new double[2];

    mLocation[0] = getCurrentLatitude();
    mLocation[1] = getCurrentLongitude();

    return mLocation;
  }

  public void setDistance(double oLat, double oLong, double uLat, double uLong) {
    this.distance = Direction.getDistance(oLat, oLong, uLat, uLong);

  }

  private String getPrevious() {
    return " PreviousLatitude: " + getPreviousLatitude() + "PreviousLongitude: "
        + getPreviousLongitude();
  }

  private String getOldest() {
    return " OldestLatitude: " + getOldestLatitude() + "OldestLongitude: " + getOldestLongitude();
  }

  public String getHeading() {
    return heading;
  }

  public double getAngle() {
    return angle;
  }

  public String getLOCATIONS() {
    return getOldest() + getPrevious() + getCurrent();
  }
}
