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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CentriVaccinali extends Application {


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

    public void registraCentroVaccinale(SingoloCentroVaccinale centroVaccinale){ //metodo per registrare i centri

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

    public void onLoginClicked() throws Exception{

    }

    public static void main(String[] args){

        new CentriVaccinali();

        Application.launch();
    }

}
