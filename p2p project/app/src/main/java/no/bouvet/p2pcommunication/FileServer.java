package no.bouvet.p2pcommunication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;


public class FileServer extends AppCompatActivity {

	TextView infoIp, infoPort;
	Button fileButton,connectDevice;
	private static final int MY_INTENT_CLICK=302;
	 String filePath, ServerIp;
	static final int SocketServerPORT = 8080;
	ServerSocket serverSocket;
	EditText editTextAddress;
    ArrayList<Node> listNote;

	ServerSocketThread serverSocketThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_server);
		infoIp = (TextView) findViewById(R.id.infoip);
		infoPort = (TextView) findViewById(R.id.infoport);
		fileButton = (Button) findViewById(R.id.select_file);
		ServerIp = getIpAddress();
		infoIp.setText(ServerIp);


		editTextAddress = (EditText) findViewById(R.id.address);
		connectDevice = (Button) findViewById(R.id.connect_device);


        listNote = new ArrayList<>();
        readAddresses();

        for(int i=0; i<listNote.size(); i++){
            editTextAddress.append(listNote.get(i).toString());
        }


		//get file
		connectDevice.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				ClientRxThread clientRxThread =
						new ClientRxThread(
								editTextAddress.getText().toString(),
								SocketServerPORT);

				clientRxThread.start();
			}});


		serverSocketThread = new ServerSocketThread();
		serverSocketThread.start();


		//send file
		fileButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.setType("*/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);

				startActivityForResult(Intent.createChooser(intent, "Select File"),MY_INTENT_CLICK);


			}
		});


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
				socket = new Socket(editTextAddress.getText().toString(), dstPort);

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

				FileServer.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(FileServer.this,
								"Finished",
								Toast.LENGTH_LONG).show();
					}});

			} catch (IOException e) {

				e.printStackTrace();

				final String eMsg = "Something wrong: " + e.getMessage();
				FileServer.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(FileServer.this,
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
	
	public class ServerSocketThread extends Thread {

		@Override
		public void run() {
			Socket socket = null;
			
			try {
				serverSocket = new ServerSocket(SocketServerPORT);
//				FileServer.this.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						infoPort.setText("I'm waiting here: "
//							+ serverSocket.getLocalPort());
//					}});
				
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
				
				final String sentMsg = "File sent to: " + socket.getInetAddress();
				FileServer.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(FileServer.this,
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
}
