package com.example.turambar.lector_qr_10;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by turambar on 06/03/2017.
 */

public class Cliente {
    /* generar procedimiento para que te conectes al IP local si estás conectado al wi-fi de la fábrica, y al externo de lo contrario  */
    //public static final String SERVER_IP = "201.139.98.214"; //server IP address
    public static final String SERVER_IP = "192.168.1.10"; //server IP address
    public static final int SERVER_PORT = 3214;

    /** ends message received notifications*/
    private String mServerMessage;

    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running

    private boolean mRun = false;
    // used to send messages

    private PrintWriter mBufferOut;
    // used to read messages from the server

    private BufferedReader mBufferIn;

    private Socket socket_;

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public Cliente(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        mRun = false;
        try {
            socket_.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.e("TCP Client", "C: Conectando...");

            //create a socket to make the connection with the server
            socket_ = new Socket(serverAddr, SERVER_PORT);

            try {
                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket_.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket_.getInputStream()));

                sendMessage("android;android;");
                Thread.sleep(300);
                sendMessage("ofrecer QR");
                Thread.sleep(300);
                sendMessage("suscribir pser");
                Thread.sleep(300);
                sendMessage("suscribir bascula");
                //in this while the client listens for the messages sent by the server
                while (mRun) {

                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mServerMessage);
                    }

                }

                Log.e("RESPUESTA", "S: Received Message: '" + mServerMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket_.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
