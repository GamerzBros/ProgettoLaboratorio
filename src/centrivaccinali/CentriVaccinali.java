package centrivaccinali;


import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Application;
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

        scene=new Scene(root);

        stage.setScene(scene);
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
        new CentriVaccinaliUI();

    }

    public void onCittadiniSelected() throws Exception{
       // new Cittadini();
    }

    public void onCentrivaccinaliHoverOn(Event event){
        scene.lookup("#centriVaccinaliShadow").setVisible(true);
    }
    public void onCentriVaccinaliHoverOff(Event event){
        scene.lookup("#centriVaccinaliShadow").setVisible(false);
    }
    public void onCittadiniHoverOn() {
        scene.lookup("cittadiniShadow").setVisible(true);
    }
    public void onCittadiniHoverOff(){
        scene.lookup("cittadiniShadow").setVisible(false);
    }


    public static void main(String[] args){
      
        new CentriVaccinali();

        Application.launch();
    }

}
