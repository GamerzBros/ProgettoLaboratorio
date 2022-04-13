package client_server;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerHandler extends Thread{
    Socket s;
    BufferedReader in;
    PrintWriter out;
    String op;
    String parameters;
    int op_converted;


    ServerHandler(Socket s){
      this.s = s;
      start();
    }

    public boolean login(String parameters) {//ricevo user e psw, connetto db e checko, il client sarà in ascolto e ritorno true o false
        String parameters_splitted[] = parameters.split(";");
        connectDB();
        return true;
    }

    public void connectDB(){
        try{
            Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres","postgres", "admin");
            if(conn != null){
                System.out.println("[DB - THREAD] - Sono connesso al db");//qui fa login
            }else{
                System.out.println("[DB - THREAD] - Non sono connesso al db");
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                parameters = in.readLine(); //qui impacchetto qualsiasi dato con separatore ";" per il server
                op = in.readLine(); //questo è l'operation code
                op_converted = Integer.parseInt(op);
                switch (op_converted){
                    case 1:
                        login(parameters);
                        break;
                }
            }
        }catch (IOException e){}
    }

}
