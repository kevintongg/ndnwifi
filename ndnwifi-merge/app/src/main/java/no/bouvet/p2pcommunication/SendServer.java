package no.bouvet.p2pcommunication;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Hieu on 2/25/2018.
 */

public class SendServer extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            ServerSocket sersock = new ServerSocket(8080);
//        System.out.println("Server ready........");
            Socket sock = sersock.accept();

            Log.d("SendServer", "Server here waiting");


            OutputStream ostream = sock.getOutputStream();
            BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(ostream));
            String s2 = "Hello From.... " + new java.util.Date();
            bw1.write(s2);

            bw1.close();
            ostream.close();
            sock.close();
            sersock.close();
        } catch (SocketException exception) {

        } catch (IOException exception) {

        }

    }
}
