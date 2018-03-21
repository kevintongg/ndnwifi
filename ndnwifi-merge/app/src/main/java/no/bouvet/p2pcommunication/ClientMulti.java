package no.bouvet.p2pcommunication;

/**
 * Created by Hieu on 3/18/2018.
 */
import java.io.*;
import java.net.*;

public class ClientMulti {
    public static void Client(){
        Socket socket=null;
        PrintWriter out=null;
        BufferedReader in=null;
        BufferedReader userInputStream=null;
        String IP="192.168.49.1";
        try{
            socket = new Socket(IP, 4444);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Unknown host:" + IP);
            System.exit(1);
        } catch  (IOException e) {
            System.out.println("Cannot connect to server...");
            System.exit(1);
        }
        String userInput, fromServer;
        try{
            userInputStream = new BufferedReader(new InputStreamReader(System.in));
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Terminate"))
                    break;

                userInput = userInputStream.readLine();
                if (userInput != null) {
                    System.out.println("> " + userInput);
                    out.println(userInput);
                }
            }
        }catch(IOException e){
            System.out.println("Bad I/O");
            System.exit(1);
        }
        try{
            out.close();
            in.close();
            userInputStream.close();
            socket.close();
            System.out.println("Terminating client...");
        }catch(IOException e){
            System.out.println("Bad I/O");
            System.exit(1);
        }catch(Exception e){
            System.out.println("Bad I/O");
            System.exit(1);
        }
    }
}
