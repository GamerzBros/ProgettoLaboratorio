package cittadini;

import centrivaccinali.CentriVaccinali;
import centrivaccinali.SingoloCentroVaccinale;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class Cittadini {
    private SingoloCittadino cittadinoLoggato;

    public Cittadini(){
        System.out.println("Creo la ui");
        CittadiniUI ui=new CittadiniUI();
        System.out.println("rip ui");
    }

    //TODO Marsio: creare il metodo registra eventi avversi


    //TODO creare registrazione e login cittadino
    public void registraCittadino(SingoloCittadino cittadino){

    }

    public void loggaCittadini() {

    }

    public static Vector<SingoloCentroVaccinale> getCentriVaccinaliFromFile(){
        Vector<SingoloCentroVaccinale> vector=new Vector<>();

        try {
            FileReader fileReader = new FileReader(CentriVaccinali.PATH_TO_CENTRIVACCINALI);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line=null;

            while ((line=bufferedReader.readLine())!=null){
                StringTokenizer st=new StringTokenizer(line,";");
                if(st.countTokens()==3){
                    String nome=st.nextToken();
                    String indirizzo=st.nextToken();
                    String tipologia=st.nextToken();

                    vector.add(new SingoloCentroVaccinale(nome,indirizzo,tipologia));
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return vector;

    }


}