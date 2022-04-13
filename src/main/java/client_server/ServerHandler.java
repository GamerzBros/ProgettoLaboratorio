package client_server;

import java.io.*;
import java.net.Socket;

public class ServerHandler extends Thread{
    Socket s;
    BufferedReader in;
    PrintWriter out;
    String op;
    int op_converted;

    ServerHandler(Socket s){
      this.s = s;
      start();
    }

    @Override
    public void run() {
        super.run();
        System.out.println("[THREAD] - Server thread startato");
        try{
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
            while (true){
                System.out.println("Ascolto");
                op = in.readLine();
                op_converted = Integer.parseInt(op);
                switch (op_converted){
                    case 1:
                        System.out.println("siamo nel case 1");
                        break;
                }

                System.out.println("Ricevuto:" +op);
            }
        }catch (IOException e){}


    }
}
