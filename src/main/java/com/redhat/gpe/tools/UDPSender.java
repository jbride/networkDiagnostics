package com.redhat.gpe.tools;

import java.io.*;
import java.net.*;
import java.util.*;


/*
    usage :  java -cp target/networkDiagnostics-1.0.jar com.redhat.gpe.tools.UDPSender 192.168.122.1 45588         (UDP unicast scenario)
             java -cp target/networkDiagnostics-1.0.jar com.redhat.gpe.tools.UDPSender 239.9.9.9 45588             (multicast scenario)
 */
public class UDPSender {

    private static String clientMessage = "what time is it ?";

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
             System.out.println("Usage: java QuoteClient <destination address> <destinationPort>");
             return;
        }

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        InetAddress address = InetAddress.getByName(args[0]);
        int destinationPort = Integer.parseInt(args[1]);

        byte[] buf = clientMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, destinationPort);
        System.out.println("sending .... ");
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData());
        System.out.println("response = " + received );

        socket.close();
    }
}

