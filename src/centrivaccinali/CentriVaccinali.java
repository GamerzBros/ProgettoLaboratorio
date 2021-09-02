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
import java.util.StringTokenizer;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
//Cristian Arcadi 745389 Varese
//David Poletti 746597 Varese
//Eros Marsichina 745299 Varese
//Tommaso Morosi  Varese
public class CentriVaccinali extends Application {
    public static final String PATH_TO_CENTRIVACCINALI_DATI = "data/CentriVaccinali.dati.txt";
    public static final String PRE_PATH_TO_EVENTI_AVVERSI="data/Vaccinati_";
    public static final String AFTER_PATH_TO_EVENTI_AVVERSI=".dati.txt";
    public static final String LINE_TYPE_PERSON ="V";
    public static final String LINE_TYPE_EVENT ="E";
    private ObservableList<String> vaccino_somministrato_items = FXCollections.observableArrayList("Pfizer","AstraZeneca","Moderna","J&J");
    private ObservableList<String> centro_vaccinale_items = FXCollections.observableArrayList();
    private ObservableList<String> qualificatore_items = FXCollections.observableArrayList("Via","V.le","Piazza");
    private ObservableList<String>tipologia_items = FXCollections.observableArrayList("Ospedaliero","Aziendale","Hub");
    private Cittadini portaleCittadini;




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

            //TODO vedere se questo singolo Cittadini serve comunque
            SingoloCittadino cittadino = new SingoloCittadino(name, surname, codice_fiscale, vaccinationDate, vaccineType, Integer.parseInt(idVaccinazione), centroVaccinale);

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



            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Nuovo Paziente");


            FileReader fileReader=new FileReader(PATH_TO_CENTRIVACCINALI_DATI);
            BufferedReader reader=new BufferedReader(fileReader);

            ChoiceBox<String>choiceBox_vaccinoSomministrato=((ChoiceBox<String>)scene.lookup("#cbx_vaccinoSomministrato"));
            choiceBox_vaccinoSomministrato.setItems(vaccino_somministrato_items);

            ChoiceBox<String> choiceBox=((ChoiceBox<String>)scene.lookup("#cbx_centroVaccinale"));

            String line;

            while ((line=reader.readLine())!=null){
                StringTokenizer tokenizer=new StringTokenizer(line,";");
                centro_vaccinale_items.add(tokenizer.nextToken());
            }
            choiceBox.setItems(centro_vaccinale_items);


            InputStream icon = getClass().getResourceAsStream("fiorellino.png");
            Image image = new Image(icon);
            stage.getIcons().add(image);
            stage.show();

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void annulla_button(ActionEvent event){
        Stage currentStage = (Stage)(((Button)event.getSource()).getScene()).getWindow();
        currentStage.close();
    }



    public void onNewVaccinateClicked(){
        onNewVaccinate();
    }



    public static void main(String[] args) throws Exception {
         Application.launch();

    }

}
