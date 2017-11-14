package no.bouvet.p2pcommunication.locationSocket;

import android.util.Log;

/**
 * Created by sabamahbub on 11/3/17.
 */

public class Direction {


    //This method takes in two sets of coordinates and determines the distance in km.
    public static double getDistance(double firstLong, double firstLat, double secondLong, double secondLat){
        double Radius =  6372.8;
        double lat1 = Math.toRadians(firstLat);
        double lat2 = Math.toRadians(secondLat);
        double distanceLat = Math.toRadians(lat2 - lat1);
        double distanceLong = Math.toRadians(secondLong - firstLong);

        double a = Math.pow(Math.sin(distanceLat / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow(Math.sin(distanceLong/2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return Radius * c;
    }

    public static double getBearings(double prevLong, double prevLat, double curLong, double curLat){
        double y = Math.sin(curLong-prevLong) * Math.cos(curLong);
        double x = Math.cos(prevLat) * Math.sin(curLat) - Math.sin(prevLat) * Math.cos(curLat)*Math.cos(curLong-prevLong);
        double angle = Math.toDegrees( Math.atan2(y, x)) ;
        return angle;
    }

    public static String getBearingsString(double angle){
        String heading = "";
        if (angle >= 338 || angle < 23){
            //GOING NORTH
            heading = "N";
        }
        else if (angle >= 23 && angle < 68){
            //GOING NORTH EAST
            heading = "NE";
        }
        else if (angle >= 68 && angle < 113){
            //GOING EAST
            heading = "E";
        }
        else if (angle >= 113 && angle < 158){
            //GOING SOUTH EAST
            heading = "SE";
        }
        else if (angle >= 158 && angle < 203){
            //GOING SOUTH
            heading = "S";
        }
        else if (angle >= 203 && angle < 248){
            //GOING SOUTH WEST
            heading = "SW";
        }
        else if (angle >= 248 && angle < 293){
            //GOING WEST
            heading = "W";
        }
        else if (angle >= 293 && angle < 338){
            //GOING NORTH WEST
            heading = "NW";
        }

        Log.d("Heading", heading);
        return heading;
    }
}
