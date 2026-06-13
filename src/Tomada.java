import java.util.Map;

public class Tomada extends Dispositivo {
    public Tomada(){
        super();
    }

    public Tomada(String id, String marca, String modelo, double consumoHora){
        super(id, marca, modelo, consumoHora);
    }

    public Tomada(Tomada t){
        super(t);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("TOMADA: ").append(super.toString());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        return super.equals(o);
    }

    @Override
    public Tomada clone(){
        return new Tomada(this);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public double getConsumo() {
        if (super.getEstado()) {
            return super.getConsumoHora(); // Se está ON, consome a base
        } else {
            return 0.0; // Se está OFF, não consome nada
        }
    }

    @Override
    public void aplicarValorExtra(int valor) {
        // tomada simples não tem valor extra configurável
    }

    @Override
    public void configurar(Map<String, Object> params) {
        // Tomada simples não tem parâmetros de configuração
    }
}
