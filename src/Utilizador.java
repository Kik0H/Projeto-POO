
import java.io.Serializable;

public class Utilizador implements Serializable{
    private String nif;
    private String nome;

    public Utilizador(){
        this.nif="";
        this.nome="";
    }

    public Utilizador(String n, String nom){
        this.nif = n;
        this.nome = nom;
    }

    public Utilizador(Utilizador u){
        this.nif = u.getNif();
        this.nome = u.getNome();
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("NIF: ").append(this.nif).append(", Nome: ").append(this.nome);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Utilizador u = (Utilizador) o;

        return this.nif.equals(u.getNif()) &&
               this.nome.equals(u.getNome());
    }

    @Override
    public Utilizador clone(){
        return new Utilizador(this);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 37 * hash + this.nif.hashCode();
        hash = 37 * hash + this.nome.hashCode();
        return hash;
    }
}