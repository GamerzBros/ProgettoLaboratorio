package centrivaccinali;



import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.application.Application;
import java.io.InputStream;
import java.net.URL;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CentriVaccinali extends Application {
    private Scene scene;
    private Rectangle cittadiniShadow;
    private Rectangle centriVaccinaliShadow;

    public CentriVaccinali(){

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("SelectionUI.fxml");
        loader.setLocation(xmlUrl);

        Parent root = loader.load();

        scene=new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Pagina iniziale");

        cittadiniShadow=(Rectangle)scene.lookup("cittadiniShadow");
        centriVaccinaliShadow=(Rectangle)scene.lookup("centriVaccinaliShadow");

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
        new CentriVaccinaliUI();

    }

    public void onCittadiniSelected() throws Exception{
       // new Cittadini();
    }

    public void onCentrivaccinaliHoverOn(){
        //scene.lookup("centriVaccinaliShadow").setVisible(true);
        centriVaccinaliShadow.setVisible(true);
    }
    public void onCentriVaccinaliHoverOff(){
        //scene.lookup("centriVaccinaliShadow").setVisible(false);
        centriVaccinaliShadow.setVisible(false);
    }
    public void onCittadiniHoverOn() {
        //scene.lookup("cittadiniShadow").setVisible(true);
        cittadiniShadow.setVisible(true);
    }
    public void onCittadiniHoverOff(){
        //scene.lookup("cittadiniShadow").setVisible(false);
        cittadiniShadow.setVisible(false);
    }


    public static void main(String[] args){
      
        new CentriVaccinali();

        Application.launch();
    }

}
