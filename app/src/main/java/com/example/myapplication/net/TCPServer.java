package com.example.myapplication.net;


import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.Presentation;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TCPServer extends Thread {
    private int port;
    private ServerSocket serverSocket;
    private volatile boolean stop = false;
    TextView textView;
    Presentation presentation;
    AvailableSlots slots = new AvailableSlots(4);
    private EchoClientHandler ev;

    public TCPServer(int port, Presentation p) {
        this.port = port;
        this.presentation = p;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            presentation.addText("Server has started!");

            while (!stop) {
                ev = new EchoClientHandler(serverSocket.accept(), presentation, slots);
                ev.setDaemon(true);
                ev.start();
            }
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void stopServer() throws Exception {
        if (serverSocket != null) {
            //
            stop = true;

        }
    }

    private static class AvailableSlots {
        private Map<String, String> slots;

        public AvailableSlots(int slotsNo){
            slots = new HashMap<>();

            for (int ii=0;ii<slotsNo;ii++){
                slots.put("id_"+ ii, "free");
            }
        }

        public void freeSlot(int index) {
            slots.put("id_"+ index, "free");
        }

        public int getFreeSlot(){
            for (String key : slots.keySet()){
                String val = slots.get(key);
                if (!val.equals("free")) {
                    continue;
                }

                slots.put(key, "taken");
                return Integer.valueOf(key.split("_")[1]);
            }
            return -1;
        }

    };

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private static int currentIndex = 0;

        private int index = -1;
        private AvailableSlots slots;
        EchoClientHandler(@NonNull Socket socket, @NonNull Presentation presentation, AvailableSlots slots) {
            this.slots = slots;
            index = slots.getFreeSlot();
            this.clientSocket = socket;
            this.presentation = new WeakReference<>(presentation);
        }

        WeakReference<Presentation> presentation;

        public void run() {
            Presentation p = presentation.get();
            if (p != null) {
                p.addText("Client " + clientSocket.getInetAddress().toString() + " has connected on port " + clientSocket.getPort());
            }
            try {
                ByteArrayOutputStream bosLeftOver = null;
                while (true) {
                    double latency = getLatency(clientSocket.getInetAddress().getHostName());

//                    InputStream inputStream = clientSocket.getInputStream();
//                    BufferedReader inFromClient =
//                            new BufferedReader(new InputStreamReader(inputStream));
                    DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
                    //String msg = inFromClient.readLine();
                    //if(msg != null) {
                    //p.addText("Client " + clientSocket.getInetAddress().toString() + " latency: " + latency + "ms. " );
                    if (index != -1){
                        p.addPingText("Client " + clientSocket.getInetAddress().toString() + " latency: " + latency + "ms. " , index);

                    }
                   // }
                    outToClient.writeBytes("TIME_SENT " + latency);
                    outToClient.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            p.addText("Client " + clientSocket.getInetAddress().toString() + " has disconnected ");
            if (index != -1) {
                slots.freeSlot(index);
            }

        }

        public double getLatency(String ipAddress){
            String pingCommand = "/system/bin/ping -c " + 1 + " " + ipAddress;
            String inputLine = "";
            double avgRtt = 0;

            try {
                // execute the command on the environment interface
                Process process = Runtime.getRuntime().exec(pingCommand);
                // gets the input stream to get the output of the executed command
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                inputLine = bufferedReader.readLine();
                while ((inputLine != null)) {
                    if (inputLine.length() > 0 && inputLine.contains("avg")) {  // when we get to the last line of executed ping command
                        break;
                    }
                    inputLine = bufferedReader.readLine();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }

            // Extracting the average round trip time from the inputLine string
            String afterEqual = inputLine.substring(inputLine.indexOf("="), inputLine.length()).trim();
            String afterFirstSlash = afterEqual.substring(afterEqual.indexOf('/') + 1, afterEqual.length()).trim();
            String strAvgRtt = afterFirstSlash.substring(0, afterFirstSlash.indexOf('/'));
            avgRtt = Double.valueOf(strAvgRtt);

            return avgRtt;
        }
    }



}