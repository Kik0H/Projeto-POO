
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cenario implements Serializable{
    private String nome;
    private Map <String, Comando> instrucoes;

    public Cenario(String nome){
        this.nome = nome;
        this.instrucoes = new HashMap<>();
    }

    public Cenario(Cenario c){
        this.nome = c.getNome();
        this.instrucoes = c.getInstrucoes();
    }

    public String getNome(){
        return this.nome;
    }

    public Map<String, Comando> getInstrucoes(){
        return this.instrucoes.entrySet().stream()
               .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
    }

    public void adicionarInstrucao(String idDispositivo, boolean ligar, Integer valorExtra) {
        this.instrucoes.put(idDispositivo, new Comando(ligar, valorExtra));
    }

    @Override
    public Cenario clone() {
        return new Cenario(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Cenario c = (Cenario) o;
        return this.nome.equals(c.getNome()) &&
               this.instrucoes.equals(c.getInstrucoes());   
    }

    @Override
    public int hashCode() {
        int result = nome.hashCode();
        result = 31 * result + instrucoes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cenário: ").append(this.nome).append("\n");
        sb.append("Instruções:\n");
        for (Map.Entry<String, Comando> entry : this.instrucoes.entrySet()) {
            sb.append("Dispositivo ID: ").append(entry.getKey()).append(" -> ").append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }
}
