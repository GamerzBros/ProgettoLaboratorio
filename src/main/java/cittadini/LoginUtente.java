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

public class LoginUtente {
    /**
     * Percorso per il file contenente i dati dei cittadini registrati
     */
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI = "data/Cittadini_Registrati.dati.txt";
    public static final int LOGIN_OPERATION_CODE=1;

   private PrintWriter in;
   private BufferedReader out;
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
    public void loggaCittadini(ActionEvent event) { //TODO implementazione server
        Scene currentScene = ((Button) event.getSource()).getScene();
        String user = ((TextField) currentScene.lookup("#txt_userLogin")).getText();
        String pwd = ((TextField) currentScene.lookup("#pswd_login")).getText();
        String[] parts;
        System.out.println("Login in corso");
        String parameters;
        try {
            if (!user.equals("") && !pwd.equals("")) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                pwd = toHexString(messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8)));
                parameters = user + ";" + pwd; //parametri per il db
                becomeClient(parameters); //connessione
                String result = out.readLine(); //valore di ritorno true o false per la query della login
                System.out.println("RISULTATO QUERY = " + result);//codice parlante xdxd
                if (result.equals("true")) { //se true vuoldire che ha matchato con il db
                    System.out.println("LOGGATO");
                    //  currentUser=parts[4]; //CF dell'utente
                    Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    HashMap<String,String> userData = (HashMap<String,String>) currentStage.getUserData();
                    userData.put("currentUser",currentUser);
                    //Controllare che non serva risettarlo
                    //currentStage.setUserData(userData);
                    currentStage.close();

                    Alert alertSuccessfullLogin = new Alert(Alert.AlertType.INFORMATION);
                    alertSuccessfullLogin.setTitle("Login effettuato");
                    alertSuccessfullLogin.setContentText("Utente loggato");
                    alertSuccessfullLogin.showAndWait();

                } else if (result.equals("false")) {
                    Alert noUserAlert = new Alert(Alert.AlertType.WARNING);
                    noUserAlert.setTitle("Errore di login");
                    noUserAlert.setContentText("Utente non trovato!");
                    noUserAlert.show();
                }
            } else {
                Alert alertNoData = new Alert(Alert.AlertType.WARNING);
                alertNoData.setTitle("Inserisci dei dati");
                alertNoData.setContentText("Non hai inserito i dati");
                alertNoData.showAndWait();
                System.out.println("Inserire dei dati");
            }
        } catch (IOException | NoSuchAlgorithmException e) {
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
    public void becomeClient(String parameters){
        try {
            System.out.println("[CLIENT] - Sono già connesso, prendo gli stream ");
            Socket s = SelectionUI.socket_container;
            in = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);
            out = new BufferedReader(new InputStreamReader(s.getInputStream()));
            in.println(parameters);
            in.println(LOGIN_OPERATION_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBackToMain(MouseEvent event){
        Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();

        new MainCittadini(stage);
    }

}
