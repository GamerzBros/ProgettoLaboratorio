package centrivaccinali;

import cittadini.MainCittadini;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class SelectionUI extends Application {
    /**
     * Crea la UI principale che permette di scegliere il portale. Metodo che viene eseguito subito dopo la creazione della classe.
     * @param stage Lo stage che conterrà la scena. Uno stage è una finestra, mentre una scena è tutto ciò contenuto in uno stage.
     * @throws Exception L'eccezione provocata dallo start del programma
     */
    Socket s;
    PrintWriter out;
    BufferedReader in;
    public static Socket socket_container;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/fxml/SelectionUI.fxml");

        loader.setLocation(xmlUrl);

        Parent root = loader.load();

        Scene scene=new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Seleziona il Portale");


        InputStream icon = getClass().getResourceAsStream("/common/fiorellino.png");
        Image image = new Image(icon);

        stage.getIcons().add(image);
        stage.show();
        becomeClient();
    }

    @Override
    public void init() throws Exception { //metodo che viene in automatico startato dopo la creazione della ui
        super.init();
       // becomeClient();
    }

    /**
     * Crea la UI del portale Cittadini. Viene richiamato una volta che viene selezionato il portale Cittadini dalla UI principale.
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage da chiudere.
     */
    public void onCittadiniSelected(ActionEvent event){
        try {
            Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();

            new MainCittadini(currentStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea la UI del portale Centri Vaccinali. Viene richiamato una volta che viene selezionato il portale Centri Vaccinale dalla UI principale.
     */
    public void onCentriVaccinaliSelected(ActionEvent event){
        Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();
        new PortaleOperatori(stage);
    }

    public void onChoiceButtonHover(MouseEvent event){
        Button btn=(Button)event.getSource();
        ImageView imgView = (ImageView) btn.getScene().lookup("#imgBg");

        if(btn.getText().equals("Portale Cittadini")) {
            imgView.setImage(new Image(getClass().getResourceAsStream("/centrivaccinali/crowd.png")));
            btn.getScene().lookup("#lbl_main").setVisible(false);
            btn.getScene().lookup("#lbl_citizen").setVisible(true);
        }
        else if(btn.getText().equals("Portale Operatori")){
            imgView.setImage(new Image(getClass().getResourceAsStream("/centrivaccinali/medici.png")));
            btn.getScene().lookup("#lbl_main").setVisible(false);
            btn.getScene().lookup("#lbl_operator").setVisible(true);
        }


    }

    public void onChoiceButtonExit(MouseEvent event){
        ImageView imgView=(ImageView)((Button)event.getSource()).getScene().lookup("#imgBg");
        imgView.setImage(new Image(getClass().getResourceAsStream("/centrivaccinali/varese.png")));

        Button btnSource=(Button)event.getSource();
        btnSource.getScene().lookup("#lbl_main").setVisible(true);
        btnSource.getScene().lookup("#lbl_citizen").setVisible(false);
        btnSource.getScene().lookup("#lbl_operator").setVisible(false);
    }

    /**
     * Termina ogni processo aperto dal programma. Viene eseguito automaticamente qualora ogni finestra del programma venga chiusa.
     * @throws Exception L'eccezione provocata dallo stop del programma.
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    }
    public void becomeClient(){
        try {
            System.out.println("[CLIENT] - Tentativo di connessione ");
            s = new Socket(InetAddress.getLocalHost(),9870);
            System.out.println("[CLIENT] - Sono connesso ");
            socket_container=s;
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore connessione");
            alert.setTitle("Server non startato");
            alert.setContentText("Il server non è stato startato, riavviare l'app accendendo prima il server");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
