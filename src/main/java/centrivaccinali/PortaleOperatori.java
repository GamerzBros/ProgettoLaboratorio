package centrivaccinali;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Gestisce la UI del portale operatori di centri vaccinali. Consente agli operatori di passare alla UI di aggiunta centro vaccinale e di aggiunta vaccinazioni
 */
public class PortaleOperatori {

    /**
     * Costruttore principale della classe PortaleOperatori
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    public PortaleOperatori(Stage stage){
        loadPortaleOperatoriUI(stage);
    }

    /**
     * Carica la UI del portale operatori
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    private void loadPortaleOperatoriUI(Stage stage){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/fxml/PortaleOperatori.fxml");
            fxmlLoader.setLocation(url);
            fxmlLoader.setController(this);

            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Portale Operatori");

            stage.centerOnScreen();

            InputStream iconStream=getClass().getResourceAsStream("/centrivaccinali/operatorPortalIcon.png");
            Image icon=new Image(iconStream);
            stage.getIcons().set(0,icon);

            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Torna indietro alla schermata di selezione portale
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage su cui inserire la nuova scena per mantenere la stessa finestra ma con una UI diversa
     */
    public void goBackToSelectionUI(MouseEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/SelectionUI.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            Scene scene=new Scene(root);

            Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();

            currentStage.setScene(scene);

            currentStage.setTitle("Seleziona il Portale");

            InputStream icon = getClass().getResourceAsStream("/common/fiorellino.png");
            Image image = new Image(icon);

            currentStage.getIcons().set(0,image);
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Crea la UI per inserire un nuovo centro vaccinale. Viene richiamato quando l'operatore selezione il pulsante per inserire un nuovo centro.
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage in cui inserire la nuova scena
     */
    public void onNuovoCentroSelected(ActionEvent event){
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();

        new RegistraNuovoCentro(stage);
    }

    /**
     * Crea la UI per inserire un nuovo centro vaccinale. Viene richiamato quando l'operatore selezione il pulsante per inserire un nuovo centro.
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage in cui inserire la nuova scena
     */
    public void onNewVaccinate(ActionEvent event) {
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();

        new RegistraNuovoVaccinato(stage);
    }

}
