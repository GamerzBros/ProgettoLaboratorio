package cittadini;

import centrivaccinali.SelectionUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LoginUtente {
    /**
     * Percorso per il file contenente i dati dei cittadini registrati
     */
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI = "data/Cittadini_Registrati.dati.txt";
    public static final int LOGIN_OPERATION_CODE=1;

    PrintWriter in;
    BufferedReader out;
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
        Scene currentScene=((Button)event.getSource()).getScene();
        String user = ((TextField)currentScene.lookup("#txt_userLogin")).getText();
        String pwd = ((TextField)currentScene.lookup("#pswd_login")).getText();
        String user_temp;
        String pwd_temp;
        String[] parts;
        System.out.println("Login in corso");

        becomeClient();


        try {
            if (!user.equals("") && !pwd.equals("")) {
                FileReader fileReader=new FileReader(PATH_TO_CITTADINI_REGISTRATI_DATI);
                BufferedReader reader=new BufferedReader(fileReader);
                boolean isLogged=false;
                String line;

                while ((line=reader.readLine())!=null) {
                    parts = line.split(";");
                    user_temp = parts[2];
                    pwd_temp = parts[3];

                    if (user_temp.equals(user)) {

                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                        pwd = toHexString(messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8)));

                        if(pwd_temp.equals(pwd)) {
                            System.out.println("LOGGATO");
                            isLogged = true;
                            currentUser=parts[4]; //CF dell'utente

                            Scene mainScene=(Scene)currentScene.getUserData();
                            String[] userData=(String[])mainScene.getUserData();
                            currentCenter=userData[0];
                            userData[1]=currentUser;
                            mainScene.setUserData(userData);

                            Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();
                            currentStage.close();

                            Alert alertSuccessfullLogin=new Alert(Alert.AlertType.INFORMATION);
                            alertSuccessfullLogin.setTitle("Login effettuato");
                            alertSuccessfullLogin.setContentText("Utente loggato");
                            alertSuccessfullLogin.showAndWait();

                            //loadRegistraEventiAvversiUI();
                        }
                    }
                }
                if(!isLogged) {
                    Alert noUserAlert = new Alert(Alert.AlertType.WARNING);
                    noUserAlert.setTitle("Errore di login");
                    noUserAlert.setContentText("Utente non trovato!");
                    noUserAlert.show();
                }
            } else {
                Alert alertNoData=new Alert(Alert.AlertType.WARNING);
                alertNoData.setTitle("Inserisci dei dati");
                alertNoData.setContentText("Non hai inserito i dati");
                alertNoData.showAndWait();
                System.out.println("Inserire dei dati");
            }
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

            //scene.setUserData(mainScene);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public void becomeClient(){ //TODO rinominare sto metodo, bruh non so come chiamarlo
        try {
            System.out.println("[CLIENT] - Sono già connesso, prendo gli stream ");
            Socket s = SelectionUI.socket_container;
            in = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);
            out = new BufferedReader(new InputStreamReader(s.getInputStream()));
            in.println(LOGIN_OPERATION_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBackToMain(ActionEvent event){
        Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();

        new MainCittadini(currentStage);
    }

}
