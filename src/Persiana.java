import java.util.Map;

public class Persiana extends Dispositivo {
    private int percentagemAbertura; // 0 = fechado, 100 = totalmente aberto

    public Persiana(){
        super();
        this.percentagemAbertura = 0; // inicialmente fechado
    }

    public Persiana(String id, String marca, String modelo, double consumo){
        super(id, marca, modelo, consumo);
        this.percentagemAbertura = 0;  // começa sempre fechado
    }

    public Persiana(Persiana pg){
        super(pg);
        this.percentagemAbertura = pg.getPercentagemAbertura();
    }

    public int getPercentagemAbertura(){
        return this.percentagemAbertura;
    }

    public void setPercentagemAbertura(int grauAbertura) {
        if (grauAbertura >= 0 && grauAbertura <= 100) {
            this.percentagemAbertura = grauAbertura;
        }
    }

    public void abrirTotalemente(){
        this.setPercentagemAbertura(100);
    }

    public void fecharTotalmente(){
        this.setPercentagemAbertura(0);
    }

    @Override
    public double getConsumo() {
        // só consome energia enquanto o motor está ON (a mover-se)
        // A posição em si (grau de abertura) não gasta energia se o motor estiver parado (OFF)
        if (super.getEstado()) {
            return super.getConsumoHora();
        } else {
            return 0.0;
        }
    }

    @Override
    public Persiana clone() {
        return new Persiana(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; 
        
        Persiana p = (Persiana) o;
        return this.percentagemAbertura == p.getPercentagemAbertura();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Persiana: ").append(super.toString());
        sb.append("Percentagem de Abertura: ").append(this.percentagemAbertura).append("%\n");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 37 * result + this.percentagemAbertura;
        return result;
    }

    @Override
    public void aplicarValorExtra(int valor) {
        this.setPercentagemAbertura(valor);
    }

    @Override
    public void configurar(Map<String, Object> params) {
        if (params.containsKey("percentagem")) {
            this.setPercentagemAbertura((Integer) params.get("percentagem"));
        }
    }
}

