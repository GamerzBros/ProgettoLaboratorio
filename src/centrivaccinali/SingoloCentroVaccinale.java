package centrivaccinali;

import java.util.Date;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class SingoloCentroVaccinale {
    //TODO Definire gli attributi dei Centri vaccinali
    private String nome;
    private String indirizzo;
    private String tipologia;

    public SingoloCentroVaccinale(String nome,String indirizzo){
        this.nome = nome;
        this.indirizzo = indirizzo;
    }

    public SingoloCentroVaccinale(String nome,String indirizzo,String tipologia){
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.tipologia=tipologia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    @Override
    public String toString() {
        return "SingoloCentroVaccinale{" +
                "nome='" + nome + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", tipologia='" + tipologia + '\'' +
                '}';
    }

}
