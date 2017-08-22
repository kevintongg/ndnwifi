//package com.example.android.wifidirect;
//
//import android.content.Context;
//import android.content.res.AssetManager;
//
//import com.example.android.wifidirect.connectivity.BitmapData;
//import com.example.android.wifidirect.connectivity.DataType;
//import com.example.android.wifidirect.connectivity.Message;
//import com.example.android.wifidirect.connectivity.MessageType;
//import com.example.android.wifidirect.connectivity.SerialMessage;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.URL;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
///**
// * Created by tj on 2/9/16.
// */
//@RunWith(MockitoJUnitRunner.class)
//public class SerialMessageUnitTest {
//    @Mock
//    Context context;
//    @Mock
//    AssetManager assetManager;
//
//    @Before
//    public void initMocks() {
//
//    }
//
//    @Test
//    public void fileObjectShouldNotBeNull() throws Exception {
//        InputStream in = this.getClass().getClassLoader().getResourceAsStream("fourleaf.png");
//        assertTrue(in != null);
//    }
//
//    @Test
//    public void createMessage() throws Exception {
//        SerialMessage message = new SerialMessage.MessageBuilder("192.168.1.1", MessageType.INTEREST,
//                "video").dataType(DataType.STRING).stringData("lakers").build();
//        assertTrue(message != null);
//    }
//
//    @Test
//    public void readingWritingImage() throws Exception {
//        BitmapData bd;
//        SerialMessage message = new SerialMessage.MessageBuilder("192.168.1.1", MessageType.INTEREST,
//                "video").dataType(DataType.STRING).stringData("lakers").build();
//        assertTrue(message != null);
////        assetManager = context.getAssets();
////        InputStream is = assetManager.open("fourleaf.png");
//        InputStream in = this.getClass().getClassLoader().getResourceAsStream("fourleaf.png");
//        bd = new BitmapData(in);
//        assertTrue(bd != null);
//        System.out.println(bd.getImageByteArray().length);
//        byte data[] = bd.getImageByteArray();
//        FileOutputStream out = new FileOutputStream("app/src/test/resources/file-to-write1.png");
//        out.write(data);
//        out.close();
//    }
//
//    @Test
//    public void serializeOut() throws Exception {
//        InputStream in = this.getClass().getClassLoader().getResourceAsStream("fourleaf.png");
//        BitmapData bd = new BitmapData(in);
//        SerialMessage message = new SerialMessage.MessageBuilder("192.168.1.1", MessageType.INTEREST,
//                "plant").dataType(DataType.IMAGE).bitmapData(bd).build();
//
//        ObjectOutputStream out= new ObjectOutputStream(new FileOutputStream("app/src/test/resources/message.obj"));
//        out.writeObject(message);
//        out.close();
//
//        // read in
//        ObjectInputStream objectInputStream =
//                new ObjectInputStream(this.getClass().getClassLoader().getResourceAsStream("message.obj"));
//        SerialMessage retrievedMessage = (SerialMessage) objectInputStream.readObject();
//        objectInputStream.close();
//        assertTrue(retrievedMessage != null);
//        assertEquals(retrievedMessage.sender, "192.168.1.1");
//        assertEquals(retrievedMessage.messageType, MessageType.INTEREST);
//        assertEquals(retrievedMessage.dataType, DataType.IMAGE);
//        assertEquals(retrievedMessage.name, "plant");
//    }
//}
