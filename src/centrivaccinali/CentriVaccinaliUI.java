package centrivaccinali;

import cittadini.SingoloCittadino;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CentriVaccinaliUI{
    private Scene scene;
    private ObservableList<String> vaccino_somministrato_items = FXCollections.observableArrayList("Pfizer","AstraZeneca","Moderna","J&J");
    private ObservableList<String> centro_vaccinale_items = FXCollections.observableArrayList();
    public static final String PATH_TO_CENTRIVACCINALI_DATI = "data/CentriVaccinali.dati.txt";

    @FXML
    private javafx.scene.control.TextField nome_paziente;
    @FXML
    private javafx.scene.control.TextField cognome_paziente;
    @FXML
    private javafx.scene.control.TextField cf_paziente;
    @FXML
    private TextField ID_vaccinazione;
    @FXML
    private ChoiceBox<String> vaccino_somministrato;
    @FXML
    private DatePicker data_vaccinazione;
    @FXML
    private ChoiceBox<String> centro_vaccinale;



    public CentriVaccinaliUI(){
        //TODO Cristian deve mettere che si apre la finestra opzioniLoggato.fxml
    }

    //TODO Shopper: creare un file interfaccia per l'inserimento di un nuovo centro vaccinale

    public void opzioniLoggato(){
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
    public void onNewVaccinateClicked(){
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

    public void vaccino_somministrato_setter(){ //TODO CRISTIAN DA RIVEDERE PERCHE COSI TRIGGHERA OGNI VOLTA L'EVENTO PER SETTARE IL CHOICEBOX
        vaccino_somministrato.setItems(vaccino_somministrato_items);
    }

    public void centro_vaccinale_setter(){
        String[] parts; //TODO CRISTIAN RIVEDERE OGNI VOLTA AGGIUNGE LO STESSO
        String nome_centro_vaccinale="";
        try{
            File file = new File(PATH_TO_CENTRIVACCINALI_DATI);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String line = reader.nextLine();
                parts = line.split(";");
                nome_centro_vaccinale = parts[0];
                centro_vaccinale_items.add(nome_centro_vaccinale);
            }
            centro_vaccinale.setItems(centro_vaccinale_items);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void registraVaccinato(){
        //TODO chiamare questo metodo dopo registrazione (pole deve fare la sua parte)
        String nome = nome_paziente.getText();
        String cognome = cognome_paziente.getText();
        String codice_fiscale =cf_paziente.getText();
        String tipoVaccino = vaccino_somministrato.getValue();
        String centroVaccinale = centro_vaccinale.getValue();
        String id_vaccino = ID_vaccinazione.getText();
        LocalDate dataVaccino = data_vaccinazione.getValue();
        String dataVaccinazione = dataVaccino.format(DateTimeFormatter.ofPattern("MMM-dd-yyyy"));


        SingoloCittadino cittadino = new SingoloCittadino(nome,cognome,codice_fiscale);
        cittadino.setCentroVaccinale(centroVaccinale);
        cittadino.setIdVaccino(Integer.parseInt(id_vaccino));

        int idVaccino = cittadino.getIdVaccino();

        String output = nome+";"+cognome+";"+codice_fiscale+";"+tipoVaccino+";"+idVaccino+";"+dataVaccinazione;
        String file_ID = "data/"+"Vaccinati_"+centroVaccinale+".dati.txt";
        try{
            FileWriter writer = new FileWriter(file_ID,true);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(output);
            out.flush();
            out.newLine();
            out.close();
            writer.close();
        }catch (IOException e){
            e.toString();
        }
    }
    public void login(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("loginMedico.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("login");
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
