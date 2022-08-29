package centrivaccinali;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import server.ServerHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

/**
 * Gestisce la UI che permette agli operatori di centri vaccinali di registrare un nuovo vaccinato
 */
public class RegistraNuovoVaccinato {
    /**
     * Buffer che permette di inviare dati primitivi al sever
     */
    PrintWriter out;
    /**
     * Buffer che permette di ricevere dati primitivi dal server
     */
    BufferedReader in;
    /**
     * Vettore di centri vaccinali
     */
    private Vector<SingoloCentroVaccinale> centriVaccinaliList=null;
    /**
     * Lista contente i tipi di vaccini
     */
    private ObservableList<String> vaccino_somministrato_items = FXCollections.observableArrayList("Pfizer","AstraZeneca","Moderna","J&J");
    /**
     * Lista contenente i centri vaccinali presenti nel file
     */
    private ObservableList<String> centro_vaccinale_items = FXCollections.observableArrayList();

    /**
     * Costruttore principale della classe RegistraNuovoVaccinato. Costruisce la UI che verrà poi gestita dalla classe attuale
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    public RegistraNuovoVaccinato(Stage stage){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/RegistraNuovoVaccinato.fxml");
            loader.setLocation(xmlUrl);
            loader.setController(this);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Nuovo Paziente");

            stage.centerOnScreen();
            //Inizializzo i choicebox
            ChoiceBox<String> choiceBox_centroVaccinale = ((ChoiceBox<String>) scene.lookup("#cbx_centroVaccinale"));
            choiceBox_centroVaccinale.setValue("Centro Vaccinale");

            ChoiceBox<String> choiceBox_vaccinoSomministrato = ((ChoiceBox<String>) scene.lookup("#cbx_vaccinoSomministrato"));
            choiceBox_vaccinoSomministrato.setValue("Tipologia Vaccino");
            choiceBox_vaccinoSomministrato.setItems(vaccino_somministrato_items);

            double popupSize=90;
            ProgressIndicator loadingPopup=new ProgressIndicator();
            loadingPopup.setMinHeight(popupSize);
            loadingPopup.setMinWidth(popupSize);
            loadingPopup.setLayoutX(scene.getWidth()/2-loadingPopup.getMinWidth()/2);
            loadingPopup.setLayoutY(scene.getHeight()/2-loadingPopup.getMinHeight()/2);
            loadingPopup.setStyle("-fx-progress-color: white");
            ((AnchorPane)scene.getRoot()).getChildren().add(loadingPopup);

            new Thread(()->{
                try {
                    //faccio in modo che il popup di caricamento sia visibile almeno per un breve periodo di tempo
                    Thread.sleep(400);

                    becomeClient();
                    ObjectInputStream ois = new ObjectInputStream(SelectionUI.socket_container.getInputStream());
                    //Creo gli stream e ricevo dal server il vettore dei centri vaccinali
                    centriVaccinaliList = (Vector<SingoloCentroVaccinale>) ois.readObject();
                    //Aggiorno la lsita dei nomi e la metto nel ChoiceBox
                    for (SingoloCentroVaccinale centro : centriVaccinaliList) {
                        centro_vaccinale_items.add(centro.getNome());
                    }
                    choiceBox_centroVaccinale.setItems(centro_vaccinale_items);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                Platform.runLater(()->{
                    ((AnchorPane)scene.getRoot()).getChildren().remove(loadingPopup);
                });
            }).start();

            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Registra un paziente vaccinato nel database
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena attuale da cui ottenere i valori da inserire nel database.
     */
    public void registraVaccinato(ActionEvent event) {
        Scene currentScene = ((Button) event.getSource()).getScene();
        String name = ((TextField) currentScene.lookup("#txt_nomePaziente")).getText();
        String surname = ((TextField) currentScene.lookup("#txt_cognomePaziente")).getText();
        String codice_fiscale = ((TextField) currentScene.lookup("#txt_cfPaziente")).getText();
        String vaccineType = ((ChoiceBox<String>) currentScene.lookup("#cbx_vaccinoSomministrato")).getValue();
        LocalDate vaccinationDate = ((DatePicker) currentScene.lookup("#datePicker_datavaccinazione")).getValue();
        int centroVaccinale = ((ChoiceBox<String>) currentScene.lookup("#cbx_centroVaccinale")).getSelectionModel().getSelectedIndex()+1;
        String dataVaccinazione = "";

        if (vaccinationDate != null) {
            dataVaccinazione = vaccinationDate.format(DateTimeFormatter.ofPattern("yyy-MM-dd"));
        }
        if (name.equals("") || surname.equals("") || codice_fiscale.equals("") || codice_fiscale.length()>16 || vaccineType.equals("") || dataVaccinazione.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Controllare i dati inseriti");
            alert.showAndWait();
        } else {
            try {
                String parameters = name+";"+surname+";"+codice_fiscale+";"+vaccineType+";"+centroVaccinale+";"+dataVaccinazione;
                out.println(parameters);
                out.println(ServerHandler.REGISTER_VACCINATED_OP_CODE);
                String result = in.readLine();
                if (result.equals("true")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Successo");
                    alert.setHeaderText(null);
                    alert.setContentText("Nuovo vaccinato registrato");
                    alert.showAndWait();

                    Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();
                    loadOpzioniOperatoreUI(stage);

                } else if (result.equals("false")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText(null);
                    alert.setContentText("Nuovo vaccinato non registrato");
                    alert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Invia al server il relativo codice di operazione per ottenere i centri vaccinali dal database e inserirli nel combo box
     */
    public void becomeClient(){
        System.out.println("[CLIENT] - Sono già connesso, prendo gli stream ");
        Socket s = SelectionUI.socket_container;
        out = SelectionUI.out_container;
        in = SelectionUI.in_container;
        out.println("null");
        out.println(ServerHandler.GET_VAX_CENTERS_OP_CODE);
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
}
