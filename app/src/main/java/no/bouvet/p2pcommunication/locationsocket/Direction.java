package no.bouvet.p2pcommunication.locationsocket;

/**
 * Created by sabamahbub on 11/3/17.
 */

public class Direction {


  final static double TEST_DESTINATION_LAT = 42.042246;
  final static double TEST_DESTINATION_LONG = -118.665396;

  //This method takes in two sets of coordinates and determines the distance in km.

  public static double getDistance(double firstLat, double firstLong, double secondLat,
      double secondLong) {
    double Radius = 6372.8;
    double distanceLat = Math.toRadians(secondLat - firstLat);
    double distanceLong = Math.toRadians(secondLong - firstLong);
    double a = Math.pow(Math.sin(distanceLat / 2), 2) +
        (Math.cos(Math.toRadians(firstLat)) * Math.cos(Math.toRadians(secondLat)) *
            Math.pow(Math.sin(distanceLong / 2), 2));
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return Radius * c;
  }

  public static double getBearings(double prevLat, double prevLong, double curLat, double curLong) {
    prevLong = Math.toRadians(prevLong);
    prevLat = Math.toRadians(prevLat);
    curLong = Math.toRadians(curLong);
    curLat = Math.toRadians(curLat);
    double y = Math.sin(curLong - prevLong) * Math.cos(curLat);
    double x = Math.cos(prevLat) * Math.sin(curLat) - Math.sin(prevLat) * Math.cos(curLat) * Math
        .cos(curLong - prevLong);
    double angle = Math.toDegrees(Math.atan2(y, x));
    angle = (angle + 360) % 360;
    return angle;
  }

  public static String getBearingsString(double angle) {
    String heading = "";
    if (angle >= 338 || angle < 23) {
      //GOING NORTH
      heading = "N";
    } else if (angle >= 23 && angle < 68) {
      //GOING NORTH EAST
      heading = "NE";
    } else if (angle >= 68 && angle < 113) {
      //GOING EAST
      heading = "E";
    } else if (angle >= 113 && angle < 158) {
      //GOING SOUTH EAST
      heading = "SE";
    } else if (angle >= 158 && angle < 203) {
      //GOING SOUTH
      heading = "S";
    } else if (angle >= 203 && angle < 248) {
      //GOING SOUTH WEST
      heading = "SW";
    } else if (angle >= 248 && angle < 293) {
      //GOING WEST
      heading = "W";
    } else if (angle >= 293 && angle < 338) {
      //GOING NORTH WEST
      heading = "NW";
    }

    //Log.d("Heading", heading);
    return heading;
  }

  /*
  Determines which user is close to the destination.
  Example code:
     double latPrev = 77.612239;
     double longPrev = -112.726889;

     double latCurr = 77.618129;
     double longCurr = -112.567587;

     double latDestination = 77.652595;
     double longDestination =  -111.355621;

        //Distance between destination and current positon
        double a = getDistance(latCurr, longCurr, latDestination, longDestination);

        //Distance between destination and prev
        double b = getDistance(latPrev, longPrev, latDestination, longDestination);

        //Distance between current and prev
        double c = getDistance(latPrev, longPrev, latCurr, longCurr);

        closeToDestination(a, b, c);
   */
  public static double closeToDestination(double a, double b, double c) {
    double angleA = Math.acos((Math.pow(a, 2) - Math.pow(b, 2) - Math.pow(c, 2)) / (-2 * b * c));

    return angleA;
  }

  /*
  Formula to determine which user is closer to the destination
  Uses three points to determine the angle.
   */
  public static double angleBetweenThreePoints(double previousLat, double previousLong,
      double currLat, double currLong, double destinationLat, double destinationLong) {

    previousLat = Math.toRadians(previousLat);
    previousLong = Math.toRadians(previousLong);
    currLat = Math.toRadians(currLat);
    currLong = Math.toRadians(currLong);
    destinationLat = Math.toRadians(destinationLat);
    destinationLong = Math.toRadians(destinationLong);

    //Calculate distance between prev and current location
    double currPrevX = currLat - previousLat;
    double currPrevY = currLong - previousLong;

    double destPrevX = destinationLat - previousLat;
    double destPrevY = destinationLong - previousLong;

    double dotProduct = (destPrevX * currPrevX) + (destPrevY * currPrevY);
    double magnitude = Math.sqrt(Math.pow(destPrevX, 2) + Math.pow(destPrevY, 2)) * Math
        .sqrt(Math.pow(currPrevX, 2) + Math.pow(currPrevY, 2));

    double result = Math.toDegrees(Math.acos(dotProduct / magnitude));
    return result;

  }
    /*
    Possible simple solution (?)
     */

  public static double computeTwoBearings(double previousLat, double previousLong, double currLat,
      double currLong, double destinationLat, double destinationLong) {
    double a = getBearings(previousLat, previousLong, currLat, currLong);
    double b = getBearings(previousLat, previousLong, destinationLat, destinationLong);

    return b - a;
  }
}