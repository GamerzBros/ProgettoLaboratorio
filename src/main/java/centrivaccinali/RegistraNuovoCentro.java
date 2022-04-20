package centrivaccinali;

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
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;

public class RegistraNuovoCentro {
    PrintWriter in;
    BufferedReader out;
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
    public static final int REGISTER_VACCINECENTRE_OPERATION_CODE =4;
    /**
     * Lista contente le tipologie di indirizzo
     */
    private ObservableList<String> qualificatore_items = FXCollections.observableArrayList("Via","V.le","Piazza");
    /**
     * Lista contenete le tipologie di centro vaccinali
     */
    private ObservableList<String>tipologia_items = FXCollections.observableArrayList("Ospedaliero","Aziendale","Hub");


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

            ChoiceBox<String> choiceBox_qualificatore=((ChoiceBox<String>)scene.lookup("#cbx_qualificatore"));
            choiceBox_qualificatore.setItems(qualificatore_items);
            choiceBox_qualificatore.setValue("Seleziona Qualificatore");
            ChoiceBox<String> choiceBox_tipologiaCentro=((ChoiceBox<String>)scene.lookup("#cbx_tipologia"));
            choiceBox_tipologiaCentro.setValue("Seleziona Tipologia");
            choiceBox_tipologiaCentro.setItems(tipologia_items);

            InputStream icon = getClass().getResourceAsStream("fiorellino.png");
            Image image = new Image(icon);

            stage.getIcons().add(image);

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
                String result = out.readLine();
                if (result.equals("true")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Successo");
                    alert.setHeaderText(null);
                    alert.setContentText("Centro vaccinale registrato");
                    alert.showAndWait();
                } else if (result.equals("false")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText(null);
                    alert.setContentText("Problemi con il server");
                    alert.showAndWait();
                }
                ((Stage) currentScene.getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goBackToOpzioniOperatore(MouseEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/PortaleOperatori.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            currentStage.setScene(scene);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void becomeClient(String parameters){ //TODO rinominare sto metodo, bruh non so come chiamarlo
        try {
            System.out.println("[CLIENT] - Sono già connesso, prendo gli stream ");
            Socket s = SelectionUI.socket_container;
            in = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);
            out = new BufferedReader(new InputStreamReader(s.getInputStream()));
            in.println(parameters);
            in.println(REGISTER_VACCINECENTRE_OPERATION_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
