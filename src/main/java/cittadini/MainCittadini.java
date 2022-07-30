package cittadini;

import centrivaccinali.SelectionUI;
import centrivaccinali.SingoloCentroVaccinale;
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
import java.util.StringTokenizer;
import java.util.Vector;

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
    private Vector<SingoloCentroVaccinale> centriVaccinaliList=new Vector<>();
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

    private int selectedCenterID;
    private boolean centerSelected=false;

    private Stage currentStage;

    private static PrintWriter out;
    private static BufferedReader in;

    private static ObjectInputStream ois;

    public static final int GETTER_OPERATION_CODE=5;

    public MainCittadini(Stage stage){
        loadMainCittadiniUI(stage);
    }

    /**
     * Carica la UI principale del portale dei cittadini. Questa UI Consente di scegliere il centro vaccinale presso cui consultare/inserire i dati. Viene chiamato dalla classe CentriVaccinali nel metodo onCittadiniSelected(ActionEvent event).
     */
    public void loadMainCittadiniUI(Stage stage){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/MainCittadini.fxml");
            System.out.println(xmlUrl.toString());
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
                btn_logout.setPrefWidth(100);
                btn_logout.setPrefHeight(30);
                btn_logout.setOnAction(event -> {
                    currentUser=null;
                    HashMap<String,String> newUserData=(HashMap<String,String>) stage.getUserData();
                    newUserData.remove("currentUser");
                    currentStage.setUserData(newUserData);
                    loadMainCittadiniUI(currentStage);
                });
            }

            currentStage=stage;

            currentStage.show();

            scrollPane_CentriVaccinali = (ScrollPane) scene.lookup("#scrollPane_CentriVaccinali");
            scrollPane_CentriVaccinali.lookup(".viewport").setStyle("-fx-background-color: #1a73e8;");

            centriVaccinaliList = getCentriVaccinaliFromDb();

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
        scrollPaneContent.setAlignment(Pos.CENTER);
        //scrollPaneContent.setMinWidth(scrollPane_CentriVaccinali.getPrefWidth()-2);
        scrollPane_CentriVaccinali.setContent(scrollPaneContent);

        for (int i=0;i<centriVaccinaliMostrati.size();i++){
            HBox hbox=new HBox();
            hbox.setPrefHeight(40);
            hbox.setAlignment(Pos.CENTER_LEFT);
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
            btnGoTo.setStyle( "-fx-cursor: hand; -fx-background-radius: 5em; -fx-min-width: 1px; -fx-background-color: #FFFFFF; -fx-border-radius: 5em; -fx-border-color: #000000;");
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
     * @param actionEvent L'evento che richiama il metodo. Necessario ad ottenere il bottone sorgente dell'evento dal quale è possibile ottenere l'id del centro selezionato.
     */
    @Override
    public void handle(ActionEvent actionEvent) {
        Button source = (Button) actionEvent.getSource();
        ScrollPane centerListPane = (ScrollPane) source.getScene().lookup("#scrollPane_CentriVaccinali");
        Pane centerInfoPane = (Pane) source.getScene().lookup("#pane_center_information");
        int currentCenterID = Integer.parseInt(source.getId());

        ScrollPane scrollPane=(ScrollPane) source.getScene().lookup("#scrollPane_CentriVaccinali");
        VBox vbox=(VBox)scrollPane.getContent();

        if(!centerSelected) {
            selectedCenterID = currentCenterID;
            centerSelected=true;

            Timeline paneTransition = new Timeline(
                    new KeyFrame(Duration.millis(350), new KeyValue(centerListPane.prefWidthProperty(), centerListPane.getPrefWidth() / 3)),
                    new KeyFrame(Duration.millis(350), new KeyValue(centerInfoPane.prefWidthProperty(), centerListPane.getPrefWidth() - (centerListPane.getPrefWidth() / 3))),
                    new KeyFrame(Duration.millis(350), new KeyValue(centerInfoPane.translateXProperty(), -(centerListPane.getPrefWidth() - (centerListPane.getPrefWidth() / 3)))));

            paneTransition.play();

            for(int i=0;i<vbox.getChildren().size();i++){
                HBox element=(HBox) vbox.getChildren().get(i);
                ((Label)element.getChildren().get(0)).setPrefWidth(140);
                ((Label)element.getChildren().get(1)).setPrefWidth(0);
                ((Label)element.getChildren().get(2)).setPrefWidth(0);
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
        }
        else{
            selectedCenterID = currentCenterID;

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
     *
     */
    public void loadVisualizzatoreCentroVaccinale(Pane centerInfoPane, int selectedCenterID){
        try {
            //TODO ottimizzare sta roba controllando se è già stata caricata la UI tramite una variabile globale booleana
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/fxml/visualizzazioneCentroVaccinale.fxml"));
            fxmlLoader.setController(this);
            Scene scene=new Scene(fxmlLoader.load());
            AnchorPane anchorPane=new AnchorPane(scene.lookup("#main_anchor_pane"));
            centerInfoPane.getChildren().add(anchorPane);

            /*Label lbl_centreName = (Label) scene.lookup("#lbl_highlitedCenterName");
            Label lbl_centreAddress = (Label) scene.lookup("#lbl_highlitedCenterAddress");
            Label lbl_centreType = (Label) scene.lookup("#lbl_highlitedCenterType");*/
            //loadCentreInfo(selectedCenterID, lbl_centreName, lbl_centreAddress, lbl_centreType);
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
     * @param idCentro L'ID contenete il numero della riga del centro vaccinale selezionato nel file*/
    /*@param lbl_centreName L'ettichetta contenete il nome del centro selezionato
     * @param lbl_centreAddress L'ettichetta contenete l'indirizzo del centro selezionato
     * @param lbl_centreType L'ettichetta contenete la tipologia del vaccina somministrato presso il centro selezione
     */
    public void loadCenterInfo(int idCentro, Scene currentScene){ //todo lato server
        try {
            FileReader fileReader=new FileReader(PATH_TO_CENTRIVACCINALI_DATI);
            BufferedReader reader=new BufferedReader(fileReader);

            /*String data=reader.readLine();
            int i=0;
            while(i!=idCentro) {
                data=reader.readLine();
                i++;
            }

            StringTokenizer stringTokenizer=new StringTokenizer(data,";");
            String name=stringTokenizer.nextToken();
            String address=stringTokenizer.nextToken()+" "+stringTokenizer.nextToken()+", "+stringTokenizer.nextToken()+" "+stringTokenizer.nextToken()+" ("+stringTokenizer.nextToken()+") "+stringTokenizer.nextToken();
            String type=stringTokenizer.nextToken();

            lbl_centreName.setText(name);
            lbl_centreAddress.setText(address);
            lbl_centreType.setText(type);

            currentCenter=name;*/

            Vector<String> eventLines=leggiEventiAvversi(idCentro);
            int[] singleEvents=new int[6];
            Vector<String> otherEventsText=new Vector<>();
            Vector<Integer> otherEventsValues=new Vector<>();

            if(eventLines!=null) {
                for (int i = 0; i < eventLines.size(); i++) {
                    StringTokenizer tokenizer = new StringTokenizer(eventLines.get(i), ";");
                    tokenizer.nextToken();
                    tokenizer.nextToken();
                    tokenizer.nextToken();

                    singleEvents[0] = singleEvents[0] + (Integer.parseInt(tokenizer.nextToken()));
                    singleEvents[1] = singleEvents[1] + (Integer.parseInt(tokenizer.nextToken()));
                    singleEvents[2] = singleEvents[2] + (Integer.parseInt(tokenizer.nextToken()));
                    singleEvents[3] = singleEvents[3] + (Integer.parseInt(tokenizer.nextToken()));
                    singleEvents[4] = singleEvents[4] + (Integer.parseInt(tokenizer.nextToken()));
                    singleEvents[5] = singleEvents[5] + (Integer.parseInt(tokenizer.nextToken()));

                    if (tokenizer.hasMoreTokens()) {
                        otherEventsText.add(tokenizer.nextToken());
                        otherEventsValues.add(Integer.parseInt(tokenizer.nextToken()));
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
                    lbl_otherEventText.setPrefWidth(475);
                    lbl_otherEventText.setPrefHeight(30);

                    Label lbl_otherEventValue = new Label(String.valueOf(otherEventsValues.get(i)));
                    lbl_otherEventValue.setFont(Font.font("Franklin Gothic Medium", 18));
                    lbl_otherEventValue.setPrefWidth(13);
                    lbl_otherEventValue.setPrefHeight(30);
                    lbl_otherEventValue.setLayoutX(490);

                    //TODO aggiungere label con scritto "intensità media"

                    vboxContent.getChildren().add(lbl_otherEventText);
                    vboxContent.getChildren().add(lbl_otherEventValue);

                    vbox.getChildren().add(vboxContent);

                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Legge il file di testo, relativo al centro vaccinale selezionato, contente gli eventi avversi e gli utenti vaccinati
     * @param currentCentreID L'ID contenete il numero della riga del centro vaccinale selezionato nel file
     * @return Una lista di stringhe contente tutte le righe del file con eventi avversi relativi al centro vaccinale selezionato
     */
    public Vector<String> leggiEventiAvversi(int currentCentreID) throws IOException, ClassNotFoundException { //todo lato server
        centriVaccinaliList= getCentriVaccinaliFromDb();

        SingoloCentroVaccinale centroVaccinale=centriVaccinaliList.get(currentCentreID);

        try{
            FileReader fileReader=new FileReader(PRE_PATH_TO_EVENTI_AVVERSI+centroVaccinale.getNome()+AFTER_PATH_TO_EVENTI_AVVERSI);
            BufferedReader reader=new BufferedReader(fileReader);

            String line=reader.readLine();

            Vector<String> eventLines=new Vector<>();
            while (line!=null){
                StringTokenizer tokenizer=new StringTokenizer(line,";");
                if(tokenizer.nextToken().equals(LINE_TYPE_EVENT)) {
                    eventLines.add(line);
                }
                line=reader.readLine();
            }

            return eventLines;

        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Legge i centri vaccinali presenti nel file e li restituisce sotto firma di lista.
     * @return Il vettore contente i centri vaccinali presenti nel file.
     */
    public static Vector<SingoloCentroVaccinale> getCentriVaccinaliFromDb() throws IOException, ClassNotFoundException {
        //Vector<SingoloCentroVaccinale> vector = new Vector<>();
        becomeClient();
        System.out.println("[CLIENT] Uscito dalla become client");
        try {
            ois = new ObjectInputStream(SelectionUI.socket_container.getInputStream());
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Database error");
            error.setContentText("Errore nel prendere i dati dal database");
            e.printStackTrace();
        }

       /* try {
            FileReader fileReader = new FileReader(PATH_TO_CENTRIVACCINALI_DATI);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.countTokens() == 8) {
                    String nome = st.nextToken();
                    String via = st.nextToken();
                    String nome1 = st.nextToken();
                    String num_civico = st.nextToken();
                    String comune = st.nextToken();
                    String provincia = st.nextToken();
                    String cap = st.nextToken();
                    String indirizzo = via+" "+nome1+", "+num_civico+", "+comune+" ("+provincia+") "+cap;
                    String tipologia = st.nextToken();

                    vector.add(new SingoloCentroVaccinale(nome, indirizzo, tipologia));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        return (Vector<SingoloCentroVaccinale>) ois.readObject();

    }


    /**
     * Effettua la ricerca di un centro vaccinale nel file di testo. Richiama poi il metodo per aggiornare la UI mostrando solo i centri vaccinali che corrispondono ai parametri della ricerca.
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena attuale da cui prendere i parametri di ricerca.
     */
    public void findCenter(ActionEvent event) throws IOException, ClassNotFoundException {
        Scene currentScene=((Button)event.getSource()).getScene();
        search(currentScene);
    }

    public void keyTyped(KeyEvent event) throws IOException, ClassNotFoundException {
        Scene currentScene=((TextField)event.getSource()).getScene();
        search(currentScene);
    }

    public void search(Scene currentScene) throws IOException, ClassNotFoundException { //todo lo teniamo cosi o facciamo roba server ?
        centriVaccinaliList= getCentriVaccinaliFromDb();

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

    public void goBackFromMainCittadini(MouseEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/SelectionUI.fxml");
            loader.setLocation(xmlUrl);

            Parent root = loader.load();

            Scene scene=new Scene(root);

            Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();

            currentStage.setScene(scene);
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }


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
     * Crea la UI che permette ad un utente di inserire eventi avversi.
     */
    public void loadRegistraEventiAvversiUI(Stage stage){
        try {

            FXMLLoader loader=new FXMLLoader();
            URL url=getClass().getResource("/fxml/registraEventiAvversi.fxml");
            loader.setLocation(url);
            Parent root=loader.load();

            Scene scene=new Scene(root);
            stage.setScene(scene);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Carica la UI necessaria ad effettuare la registrazione di un utente.
     * @param event L'evento che richiama il metodo. Necessario per chiudere la UI di login
     */
    public void loadRegisterCitizenUI(ActionEvent event){
        try {
            //Scene mainScene=((Button)event.getSource()).getScene();
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

    public static void becomeClient(){
        try {
            System.out.println("[CLIENT] - Sono già connesso, prendo gli stream ");
            Socket s = SelectionUI.socket_container;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out.println("void");
            out.println(GETTER_OPERATION_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
