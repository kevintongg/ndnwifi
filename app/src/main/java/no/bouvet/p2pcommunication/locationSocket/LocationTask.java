package no.bouvet.p2pcommunication.locationSocket;

/**
 * Created by micha on 10/17/2017.
 */

public class LocationTask {


    private double[] location;
    private String senderIpAddress;
    private boolean sentByMe;

    public LocationTask(double[] location, String senderIpAddress) {
        this.location = location;
        this.senderIpAddress = senderIpAddress;
    }

    public LocationTask(double[] location, String senderIpAddress, boolean sentByMe) {
        this.location = location;
        this.senderIpAddress = senderIpAddress;
        this.sentByMe = sentByMe;
    }

    public double[] getLocation() {
        return location;
    }

    public String getSenderIpAddress() {
        return senderIpAddress;
    }

    public boolean isSentByMe() {
        return sentByMe;
    }

    public void setSentByMe(boolean sentByMe) {
        this.sentByMe = sentByMe;
    }
}