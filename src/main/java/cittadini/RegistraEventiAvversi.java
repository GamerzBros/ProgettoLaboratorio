package cittadini;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Gestisce la UI che permette di registrare a un cittadino gli eventi avversi
 */
public class RegistraEventiAvversi {
    /**
     * Percorso per il file contente le informazioni dei centri vaccinali registrati
     */
    public static final String PATH_TO_CENTRIVACCINALI_DATI = "data/CentriVaccinali.dati.txt";
    /**
     * Parte iniziale percorso per il file del centro vaccinale selezionato
     */
    public static final String PRE_PATH_TO_EVENTI_AVVERSI="data/Vaccinati_";
    /**
     * Parte finale del percorso del centro vaccinale selezionato
     */
    public static final String AFTER_PATH_TO_EVENTI_AVVERSI=".dati.txt";
    /**
     * Percorso per il file contenente i dati dei cittadini registrati
     */
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI = "data/Cittadini_Registrati.dati.txt";
    /**
     * Tipo di linea del file contente le informazioni relative al vaccinato
     */
    public static final String LINE_TYPE_PERSON ="V";
    /**
     * Tipo di linea del file contente le informazioni relative agli eventi avversi
     */
    public static final String LINE_TYPE_EVENT ="E";
    /**
     * Lista contente tutti i centri vaccinali presenti nel file. Popolata dal metodo getCentriVaccinaliFromFile()
     */
    /**
     * Codice fiscale dell'utente attualmente loggato
     */
    private String currentUser;
    /**
     * Centro vaccinale attualmente selezionato
     */
    private String currentCenter;

    /**
     * Costruttore principale della classe RegistraEventiAvversi
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    public RegistraEventiAvversi(Stage stage){
        HashMap<String,String> userData= (HashMap<String,String>) stage.getUserData();
        currentUser=userData.get("currentUser");
        currentCenter=userData.get("currentCenter");

        loadUI(stage);
    }

    /**
     * Carica la UI principale della schermata RegistraEventiAvversi.
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    private void loadUI(Stage stage){
        try {

            FXMLLoader loader=new FXMLLoader();
            URL url=getClass().getResource("/fxml/registraEventiAvversi.fxml");
            loader.setLocation(url);
            loader.setController(this);
            Parent root=loader.load();

            Scene scene=new Scene(root);

            stage.setScene(scene);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Registra sul file di testo relativo al centro vaccinale selezionato, gli eventi avversi inseriti dall'utente.
     * @param actionEvent L'evento che richiama il metodo. Necessario a ottenere la scena attuale da cui prendere i dati inseriti dall'utente.
     */
    public void registerEventiAvversi(ActionEvent actionEvent){ //TODO fare con il server
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

            FileReader reader = new FileReader(PRE_PATH_TO_EVENTI_AVVERSI + currentCenter + AFTER_PATH_TO_EVENTI_AVVERSI);
            BufferedReader in = new BufferedReader(reader);
            boolean authorized = false;
            boolean alreadyIn = false;
            String line;

            while ((line = in.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0].equals(LINE_TYPE_PERSON) && data[3].equalsIgnoreCase(currentUser)) {
                    authorized = true;
                } else if (data[0].equals(LINE_TYPE_EVENT) && data[2].equalsIgnoreCase(currentUser)) {
                    alreadyIn = true;
                }
            }

            if (alreadyIn) {
                Alert alertAlreadyIn = new Alert(Alert.AlertType.ERROR);
                alertAlreadyIn.setTitle("Eventi già inseriti");
                alertAlreadyIn.setContentText("L'utente ha già inserito una volta degli eventi avversi presso il centro attuale");
                alertAlreadyIn.showAndWait();
            }
            else if (authorized) {

                FileWriter writer = new FileWriter(PRE_PATH_TO_EVENTI_AVVERSI + currentCenter + AFTER_PATH_TO_EVENTI_AVVERSI, true);
                BufferedWriter out = new BufferedWriter(writer);
                //String fileInput = "Mal di Testa:" + evento1 + ";" + "Febbre:" + evento2 + ";" + "Dolori muscolari o articolari:" + evento3 + ";" + "Linfoadenopatia:" + evento4 + ";" + "Tachicardia:" + evento5 + ";" + "Crisi ipertensiva:" + evento6 + ";";

                String fileInput = LINE_TYPE_EVENT + ";" + currentCenter + ";" + currentUser + ";" + evento1 + ";" + evento2 + ";" + evento3 + ";" + evento4 + ";" + evento5 + ";" + evento6;
                if (otherEvent.compareTo("") != 0) {
                    fileInput += ";" + otherEvent;
                }

                out.write(fileInput);
                out.newLine();
                out.flush();
                out.close();
            } else {
                Alert alertNoPermission = new Alert(Alert.AlertType.ERROR);
                alertNoPermission.setTitle("Utente non autorizzato");
                alertNoPermission.setContentText("Non sei stato vaccinato presso il centro selezionato!");
                alertNoPermission.showAndWait();
            }

            Stage stage = (Stage) currentScene.getWindow();
            stage.close();
        }
        catch (IOException e){
            e.printStackTrace();
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
}
