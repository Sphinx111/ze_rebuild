package Server; /**
 * Created by Eddy on 30/03/2017.
 */

import java.net.*;
import java.util.Enumeration;

public class ClientModule {
    //This class manages network functions for the client app.
    //Runs one separate thread for initial discovery of servers, thread only exists whilst player is actively searching.

    ze_rebuild pApp;

    //Threads check this value to see if they should terminate themselves early.
    public boolean clientClosing = false;

    //this is the thread that runs in the background to find new servers, a new thread is started each time setup() is called
    DiscoveryThread discoveryThread;

    //this thread runs in the background to listen for server updates, and sends client input snapshots
    ClientThread clientThread;

    //server details if known:
    InetAddress serverAddress;
    int serverPort;

    //client's playerID and auth-code, -1 is NULL.
    int myPlayerID = -1;
    int myAuthCode = -1;

    //Standard packet size to use. keep small as possible(512 bytes, transmitted 20 times a second ~= 10kb/sec):
    int bufLength = 512;


    public ClientModule(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    void setup() {
        //start searching for new servers.
        discoveryThread = new DiscoveryThread(this);
        discoveryThread.start();
        //prepare main client thread for communicating with server.
        clientThread = new ClientThread(this);
        clientThread.start();
    }

    public void update() {

    }

    void setServerAddressAndPort(InetAddress address, int port) {
        serverAddress = address;
        serverPort = port;
    }

    private class DiscoveryThread extends Thread {

        ClientModule parentClient;
        DatagramSocket searchSocket;
        //these are the phrases server and client use to establish initial connection
        String DISCOVER_SEARCH_CODE = "ze_rebuild_client_search";
        String DISCOVER_REPLY_CODE = "ze_rebuild_search_response";
        //SEARCH_SOCKET is the socket that server modules should be listening on.
        //ALT_SEARCH_SOCKET is the backup socket that server modules will use if first is unavailable.
        private int SEARCH_SOCKET = 15129;
        private int ALT_SEARCH_SOCKET = 14129;

        public DiscoveryThread(ClientModule pClient) {
            super("DiscoveryThread");
            parentClient = pClient;
        }

        @Override
        public void run() {
            try {
                //set up a broadcast socket to transmit to any and all machines listening with another broadcast socket (needs port specified too).
                searchSocket = new DatagramSocket();
                searchSocket.setBroadcast(true);

                //convert my discovery code to a stream of bytes.
                byte[] sendData = DISCOVER_SEARCH_CODE.getBytes();

                //try default 255.255.255.255 address first
                try {
                    DatagramPacket packet = new DatagramPacket(sendData,sendData.length, InetAddress.getByName("255.255.255.255"),SEARCH_SOCKET);
                    DatagramPacket packet2 = new DatagramPacket(sendData,sendData.length, InetAddress.getByName("255.255.255.255"),ALT_SEARCH_SOCKET);
                    searchSocket.send(packet);
                    searchSocket.send(packet2);
                    System.out.println(getClass().getName() + ">>> SEARCH PACKET SENT TO 255.255.255.255");
                } catch (Exception e) {
                }

                //broadcast the message over all network interfaces...
                Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

                while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = (NetworkInterface)interfaces.nextElement();
                    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                        continue; //don't want to broadcast to loopback interface or to disabled interfaces
                    }
                    //get every broadcast address on every interface
                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        InetAddress broadcast = interfaceAddress.getBroadcast();
                        if (broadcast == null) {
                            continue;
                        }
                        try {
                            DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, broadcast, SEARCH_SOCKET);
                            DatagramPacket sendPacket2 = new DatagramPacket(sendData,sendData.length, broadcast, ALT_SEARCH_SOCKET);
                            searchSocket.send(sendPacket);
                            searchSocket.send(sendPacket2);
                        } catch (Exception e) {
                        }
                        System.out.println(getClass().getName() + ">>> SEARCH PACKET SENT TO " + broadcast.getHostAddress() + " via Interface: " + networkInterface.getDisplayName());
                    }
                }
                System.out.println(getClass().getName() + ">>> FINISHED SENDING SEARCH PACKETS ON ALL INTERFACES, NOW AWAITING RESPONSE");

                //wait for the response
                byte[] recvBuf = new byte[bufLength];
                DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
                searchSocket.receive(recvPacket);

                //WE HAVE A RESPONSE!
                System.out.println(getClass().getName() + ">>> Search Response received from " + recvPacket.getAddress().getHostAddress());
                String message = new String(recvPacket.getData()).trim();
                //Check for the specified reply code to ensure the other machine is a ze_rebuild server
                if (message.contains(DISCOVER_REPLY_CODE)) {
                    String[] pieces = message.split("/");
                    int serverPort = Integer.parseInt(pieces[1]);
                    parentClient.setServerAddressAndPort(recvPacket.getAddress(),serverPort);
                }

                searchSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private class ClientThread extends Thread {

        ClientModule parentClient;

        DatagramSocket clientSocket;
        int myPort;

        String SERVER_JOIN_QUERY = "CLIENT_READY_FOR_JOINDATA";

        public ClientThread(ClientModule pClient) {
            super("ClientThread");
            parentClient = pClient;

            try {
                //Set up a socket at random open port...
                clientSocket = new DatagramSocket();
                myPort = clientSocket.getLocalPort();
                System.out.println(getClass().getName() + ">>>listening on port: " + clientSocket.getLocalPort());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {


            //if my clientSocket failed to start, cut the thread early, and print out an error message.
            if (clientSocket == null) {
                System.out.println(getClass().getName() + ">>> not running thread, clientSocket not initialised");
                return;
            }

            while (true) {

                //if the client is in the process of closing, terminate the thread before starting another loop.
                if (clientClosing) {
                    return;
                }

                try {
                    if (serverAddress != null && myPlayerID == -1) {
                        //if we haven't had any joinData, query the server.
                        byte[] sendData = SERVER_JOIN_QUERY.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress,serverPort);
                        clientSocket.send(sendPacket);
                    } else if (serverAddress != null && myPlayerID != -1 && shouldSendUpdate()) {
                        //if we have had joinData, and are due to send an update packet get the packetData and send it:
                        byte[] dataOut = preparePlayerInputData();
                        DatagramPacket clientUpdatePacket = new DatagramPacket(dataOut, dataOut.length, serverAddress, serverPort);
                        clientSocket.send(clientUpdatePacket);
                    } else {
                        //if we don't need to send any updateData, wait for a new packet from server
                        byte[] recvData = new byte[bufLength];
                        DatagramPacket packetIn = new DatagramPacket(recvData, recvData.length);
                        //tell my clientSocket to stop blocking after 50milliseconds, allowing loop to run again.
                            clientSocket.setSoTimeout(50);
                        try {
                            clientSocket.receive(packetIn);
                            enums.PacketType receiveType = checkPacketType(packetIn);
                            if (receiveType == enums.PacketType.SERVER_JOINDATA) {
                                processJoinData(packetIn);
                            } else if (receiveType == enums.PacketType.SERVER_UPDATE) {
                                processUpdate(packetIn);
                            }
                        } catch (SocketTimeoutException toe) {
                            //catch and ignore any Socket Timeout exceptions thrown by the socket.receive() line above
                            //this prevents the console filling up with stackTraces I want to ignore.
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        boolean shouldSendUpdate() {
            //return whether the client has to send an update this tick.
            if (pApp.frameCount % 3 == 0) {
                return true; //return true for 1/3 of all frames;
                //TODO: replace this with something more intelligent
            } else {
                return false;
            }
        }

        void processUpdate(DatagramPacket packetIn) {
            //THIS DOES NOTHING YET
        }

        //function parses SERVER_JOINDATA packets (primarily string data at the moment);
        void processJoinData(DatagramPacket packetIn) {
            String message = new String(packetIn.getData()).trim();
            String[] lines = message.split("%");
            String line1[] = lines[1].split("/");
            int myNewID = Integer.parseInt(line1[0]);
            int serverTickCount = Integer.parseInt(line1[1]);
            String line2[] = lines[2].split("/");
            String mapName = line2[0];

            myPlayerID = myNewID;
            // mapHandler.loadMap(mapName);
            //TODO: correct method call/reference please
        }

        //function turns player input data into a stream of bytes for transmission
        //TODO: Using strings is really inefficient, create a custom serializable wrapper class to tag each set of inputs
        //with a corresponding frame number, allows server to accurately simulate player inputs.
        byte[] preparePlayerInputData() {
            byte[] toSend = new byte[bufLength];
            StringBuilder newString = new StringBuilder("");
            String packetNameType = "CLIENT_INPUT";
            newString.append(packetNameType);
            newString.append("%");
            String thisID = myPlayerID + "";
            newString.append(thisID);
            newString.append("/");
            String tickNow = pApp.frameCount + ""; //TODO: Smarter tick count integration please.
            newString.append(tickNow);
            newString.append("%");
            newString.append(tickNow);
            newString.append("/");
            int W = 1;//keyHandler.getWInt(); //TODO: Correct method call/references please.
            int A = 1;//keyHandler.getAInt();
            int S = 0;//keyHandler.getSInt();
            int D = 0;//keyHandler.getDInt();
            String keysTick = "" + W+A+S+D;
            newString.append(keysTick);
            newString.append("%");
            float angle = 0;//actorControl.player.body.getAngle(); //TODO: Correct method call/references please.
            String angleString = angle+"";
            newString.append(angleString);
            newString.append("/");
            int mouseClick;
            if (pApp.mousePressed) {mouseClick = 1;} else {mouseClick = 0;}
            String mouseString = mouseClick + "";
            newString.append(mouseString);

            String stringToSend = newString.toString();
            toSend = stringToSend.getBytes();

            return toSend;
        }

        void acceptTickUpdate(DatagramPacket packetIn) {

        }

        enums.PacketType checkPacketType(DatagramPacket packetIn) {
            String dataIn = new String(packetIn.getData()).trim();
            String[] dataLines = dataIn.split("%");
            String type = dataLines[0];
            if (type.equals("SERVER_JOINDATA")) {
                return enums.PacketType.SERVER_JOINDATA;
            } else if (type.equals("SERVER_UPDATE")) {
                return enums.PacketType.SERVER_UPDATE;
            } else {
                return enums.PacketType.NO_TYPE;
            }
        }

    }


}
