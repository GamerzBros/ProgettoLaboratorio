package cittadini;
//TODO METTERE NOME COGNOME MATRICOLA SEDE
public class SingoloCittadino {
    //TODO Definire gli attributi dei cittadini
    private String nome;
    private String cognome;
    private String codice_fiscale;


    public SingoloCittadino(String nome, String cognome, String codice_fiscale){
        this.nome=nome;
        this.cognome=cognome;
        this.codice_fiscale=codice_fiscale;
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
