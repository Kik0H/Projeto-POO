import java.io.Serializable;
import java.util.Map;

public abstract class Dispositivo implements Serializable{
    private String id;
    private String marca;
    private String modelo;
    private double consumoHora;     //consumo por hora se tiver ligado
    private boolean estado; // true ligado, false desligado

    private double consumoTotal;    //consumo total desde a criacao do dispositivo

    private int numeroAtivacoes;
    private double tempoTotalLigado;

    public Dispositivo(){
        this.id="";
        this.marca="";
        this.modelo="";
        this.consumoHora = 0.0;
        this.consumoTotal = 0.0;
        this.estado = false;
        this.numeroAtivacoes = 0;
        this.tempoTotalLigado = 0.0;
    }

    public Dispositivo(String id, String marca, String modelo, double consumo){
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.consumoHora = consumo;
        this.consumoTotal = 0.0;
        this.estado = false;
        this.numeroAtivacoes = 0;
        this.tempoTotalLigado = 0.0;
    }

    public Dispositivo(Dispositivo d){
        this.id = d.getId();
        this.marca = d.getMarca();
        this.modelo = d.getModelo();
        this.consumoHora = d.getConsumoHora();
        this.consumoTotal = d.getConsumoTotal();
        this.estado = d.getEstado();
        this.numeroAtivacoes = d.getNumeroAtivacoes();
        this.tempoTotalLigado = d.getTempoTotalLigado();
    }

    public String getId(){
        return this.id;
    }

    public String getMarca(){
        return this.marca;
    }

    public String getModelo(){
        return this.modelo;
    }

    public double getConsumoHora(){
        return this.consumoHora;
    }

    public boolean getEstado(){
        return this.estado;
    }

    public double getConsumoTotal(){
        return this.consumoTotal;
    }

    public int getNumeroAtivacoes() {
        return this.numeroAtivacoes;
    }

    public double getTempoTotalLigado() {
        return this.tempoTotalLigado;
    }

    // Nao vamos fazer setters para consumoTotal , porque estes so podem ser atualizados com o método atualizarConsumo() e resetConsumoPeriodo()

    public void setId(String id){
        this.id = id;
    }

    public void setMarca(String marca){
        this.marca = marca;
    }

    public void setModelo(String modelo){
        this.modelo = modelo;
    }

    public void setConsumo(double consumo){
        this.consumoHora = consumo;
    }

    public void setEstado(boolean estado){
        this.estado = estado;
    }
    

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(this.id).append("\n");
        sb.append("Marca: ").append(this.marca).append("\n");
        sb.append("Modelo: ").append(this.modelo).append("\n");
        sb.append("ConsumoHora: ").append(this.consumoHora).append("\n");
        sb.append("ConsumoTotal: ").append(this.consumoTotal).append("\n");
        sb.append("Estado: ").append(this.estado ? "Ligado" : "Desligado");
        sb.append("NumeroAtivacoes: ").append(this.numeroAtivacoes).append("\n");
        sb.append("TempoTotalLigado: ").append(this.tempoTotalLigado).append("\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;

        Dispositivo d = (Dispositivo) o;

        return this.id.equals(d.getId()) &&
               this.marca.equals(d.getMarca()) &&
               this.modelo.equals(d.getModelo()) &&
               Double.compare(d.getConsumoHora(), this.consumoHora) == 0 &&
               Double.compare(d.getConsumoTotal(), this.consumoTotal) == 0 &&
               this.estado == d.getEstado() &&
               this.numeroAtivacoes == d.getNumeroAtivacoes() &&
               Double.compare(d.getTempoTotalLigado(), this.tempoTotalLigado) == 0;
    }

    public abstract Dispositivo clone();

    @Override
    public int hashCode(){
        int hash = 7;

        hash = 37 * hash + this.id.hashCode();
        hash = 37 * hash + this.marca.hashCode();
        hash = 37 * hash + this.modelo.hashCode();

        long aux = Double.doubleToLongBits(this.consumoHora);
        hash = 37 * hash + (int)(aux ^ (aux >>> 32));

        long aux2 = Double.doubleToLongBits(this.consumoTotal);
        hash = 37 * hash + (int)(aux2 ^ (aux2 >>> 32));

        hash = 37 * hash + (this.estado ? 1 : 0);

        hash = 37 * hash + this.numeroAtivacoes;

        long aux4 = Double.doubleToLongBits(this.tempoTotalLigado);
        hash = 37 * hash + (int)(aux4 ^ (aux4 >>> 32));

        return hash;
    }

    public void turnON(){
        if (!this.estado) {
            this.estado=true;
            this.numeroAtivacoes++;
        }
    }

    public void turnOFF(){
        this.estado = false;
    }

    /**
     * aplica um valor numérico extra (volume, luminosidade, abertura...).
     * por defeito não faz nada, cada subclasse sobrepõe se necessário.
     */
    public void aplicarValorExtra(int valor) {
        // Tomada e outros sem valor extra não precisam de fazer nada
    }

    //configura o dispositivo com um conjunto de parâmetros.
    public abstract void configurar(Map<String, Object> params);

    public abstract double getConsumo(); //consumo atual, que depende do estado e de outras variáveis (ex: luminosidade da lampada)

    /**
     * O MOTOR (DomusControl) chama isto quando o utilizador avança o tempo simulado!
     */
    public void atualizarConsumo(double horasDecorridas) {
        // O this.getConsumo() chama a fórmula da Lâmpada (que vê a luminosidade e modo ECO)
        // ou da Tomada, ou do Portão... 
        double consumoNesteSaltoDeTempo = this.getConsumo() * horasDecorridas;
        
        this.consumoTotal += consumoNesteSaltoDeTempo;

        if (this.estado) {
            this.tempoTotalLigado += horasDecorridas; 
        }
    }
}
