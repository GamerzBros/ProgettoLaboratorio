package cittadini;

import centrivaccinali.CentriVaccinali;
import centrivaccinali.SingoloCentroVaccinale;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;


import java.io.BufferedWriter;
import java.io.FileWriter;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class Cittadini implements EventHandler<ActionEvent> {
    private SingoloCittadino cittadinoLoggato;

    @FXML
    private ScrollPane scrollPane_CentriVaccinali;
    private Vector<SingoloCentroVaccinale> centriVaccinaliList=new Vector<>();


    public void loadUI() throws Exception {
        FXMLLoader fxmlLoader=new FXMLLoader();
        URL url=getClass().getResource("mainCittadini.fxml");
        fxmlLoader.setLocation(url);
        Parent root=fxmlLoader.load();

        Scene scene=new Scene(root);

        Stage stage=new Stage();
        stage.setScene(scene);
        stage.setTitle("Portale Cittadini");

        scrollPane_CentriVaccinali=(ScrollPane) scene.lookup("#scrollPane_CentriVaccinali");

        centriVaccinaliList=Cittadini.getCentriVaccinaliFromFile();

        creaVbox(centriVaccinaliList);

        stage.show();
    }

    private void creaVbox(List<SingoloCentroVaccinale> centriVaccinaliMostrati){
        VBox scrollPaneContent=new VBox();
        scrollPaneContent.setMinWidth(scrollPane_CentriVaccinali.getPrefWidth()-2);

        scrollPane_CentriVaccinali.setContent(scrollPaneContent);

        //scrollPaneContent.getChildren().add();

        for (int i=0;i<centriVaccinaliMostrati.size();i++){
            Pane panel=new Pane();
            panel.setMinHeight(30);
            SingoloCentroVaccinale currentCentro=centriVaccinaliMostrati.get(i);

            panel.setStyle("-fx-background-color: #FFFFFF");
            Label lblName=new Label(currentCentro.getNome());
            Label lblAddress=new Label(currentCentro.getIndirizzo());
            Label lblType=new Label(currentCentro.getTipologia());
            Button btnGoTo=new Button(">");

            lblName.setLayoutX(6);
            lblName.setMinHeight(30);
            lblName.setFont(new Font("Arial",19));

            lblAddress.setLayoutX(175);
            lblAddress.setMinHeight(30);
            lblAddress.setFont(new Font("Arial",19));

            lblType.setLayoutX(420);
            lblType.setMinHeight(30);
            lblType.setFont(new Font("Arial",19));

            btnGoTo.setLayoutX(520);
            btnGoTo.setFont(new Font("Arial",19));
            btnGoTo.setStyle( "-fx-background-radius: 5em;" + "-fx-min-width: 1px;" + "-fx-background-color: #FFFFFF;" + "-fx-border-radius: 5em;" + "-fx-border-color: #000000;");
            btnGoTo.setId(String.valueOf(i));
            btnGoTo.setOnAction(this);


            panel.getChildren().add(lblName);
            panel.getChildren().add(lblAddress);
            panel.getChildren().add(lblType);
            panel.getChildren().add(btnGoTo);


            scrollPaneContent.getChildren().add(panel);
        }
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        Button source = (Button) actionEvent.getSource();
        int buttonID = Integer.parseInt(source.getId());
        //TODO David: far partire il metodo che carica la ui del centro vaccinale (in base all'id)    }

    public static void registerEventiAvversi()throws Exception {
        String evento1 = box1.getSelectedItem().toString(); //evento1 = Mal di testa
        String evento2 = box2.getSelectedItem().toString(); //evento2 = Febbre
        String evento3 = box3.getSelectedItem().toString(); //evento3 = Dolori muscolari o articolari
        String evento4 = box4.getSelectedItem().toString(); //evento4 = Linfoadenopatia
        String evento5 = box5.getSelectedItem().toString(); //evento5 = Tachicardia
        String evento6 = box6.getSelectedItem().toString(); //evento6 = Crisi ipertensiva
        FileWriter writer = new FileWriter("account.txt", true);
        BufferedWriter out = new BufferedWriter(writer);
        String fileInput =  "Mal di Testa:" + evento1 + ";" + "Febbre:" + evento2 + ";" + "Dolori muscolari o articolari:" + evento3 + ";" + "Linfoadenopatia:" + evento4 + ";" + "Tachicardia:" + evento5 + ";" + "Crisi ipertensiva:" + evento6 + ";";
        out.write(fileInput);
        out.newLine();
        out.flush();
        out.close();
    }

    public static Vector<SingoloCentroVaccinale> getCentriVaccinaliFromFile() {
        Vector<SingoloCentroVaccinale> vector = new Vector<>();

        try {
            FileReader fileReader = new FileReader(CentriVaccinali.PATH_TO_CENTRIVACCINALI);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.countTokens() == 3) {
                    String nome = st.nextToken();
                    String indirizzo = st.nextToken();
                    String tipologia = st.nextToken();

                    vector.add(new SingoloCentroVaccinale(nome, indirizzo, tipologia));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vector;

    }


    //TODO Marsio:creare registrazione e login cittadino
    public void registraCittadino(SingoloCittadino cittadino) {

    }

    public void loggaCittadini() {

    }

    //TODO: Marsio: implementare ricerca centro vaccinale

}


}