package centrivaccinali;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CentriVaccinaliUI extends Application {
    private Scene scene;

    public CentriVaccinaliUI(){

    }

    @Override
    public void start(Stage stage) throws Exception {

    }

    @Override
    public void stop() throws Exception {
        super.stop();
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

    //TODO Crea Interfaccia Grafica Centri Vaccinali
}
