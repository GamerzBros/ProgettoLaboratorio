package centrivaccinali;

import cittadini.Cittadini;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

/*Cristian Arcadi 745389 Varese
  David Poletti 746597 Varese
  Eros Marsichina 745299 Varese
  Tommaso Morosi 741227 Varese*/

//TODO eliminare questa classe

/**
 * Contiene tutte le UI e i metodi del portale CentriVaccinali. Contiene inoltre la UI che permette di seleziona il portale con cui interagire.
 */
public class CentriVaccinali {

    /**
     * Contiene il codice di avvio del programma.
     * @param args Gli argomenti di lancio passati via console al programma.
     * @throws Exception L'eccezione del metodo main
     */
    public static void main(String[] args) throws Exception {
         Application.launch();
    }

}
