//package com.example.android.wifidirect;
//import android.util.Log;
//
//import com.example.android.wifidirect.connectivity.Message;
//import com.example.android.wifidirect.connectivity.MessageType;
//
//import org.json.JSONObject;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import static org.junit.Assert.*;
//
///**
// * To work on unit tests, switch the Test Artifact in the Build Variants view.
// */
//@RunWith(MockitoJUnitRunner.class)
//public class MessageUnitTest {
//    @Mock
//    JSONObject json;
//
//    @Test
//    public void createMessage() throws Exception {
//        Message message = new Message.MessageBuilder("192.168.1.1", MessageType.INTEREST, "video")
//                .data("lakers").build();
//        assertTrue(message != null);
//    }
//
//    @Test
//    public void makeJson() throws Exception{
//        Message message = new Message.MessageBuilder("192.168.1.1", MessageType.INTEREST, "video")
//                .data("lakers").build();
//
//        json = message.toJson();
//        assertTrue(json != null);
////        System.out.println(message.toString());
////        assertEquals(json.getString("sender"), "192.168.1.1");
//    }
//
//    @Test
//    public void fromJson() {
//        Message message = new Message.MessageBuilder("192.168.1.1", MessageType.INTEREST, "video")
//                .data("lakers").build();
//    }
//}