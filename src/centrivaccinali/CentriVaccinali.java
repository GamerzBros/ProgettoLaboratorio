package centrivaccinali;
import cittadini.Cittadini;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.application.Application;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CentriVaccinali extends Application {
    private Scene scene;

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

        scene=new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("login");
        stage.show();
    }

    public void onCentrivaccinaliHoverOn(){
        scene.lookup("centriVaccinaliShadow").setVisible(true);
    }
    public void onCentriVaccinaliHoverOff(){
        scene.lookup("centriVaccinaliShadow").setVisible(false);
    }
    public void onCittadiniHoverOn() {
        scene.lookup("centriVaccinaliittadini").setVisible(true);
    }
    public void onCittadiniHoverOff(){
        scene.lookup("cittadiniShadow").setVisible(false);
    }


    public static void main(String[] args){
      
        new CentriVaccinali();

        Application.launch();
    }

}
