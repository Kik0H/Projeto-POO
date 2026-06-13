import java.util.Map;

public class Coluna extends Dispositivo {
    public static final int MAX_VOLUME = 100;

    private int volume;
    private String canal; 


    public Coluna(){
        super();
        this.volume = 50;
        this.canal = "";
    }

    public Coluna(String id, String marca, String modelo, double consumoHora, String canal){
        super(id, marca, modelo, consumoHora);
        this.volume = 50;       // todas as colunas começam a 50
        this.canal = canal;
    }

    public Coluna(Coluna c){
        super(c);
        this.volume = c.getVolume();
        this.canal = c.getCanal();
    }

    public int getVolume(){
        return this.volume;
    }

    public String getCanal(){
        return this.canal;
    }
    public void setVolume(int volume){
        if (volume < 0) {
            this.volume = 0;
        } else if (volume > MAX_VOLUME) {
            this.volume = MAX_VOLUME;
        } else {
            this.volume = volume;
        }
    }

    public void setCanal(String canal){
        this.canal = canal;
    }

    public void volumeUp() {
        if (this.volume < MAX_VOLUME) {
            this.volume++;
        }
    }

    public void volumeDown() {
        if (this.volume > 0) {
            this.volume--;
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("COLUNA: ").append(super.toString())
        .append("Volume: ").append(this.volume).append("\n")
        .append("Canal: ").append(this.canal).append("\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;

        Coluna c = (Coluna) o;
        
        return this.volume == c.getVolume() &&
               this.canal.equals(c.getCanal());
    }

    @Override
    public Coluna clone(){
        return new Coluna(this);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + this.volume;
        hash = 37 * hash + this.canal.hashCode();
        return hash;
    }

    @Override
    public double getConsumo() {
        // O consumo aumenta ligeiramente com o volume.
        // Se o volume estiver no máximo (100%), consome o dobro do base.
        if (!super.getEstado()){
            return 0.0;
        } else {
            return super.getConsumoHora() + (super.getConsumoHora() * ((double) this.volume / MAX_VOLUME)); // consumo base + aumento proporcional ao volume
        }
    }

    @Override
    public void aplicarValorExtra(int valor) {
        this.setVolume(valor);
    }

    @Override
    public void configurar(Map<String, Object> params) {
        if (params.containsKey("volume")) {
            this.setVolume((Integer) params.get("volume"));
        }
        if (params.containsKey("canal")) {
            this.setCanal((String) params.get("canal"));
        }
    }
    
}
