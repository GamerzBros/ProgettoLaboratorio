package centrivaccinali;

import java.util.Date;

/*Cristian Arcadi 745389 Varese
  David Poletti 746597 Varese
  Eros Marsichina 745299 Varese
  Tommaso Morosi 741227 Varese*/

/**
 * Classe container. Contiene le informazioni principali di un centro vaccinale.
 */
public class SingoloCentroVaccinale {
    /**
     * Nome del centro vaccinale
     */
    private String nome;
    /**
     * Indirizzo del centro vaccinale
     */
    private String indirizzo;
    /**
     * Tipologia del centro vaccinale
     */
    private String tipologia;

    /**
     * Costruisce un SingoloCentroVaccinale con nome e indirizzo
     * @param nome La stringa contente il nome del centro vaccinale
     * @param indirizzo La stringa contenente l'indirizzo del centro vaccinale
     */
    public SingoloCentroVaccinale(String nome,String indirizzo){
        this.nome = nome;
        this.indirizzo = indirizzo;
    }

    /**
     * Costruisce un SingoloCentroVaccinale con nome, indirizzo e tipologia
     * @param nome La stringa contente il nome del centro vaccinale
     * @param indirizzo La stringa contenente l'indirizzo del centro vaccinale
     * @param tipologia La stringa contenete la tipologia del centro vaccinale
     */
    public SingoloCentroVaccinale(String nome,String indirizzo,String tipologia){
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.tipologia=tipologia;
    }

    /**
     * Restituisce il nome del centro vaccinale
     * @return Una stringa contente il nome del centro vaccinale
     */
    public String getNome() {
        return nome;
    }

    /**
     * Cambia il nome del centro vaccinale
     * @param nome Una stringa contente il nuovo valore dell'attributo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce l'indirizzo del centro vaccinale
     * @return Una stringa contente l'indirizzo del centro vaccinale
     */
    public String getIndirizzo() {
        return indirizzo;
    }

    /**
     * Cambia l'indirizzo del centro vaccinale
     * @param indirizzo Una stringa contente il nuovo valore dell'attributo indirizzo
     */
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    /**
     * Restituisce la tipologia del centro vaccinale
     * @return Una stringa contente la tipologia del centro vaccinale
     */
    public String getTipologia() {
        return tipologia;
    }

    /**
     * Cambia la tipologia del centro vaccinale
     * @param tipologia Una stringa contente il nuovo valore dell'attributo tipologia
     */
    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    /**
     * Converte la classe in una stringa
     * @return Una stringa contente tutti i valori degli attributi della classe
     */
    @Override
    public String toString() {
        return "SingoloCentroVaccinale{" +
                "nome='" + nome + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", tipologia='" + tipologia + '\'' +
                '}';
    }

}
