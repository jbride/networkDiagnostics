package com.redhat.gpe.tools;

import java.io.*;
import java.net.*;
import java.util.*;

/*
    A multicast group is specified by a class D IP address and by a standard UDP port number
    Class D IP addresses are in the range 224.0.0.0 to 239.255.255.255, inclusive. The address 224.0.0.0 is reserved and should not be used

    usage :  java -cp target/networkDiagnostics-1.0.jar com.redhat.gpe.tools.MulticastResponder 239.9.9.9 45588 $HOSTNAME
 */
public class MulticastResponder {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
             System.out.println("Usage: java MulticastResponder <multicast address> <multicast Port> <local ip>");
             return;
        }

        InetAddress multicastAddr = InetAddress.getByName(args[0]);
        int multicastPort = Integer.parseInt(args[1]);
        String localIPAddress = args[2];

        new MulticastResponderThread(multicastAddr, multicastPort, localIPAddress).start();
    }
}


class MulticastResponderThread extends Thread {

    protected MulticastSocket mSocket = null;
    protected boolean continueLoop = true;
    protected InetAddress multicastAddr = null;
    protected int multicastPort = 0;

    public MulticastResponderThread(InetAddress multicastAddr, int multicastPort, String localIPAddress) throws Exception {
        this.multicastAddr = multicastAddr;
        this.multicastPort = multicastPort;

        System.out.println("multicast address = "+multicastAddr+" : multicastPort = "+multicastPort);
        mSocket = new MulticastSocket(multicastPort);

        if(localIPAddress == null){
            mSocket.joinGroup(multicastAddr);
        }else {
            InetSocketAddress mcastSocket = new InetSocketAddress(multicastAddr, multicastPort);
            NetworkInterface localInterface = NetworkInterface.getByName(localIPAddress);
            mSocket.joinGroup(mcastSocket, localInterface);
        }
    }

    public void run() {

        while (continueLoop) {
            try {
                byte[] buf = new byte[256];

                    // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                mSocket.receive(packet);

                byte[] initialBytes = packet.getData();
                for(int x = 0; x < initialBytes.length; x++) {
                    if(initialBytes[x] == 0) {
                        System.out.println("just read following # of characters : "+x+" from "+packet.getAddress()+":"+packet.getPort());
                        break;
                    }
                }

                String dString = null;
                dString = new Date().toString();
                buf = dString.getBytes();

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                mSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                continueLoop = false;
            }
        }
        mSocket.close();
    }
}
