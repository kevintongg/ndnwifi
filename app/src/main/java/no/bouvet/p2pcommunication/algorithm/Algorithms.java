package no.bouvet.p2pcommunication.algorithm;

import android.location.LocationManager;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import butterknife.internal.Utils;
import no.bouvet.p2pcommunication.deviceList.Device;
import no.bouvet.p2pcommunication.locationSocket.Direction;
import no.bouvet.p2pcommunication.locationSocket.Locations;
import no.bouvet.p2pcommunication.util.NetworkUtil;

import static java.lang.Double.NaN;
import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceList;
import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceLocations;

/**
 * Created by micha on 3/20/2018.
 */

//TODO: Work in Progress, implement First-hit, flooding, location
public class Algorithms {
    public static final String TAG = "Algorithms";
    String forwardingStrategy = "Flooding";

    public void prepareForwarding(String imageName){
        forwarding(imageName, NetworkUtil.getMyWifiP2pIpAddress());
    }

    public void forwarding(String imageName, String incoming){
        // timeout will start with 10 seconds
        // if a data is received, cancel the timout
        // else, remove the corresponding PIT entries
        Message message = new Message.MessageBuilder(NetworkUtil.getMyWifiP2pIpAddress(), MessageType.INTEREST,
                imageName).build();
        Log.d(TAG, "preparing message: " + imageName);
        // try to forward base on hitmap

        switch(forwardingStrategy){
            case "Flooding":
                flooding(message, imageName, incoming);
                break;
            case "First":
                first(message, imageName, incoming);
                break;
            case "Location":
                locationBased(message, imageName, incoming);
                break;

        }

    }


    //First
    public void first(Message message, String imageName, String ip){
        for(Map.Entry<String, Device> entry : deviceList.entrySet()){
            String key = entry.getKey();
            Device list = entry.getValue();

            if(!list.getIp().equals(ip)){
                //send request
                //wait for response before continuing
                //if response contains data break out of loop
            }
        }
    }

    //Flooding
    public void flooding(Message message, String imageName, String ip){
        for(Map.Entry<String, Device> entry : deviceList.entrySet()){
            String key = entry.getKey();
            Device list = entry.getValue();

            if(!list.getIp().equals(ip)){
                //send request via socket
            }
        }
    }


    //Location
    public void locationBased(Message message, String imageName, String ip){
        String key = "";
        double value = -1;
        double angle;

        if(deviceLocations != null){

            for (java.util.Map.Entry<String, Locations> entry : deviceLocations.entrySet()) {
                String k = entry.getKey();
                Locations v = entry.getValue();

                //replace 0 with source destination
                angle = Direction.angleBetweenThreePoints(v.getPreviousLatitude(), v.getPreviousLongitude(),
                        v.getCurrentLatitude(), v.getCurrentLongitude(), 0, 0);

                if (value == -1 && value != NaN) {
                    key = k;
                    value = angle;
                } else if (value > angle && value != NaN) {
                    key = k;
                    value = angle;
                }
            }

            //send request message to key (ip)

        }
    }


}
