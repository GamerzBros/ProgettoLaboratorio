package centrivaccinali;

import cittadini.SingoloCittadino;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.application.Application;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CentriVaccinali extends Application {
    public static final String PATH_TO_CENTRIVACCINALI="data/CentriVaccinali.txt";
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI="data/Cittadini_Registrati.dati.txt";
    private Scene scene;
    @FXML
    private Rectangle cittadiniShadow;
    @FXML
    private Rectangle centriVaccinaliShadow;
    @FXML
    private TextField user_txtfield;
    @FXML
    private PasswordField user_password;


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("SelectionUI.fxml");
        loader.setLocation(xmlUrl);

        Parent root = loader.load();

        scene=new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Pagina iniziale");

        InputStream icon = getClass().getResourceAsStream("fiorellino.png");
        Image image = new Image(icon);

        stage.getIcons().add(image);
        stage.show();

    }


    public void registraCentroVaccinale(SingoloCentroVaccinale centroVaccinale)throws Exception { //metodo per registrare i centri //TODO mettere il try catch al posto del throws
        //TODO creare per ogni centro vaccinale un file .txt dinamicamente ( crea un bel algoritmo )
        //ESEMPIO: SE UN CENTRO VACCINALE SI CHIAMA SANGIORGIO, DOVRAI CREARE UN .TXT CHIAMATO Vaccinati_SanGiorgio.dati.txt
        String nome = centroVaccinale.getNome();
        String indirizzo = centroVaccinale.getIndirizzo();
        String tipologia = centroVaccinale.getTipologia();
        FileWriter writer = new FileWriter(PATH_TO_CENTRIVACCINALI, true);
        BufferedWriter out = new BufferedWriter(writer);
        String fileInput = nome + ";" + indirizzo + ";" + tipologia;
        out.write(fileInput);
        out.newLine();
        out.flush();
        out.close();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public void cercaCentroVaccinale(String nomeCentroVaccinale)throws FileNotFoundException{ //Ricerca centro per nome, ogni centro che contiene quella "parte" di nome, viene visualizzato
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

        /*parts = line.split(";");
        if(parts[0].contains(nomeCentroVaccinale)){
            System.out.println("Centro trovato");
        }else{
            System.out.println("Il centro potrebbe non esistere");
        }
        reader.close();

         */
    }

    public void cercaCentroVaccinale(String comune, String tipologia) throws FileNotFoundException{  //TODO rivedere i tipi dei parametri e try catch

    }

    public void visualizzaInfoCentroVaccinale(SingoloCentroVaccinale centroVaccinale){
        System.out.println(centroVaccinale.toString());

    }

    public void inserisciEventiAvversi(Object eventoAvverso){  //TODO modificare i parametri

    }

    public void onCentriVaccinaliSelected() throws Exception{
        new CentriVaccinaliUI();

    }

    public void onCittadiniSelected() throws Exception{
       // new Cittadini();
    }


    public void onCentrivaccinaliHoverOn() {
        //scene.lookup("centriVaccinaliShadow").setVisible(true);
        centriVaccinaliShadow.setVisible(true);
    }


    public void onRegisterClicked() throws Exception{ //TODO FAR ANDARE A CAPO QUANDO SCRIVE
        String pwd = user_password.getText();
        String user = user_txtfield.getText();


        //Hashing della password per renderla one-way
        MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
        byte[] pwdBytes=messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8));

        //Trasformazione dei byte hashati della password in stringa esadecimale
        StringBuilder sb=new StringBuilder(pwdBytes.length*2);
        for(byte b: pwdBytes){
            sb.append(String.format("%02x",b));
        }
        pwd=sb.toString();


        FileWriter writer = new FileWriter(PATH_TO_CITTADINI_REGISTRATI_DATI,true);
        BufferedWriter out = new BufferedWriter(writer);

        String scrivi = user+";"+pwd;
        out.write(scrivi);
        out.newLine();
        out.close();
    }



    public void onLoginClicked() throws Exception { //TODO TRY CATCH
        String user = user_txtfield.getText();
        String pwd = user_password.getText();

        //Hashing della password per renderla one-way
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] pwdBytes = messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8));

        //Trasformazione dei byte hashati della password in stringa esadecimale
        StringBuilder sb = new StringBuilder(pwdBytes.length * 2);
        for (byte b : pwdBytes){
            sb.append(String.format("%02x", b));
        }
        pwd=sb.toString();


        String user_temp; //questi temp sono i "candidati" user e psw presi dal reader dal file
        String pwd_temp;
        String[] parts;//contenitore per il metodo split
        if(!user.equals("") && !pwd.equals("")){
            File file = new File(PATH_TO_CITTADINI_REGISTRATI_DATI);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String line = reader.nextLine();
                parts = line.split(";");
                user_temp=parts[0];
                pwd_temp=parts[1];

                if(user_temp.equals(user) && pwd_temp.equals(pwd)){
                    System.out.println("LOGGATO");  //in qualche modo qui caricherà la nuova interface, vai pole divertiti
                }else{
                    System.out.println("User inesistente, premere sul tasto 'register'");//popup magari (?)
                }
            }
        }else{
            System.out.println("Inserire dei dati");
        }
    }

    public static void registraVaccinato(SingoloCittadino cittadino){
        //TODO chiamare questo metodo dopo registrazione (pole deve fare la sua parte)
        //TODO  scrivere dati del cittadino nel file (Vaccinati_NomeCentroVaccinale.dati )
        String nome = cittadino.getNome();
        String cognome = cittadino.getCognome();
        String codice_fiscale = cittadino.getCodice_fiscale();
        String tipoVaccino = cittadino.getTipoVaccino();
        String centroVaccinale = cittadino.getCentroVaccinale();
        int idVaccino = cittadino.getIdVaccino();
        Date dataVaccinazione = cittadino.getDataVaccinazione();

        String output = nome+cognome+codice_fiscale+tipoVaccino+idVaccino+dataVaccinazione;
        String file_ID = "Vaccinati_"+centroVaccinale+".dati.txt";
        try{
            FileWriter writer = new FileWriter(file_ID);
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

    public static void main(String[] args) throws Exception {

         CentriVaccinali c = new CentriVaccinali();
         c.cercaCentroVaccinale("Nome");
         Application.launch();


    }

}
