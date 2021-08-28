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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class Cittadini implements EventHandler<ActionEvent> {
    public static final String PATH_TO_CENTRIVACCINALI_DATI = "data/CentriVaccinali.dati.txt";
    public static final String PRE_PATH_TO_EVENTI_AVVERSI="data/Vaccinati_";
    public static final String AFTER_PATH_TO_EVENTI_AVVERSI=".dati.txt";
    public static final String PATH_TO_CITTADINI_REGISTRATI_DATI = "data/Cittadini_Registrati.dati.txt";
    //TODO mettere il delimitatore come attributo
    private boolean isLogged=false;
    private int currentCentreID;
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
        currentCentreID = Integer.parseInt(source.getId());

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
                    String indirizzo = via+" "+" "+nome1+" "+num_civico+" "+comune+" "+provincia+" "+cap;
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

        Label lbl_centreName=(Label)newScene.lookup("#lbl_highlitedCentreName");
        Label lbl_centreAddress=(Label)newScene.lookup("#lbl_highlitedCentreAddress");
        Label lbl_centreType=(Label)newScene.lookup("#lbl_highlitedCentreType");

        loadCentreInfo(idCentro,lbl_centreName,lbl_centreAddress,lbl_centreType);

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


            String[] eventLines=leggiEventiAvversi();
            int firstEvent=0;
            int secondEvent=0;
            int thirdEvent=0;
            int forthEvent=0;
            int fifthEvent=0;
            int sixthEvent=0;
            String seventhEventDescription;
            int seventhEvent=0;

            for(i=0;i<eventLines.length;i++) {
                StringTokenizer tokenizer=new StringTokenizer(eventLines[i],";");

                firstEvent=firstEvent+(Integer.parseInt(tokenizer.nextToken()));
                secondEvent=secondEvent+(Integer.parseInt(tokenizer.nextToken()));
                thirdEvent=thirdEvent+(Integer.parseInt(tokenizer.nextToken()));
                forthEvent=forthEvent+(Integer.parseInt(tokenizer.nextToken()));
                fifthEvent=fifthEvent+(Integer.parseInt(tokenizer.nextToken()));
                sixthEvent=sixthEvent+(Integer.parseInt(tokenizer.nextToken()));

                if(tokenizer.hasMoreTokens()){
                    seventhEventDescription=tokenizer.nextToken();
                    seventhEvent=seventhEvent+(Integer.parseInt(tokenizer.nextToken()));
                }


                String[] singleEvents=new String[8];

                if (eventLines[0] != null) {
                    Scene currentScene = lbl_centreName.getScene();

                    Spinner spn_headache = (Spinner) currentScene.lookup("#spn_headache");
                    Spinner spn_fever = (Spinner) currentScene.lookup("#spn_fever");
                    Spinner spn_hurt = (Spinner) currentScene.lookup("#spn_hurt");
                    Spinner spn_linf = (Spinner) currentScene.lookup("#spn_linf");
                    Spinner spn_tac = (Spinner) currentScene.lookup("#spn_tac");
                    Spinner spn_crs = (Spinner) currentScene.lookup("#spn_crs");
                    TextField txt_other1 = (TextField) currentScene.lookup("#txt_other1");
                    Spinner spn_other1 = (Spinner) currentScene.lookup("#spn_other1");

                    spn_headache.setPromptText(String.valueOf(firstEvent));//evento1 = Mal di testa
                    spn_fever.setPromptText(String.valueOf(secondEvent)); //evento2 = Febbre
                    spn_hurt.setPromptText(String.valueOf(thirdEvent)); //evento3 = Dolori muscolari o articolari
                    spn_linf.setPromptText(String.valueOf(forthEvent)); //evento4 = Linfoadenopatia
                    spn_tac.setPromptText(String.valueOf(fifthEvent)); //evento5 = Tachicardia
                    spn_crs.setPromptText(String.valueOf(sixthEvent));//evento6 = Crisi ipertensiva

                    //TODO aggiungere gli eventi testuali nella nuopva grafica di tommy
                    //TODO di classe: mettere che se due eventi sono identici, fa la media
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void checkLogin(){
        if(isLogged){
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

            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void registerEventiAvversi(ActionEvent actionEvent) throws Exception {
        Scene currentScene=((Button)actionEvent.getSource()).getScene();

        Spinner spn_headache=(Spinner) currentScene.lookup("#spn_headache");
        Spinner spn_fever=(Spinner) currentScene.lookup("#spn_fever");
        Spinner spn_hurt=(Spinner) currentScene.lookup("#spn_hurt");
        Spinner spn_linf=(Spinner) currentScene.lookup("#spn_linf");
        Spinner spn_tac=(Spinner) currentScene.lookup("#spn_tac");
        Spinner spn_crs=(Spinner) currentScene.lookup("#spn_crs");
        TextField txt_other1=(TextField)currentScene.lookup("#txt_other1");
        Spinner spn_other1=(Spinner)currentScene.lookup("#spn_other1");


        String evento1 = spn_headache.getPromptText();//evento1 = Mal di testa
        String evento2 = spn_fever.getPromptText(); //evento2 = Febbre
        String evento3 = spn_hurt.getPromptText(); //evento3 = Dolori muscolari o articolari
        String evento4 = spn_linf.getPromptText(); //evento4 = Linfoadenopatia
        String evento5 = spn_tac.getPromptText(); //evento5 = Tachicardia
        String evento6 = spn_crs.getPromptText();//evento6 = Crisi ipertensiva
        String otherEvent=txt_other1.getText();
        String otherEventValue=spn_other1.getPromptText();

        FileWriter writer = new FileWriter(PRE_PATH_TO_EVENTI_AVVERSI+centriVaccinaliList.get(currentCentreID).getNome()+AFTER_PATH_TO_EVENTI_AVVERSI, true);
        BufferedWriter out = new BufferedWriter(writer);
        //String fileInput =  "Mal di Testa:" + evento1 + ";" + "Febbre:" + evento2 + ";" + "Dolori muscolari o articolari:" + evento3 + ";" + "Linfoadenopatia:" + evento4 + ";" + "Tachicardia:" + evento5 + ";" + "Crisi ipertensiva:" + evento6 + ";";

        String fileInput =currentCentreID + ";"+  evento1 + ";" + evento2 + ";" + evento3 + ";" + evento4 + ";" + evento5 + ";" + evento6;
        if(otherEvent.compareTo("")!=0){
            fileInput+=";"+otherEvent+";"+otherEventValue;
        }

        out.write(fileInput);
        out.newLine();
        out.flush();
        out.close();

        Stage stage=(Stage)spn_headache.getScene().getWindow();
        stage.close();
    }


    public String[] leggiEventiAvversi() throws Exception{
        centriVaccinaliList=getCentriVaccinaliFromFile();

        SingoloCentroVaccinale centroVaccinale=centriVaccinaliList.get(currentCentreID);

        try{
            FileReader fileReader=new FileReader(PRE_PATH_TO_EVENTI_AVVERSI+centroVaccinale.getNome()+AFTER_PATH_TO_EVENTI_AVVERSI);
            BufferedReader reader=new BufferedReader(fileReader);

            String line=reader.readLine();

            String[] eventLines=new String[8];
            int arrayIndex=0;
            while (line!=null){
                eventLines[arrayIndex]=line;
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
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("loginCittadino.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void loadRegisterCitizenUI(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("registraCittadino.fxml");
            loader.setLocation(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void registraCittadino(ActionEvent event) throws Exception {
        Scene currentScene=((Button)event.getSource()).getScene();
        String pwd = ((PasswordField)currentScene.lookup("#pswd_register")).getText();
        String user = ((TextField)currentScene.lookup("#txt_userRegister")).getText();

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8));
            pwd = toHexString(hash);
            System.out.println(pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileWriter writer = new FileWriter(PATH_TO_CITTADINI_REGISTRATI_DATI, true);
        BufferedWriter out = new BufferedWriter(writer);
        String scrivi = user + ";" + pwd;
        out.write(scrivi);
        out.newLine();
        out.close();
    }

    public void loggaCittadini(ActionEvent event) {
        Scene currentScene=((Button)event.getSource()).getScene();
        String user = ((TextField)currentScene.lookup("#txt_userLogin")).getText();
        String pwd = ((TextField)currentScene.lookup("#pswd_login")).getText();
        String user_temp;
        String pwd_temp;
        String[] parts;

        try {
            if (!user.equals("") && !pwd.equals("")) {
                FileReader fileReader=new FileReader(PATH_TO_CITTADINI_REGISTRATI_DATI);
                BufferedReader reader=new BufferedReader(fileReader);
                String line;

                while ((line=reader.readLine())!=null) {
                    parts = line.split(";");
                    user_temp = parts[0];
                    pwd_temp = parts[1];

                    if (user_temp.equals(user)) {
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                        pwd = toHexString(messageDigest.digest(pwd.getBytes(StandardCharsets.UTF_8)));
                        System.out.println(pwd);
                        if(pwd_temp.equals(pwd)) {
                            System.out.println("LOGGATO");
                            isLogged = true;

                            Stage currentStage=(Stage) ((Button)event.getSource()).getScene().getWindow();
                            currentStage.close();
                            loadRegistraEventiAvversiUI();
                        }
                    }
                    else {
                        //TODO mettere gli alert
                        Alert noUserAlert=new Alert(Alert.AlertType.WARNING);
                        noUserAlert.setTitle("Errore di login");
                        noUserAlert.setContentText("Utente non trovato!");
                        noUserAlert.show();
                        System.out.println("User inesistente, premere sul tasto 'register'");
                    }
                }
            } else {
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
        String search = ((TextField)((Button)event.getSource()).getScene().lookup("#txt_searchCenter")).getText().toLowerCase();

        for(int index=0;index<centriVaccinaliList.size();index++){
            SingoloCentroVaccinale tempCentre=centriVaccinaliList.get(index);
            String nome=tempCentre.getNome().toLowerCase();
            String indirizzo=tempCentre.getIndirizzo().toLowerCase();
            String tipologia=tempCentre.getTipologia().toLowerCase();
            //TODO aggiungere ricerche personalizzate
            if (nome.contains(search) || indirizzo.contains(search) || tipologia.contains(search)) {
                vector_search.add(new SingoloCentroVaccinale(nome, indirizzo, tipologia));
            }
        }
        creaVbox(vector_search);
    }

}


