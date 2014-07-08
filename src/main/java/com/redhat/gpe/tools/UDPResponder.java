package com.redhat.gpe.tools;

import java.io.*;
import java.net.*;
import java.util.*;

/*
    usage :     java -cp target com.redhat.gpe.tools.UDPResponder 192.168.122.1 45588
 */
public class UDPResponder {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
             System.out.println("Usage: java UDPResponder <local address> <Port>");
             return;
        }

        InetAddress bindAddr = InetAddress.getByName(args[0]);
        int bindPort = Integer.parseInt(args[1]);

        new UDPResponderThread(bindAddr, bindPort).start();
    }
}


class UDPResponderThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean continueLoop = true;
    protected InetAddress bindAddr = null;
    protected int bindPort = 0;

    public UDPResponderThread(InetAddress bindAddr, int bindPort) throws Exception {
        this.bindAddr = bindAddr;
        this.bindPort = bindPort;

        System.out.println("address = "+bindAddr+" : bindPort = "+bindPort);
        socket = new DatagramSocket(bindPort, bindAddr);
    }

    public void run() {

        while (continueLoop) {
            try {
                byte[] buf = new byte[256];

                    // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                byte[] initialBytes = packet.getData();
                for(int x = 0; x < initialBytes.length; x++) {
                    if(initialBytes[x] == 0) {
                        System.out.println("just read following # of characters : "+x);
                        break;
                    }
                }



                    // figure out response
                String dString = null;
                dString = new Date().toString();
                buf = dString.getBytes();

           // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                continueLoop = false;
            }
        }
        socket.close();
    }
}
