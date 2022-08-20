package cittadini;

import centrivaccinali.SelectionUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Gestisce la UI che permette a un utente di effettuare il login
 */
public class LoginUtente {
    /**
     * Percorso per il file contenente i dati dei cittadini registrati
     */
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI = "data/Cittadini_Registrati.dati.txt";
    public static final int LOGIN_OPERATION_CODE=1;
    /**
     * Buffer che permette di ricevere dati primitivi dal server
     */
   private PrintWriter out;
    /**
     * Buffer che permette di inviare dati primitivi al sever
     */
   private BufferedReader in;
    /**
     * Codice fiscale dell'utente attualmente loggato
     */
    private String currentUser;
    /**
     * Centro vaccinale attualmente selezionato
     */
    private String currentCenter;

    /**
     * Effettua il login dell'utente.
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena da cui prendere i dati inseriti dall'utente
     */
    public void loggaCittadini(ActionEvent event) { //TODO FARE BENE LOGIN
        Scene currentScene = ((Button) event.getSource()).getScene();
        String user = ((TextField) currentScene.lookup("#txt_userLogin")).getText();
        String pwd = ((TextField) currentScene.lookup("#pswd_login")).getText();
        System.out.println("Login in corso");
        String parameters;
        try {
            if (user.equals("") || pwd.equals("")) {
                Alert alertNoData = new Alert(Alert.AlertType.WARNING);
                alertNoData.setTitle("Inserisci dei dati");
                alertNoData.setContentText("Riempi tutti i campi");
                alertNoData.show();
                return;
            }
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            pwd = toHexString(messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8)));
            parameters = user + ";" + pwd; //parametri per il db
            becomeClient(parameters); //connessione
            String result = in.readLine(); //valore di ritorno true o false per la query della login
            System.out.println("RISULTATO QUERY = " + result);//codice parlante xdxd
            if (result==null) { //se null vuol dire che il login non è andato a buon fine
                Alert noUserAlert = new Alert(Alert.AlertType.WARNING);
                noUserAlert.setTitle("Errore di login");
                noUserAlert.setContentText("Utente non trovato!");
                noUserAlert.show();
                return;
            }
            System.out.println("LOGGATO");

            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            HashMap<String,String> userData = (HashMap<String,String>) currentStage.getUserData();
            userData.put("currentUser",result);

            Alert alertSuccessfullLogin = new Alert(Alert.AlertType.INFORMATION);
            alertSuccessfullLogin.setTitle("Login effettuato");
            alertSuccessfullLogin.setContentText("Utente loggato");
            alertSuccessfullLogin.showAndWait();

            new MainCittadini(currentStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converte un array di byte in una stringa. Viene utilizzato dopo aver effettuato l'hashing di una stringa, per ricomporre quest'ultima.
     * @param array L'array contenente i byte di risultato dell'hashing.
     * @return La stringa ottenuta come risultato dalla funzione di hash.
     */
    private String toHexString(byte[] array) {
        StringBuilder sb = new StringBuilder(array.length * 2);

        for (byte b : array) {
            int value = 0xFF & b;
            String toAppend = Integer.toHexString(value);

            sb.append(toAppend);
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Carica la UI necessaria ad effettuare la registrazione di un utente.
     * @param event L'evento che richiama il metodo. Necessario per chiudere la UI di login
     */
    public void loadRegisterCitizenUI(ActionEvent event){
        try {
            //Scene mainScene=((Button)event.getSource()).getScene();
            Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/fxml/RegistraUtente.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            currentStage.setScene(scene);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Invia al server il relativo codice di operazione per effettuare il login di un utente
     * @param parameters I dati relativi al login che il server dovrà verificare
     */
    public void becomeClient(String parameters){
        System.out.println("[CLIENT LOGIN UTENTE] - Sono già connesso, prendo gli stream ");
        Socket s = SelectionUI.socket_container;
        out = SelectionUI.out_container;
        in = SelectionUI.in_container;
        out.println(parameters);
        out.println(LOGIN_OPERATION_CODE);
        System.out.println("[CLIENT LOGIN UTENTE] - Uscito dalla become client ");
    }

    /**
     * Torna alla pagina iniziale del portale cittadini
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage in cui inserire la nuova scena
     */
    public void goBackToMain(MouseEvent event){
        Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();

        new MainCittadini(stage);
    }

}
