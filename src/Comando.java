import java.io.Serializable;

public class Comando implements Serializable, Cloneable {
    private boolean ligar;
    private Integer valorExtra; // Pode ser volume, luminosidade, etc. Pode ser null.

    public Comando(boolean ligar, Integer valorExtra) {
        this.ligar = ligar;
        this.valorExtra = valorExtra;
    }

    public boolean isLigar() { return ligar; }
    public Integer getValorExtra() { return valorExtra; }

    @Override
    public Comando clone() {
        return new Comando(this.ligar, this.valorExtra);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Comando c = (Comando) o;
        return this.ligar == c.isLigar() &&
               ((this.valorExtra == null && c.getValorExtra() == null) ||
                (this.valorExtra != null && this.valorExtra.equals(c.getValorExtra())));
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(ligar);
        result = 31 * result + (valorExtra == null ? 0 : valorExtra.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Comando: ").append(ligar ? "Ligar" : "Desligar");
        if (valorExtra != null) {
            sb.append(", Valor Extra: ").append(valorExtra);
        }
        return sb.toString();
    }

    
}