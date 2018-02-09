package no.bouvet.p2pcommunication;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Server extends AppCompatActivity {

    static final int SocketServerPORT = 8080;
    LinearLayout loginPanel, chatPanel;
    TextView infoIp, infoPort, chatMsg;
    Button buttonConnect;
    EditText editTextUserName;
    List<ChatClient> userList;
    Button buttonSend,sendFile,downloadFile;
    EditText editTextSay;
    String msgLog = "";
    String inputText ="";
    ServerSocket serverSocket;
    ChatServerThread chatServerThread = null;
    String textUserName,filePath;
    private static final int MY_INTENT_CLICK=302;
    ServerSocketThread serverSocketThread = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_server);
        infoIp = (TextView) findViewById(R.id.infoip);
        infoPort = (TextView) findViewById(R.id.infoport);
        chatMsg = (TextView) findViewById(R.id.chatmsg);
        editTextUserName = (EditText) findViewById(R.id.username);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        loginPanel = (LinearLayout)findViewById(R.id.loginpanel);
        chatPanel = (LinearLayout)findViewById(R.id.chatpanel);

//        sendFile = (Button) findViewById(R.id.send_file);
//        downloadFile = (Button) findViewById(R.id.get_file);

        infoIp.setText(getIpAddress());

        userList = new ArrayList<ChatClient>();
        buttonSend = (Button)findViewById(R.id.send);

        buttonSend.setOnClickListener(buttonSendOnClickListener);
        editTextSay = (EditText)findViewById(R.id.say);

//        ChatServerThread chatServerThread = new ChatServerThread();
//        chatServerThread.start();






//        sendFile.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//
//                Intent intent = new Intent();
//                intent.setType("*/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(intent, "Select File"),MY_INTENT_CLICK);
//
//                Log.d("server","executed");
//
//
//            }
//        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == MY_INTENT_CLICK)
            {
                if (null == data) return;


                Uri selectedImageUri = data.getData();

                //MEDIA GALLERY
                filePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);

                //txta.setText("File Path : \n"+selectedImagePath);
            }
        }
    }





    View.OnClickListener buttonSendOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (editTextSay.getText().toString().equals("")) {
                return;
            }

            if(inputText==null){
                return;
            }
            inputText = editTextSay.getText().toString() ;

//            chatMsg.setText(inputText);
            broadcastMsg(textUserName + ": " + inputText + "\n");
            msgLog += textUserName + ": " + inputText + "\n";
            chatMsg.setText(msgLog  + "\n");
        }

    };


    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             textUserName = editTextUserName.getText().toString();
            if (textUserName.equals("")) {
                Toast.makeText(Server.this, "Enter User Name",
                        Toast.LENGTH_LONG).show();
                return;
            }



//            msgLog = "";
//            chatMsg.setText(msgLog);
            loginPanel.setVisibility(View.GONE);
            chatPanel.setVisibility(View.VISIBLE);

            chatServerThread = new ChatServerThread();

            chatServerThread.start();



        }

    };



    //image service
    public class ServerSocketThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);
//                Server.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        infoPort.setText("I'm waiting here: "
//                                + serverSocket.getLocalPort());
//                    }});

                while (true) {
                    socket = serverSocket.accept();
                    FileTxThread fileTxThread = new FileTxThread(socket);
                    fileTxThread.start();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (socket != null) {
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

    public class FileTxThread extends Thread {
        Socket socket;

        FileTxThread(Socket socket){
            this.socket= socket;
        }

        @Override
        public void run() {
            File file = new File(filePath);



            byte[] bytes = new byte[(int) file.length()];
            BufferedInputStream bis;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytes, 0, bytes.length);

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(bytes);
                oos.flush();

                socket.close();

//                final String sentMsg = "File sent to: " + socket.getInetAddress();
                final String sentMsg = "Sending...";

                Server.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Server.this,
                                sentMsg,
                                Toast.LENGTH_LONG).show();
                    }});

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

//====================================================================================================
    //chat service
    private class ChatServerThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);
//                Server.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        infoPort.setText("I'm waiting here: "
//                                + serverSocket.getLocalPort());
//                    }
//                });

                while (true) {
                    socket = serverSocket.accept();
                    ChatClient client = new ChatClient();
                    userList.add(client);
                    ConnectThread connectThread = new ConnectThread(client, socket);
                    connectThread.start();


                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
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

    private class ConnectThread extends Thread {

        Socket socket;
        ChatClient connectClient;
        String msgToSend = "";

        ConnectThread(ChatClient client, Socket socket){
            connectClient = client;
            this.socket= socket;
            client.socket = socket;
            client.chatThread = this;
        }

        @Override
        public void run() {
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                String n = dataInputStream.readUTF();

                connectClient.name = n;

//                msgLog += connectClient.name + " connected@" +
//                        connectClient.socket.getInetAddress() +
//                        ":" + connectClient.socket.getPort() + "\n";

                msgLog += connectClient.name + " connected to server" + "\n";
                msgLog += textUserName + " connected to the chat room" + "\n";

                //display msglog above
                Server.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        chatMsg.setText(msgLog  + "\n");
                    }
                });

                //send to client and flush
                dataOutputStream.writeUTF("Welcome " + n + "\n");
                dataOutputStream.writeUTF("Welcome " + textUserName + "\n");
                dataOutputStream.flush();

//                broadcastMsg(n + " join the chat room .\n");
//                broadcastMsg(textUserName + " join the chat room .\n");

                while (true) {
                    if (dataInputStream.available() > 0) {
                        String newMsg = dataInputStream.readUTF();


                        msgLog += n + ": " + newMsg;


//                        Server.this.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                chatMsg.setText(msgLog + "server2" + "\n");
//                            }
//                        });

                        //send and display msg in client
                        broadcastMsg(n + ": " + newMsg);
                    }



                    if(!msgToSend.equals("")){
                        dataOutputStream.writeUTF(msgToSend);
                        dataOutputStream.flush();
                        msgToSend = "";
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
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

                userList.remove(connectClient);
                Server.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Server.this,
                                connectClient.name + " removed.", Toast.LENGTH_LONG).show();

                        msgLog += "-- " + connectClient.name + " leaved\n";
                        Server.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                chatMsg.setText(msgLog + "server2" + "\n");
                            }
                        });

                        broadcastMsg("-- " + connectClient.name + " leaved\n");
                    }
                });
            }

        }

        private void sendMsg(String msg){
            msgToSend = msg;
        }

    }


    //send to client
    private void broadcastMsg(String msg){
        for(int i=0; i<userList.size(); i++){
            userList.get(i).chatThread.sendMsg(msg);

            //show message in server
//            msgLog += "- send to " + userList.get(i).name + "\n";
        }

        Server.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                chatMsg.setText(msgLog);
            }
        });
    }

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
                        ip += "SiteLocalAddress: "
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

    class ChatClient {
        String name;
        Socket socket;
        ConnectThread chatThread;

    }

}