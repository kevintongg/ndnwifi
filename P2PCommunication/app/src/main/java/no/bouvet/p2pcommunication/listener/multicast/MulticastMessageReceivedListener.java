package no.bouvet.p2pcommunication.listener.multicast;

import no.bouvet.p2pcommunication.multicast.MulticastMessage;

public interface MulticastMessageReceivedListener {

    void onMulticastMessageReceived(MulticastMessage multicastMessage);
}
