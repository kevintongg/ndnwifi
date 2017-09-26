package no.bouvet.p2pcommunication.multicast;

public class MulticastMessage {

    private String text;
    private String senderIpAddress;
    private boolean sentByMe;

    public MulticastMessage(String text, String senderIpAddress) {
        this.text = text;
        this.senderIpAddress = senderIpAddress;
    }

    public MulticastMessage(String text, String senderIpAddress, boolean sentByMe) {
        this.text = text;
        this.senderIpAddress = senderIpAddress;
        this.sentByMe = sentByMe;
    }

    public String getText() {
        return text;
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
