package no.bouvet.p2pcommunication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import android.bluetooth.BluetoothAdapter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Node;

import static no.bouvet.p2pcommunication.fragment.DiscoveryAndConnectionFragment.clickedDeviceIp;


public class Client extends AppCompatActivity {

    static final int SocketServerPORT = 8080;
    public static WifiP2pDevice currentDevice;
    public static WifiP2pInfo p2pInfo;
    ArrayList<Node> listNote;

    public String ip="";
    String textAddress;
    LinearLayout loginPanel, chatPanel;

    EditText editTextUserName, editTextAddress;
    Button buttonConnect,sendFile,getFile;
    TextView chatMsg, textPort;
    TextView infoIp;
    EditText editTextSay;
    Button buttonSend;
    Button buttonDisconnect;

    String msgLog = "";
    String getIp ;

    ChatClientThread chatClientThread = null;
    ClientRxThread clientRxThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);

        loginPanel = (LinearLayout)findViewById(R.id.loginpanel);
        chatPanel = (LinearLayout)findViewById(R.id.chatpanel);
        infoIp = (TextView) findViewById(R.id.infoip);
//        sendFile = (Button) findViewById(R.id.send_file);
//        getFile = (Button) findViewById((R.id.get_file)) ;

        infoIp.setText(getIpAddress());

        editTextUserName = (EditText) findViewById(R.id.username);


//        Log.i( "client",clickedDeviceIp);

        editTextAddress = (EditText) findViewById(R.id.address);
//        editTextAddress.setText(clickedDeviceIp);
//        textPort = (TextView) findViewById(R.id.port);
//        textPort.setText("port: " + SocketServerPORT);


        listNote = new ArrayList<>();
        readAddresses();

        for(int i=0; i<listNote.size(); i++){
            editTextAddress.append(listNote.get(i).toString());
        }



        buttonConnect = (Button) findViewById(R.id.connect);
        buttonDisconnect = (Button) findViewById(R.id.disconnect);
        chatMsg = (TextView) findViewById(R.id.chatmsg);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonDisconnect.setOnClickListener(buttonDisconnectOnClickListener);

        editTextSay = (EditText)findViewById(R.id.say);
        buttonSend = (Button)findViewById(R.id.send);

        buttonSend.setOnClickListener(buttonSendOnClickListener);

//        getFile.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//
//                Log.d(editTextAddress.getText().toString(),textAddress);
//                clientRxThread = new ClientRxThread( editTextAddress.getText().toString(),SocketServerPORT);
//                clientRxThread.start();
//
//            }});


    }



    private void readAddresses() {
        listNote.clear();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        Node thisNode = new Node(ip);
                        listNote.add(thisNode);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Node {
        String ip;


        Node(String ip){
            this.ip = ip;
        }

        @Override
        public String toString() {
            return ip ;
        }
    }











    //start image transfer
    private class ClientRxThread extends Thread {
        String dstAddress;
        int dstPort;

        ClientRxThread(String address, int port) {
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;

            try {
                socket = new Socket(textAddress, dstPort);
                Log.d("Server","120");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
                Date now = new Date();
                String fileName = formatter.format(now) + ".jpg";

                File file = new File(
                        Environment.getExternalStorageDirectory(),
                        fileName);


                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                byte[] bytes;
                FileOutputStream fos = null;
                try {
                    bytes = (byte[])ois.readObject();
                    fos = new FileOutputStream(file);
                    fos.write(bytes);
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if(fos!=null){
                        fos.close();
                    }

                }

                socket.close();

                Client.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Client.this,
                                "Finished",
                                Toast.LENGTH_LONG).show();
                    }});

            } catch (IOException e) {

                e.printStackTrace();

                final String eMsg = "Something wrong: " + e.getMessage();
                Client.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Client.this,
                                eMsg,
                                Toast.LENGTH_LONG).show();
                    }});

            } finally {
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
//end image transfer



    OnClickListener buttonDisconnectOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if(chatClientThread==null){
                return;
            }
            chatClientThread.disconnect();
        }

    };

    OnClickListener buttonSendOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (editTextSay.getText().toString().equals("")) {
                return;
            }

            if(chatClientThread==null){
                return;
            }

            chatClientThread.sendMsg(editTextSay.getText().toString() + "\n");
        }

    };

    OnClickListener buttonConnectOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String textUserName = editTextUserName.getText().toString();


            if (textUserName.equals("")) {
                Toast.makeText(Client.this, "Enter User Name",
                        Toast.LENGTH_LONG).show();
                return;
            }

            textAddress = editTextAddress.getText().toString();
            if (textAddress.equals("")) {
                Toast.makeText(Client.this, "Enter ip address",
                        Toast.LENGTH_LONG).show();
                return;
            }

            msgLog = "";
            chatMsg.setText(msgLog);
            loginPanel.setVisibility(View.GONE);
            chatPanel.setVisibility(View.VISIBLE);

            chatClientThread = new ChatClientThread(textUserName, textAddress, SocketServerPORT);
            chatClientThread.start();


        }

    };

    private class ChatClientThread extends Thread {

        String name;
        String dstAddress;
        int dstPort;

        String msgToSend = "";
        boolean goOut = false;

        ChatClientThread(String name, String address, int port) {
            this.name = name;
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();

                while (!goOut) {
                    if (dataInputStream.available() > 0) {
                        msgLog += dataInputStream.readUTF();

                        Client.this.runOnUiThread(new Runnable() {

                            //show msg in client text
                            @Override
                            public void run() {
                                chatMsg.setText(msgLog);
                            }
                        });
                    }

                    if(!msgToSend.equals("")){
                        dataOutputStream.writeUTF(msgToSend);
                        dataOutputStream.flush();
                        msgToSend = "";
                    }
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                final String eString = e.toString();
                Client.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Client.this, eString, Toast.LENGTH_LONG).show();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
                final String eString = e.toString();
                Client.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Client.this, eString, Toast.LENGTH_LONG).show();
                    }

                });
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                Client.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        loginPanel.setVisibility(View.VISIBLE);
                        chatPanel.setVisibility(View.GONE);
                    }

                });
            }

        }

        private void sendMsg(String msg){
            msgToSend = msg;
        }

        private void disconnect(){
            goOut = true;
        }
    }



    // get its own ip address
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                        String deviceName = myDevice.getName();
                        ip += deviceName + " - ip address: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
}