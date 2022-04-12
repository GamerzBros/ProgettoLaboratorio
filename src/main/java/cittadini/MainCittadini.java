package cittadini;

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
import java.net.URL;
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
    private String currentUser;
    /**
     * Centro vaccinale attualmente selezionato
     */
    private String currentCenter;

    private int selectedCenterID;
    private boolean centerSelected=false;

    private Stage currentStage;

    public MainCittadini(Stage stage){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/MainCittadini.fxml");
            System.out.println(xmlUrl.toString());
            loader.setLocation(xmlUrl);
            loader.setController(this);

            Parent root = loader.load();

            Scene scene=new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Portale Cittadini");
            stage.setY(50);
            stage.setX(175);

            String[] userData = new String[2];
            scene.setUserData(userData);

            scrollPane_CentriVaccinali = (ScrollPane) scene.lookup("#scrollPane_CentriVaccinali");
            scrollPane_CentriVaccinali.lookup(".viewport").setStyle("-fx-background-color: #1a73e8;");

            centriVaccinaliList = getCentriVaccinaliFromFile();

            creaVbox(centriVaccinaliList);

            currentStage=stage;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica la UI principale del portale dei cittadini. Questa UI Consente di scegliere il centro vaccinale presso cui consultare/inserire i dati. Viene chiamato dalla classe CentriVaccinali nel metodo onCittadiniSelected(ActionEvent event).
     */
    /*@Override
    public void start(Stage stage) throws Exception {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/cittadini/MainCittadini.fxml");
            fxmlLoader.setLocation(url);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Portale Cittadini");
            stage.setY(50);
            stage.setX(175);

            String[] userData = new String[2];
            scene.setUserData(userData);

            scrollPane_CentriVaccinali = (ScrollPane) scene.lookup("#scrollPane_CentriVaccinali");
            scrollPane_CentriVaccinali.lookup(".viewport").setStyle("-fx-background-color: #1a73e8;");

            centriVaccinaliList = getCentriVaccinaliFromFile();

            creaVbox(centriVaccinaliList);

            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }*/

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
     * Porta il cittadino alla UI del centro vaccinale sul quale ha cliccato.
     * @param actionEvent L'evento che richiama il metodo. Necessario ad ottenere il bottone sorgente dell'evento dal quale è possibile ottenere l'id del centro selezionato.
     */
    @Override
    public void handle(ActionEvent actionEvent) {
        Button source = (Button) actionEvent.getSource();
        ScrollPane centerListPane = (ScrollPane) source.getScene().lookup("#scrollPane_CentriVaccinali");
        Pane centerInfoPane = (Pane) source.getScene().lookup("#pane_center_information");
        int currentCenterID = Integer.parseInt(source.getId());

        if(!centerSelected) {
            selectedCenterID = currentCenterID;
            centerSelected=true;

            Timeline paneTransition = new Timeline(
                    new KeyFrame(Duration.millis(350), new KeyValue(centerListPane.prefWidthProperty(), centerListPane.getPrefWidth() / 3)),
                    new KeyFrame(Duration.millis(350), new KeyValue(centerInfoPane.prefWidthProperty(), centerListPane.getPrefWidth() - (centerListPane.getPrefWidth() / 3))),
                    new KeyFrame(Duration.millis(350), new KeyValue(centerInfoPane.translateXProperty(), -(centerListPane.getPrefWidth() - (centerListPane.getPrefWidth() / 3)))));

            paneTransition.play();

            ScrollPane scrollPane=(ScrollPane) source.getScene().lookup("#scrollPane_CentriVaccinali");
            VBox vbox=(VBox)scrollPane.getContent();

            for(int i=0;i<vbox.getChildren().size();i++){
                HBox element=(HBox) vbox.getChildren().get(i);
                ((Label)element.getChildren().get(0)).setPrefWidth(140);
                ((Label)element.getChildren().get(1)).setPrefWidth(0);
                ((Label)element.getChildren().get(2)).setPrefWidth(0);
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

            ScrollPane scrollPane=(ScrollPane) source.getScene().lookup("#scrollPane_CentriVaccinali");
            VBox vbox=(VBox)scrollPane.getContent();

            for(int i=0;i<vbox.getChildren().size();i++){
                HBox element=(HBox) vbox.getChildren().get(i);
                ((Label)element.getChildren().get(0)).setPrefWidth(150);
                ((Label)element.getChildren().get(1)).setPrefWidth(465);
                ((Label)element.getChildren().get(2)).setPrefWidth(133);
            }
        }
        else{
            selectedCenterID = currentCenterID;
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
            e.printStackTrace(); //TODO mettere popup con scritto "errore nel caricamento dello info del centro"
        }
    }

    /**
     * Carica le informazioni principali del centri vaccinale selezionato.
     * @param idCentro L'ID contenete il numero della riga del centro vaccinale selezionato nel file*/
    /*@param lbl_centreName L'ettichetta contenete il nome del centro selezionato
     * @param lbl_centreAddress L'ettichetta contenete l'indirizzo del centro selezionato
     * @param lbl_centreType L'ettichetta contenete la tipologia del vaccina somministrato presso il centro selezione
     */
    public void loadCenterInfo(int idCentro, Scene currentScene){
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
    public Vector<String> leggiEventiAvversi(int currentCentreID){
        centriVaccinaliList=getCentriVaccinaliFromFile();

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
    public static Vector<SingoloCentroVaccinale> getCentriVaccinaliFromFile() {
        Vector<SingoloCentroVaccinale> vector = new Vector<>();

        try {
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

        return vector;

    }

    /**
     * Effettua la ricerca di un centro vaccinale nel file di testo. Richiama poi il metodo per aggiornare la UI mostrando solo i centri vaccinali che corrispondono ai parametri della ricerca.
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena attuale da cui prendere i parametri di ricerca.
     */
    public void findCenter(ActionEvent event) {
        Scene currentScene=((Button)event.getSource()).getScene();
        search(currentScene);
    }

    public void keyTyped(KeyEvent event){
        Scene currentScene=((TextField)event.getSource()).getScene();
        search(currentScene);
    }

    public void search(Scene currentScene){
        centriVaccinaliList=getCentriVaccinaliFromFile();

        Vector<SingoloCentroVaccinale> vector_search = new Vector<>();

        String search = ((TextField)currentScene.lookup("#txt_searchCenter")).getText().toLowerCase();
        boolean searchByName=((RadioButton)currentScene.lookup("#radio_name")).isSelected();
        boolean searchByTypeAndAddress=((RadioButton)currentScene.lookup("#radio_type")).isSelected();

        for(int index=0;index<centriVaccinaliList.size();index++){
            SingoloCentroVaccinale tempCentre=centriVaccinaliList.get(index);
            String nome=tempCentre.getNome();
            String indirizzo=tempCentre.getIndirizzo();
            String tipologia=tempCentre.getTipologia();

            if (searchByName){
                if((nome.toLowerCase()).contains(search)){
                    vector_search.add(new SingoloCentroVaccinale(nome, indirizzo, tipologia));
                }
            }
            else if(searchByTypeAndAddress){
                if((indirizzo.toLowerCase()).contains(search) || (tipologia.toLowerCase()).contains(search)) {
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

    /**
     * Controlla che l'utente sia loggato quando prova ad inserire nuovi eventi avversi.
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena attuale da cui prendere il nome del centro vaccinale selezionato e, se presente, l'utente attuale.
     */
    public void checkLogin(ActionEvent event){
        Scene mainScene=((Button)event.getSource()).getScene();
        Stage currentStage=(Stage)mainScene.getWindow();
        String[] userData=(String[]) mainScene.getUserData();
        currentCenter=userData[0];

        if(userData[1]!=null){
            currentUser=userData[1];
            //TODO rivedere dove porta il login e il register
            loadRegistraEventiAvversiUI();
        }
        else{
            //TODO aggiungere un popup per dire all'utente di loggarsi prima
            loadLoginUI(currentStage);
        }
    }

    /**
     * Carica la UI che permette ad un utente di effettuare il login, o in alternativa, di caricare la UI necessaria alla registrazione
     * @param stage La scena da cui inserire e prendere il nome centro vaccinale e il codice fiscale del cittadino loggato
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
    public void loadRegistraEventiAvversiUI(){
        try {

            FXMLLoader loader=new FXMLLoader();
            URL url=getClass().getResource("registraEventiAvversi.fxml");
            loader.setLocation(url);
            Parent root=loader.load();

            Scene scene=new Scene(root);
            Stage stage=new Stage();
            stage.setScene(scene);

            /*String[] userData=new String[2];
            userData[0]=currentCenter;
            userData[1]=currentUser;
            scene.setUserData(userData);

            System.out.println(userData[0]);

            stage.show();*/
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Registra sul file di testo relativo al centro vaccinale selezionato, gli eventi avversi inseriti dall'utente.
     * @param actionEvent L'evento che richiama il metodo. Necessario ad ottenere la scena attuale da cui prendere i dati inseriti dall'utente.
     */
    public void registerEventiAvversi(ActionEvent actionEvent){
        try {
            Scene currentScene = ((Button) actionEvent.getSource()).getScene();

            String[] userData = (String[]) currentScene.getUserData();
            currentCenter = userData[0];
            currentUser = userData[1];

            System.out.println(currentCenter);

            Spinner<Integer> spn_headache = (Spinner<Integer>) currentScene.lookup("#spn_headache");
            Spinner<Integer> spn_fever = (Spinner<Integer>) currentScene.lookup("#spn_fever");
            Spinner<Integer> spn_hurt = (Spinner<Integer>) currentScene.lookup("#spn_hurt");
            Spinner<Integer> spn_linf = (Spinner<Integer>) currentScene.lookup("#spn_linf");
            Spinner<Integer> spn_tac = (Spinner<Integer>) currentScene.lookup("#spn_tac");
            Spinner<Integer> spn_crs = (Spinner<Integer>) currentScene.lookup("#spn_crs");
            TextField txt_other1 = (TextField) currentScene.lookup("#txt_other");
            Spinner<Integer> spn_other1 = (Spinner<Integer>) currentScene.lookup("#spn_other");


            int evento1 = spn_headache.getValue();//evento1 = Mal di testa
            int evento2 = spn_fever.getValue(); //evento2 = Febbre
            int evento3 = spn_hurt.getValue(); //evento3 = Dolori muscolari o articolari
            int evento4 = spn_linf.getValue(); //evento4 = Linfoadenopatia
            int evento5 = spn_tac.getValue(); //evento5 = Tachicardia
            int evento6 = spn_crs.getValue();//evento6 = Crisi ipertensiva
            String otherEvent = txt_other1.getText();
            int otherEventValue = spn_other1.getValue();

            FileReader reader = new FileReader(PRE_PATH_TO_EVENTI_AVVERSI + currentCenter + AFTER_PATH_TO_EVENTI_AVVERSI);
            BufferedReader in = new BufferedReader(reader);
            boolean authorized = false;
            boolean alreadyIn = false;
            String line;

            while ((line = in.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0].equals(LINE_TYPE_PERSON) && data[3].equalsIgnoreCase(currentUser)) {
                    authorized = true;
                } else if (data[0].equals(LINE_TYPE_EVENT) && data[2].equalsIgnoreCase(currentUser)) {
                    alreadyIn = true;
                }
            }

            if (alreadyIn) {
                Alert alertAlreadyIn = new Alert(Alert.AlertType.ERROR);
                alertAlreadyIn.setTitle("Eventi già inseriti");
                alertAlreadyIn.setContentText("L'utente ha già inserito una volta degli eventi avversi presso il centro attuale");
                alertAlreadyIn.showAndWait();
            }
            else if (authorized) {

                FileWriter writer = new FileWriter(PRE_PATH_TO_EVENTI_AVVERSI + currentCenter + AFTER_PATH_TO_EVENTI_AVVERSI, true);
                BufferedWriter out = new BufferedWriter(writer);
                //String fileInput = "Mal di Testa:" + evento1 + ";" + "Febbre:" + evento2 + ";" + "Dolori muscolari o articolari:" + evento3 + ";" + "Linfoadenopatia:" + evento4 + ";" + "Tachicardia:" + evento5 + ";" + "Crisi ipertensiva:" + evento6 + ";";

                String fileInput = LINE_TYPE_EVENT + ";" + currentCenter + ";" + currentUser + ";" + evento1 + ";" + evento2 + ";" + evento3 + ";" + evento4 + ";" + evento5 + ";" + evento6;
                if (otherEvent.compareTo("") != 0) {
                    fileInput += ";" + otherEvent + ";" + otherEventValue;
                }

                out.write(fileInput);
                out.newLine();
                out.flush();
                out.close();
            } else {
                Alert alertNoPermission = new Alert(Alert.AlertType.ERROR);
                alertNoPermission.setTitle("Utente non autorizzato");
                alertNoPermission.setContentText("Non sei stato vaccinato presso il centro selezionato!");
                alertNoPermission.showAndWait();
            }

            Stage stage = (Stage) currentScene.getWindow();
            stage.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
