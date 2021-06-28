package centrivaccinali;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CentriVaccinaliUI{
    private Scene scene;

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
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public  void login(){
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
