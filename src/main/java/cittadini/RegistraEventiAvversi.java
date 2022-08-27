package cittadini;

import centrivaccinali.SelectionUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import server.ServerHandler;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.Buffer;
import java.util.HashMap;

/**
 * Gestisce la UI che permette di registrare a un cittadino gli eventi avversi
 */
public class RegistraEventiAvversi {
    /**
     * ID dell'utente attualmente loggato
     */
    private String currentUser;
    /**
     * Centro vaccinale attualmente selezionato
     */
    private int currentCenter;
    /**
     * Il numero della vaccinazione relativa agli eventi avversi
     */
    private String eventsNum;
    /**
     * PrintWriter utilizzato per mandare messaggi al server
     */
    private PrintWriter out;
    /**
     * BufferedReader utilizzato per ricevere messaggi dal server
     */
    private BufferedReader in;

    /**
     * Costruttore principale della classe RegistraEventiAvversi
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    public RegistraEventiAvversi(Stage stage){
        HashMap<String,String> userData= (HashMap<String,String>) stage.getUserData();
        currentUser=userData.get("currentUser");
        currentCenter=Integer.parseInt(userData.get("currentCenter"));
        eventsNum=userData.get("eventsNum");
        loadUI(stage);
    }

    /**
     * Carica la UI principale della schermata RegistraEventiAvversi.
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    private void loadUI(Stage stage){
        try {
            FXMLLoader loader=new FXMLLoader();
            URL url=getClass().getResource("/fxml/RegistraEventiAvversi.fxml");
            loader.setLocation(url);
            loader.setController(this);
            Parent root=loader.load();

            Scene scene=new Scene(root);

            ((Label)scene.lookup("#vaccination_num")).setText("Vaccinazione numero "+eventsNum);

            stage.setScene(scene);

            stage.centerOnScreen();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Registra sul database gli eventi avversi inseriti dall'utente.
     * @param actionEvent L'evento che richiama il metodo. Necessario a ottenere la scena attuale da cui prendere i dati inseriti dall'utente.
     */
    public void registerEventiAvversi(ActionEvent actionEvent){
        try {
            Scene currentScene =((Button) actionEvent.getSource()).getScene();

            Spinner<Integer> spn_headache = (Spinner<Integer>) currentScene.lookup("#spn_headache");
            Spinner<Integer> spn_fever = (Spinner<Integer>) currentScene.lookup("#spn_fever");
            Spinner<Integer> spn_hurt = (Spinner<Integer>) currentScene.lookup("#spn_hurt");
            Spinner<Integer> spn_linf = (Spinner<Integer>) currentScene.lookup("#spn_linf");
            Spinner<Integer> spn_tac = (Spinner<Integer>) currentScene.lookup("#spn_tac");
            Spinner<Integer> spn_crs = (Spinner<Integer>) currentScene.lookup("#spn_crs");
            TextField txt_other1 = (TextField) currentScene.lookup("#txt_other");

            int evento1 = spn_headache.getValue();//evento1 = Mal di testa
            int evento2 = spn_fever.getValue(); //evento2 = Febbre
            int evento3 = spn_hurt.getValue(); //evento3 = Dolori muscolari o articolari
            int evento4 = spn_linf.getValue(); //evento4 = Linfoadenopatia
            int evento5 = spn_tac.getValue(); //evento5 = Tachicardia
            int evento6 = spn_crs.getValue();//evento6 = Crisi ipertensiva
            String otherEvent = txt_other1.getText();

            //inizializzo la classe container e la mando al server tramite l'ObjectOutputWriter

            EventiAvversi eventiSalvati= new EventiAvversi(evento1, evento2, evento3, evento4, evento5, evento6, otherEvent, currentCenter,currentUser);

            //inizializzo socket e stream
            becomeClient();
            ObjectOutputStream obOut= new ObjectOutputStream(SelectionUI.socket_container.getOutputStream());
            obOut.writeObject(eventiSalvati);

            if(in.readLine().equals("true")){
                System.out.println("Eventi avversi registrati con successo");
                Alert alert=new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registrazione avvenuta con successo");
                alert.setHeaderText("Eventi avversi registrati con successo");
                alert.showAndWait();
                Stage stage = (Stage) currentScene.getWindow();
                new MainCittadini(stage);

            }
            else{
                System.out.println("Errore nella registrazione degli eventi avversi");
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore nella registrazione");
                alert.setHeaderText("Gli eventi avversi non sono stati inseriti correttamente, ti invitiamo a riprovare");
                alert.showAndWait();
            }
        }
        catch (IOException e){
            e.printStackTrace();
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Abbiamo riscontrato un problema, ti invitiamo a riprovare");
        }
    }

    /**
     * Torna indietro alla schermata di selezione portale.
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage su cui inserire la nuova scena per mantenere la stessa finestra ma con una UI diversa
     */
    public void goBackToMain(MouseEvent event){
        Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();

        new MainCittadini(stage);
    }

    /**
     * Metodo che invia al server il relativo codice operazione per la registrazione di eventi avversi
     */
    public void becomeClient(){
        System.out.println("[CLIENT EVENTI AVVERSI] - Sono già connesso, prendo gli stream ");
        Socket s = SelectionUI.socket_container;
        out = SelectionUI.out_container;
        in = SelectionUI.in_container;
        out.println("null");
        out.println(ServerHandler.REGISTER_EVENTIAVVERSI_OP_CODE);
        System.out.println("[CLIENT EVENTI AVVERSI] - Uscito dalla become client");


    }
}
