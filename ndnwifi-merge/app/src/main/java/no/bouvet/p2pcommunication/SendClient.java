package no.bouvet.p2pcommunication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Hieu on 2/25/2018.
 */

public class SendClient extends AppCompatActivity {

    public static String stringS ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Socket sock = new Socket("192.168.49.1", 8080);

            InputStream istream = sock.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(istream));

            stringS = br1.readLine();
//        System.out.println(s1);
            Log.d("Sendclient", "client here waiting ");

            //show alert box for string received from server

            AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
            myAlert.setMessage(stringS)
                    .setPositiveButton("Exit!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            myAlert.show();




            br1.close();
            istream.close();
            sock.close();

        } catch (SocketException exception) {

        } catch (IOException exception) {

        }
    }


}
