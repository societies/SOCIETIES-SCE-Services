package org.societies.thirdpartyservices.crowdtasking.tools;

import android.util.Log;

import org.societies.thirdpartyservices.crowdtasking.MainActivity;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Created by juresas on 15.10.2013.
 */
public class SocketClient implements Runnable {
    private final static String LOG_TAG = "SocketClient";
    private Socket requestSocket;
    static final int SERVERPORT = 38980;
    static final String SERVER_IP = "192.168.1.102";
//    static final String SERVER_IP = "192.168.1.67";
//    static final String SERVER_IP = "137.195.27.87";
    private boolean connected = true;
    private ObjectInputStream in = null;
    private ObjectOutputStream out =null;
    private MainActivity mainActivity;

    public SocketClient(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        Log.d(LOG_TAG, "SocketClient object created");
    }

    public void run(){
        try{
            //1. creating a socket to connect to the server
            Log.d(LOG_TAG, "Connecting to SocketServer");
            requestSocket = new Socket();
            requestSocket.connect(new InetSocketAddress(SERVER_IP, SERVERPORT), 4000);
            Log.d(LOG_TAG, "Connected to SocketServer " + requestSocket.getInetAddress() + " on port " + requestSocket.getPort());

            //2. get Input/Output stream
            in = new ObjectInputStream(requestSocket.getInputStream());
            Log.d(LOG_TAG, "Input Stream created");
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            Log.d(LOG_TAG, "OutputStream created");

            //3: Communicating with the server
            try{
                //Output
                String message = "Give me your requestor id, please.";
                out.writeObject(message);
                Log.d(LOG_TAG, "User cssId sent to the server: " + message);
                out.reset();

                //Input
                Object x;
//                while(connected){
                    Log.d(LOG_TAG, "Client is listening for server message...");
                    x=in.readObject();
                    if(x instanceof String) {
                        if ("No service id.".equalsIgnoreCase((String)x)) {
                            Log.d(LOG_TAG, "no service id :(");
                        } else {
                            mainActivity.SERVICE_ID = ((String) x);
                            Log.d(LOG_TAG, "received service id: " + mainActivity.SERVICE_ID);
                        }
                        //break;
                    }
//                }
            }catch(ClassNotFoundException e){
                Log.e(LOG_TAG, "ClassNotFoundException: " + e);
                e.printStackTrace();
            }
            catch(InvalidClassException e){
                Log.e(LOG_TAG, "InvalidClassException: " + e);
                e.printStackTrace();
            }
        }catch(UnknownHostException e){
            Log.e(LOG_TAG, "You are trying to connect to an unknown host! " + e);
            e.printStackTrace();
        }
        catch(IOException e){
            Log.e(LOG_TAG, "IOException: " + e);
            e.printStackTrace();
            mainActivity.SERVICE_ID = "org.societies.thirdpartyservices.crowdtasking.CrowdTaskingClient";
        }
    }
}