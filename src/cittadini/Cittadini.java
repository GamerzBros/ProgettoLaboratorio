package cittadini;

import centrivaccinali.SingoloCentroVaccinale;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*Cristian Arcadi 745389 Varese
  David Poletti 746597 Varese
  Eros Marsichina 745299 Varese
  Tommaso Morosi 741227 Varese*/

/**
 * Contiene tutte le UI e i metodi del portale Cittadini
 */
public class Cittadini implements EventHandler<ActionEvent> {
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
     * Codice fiscale dell'utente attualmente loggato
     */
    private String currentUser;
    /**
     * Centro vaccinale attualmente selezionato
     */
    private String currentCenter;
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
     * Carica la UI principale del portale dei cittadini. Questa UI Consente di scegliere il centro vaccinale presso cui consultare/inserire i dati. Viene chiamato dalla classe CentriVaccinali nel metodo onCittadiniSelected(ActionEvent event).
     */
    public void loadMainCittadiniUI(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("mainCittadini.fxml");
            fxmlLoader.setLocation(url);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Portale Cittadini");

            scrollPane_CentriVaccinali = (ScrollPane) scene.lookup("#scrollPane_CentriVaccinali");

            centriVaccinaliList = Cittadini.getCentriVaccinaliFromFile();

            creaVbox(centriVaccinaliList);

            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Crea il vbox e i necessari componenti grafici contenenti le informazioni sui centri vaccinali consultabili.
     * @param centriVaccinaliMostrati Lista contenente i centri vaccinale da inserire dentro il vbox (quindi dentro la UI).
     */
    private void creaVbox(List<SingoloCentroVaccinale> centriVaccinaliMostrati){
        VBox scrollPaneContent=new VBox();
        scrollPaneContent.setMinWidth(scrollPane_CentriVaccinali.getPrefWidth()-2);

        scrollPane_CentriVaccinali.setContent(scrollPaneContent);

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

            lblType.setLayoutX(575);
            lblType.setMinHeight(30);
            lblType.setFont(new Font("Arial",19));

            btnGoTo.setLayoutX(725);
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

    /**
     * Porta il cittadino alla UI del centro vaccinale sul quale ha cliccato.
     * @param actionEvent L'evento che richiama il metodo. Necessario ad ottenere il bottone sorgente dell'evento dal quale è possibile ottenere l'id del centro selezionato.
     */
    @Override
    public void handle(ActionEvent actionEvent) {
        Button source = (Button) actionEvent.getSource();
        int currentCentreID = Integer.parseInt(source.getId());

        loadVisualizzatoreCentroVaccinale(currentCentreID);
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
     * Crea la UI che mostra i dati relativi al centro vaccinale selezionato. Viene richiamato quando l'utente seleziona un centro vaccinale.
     * @param idCentro L'ID contenete il numero della riga del centro vaccinale selezionato nel file
     */
    public void loadVisualizzatoreCentroVaccinale(int idCentro){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("visualizzazioneCentroVaccinale.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene newScene = new Scene(root);

            Stage currentStage = (Stage) scrollPane_CentriVaccinali.getScene().getWindow();
            currentStage.setScene(newScene);

            Label lbl_centreName = (Label) newScene.lookup("#lbl_highlitedCenterName");
            Label lbl_centreAddress = (Label) newScene.lookup("#lbl_highlitedCenterAddress");
            Label lbl_centreType = (Label) newScene.lookup("#lbl_highlitedCenterType");

            loadCentreInfo(idCentro, lbl_centreName, lbl_centreAddress, lbl_centreType);

            String[] userData = new String[2];
            userData[0] = currentCenter;
            newScene.setUserData(userData);

            currentStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Carica le informazioni principali del centri vaccinale selezionato.
     * @param idCentro L'ID contenete il numero della riga del centro vaccinale selezionato nel file
     * @param lbl_centreName L'ettichetta contenete il nome del centro selezionato
     * @param lbl_centreAddress L'ettichetta contenete l'indirizzo del centro selezionato
     * @param lbl_centreType L'ettichetta contenete la tipologia del vaccina somministrato presso il centro selezione
     */
    public void loadCentreInfo(int idCentro, Label lbl_centreName,Label lbl_centreAddress,Label lbl_centreType){
        try {
            FileReader fileReader=new FileReader(PATH_TO_CENTRIVACCINALI_DATI);
            BufferedReader reader=new BufferedReader(fileReader);

            String data=reader.readLine();
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

            currentCenter=name;

            Vector<String> eventLines=leggiEventiAvversi(idCentro);
            int[] singleEvents=new int[6];
            Vector<String> otherEventsText=new Vector<>();
            Vector<Integer> otherEventsValues=new Vector<>();

            if(eventLines!=null) {
                for (i = 0; i < eventLines.size(); i++) {
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


                Scene currentScene = lbl_centreName.getScene();

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
                scrollPane_otherEvents.setContent(vbox);

                for (i = 0; i < otherEventsText.size(); i++) {
                    Pane vboxContent = new Pane();

                    Label lbl_otherEventText = new Label(otherEventsText.get(i));
                    lbl_otherEventText.setFont(Font.font("Franklin Gothic Medium", 14));
                    lbl_otherEventText.setMinWidth(800);
                    lbl_otherEventText.setMinHeight(30);

                    Label lbl_otherEventValue = new Label(String.valueOf(otherEventsValues.get(i)));
                    lbl_otherEventValue.setMinWidth(30);
                    lbl_otherEventValue.setMinHeight(30);
                    lbl_otherEventValue.setLayoutX(800);

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
     * Controlla che l'utente sia loggato quando prova ad inserire nuovi eventi avversi.
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena attuale da cui prendere il nome del centro vaccinale selezionato e, se presente, l'utente attuale.
     */
    public void checkLogin(ActionEvent event){
        Scene mainScene=((Button)event.getSource()).getScene();
        String[] userData=(String[]) mainScene.getUserData();
        currentCenter=userData[0];

        System.out.println(currentCenter);

        if(userData[1]!=null){
            currentUser=userData[1];
            loadRegistraEventiAvversiUI();
        }
        else{
            loadLoginUI(mainScene);
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

            String[] userData=new String[2];
            userData[0]=currentCenter;
            userData[1]=currentUser;
            scene.setUserData(userData);

            System.out.println(userData[0]);

            stage.show();
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
     * Carica la UI che permette ad un utente di effettuare il login, o in alternativa, di caricare la UI necessaria alla registrazione
     * @param mainScene La scena da cui inserire e prendere il nome centro vaccinale e il codice fiscale del cittadino loggato
     */
    public void loadLoginUI(Scene mainScene){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("loginCittadino.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            scene.setUserData(mainScene);

            stage.show();
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
            Scene mainScene=(Scene) ((Button)event.getSource()).getScene().getUserData();

            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("nuovoCittadino.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            scene.setUserData(mainScene);

            stage.show();

            Scene loginScene=((Button)event.getSource()).getScene();
            ((Stage)loginScene.getWindow()).close();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Registra un cittadino nel file di testo contente tutti i cittadini registrati.
     * @param event L'evento che richiama il metodo. Necessario ad ottenere la scena attuale per prendere le informazioni inserite dall'utente.
     */
    public void registraCittadino(ActionEvent event){
        Scene currentScene=((Button)event.getSource()).getScene();

        String name = ((TextField)currentScene.lookup("#txt_userName")).getText();
        String surname = ((TextField)currentScene.lookup("#txt_userSurname")).getText();
        String user = ((TextField)currentScene.lookup("#txt_userMail")).getText();
        String userCF=((TextField)currentScene.lookup("#txt_userCF")).getText();
        String pwd = ((PasswordField)currentScene.lookup("#pswd_register")).getText();
        String confrmationPwd=((PasswordField)currentScene.lookup("#pswd_confirm")).getText();
        LocalDate vaccinationDate = ((DatePicker)currentScene.lookup("#datePicker_datavaccinazione")).getValue();
        String dataVaccinazione = vaccinationDate.format(DateTimeFormatter.ofPattern("MMM-dd-yyyy"));

        if(pwd.compareTo(confrmationPwd)==0) {

            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] hash = messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8));
                pwd = toHexString(hash);

                FileWriter writer = new FileWriter(PATH_TO_CITTADINI_REGISTRATI_DATI, false);
                BufferedWriter out = new BufferedWriter(writer);
                String scrivi = name+";"+surname+";"+user+";"+pwd+";"+userCF+";"+dataVaccinazione;
                out.write(scrivi);
                out.newLine();
                out.close();

                Scene mainScene=(Scene)currentScene.getUserData();
                String[] userData=(String[])mainScene.getUserData();
                currentCenter=userData[0];

                currentUser=userCF;


                userData[1]=currentUser;

                mainScene.setUserData(userData);


                ((Stage)currentScene.getWindow()).close();

                Alert alertRegistrationSuccessfull=new Alert(Alert.AlertType.INFORMATION);
                alertRegistrationSuccessfull.setTitle("Registrazione completata");
                alertRegistrationSuccessfull.setContentText("Registrazione avvenuta con successo");
                alertRegistrationSuccessfull.showAndWait();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Alert wrongPswdAlert=new Alert(Alert.AlertType.ERROR);
            wrongPswdAlert.setTitle("Errore di registrazione");
            wrongPswdAlert.setContentText("Le password inserite non corrispondono");
            wrongPswdAlert.show();
        }
    }

    /**
     * Effettua il login dell'utente.
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena da cui prendere i dati inseriti dall'utente
     */
    public void loggaCittadini(ActionEvent event) {
        Scene currentScene=((Button)event.getSource()).getScene();
        String user = ((TextField)currentScene.lookup("#txt_userLogin")).getText();
        String pwd = ((TextField)currentScene.lookup("#pswd_login")).getText();
        String user_temp;
        String pwd_temp;
        String[] parts;
        System.out.println("Login in corso");

        try {
            if (!user.equals("") && !pwd.equals("")) {
                FileReader fileReader=new FileReader(PATH_TO_CITTADINI_REGISTRATI_DATI);
                BufferedReader reader=new BufferedReader(fileReader);
                boolean isLogged=false;
                String line;

                while ((line=reader.readLine())!=null) {
                    parts = line.split(";");
                    user_temp = parts[2];
                    pwd_temp = parts[3];

                    if (user_temp.equals(user)) {

                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                        pwd = toHexString(messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8)));

                        if(pwd_temp.equals(pwd)) {
                            System.out.println("LOGGATO");
                            isLogged = true;
                            currentUser=parts[4]; //CF dell'utente

                            Scene mainScene=(Scene)currentScene.getUserData();
                            String[] userData=(String[])mainScene.getUserData();
                            currentCenter=userData[0];
                            userData[1]=currentUser;
                            mainScene.setUserData(userData);

                            Stage currentStage=(Stage)((Button)event.getSource()).getScene().getWindow();
                            currentStage.close();

                            Alert alertSuccessfullLogin=new Alert(Alert.AlertType.INFORMATION);
                            alertSuccessfullLogin.setTitle("Login effettuato");
                            alertSuccessfullLogin.setContentText("Utente loggato");
                            alertSuccessfullLogin.showAndWait();

                            loadRegistraEventiAvversiUI();
                        }
                    }
                }
                if(!isLogged) {
                    Alert noUserAlert = new Alert(Alert.AlertType.WARNING);
                    noUserAlert.setTitle("Errore di login");
                    noUserAlert.setContentText("Utente non trovato!");
                    noUserAlert.show();
                }
            } else {
                Alert alertNoData=new Alert(Alert.AlertType.WARNING);
                alertNoData.setTitle("Inserisci dei dati");
                alertNoData.setContentText("Non hai inserito i dati");
                alertNoData.showAndWait();
                System.out.println("Inserire dei dati");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converte un array di byte in una stringa. Viene utilizzato dopo aver effettuato l'hashing di una stringa, per ricomporre quest'ultima.
     * @param array L'array contenente i byte di risultato dell'hashing.
     * @return La stringa ottenuta come risultato dalla funzione di hash.
     */
    private String toHexString(byte[] array) {
        StringBuilder sb = new StringBuilder(array.length * 2);

        for (byte b : array) {
            int value = 0xFF & b;
            String toAppend = Integer.toHexString(value);

            sb.append(toAppend);
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Effettua la ricerca di un centro vaccinale nel file di testo. Richiama poi il metodo per aggiornare la UI mostrando solo i centri vaccinali che corrispondono ai parametri della ricerca.
     * @param event L'evento che richiama il metodo. Necessario per ottenere la scena attuale da cui prendere i parametri di ricerca.
     */
    public void findCenter(ActionEvent event) {
        centriVaccinaliList=getCentriVaccinaliFromFile();

        Vector<SingoloCentroVaccinale> vector_search = new Vector<>();

        Scene currentScene=((Button)event.getSource()).getScene();

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

    /**
     * Chiude la finestra (stage) attuale. Il metodo viene usato per tutte le UI, relative al package, che contengono il tasto "annulla".
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage da chiudere.
     */
    public void onAnnullaButtonClicked(ActionEvent event){
        Scene currentScene=((Button) event.getSource()).getScene();
        Stage currentStage=(Stage)currentScene.getWindow();
        currentStage.close();
    }

    /**
     * Torna alla finestra MainCittadini.fxml.
     * @param event L'evento che richiama il metodo. Necessario per ottenere lo stage da chiudere.
     */
    public void onGoBackToMain(ActionEvent event){
        Stage currentStage = (Stage)((Scene)((Button)event.getSource()).getScene()).getWindow();
        currentStage.close();

        loadMainCittadiniUI();
    }

}


