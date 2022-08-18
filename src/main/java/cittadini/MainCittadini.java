package cittadini;

import centrivaccinali.SelectionUI;
import centrivaccinali.SingoloCentroVaccinale;
import javafx.animation.Interpolator;
import server.ServerHandler;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
//TODO rivedere quando usare i throws e i try/catch all'interno del progetto
public class MainCittadini implements EventHandler<ActionEvent> {
    /**
     * Percorso per il file contente le informazioni dei centri vaccinali registrati
     */
    public static final String PATH_TO_CENTRIVACCINALI_DATI = "data/CentriVaccinali.dati.txt";
    /**
     * Parte iniziale percorso per il file del centro vaccinale selezionato
     */
    public static final String PRE_PATH_TO_EVENTI_AVVERSI="data/Vaccinati_";
    /**
     * Parte finale del percorso del centro vaccinale selezionato
     */
    public static final String AFTER_PATH_TO_EVENTI_AVVERSI=".dati.txt";
    /**
     * Percorso per il file contenente i dati dei cittadini registrati
     */
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI = "data/Cittadini_Registrati.dati.txt";
    /**
     * Tipo di linea del file contente le informazioni relative al vaccinato
     */
    public static final String LINE_TYPE_PERSON ="V";
    /**
     * Tipo di linea del file contente le informazioni relative agli eventi avversi
     */
    public static final String LINE_TYPE_EVENT ="E";
    /**
     * Lista contente tutti i centri vaccinali presenti nel file. Popolata dal metodo getCentriVaccinaliFromFile()
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
     * Codice fiscale dell'utente attualmente loggato
     */
    private String currentUser=null;
    /**
     * Centro vaccinale attualmente selezionato
     */
    private String currentCenter=null;

    /**
     * Contiene l'ID del centro vaccinale selezionato. Serve per controllare se l'utente vuole chiudere il pannello che mostra le informazioni del centro vaccinale, o se vuole visualizzare le informazioni di un altro centro
     */
    private int selectedCenterID;
    /**
     * Usata per sapere se nella UI sià già aperto il pannello contente le informazioni su un centro vaccinale
     */
    private boolean centerSelected=false;

    /**
     * Buffer che permette di inviare dati primitivi al sever
     */
    private static PrintWriter out;
    /**
     * Buffer che permette di ricevere dati primitivi dal server
     */
    private static BufferedReader in;
    /**
     * Buffer che permette di ricevere dati composti (classi) dal server
     */
    private static ObjectInputStream ois;

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
        //TODO aggiungere feedback visivo del caricamento
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/MainCittadini.fxml");
            loader.setLocation(xmlUrl);
            loader.setController(this);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Portale Cittadini");
            stage.setY(50);
            stage.setX(175);

            //Setto la userData dello stage per evitare possibili null pointer
            HashMap<String,String> userData;

            if (stage.getUserData() != null){
                userData = (HashMap<String,String>) stage.getUserData();
                currentUser = userData.get("currentUser");
                currentCenter = userData.get("currentCenter");
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
                btn_logout.setOnAction(event -> {
                    currentUser=null;
                    HashMap<String,String> newUserData=(HashMap<String,String>) stage.getUserData();
                    newUserData.remove("currentUser");
                    stage.setUserData(newUserData);
                    loadMainCittadiniUI(stage);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Logout effettuato");
                    alert.setContentText("Logout effettuato correttamente");
                    alert.showAndWait();
                });

                ((AnchorPane)scene.lookup("#mainPane")).getChildren().add(btn_logout);
            }

            stage.show();

            InputStream iconStream=getClass().getResourceAsStream(CITIZENS_PORTAL_ICON_PATH);
            Image icon=new Image(iconStream);

            stage.getIcons().set(0,icon);

            scrollPane_CentriVaccinali = (ScrollPane) scene.lookup("#scrollPane_CentriVaccinali");
            scrollPane_CentriVaccinali.lookup(".viewport").setStyle("-fx-background-color: #1a73e8;");

            scene.lookup("#radio_name").getStyleClass().remove("radio-button");
            scene.lookup("#radio_name").getStyleClass().add("toggle-button");
            scene.lookup("#radio_type").getStyleClass().remove("radio-button");
            scene.lookup("#radio_type").getStyleClass().add("toggle-button");

            centriVaccinaliList = getCentriVaccinaliFromDb();

            if(centriVaccinaliList==null||centriVaccinaliList.size()==0){
                scrollPane_CentriVaccinali.setVisible(false);
                scene.lookup("#noCentersImg").setVisible(true);
                scene.lookup("#noCentersLabel").setVisible(true);
                System.out.println("Nessun centro vaccinale presente nel database");
                /*ImageView img=new ImageView(new Image(getClass().getResourceAsStream(NO_CENTERS_IMG_PATH)));
                img.setX(25);
                img.setY(83);
                img.prefHeight(400);
                img.prefWidth(650);
                img.toFront();
                ((AnchorPane)scene.lookup("#mainPane")).getChildren().add(img);*/
                return;
            }

            creaVbox(centriVaccinaliList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Crea il vbox e i necessari componenti grafici contenenti le informazioni sui centri vaccinali consultabili.
     * @param centriVaccinaliMostrati Lista contenente i centri vaccinale da inserire dentro il vbox (quindi dentro la UI).
     */
    private void creaVbox(List<SingoloCentroVaccinale> centriVaccinaliMostrati){
        VBox scrollPaneContent=new VBox();
        scrollPaneContent.setSpacing(15);
        scrollPaneContent.setPrefHeight(409);
        //scrollPaneContent.setAlignment(Pos.CENTER);
        scrollPane_CentriVaccinali.setContent(scrollPaneContent);

        for (int i=0;i<centriVaccinaliMostrati.size();i++){
            HBox hbox=new HBox();
            hbox.setPrefHeight(40);
            hbox.setAlignment(Pos.CENTER_LEFT);
            //usa #2A2A32 per il tema scuro
            hbox.setStyle("-fx-border-color:#9aa0a6; -fx-border-style: hidden; -fx-background-color: white; -fx-background-radius: 12; -fx-padding: 0 5 0 5");
            SingoloCentroVaccinale currentCentro=centriVaccinaliMostrati.get(i);
            hbox.setSpacing(20);

            Label lblName=new Label(currentCentro.getNome());
            Label lblAddress=new Label(currentCentro.getIndirizzo());
            Label lblType=new Label(currentCentro.getTipologia());
            Button btnGoTo=new Button(">");


            lblName.setPrefWidth(150);
            lblName.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-font-size: 19");


            lblAddress.setPrefWidth(465);
            lblAddress.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-font-size: 19");


            lblType.setPrefWidth(135);
            lblType.setStyle("-fx-text-fill: black; -fx-font-family: Arial; -fx-font-size: 19");


            btnGoTo.setFont(new Font("Arial",16));
            //usa #292E32 per il tema scuro
            btnGoTo.setStyle( "-fx-background-radius: 5em; -fx-min-width: 1px; -fx-background-color: #FFFFFF; -fx-border-radius: 5em; -fx-border-color: #000000;");
            btnGoTo.setId(String.valueOf(i));
            btnGoTo.setOnAction(this);

            HBox.setHgrow(lblAddress, Priority.ALWAYS);
            hbox.getChildren().add(lblName);
            hbox.getChildren().add(lblAddress);
            hbox.getChildren().add(lblType);
            hbox.getChildren().add(btnGoTo);

            scrollPaneContent.getChildren().add(hbox);
        }
    }

    /**
     * Apre la UI del centro vaccinale sul quale il cittadino ha cliccato.
     * @param actionEvent L'evento che richiama il metodo. Necessario a ottenere il bottone sorgente dell'evento dal quale è possibile ottenere l'id del centro selezionato.
     */
    @Override
    public void handle(ActionEvent actionEvent) {
        Button source = (Button) actionEvent.getSource();
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

            for(int i=0;i<vbox.getChildren().size();i++){
                HBox element=(HBox) vbox.getChildren().get(i);
                //((Label)element.getChildren().get(0)).setPrefWidth(140);
                //((Label)element.getChildren().get(1)).setPrefWidth(0);
                //((Label)element.getChildren().get(2)).setPrefWidth(0);

                Timeline elementTransition = new Timeline(
                        new KeyFrame(Duration.millis(350), new KeyValue(((Label)element.getChildren().get(0)).prefWidthProperty(), 140, Interpolator.EASE_BOTH)),
                        new KeyFrame(Duration.millis(350), new KeyValue(((Label)element.getChildren().get(1)).prefWidthProperty(), 0, Interpolator.EASE_BOTH)),
                        new KeyFrame(Duration.millis(350), new KeyValue(((Label)element.getChildren().get(2)).prefWidthProperty(), 0, Interpolator.EASE_BOTH)));

                elementTransition.play();

                Button button=(Button)element.getChildren().get(3);
                if(selectedCenterID==Integer.parseInt(button.getId())){
                    button.setStyle("-fx-cursor: hand; -fx-background-radius: 5em; -fx-min-width: 1px; -fx-background-color: #1aaee8; -fx-border-radius: 5em; -fx-border-color: #000000;");
                }
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

            for(int i=0;i<vbox.getChildren().size();i++){
                HBox element=(HBox) vbox.getChildren().get(i);
                ((Label)element.getChildren().get(0)).setPrefWidth(150);
                ((Label)element.getChildren().get(1)).setPrefWidth(465);
                ((Label)element.getChildren().get(2)).setPrefWidth(133);
                Button button=(Button)element.getChildren().get(3);
                button.setStyle("-fx-cursor: hand; -fx-background-radius: 5em; -fx-min-width: 1px; -fx-background-color: #FFFFFF; -fx-border-radius: 5em; -fx-border-color: #000000;");
            }

            userData.remove("currentCenter");

        }
        else{
            selectedCenterID = currentCenterID;
            userData.put("currentCenter",String.valueOf(selectedCenterID));

            for(int i=0;i<vbox.getChildren().size();i++){
                HBox element=(HBox) vbox.getChildren().get(i);
                Button button=(Button)element.getChildren().get(3);

                if(selectedCenterID==Integer.parseInt(button.getId())) {
                    button.setStyle("-fx-cursor: hand; -fx-background-radius: 5em; -fx-min-width: 1px; -fx-background-color: #1aaee8; -fx-border-radius: 5em; -fx-border-color: #000000;");
                }
                else{
                    button.setStyle("-fx-cursor: hand; -fx-background-radius: 5em; -fx-min-width: 1px; -fx-background-color: #FFFFFF; -fx-border-radius: 5em; -fx-border-color: #000000;");
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
            //TODO ottimizzare sta roba controllando se è già stata caricata la UI tramite una variabile globale booleana
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/fxml/VisualizzazioneCentroVaccinale.fxml"));
            fxmlLoader.setController(this);
            Scene scene=new Scene(fxmlLoader.load());
            AnchorPane anchorPane=new AnchorPane(scene.lookup("#main_anchor_pane"));
            centerInfoPane.getChildren().add(anchorPane);

            loadCenterInfo(selectedCenterID,scene);

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
     * @param idCentro L'ID contenete il numero della riga del centro vaccinale selezionato nel file
     * @param currentScene La UI in cui verranno inseriti a video i dati presi dal database
     */
    public void loadCenterInfo(int idCentro, Scene currentScene){
        //TODO aggiungere un feedback visivo per il caricamento
        try {
            Vector<EventiAvversi> eventLines=leggiEventiAvversi(idCentro);
            int[] singleEvents=new int[6];
            Vector<String> otherEventsText=new Vector<>();

            for(int i=0;i<singleEvents.length;i++){
                singleEvents[i]=0;
            }

            for(EventiAvversi currentEvents: eventLines) {

                //sommo tra di loro i valori di ogni sintomo per poi poterne far la media
                singleEvents[0] += currentEvents.getMaleTesta();
                singleEvents[1] += currentEvents.getFebbre();
                singleEvents[2] += currentEvents.getDoloriMuscolari();
                singleEvents[3] += currentEvents.getLinfoadenopatia();
                singleEvents[4] += currentEvents.getTachicardia();
                singleEvents[5] += currentEvents.getCrisiIpertensiva();

                if (currentEvents.getOtherSymptoms() != null&&!(currentEvents.getOtherSymptoms().equals(""))) {
                    otherEventsText.add(currentEvents.getOtherSymptoms());
                }
            }

            for(int i=0;i<singleEvents.length;i++){
                if(singleEvents[i]!=0) {
                    singleEvents[i] /= eventLines.size();
                }
            }


            Label lbl_headacheEffect = (Label) currentScene.lookup("#lbl_effect1");
            Label lbl_feverEffect = (Label) currentScene.lookup("#lbl_effect2");
            Label lbl_hurtEffect = (Label) currentScene.lookup("#lbl_effect3");
            Label lbl_linfEffect = (Label) currentScene.lookup("#lbl_effect4");
            Label lbl_tacEffect = (Label) currentScene.lookup("#lbl_effect5");
            Label lbl_crsEffect = (Label) currentScene.lookup("#lbl_effect6");

            lbl_headacheEffect.setText(String.valueOf(singleEvents[0]));//evento1 = Mal di testa
            lbl_feverEffect.setText(String.valueOf(singleEvents[1])); //evento2 = Febbre
            lbl_hurtEffect.setText(String.valueOf(singleEvents[2])); //evento3 = Dolori muscolari o articolari
            lbl_linfEffect.setText(String.valueOf(singleEvents[3])); //evento4 = Linfoadenopatia
            lbl_tacEffect.setText(String.valueOf(singleEvents[4])); //evento5 = Tachicardia
            lbl_crsEffect.setText(String.valueOf(singleEvents[5]));//evento6 = Crisi ipertensiva

            ScrollPane scrollPane_otherEvents = (ScrollPane) currentScene.lookup("#scrollPane_otherEvents");
            VBox vbox = new VBox();
            vbox.setStyle("-fx-padding: 0 6");
            scrollPane_otherEvents.setContent(vbox);

            for (int i = 0; i < otherEventsText.size(); i++) {
                Pane vboxContent = new Pane();

                Label lbl_otherEventText = new Label(otherEventsText.get(i));
                lbl_otherEventText.setFont(Font.font("Franklin Gothic Medium", 18));
                lbl_otherEventText.setPrefWidth(490);
                lbl_otherEventText.setPrefHeight(30);


                vboxContent.getChildren().add(lbl_otherEventText);

                vbox.getChildren().add(vboxContent);

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Legge il database, relativo al centro vaccinale selezionato, contente gli eventi avversi e gli utenti vaccinati
     * @param currentCentreID L'ID (presente nel database) del centro vaccinale selezionato
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
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Database error");
            error.setContentText("Errore nel prendere i dati dal database");
            error.show();
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
     * Effettua la ricerca di un centro vaccinale. Richiama poi il metodo per aggiornare la UI mostrando solo i centri vaccinali che corrispondono ai parametri della ricerca.
     * @param currentScene La scena in cui verranno inseriti i centri trovati
     */
    public void search(Scene currentScene) throws IOException, ClassNotFoundException { //todo lo teniamo cosi o facciamo roba server ?
        //controllo che la lista non sia già popolata (per evitare inutili chiamate al db)
        if(centriVaccinaliList!=null) {
            centriVaccinaliList = getCentriVaccinaliFromDb();
        }

        Vector<SingoloCentroVaccinale> vector_search = new Vector<>();

        String search = ((TextField)currentScene.lookup("#txt_searchCenter")).getText().toLowerCase();
        boolean searchByName=((RadioButton)currentScene.lookup("#radio_name")).isSelected();
        boolean searchByTypeAndAddress=((RadioButton)currentScene.lookup("#radio_type")).isSelected();

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
        creaVbox(vector_search);
    }

    /**
     * Torna indietro alla schermata di selezione portale
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

            InputStream icon = getClass().getResourceAsStream("/common/fiorellino.png");
            Image image = new Image(icon);

            currentStage.getIcons().set(0,image);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Carica la UI di login utente. Viene richiamato quando l'utente preme sul tasto "Login"
     * @param event Il bottone cliccato dall'utente
     */
    public void onLoginClick(ActionEvent event){
        Stage stage=(Stage)((Button)event.getSource()).getScene().getWindow();
        loadLoginUI(stage);
    }

    /**
     * Carica la UI che permette ad un utente di effettuare il login, o in alternativa, di caricare la UI necessaria alla registrazione
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

            //scene.setUserData(currentStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea la UI che permette a un utente di inserire eventi avversi.
     * @param actionEvent L'evento che richiama il metodo. Necessario per ottenere lo stage in cui inserire la nuova scena
     */
    public void loadRegistraEventiAvversiUI(ActionEvent actionEvent){
        //TODO controllare che l'utente, se loggato, si sia vaccinato preso il centro selezionato
        Stage stage=(Stage)((Button)actionEvent.getSource()).getScene().getWindow();

        HashMap<String,String> userData=(HashMap<String,String>)stage.getUserData();
        String user=userData.get("currentUser");
        String center=userData.get("currentCenter");
        System.out.println(center);
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
     * @param user
     * @param currentCenter
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

            //scene.setUserData(mainScene);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Invia al server il relativo codice operazione per ottenere una lista di tutti i centri vaccinali presenti nel database
     */
    public static void becomeClient(){
        try {
            System.out.println("[CLIENT] - Sono già connesso, prendo gli stream ");
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
