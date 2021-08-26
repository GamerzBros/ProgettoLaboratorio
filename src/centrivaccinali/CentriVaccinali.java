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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CentriVaccinali extends Application {
    public static final String PATH_TO_CENTRIVACCINALI="data/CentriVaccinali.dati.txt";
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI="data/Cittadini_Registrati.dati.txt";
    public static final String PATH_TO_DATA="data/";
    public static final String PREFIX = "Vaccinati_";
    public static final String SUFFIX = ".dati.txt";
    private ObservableList<String> vaccino_somministrato_items = FXCollections.observableArrayList("Pfizer","AstraZeneca","Moderna","J&J");
    private ObservableList<String> centro_vaccinale_items = FXCollections.observableArrayList();
    private ObservableList<String> qualificatore_items = FXCollections.observableArrayList("via","v.le","pzza");
    private ObservableList<String>tipologia_items = FXCollections.observableArrayList("ospedaliero","aziendale","hub");
    public static final String PATH_TO_CENTRIVACCINALI_DATI = "data/CentriVaccinali.dati.txt";
    private Scene scene;
    Cittadini cittadini;
    @FXML
    private TextField user_txtfield;
    @FXML
    private PasswordField user_password;
    @FXML
    private Button btn_cittadini;
    @FXML
    private Button btn_centriVaccinali;
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

    public void cercaCentroVaccinale(String nomeCentroVaccinale)throws FileNotFoundException{ //Ricerca centro per nome, ogni centro che contiene quella "parte" di nome, viene visualizzato
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
    }

    public void cercaCentroVaccinale(String comune, String tipologia) throws FileNotFoundException{  //TODO rivedere i tipi dei parametri e try catch

    }

    public void visualizzaInfoCentroVaccinale(SingoloCentroVaccinale centroVaccinale){
        System.out.println(centroVaccinale.toString());

    }

    public void inserisciEventiAvversi(Object eventoAvverso){  //TODO modificare i parametri

    }

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
            URL xmlUrl = getClass().getResource("nuovoCentroVaccinale.fxml");
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText(null);
            alert.setContentText("Paziente registrato a sistema");
            alert.showAndWait();
        }catch (IOException e){
            e.toString();
        }
    }


    public void onCittadiniSelected(){
        cittadini=new Cittadini();
        try {
            cittadini.loadMainCittadiniUI();
            Stage stage=(Stage)btn_cittadini.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onRegisterClicked() throws Exception{ //TODO FAR ANDARE A CAPO QUANDO SCRIVE
        String pwd = user_password.getText();
        String user = user_txtfield.getText();

        /*Hashing della password per renderla one-way
        MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
        pwd=new String(messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8)));*/ //TODO controllare che questo controllore sia giusto. sul web dicono che non funzioni correttamente

        try{
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8));
            pwd = toHexString(hash);
            System.out.println(pwd);
        }catch (Exception e) {
            e.printStackTrace();
        }

        FileWriter writer = new FileWriter(PATH_TO_CITTADINI_REGISTRATI_DATI,true);
        BufferedWriter out = new BufferedWriter(writer);
        String scrivi = user+";"+pwd;
        out.write(scrivi);
        out.newLine();
        out.close();
    }

    private String toHexString(byte[] array) {
        StringBuilder sb = new StringBuilder(array.length * 2);

        for (byte b : array){
            int value = 0xFF & b;
            String toAppend = Integer.toHexString(value);

            sb.append(toAppend);
        }
        sb.setLength(sb.length()-1);
        return sb.toString();
    }

    public void onLoginClicked() {
        String user = user_txtfield.getText();
        String pwd = user_password.getText();
        String user_temp; //questi temp sono i "candidati" user e psw presi dal reader dal file
        String pwd_temp;
        String[] parts;//contenitore per il metodo split

        try {
            if (!user.equals("") && !pwd.equals("")) {
                File file = new File(PATH_TO_CITTADINI_REGISTRATI_DATI);
                Scanner reader = new Scanner(file);
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    parts = line.split(";");
                    user_temp = parts[0];
                    pwd_temp = parts[1];

                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    pwd_temp = new String(messageDigest.digest(pwd_temp.getBytes(StandardCharsets.UTF_8))); //TODO Rivedere controllore

                    if (user_temp.equals(user) && pwd_temp.equals(pwd)) {
                        System.out.println("LOGGATO");  //in qualche modo qui caricherà la nuova interface, vai pole divertiti
                    } else {
                        System.out.println("User inesistente, premere sul tasto 'register'");//popup magari (?)
                        /*
                        JOptionPane.showMessageDialog(null,
                            "User inesistente, premere sul tasto 'register'",
                            JOptionPane.ERROR_MESSAGE);                           popup?
                        */
                    }
                }
            } else {
                System.out.println("Inserire dei dati");
            }
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
        vaccino_somministrato.setItems(vaccino_somministrato_items);
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
