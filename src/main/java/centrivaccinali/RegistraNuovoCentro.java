package centrivaccinali;

import cittadini.MainCittadini;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.net.URL;

/**
 * Gestisce la UI che permette agli operatori di centri vaccinali di registrare un nuovo centro
 */
public class RegistraNuovoCentro {
    /**
     * Buffer che permette di ricevere dati dal server
     */
    PrintWriter out;
    /**
     * Buffer che permette di inviare dati al sever
     */
    BufferedReader in;
    /**
     * Codice operazionale per il server
     * */
    public static final int REGISTER_VACCINECENTRE_OPERATION_CODE =4;
    /**
     * Lista contente le tipologie di indirizzo
     */
    private ObservableList<String> qualificatore_items = FXCollections.observableArrayList("Via","V.le","Piazza");
    /**
     * Lista contenete le tipologie di centro vaccinali
     */
    private ObservableList<String>tipologia_items = FXCollections.observableArrayList("Ospedaliero","Aziendale","Hub");


    /**
     * Costruttore principale della classe RegistraNuovoCentro
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    public RegistraNuovoCentro(Stage stage){
        try {
            FXMLLoader loader = new FXMLLoader();
            //URL xmlUrl = getClass().getResource("RegistraNuovoCentroVaccinale.fxml");
            URL xmlUrl = getClass().getResource("/fxml/RegistraNuovoCentroVaccinale.fxml");
            loader.setLocation(xmlUrl);
            loader.setController(this);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Nuovo centro vaccinale");

            stage.centerOnScreen();

            ChoiceBox<String> choiceBox_qualificatore=((ChoiceBox<String>)scene.lookup("#cbx_qualificatore"));
            choiceBox_qualificatore.setItems(qualificatore_items);
            choiceBox_qualificatore.setValue("Seleziona Qualificatore");
            ChoiceBox<String> choiceBox_tipologiaCentro=((ChoiceBox<String>)scene.lookup("#cbx_tipologia"));
            choiceBox_tipologiaCentro.setValue("Seleziona Tipologia");
            choiceBox_tipologiaCentro.setItems(tipologia_items);

            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Registra un centro vaccinale prendendo i dati dalla UI.
     * @param event L'evento che richiamerà il metodo attuale. Necessario per prendere il bottone sorgente dell'evento, e da quest'ultimo ottenere la scena. Dalla scena è possibile ottenere tutti componenti grafici con le informazioni necessarie alla registrazione.
     */
    public void registraCentroVaccinale(ActionEvent event) {
        Scene currentScene = ((Button) event.getSource()).getScene();
        String nome = ((TextField) currentScene.lookup("#txt_nomeCentro")).getText();
        String qualif = ((ChoiceBox<String>) currentScene.lookup("#cbx_qualificatore")).getValue();
        String via = ((TextField) currentScene.lookup("#txt_via")).getText();
        String civico = ((TextField) currentScene.lookup("#txt_numeroCivico")).getText();
        String com = ((TextField) currentScene.lookup("#txt_comune")).getText();
        String prov = ((TextField) currentScene.lookup("#txt_provincia")).getText();
        String cap = ((TextField) currentScene.lookup("#txt_cap")).getText();
        String tipolog = ((ChoiceBox<String>) currentScene.lookup("#cbx_tipologia")).getValue();
        String parameters = nome + ";" + qualif + ";" + via + ";" + civico + ";" + com + ";" + prov + ";" + cap + ";" + tipolog;
        if(prov.length()>2){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Controllare i dati inseriti");
            alert.showAndWait();
        }
        if (nome.equals("") || qualif == null || via.equals("") || civico.equals("") || com.equals("") || prov.equals("") || cap.equals("") || tipolog == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Controllare i dati inseriti");
            alert.showAndWait();
        } else {
            try {
                becomeClient(parameters);
                String result = in.readLine();
                if (result.equals("true")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Successo");
                    alert.setHeaderText(null);
                    alert.setContentText("Centro vaccinale registrato");
                    alert.showAndWait();

                    Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();
                    loadOpzioniOperatoreUI(stage);
                } else if (result.equals("false")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText(null);
                    alert.setContentText("Problemi con il server");
                    alert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Torna alla pagina iniziale del portale operatori di centri vaccinali
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage in cui inserire la nuova scena
     */
    public void goBackToOpzioniOperatore(MouseEvent event){
        Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();
        loadOpzioniOperatoreUI(stage);
    }

    /**
     * Carica la UI del portale operatori di centri vaccinali
     * @param stage Lo stage in cui inserire la nuova fx scene
     */
    private void loadOpzioniOperatoreUI(Stage stage){
        new PortaleOperatori(stage);
    }


    /**
     * Invia al server il relativo codice di operazione per registrare un nuovo centro vaccinale
     * @param parameters I dati relativi al nuovo utente che il server dovrà inserire nel database
     */
    public void becomeClient(String parameters){
        System.out.println("[CLIENT] - Sono già connesso, prendo gli stream ");
        Socket s = SelectionUI.socket_container;
        out = SelectionUI.out_container;
        in = SelectionUI.in_container;
        out.println(parameters);
        out.println(REGISTER_VACCINECENTRE_OPERATION_CODE);
    }
}
