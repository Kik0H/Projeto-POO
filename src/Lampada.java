import java.util.Map;

public class Lampada extends Dispositivo {
    public enum Modo {
        NORMAL,
        ECO
    }

    private double luminosidade;
    private int cor;        // 2700K a 4000K
    private Modo modo;

    public Lampada(){
        super();
        this.luminosidade = 100;
        this.cor = 2700;
        this.modo = Modo.NORMAL;
    }

    public Lampada(String id, String marca, String modelo, double consumoHora, int cor, double luminosidade, Modo modo){
        super(id, marca, modelo, consumoHora);
        this.luminosidade = luminosidade;
        this.cor = cor;
        this.modo = modo;
    }

    public Lampada(Lampada l){
        super(l);
        this.luminosidade = l.getLuminosidade();
        this.cor = l.getCor();
        this.modo = l.getModo();
    }

    public double getLuminosidade(){
        return this.luminosidade;
    }

    public Modo getModo(){
        return this.modo;
    }

    public int getCor(){
        return this.cor;
    }

    public void setCor(int cor){        // TODAS AS LAMPADAS MUDAM DE COR 
        if (cor >= 2700 && cor <= 4000) {
            this.cor = cor;
        }
    }

    public void setLuminosidade(double luminosidade){
        if (luminosidade >= 0 && luminosidade <= 100) {
            this.luminosidade = luminosidade;
        }
    }

    public void setModoEco(){
        super.turnON();
        this.modo = Modo.ECO;
    }

    public void setModoNormal(){
        super.turnON();
        this.modo = Modo.NORMAL;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("Luminosidade: ").append(this.luminosidade).append("\n");
        sb.append("Cor: ").append(this.cor).append("K\n");
        sb.append("Modo: ").append(this.modo).append("\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Lampada l = (Lampada) o;
        return this.luminosidade == l.getLuminosidade() &&
               this.cor == l.getCor() &&
               this.modo == l.getModo();
    }

    @Override
    public Lampada clone(){
        return new Lampada(this);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp = Double.doubleToLongBits(luminosidade);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        result = 37 * result + (modo != null ? modo.hashCode() : 0);
        result = 37 * result + cor;
        return result;
    }

    @Override
    public double getConsumo(){
        if (!super.getEstado()) {       // se nao tiver ligado
            return 0.0;
        }

        double consumoAtual = super.getConsumoHora() * (this.luminosidade / 100.0); // consumo proporcional à luminosidade

        if (this.modo == Modo.ECO) {
            consumoAtual *= 0.5; // reduz o consumo em 50% no modo ECO
        }

        return consumoAtual;
    }

    @Override
    public void aplicarValorExtra(int valor) {
        this.setLuminosidade(valor);
    }

    @Override
    public void configurar(Map<String, Object> params) {
        if (params.containsKey("luminosidade")) {
            this.setLuminosidade((Double) params.get("luminosidade"));
        }
        if (params.containsKey("modoEco")) {
            boolean eco = (Boolean) params.get("modoEco");
            if (eco) this.setModoEco();
            else this.setModoNormal();
        }
    }
    
}
