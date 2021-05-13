package cittadini;

import java.util.Date;

//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class SingoloCittadino {
    //TODO Definire gli attributi dei cittadini
    private String nome;
    private String cognome;
    private String codice_fiscale;
    private Date dataVaccinazione;
    private String tipoVaccino; //TODO creare un enum per il tipo di vaccino (Pfizer, AstraZeneca, Moderna, J&J)
    private int idVaccino; //16 bit


    public SingoloCittadino(String nome, String cognome, String codice_fiscale){
        this.nome=nome;
        this.cognome=cognome;
        this.codice_fiscale=codice_fiscale;
    }
    public int getIdVaccino() {
        return idVaccino;
    }

    public void setIdVaccino(int idVaccino) {
        this.idVaccino = idVaccino;
    }

    public Date getDataVaccinazione() {
        return dataVaccinazione;
    }

    public void setDataVaccinazione(Date dataVaccinazione) {
        this.dataVaccinazione = dataVaccinazione;
    }

    public String getTipoVaccino() {
        return tipoVaccino;
    }

    public void setTipoVaccino(String tipoVaccino) {
        this.tipoVaccino = tipoVaccino;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodice_fiscale() {
        return codice_fiscale;
    }

    public void setCodice_fiscale(String codice_fiscale) {
        this.codice_fiscale = codice_fiscale;
    }

    @Override
    public String toString() {
        return "SingoloCittadino{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", codice_fiscale='" + codice_fiscale + '\'' +
                '}';
    }
}
