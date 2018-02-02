package no.bouvet.p2pcommunication.locationSocket;


import android.util.Log;

/**
 * Created by sabamahbub on 10/20/17.
 */

public class Locations {
    String deviceAdress;
    double[] locations = new double[6];
    double angle =0;
    String heading = "";

    public Locations(String device, double longitude, double latitude){
        this.deviceAdress = device;
        this.locations[0] = latitude;
        this.locations[1] = longitude;
    }

    public Locations(String device){
        this.deviceAdress = device;
    }

    public void update(String deviceAdress, double longitude, double latitude){
        if(this.deviceAdress.equals(deviceAdress)) {
            this.locations[5] = this.locations[3];
            this.locations[4] = this.locations[2];
            this.locations[3] = this.locations[1];
            this.locations[2] = this.locations[0];
            this.locations[1] = longitude;
            this.locations[0] = latitude;
            updateAngle();
        }
    }

    public void update(String deviceAdress, double[] location){
        if(this.deviceAdress.equals(deviceAdress) && location.length == 2) {
            this.locations[5] = this.locations[3];
            this.locations[4] = this.locations[2];
            this.locations[3] = this.locations[1];
            this.locations[2] = this.locations[0];
            this.locations[1] = location[1];
            this.locations[0] = location[0];
        }
    }

    public void updateAngle(){
        if(locations[2] != 0){
            angle = Direction.getBearings(this.locations[2], this.locations[3], this.locations[0], this.locations[1]);
            heading = Direction.getBearingsString(angle);
           // Log.d("Angle of other Phone: ", "" + angle);
           // Log.d("Location of other Phone" , "Current Latitude and Longitude: "+ locations[0] + ",  " + locations[1] + " || Previous Latitude and Longitude:  "+ locations[2] + ", "  + locations[3]);
        }
    }

    public double getCurrentLongitude(){
        return locations[1];
    }

    public double getCurrentLatitude(){
        return locations[0];
    }

    public double getPreviousLongitude(){
        return locations[3];
    }

    public double getPreviousLatitude(){
        return locations[2];
    }

    public double getOldestLongitude(){
        return locations[5];
    }

    public double getOldestLatitude(){
        return locations[4];
    }

    public String getCurrent(){
        return "CurrentLongitude: " + getCurrentLongitude() + " CurrentLatitude: " + getCurrentLatitude();
    }

    public double[] getCurrentArray(){
        double[] mLocation = new double[2];

        mLocation[0] = getCurrentLatitude();
        mLocation[1] = getCurrentLongitude();

        return mLocation;
    }

    public String getPrevious(){
        return " PreviousLatitude: " + getPreviousLatitude() +  "PreviousLongitude: " + getPreviousLongitude();
    }

    public String getOldest(){
        return " OldestLatitude: " + getOldestLatitude() + "OldestLongitude: " + getOldestLongitude();
    }

    public String getHeading(){
        return heading;
    }

    public double getAngle(){
        return angle;
    }

    public String getLocations(){
        return getOldest() + getPrevious() + getCurrent();
    }
}
