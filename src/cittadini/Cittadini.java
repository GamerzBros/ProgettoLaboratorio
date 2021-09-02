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

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class Cittadini implements EventHandler<ActionEvent> {
    public static final String PATH_TO_CENTRIVACCINALI_DATI = "data/CentriVaccinali.dati.txt";
    public static final String PRE_PATH_TO_EVENTI_AVVERSI="data/Vaccinati_";
    public static final String AFTER_PATH_TO_EVENTI_AVVERSI=".dati.txt";
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI = "data/Cittadini_Registrati.dati.txt";
    public static final String LINE_TYPE_PERSON ="V";
    public static final String LINE_TYPE_EVENT ="E";
    //TODO mettere il delimitatore come attributo
    private boolean isLogged=false;
    private String currentUser;
    private String currentCenter;
    private Vector<SingoloCentroVaccinale> centriVaccinaliList=new Vector<>();
    @FXML
    private ScrollPane scrollPane_CentriVaccinali;


    public void loadMainCittadiniUI() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("mainCittadini.fxml");
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
        int currentCentreID = Integer.parseInt(source.getId());

        try {
            loadVisualizzatoreCentroVaccinale(currentCentreID);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

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


    public void loadVisualizzatoreCentroVaccinale(int idCentro) throws IOException {
        FXMLLoader loader=new FXMLLoader();
        URL url=getClass().getResource("visualizzazioneCentroVaccinale.fxml");
        loader.setLocation(url);
        Parent root=loader.load();

        Scene newScene=new Scene(root);

        Stage currentStage=(Stage)scrollPane_CentriVaccinali.getScene().getWindow();
        currentStage.setScene(newScene);

        Label lbl_centreName=(Label)newScene.lookup("#lbl_highlitedCenterName");
        Label lbl_centreAddress=(Label)newScene.lookup("#lbl_highlitedCenterAddress");
        Label lbl_centreType=(Label)newScene.lookup("#lbl_highlitedCenterType");

        loadCentreInfo(idCentro,lbl_centreName,lbl_centreAddress,lbl_centreType);

        String[] userData=new String[1];
        userData[0]=currentCenter;
        newScene.setUserData(userData);

        currentStage.show();
    }

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
            String address=stringTokenizer.nextToken();
            String type=stringTokenizer.nextToken();

            lbl_centreName.setText(name);
            lbl_centreAddress.setText(address);
            lbl_centreType.setText(type);

            currentCenter=name;

            Vector<String> eventLines=leggiEventiAvversi(idCentro);
            int[] singleEvents=new int[6];
            Vector<String> otherEventsText=new Vector<>();
            Vector<Integer> otherEventsValues=new Vector<>();

            for(i=0;i<eventLines.size();i++) {
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

            ScrollPane scrollPane_otherEvents=(ScrollPane) currentScene.lookup("#scrollPane_otherEvents");
            VBox vbox=new VBox();
            scrollPane_otherEvents.setContent(vbox);

            for(i=0;i<otherEventsText.size();i++) {
                Pane vboxContent = new Pane();

                Label lbl_otherEventText=new Label(otherEventsText.get(i));
                lbl_otherEventText.setFont(Font.font("Franklin Gothic Medium",14));
                lbl_otherEventText.setMinWidth(800);
                lbl_otherEventText.setMinHeight(30);

                Label lbl_otherEventValue=new Label(String.valueOf(otherEventsValues.get(i)));
                lbl_otherEventValue.setMinWidth(30);
                lbl_otherEventValue.setMinHeight(30);
                lbl_otherEventValue.setLayoutX(800);

                vboxContent.getChildren().add(lbl_otherEventText);
                vboxContent.getChildren().add(lbl_otherEventValue);

                vbox.getChildren().add(vboxContent);

            }

            //TODO aggiungere gli eventi testuali nella nuova grafica di tommy
            //TODO di classe: mettere che se due eventi sono identici, fa la media


        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void checkLogin(ActionEvent event){
        String[] userData=(String[]) ((Button)event.getSource()).getScene().getUserData();
        currentCenter=userData[0];

        if(userData.length>1){
            currentUser=userData[1];
            loadRegistraEventiAvversiUI();
        }
        else{
            loadLoginUI();
        }
    }

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

            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void registerEventiAvversi(ActionEvent actionEvent) throws Exception {
        Scene currentScene=((Button)actionEvent.getSource()).getScene();

        String[] userData=(String[]) currentScene.getUserData();
        currentCenter=userData[0];
        currentUser=userData[1];

        Spinner<Integer> spn_headache=(Spinner<Integer>) currentScene.lookup("#spn_headache");
        Spinner<Integer> spn_fever=(Spinner<Integer>) currentScene.lookup("#spn_fever");
        Spinner<Integer> spn_hurt=(Spinner<Integer>) currentScene.lookup("#spn_hurt");
        Spinner<Integer> spn_linf=(Spinner<Integer>) currentScene.lookup("#spn_linf");
        Spinner<Integer> spn_tac=(Spinner<Integer>) currentScene.lookup("#spn_tac");
        Spinner<Integer> spn_crs=(Spinner<Integer>) currentScene.lookup("#spn_crs");
        TextField txt_other1=(TextField)currentScene.lookup("#txt_other");
        Spinner<Integer> spn_other1=(Spinner<Integer>)currentScene.lookup("#spn_other");


        int evento1 = spn_headache.getValue();//evento1 = Mal di testa
        int evento2 = spn_fever.getValue(); //evento2 = Febbre
        int evento3 = spn_hurt.getValue(); //evento3 = Dolori muscolari o articolari
        int evento4 = spn_linf.getValue(); //evento4 = Linfoadenopatia
        int evento5 = spn_tac.getValue(); //evento5 = Tachicardia
        int evento6 = spn_crs.getValue();//evento6 = Crisi ipertensiva
        String otherEvent=txt_other1.getText();
        int otherEventValue=spn_other1.getValue();

        FileReader reader=new FileReader(PRE_PATH_TO_EVENTI_AVVERSI+currentCenter+AFTER_PATH_TO_EVENTI_AVVERSI);
        BufferedReader in=new BufferedReader(reader);
        boolean authorized=false;
        boolean alreadyIn=false;
        String line;

        while (!authorized&&!alreadyIn&&(line=in.readLine())!=null){
            String[] data=line.split(";");
            if(data[0].equals(LINE_TYPE_PERSON)&&data[3].equals(currentUser)){
                authorized=true;
            }
            else if(data[0].equals(LINE_TYPE_EVENT)&&data[3].equals(currentUser)){
                alreadyIn=true;
            }
        }
        if(authorized) {

            FileWriter writer = new FileWriter(PRE_PATH_TO_EVENTI_AVVERSI + currentCenter+ AFTER_PATH_TO_EVENTI_AVVERSI, true);
            BufferedWriter out = new BufferedWriter(writer);
            //String fileInput = "Mal di Testa:" + evento1 + ";" + "Febbre:" + evento2 + ";" + "Dolori muscolari o articolari:" + evento3 + ";" + "Linfoadenopatia:" + evento4 + ";" + "Tachicardia:" + evento5 + ";" + "Crisi ipertensiva:" + evento6 + ";";

            String fileInput = LINE_TYPE_EVENT + ";" + currentCenter + ";" + currentUser + ";" + evento1 + ";" + evento2 + ";" + evento3 + ";" + evento4 + ";" + evento5 + ";" + evento6;
            if(otherEvent.compareTo("")!=0) {
                fileInput += ";" + otherEvent + ";" + otherEventValue;
            }

            out.write(fileInput);
            out.newLine();
            out.flush();
            out.close();
        }
        else if(alreadyIn){
            Alert alertAlreadyIn=new Alert(Alert.AlertType.ERROR);
            alertAlreadyIn.setTitle("Eventi già inseriti");
            alertAlreadyIn.setContentText("L'utente ha già inserito una volta degli eventi avversi presso il centro attuale");
            alertAlreadyIn.showAndWait();
        }
        else{
            Alert alertNoPermission=new Alert(Alert.AlertType.ERROR);
            alertNoPermission.setTitle("Utente non autorizzato");
            alertNoPermission.setContentText("Non sei stato vaccinato presso il centro selezionato!");
            alertNoPermission.showAndWait();
        }

        Stage stage=(Stage)currentScene.getWindow();
        stage.close();
    }


    public Vector<String> leggiEventiAvversi(int currentCentreID) throws Exception{
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

    public void loadLoginUI(){
        System.out.println(currentCenter);
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("loginCittadino.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            scene.setUserData(currentCenter);

            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void loadRegisterCitizenUI(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("nuovoCittadino.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);

            scene.setUserData(currentCenter);

            stage.show();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void registraCittadino(ActionEvent event) throws Exception {
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

                currentCenter=(String)currentScene.getUserData();

                currentUser=userCF;

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

                            currentCenter=(String)currentScene.getUserData();

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
                //TODO mettere gli alert
                System.out.println("Inserire dei dati");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    //TODO: Marsio: implementare ricerca centro vaccinale


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

    public void onAnnullaButtonClicked(ActionEvent event){
        Scene currentScene=((Button) event.getSource()).getScene();
        Stage currentStage=(Stage)currentScene.getWindow();
        currentStage.close();
    }

}


