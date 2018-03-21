package no.bouvet.p2pcommunication;

/**
 * Created by Hieu on 3/18/2018.
 */

import java.io.*;
import java.net.*;

//use to do multiclient and used by server
public class ClientWorker extends Thread{
    Socket cwsocket=null;

    public ClientWorker(Socket cwsocket){
        super("ClientWorker");
        this.cwsocket=cwsocket;
    }

    public void run(){
        try {
            PrintWriter out = new PrintWriter(cwsocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(cwsocket.getInputStream()));

            String serverinput = "Hello44", serveroutput="Hello12345";

            out.println(serveroutput);

            while ((serverinput = in.readLine()) != null) {
                out.println(serveroutput);
                if (serveroutput.equals("Terminate"))
                    break;
            }
            out.close();
            in.close();
            cwsocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}