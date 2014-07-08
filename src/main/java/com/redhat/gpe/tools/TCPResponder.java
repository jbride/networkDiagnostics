package com.redhat.gpe.tools;

import java.io.*;
import java.net.*;
import java.util.*;

/*
    usage :  java -cp target/networkDiagnostics-1.0.jar com.redhat.gpe.tools.TCPResponder 192.168.122.1 45588
 */
public class TCPResponder {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
             System.out.println("Usage: java TCPResponder <local address> <Port>");
             return;
        }

        InetAddress bindAddr = InetAddress.getByName(args[0]);
        int bindPort = Integer.parseInt(args[1]);

        new TCPResponderThread(bindAddr, bindPort).start();
    }
}


class TCPResponderThread extends Thread {

    protected ServerSocket ss = null;
    protected boolean continueLoop = true;
    protected InetAddress bindAddr = null;
    protected int bindPort = 0;

    public TCPResponderThread(InetAddress bindAddr, int bindPort) throws Exception {
        this.bindAddr = bindAddr;
        this.bindPort = bindPort;

        System.out.println("address = "+bindAddr+" : bindPort = "+bindPort);
        ss = new ServerSocket(bindPort, 0, bindAddr);
    }

    public void run() {

        while (continueLoop) {
            Socket socketObj = null;
            try {
                socketObj = ss.accept();


                char[] inputBuf = new char[256];
                InputStreamReader isReader = new InputStreamReader(socketObj.getInputStream());
                int charactersRead = isReader.read(inputBuf, 0, 256);
                System.out.println("just read following # of characters : "+charactersRead);

                byte[] outputBuf = (new Date()).toString().getBytes();
                OutputStream outStream = socketObj.getOutputStream();
                outStream.write(outputBuf);
                outStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                continueLoop = false;
            } finally {
                try {
                    if(socketObj != null)
                        socketObj.close();
                } catch(Exception x) {
                    x.printStackTrace();
                }
            }
        }
    }
}
