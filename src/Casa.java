import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Casa implements Serializable{
    private String id;
    private String morada;
    private Map<String, Divisao> divisoes;
    private Set<Utilizador> administradores; // podemos usar a instancia 
    private Set<Utilizador> usersNormais;
    private Map<String, Cenario> cenarios;

    public Casa(){
        this.id="";
        this.morada="";
        this.divisoes = new HashMap<>();
        this.administradores = new HashSet<>();
        this.usersNormais = new HashSet<>();
        this.cenarios = new HashMap<>();
    }

    public Casa(String i, String m){
        this.id = i;
        this.morada = m;
        this.divisoes = new HashMap<>();
        this.administradores = new HashSet<>();
        this.usersNormais = new HashSet<>();
        this.cenarios = new HashMap<>();
    }

    public Casa(Casa c){
        this.id = c.getId();
        this.morada = c.getMorada();
        this.divisoes = c.getDivisoes();
        this.administradores = c.getAdministradores();
        this.usersNormais = c.getUsersNormais();
        this.cenarios = c.getCenarios();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public Map<String, Divisao> getDivisoes() {
        return this.divisoes.entrySet().stream()
               .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
    }

    public void setDivisoes(Map<String, Divisao> divisoes) {
        this.divisoes = divisoes.entrySet().stream()
                      .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
    }

    public Set<Utilizador> getAdministradores() {
        return this.administradores.stream()
                   .map(Utilizador::clone) // Devolve clones!
                   .collect(Collectors.toSet());
    }

    public Set<Utilizador> getUsersNormais() {
        return this.usersNormais.stream()
                   .map(Utilizador::clone) // Devolve clones!
                   .collect(Collectors.toSet());
    }

    public Map<String, Cenario> getCenarios(){
        return this.cenarios.entrySet().stream()
               .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
    }
    
    // setters de administradores e usersNormais nao sao necessarios, porque estes so podem ser atualizados com os metodos add/remove


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.id).append(" | ");
        sb.append(this.morada).append(" | ");
        this.divisoes.values().forEach(d -> sb.append(d.toString()).append(" | "));
        this.administradores.forEach(a -> sb.append("Admin: ").append(a.toString()).append(" | "));
        this.usersNormais.forEach(u -> sb.append("User: ").append(u.toString()).append(" | "));
        this.cenarios.values().forEach(d -> sb.append(d.toString()).append(" | "));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Casa c = (Casa) o;

        return this.id.equals(c.getId()) &&
               this.morada.equals(c.getMorada()) &&
               this.divisoes.equals(c.getDivisoes()) &&
               this.administradores.equals(c.getAdministradores()) &&
               this.usersNormais.equals(c.getUsersNormais()) &&
               this.cenarios.equals(c.getCenarios());
    }

    @Override
    public Casa clone(){
        return new Casa(this);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 37 * hash + this.id.hashCode();
        hash = 37 * hash + this.morada.hashCode();
        hash = 37 * hash + this.divisoes.hashCode();
        hash = 37 * hash + this.administradores.hashCode();
        hash = 37 * hash + this.usersNormais.hashCode();
        hash = 37 * hash + this.cenarios.hashCode();
        return hash;
    }

    // METODOS

    public void addAdmin(Utilizador u){
        this.administradores.add(u.clone());
    }

    public void removeAdmin(String nif){
    if (this.administradores.size() <= 1 && this.isAdmin(nif)) {
        throw new IllegalStateException("Impossível remover. A casa tem de ter pelo menos um administrador.");
    }
    this.administradores.removeIf(u -> u.getNif().equals(nif));
    }

    public void addUser(Utilizador u){
        this.usersNormais.add(u.clone());
    }

    public void removeUser(String nif){
        this.usersNormais.removeIf(u -> u.getNif().equals(nif));
    }

    public boolean isAdmin(String nif) { 
        return this.administradores.stream()
                   .anyMatch(u -> u.getNif().equals(nif));
    }

    // Verifica especificamente se é apenas um utilizador comum
    public boolean isUserNormal(String nif) {
        return this.usersNormais.stream()
                   .anyMatch(u -> u.getNif().equals(nif));
    }

    public boolean temAcesso(String nif) {
        // Se for admin OU se for utilizador normal, pode entrar e mexer nas coisas!
        return this.isAdmin(nif) || this.isUserNormal(nif);
    }

    public void addDivisao(String nome){
        // 1. Verifica se já existe
        if (this.divisoes.containsKey(nome)) {
            throw new IllegalArgumentException("A divisão '" + nome + "' já existe nesta casa.");
        }
        
        // 2. Se não existir, adiciona normalmente
        this.divisoes.put(nome, new Divisao(nome));
    }

    public void addDispositivoDivisao(String nomeDivisao, Dispositivo d){
        Divisao div = this.divisoes.get(nomeDivisao);
        if (div != null){
            div.addDispositivo(d);
        }
    }

    public boolean existeDispositivoNaCasa(String idDisp) {
        for (Divisao d : this.divisoes.values()) {
            if (d.getDispositivos().containsKey(idDisp)) {
                return true; // Encontrou o ID numa das divisões!
            }
        }
        return false; // O ID está livre
    }

    public List<Dispositivo> getTodosDispositivos() {
        return this.divisoes.values().stream()
                   .flatMap(d -> d.getDispositivos().values().stream())
                   .collect(Collectors.toList());
    }

    public double getConsumoTotalDaCasa() {
        return this.getTodosDispositivos().stream()
                   .mapToDouble(Dispositivo::getConsumoTotal) // Vai a cada dispositivo e chama d.getConsumoTotal()
                   .sum(); 
    }


    public void alterarEstadoDispositivo(String idDispositivo, boolean ligar) throws IllegalArgumentException {
        // Percorre as divisões da casa
        for (Divisao div : this.divisoes.values()) {
            // Manda a divisão tentar ligar. Se der true, acabou o trabalho!
            if (div.alterarEstadoDispositivo(idDispositivo, ligar)) {
                return; 
            }
        }
        throw new IllegalArgumentException("Dispositivo '" + idDispositivo + "' não encontrado.");
    }

    public void avancarTempo(double horas) {
        // Percorre as divisões  e manda-as atualizar o consumo
        for (Divisao divReal : this.divisoes.values()) {
            divReal.avancarTempo(horas);
        }
    }

   public void configurarDispositivo(String idDispositivo, Map<String, Object> params) {
        for (Divisao divReal : this.divisoes.values()) {
            if (divReal.configurarDispositivo(idDispositivo, params)) return;
        }
        throw new IllegalArgumentException("Dispositivo '" + idDispositivo + "' não encontrado.");
    }


    // CENARIOS 
    public void addCenario(Cenario c) {
        this.cenarios.put(c.getNome(), c.clone());
    }

    // Aplica instrucao a instrucao...
    public void ativarCenario(String nomeCenario) throws IllegalArgumentException {
        Cenario c = this.cenarios.get(nomeCenario);
        if (c == null) {
            throw new IllegalArgumentException("O cenário '" + nomeCenario + "' não existe.");
        }

        // Percorre todas as instruções guardadas no cenário
        for (Map.Entry<String, Comando> entry : c.getInstrucoes().entrySet()) {
            String idDisp = entry.getKey();
            Comando comando = entry.getValue();

            // Pede às divisões para aplicarem o comando!
            for (Divisao d : this.divisoes.values()) {
                if (d.temDispositivo(idDisp)) {
                    d.aplicarComando(idDisp, comando);
                    break; // Já encontrou e aplicou, passa para a próxima instrução do cenário
                }
            }
        }
    }
}   
