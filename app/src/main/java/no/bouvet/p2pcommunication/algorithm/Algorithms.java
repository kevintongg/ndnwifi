package no.bouvet.p2pcommunication.algorithm;

import android.os.AsyncTask;
import android.util.Log;
import java.util.Map;

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

    //Use for Destination Phone
    final public static double SOURCE_LAT = 0;
    final public static double SOURCE_LONG = 0;

    //Use for Source Phone
    final double DEST_LAT = 0;
    final double DEST_LONG = 0;

    public static void forwarding(){


        switch(forwardingStrategy){
            case "Flooding":
                flooding();
                break;
            case "First":
                first();
                break;
            case "Angle-Based":
                locationAngleBased();
                break;
            case "Distance":
                locationDistance();
                break;
            default:
                flooding();

        }

    }


    //First
    public static void first(){
        for(Map.Entry<String, Device> entry : deviceList.entrySet()){
            String key = entry.getKey();
            Device list = entry.getValue();

            if(!list.getIp().isEmpty()){
                Client run = new Client();
                run.run(list.getIp());
            }
        }
    }

    //Flooding
    public static void flooding(){
        for(Map.Entry<String, Device> entry : deviceList.entrySet()){
            String key = entry.getKey();
            Device list = entry.getValue();
            String ip = list.getIp();

            if(!list.getIp().equals(ip)){
                //send request via socket
            }
        }
    }


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

            //send request message to key (ip)

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

            //send request message to key (ip)

        }
    }

    //Possible Timeout/Wait
    public class TimeoutTask extends AsyncTask<String, Integer, Long> {
        public static final String TAG = "timeouttask";
        private final Integer timeout = 2000;
        private String name;
        protected Long doInBackground(String... strings) {
            name = strings[0];
            try{
                Thread.sleep(timeout);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
            return 0L;
        }

        protected void onCancelled(){
            Log.d(TAG, "cancelling time out");
        }

        protected void onPostExecute(Long result) {
            Log.d(TAG, "timed out--removing " + name + " from pit");
        }

    }

    public class SequentialTimeoutTask extends AsyncTask<String, Integer, Long> {
        public static final String TAG = "sequentialtimeouttask";
        private final Integer timeout = 2000;
        private String name;
        private String message;
        private String incoming;
        private String outgoing;

        public SequentialTimeoutTask(String message, String name,
                                     String incoming, String outgoing){
            this.message = message;
            this.name = name;
            this.incoming = incoming;
            this.outgoing = outgoing;
        }

        protected Long doInBackground(String... strings) {
            try{
                Thread.sleep(timeout);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
            return 0L;
        }

        protected void onCancelled(){
            Log.d(TAG, "cancelling sequential time out");
        }

        protected void onPostExecute(Long result) {
            Log.d(TAG, "timed out -- trying next element ");
            // FIXME: should use a combo of ip and interest;
        }

    }


}
