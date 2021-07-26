package cittadini;

import centrivaccinali.CentriVaccinali;
import centrivaccinali.SingoloCentroVaccinale;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CittadiniUI extends Application {
    private Cittadini cittadini;
    @FXML
    private ScrollPane scrollPane_CentriVaccinali;
    private Vector<SingoloCentroVaccinale> centriVaccinaliList=new Vector<>();

    
    @Override
    public void start(Stage stage) throws Exception {

        try {
            FXMLLoader fxmlLoader=new FXMLLoader();
            URL url=getClass().getResource("mainCittadini.fxml");
            fxmlLoader.setLocation(url);
            Parent root=fxmlLoader.load();

            Scene scene=new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Portale Cittadini");
            stage.show();

            VBox scrollPaneContent=(VBox)scrollPane_CentriVaccinali.getContent();
            //scrollPaneContent.getChildren().add();

            centriVaccinaliList=Cittadini.getCentriVaccinaliFromFile();

            for (int i=0;i<centriVaccinaliList.size();i++){
                Pane panel=new Pane();
                SingoloCentroVaccinale currentCentro=centriVaccinaliList.get(i);

                panel.getChildren().add(new Label(currentCentro.getNome()));
                panel.getChildren().add(new Label(currentCentro.getIndirizzo()));
                panel.getChildren().add(new Label(currentCentro.getTipologia()));


                scrollPaneContent.getChildren().add(panel);
                //TODO aggiungere per ogni nodo della lista un centro vaccinale grafico
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
