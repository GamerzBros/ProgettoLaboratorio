package cittadini;

import java.io.Serializable;

/**
 * Classe container. Contiene gli eventi avversi registrati da un cittadino
 */
public class EventiAvversi implements Serializable {
    private int maleTesta;

    private int febbre;

    private int doloriMuscolari;

    private int linfoadenopatia;

    private int tachicardia;

    private int crisiIpertensiva;

    private String otherSimptoms;

    public EventiAvversi(int maleTesta, int febbre, int doloriMuscolari, int linfoadenopatia, int tachicardia, int crisiIpertensiva, String otherSimptoms) {
        this.maleTesta = maleTesta;
        this.febbre = febbre;
        this.doloriMuscolari = doloriMuscolari;
        this.linfoadenopatia = linfoadenopatia;
        this.tachicardia = tachicardia;
        this.crisiIpertensiva = crisiIpertensiva;
        this.otherSimptoms = otherSimptoms;
    }

    public int getMaleTesta() {
        return maleTesta;
    }

    public void setMaleTesta(int maleTesta) {
        this.maleTesta = maleTesta;
    }

    public int getFebbre() {
        return febbre;
    }

    public void setFebbre(int febbre) {
        this.febbre = febbre;
    }

    public int getDoloriMuscolari() {
        return doloriMuscolari;
    }

    public void setDoloriMuscolari(int doloriMuscolari) {
        this.doloriMuscolari = doloriMuscolari;
    }

    public int getLinfoadenopatia() {
        return linfoadenopatia;
    }

    public void setLinfoadenopatia(int linfoadenopatia) {
        this.linfoadenopatia = linfoadenopatia;
    }

    public int getTachicardia() {
        return tachicardia;
    }

    public void setTachicardia(int tachicardia) {
        this.tachicardia = tachicardia;
    }

    public int getCrisiIpertensiva() {
        return crisiIpertensiva;
    }

    public void setCrisiIpertensiva(int crisiIpertensiva) {
        this.crisiIpertensiva = crisiIpertensiva;
    }

    public String getOtherSimptoms() {
        return otherSimptoms;
    }

    public void setOtherSimptoms(String otherSimptoms) {
        this.otherSimptoms = otherSimptoms;
    }
}
