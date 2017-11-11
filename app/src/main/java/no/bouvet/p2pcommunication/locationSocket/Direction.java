package no.bouvet.p2pcommunication.locationSocket;
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
}
