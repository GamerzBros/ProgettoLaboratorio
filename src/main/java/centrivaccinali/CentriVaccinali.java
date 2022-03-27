package centrivaccinali;

import cittadini.Cittadini;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

/*Cristian Arcadi 745389 Varese
  David Poletti 746597 Varese
  Eros Marsichina 745299 Varese
  Tommaso Morosi 741227 Varese*/

/**
 * Contiene tutte le UI e i metodi del portale CentriVaccinali. Contiene inoltre la UI che permette di seleziona il portale con cui interagire.
 */
public class CentriVaccinali extends Application {
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
    /**
     * Lista contente le tipologie di indirizzo
     */
    private ObservableList<String> qualificatore_items = FXCollections.observableArrayList("Via","V.le","Piazza");
    /**
     * Lista contenete le tipologie di centro vaccinali
     */
    private ObservableList<String>tipologia_items = FXCollections.observableArrayList("Ospedaliero","Aziendale","Hub");
    /**
     * Reference al portale cittadini
     */
    private Cittadini portaleCittadini;

    /**
     * Crea la UI principale che permette di scegliere il portale. Metodo che viene eseguito subito dopo la creazione della classe.
     * @param stage Lo stage che conterrà la scena. Uno stage è una finestra, mentre una scena è tutto ciò contenuto in uno stage.
     * @throws Exception L'eccezione provocata dallo start del programma
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/centrivaccinali/SelectionUI.fxml");

        loader.setLocation(xmlUrl);

        Parent root = loader.load();

        Scene scene=new Scene(root);


        stage.setScene(scene);
        stage.setTitle("Pagina iniziale");

        AnchorPane pane=(AnchorPane)scene.lookup("#anchroPane");



        InputStream icon = getClass().getResourceAsStream("fiorellino.png");
        Image image = new Image(icon);

        stage.getIcons().add(image);
        stage.show();
    }

    /**
     * Registra un centro vaccinale prendendo i dati dalla UI.
     * @param event L'evento che richiamerà il metodo attuale. Necessario per prendere il bottone sorgente dell'evento, e da quest'ultimo ottenere la scena. Dalla scena è possibile ottenere tutti componenti grafici con le informazioni necessarie alla registrazione.
     */
    public void registraCentroVaccinale(ActionEvent event){
        Scene currentScene=((Button)event.getSource()).getScene();
        String nome = ((TextField)currentScene.lookup("#txt_nomeCentro")).getText();
        String qualif = ((ChoiceBox<String>)currentScene.lookup("#cbx_qualificatore")).getValue();
        String via = ((TextField)currentScene.lookup("#txt_via")).getText();
        String civico = ((TextField)currentScene.lookup("#txt_numeroCivico")).getText();
        String com = ((TextField)currentScene.lookup("#txt_comune")).getText();
        String prov = ((TextField)currentScene.lookup("#txt_provincia")).getText();
        String Cap = ((TextField)currentScene.lookup("#txt_cap")).getText();
        String tipolog = ((ChoiceBox<String>)currentScene.lookup("#cbx_tipologia")).getValue();

        if(nome.equals("") || qualif==null || via.equals("") || civico.equals("") || com.equals("") || prov.equals("") || Cap.equals("") || tipolog==null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Controllare i dati inseriti");
            alert.showAndWait();
        }
        else{
            try{
                File file = new File(PATH_TO_CENTRIVACCINALI_DATI);
                BufferedWriter out = new BufferedWriter(new FileWriter(file,true));
                String output = nome+";"+qualif+";"+via+";"+civico+";"+com+";"+prov+";"+Cap+";"+tipolog;
                out.write(output);
                out.newLine();
                out.flush();
                out.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Successo");
                alert.setHeaderText(null);
                alert.setContentText("Centro vaccinale registrato");
                alert.showAndWait();

                String file_ID = PRE_PATH_TO_EVENTI_AVVERSI+nome+AFTER_PATH_TO_EVENTI_AVVERSI;
                File fileVaccinati=new File(file_ID);
                if (!fileVaccinati.exists()){
                    fileVaccinati.createNewFile();
                }

                ((Stage)currentScene.getWindow()).close();

            }catch (IOException e){
                e.toString();
            }
        }
    }


    /**
     * Termina ogni processo aperto dal programma. Viene eseguito automaticamente qualora ogni finestra del programma venga chiusa.
     * @throws Exception L'eccezione provocata dallo stop del programma.
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /**
     * Crea la UI del portale Cittadini. Viene richiamato una volta che viene selezionato il portale Cittadini dalla UI principale.
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage da chiudere.
     */
    public void onCittadiniSelected(ActionEvent event){
        portaleCittadini =new Cittadini();
        try {
            portaleCittadini.loadMainCittadiniUI();
            Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea la UI del portale Centri Vaccinali. Viene richiamato una volta che viene selezionato il portale Centri Vaccinale dalla UI principale.
     */
    public void onCentriVaccinaliSelected(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("opzioniLoggato.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("opzioniLoggato");

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
     * Crea la UI per inserire un nuovo centro vaccinale. Viene richiamato quando l'operatore selezione il pulsante per inserire un nuovo centro.
     */
    public void onNuovoCentroSelected(){
        try {
            FXMLLoader loader = new FXMLLoader();
            //URL xmlUrl = getClass().getResource("nuovoCentroVaccinale.fxml");
            URL xmlUrl = getClass().getResource("nuovoCentroVaccinale.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Nuovo centro vaccinale");

            ChoiceBox<String> choiceBox_qualificatore=((ChoiceBox<String>)scene.lookup("#cbx_qualificatore"));
            choiceBox_qualificatore.setItems(qualificatore_items);
            ChoiceBox<String> choiceBox_tipologiaCentro=((ChoiceBox<String>)scene.lookup("#cbx_tipologia"));
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
     * Crea la UI per inserire un nuovo centro vaccinale. Viene richiamato quando l'operatore selezione il pulsante per inserire un nuovo centro.
     */
    public void onNewVaccinate(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("nuovoPaziente.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();



            Scene scene = new Scene(root);

            Stage stage = new Stage();
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
    public void registraVaccinato(ActionEvent event){
        Scene currentScene=((Button)event.getSource()).getScene();
        String name = ((TextField)currentScene.lookup("#txt_nomePaziente")).getText();
        String surname = ((TextField)currentScene.lookup("#txt_cognomePaziente")).getText();
        String codice_fiscale =((TextField)currentScene.lookup("#txt_cfPaziente")).getText();
        String vaccineType = ((ChoiceBox<String>)currentScene.lookup("#cbx_vaccinoSomministrato")).getValue();
        LocalDate vaccinationDate = ((DatePicker)currentScene.lookup("#datePicker_datavaccinazione")).getValue();
        String centroVaccinale=((ChoiceBox<String>)currentScene.lookup("#cbx_centroVaccinale")).getValue();
        String dataVaccinazione = vaccinationDate.format(DateTimeFormatter.ofPattern("MMM-dd-yyyy"));
        String idVaccinazione=null;


        try {
            //L'id vaccinazione è diviso nel seguente modo:i primi 6 bit sono composti dal numero riga del centro vaccinale. I restanti 10 sono composti dal numero riga vaccinato.
            FileReader fileReader=new FileReader(PATH_TO_CENTRIVACCINALI_DATI);
            BufferedReader reader=new BufferedReader(fileReader);

            String line;
            int index=0;

            while ((line=reader.readLine())!=null&&(!line.contains(centroVaccinale))){
                index++;
            }

            String centerIndex=String.valueOf(index);
            while (centerIndex.length()<6){
                centerIndex="0"+centerIndex;
            }

            fileReader = new FileReader(PRE_PATH_TO_EVENTI_AVVERSI + centroVaccinale + AFTER_PATH_TO_EVENTI_AVVERSI);
            reader=new BufferedReader(fileReader);

            index=0;
            while ((line=reader.readLine())!=null){
                index++;
            }

            String patientIndex=String.valueOf(index);
            while (patientIndex.length()<10){
                patientIndex="0"+patientIndex;
            }

            idVaccinazione=centerIndex+patientIndex;
            System.out.println(idVaccinazione);

            String output = LINE_TYPE_PERSON +";"+name + ";" + surname + ";" + codice_fiscale + ";" + vaccineType + ";" + idVaccinazione + ";" + dataVaccinazione + ";" + centroVaccinale;
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

            ((Stage)currentScene.getWindow()).close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Chiude la finestra (stage) attuale. Il metodo viene usato per tutte le UI, relative al package, che contengono il tasto "annulla".
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage da chiudere.
     */
    public void annulla_button(ActionEvent event){
        Stage currentStage = (Stage)(((Button)event.getSource()).getScene()).getWindow();
        currentStage.close();
    }

    public void onChoiceButtonHover(MouseEvent event){
        Button btn=(Button)event.getSource();
        ImageView imgView = (ImageView) btn.getScene().lookup("#imgBg");

        if(btn.getText().equals("Portale Cittadini")) {
            imgView.setImage(new Image(getClass().getResourceAsStream("/centrivaccinali/crowd.png")));
            btn.getScene().lookup("#lbl_main").setVisible(false);
            btn.getScene().lookup("#lbl_citizen").setVisible(true);
        }
        else if(btn.getText().equals("Portale Operatori")){
            imgView.setImage(new Image(getClass().getResourceAsStream("/centrivaccinali/medici.png")));
            btn.getScene().lookup("#lbl_main").setVisible(false);
            btn.getScene().lookup("#lbl_operator").setVisible(true);
        }


    }

    public void onChoiceButtonExit(MouseEvent event){
        ImageView imgView=(ImageView)((Button)event.getSource()).getScene().lookup("#imgBg");
        imgView.setImage(new Image(getClass().getResourceAsStream("/centrivaccinali/varese.png")));

        Button btnSource=(Button)event.getSource();
        btnSource.getScene().lookup("#lbl_main").setVisible(true);
        btnSource.getScene().lookup("#lbl_citizen").setVisible(false);
        btnSource.getScene().lookup("#lbl_operator").setVisible(false);
    }

    /**
     * Contiene il codice di avvio del programma.
     * @param args Gli argomenti di lancio passati via console al programma.
     * @throws Exception L'eccezione del metodo main
     */
    public static void main(String[] args) throws Exception {
         Application.launch();
    }

}
