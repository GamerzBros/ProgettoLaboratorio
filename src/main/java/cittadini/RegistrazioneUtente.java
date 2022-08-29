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
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;

/**
 * Gestisce la UI che permette a un utente di registrarsi per poi poter registrare eventi aversi
 */
public class RegistrazioneUtente {
    /**
     * Buffer che permette di inviare dati al sever
     */
    private BufferedReader in;
    /**
     * Buffer che permette di ricevere dati dal server
     */
    private PrintWriter out;

    /**
     * Registra un cittadino database contente tutti i cittadini registrati.
     * @param event L'evento che richiama il metodo. Necessario a ottenere la scena attuale per prendere le informazioni inserite dall'utente.
     */
    public void registraCittadino(ActionEvent event){
        Scene currentScene=((Button)event.getSource()).getScene();

        String name = ((TextField)currentScene.lookup("#txt_userName")).getText();
        String surname = ((TextField)currentScene.lookup("#txt_userSurname")).getText();
        String user = ((TextField)currentScene.lookup("#txt_userMail")).getText().toLowerCase();
        String userCF=((TextField)currentScene.lookup("#txt_userCF")).getText().toUpperCase();
        String pwd = ((PasswordField)currentScene.lookup("#pswd_register")).getText();
        String confirmationPwd=((PasswordField)currentScene.lookup("#pswd_confirm")).getText();
        LocalDate dataNascita = ((DatePicker)currentScene.lookup("#datePicker_datavaccinazione")).getValue();

        //TODO dividere gli errori in diversi if (per mostrare all'utente popup diversi e meno generici)
        if(name.equals("")||surname.equals("")||user.equals("")||pwd.equals("")||pwd.compareTo(confirmationPwd)!=0||dataNascita==null||userCF.length()!=16){
            Alert alert=new Alert(Alert.AlertType.WARNING,"Compila tutti i campi");
            alert.setContentText("Attenzione, controlla di aver inserito tutti i dati nei vari campi");
            alert.show();
            return;
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8));
            pwd = toHexString(hash);

            String parameters =name+";"+surname+";"+user+";"+userCF+";"+pwd+";"+dataNascita;
            becomeClient(parameters);
            String result = in.readLine();//risultato query login true o false dal server
            if(result.equals("true")){
                Alert alertRegistrationSuccessfull=new Alert(Alert.AlertType.INFORMATION);
                alertRegistrationSuccessfull.setTitle("Registrazione completata");
                alertRegistrationSuccessfull.setContentText("Registrazione avvenuta con successo");
                alertRegistrationSuccessfull.showAndWait();
                new MainCittadini((Stage) currentScene.getWindow());
            } else if (result.equals("false")) {
                Alert alertRegistrationError=new Alert(Alert.AlertType.ERROR);
                alertRegistrationError.setTitle("Errore registrazione");
                alertRegistrationError.setContentText("Non e' stato possibile soddisfare la sua richiesta");
                alertRegistrationError.showAndWait();
            }
        }
        catch (Exception e) {
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
     * Torna alla pagina iniziale del portale cittadini
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage in cui inserire la nuova scena
     */
    public void goBackToMain(MouseEvent event){
        Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();

        new MainCittadini(stage);
    }

    /**
     * Invia al server il relativo codice di operazione, e i dati, per registrare un nuovo utente
     * @param parameters I dati relativi al nuovo utente che il server dovrà inserire nel database
     */
    private void becomeClient(String parameters){
        System.out.println("[CLIENT] - Sono già connesso, prendo gli stream ");
        Socket s = SelectionUI.socket_container;
        out = SelectionUI.out_container;
        in = SelectionUI.in_container;
        out.println(parameters);
        out.println(ServerHandler.REGISTER_USER_OP_CODE);
    }

    /**
     * Carica la UI necessaria ad effettuare il login di un utente.
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage in cui inserire la nuova scena
     */
    public void loadLoginCitizenUI(ActionEvent event){
        try{
            Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/fxml/LoginUtente.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.centerOnScreen();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
