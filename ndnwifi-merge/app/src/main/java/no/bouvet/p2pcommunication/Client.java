package no.bouvet.p2pcommunication;

//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.net.wifi.p2p.WifiP2pDevice;
//import android.net.wifi.p2p.WifiP2pInfo;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.v4.app.NotificationCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Locale;
//
//
//public class Client extends AppCompatActivity {
//
//  public static final int NOTIFICATION_ID = 1;
//  static final int SocketServerPORT = 8080;
//  private static final int MY_INTENT_CLICK = 302;
//  public static WifiP2pDevice currentDevice;
//  public static WifiP2pInfo p2pInfo;
//  ArrayList<Node> listNote;
//  String textAddress, filePath;
//  LinearLayout loginPanel, chatPanel;
//  EditText editTextUserName, editTextAddressClient;
//  Button buttonConnect, sendFile, getFile;
//  TextView chatMsg, textPort;
//  TextView infoIp;
//  EditText editTextSay;
//  Button buttonSend;
//  Button buttonDisconnect;
//  ServerSocket serverSocket;
//  ServerSocketThread serverSocketThread = null;
//
//  String msgLog = "";
//
//  ChatClientThread chatClientThread = null;
//  OnClickListener buttonDisconnectOnClickListener = new OnClickListener() {
//
//    @Override
//    public void onClick(View v) {
//      if (chatClientThread == null) {
//        return;
//      }
//      chatClientThread.disconnect();
//    }
//
//  };
//  OnClickListener buttonSendOnClickListener = new OnClickListener() {
//
//    @Override
//    public void onClick(View v) {
//      if (editTextSay.getText().toString().equals("")) {
//        return;
//      }
//
//      if (chatClientThread == null) {
//        return;
//      }
//
//
//
//      chatClientThread.sendMsg(currentDevice.deviceName + "\n");
//    }
//
//  };
//  OnClickListener buttonConnectOnClickListener = new OnClickListener() {
//    @Override
//    public void onClick(View v) {
//      String textUserName = "User 2";
//
//      if (textUserName.equals("")) {
//        Toast.makeText(Client.this, "Enter User Name",
//            Toast.LENGTH_LONG).show();
//        return;
//      }
//        listNote = new ArrayList<>();
//        readAddresses();
//
//        for (int i = 0; i < listNote.size(); i++) {
//            editTextAddressClient.append(listNote.get(i).toString());
//        }
//        Log.d("Client", editTextAddressClient.getText().toString());
//
////      textAddress = editTextAddressClient.getText().toString();
////      if (textAddress.equals("")) {
////        Toast.makeText(Client.this, "Enter ip address",
////            Toast.LENGTH_LONG).show();
////        return;
////      }
//
//      msgLog = "";
//      chatMsg.setText(msgLog);
//      loginPanel.setVisibility(View.GONE);
//      chatPanel.setVisibility(View.VISIBLE);
//
//      chatClientThread = new ChatClientThread( textAddress, SocketServerPORT);
//      chatClientThread.start();
//
//
//    }
//
//  };
//
//
//
//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_main_client);
//
//    loginPanel = (LinearLayout) findViewById(R.id.loginpanel);
//    chatPanel = (LinearLayout) findViewById(R.id.chatpanel);
//        infoIp = (TextView) findViewById(R.id.infoip);
//    sendFile = (Button) findViewById(R.id.send_file);
//    getFile = (Button) findViewById((R.id.get_file));
//
////        infoIp.setText(getIpAddress());
//
//    editTextUserName = (EditText) findViewById(R.id.username);
//
//
//    editTextAddressClient = (EditText) findViewById(R.id.address);
////        editTextAddressClient.setText(clickedDeviceIp);
////        textPort = (TextView) findViewById(R.id.port);
////        textPort.setText("port: " + SocketServerPORT);
//
//    listNote = new ArrayList<>();
//    readAddresses();
//
//    for (int i = 0; i < listNote.size(); i++) {
//      editTextAddressClient.append(listNote.get(i).toString());
//    }
//    Log.d("Client", editTextAddressClient.getText().toString());
//
//    String ip = editTextAddressClient.getText().toString();
//
//    buttonConnect = (Button) findViewById(R.id.connect);
//    buttonDisconnect = (Button) findViewById(R.id.disconnect);
//    chatMsg = (TextView) findViewById(R.id.chatmsg);
//
//    buttonConnect.setOnClickListener(buttonConnectOnClickListener);
//    buttonDisconnect.setOnClickListener(buttonDisconnectOnClickListener);
//
//    editTextSay = (EditText) findViewById(R.id.say);
//    buttonSend = (Button) findViewById(R.id.send);
//
//    buttonSend.setOnClickListener(buttonSendOnClickListener);
//
//    getFile.setOnClickListener(new OnClickListener() {
//
//      @Override
//      public void onClick(View v) {
//
//        ClientRxThread clientRxThread = new ClientRxThread(editTextAddressClient.getText().toString(),
//            SocketServerPORT);
//        clientRxThread.start();
//
//      }
//    });
//
//    sendFile.setOnClickListener(new OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        serverSocketThread = new ServerSocketThread();
//        serverSocketThread.start();
//
//        Intent intent = new Intent();
//        intent.setType("*/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//
//        startActivityForResult(Intent.createChooser(intent, "Select File"), MY_INTENT_CLICK);
//
//      }
//    });
//
//      chatClientThread = new ChatClientThread(ip, SocketServerPORT);
//      chatClientThread.start();
//
//  }
//
//  private void readAddresses() {
//    listNote.clear();
//    BufferedReader bufferedReader = null;
//
//    try {
//      bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
//
//      String line;
//      while ((line = bufferedReader.readLine()) != null) {
//        String[] splitted = line.split(" +");
//        if (splitted != null && splitted.length >= 4) {
//          String ip = splitted[0];
//          String mac = splitted[3];
//          if (mac.matches("..:..:..:..:..:..")) {
//            Node thisNode = new Node(ip);
//            listNote.add(thisNode);
//          }
//        }
//      }
//
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    } finally {
//      try {
//        bufferedReader.close();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//  }
//
//  @Override
//  public void onActivityResult(int requestCode, int resultCode, Intent data) {
//    if (resultCode == RESULT_OK) {
//      if (requestCode == MY_INTENT_CLICK) {
//        if (null == data) {
//          return;
//        }
//
//        Uri selectedImageUri = data.getData();
//
//        //MEDIA GALLERY
//        filePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
//
//        //txta.setText("File Path : \n"+selectedImagePath);
//      }
//    }
//  }
//
//  class Node {
//
//    String ip;
//
//
//    Node(String ip) {
//      this.ip = ip;
//    }
//
//    @Override
//    public String toString() {
//      return ip;
//    }
//  }
////end image transfer-------------------------------------------------------------------------------------------------------
//
//  public class ServerSocketThread extends Thread {
//
//    @Override
//    public void run() {
//      Socket socket = null;
//
//      try {
////            serverSocket = new ServerSocket();
////            serverSocket.setReuseAddress(true);
//        serverSocket = new ServerSocket(SocketServerPORT);
////            serverSocket.bind(new InetSocketAddress(SocketServerPORT)); // <-- now bind it
//
////                Server.this.runOnUiThread(new Runnable() {
////
////                    @Override
////                    public void run() {
////                        infoPort.setText("I'm waiting here: "
////                                + serverSocket.getLocalPort());
////                    }});
//
//        while (true) {
//          socket = serverSocket.accept();
//          FileTxThread fileTxThread = new FileTxThread(socket);
//          fileTxThread.start();
//        }
//      } catch (IOException e) {
//        e.printStackTrace();
//        final String eMsg = "Something wrong: " + e.getMessage() + " Please Try Again";
//        Client.this.runOnUiThread(new Runnable() {
//
//          @Override
//          public void run() {
//            Toast toast = Toast.makeText(Client.this, eMsg, Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//
//
//          }
//        });
//      } finally {
//        if (socket != null) {
//          try {
//            socket.close();
//          } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//          }
//        }
//      }
//    }
//
//  }
//
//  public class FileTxThread extends Thread {
//
//    Socket socket;
//
//    FileTxThread(Socket socket) {
//
//      this.socket = socket;
//    }
//
//    @Override
//    public void run() {
//      File file = new File(filePath);
//
//      byte[] bytes = new byte[(int) file.length()];
//      BufferedInputStream bis;
//      try {
//        bis = new BufferedInputStream(new FileInputStream(file));
//        bis.read(bytes, 0, bytes.length);
//
//        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//        oos.writeObject(bytes);
//        oos.flush();
//
//        socket.close();
//
////                final String sentMsg = "File sent to: " + socket.getInetAddress();
//        final String sentMsg = "Sending...";
//
//        Client.this.runOnUiThread(new Runnable() {
//
//          @Override
//          public void run() {
//            Toast.makeText(Client.this,
//                sentMsg,
//                Toast.LENGTH_LONG).show();
//          }
//        });
//
//      } catch (FileNotFoundException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//      } catch (IOException e) {
//
//        e.printStackTrace();
//        final String eMsg = "Something wrong: " + e.getMessage() + " Please Try Again";
//        Client.this.runOnUiThread(new Runnable() {
//
//          @Override
//          public void run() {
//            Toast toast = Toast.makeText(Client.this, eMsg, Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//
//
//          }
//        });
//
//      } finally {
//        try {
//          socket.close();
//        } catch (IOException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//        }
//      }
//
//    }
//  }
//
//  //start image transfer
//  private class ClientRxThread extends Thread {
//
//    String dstAddress;
//    int dstPort;
//
//    ClientRxThread(String address, int port) {
//      dstAddress = address;
//      dstPort = port;
//    }
//
//    @Override
//    public void run() {
//      Socket socket = null;
//
//      try {
//        socket = new Socket(textAddress, dstPort);
//        Log.d("Client", textAddress);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
//        Date now = new Date();
//        String fileName = formatter.format(now) + ".jpg";
//
//        File file = new File(
//            Environment.getExternalStorageDirectory(),
//            fileName);
//
//        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//        byte[] bytes;
//        FileOutputStream fos = null;
//        try {
//          bytes = (byte[]) ois.readObject();
//          fos = new FileOutputStream(file);
//          fos.write(bytes);
//        } catch (ClassNotFoundException e) {
//          e.printStackTrace();
//          final String eMsg = "Something wrong: " + e.getMessage() + " Please Try Again";
//          Client.this.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//              Toast toast = Toast.makeText(Client.this, eMsg, Toast.LENGTH_LONG);
//              toast.setGravity(Gravity.CENTER, 0, 0);
//              toast.show();
//
//
//            }
//          });
//        } finally {
//          if (fos != null) {
//            fos.close();
//          }
//
//        }
//
//        socket.close();
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(
//            getApplicationContext());
//        builder.setSmallIcon(R.drawable.chat_bubble_received);
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), "image/*");
//
//        PendingIntent pendingIntent = PendingIntent
//            .getActivity(getApplicationContext(), 0, intent, 0);
//        builder.setContentIntent(pendingIntent);
//
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
//
//        builder.setContentTitle("You Got An Image");
//
//        builder.setContentText("Image Arrived");
//
//        builder.setSubText("Tap to view image");
//        builder.setDefaults(Notification.DEFAULT_ALL);
//        builder.setAutoCancel(true);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(
//            NOTIFICATION_SERVICE);
//
//        notificationManager.notify(NOTIFICATION_ID, builder.build());
//
//        Client.this.runOnUiThread(new Runnable() {
//
//          @Override
//          public void run() {
//            Toast.makeText(Client.this,
//                "Finished",
//                Toast.LENGTH_LONG).show();
//          }
//        });
//
//      } catch (IOException e) {
//
//        e.printStackTrace();
//
//        final String eMsg = "Something wrong: " + e.getMessage() + " Please Try Again";
//        Client.this.runOnUiThread(new Runnable() {
//
//          @Override
//          public void run() {
//            Toast toast = Toast.makeText(Client.this, eMsg, Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//          }
//        });
//
//      } finally {
//        if (socket != null) {
//          try {
//            socket.close();
//          } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//          }
//        }
//      }
//    }
//  }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (serverSocket != null) {
//            try {
//                serverSocket.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//
//  private class ChatClientThread extends Thread {
//
////    String name;
//    String dstAddress;
//    int dstPort;
//
//    String msgToSend = "";
//    boolean goOut = false;
//
//    ChatClientThread( String address, int port) {
////      this.name = name;
//      dstAddress = address;
//      dstPort = port;
//    }
//
//    @Override
//    public void run() {
//      Socket socket = null;
//      DataOutputStream dataOutputStream = null;
//      DataInputStream dataInputStream = null;
//
//      try {
//        socket = new Socket(dstAddress, dstPort);
//        dataOutputStream = new DataOutputStream(
//            socket.getOutputStream());
////        dataInputStream = new DataInputStream(socket.getInputStream());
////          dataOutputStream.writeUTF(name);
////          dataOutputStream.flush();
//
//        while (!goOut) {
//          if (dataInputStream.available() > 0) {
//            msgLog += dataInputStream.readUTF();
//
//            Client.this.runOnUiThread(new Runnable() {
//
//              //show msg in client text
//              @Override
//              public void run() {
//                chatMsg.setText(msgLog);
//              }
//            });
//          }
//
//          if (!msgToSend.equals("")) {
//            dataOutputStream.writeUTF(msgToSend);
//            dataOutputStream.flush();
//            msgToSend = "";
//          }
//        }
//
//      } catch (UnknownHostException e) {
//        e.printStackTrace();
//        final String eString = e.toString();
//        Client.this.runOnUiThread(new Runnable() {
//
//          @Override
//          public void run() {
//            Toast.makeText(Client.this, eString, Toast.LENGTH_LONG).show();
//          }
//
//        });
//      } catch (IOException e) {
//        e.printStackTrace();
//        final String eMsg = "Something wrong: " + e.getMessage() + " Please Try Again";
//        Client.this.runOnUiThread(new Runnable() {
//
//          @Override
//          public void run() {
//            Toast toast = Toast.makeText(Client.this, eMsg, Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
//
//
//          }
//        });
//      } finally {
//        if (socket != null) {
//          try {
//            socket.close();
//          } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//          }
//        }
//
//        if (dataOutputStream != null) {
//          try {
//            dataOutputStream.close();
//          } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//          }
//        }
//
//        if (dataInputStream != null) {
//          try {
//            dataInputStream.close();
//          } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//          }
//        }
//
//        Client.this.runOnUiThread(new Runnable() {
//
//          @Override
//          public void run() {
//            loginPanel.setVisibility(View.VISIBLE);
//            chatPanel.setVisibility(View.GONE);
//          }
//
//        });
//      }
//
//    }
//
//    private void sendMsg(String msg) {
//      msgToSend = msg;
//    }
//
//    private void disconnect() {
//      goOut = true;
//    }
//  }
//
//  // get its own ip address
////    private String getIpAddress() {
////        String ip = "";
////        try {
////            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
////                    .getNetworkInterfaces();
////            while (enumNetworkInterfaces.hasMoreElements()) {
////                NetworkInterface networkInterface = enumNetworkInterfaces
////                        .nextElement();
////                Enumeration<InetAddress> enumInetAddress = networkInterface
////                        .getInetAddresses();
////                while (enumInetAddress.hasMoreElements()) {
////                    InetAddress inetAddress = enumInetAddress.nextElement();
////
////                    if (inetAddress.isSiteLocalAddress()) {
////                        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
////                        String deviceName = myDevice.getName();
////                        ip += deviceName + " - ip address: "
////                                + inetAddress.getHostAddress() + "\n";
////                    }
////
////                }
////
////            }
////
////        } catch (SocketException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////            ip += "Something Wrong! " + e.toString() + "\n";
////        }
////
////        return ip;
////    }
//}


//import android.util.Log;
//
//import java.io.*;
//import java.util.*;
//import java.net.*;
//
////for server
//public class Client implements Runnable {
//
//    public static final long IDLE_TIME = 10;
//
//    private Connection  connection;
//    private boolean     alive;
//    private Thread      t;
//
//    private List<Client> clientList;
//
//    public Client(Connection connection, List<Client> clientList) {
//        this.connection = connection;
//        this.clientList = clientList;
//        alive = false;
//    }
//
//    public synchronized void startSession() {
//
//        if (alive)
//            return;
//
//        alive = true;
//
//        t = new Thread(this);
//        t.start();
//
//    }
//
//    public synchronized void closeSession() {
//
//        if (!alive)
//            return;
//
//        alive = false;
//
//        try {
//            connection.close();
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void run() {
//
//        while (connection.isAlive()) {
//
//            String in = connection.read();
//            if (in != null) {
//
//                String[] str = in.split(" ");
//                for(int i=0 ; i < str.length;i++){
//                    if(str[i].equals("fourleaf.jpg")){
//                        System.out.println(" success");
//                        Log.d("Client", in);
//                    }
//                }
//
//
//                //print msg from client to server
//                System.out.println(in);
//                Log.d("Client", in);
//
//
//                for (Client c : clientList) {
//                    c.send(in);
//
//                }
//
//
//            } else {
//                try {
//                    Thread.sleep(IDLE_TIME);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//
//
//        }
//
//    }
//
//    public void send(String msg) {
//
//        connection.write(msg + "\n");
//        connection.flush();
//    }
//
//}

