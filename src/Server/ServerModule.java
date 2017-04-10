package Server;

import com.sun.corba.se.spi.activation.Server;

import java.net.*;
import java.util.HashMap;

/**
 * Created by Eddy on 06/04/2017.
 */
public class ServerModule {
    //This class handles the communication between this instance and multiple client instances

    ze_rebuild pApp;

    public ServerModule(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    ServerDiscoveryThread serverDiscovery;

    ServerThread serverThread;

    HashMap<Integer,LinkedClient> linkedClients;

    class LinkedClient {
        int id;
        InetAddress address;
        int port;
    }

    class ServerThread extends Thread {
        DatagramSocket listeningSocket = null;
        private int LISTENING_SOCKET = 15129;
        private int ALT_LISTENING_SOCKET = 14129;
        int bufLength = 256;

        ServerModule parentServer;

        public ServerThread(ServerModule parentServer) {
            super("ServerThread");
            this.parentServer = parentServer;

            try {
                //Set up a socket at random open port...
                listeningSocket = new DatagramSocket(LISTENING_SOCKET);
                System.out.println(getClass().getName() + ">>>listening on port: " + listeningSocket.getLocalPort());

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    listeningSocket = new DatagramSocket(ALT_LISTENING_SOCKET);
                    System.out.println(getClass().getName() + ">>>listening on port: " + listeningSocket.getLocalPort());
                } catch (Exception e2) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run () {
            //if my clientSocket failed to start, cut the thread early, and print out an error message.
            if (listeningSocket == null) {
                System.out.println(getClass().getName() + ">>> not running thread, clientSocket not initialised");
                return;
            }

            while (true) {

                //if the client is in the process of closing, terminate the thread before starting another loop.
                if (listeningSocket == null) {
                    return;
                }

                try {
                    

                    //Listen for client registrations.

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }



    }

    //this is our discovery thread that allows clients to find the server on the network
    class ServerDiscoveryThread extends Thread {
        DatagramSocket discoverySocket;
        int bufLength = 128;

        //these are the phrases server and client use to establish initial connection
        String DISCOVER_SEARCH_CODE = "ze_rebuild_client_search";
        String DISCOVER_REPLY_CODE = "ze_rebuild_search_response";
        //SEARCH_SOCKET is the socket that server modules should be listening on.
        //ALT_SEARCH_SOCKET is the backup socket that server modules will use if first is unavailable.
        private int SEARCH_SOCKET = 15129;
        private int ALT_SEARCH_SOCKET = 14129;

        public ServerDiscoveryThread () {
            super("ServerDiscoveryThread");
        }

        @Override
        public void run() {
            try {
                //keep a socket open to listen to all UDP traffic being broadcast to specified port.
                try {
                    discoverySocket = new DatagramSocket(SEARCH_SOCKET);
                    discoverySocket.setBroadcast(true);
                } catch (Exception e) {
                    try {
                        discoverySocket = new DatagramSocket(ALT_SEARCH_SOCKET);
                        discoverySocket.setBroadcast(true);
                    } catch (Exception e2) {
                        discoverySocket.close();
                    }
                }

                while (true) {
                    System.out.println(getClass().getName() + ">>>Ready to receive packet broadcasts at port: " + discoverySocket.getLocalPort());

                    //Receive a packet (receive method only triggers if a packet is actually received, otherwise it blocks the function)
                    byte[] recvBuf = new byte[bufLength];
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    discoverySocket.receive(packet);

                    //announce received packet in console:
                    System.out.println(getClass().getName() + ">>>Discovery Packet received from: " + packet.getAddress().getHostAddress());
                    System.out.println(getClass().getName() + ">>>Packet data: " + new String(packet.getData()));

                    //Once packet received, evaluate it:
                    String message = new String(packet.getData()).trim();
                    if (message.equals(DISCOVER_SEARCH_CODE)) {
                        //if the message is valid, send a response.
                        //provide proper port to use to client after reply code:
                        String dataToSend = DISCOVER_REPLY_CODE + "/" + serverThread.LISTENING_PORT;

                        byte[] standardResponse = new String(dataToSend).getBytes();
                        DatagramPacket responsePacket = new DatagramPacket(standardResponse,standardResponse.length,packet.getAddress(),packet.getPort());
                        discoverySocket.send(responsePacket);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
