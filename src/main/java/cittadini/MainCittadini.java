package cittadini;

import centrivaccinali.SelectionUI;
import centrivaccinali.SingoloCentroVaccinale;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.image.ImageView;
import server.ServerHandler;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Gestisce la UI che permette ai cittadini di consultare i centri vaccinali e i loro relativi eventi avversi registrati
 */
public class MainCittadini {
    /**
     * Il percorso dell'immagine che verrà mostrata qualora non sia possibile connettersi al server
     */
    public static final String ERROR_WITH_DB_IMG_PATH="/cittadini/errorWithDb.png";
    /**
     * Il percorso dell'immagine che verrà mostrata qualora non sia presente nessun centro vaccinale nel database
     */
    public static final String NO_CENTERS_IMG_PATH="/cittadini/noCenters.png";
    /**
     * Il percorso dell'immagine che verrà mostrata qualora non sia presente nessun centro vaccinale corrispondente alla ricerca
     */
    public static final String NO_SEARCH_RESULT_IMG_PATH="/cittadini/noSearchResult.png";
    /**
     * La string che verrà mostrata all'utente qualora non sia stato possibile connettersi al server
     */
    public static final String SERVER_ERROR_TEXT ="Abbiamo riscontrato un errore di connessione con il server. Riprova più tardi";
    /**
     * La stringa che verrà mostrata all'utente qualora non sia presente nessun centro vaccinale all'interno del database
     */
    public static final String NO_CENTERS_TEXT="Non è stato trovato nessun centro vaccinale nel database";
    /**
     * La stringa che verrà mostrata all'utente qualora non sia stato trovato nessun centro vaccinale corrispondente alla ricerca
     */
    public static final String NO_SEARCHED_CENTERS_FOUND_TEXT ="Purtroppo la tua ricerca non ha prodotto nessun risultato";
    /**
     * Il percorso dell'immagine che verrà inserita nel bottone per aprire il pannello di informazioni di ogni centro
     */
    private static final String OPEN_CENTER_INFO_IMG_PATH = "/cittadini/openCenterInfo.png";
    /**
     * Lista contente tutti i centri vaccinali presenti nel database. Popolata dal metodo getCentriVaccinaliFromFile()
     */
    private Vector<SingoloCentroVaccinale> centriVaccinaliList=null;
    /**
     * Percorso dell'immagine che verrà usata come icona del portale cittadini
     */
    public static final String CITIZENS_PORTAL_ICON_PATH ="/cittadini/citizenPortalIcon.png";
    /**
     * ScrollPane contenente tutti gli elementi per rappresentare visivamente i centri vaccinali
     */
    @FXML
    private ScrollPane scrollPane_CentriVaccinali;
    /**
     * Contiene l'ID del centro vaccinale selezionato. Serve per controllare se l'utente vuole chiudere il pannello che mostra le informazioni del centro vaccinale, o se vuole visualizzare le informazioni di un altro centro
     */
    private int selectedCenterID;
    /**
     * variabile usata per sapere se nella UI sià già aperto il pannello contente le informazioni su un centro vaccinale
     */
    private boolean centerSelected=false;

    /**
     * Buffer che permette di inviare dati al sever
     */
    private static PrintWriter out;
    /**
     * Buffer che permette di ricevere dati dal server
     */
    private static BufferedReader in;
    /**
     * Buffer che permette di ricevere dati composti (classi) dal server
     */
    private static ObjectInputStream ois;
    /**
     * Thread per la ricerca. Serve per far terminare la ricerca corrente quando l'utente ne effettua una nuova
     */
    private Thread currentSearchThread=null;
    /**
     * Popup mostrato durante la ricerca. Serve al Thread di ricerca per eliminare dalla UI il popup quando l'operazione è terminata
     */
    private Node loadingPopup=null;
    /**
     * variabile usata per sapere se la UI, contente le informazioni del centro vaccinale, sia già stata caricata
     */
    private boolean centerInfoPaneUILoaded=false;


    /**
     * Costruttore principale della classe MainCittadini
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    public MainCittadini(Stage stage){
        loadMainCittadiniUI(stage);
    }

    /**
     * Carica la UI del portale cittadini. Questa UI Consente di scegliere il centro vaccinale presso cui consultare/inserire i dati. Viene chiamato dalla classe CentriVaccinali nel metodo onCittadiniSelected(ActionEvent event).
     * @param stage Lo stage su cui verrà caricata la nuova FX Scene
     */
    public void loadMainCittadiniUI(Stage stage){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/MainCittadini.fxml");
            loader.setLocation(xmlUrl);
            loader.setController(this);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Portale Cittadini");

            stage.centerOnScreen();

            setSceneButtons(stage);

            InputStream iconStream=getClass().getResourceAsStream(CITIZENS_PORTAL_ICON_PATH);
            Image icon=new Image(iconStream);

            stage.getIcons().set(0,icon);


            scene.lookup("#radio_name").getStyleClass().remove("radio-button");
            scene.lookup("#radio_name").getStyleClass().add("toggle-button");
            scene.lookup("#radio_type").getStyleClass().remove("radio-button");
            scene.lookup("#radio_type").getStyleClass().add("toggle-button");

            AnchorPane mainPane=(AnchorPane) scene.lookup("#mainPane");
            scrollPane_CentriVaccinali= (ScrollPane) scene.lookup("#scrollPane_CentriVaccinali");

            Node loadingPopup=showLoadingAnimation(scene);

            new Thread(()-> {
                try {
                    //Faccio in modo che il popup di caricamento sia visibile almeno per poco tempo
                    Thread.sleep(700);

                    centriVaccinaliList = getCentriVaccinaliFromDb();

                    Platform.runLater(()->{
                        if (centriVaccinaliList == null) {
                            scrollPane_CentriVaccinali.setVisible(false);
                            ImageView img=((ImageView)scene.lookup("#errorImg"));
                            img.setImage(new Image(getClass().getResourceAsStream(ERROR_WITH_DB_IMG_PATH)));
                            img.setVisible(true);
                            Label label= (Label) scene.lookup("#errorLabel");
                            label.setText(SERVER_ERROR_TEXT);
                            label.setVisible(true);
                        }
                        else if (centriVaccinaliList.size() == 0) {
                            scrollPane_CentriVaccinali.setVisible(false);
                            ImageView img=((ImageView)scene.lookup("#errorImg"));
                            img.setImage(new Image(getClass().getResourceAsStream(NO_CENTERS_IMG_PATH)));
                            img.setVisible(true);
                            Label label= (Label) scene.lookup("#errorLabel");
                            label.setText(NO_CENTERS_TEXT);
                            label.setVisible(true);
                        } else {
                            creaVbox(centriVaccinaliList);
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Alert alert=new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setContentText("Abbiamo riscontrato un problema nella creazione del vbox");
                    alert.show();
                }
                Platform.runLater(()->{
                    ((AnchorPane) scrollPane_CentriVaccinali.getScene().getRoot()).getChildren().remove(loadingPopup);
                    mainPane.setEffect(null);
                });
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Controlla se l'utente ha effettuato l'accesso e carica i relativi bottoni.
     * Se l'utente è quindi loggato, inserisce nella UI il pulsante di logout.
     * Se l'utente non è loggato, rende visibili i pulsanti di accesso e di registrazione e rimuove, qualora sia presente, il pulsante di logout
     * @param stage Lo stage da cui verranno ottenuti gli UserData per controllare se l'utente ha effettuato l'accesso
     */
    private void setSceneButtons(Stage stage){
        //Setto la userData dello stage per evitare possibili null pointer
        HashMap<String,String> userData;
        String currentUser=null;
        Scene scene=stage.getScene();

        if (stage.getUserData() != null){
            userData = (HashMap<String,String>) stage.getUserData();
            currentUser = userData.get("currentUser");
        }
        else{
            userData=new HashMap<String,String>();
            stage.setUserData(userData);
        }

        if(currentUser!=null){
            Button btn_logout=new Button();
            btn_logout.setText("Logout");
            btn_logout.setStyle("-fx-border-style: hidden;-fx-alignment:  center; -fx-background-color: #1a73e8; -fx-background-radius: 8; -fx-text-fill: white;");
            btn_logout.setPrefWidth(100);
            btn_logout.setPrefHeight(25);
            scene.lookup("#btn_login").setVisible(false);
            scene.lookup("#btn_register").setVisible(false);
            btn_logout.setLayoutX(810);
            btn_logout.setLayoutY(10);
            btn_logout.getStyleClass().add("buttonSelection");
            btn_logout.setOnAction(event -> {
                HashMap<String,String> newUserData=(HashMap<String,String>) stage.getUserData();
                newUserData.remove("currentUser");
                stage.setUserData(newUserData);
                setSceneButtons(stage);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Logout effettuato");
                alert.setContentText("Logout effettuato correttamente");
                alert.showAndWait();
            });

            btn_logout.idProperty().set("btn_logout");

            ((AnchorPane)scene.lookup("#mainPane")).getChildren().add(btn_logout);
        }
        else{
            Button btnLogout=(Button) scene.lookup("#btn_logout");
            if(btnLogout!=null) {
                ((AnchorPane) scene.lookup("#mainPane")).getChildren().remove(btnLogout);
            }
            scene.lookup("#btn_login").setVisible(true);
            scene.lookup("#btn_register").setVisible(true);
        }
    }


    /**
     * Crea il vbox e i necessari componenti grafici contenenti le informazioni sui centri vaccinali consultabili.
     * @param centriVaccinaliMostrati Lista contenente i centri vaccinale da inserire dentro il vbox.
     */
    private void creaVbox(List<SingoloCentroVaccinale> centriVaccinaliMostrati){
        scrollPane_CentriVaccinali.setVisible(true);

        VBox scrollPaneContent=new VBox();
        scrollPaneContent.setSpacing(16);
        scrollPaneContent.setPrefHeight(409);
        //scrollPaneContent.setAlignment(Pos.CENTER);
        scrollPane_CentriVaccinali.setContent(scrollPaneContent);

        for (int i=0;i<centriVaccinaliMostrati.size();i++){
            HBox hbox=new HBox();
            hbox.setPrefHeight(40);
            hbox.setAlignment(Pos.CENTER_LEFT);
            //usa #2A2A32 per il tema scuro
            hbox.setStyle("-fx-border-color:#9aa0a6; -fx-border-style: hidden; -fx-background-color: white; -fx-background-radius: 12; -fx-padding: 0 4 0 6; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 7, 0, 0, 0);");
            SingoloCentroVaccinale currentCentro=centriVaccinaliMostrati.get(i);
            hbox.setSpacing(20);

            Label lblName=new Label(currentCentro.getNome());
            Label lblAddress=new Label(currentCentro.getIndirizzo());
            Label lblType=new Label(currentCentro.getTipologia());
            ImageView btnGoTo=new ImageView(new Image(getClass().getResourceAsStream(OPEN_CENTER_INFO_IMG_PATH)));

            Tooltip tooltip=new Tooltip(lblName.getText());
            tooltip.setShowDelay(Duration.seconds(0.2));
            lblName.setWrapText(true);
            lblName.setTooltip(tooltip);
            tooltip=new Tooltip(lblAddress.getText());
            tooltip.setShowDelay(Duration.seconds(0.2));
            lblAddress.setWrapText(true);
            lblAddress.setTooltip(tooltip);
            tooltip=new Tooltip(lblType.getText());
            tooltip.setShowDelay(Duration.seconds(0.2));
            lblType.setWrapText(true);
            lblType.setTooltip(tooltip);


            lblName.setPrefWidth(150);
            lblName.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-font-size: 19;");


            lblAddress.setPrefWidth(465);
            lblAddress.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-font-size: 19");


            lblType.setPrefWidth(135);
            lblType.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-font-size: 19");


            btnGoTo.setCursor(Cursor.HAND);
            //usa #292E32 per il tema scuro
            btnGoTo.setFitWidth(36);
            btnGoTo.setFitHeight(36);
            btnGoTo.getStyleClass().add("buttonSelection");
            btnGoTo.setId(String.valueOf(i+1));
            btnGoTo.setOnMouseClicked(this::startOpenInfoPaneAnimation);

            HBox.setHgrow(lblAddress, Priority.ALWAYS);
            hbox.getChildren().add(lblName);
            hbox.getChildren().add(lblAddress);
            hbox.getChildren().add(lblType);
            hbox.getChildren().add(btnGoTo);

            scrollPaneContent.getChildren().add(hbox);
        }

        scrollPane_CentriVaccinali.setDisable(false);
    }

    /**
     * Crea un popup che mostra l'animazione di caricamento.
     * @param scene La scena in cui verrà inserito il popup
     * @return Il popup creato
     * @throws IOException Se non è possibile caricare il file FXML
     */
    private Node showLoadingAnimation(Scene scene) throws IOException{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/LoadingPopup.fxml"));
        Node loadingPopup=loader.load();

        ((AnchorPane)scene.getRoot()).getChildren().add(loadingPopup);

        //metto il popup al centro della scena
        loadingPopup.setLayoutX(scene.getWidth()/2-loadingPopup.getBoundsInLocal().getWidth()/2);
        loadingPopup.setLayoutY(scene.getHeight()/2-loadingPopup.getBoundsInLocal().getHeight()/2);

        return loadingPopup;
    }

    /**
     * Apre la UI del centro vaccinale sul quale il cittadino ha cliccato.
     * @param actionEvent L'evento che richiama il metodo. Necessario a ottenere il bottone sorgente dell'evento dal quale è possibile ottenere l'id del centro selezionato.
     */
    public void startOpenInfoPaneAnimation(MouseEvent actionEvent) {
        ImageView source = (ImageView) actionEvent.getSource();
        ScrollPane centerListPane = (ScrollPane) source.getScene().lookup("#scrollPane_CentriVaccinali");
        Pane centerInfoPane = (Pane) source.getScene().lookup("#pane_center_information");
        int currentCenterID = Integer.parseInt(source.getId());

        ScrollPane scrollPane=(ScrollPane) source.getScene().lookup("#scrollPane_CentriVaccinali");
        VBox vbox=(VBox)scrollPane.getContent();

        HashMap<String,String> userData=(HashMap<String,String>)source.getScene().getWindow().getUserData();

        if(!centerSelected) {
            selectedCenterID = currentCenterID;
            centerSelected=true;
            userData.put("currentCenter",String.valueOf(selectedCenterID));

            Timeline paneTransition = new Timeline(
                    new KeyFrame(Duration.millis(350), new KeyValue(centerListPane.prefWidthProperty(), centerListPane.getPrefWidth() / 3, Interpolator.EASE_BOTH)),
                    new KeyFrame(Duration.millis(350), new KeyValue(centerInfoPane.prefWidthProperty(), centerListPane.getPrefWidth() - (centerListPane.getPrefWidth() / 3), Interpolator.EASE_BOTH)),
                    new KeyFrame(Duration.millis(350), new KeyValue(centerInfoPane.translateXProperty(), -(centerListPane.getPrefWidth() - (centerListPane.getPrefWidth() / 3)), Interpolator.EASE_BOTH)));

            paneTransition.play();

            paneTransition.setOnFinished((eventHandler)->{
                Timeline buttonRotation=new Timeline(
                new KeyFrame(Duration.millis(400), new KeyValue(source.rotateProperty(), 225, Interpolator.EASE_BOTH)));

                buttonRotation.play();
            });

            for(int i=0;i<vbox.getChildren().size();i++){
                HBox element=(HBox) vbox.getChildren().get(i);

                Timeline elementTransition = new Timeline(
                        new KeyFrame(Duration.millis(350), new KeyValue(((Label)element.getChildren().get(0)).prefWidthProperty(), 140, Interpolator.EASE_BOTH)),
                        new KeyFrame(Duration.millis(350), new KeyValue(((Label)element.getChildren().get(1)).prefWidthProperty(), 0, Interpolator.EASE_BOTH)),
                        new KeyFrame(Duration.millis(350), new KeyValue(((Label)element.getChildren().get(2)).prefWidthProperty(), 0, Interpolator.EASE_BOTH)));

                elementTransition.play();

            }

            loadVisualizzatoreCentroVaccinale(centerInfoPane, currentCenterID);

        }
        else if(currentCenterID==selectedCenterID){
            Timeline paneTransition = new Timeline(
                    new KeyFrame(Duration.millis(350), new KeyValue(centerListPane.prefWidthProperty(), centerListPane.getPrefWidth() * 3)),
                    new KeyFrame(Duration.millis(350), new KeyValue(centerInfoPane.prefWidthProperty(), 0)),
                    new KeyFrame(Duration.millis(350), new KeyValue(centerInfoPane.translateXProperty(), centerListPane.getPrefWidth()*2)));
            centerSelected=false;

            paneTransition.play();

            paneTransition.setOnFinished((eventHandler)->{
                Timeline buttonRotation=new Timeline(
                        new KeyFrame(Duration.millis(400), new KeyValue(source.rotateProperty(), 0, Interpolator.EASE_BOTH)));

                buttonRotation.play();
            });

            for(int i=0;i<vbox.getChildren().size();i++){
                HBox element=(HBox) vbox.getChildren().get(i);
                ((Label)element.getChildren().get(0)).setPrefWidth(150);
                ((Label)element.getChildren().get(1)).setPrefWidth(465);
                ((Label)element.getChildren().get(2)).setPrefWidth(133);
            }

            userData.remove("currentCenter");

        }
        else{
            selectedCenterID = currentCenterID;
            userData.put("currentCenter",String.valueOf(selectedCenterID));

            for(int i=0;i<vbox.getChildren().size();i++){
                HBox element=(HBox) vbox.getChildren().get(i);
                ImageView imgButton=(ImageView)element.getChildren().get(3);

                if(selectedCenterID==Integer.parseInt(imgButton.getId())) {
                    Timeline buttonRotation=new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(imgButton.rotateProperty(), 225, Interpolator.EASE_BOTH)));

                    buttonRotation.play();
                }
                else{
                    Timeline buttonRotation=new Timeline(new KeyFrame(Duration.millis(400), new KeyValue(imgButton.rotateProperty(), 0, Interpolator.EASE_BOTH)));

                    buttonRotation.play();
                }
            }

            loadVisualizzatoreCentroVaccinale(centerInfoPane, currentCenterID);
        }
    }

    /**
     * Crea la UI che mostra i dati relativi al centro vaccinale selezionato. Viene richiamato quando l'utente seleziona un centro vaccinale.
     * @param centerInfoPane Il panel che conterrà la UI con le informazioni relative al centro selezionato
     * @param selectedCenterID L'id del centro vaccinale selezionato. Utilizzato per prendere i relativi dati dal database
     */
    public void loadVisualizzatoreCentroVaccinale(Pane centerInfoPane, int selectedCenterID){
        try {
            //TODO ottimizzare sta roba mettendo le due scene come attributi (e skippando quindi ogni volta il caricamento)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/VisualizzazioneCentroVaccinalePage1.fxml"));
            fxmlLoader.setController(this);
            Parent centerPageOne = fxmlLoader.load();
            fxmlLoader=new FXMLLoader(getClass().getResource("/fxml/VisualizzazioneCentroVaccinalePage2.fxml"));
            fxmlLoader.setController(this);
            Parent centerPageTwo = fxmlLoader.load();


            if(centerInfoPaneUILoaded){
                centerInfoPane.getChildren().remove(0);
            }
            else {
                centerInfoPaneUILoaded = true;
            }

            Pagination pagination=new Pagination(2);
            pagination.getStyleClass().add("pagination");

            pagination.setPageFactory((pageIndex)->{
                if(pageIndex==0){
                    return centerPageOne;
                }
                else{
                    return centerPageTwo;
                }
            });
            centerInfoPane.getChildren().add(pagination);

            loadCenterInfo(selectedCenterID,centerInfoPane,(AnchorPane) centerPageOne,(AnchorPane) centerPageTwo);

        }
        catch(IOException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Errore nel caricamento dello info del centro");
            alert.showAndWait();
        }
    }

    /**
     * Carica le informazioni principali del centri vaccinale selezionato.
     * @param idCentro L'ID del centro vaccinale
     * @param centerInfoPane Il pannello in cui verranno inseriti a video i dati presi dal database
     */
    public void loadCenterInfo(int idCentro, Pane centerInfoPane, AnchorPane centerPageOne, AnchorPane centerPageTwo){
        double indicatorSize=115;

        ProgressIndicator loadingIndicator=new ProgressIndicator();
        centerInfoPane.getChildren().add(loadingIndicator);
        ScrollPane scrollPane=((ScrollPane)(centerInfoPane.getParent()).lookup("#scrollPane_CentriVaccinali"));
        loadingIndicator.setMinHeight(indicatorSize);
        loadingIndicator.setMinWidth(indicatorSize);
        if(scrollPane.getPrefWidth()<centerInfoPane.getPrefWidth()){
            loadingIndicator.setLayoutX((centerInfoPane.getPrefWidth() - loadingIndicator.getMinWidth()) / 2);
        }
        else {
            loadingIndicator.setLayoutX((scrollPane.getPrefWidth() - (scrollPane.getPrefWidth() / 3) - loadingIndicator.getMinWidth()) / 2);
        }
        loadingIndicator.setLayoutY((centerInfoPane.getPrefHeight()-loadingIndicator.getMinHeight())/2);
        loadingIndicator.setStyle("-fx-progress-color: blue");
        centerInfoPane.getChildren().get(0).setOpacity(0.6);

        new Thread(()-> {
            try {
                Thread.sleep(550);

                Vector<EventiAvversi> eventLines = leggiEventiAvversi(idCentro);
                int[] eventsCount = new int[6];
                int[] singleEvents = new int[6];
                Vector<String> otherEventsText = new Vector<>();

                for (EventiAvversi currentEvent : eventLines) {
                    //sommo tra di loro i valori di ogni sintomo per poi poterne far la media
                    singleEvents[0] += currentEvent.getMaleTesta();
                    singleEvents[1] += currentEvent.getFebbre();
                    singleEvents[2] += currentEvent.getDoloriMuscolari();
                    singleEvents[3] += currentEvent.getLinfoadenopatia();
                    singleEvents[4] += currentEvent.getTachicardia();
                    singleEvents[5] += currentEvent.getCrisiIpertensiva();

                    if (currentEvent.getOtherSymptoms() != null && !(currentEvent.getOtherSymptoms().equals(""))) {
                        otherEventsText.add(currentEvent.getOtherSymptoms());
                    }

                    //Conto il numero di occorrenze per ogni sintomo
                    if(currentEvent.getMaleTesta()>0){
                        eventsCount[0]++;
                    }
                    if(currentEvent.getFebbre()>0){
                        eventsCount[1]++;
                    }
                    if(currentEvent.getDoloriMuscolari()>0){
                        eventsCount[2]++;
                    }
                    if(currentEvent.getLinfoadenopatia()>0){
                        eventsCount[3]++;
                    }
                    if(currentEvent.getTachicardia()>0){
                        eventsCount[4]++;
                    }
                    if(currentEvent.getCrisiIpertensiva()>0){
                        eventsCount[5]++;
                    }

                }

                for (int i = 0; i < singleEvents.length; i++) {
                    if (singleEvents[i] != 0) {
                        singleEvents[i] /= eventLines.size();
                    }
                }


                Label lbl_headacheEffect = (Label) centerPageOne.lookup("#lbl_effect1");
                Label lbl_feverEffect = (Label) centerPageOne.lookup("#lbl_effect2");
                Label lbl_hurtEffect = (Label) centerPageOne.lookup("#lbl_effect3");
                Label lbl_linfEffect = (Label) centerPageOne.lookup("#lbl_effect4");
                Label lbl_tacEffect = (Label) centerPageOne.lookup("#lbl_effect5");
                Label lbl_crsEffect = (Label) centerPageOne.lookup("#lbl_effect6");

                int total=0;
                for(int i=0;i<singleEvents.length;i++){
                    total+=singleEvents[i];
                }

                PieChart pieChart = (PieChart) centerPageTwo.lookup("#pieChart_symptoms");
                Label lbl_totalEvents = (Label) centerPageTwo.lookup("#lbl_totalEvents");

                if(total==0){
                    centerPageTwo.lookup("#img_noChartData").setVisible(true);
                    centerPageTwo.lookup("#lbl_noChartData").setVisible(true);

                    pieChart.setVisible(false);
                    lbl_totalEvents.setVisible(false);
                }
                else {
                    centerPageTwo.lookup("#img_noChartData").setVisible(false);
                    centerPageTwo.lookup("#lbl_noChartData").setVisible(false);

                    //Inserisco il numero di occorrenze di ogni sintomo
                    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                    pieChartData.add(new PieChart.Data("Mal di testa ["+eventsCount[0]+"]", eventsCount[0]));
                    pieChartData.add(new PieChart.Data("Febbre ["+eventsCount[1]+"]", eventsCount[1]));
                    pieChartData.add(new PieChart.Data("Dolori muscolari ["+eventsCount[2]+"]", eventsCount[2]));
                    pieChartData.add(new PieChart.Data("Linfoadenopatia ["+eventsCount[3]+"]", eventsCount[3]));
                    pieChartData.add(new PieChart.Data("Tachicardia ["+eventsCount[4]+"]", eventsCount[4]));
                    pieChartData.add(new PieChart.Data("Crisi ipertensiva ["+eventsCount[5]+"]", eventsCount[5]));
                    pieChart.setData(pieChartData);

                    pieChart.getStyleClass().add("pieChart");

                    lbl_totalEvents.setText("Numero totale di eventi registrati: " + eventLines.size());
                }

                Platform.runLater(() -> {
                    lbl_headacheEffect.setText(String.valueOf(singleEvents[0]));//evento1 = Mal di testa
                    lbl_feverEffect.setText(String.valueOf(singleEvents[1])); //evento2 = Febbre
                    lbl_hurtEffect.setText(String.valueOf(singleEvents[2])); //evento3 = Dolori muscolari o articolari
                    lbl_linfEffect.setText(String.valueOf(singleEvents[3])); //evento4 = Linfoadenopatia
                    lbl_tacEffect.setText(String.valueOf(singleEvents[4])); //evento5 = Tachicardia
                    lbl_crsEffect.setText(String.valueOf(singleEvents[5]));//evento6 = Crisi ipertensiva

                    ScrollPane scrollPane_otherEvents = (ScrollPane) centerInfoPane.lookup("#scrollPane_otherEvents");
                    VBox vbox = new VBox();
                    vbox.setStyle("-fx-padding: 0 6");
                    vbox.setSpacing(3);
                    scrollPane_otherEvents.setContent(vbox);

                    for (int i = 0; i < otherEventsText.size(); i++) {
                        Pane vboxContent = new Pane();

                        Label lbl_otherEventText = new Label(otherEventsText.get(i));
                        lbl_otherEventText.setFont(Font.font("Franklin Gothic Medium", 18));
                        lbl_otherEventText.setPrefWidth(490);
                        lbl_otherEventText.setPrefHeight(30);

                        Separator separator = new Separator();
                        separator.setOrientation(Orientation.HORIZONTAL);
                        separator.setPrefWidth(480);
                        separator.setCenterShape(true);

                        vboxContent.getChildren().add(lbl_otherEventText);

                        vbox.getChildren().add(vboxContent);
                        vbox.getChildren().add(separator);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setContentText("Abbiamo riscontrato un errore nel caricamento dei dati relativi al centro");
                alert.show();
            }

            Platform.runLater(() -> {
                centerInfoPane.getChildren().remove(loadingIndicator);
                centerInfoPane.getChildren().get(0).setOpacity(1);
            });
        }).start();

    }

    /**
     * Legge il database, relativo al centro vaccinale selezionato, contente gli eventi avversi e gli utenti vaccinati
     * @param currentCentreID L'ID  del centro vaccinale selezionato
     * @return Una lista di stringhe contente gli eventi avversi relativi al centro vaccinale selezionato
     */
    public Vector<EventiAvversi> leggiEventiAvversi(int currentCentreID) {
        try {
            out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(SelectionUI.socket_container.getOutputStream())),true);
            out.println(currentCentreID);
            out.println(ServerHandler.GET_EVENTIAVVERSI_OP_CODE);

            ois = new ObjectInputStream(SelectionUI.socket_container.getInputStream());
            return (Vector<EventiAvversi>) ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Legge i centri vaccinali presenti nel database e li restituisce sotto firma di lista.
     * @return Il vettore contente i centri vaccinali presenti nel database.
     */
    public static Vector<SingoloCentroVaccinale> getCentriVaccinaliFromDb() throws IOException, ClassNotFoundException {
        becomeClient();
        System.out.println("[CLIENT] Uscito dalla become client");
        try {
            ois = new ObjectInputStream(SelectionUI.socket_container.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Vector <SingoloCentroVaccinale>v =(Vector<SingoloCentroVaccinale>) ois.readObject();
        return v;
    }

    /**
     * Evento richiamato quando l'utente preme il tasto di ricerca. Richiama il metodo search(Scene currentScene) per effettuare la ricerca dei centri vaccinali
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena attuale da cui prendere i parametri di ricerca.
     */
    public void findCenter(ActionEvent event) throws IOException, ClassNotFoundException {
        Scene currentScene=((Button)event.getSource()).getScene();
        search(currentScene);
    }

    /**
     * Evento richiamato quando l'utente digita un carattere nella barra di ricerca. Richiama il metodo search(Scene currentScene) per effettua la ricerca dei centri vaccinali
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena attuale in cui inserire i centri trovati
     */
    public void keyTyped(KeyEvent event) throws IOException, ClassNotFoundException {
        Scene currentScene=((TextField)event.getSource()).getScene();
        search(currentScene);
    }

    /**
     * Effettua la ricerca di un centro vaccinale su un thread separato. Richiama poi il metodo per aggiornare la UI mostrando solo i centri vaccinali che corrispondono ai parametri della ricerca.
     * @param currentScene La scena in cui verranno inseriti i centri trovati
     */
    public void search(Scene currentScene){
        try {
            AnchorPane mainPane = (AnchorPane) currentScene.lookup("#mainPane");
            ScrollPane scrollPane = (ScrollPane) currentScene.lookup("#scrollPane_CentriVaccinali");
            ImageView errorImg=(ImageView)currentScene.lookup("#errorImg");
            Label errorLabel=(Label)currentScene.lookup("#errorLabel");
            Pane centerInfoPane=(Pane)currentScene.lookup("#pane_center_information");

            scrollPane.setContent(null);
            errorImg.setVisible(false);
            errorLabel.setVisible(false);

            //Se c'è un centro selezionato, chiudo il pannello di informazioni del centro per poter ricostruire il vbox senza bug
            if (centerSelected) {
                ((HashMap<String, String>) currentScene.getWindow().getUserData()).remove("currentCenter");
                centerInfoPane.prefWidth(0);
                centerInfoPane.translateXProperty().set(scrollPane.getWidth()*2);
                scrollPane.setPrefWidth(scrollPane.getPrefWidth() * 3);
                centerSelected = false;
            }

            if(loadingPopup==null) {
                loadingPopup = showLoadingAnimation(currentScene);
            }
            if(currentSearchThread!=null){
                currentSearchThread.interrupt();
            }

            currentSearchThread=new Thread(() -> {
                try {
                    Thread.sleep(400);
                    try {
                        //Metto in sleep il thread per fare in modo che il popup di caricamento si veda per un minimo di tempo

                        //controllo che la lista non sia già popolata (per evitare inutili chiamate al db)
                        if (centriVaccinaliList == null) {
                            centriVaccinaliList = getCentriVaccinaliFromDb();
                        }

                        Vector<SingoloCentroVaccinale> vector_search = new Vector<>();

                        String search = ((TextField) currentScene.lookup("#txt_searchCenter")).getText().toLowerCase();
                        boolean searchByName = ((RadioButton) currentScene.lookup("#radio_name")).isSelected();
                        boolean searchByTypeAndAddress = ((RadioButton) currentScene.lookup("#radio_type")).isSelected();

                        for (SingoloCentroVaccinale tempCentre : centriVaccinaliList) {
                            String nome = tempCentre.getNome();
                            String indirizzo = tempCentre.getIndirizzo();
                            String tipologia = tempCentre.getTipologia();

                            if (searchByName) {
                                if ((nome.toLowerCase()).contains(search)) {
                                    vector_search.add(new SingoloCentroVaccinale(nome, indirizzo, tipologia));
                                }
                            } else if (searchByTypeAndAddress) {
                                if ((indirizzo.toLowerCase()).contains(search) || (tipologia.toLowerCase()).contains(search)) {
                                    vector_search.add(new SingoloCentroVaccinale(nome, indirizzo, tipologia));
                                }
                            }
                        }

                        Platform.runLater(() -> {
                            if (vector_search.size() == 0) {
                                scrollPane.setVisible(false);
                                errorImg.setImage(new Image(getClass().getResourceAsStream(NO_SEARCH_RESULT_IMG_PATH)));
                                errorImg.setVisible(true);
                                errorLabel.setText(NO_SEARCHED_CENTERS_FOUND_TEXT);
                                errorLabel.setVisible(true);
                            } else {
                                errorImg.setVisible(false);
                                errorLabel.setVisible(false);
                                creaVbox(vector_search);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorImg.setImage(new Image(getClass().getResourceAsStream(ERROR_WITH_DB_IMG_PATH)));
                        errorImg.setVisible(true);
                        errorLabel.setText(SERVER_ERROR_TEXT);
                        errorLabel.setVisible(true);
                    }

                    Platform.runLater(() -> {
                        mainPane.setEffect(null);
                        ((AnchorPane) currentScene.getRoot()).getChildren().remove(loadingPopup);
                        loadingPopup = null;
                    });

                    currentSearchThread = null;
                }
                catch (InterruptedException e) {
                    System.out.println("Nuova ricerca effettuata");
                }
            });
            currentSearchThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo per tornare indietro alla schermata di selezione portale
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage su cui inserire la nuova scena per mantenere la stessa finestra ma con una UI diversa
     */
    public void goBackFromMainCittadini(MouseEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/SelectionUI.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            Scene scene=new Scene(root);

            Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();

            currentStage.setScene(scene);

            currentStage.setTitle("Seleziona il Portale");

            currentStage.centerOnScreen();

            InputStream icon = getClass().getResourceAsStream("/common/fiorellino.png");
            Image image = new Image(icon);

            currentStage.getIcons().set(0,image);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Metodo per caricare la UI di login utente. Viene richiamato quando l'utente preme sul tasto "Login"
     * @param event Il bottone cliccato dall'utente
     */
    public void onLoginClick(ActionEvent event){
        Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();
        loadLoginUI(stage);
    }

    /**
     * Metodo per caricare la UI che permette ad un utente di effettuare il login, o in alternativa, di caricare la UI necessaria alla registrazione
     * @param stage Lo stage da cui inserire e prendere il nome centro vaccinale e il codice fiscale del cittadino loggato
     */
    public void loadLoginUI(Stage stage){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/fxml/LoginUtente.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea la UI che permette a un utente di inserire eventi avversi.
     * @param actionEvent L'evento che richiama il metodo. Necessario per ottenere lo stage in cui inserire la nuova scena
     */
    public void loadRegistraEventiAvversiUI(ActionEvent actionEvent){
        Stage stage=(Stage)((Button)actionEvent.getSource()).getScene().getWindow();

        HashMap<String,String> userData=(HashMap<String,String>)stage.getUserData();
        String user=userData.get("currentUser");
        String center=userData.get("currentCenter");

        //se l'utente non è loggato, non può inserire eventi avversi
        if(user==null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Per aggiungere eventi avversi devi aver effettuato l'accesso");
            alert.show();
            return;
        }
        int eventsNum= getUserEventsNum(user,center);
        if(eventsNum==-1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Non sei stato vaccinato presso questo centro");
            alert.show();
            return;
        }
        if(eventsNum==0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Hai già inserito tutti gli eventi avversi");
            alert.show();
            return;
        }
        userData.put("eventsNum",String.valueOf(eventsNum));
        new RegistraEventiAvversi(stage);
    }

    /**
     * Controlla se l'utente è stato vaccinato presso il centro vaccinale selezionato e, in caso positivo, restituisce il numero di eventi avversi ancora da inserire.
     * @param user stringa rappresentante l'utente
     * @param currentCenter stringa rappresentante il centro dove ci si è vaccinati
     * @return
     */
    private int getUserEventsNum(String user, String currentCenter){
        try {
            out.println(user + ";" + currentCenter);
            out.println(ServerHandler.USER_ADD_EVENTS_PERMISSION_CHECK_OP_CODE);

            int eventsNum=Integer.parseInt(in.readLine());
            return eventsNum;
        }
        catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Carica la UI necessaria a effettuare la registrazione di un utente.
     * @param event L'evento che richiama il metodo. Necessario per chiudere la UI di login
     */
    public void loadRegisterCitizenUI(ActionEvent event){
        try {
            Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/fxml/RegistraUtente.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            currentStage.setScene(scene);

            currentStage.centerOnScreen();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Metodo che invia al server il relativo codice operazione per ottenere una lista di tutti i centri vaccinali presenti nel database
     */
    public static void becomeClient(){
        try {
            System.out.println("[CLIENT MAIN CITTADINI] - Sono già connesso, prendo gli stream ");
            Socket s = SelectionUI.socket_container;
            out = SelectionUI.out_container;
            in = SelectionUI.in_container;
            out.println("void");
            System.out.println("spedito void");
            out.println(ServerHandler.GET_VAX_CENTERS_OP_CODE);
            System.out.println("spedito codice");
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
}
