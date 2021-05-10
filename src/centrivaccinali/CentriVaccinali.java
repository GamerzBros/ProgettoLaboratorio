package centrivaccinali;

import cittadini.Cittadini;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class CentriVaccinali {

//boia che branch
    public CentriVaccinali(){
        try {
            Parent root=FXMLLoader.load(getClass().getResource("SelectionUI.fxml"));

            Scene scene=new Scene(root);

            Stage stage=new Stage();

            stage.setTitle("Seleziona il modulo");
            stage.setScene(scene);
            stage.show();

            //scene.lookup();  per prendere un elemento grafico dato il suo ID

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registraCentroVaccinale(SingoloCentroVaccinale centroVaccinale){ //metodo per registrare i centri

    }

    public void cercaCentroVaccinale(String nomeCentroVaccinale){

    }

    public void cercaCentroVaccinale(String comune, String tipologia){  //TODO rivedere i tipi dei parametri

    }

    public void visualizzaInfoCentroVaccinale(){

    }

    public void inserisciEventiAvversi(Object eventoAvverso){  //TODO modificare i parametri

    }

    public void onCentriVaccinaliSelected(){
        new CentriVaccinaliUI();
    }

    public void onCittadiniSelected(){
        new Cittadini();
    }


    public static void main(String[] args){

    }

}
