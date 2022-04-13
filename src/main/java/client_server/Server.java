package client_server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    final int PORT = 9870; //TODO implementazione db
    ServerSocket server_socket;
    Socket s;
    Server() throws IOException {
        server_socket = new ServerSocket(PORT);
        System.out.println("Server inizializzato");
    }

    public void exec() {
        System.out.println("Sono in ascolto");
        try {
            while (true) {
                s = server_socket.accept();
                System.out.println("Un client si è connesso");
                //TODO SERVER HANDLER THREAD
                new ServerHandler(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Server s = new Server();
            s.exec();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
