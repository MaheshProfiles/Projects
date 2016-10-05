package com.snapbizz.snapbilling.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.net.SocketTimeoutException;

import com.snapbizz.snapbilling.services.mpos.InvoicesSync;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Method;

public class MultiPos extends Service {
	private static final String TAG 					= "[MultiPos]";
	private static final int PORT 						= 1507;
	private static final int BUFFER_SIZE 				= 128;			// 128 bytes
	private static final int RECEIVE_TIMEOUT 			= 1000*30; 		// 30 sec
	private static final String BROADCAST_MESSAGE_HELLO = "SnapServerHello";
	private static final String BROADCAST_MESSAGE_REPLY = "SnapServerHRU";
	
	private final IBinder mBinder 						= new MultiPosBinder();
	private static String[] ipAddresses 				= null;
	private MultiPosHttpD server 						= null;
	private HashMap<String, ServerSyncService>
										allSyncServices = null;
	private volatile DatagramSocket socket 				= null;
	
	// Client specific
	private static InetAddress lastKnownServerAddress 	= null;			/*< Auto-detected server address */
	private boolean bRunDetection 						= true;
	private static final int SLEEP_TIME 				= 30*60*1000;	// 30 minutes
	private static final int NONE_FOUND_SLEEP_TIME		= 1000*30;		// 30 sec
	
	public class MultiPosBinder extends Binder {
		public MultiPos getService() {
            return MultiPos.this;
        }
	}
	
	public static abstract class ServerSyncService {
		protected static final String BASE_URL_V2 = "/v2/api/";
		public abstract String processData(Method method, String json);
		public abstract String getListenUri();
		protected String getJsonStatus(String status) {
			if(status == null)
				status = "success";
			return "{'status':'"+status+"'}";
		}
	};
	
	public static final String[] getCurrentIpAddresses() {
		return ipAddresses;
	}
	
	public static final InetAddress getLastKnownServerAddress() {
		return lastKnownServerAddress;
	}
    
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		stopServer();
		stopServerDetection();
		return false;
	}
	
	public void initializeServices(boolean bServer) {
		//GlobalDB gdb = GlobalDB.getInstance(getApplicationContext(), false);
		SnapbizzDB db = SnapbizzDB.getInstance(getApplicationContext(), false);
		ServerSyncService[] allServices = new ServerSyncService[] {
												new InvoicesSync(db)
		  								  };
		allSyncServices = new HashMap<String, ServerSyncService>();
		for(ServerSyncService service : allServices) {
			allSyncServices.put(service.getListenUri().toLowerCase(Locale.ENGLISH), service);
		}
		// Get All IP Addresses for display
		getAllIpAddresses();

		// Start Server
		if(bServer)
			restartServer();
		else
			startServerDetection();
	}
	
	private void stopServerDetection() {
		bRunDetection = false;
	}
	
	private void startServerDetection() {
		bRunDetection = true;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(bRunDetection) {
						findMultiPosServer();
						Thread.sleep(lastKnownServerAddress == null ? NONE_FOUND_SLEEP_TIME : SLEEP_TIME);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		thread.start();
	}
	
	private void stopServer() {
		// Close Broadcast Server
		if(socket != null)
			socket.close();
		socket = null;

		// Close API Server
		if(server != null && server.isAlive())
			server.stop();
		server = null;
		
	}

	private void startBroadcastServer() {
		try {
			// Start Broadcast server
			socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					byte[] recvBuf = new byte[BUFFER_SIZE];
					while(socket != null) {
						try {
							DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
							socket.receive(packet);
							String message = new String(packet.getData()).trim();
							Log.d(TAG, "Server Received:"+message+":"+packet.getAddress().getHostAddress()+":"+packet.getPort());
							if(message.equalsIgnoreCase(BROADCAST_MESSAGE_HELLO)) {
								byte[] sendData = BROADCAST_MESSAGE_REPLY.getBytes();
								DatagramPacket sendPacket = new DatagramPacket(sendData,
																			   sendData.length,
																			   packet.getAddress(),
																			   packet.getPort());
								socket.send(sendPacket);
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			thread.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tries finding a server.
	 * 
	 * Requires to be run on a different thread.
	 * 
	 * @return Returns lastKnownServerAddress (can be null)
	 */
	public static InetAddress findMultiPosServer() {
		DatagramSocket client = null;
		try {
			client = new DatagramSocket();
			byte[] helloMessage = BROADCAST_MESSAGE_HELLO.getBytes();
			client.setBroadcast(true);

			// Try Broadcast IP
			DatagramPacket sendPacket = new DatagramPacket(helloMessage,
														   helloMessage.length,
														   InetAddress.getByName("255.255.255.255"),
														   PORT);
			client.send(sendPacket);

			// Try Receiving response
			byte[] recvBuf = new byte[BUFFER_SIZE];
			Log.d(TAG, "Starting receive...");
			DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
			client.setSoTimeout(RECEIVE_TIMEOUT);
			client.receive(packet);
			String message = new String(packet.getData()).trim();
			Log.d(TAG, "Client Received:"+message);
			if(message.equalsIgnoreCase(BROADCAST_MESSAGE_REPLY)) {
				lastKnownServerAddress = packet.getAddress();
				Log.d(TAG, "Found Server:"+lastKnownServerAddress.getHostAddress()+":"+lastKnownServerAddress.getHostName());
			}
		} catch(SocketTimeoutException e) {
			Log.d(TAG, "Timedout...");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(client != null)
				client.close();
		}
		return lastKnownServerAddress;
	}

	private void startServer() throws IOException {
		// Start API Server
		server = new MultiPosHttpD();
		server.start();
		
		startBroadcastServer();
	}

	private void restartServer() {
		try {
			stopServer();
			startServer();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getAllIpAddresses() {
		ArrayList<String> addresses = new ArrayList<String>();
	    try {
	        for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
	            NetworkInterface intf = en.nextElement();
	            for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if(!inetAddress.isLoopbackAddress()) {
	                	Log.e(TAG, "Adding IP Address:"+inetAddress.getHostAddress().toString());
	                    addresses.add(inetAddress.getHostAddress().toString());
	                }
	             }
	         }
	     } catch (SocketException ex) {
	         ex.printStackTrace();
	     }
	     ipAddresses = addresses.toArray(new String[0]);
	}

	private class MultiPosHttpD extends NanoHTTPD {
		
		public MultiPosHttpD() throws IOException {
			super(PORT);
		}

		private String mapToString(Map<String, String> params) {
			final StringBuilder buf = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				buf.append(entry.getKey() + ":" + entry.getValue() + "\n");
			}
			return buf.toString();
		}

		private void logHttpRequest(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
			Log.d(TAG, "URI:"+uri);
			Log.d(TAG, "Method:"+method.name());
			Log.d(TAG, "Headers:"+mapToString(headers));
			Log.d(TAG, "Params:"+mapToString(params));
			Log.d(TAG, "Files:"+mapToString(files));
		}
		
		private String getGenericHtmlResponse(String content) {
			return "<html><head><head><body><h1>"+content+"</h1></body></html>";
		}

		@Override
		public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
			logHttpRequest(uri, method, headers, params, files);

			if(method == Method.GET && uri.equalsIgnoreCase("/test"))
				return testServe(uri, method, headers, params, files);
			if(method == Method.POST) {
				// TODO: process authentication data -> deviceId gives posId, can be logged / used separately

				ServerSyncService service = allSyncServices.get(uri.toLowerCase(Locale.ENGLISH));
				if(service != null) {
					String response = null;
					String json = params.get("NanoHttpd.QUERY_STRING");
					if(json == null || json.isEmpty())
						json = files.get("postData");
					if(json != null && !json.isEmpty())
						response = service.processData(method, json);
					if(response == null)
						response = getGenericHtmlResponse("Error processing:"+uri);
					return newFixedLengthResponse(Response.Status.OK, MIME_HTML, response);
				}
			}
			final String html = getGenericHtmlResponse("Unknown path: "+uri);
			return newFixedLengthResponse(Response.Status.OK, MIME_HTML, html);
		}

		public Response testServe(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
			final String html = "<html><head><head><body><h1>Hello, World</h1></body></html>";
			return newFixedLengthResponse(Response.Status.OK, MIME_HTML, html);
		}
	}
}