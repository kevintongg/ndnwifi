package no.bouvet.p2pcommunication.multicast;

public class MulticastMessage {

  private final String text;
  private final String SENDER_IP_ADDRESS;
  private boolean sentByMe;

  MulticastMessage(String text, String SENDER_IP_ADDRESS) {
    this.text = text;
    this.SENDER_IP_ADDRESS = SENDER_IP_ADDRESS;
  }

  public MulticastMessage(String text, String SENDER_IP_ADDRESS, boolean sentByMe) {
    this.text = text;
    this.SENDER_IP_ADDRESS = SENDER_IP_ADDRESS;
    this.sentByMe = sentByMe;
  }

  public String getText() {
    return text;
  }

  public String getSENDER_IP_ADDRESS() {
    return SENDER_IP_ADDRESS;
  }

  public boolean isSentByMe() {
    return sentByMe;
  }

  public void setSentByMe(boolean sentByMe) {
    this.sentByMe = sentByMe;
  }
}
