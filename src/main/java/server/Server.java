package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Gestisce le connessioni di nuovi client al server. Per ogni nuova connessione viene istanziato un nuovo server slave (ServerHandler) che gestirà la singola comunicazione con il client
 */
public class Server {
    /**
     * La porta su cui il server è in ascolto
     */
    public final int PORT = 9870;
    /**
     * Il socket del server che sarà in ascolto per nuove connessioni
     */
    private ServerSocket server_socket;
    /**
     * Il socket del client appena connesso che verrà passato al sever slave
     */
    private Socket s;

    /**
     * Costruttore principale del server
     * @throws IOException Causata da un errore nell'inizializzazione del server socket
     */
    Server() throws IOException {
        server_socket = new ServerSocket(PORT);
        System.out.println("Server inizializzato");
    }

    /**
     * Mantiene il server in ascolto per nuove connessioni e affida la comunicazione a un nuovo server slave per ogni nuovo client connesso
     */
    public void exec() {
        System.out.println("Sono in ascolto");
        try {
            while (true) {
                s = server_socket.accept();
                System.out.println("Un client si è connesso");
                new ServerHandler(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea una nuova istanza della classe server e ne lancia l'esecuzione
     * @param args Gli argomenti del programma passati via console
     */
    public static void main(String[] args) {
        try {
            Server s = new Server();
            s.exec();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
