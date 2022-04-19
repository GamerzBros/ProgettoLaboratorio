package centrivaccinali;

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
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

public class RegistraNuovoVaccinato {
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
     * Lista contente i tipi di vaccini
     */
    private ObservableList<String> vaccino_somministrato_items = FXCollections.observableArrayList("Pfizer","AstraZeneca","Moderna","J&J");
    /**
     * Lista contenente i centri vaccinali presenti nel file
     */
    private ObservableList<String> centro_vaccinale_items = FXCollections.observableArrayList();


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

            try {
                FileReader fileReader = new FileReader(PATH_TO_CENTRIVACCINALI_DATI);
                BufferedReader reader = new BufferedReader(fileReader);

                ChoiceBox<String> choiceBox_vaccinoSomministrato = ((ChoiceBox<String>) scene.lookup("#cbx_vaccinoSomministrato"));
                choiceBox_vaccinoSomministrato.setItems(vaccino_somministrato_items);

                ChoiceBox<String> choiceBox = ((ChoiceBox<String>) scene.lookup("#cbx_centroVaccinale"));

                String line;

                while ((line = reader.readLine()) != null&&line.compareTo("")!=0) {
                    StringTokenizer tokenizer = new StringTokenizer(line, ";");
                    centro_vaccinale_items.add(tokenizer.nextToken());
                }
                choiceBox.setItems(centro_vaccinale_items);
            }
            catch (IOException e){
                e.printStackTrace();
            }


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
     * Registra un paziente vaccinato nel file di testo relativo al centro vaccinale.
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena attuale da cui ottenere i valori da inserire nel file.
     */
    public void registraVaccinato(ActionEvent event) {
        Scene currentScene = ((Button) event.getSource()).getScene();
        String name = ((TextField) currentScene.lookup("#txt_nomePaziente")).getText();
        String surname = ((TextField) currentScene.lookup("#txt_cognomePaziente")).getText();
        String codice_fiscale = ((TextField) currentScene.lookup("#txt_cfPaziente")).getText();
        String vaccineType = ((ChoiceBox<String>) currentScene.lookup("#cbx_vaccinoSomministrato")).getValue();
        LocalDate vaccinationDate = ((DatePicker) currentScene.lookup("#datePicker_datavaccinazione")).getValue();
        String centroVaccinale = ((ChoiceBox<String>) currentScene.lookup("#cbx_centroVaccinale")).getValue();
        String dataVaccinazione = "";
        String idVaccinazione = null;

        if (vaccinationDate != null) {
            dataVaccinazione = vaccinationDate.format(DateTimeFormatter.ofPattern("MMM-dd-yyyy"));
        }
        if (name.equals("") || surname.equals("") || codice_fiscale.equals("") || vaccineType.equals("") || centroVaccinale.equals("") || dataVaccinazione.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Controllare i dati inseriti");
            alert.showAndWait();
        } else {
            try {

                //L'id vaccinazione è diviso nel seguente modo:i primi 6 bit sono composti dal numero riga del centro vaccinale. I restanti 10 sono composti dal numero riga vaccinato.
                FileReader fileReader = new FileReader(PATH_TO_CENTRIVACCINALI_DATI);
                BufferedReader reader = new BufferedReader(fileReader);

                String line;
                int index = 0;

                while ((line = reader.readLine()) != null && (!line.contains(centroVaccinale))) {
                    index++;
                }

                String centerIndex = String.valueOf(index);
                while (centerIndex.length() < 6) {
                    centerIndex = "0" + centerIndex;
                }

                fileReader = new FileReader(PRE_PATH_TO_EVENTI_AVVERSI + centroVaccinale + AFTER_PATH_TO_EVENTI_AVVERSI);
                reader = new BufferedReader(fileReader);

                index = 0;
                while ((line = reader.readLine()) != null) {
                    index++;
                }

                String patientIndex = String.valueOf(index);
                while (patientIndex.length() < 10) {
                    patientIndex = "0" + patientIndex;
                }

                idVaccinazione = centerIndex + patientIndex;
                System.out.println(idVaccinazione);

                String output = LINE_TYPE_PERSON + ";" + name + ";" + surname + ";" + codice_fiscale + ";" + vaccineType + ";" + idVaccinazione + ";" + dataVaccinazione + ";" + centroVaccinale;
                FileWriter writer = new FileWriter(PRE_PATH_TO_EVENTI_AVVERSI + centroVaccinale + AFTER_PATH_TO_EVENTI_AVVERSI, true);
                BufferedWriter out = new BufferedWriter(writer);
                out.write(output);
                out.flush();
                out.newLine();
                out.close();
                writer.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Successo");
                alert.setHeaderText(null);
                alert.setContentText("Paziente registrato a sistema");
                alert.showAndWait();

                ((Stage) currentScene.getWindow()).close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goBackToOpzioniOperatore(MouseEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("PortaleOperatori.fxml");
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
}
