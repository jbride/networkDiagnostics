package com.redhat.gpe.tools;

import java.net.*;
import java.io.*;

/*
    usage :  java -cp target/networkDiagnostics-1.0.jar com.redhat.gpe.tools.TCPSender 192.168.122.1 45588
 */
public class TCPSender {

    private static String clientMessage = "what time is it ?";

    public static void main(String args[]) throws Exception {

        if (args.length != 2) {
             System.out.println("Usage: java TCPSender <destination address> <Port>");
             return;
        }

        Socket aSocket = null;
        try {
            InetAddress remoteAddress = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            aSocket = new Socket(remoteAddress, port);

            System.out.println("isReachable = " + remoteAddress.isReachable(null, 0, 15000));

            OutputStreamWriter out = new OutputStreamWriter(aSocket.getOutputStream());
            out.write(clientMessage, 0, clientMessage.length());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
            System.out.println("response = "+reader.readLine());

        } finally {
            try {
                if (aSocket != null)
                    aSocket.close();
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }
}
