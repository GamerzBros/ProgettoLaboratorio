package client_server;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


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

    public void login(String parameters) {//ricevo user e psw, connetto db e checko, il client sarà in ascolto e ritorno true o false
        String[] parameters_splitted = parameters.split(";");
        String email = parameters_splitted[0];
        String mail_db = "";
        String pwd_db = "";
        String pwd = parameters_splitted[1];

        try {
            Connection con = connectDB();
            PreparedStatement stm = con.prepareStatement("SELECT email,password FROM public.utente where email=? and password =?");
            stm.setString(1, email);
            stm.setString(2, pwd);
            ResultSet result = stm.executeQuery();
            while (result.next()) {
                mail_db = result.getString("email");

                pwd_db = result.getString("password");

            }
            if (email.equals(mail_db) && pwd.equals(pwd_db)) {
                System.out.println("[DB - THREAD] MATCH NEL DB");
                out.println("true");
            } else {
                System.out.println("[DB - THREAD]NO MATCH NEL DB");
                out.println("false");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void register(String parameters) throws ParseException {
        String[] parameters_splitted = parameters.split(";");
        String name = parameters_splitted[0];
        String surname = parameters_splitted[1];
        String user = parameters_splitted[2];
        String userCF = parameters_splitted[3];
        String pwd = parameters_splitted[4];
        String dateBirth = parameters_splitted[5];
        Date date1 = java.sql.Date.valueOf(dateBirth);


        try{
            Connection con = connectDB();
            PreparedStatement stm = con.prepareStatement("insert into public.utente(nome,cognome,cf,data_nascita,email,password) values (?,?,?,?,?,?)");
            stm.setString(1,name);
            stm.setString(2,surname);
            stm.setString(3,userCF);
            stm.setDate(4,date1);
            stm.setString(5,user);
            stm.setString(6,pwd);
            int result = stm.executeUpdate();
            if(result>0){
                System.out.println("[DB -THREAD] QUERY REGISTRAZIONE COMPLETATA");
                out.println("true");
            }else {
                System.out.println("[DB - THREAD] QUERY REGISTRAZIONE ERRORE");
                out.println("false");
            }
        }catch (SQLException  e){
            e.printStackTrace();
        }
    }

    public Connection connectDB() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/CentriVaccinali", "postgres", "admin");
        if (conn != null) {
            System.out.println("[DB - THREAD] - Sono connesso al db");//qui fa login
        } else {
            System.err.println("[DB - THREAD] - Non sono connesso al db");
        }
        return conn;
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
                System.out.println(parameters);
                op = in.readLine(); //questo è l'operation code
                op_converted = Integer.parseInt(op);
                switch (op_converted) {
                    case 1 -> {
                        System.out.println("[THREAD] Login chiamata");
                        login(parameters);
                    }
                    case 2 -> {
                        System.out.println("[THREAD] Register chiamata");
                        register(parameters);
                    }
                }
            }
        }catch (IOException e){} catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
