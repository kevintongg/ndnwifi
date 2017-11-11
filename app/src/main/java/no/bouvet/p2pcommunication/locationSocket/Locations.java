package no.bouvet.p2pcommunication.locationSocket;

import android.location.Location;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

/**
 * Created by sabamahbub on 10/20/17.
 */

public class Locations {
    String deviceAdress;
    double[] locations = new double[6];
    double angle =0;

    public Locations(String device, double longitude, double latitude){
        this.deviceAdress = device;
        this.locations[0] = longitude;
        this.locations[1] = latitude;
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
            this.locations[1] = latitude;
            this.locations[0] = longitude;
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
            Log.d("Angle: ", "" + angle);
        }
    }

    public double getCurrentLongitude(){
        return locations[0];
    }

    public double getCurrentLatitude(){
        return locations[1];
    }

    public double getPreviousLongitude(){
        return locations[2];
    }

    public double getPreviousLatitude(){
        return locations[3];
    }

    public double getOldestLongitude(){
        return locations[4];
    }

    public double getOldestLatitude(){
        return locations[5];
    }

    public String getCurrent(){
        return "CurrentLongitude: " + getCurrentLongitude() + " CurrentLatitude: " + getCurrentLatitude();
    }

    public double[] getCurrentArray(){
        double[] mLocation = new double[2];

        mLocation[0] = getCurrentLongitude();
        mLocation[1] = getCurrentLatitude();

        return mLocation;
    }

    public String getPrevious(){
        return "PreviousLongitude: " + getPreviousLongitude() + " PreviousLatitude: " + getPreviousLatitude();
    }

    public String getOldest(){
        return "OldestLongitude: " + getOldestLongitude() + " OldestLatitude: " + getOldestLatitude();
    }


    public String getLocations(){
        return getOldest() + getPrevious() + getCurrent();
    }
}
