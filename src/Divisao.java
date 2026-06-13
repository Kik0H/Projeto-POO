import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Divisao implements Serializable{
    private String nome;
    private Map<String, Dispositivo> dispositivos;

    public Divisao(){
        this.nome = "";
        this.dispositivos = new HashMap<>();
    }

    public Divisao(String nome){
        this.nome = nome;
        this.dispositivos = new HashMap<>();        // nao faz sentido ter um construtor a passar os dispositivos, começam vazios
    }

    public Divisao(Divisao d){
        this.nome = d.getNome();
        this.dispositivos = d.getDispositivos();
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Map<String, Dispositivo> getDispositivos(){
        return this.dispositivos.entrySet().stream()
               .collect(Collectors.toMap(
                    entry -> entry.getKey(), 
                    entry -> entry.getValue().clone()
               ));
    }

    public void setDispositivos(Map<String, Dispositivo> d){
        this.dispositivos = d.entrySet().stream()
               .collect(Collectors.toMap(
                    entry -> entry.getKey(),
                    entry -> entry.getValue().clone()
               ));
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.nome).append(" | ");
        this.dispositivos.values().forEach(d -> sb.append(d.toString()).append(" | "));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;

        Divisao d = (Divisao) o;

        return this.nome.equals(d.getNome()) &&
               this.dispositivos.equals(d.getDispositivos());

    }
    
    @Override
    public Divisao clone(){
        return new Divisao(this);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 37 * hash + this.nome.hashCode();
        hash = 37 * hash + this.dispositivos.hashCode();
        return hash;
    }

    // METODOS
    public void addDispositivo(Dispositivo d){
        this.dispositivos.put(d.getId(),d.clone());
    }

    public void removeDispositivo(String id){
        this.dispositivos.remove(id);
    }

    public Dispositivo getDispositivo(String id){
        Dispositivo d = this.dispositivos.get(id);
        return (d == null) ? null : d.clone();
    }



    public boolean alterarEstadoDispositivo(String idDispositivo, boolean estado) {
        if (this.dispositivos.containsKey(idDispositivo)) {
            Dispositivo disp = this.dispositivos.get(idDispositivo);
            if (estado) {
                disp.turnON();
            } else {
                disp.turnOFF();
            }
            return true; // Encontrou e resolveu!
        }
        return false; // Não está nesta divisão
    }       // PODEMOS PASSAR DIREITO CASA -> DISPOSITIVO EM VEZ DISTO! AFINAL NAO PODEMOS, AO FAZER getDIVISOES na casa iamos passar clone

    public void avancarTempo(double horas) {
        // Percorre os dispositivos e atualiza o consumo
        for (Dispositivo disp : this.dispositivos.values()) {
            disp.atualizarConsumo(horas);
        }
    }

    public void aplicarComando(String idDisp, Comando comando) {
        Dispositivo aparelho = this.dispositivos.get(idDisp);
        if (aparelho != null) {
            if (comando.isLigar()) {
                aparelho.turnON();
            } else {
                aparelho.turnOFF();
            }
            if (comando.getValorExtra() != null) {
                aparelho.aplicarValorExtra(comando.getValorExtra());
            }
        }
    }

    public boolean configurarDispositivo(String idDispositivo, Map<String, Object> params) {
        Dispositivo d = this.dispositivos.get(idDispositivo);
        if (d == null) return false;
        d.configurar(params);
        return true;
    }


    /**
     * Verifica se a divisão contém o dispositivo (para a Casa não ter de pedir o mapa).
     */
    public boolean temDispositivo(String idDisp) {
        return this.dispositivos.containsKey(idDisp);
    }


}


