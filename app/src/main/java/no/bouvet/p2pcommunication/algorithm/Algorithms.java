package no.bouvet.p2pcommunication.algorithm;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import no.bouvet.p2pcommunication.deviceList.Device;
import no.bouvet.p2pcommunication.locationSocket.Direction;
import no.bouvet.p2pcommunication.locationSocket.Locations;
import no.bouvet.p2pcommunication.multiclients.Client;
import no.bouvet.p2pcommunication.util.NetworkUtil;

import static java.lang.Double.NaN;
import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceList;
import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceLocations;
import static no.bouvet.p2pcommunication.locationSocket.Direction.getDistance;

/**
 * Created by micha on 3/20/2018.
 */

//TODO: Work in Progress, implement First-hit, flooding, location
public class Algorithms {
    public static final String TAG = "Algorithms";
    public static String forwardingStrategy = "First";
    public static  ArrayList<String> alreadyCalled = new ArrayList<>();

    //Use for Destination Phone
    final public static double SOURCE_LAT = 0;
    final public static double SOURCE_LONG = 0;

    //Use for Source Phone
    final double DEST_LAT = 0;
    final double DEST_LONG = 0;

    public static void forwarding(String ip){

        switch(forwardingStrategy) {
            case "First":
                first(ip);
                break;
            case "Angle-Based":
                locationAngleBased();
                break;
            case "Distance":
                locationDistance();
                break;
            default:

                }
    }


    //First
    public static void first(String ip){
        if(deviceList.size() < alreadyCalled.size()){
            alreadyCalled.clear();
        }

        Client run = new Client();
        run.run(ip);


//        for(Map.Entry<String, Device> entry : deviceList.entrySet()) {
//            Device list = entry.getValue();
//
//            if (list.getIp() != null) {
//                Client run = new Client();
//                run.run(list.getIp());
//
//                Log.d("-----Test", list.getIp());
//                if(!(alreadyCalled.contains(list.getIp())))
//                    alreadyCalled.add(list.getIp());
//            }
//
//        }


    }

//    //Flooding
//    public static void flooding(){
//        for(Map.Entry<String, Device> entry : deviceList.entrySet()){
//            Device list = entry.getValue();
//            String ip = list.getIp();
//
//            if(!list.getIp().equals(ip)){
//                //send request via socket
//            }
//        }
//    }


    //Location angle
    public static void locationAngleBased(){
        String ipKey = "";
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
                    ipKey = k;
                    value = angle;
                } else if (value > angle && value != NaN) {
                    ipKey = k;
                    value = angle;
                }
            }

            Client run = new Client();
            run.run(ipKey);

        }
    }

    //Location angle
    public static void locationDistance(){
        double distance = 0;
        String ipKey = "";
        double value = -1;

        if(deviceLocations != null){

            for (java.util.Map.Entry<String, Locations> entry : deviceLocations.entrySet()) {
                String k = entry.getKey();
                Locations v = entry.getValue();

                distance = getDistance(v.getCurrentLatitude(), v.getCurrentLongitude(), SOURCE_LAT, SOURCE_LONG);

                if (value == -1 && value != NaN) {
                    ipKey = k;
                    value = distance;
                } else if (value > distance && value != NaN) {
                    ipKey = k;
                    value = distance;
                }
            }

            Client run = new Client();
            run.run(ipKey);

        }
    }



}
