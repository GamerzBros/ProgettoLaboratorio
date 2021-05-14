package centrivaccinali;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.*;
import java.net.URL;
import java.util.Scanner;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class
CentriVaccinali extends Application {


    public CentriVaccinali(){
       /* try {
            Parent root=FXMLLoader.load(getClass().getResource("SelectionUI.fxml"));

            Scene scene=new Scene(root);

            Stage stage=new Stage();
            

            stage.setTitle("Seleziona il modulo");
            stage.setScene(scene);
            stage.show();

            //scene.lookup();  per prendere un elemento grafico dato il suo ID

        } catch (IOException e) {
            e.printStackTrace();
        }

        */

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("SelectionUI.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Pagina iniziale");
        InputStream icon = getClass().getResourceAsStream("fiorellino.png");
        Image image = new Image(icon);
        stage.getIcons().add(image);
        stage.show();

    }

    public void registraCentroVaccinale(SingoloCentroVaccinale centroVaccinale)throws Exception{ //metodo per registrare i centri //TODO mettere il try catch al posto del throws
        String nome = centroVaccinale.getNome();
        String indirizzo = centroVaccinale.getIndirizzo();
        String tipologia = centroVaccinale.getTipologia();
        FileWriter writer = new FileWriter("data/CentriVaccinali.txt",true);
        BufferedWriter out = new BufferedWriter(writer);
        String fileInput =nome+";"+indirizzo+";"+tipologia;
        out.write(fileInput);
        out.newLine();
        out.flush();
        out.close();
    }

    public void cercaCentroVaccinale(String nomeCentroVaccinale){

    }

    public void cercaCentroVaccinale(String comune, String tipologia){  //TODO rivedere i tipi dei parametri

    }

    public void visualizzaInfoCentroVaccinale(){

    }

    public void inserisciEventiAvversi(Object eventoAvverso){  //TODO modificare i parametri

    }

    public void onCentriVaccinaliSelected() throws Exception{
        //new CentriVaccinaliUI();

    }

    public void onCittadiniSelected() throws Exception{
       // new Cittadini();
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("login.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("login");
        stage.show();
    }


    @FXML
    private TextField user_txtfield;
    @FXML
    private PasswordField user_password;


    public void onRegisterClicked() throws Exception{ //TODO FAR ANDARE A CAPO QUANDO SCRIVE
        String pwd = user_password.getText();
        String user = user_txtfield.getText();
        FileWriter writer = new FileWriter("data/Cittadini_Registrati.dati.txt",true);
        BufferedWriter out = new BufferedWriter(writer);
        String scrivi = user+";"+pwd;
        out.write(scrivi);
        out.newLine();
        out.close();

    }



    public void onLoginClicked() throws Exception{ //TODO TRY CATCH
        String user = user_txtfield.getText();
        String pwd = user_password.getText();
        String user_temp; //questi temp sono i "candidati" user e psw presi dal reader dal file
        String pwd_temp;
        String[] parts;//contenitore per il metodo split
        if(!user.equals("") && !pwd.equals("")){
            File file = new File("data/Cittadini_Registrati.dati.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String line = reader.nextLine();
                parts = line.split(";");
                user_temp=parts[0];
                pwd_temp=parts[1];
                if(user_temp.equals(user) && pwd_temp.equals(pwd)){
                    System.out.println("LOGGATO");  //in qualche modo qui caricherà la nuova interface, vai pole divertiti
                }else{
                    System.out.println("User inesistente, premere sul tasto 'register'");
                }
            }
        }else{
            System.out.println("Inserire dei dati");
        }
    }

    public static void main(String[] args) throws Exception {

        new CentriVaccinali();
        Application.launch();


    }

}
