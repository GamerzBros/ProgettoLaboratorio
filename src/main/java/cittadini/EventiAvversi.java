package cittadini;

import java.io.Serializable;

/**
 * Classe container. Contiene gli eventi avversi registrati da un cittadino
 */
public class EventiAvversi implements Serializable {
    /**
     * Evento avverso mal di testa
     */
    private int maleTesta;
    /**
     * Evento avverso febbre
     */
    private int febbre;
    /**
     * Evento avverso dolori muscolari
     */
    private int doloriMuscolari;
    /**
     * Evento avverso linfoadenopatia
     */
    private int linfoadenopatia;
    /**
     * Evento avverso tachicardia
     */
    private int tachicardia;
    /**
     * Evento avverso crisi ipertensiva
     */
    private int crisiIpertensiva;
    /**
     * Campo utilizzato per poter inserire la descrizione testuale (di massimo 256 caratteri) di un ulteriore evento avverso (non listato) riscontrato dall'utente
     */
    private String otherSymptoms;

    /**
     * Costruttore principale della classe container EventiAvversi che riceve come parametri tutti i valori degli eventi avversi
     * @param maleTesta L'evento avverso mal di testa
     * @param febbre L'evento avverso febbre
     * @param doloriMuscolari L'evento avverso dolori muscolari
     * @param linfoadenopatia L'evento avverso linfoadnopatia
     * @param tachicardia L'evento avverso tachicardia
     * @param crisiIpertensiva L'evento avverso crisi ipertensiva
     * @param otherSymptoms Il campo testuale per inserire ulteriori eventi avversi non listati
     */
    public EventiAvversi(int maleTesta, int febbre, int doloriMuscolari, int linfoadenopatia, int tachicardia, int crisiIpertensiva, String otherSymptoms) {
        this.maleTesta = maleTesta;
        this.febbre = febbre;
        this.doloriMuscolari = doloriMuscolari;
        this.linfoadenopatia = linfoadenopatia;
        this.tachicardia = tachicardia;
        this.crisiIpertensiva = crisiIpertensiva;
        this.otherSymptoms = otherSymptoms;
    }

    /**
     * Getter del campo male testa
     * @return Il valore dell'attributo maleTesta
     */
    public int getMaleTesta() {
        return maleTesta;
    }
    /**
     * Setter del campo male testa
     * @param maleTesta Il nuovo valore dell'attributo maleTesta
     */
    public void setMaleTesta(int maleTesta) {
        this.maleTesta = maleTesta;
    }
    /**
     * Getter del campo febbre
     * @return Il valore dell'attributo febbre
     */
    public int getFebbre() {
        return febbre;
    }
    /**
     * Setter del campo febbre
     * @param febbre Il nuovo valore dell'attributo febbre
     */
    public void setFebbre(int febbre) {
        this.febbre = febbre;
    }
    /**
     * Getter del campo dolori muscolari
     * @return Il valore dell'attributo doloriMuscolari
     */
    public int getDoloriMuscolari() {
        return doloriMuscolari;
    }
    /**
     * Setter del campo dolori muscolari
     * @param doloriMuscolari Il nuovo valore dell'attributo doloriMuscolare
     */
    public void setDoloriMuscolari(int doloriMuscolari) {
        this.doloriMuscolari = doloriMuscolari;
    }
    /**
     * Getter del campo linfoadenopatia
     * @return Il valore dell'attributo linfoadenopatia
     */
    public int getLinfoadenopatia() {
        return linfoadenopatia;
    }
    /**
     * Setter del campo linfoadenopatia
     * @param linfoadenopatia Il nuovo valore dell'attributo linfoadenopatia
     */
    public void setLinfoadenopatia(int linfoadenopatia) {
        this.linfoadenopatia = linfoadenopatia;
    }
    /**
     * Getter del campo tachicardia
     * @return Il valore dell'attributo tachicardia
     */
    public int getTachicardia() {
        return tachicardia;
    }
    /**
     * Setter del campo tachicardia
     * @param tachicardia Il nuovo valore dell'attributo tachicardia
     */
    public void setTachicardia(int tachicardia) {
        this.tachicardia = tachicardia;
    }

    /**
     * Getter del campo crisi ipertensiva
     * @return Il valore dell'attributo crisiIpertensiva
     */
    public int getCrisiIpertensiva() {
        return crisiIpertensiva;
    }
    /**
     * Setter del campo crisi ipertensiva
     * @param crisiIpertensiva Il nuovo valore dell'attributo crisiIpertensiva
     */
    public void setCrisiIpertensiva(int crisiIpertensiva) {
        this.crisiIpertensiva = crisiIpertensiva;
    }
    /**
     * Getter del campo contenente ulteriori sintomi
     * @return Il valore dell'attributo otherSimptoms
     */
    public String getOtherSymptoms() {
        return otherSymptoms;
    }
    /**
     * Setter del campo contente ulteriori sintomi
     * @param otherSymptoms Il nuovo valore dell'attributo otherSymptoms
     */
    public void setOtherSymptoms(String otherSymptoms) {
        this.otherSymptoms = otherSymptoms;
    }
}
