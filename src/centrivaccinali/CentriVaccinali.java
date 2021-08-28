package centrivaccinali;

import cittadini.Cittadini;
import cittadini.SingoloCittadino;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.application.Application;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
//Cristian Arcadi 745389 Varese
//David Poletti 746597 Varese
//Eros Marsichina 745299 Varese
//Tommaso Morosi  Varese
public class CentriVaccinali extends Application {
    public static final String PATH_TO_CENTRIVACCINALI_DATI = "data/CentriVaccinali.dati.txt";
    private ObservableList<String> vaccino_somministrato_items = FXCollections.observableArrayList("Pfizer","AstraZeneca","Moderna","J&J");
    private ObservableList<String> centro_vaccinale_items = FXCollections.observableArrayList();
    private ObservableList<String> qualificatore_items = FXCollections.observableArrayList("via","v.le","pzza");
    private ObservableList<String>tipologia_items = FXCollections.observableArrayList("ospedaliero","aziendale","hub");
    private Scene scene;
    private Cittadini portaleCittadini;
    @FXML
    private ChoiceBox<String> choiceBox_vaccinoSomministrato;
    @FXML
    private DatePicker datePicker_datavaccinazione;
    @FXML
    private ChoiceBox<String> centro_vaccinale;
    @FXML
    private TextField nome_centro;
    @FXML
    private TextField nome_via;
    @FXML
    private TextField numero_civico;
    @FXML
    private TextField comune;
    @FXML
    private TextField provincia;
    @FXML
    private TextField cap;
    @FXML
    private ChoiceBox<String> qualificatore;
    @FXML
    private ChoiceBox<String> tipologia;
    @FXML
    private Button annulla;




    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("SelectionUI.fxml");
        loader.setLocation(xmlUrl);

        Parent root = loader.load();

        Scene scene=new Scene(root);


        stage.setScene(scene);
        stage.setTitle("Pagina iniziale");

        InputStream icon = getClass().getResourceAsStream("fiorellino.png");
        Image image = new Image(icon);

        stage.getIcons().add(image);
        stage.show();
    }


    public void registraCentroVaccinale(){
        String nome = nome_centro.getText();
        String qualif = qualificatore.getValue();
        String via = nome_via.getText();
        String civico = numero_civico.getText();
        String com = comune.getText();
        String prov = provincia.getText();
        String Cap = cap.getText();
        String tipolog = tipologia.getValue();
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

                String file_ID = "data/"+"Vaccinati_"+nome+".dati.txt";
                File fileVaccinati=new File(file_ID);
                if (!fileVaccinati.exists()){
                    fileVaccinati.createNewFile();
                }
            }catch (IOException e){
                e.toString();
            }
        }
    }


    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /*public void cercaCentroVaccinale(String nomeCentroVaccinale)throws FileNotFoundException{ //Ricerca centro per nome, ogni centro che contiene quella "parte" di nome, viene visualizzato
        try{
            File file = new File(PATH_TO_CENTRIVACCINALI);
            Scanner reader = new Scanner(file);
            String[] parts;
            while(reader.hasNext()){
                String line = reader.nextLine();
                parts = line.split(";");
                if(parts[0].contains(nomeCentroVaccinale)){
                    System.out.println("Centri trovati:"+parts[0]);
                }else{
                    System.out.println("Il centro potrebbe non esistere");
                }
            }
            reader.close();

        }catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
    }*/

    public void onCentriVaccinaliSelected(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("opzioniLoggato.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            scene = new Scene(root);

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

    public void onNuovoCentroSelected(){
        try {
            FXMLLoader loader = new FXMLLoader();
            //URL xmlUrl = getClass().getResource("nuovoCentroVaccinaleRifatto.fxml");
            URL xmlUrl = getClass().getResource("nuovoCentroVaccinaleRifatto.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Nuovo centro vaccinale");

            InputStream icon = getClass().getResourceAsStream("fiorellino.png");
            Image image = new Image(icon);

            stage.getIcons().add(image);

            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }

    public void registraVaccinato(ActionEvent event){
        Scene currentScene=((Button)event.getSource()).getScene();
        String nome = ((TextField)currentScene.lookup("#txt_nomePaziente")).getText();
        String cognome = ((TextField)currentScene.lookup("#txt_cognomePaziente")).getText();
        String codice_fiscale =((TextField)currentScene.lookup("#txt_cfPaziente")).getText();
        String tipoVaccino = ((ChoiceBox<String>)currentScene.lookup("#choiceBox_vaccinoSomministrato")).getValue();
        String centroVaccinale = centro_vaccinale.getValue();
        String idVaccinazione=null;
        LocalDate dataVaccino = datePicker_datavaccinazione.getValue();
        String dataVaccinazione = dataVaccino.format(DateTimeFormatter.ofPattern("MMM-dd-yyyy"));


        //TODO Calcolare l'ID vaccinazione
        //TODO vedere se questo singolo Cittadini serve comunque
        SingoloCittadino cittadino = new SingoloCittadino(nome,cognome,codice_fiscale,dataVaccino,tipoVaccino,Integer.parseInt(idVaccinazione),centroVaccinale);

        String output = nome+";"+cognome+";"+codice_fiscale+";"+tipoVaccino+";"+idVaccinazione+";"+dataVaccinazione+";"+centroVaccinale;
        String file_ID = "data/"+"Vaccinati_"+centroVaccinale+".dati.txt";
        try{
            FileWriter writer = new FileWriter(file_ID,true);
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
        }catch (IOException e){
            e.printStackTrace();
        }
    }


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

    public void onNewVaccinate(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("nuovoPaziente.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();



            scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Nuovo Paziente");



            InputStream icon = getClass().getResourceAsStream("fiorellino.png");
            Image image = new Image(icon);
            stage.getIcons().add(image);
            stage.show();

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void vaccino_somministrato_setter(){
        choiceBox_vaccinoSomministrato.setItems(vaccino_somministrato_items);
    }

    public void qualificatore_setter(){
        qualificatore.setItems(qualificatore_items);
    }

    public void tipologia_setter(){
        tipologia.setItems(tipologia_items);
    }

    public void centro_vaccinale_setter(){
        String[] parts;
        String nome_centro_vaccinale="";
        try{
            File file = new File(PATH_TO_CENTRIVACCINALI_DATI);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String line = reader.nextLine();
                parts = line.split(";");
                nome_centro_vaccinale = parts[0];
                if(!centro_vaccinale_items.contains(nome_centro_vaccinale))
                    centro_vaccinale_items.add(nome_centro_vaccinale);
            }
            centro_vaccinale.setItems(centro_vaccinale_items);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void annulla_button(){
        Stage stage = (Stage)annulla.getScene().getWindow();
        stage.close();
    }



    public void onNewVaccinateClicked(){
        onNewVaccinate();
    }



    public static void main(String[] args) throws Exception {
         Application.launch();

    }

}
