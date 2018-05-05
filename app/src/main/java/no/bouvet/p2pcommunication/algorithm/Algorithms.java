package no.bouvet.p2pcommunication.algorithm;

import static java.lang.Double.NaN;
import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceList;
import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceLocations;
import static no.bouvet.p2pcommunication.locationsocket.Direction.getDistance;

import android.util.Log;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import no.bouvet.p2pcommunication.devicelist.Device;
import no.bouvet.p2pcommunication.locationsocket.Direction;
import no.bouvet.p2pcommunication.locationsocket.Locations;
import no.bouvet.p2pcommunication.multiclient.Client;


//TODO: Work in Progress, implement First-hit, flooding, distance, angle, hitaware
public class Algorithms {

  public static final String TAG = "Algorithms";
  private static String forwardingStrategy = "Angle-Based";
  private static final ArrayList<String> ALREADY_CALLED = new ArrayList<>();
  private static final HitCountMap HIT_COUNT_MAP = new HitCountMap();

  //Use for Destination Phone
  private final static double SOURCE_LAT = 34.065510;
  private final static double SOURCE_LONG = -118.166488;

  //Use for Source Phone
  final static double DEST_LAT = 34.075510;
  final static double DEST_LONG = -118.266488;

  public static void forwarding() {
    readAddresses();
    switch (forwardingStrategy) {
      case "First":
        first();
        break;
      case "Flooding":
        flooding();
        break;
      case "Angle-Based":
        locationAngleBased();
        break;
      case "Distance":
        locationDistance();
        break;
      case "HitCountMap":
        Client run = new Client();
        run.run(HIT_COUNT_MAP.getBestIP());
        break;
      default:

    }
  }


  //First
  private static void first() {
    if (deviceList.size() < ALREADY_CALLED.size()) {
      ALREADY_CALLED.clear();
    }

    for (Map.Entry<String, Device> entry : deviceList.entrySet()) {
      Device list = entry.getValue();

      if (list.getIp() != null) {
        Client run = new Client();
        run.run(list.getIp());
        HIT_COUNT_MAP.addHit(list.getIp());

        Log.d("-----Test", list.getIp());
        if (!(ALREADY_CALLED.contains(list.getIp()))) {
          ALREADY_CALLED.add(list.getIp());
        }
      }

    }


  }

  //    //Flooding
  private static void flooding() {
    for (Map.Entry<String, Device> entry : deviceList.entrySet()) {
      Device list = entry.getValue();
      String ip = list.getIp();

      if (!list.getIp().equals(ip)) {
        Client run = new Client();
        run.run(ip);
        HIT_COUNT_MAP.addHit(list.getIp());
      }
    }
  }


  //Location angle
  private static void locationAngleBased() {
    String ipKey = "";
    double value = -1;
    double angle;

    //TODO: Use this for Destination
    if (deviceLocations != null) {

      for (java.util.Map.Entry<String, Locations> entry : deviceLocations.entrySet()) {
        String k = entry.getKey();
        Locations v = entry.getValue();

        if (k != null) {

          //replace 0 with source destination
          angle = Direction
              .angleBetweenThreePoints(v.getPreviousLatitude(), v.getPreviousLongitude(),
                  v.getCurrentLatitude(), v.getCurrentLongitude(), SOURCE_LAT, SOURCE_LONG);

          if (value == -1) {
            ipKey = k;
            value = angle;
          } else if (value > angle && value != NaN) {
            ipKey = k;
            value = angle;
          }
        }
      }

      Client run = new Client();
      run.run(ipKey);
      HIT_COUNT_MAP.addHit(ipKey);

    }

    //TODO: Use this for Source
//        if(deviceLocations != null){
//
//            for (java.util.Map.Entry<String, Locations> entry : deviceLocations.entrySet()) {
//                String k = entry.getKey();
//                Locations v = entry.getValue();
//
//                //replace 0 with source destination
//                angle = Direction.angleBetweenThreePoints(v.getPreviousLatitude(), v.getPreviousLongitude(),
//                        v.getCurrentLatitude(), v.getCurrentLongitude(), DEST_LAT, DEST_LONG);
//
//                if (value == -1 && value != NaN) {
//                    ipKey = k;
//                    value = angle;
//                } else if (value > angle && value != NaN) {
//                    ipKey = k;
//                    value = angle;
//                }
//            }
//
//            Client run = new Client();
//            run.run(ipKey);
//
//        }
  }

  //Location distance
  private static void locationDistance() {
    double distance;
    String ipKey = "";
    double value = -1;

    //TODO: Use this for Destination
    if (deviceLocations != null) {

      for (java.util.Map.Entry<String, Locations> entry : deviceLocations.entrySet()) {
        String k = entry.getKey();
        Locations v = entry.getValue();

        if (k != null) {

          distance = getDistance(v.getCurrentLatitude(), v.getCurrentLongitude(), SOURCE_LAT,
              SOURCE_LONG);

          if (value == -1) {
            ipKey = k;
            value = distance;
            Log.d("Algo1", ipKey);

          } else if (value > distance && value != NaN) {
            ipKey = k;
            value = distance;
            Log.d("Algo2", ipKey);
          }
        }
      }

      Client run = new Client();
      run.run(ipKey);
      HIT_COUNT_MAP.addHit(ipKey);

    }

    //TODO: Use this for Source
//        if(deviceLocations != null){
//
//            for (java.util.Map.Entry<String, Locations> entry : deviceLocations.entrySet()) {
//                String k = entry.getKey();
//                Locations v = entry.getValue();
//
//                distance = getDistance(v.getCurrentLatitude(), v.getCurrentLongitude(), DEST_LAT, DEST_LONG);
//
//                if (value == -1 && value != NaN) {
//                    ipKey = k;
//                    value = distance;
//                } else if (value > distance && value != NaN) {
//                    ipKey = k;
//                    value = distance;
//                }
//            }
//
//            Client run = new Client();
//            run.run(ipKey);
//
//        }
  }


  private static void readAddresses() {

    BufferedReader bufferedReader = null;

    try {
      bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        String[] splitted = line.split(" +");
        if (splitted.length >= 4) {
          String ip = splitted[0];
          String mac = splitted[3];

          if (mac.matches("..:..:..:..:..:..")) {
            Log.d("----TEST", mac + "||" + ip);

            deviceList.get(mac).setIp(ip);


          }
        }
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        Objects.requireNonNull(bufferedReader).close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


}
