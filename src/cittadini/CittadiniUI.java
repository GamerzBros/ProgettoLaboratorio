package cittadini;

import centrivaccinali.SingoloCentroVaccinale;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CittadiniUI{
    @FXML
    ScrollPane scrollPane_CentriVaccinali;
    Vector<SingoloCentroVaccinale> centriVaccinaliList=new Vector<>();

    public CittadiniUI(){
        createUI();
    }

    public void createUI(){
        try {
            FXMLLoader fxmlLoader=new FXMLLoader();
            URL url=getClass().getResource("home.fxml");
            fxmlLoader.setLocation(url);
            Parent root=fxmlLoader.load();

            Scene scene=new Scene(root);
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.setTitle("Portale Cittadini");
            stage.show();

            VBox scrollPaneContent=(VBox)scrollPane_CentriVaccinali.getContent();
            //scrollPaneContent.getChildren().add();

            centriVaccinaliList=getCentriVaccinaliFromFile();

            for (int i=0;i<centriVaccinaliList.size();i++){
                //TODO aggiungere per ogni nodo della lista un centro vaccinale grafico
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<SingoloCentroVaccinale> getCentriVaccinaliFromFile(){
        Vector<SingoloCentroVaccinale> vector=new Vector<>();

        try {
            FileReader fileReader = new FileReader("CentriVaccinali.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line=null;

            while ((line=bufferedReader.readLine())!=null){
                StringTokenizer st=new StringTokenizer(line,";");
                if(st.countTokens()==3){
                    String nome=st.nextToken();
                    String indirizzo=st.nextToken();
                    String tipologia=st.nextToken();

                    vector.add(new SingoloCentroVaccinale(nome,indirizzo,tipologia));
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return vector;

    }
}
