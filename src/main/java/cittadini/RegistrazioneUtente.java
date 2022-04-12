package cittadini;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegistrazioneUtente {
    /**
     * Percorso per il file contenente i dati dei cittadini registrati
     */
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI = "data/Cittadini_Registrati.dati.txt";
    /**
     * Codice fiscale dell'utente attualmente loggato
     */
    private String currentUser;
    /**
     * Centro vaccinale attualmente selezionato
     */
    private String currentCenter;


    /**
     * Registra un cittadino nel file di testo contente tutti i cittadini registrati.
     * @param event L'evento che richiama il metodo. Necessario ad ottenere la scena attuale per prendere le informazioni inserite dall'utente.
     */
    public void registraCittadino(ActionEvent event){
        Scene currentScene=((Button)event.getSource()).getScene();

        String name = ((TextField)currentScene.lookup("#txt_userName")).getText();
        String surname = ((TextField)currentScene.lookup("#txt_userSurname")).getText();
        String user = ((TextField)currentScene.lookup("#txt_userMail")).getText();
        String userCF=((TextField)currentScene.lookup("#txt_userCF")).getText();
        String pwd = ((PasswordField)currentScene.lookup("#pswd_register")).getText();
        String confrmationPwd=((PasswordField)currentScene.lookup("#pswd_confirm")).getText();
        LocalDate vaccinationDate = ((DatePicker)currentScene.lookup("#datePicker_datavaccinazione")).getValue();
        String dataVaccinazione = vaccinationDate.format(DateTimeFormatter.ofPattern("MMM-dd-yyyy"));

        if(pwd.compareTo(confrmationPwd)==0) {

            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] hash = messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8));
                pwd = toHexString(hash);

                FileWriter writer = new FileWriter(PATH_TO_CITTADINI_REGISTRATI_DATI, false);
                BufferedWriter out = new BufferedWriter(writer);
                String scrivi = name+";"+surname+";"+user+";"+pwd+";"+userCF+";"+dataVaccinazione;
                out.write(scrivi);
                out.newLine();
                out.close();

                Scene mainScene=(Scene)currentScene.getUserData();
                String[] userData=(String[])mainScene.getUserData();
                currentCenter=userData[0];

                currentUser=userCF;

                userData[1]=currentUser;

                mainScene.setUserData(userData);


                ((Stage)currentScene.getWindow()).close();

                Alert alertRegistrationSuccessfull=new Alert(Alert.AlertType.INFORMATION);
                alertRegistrationSuccessfull.setTitle("Registrazione completata");
                alertRegistrationSuccessfull.setContentText("Registrazione avvenuta con successo");
                alertRegistrationSuccessfull.showAndWait();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Alert wrongPswdAlert=new Alert(Alert.AlertType.ERROR);
            wrongPswdAlert.setTitle("Errore di registrazione");
            wrongPswdAlert.setContentText("Le password inserite non corrispondono");
            wrongPswdAlert.show();
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

    public void goBackToMain(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/SelectionUI.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            Scene scene=new Scene(root);

            Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();

            currentStage.setScene(scene);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
