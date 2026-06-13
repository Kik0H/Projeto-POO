import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DomusControl implements Serializable {
    private Map<String, Casa> casas;
    private Map<String, Utilizador> utilizadores;
    private LocalDateTime dataHoraAtual;

    public DomusControl(){
        this.casas = new HashMap<>();
        this.utilizadores = new HashMap<>();
        this.dataHoraAtual = LocalDateTime.now();
    }

    public DomusControl(Map<String, Casa> casas, Map<String, Utilizador> utilizadores){
        this.casas = casas.entrySet().stream()
                          .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
        this.utilizadores = utilizadores.entrySet().stream()
                                        .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
        this.dataHoraAtual = LocalDateTime.now();
    }

    public DomusControl(DomusControl dc){
        this.casas = dc.getCasas();
        this.utilizadores = dc.getUtilizadores();
        this.dataHoraAtual = dc.getDataHoraAtual();
    }

    public Map<String, Casa> getCasas() {
        return this.casas.entrySet().stream()
               .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
    }

    public void setCasas(Map<String, Casa> casas) {
        this.casas = casas.entrySet().stream()
                          .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
    }

    public Map<String, Utilizador> getUtilizadores() {
        return this.utilizadores.entrySet().stream()
               .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
    }

    public void setUtilizadores(Map<String, Utilizador> utilizadores) {
        this.utilizadores = utilizadores.entrySet().stream()
                                         .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().clone()));
    }

    public LocalDateTime getDataHoraAtual() {
        return this.dataHoraAtual;
    }

    public void setDataHoraAtual(LocalDateTime dataHoraAtual) {
        this.dataHoraAtual = dataHoraAtual;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("DOMUS CONTROL:\n");
        sb.append("Casas:\n");
        for (Casa c : this.casas.values()) {
            sb.append(c.toString()).append("\n");
        }
        sb.append("Utilizadores:\n");
        for (Utilizador u : this.utilizadores.values()) {
            sb.append(u.toString()).append("\n");
        }
        sb.append("Data/Hora Atual: ").append(this.dataHoraAtual.toString()).append("\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomusControl dc = (DomusControl) o;
        return this.casas.equals(dc.getCasas()) &&
               this.utilizadores.equals(dc.getUtilizadores()) &&
               this.dataHoraAtual.equals(dc.getDataHoraAtual());
    }

    @Override
    public DomusControl clone(){
        return new DomusControl(this);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.casas.hashCode();
        hash = 37 * hash + this.utilizadores.hashCode();
        hash = 37 * hash + this.dataHoraAtual.hashCode();
        return hash;
    }

    //METODOS DE LOGIN
    public void registarUtilizador(Utilizador u){
        if (this.utilizadores.containsKey(u.getNif())) {
            throw new IllegalArgumentException("Já existe um utilizador registado com o NIF " + u.getNif());
        }
        this.utilizadores.put(u.getNif(), u.clone());
    }

    public boolean existeUtilizador(String nif){
        return this.utilizadores.containsKey(nif);
    }

    public boolean temUtilizadoresRegistados() {
        return !this.utilizadores.isEmpty();
    }

    // METODOS DE GESTAO DE CASA 
    public void criarCasa(String nifCriador, String idCasa, String morada) throws IllegalArgumentException{
        Utilizador criador = this.utilizadores.get(nifCriador);
        if (criador == null) {
            throw new IllegalArgumentException("O utilizador com o NIF '" + nifCriador + "' não existe no sistema.");
        }

        if (this.casas.containsKey(idCasa)) {
            throw new IllegalArgumentException("Já existe uma casa com o ID " + idCasa);
        }

        Casa c = new Casa(idCasa, morada);
        c.addAdmin(criador);
        this.casas.put(idCasa, c);
    }

    public void criarDivisao(String nifCriador, String idCasa, String nomeDivisao) throws IllegalArgumentException, IllegalAccessException{
        Casa c = validarEObterCasaParaAdmin(nifCriador, idCasa);
        c.addDivisao(nomeDivisao);
    }

    public void adicionarDispositivo(String nif, String idCasa, String nomeDivisao, Dispositivo d) throws IllegalArgumentException, IllegalAccessException{
        Casa c = validarEObterCasaParaAdmin(nif, idCasa);

        if (!c.getDivisoes().containsKey(nomeDivisao)) {
            throw new IllegalArgumentException("A divisão '" + nomeDivisao + "' não existe nesta casa.");
        }

        if (c.existeDispositivoNaCasa(d.getId())) {
            throw new IllegalArgumentException("Instalação falhou: Já existe um dispositivo com o ID '" + d.getId() + "' nesta casa!");
        }

        c.addDispositivoDivisao(nomeDivisao, d);
    }

    public void ligarDispositivo(String nifUser, String idCasa, String idDispositivo) throws IllegalArgumentException, IllegalAccessException{
        Casa c = this.casas.get(idCasa);
        if (c == null) {
            throw new IllegalArgumentException("Casa não encontrada.");
        }

        if (!c.temAcesso(nifUser)) {
            throw new IllegalAccessException("Acesso Negado. Não tem permissões nesta casa.");
        }

        // Manda a Casa ligar o aparelho (a Casa delega na Divisão)
        c.alterarEstadoDispositivo(idDispositivo, true);
    }

    public void desligarDispositivo(String nifUser, String idCasa, String idDispositivo) throws IllegalArgumentException, IllegalAccessException {
        Casa c = this.casas.get(idCasa);
        if (c == null) throw new IllegalArgumentException("Casa não encontrada.");
        if (!c.temAcesso(nifUser)) throw new IllegalAccessException("Não tem permissão para interagir com esta casa.");

        // Manda a Casa desligar o dispositivo!
        c.alterarEstadoDispositivo(idDispositivo, false);
    }


    // ESTATISTICAS
    public Casa getCasaMaisGastadora() {
        return this.casas.values().stream()
               .max(Comparator.comparingDouble(Casa::getConsumoTotalDaCasa))
               .map(Casa::clone)
               .orElse(null);
    }

    public Casa getCasaMaisEconomica() {
        return this.casas.values().stream()
                // Usamos min() em vez de max()
                .min(Comparator.comparingDouble(Casa::getConsumoTotalDaCasa))
                .map(Casa::clone)
                .orElse(null);
    }

    // Os 3 dispositivos mais usados (por número de ativações) numa casa.
    public List<Dispositivo> getTop3DispositivosPorAtivacoes(String idCasa) {
        Casa c = this.casas.get(idCasa);
        if (c == null) return new ArrayList<>();

        return c.getTodosDispositivos().stream()
                // Ordena do maior para o menor (reversed) 
                .sorted(Comparator.comparingInt(Dispositivo::getNumeroAtivacoes).reversed())
                .limit(3)              // Corta a lista aos 3 primeiros
                .map(Dispositivo::clone) 
                .collect(Collectors.toList());
    }

    // Os 3 dispositivos mais usados (por tempo total ligado) numa casa.
    public List<Dispositivo> getTop3DispositivosPorTempo(String idCasa) {
        Casa c = this.casas.get(idCasa);
        if (c == null) return new ArrayList<>();

        return c.getTodosDispositivos().stream()
                // Ordena do maior para o menor usando o tempo
                .sorted(Comparator.comparingDouble(Dispositivo::getTempoTotalLigado).reversed())
                .limit(3)
                .map(Dispositivo::clone)
                .collect(Collectors.toList());
    }

    public List<String> getTop3DivisoesComMaisDispositivos() {
        // "Casa X - Divisão Y" -> Número de Dispositivos
        Map<String, Integer> contagem = new HashMap<>();

        // Percorre todas as casas do sistema
        for (Casa c : this.casas.values()) {
            // Percorre todas as divisões de cada casa
            for (Divisao d : c.getDivisoes().values()) {
                // Cria um identificador único para a divisão
                String identificador = "Casa: " + c.getId() + " | Divisão: " + d.getNome();
                // Guarda no mapa o identificador e o tamanho da lista de dispositivos
                contagem.put(identificador, d.getDispositivos().size());
            }
        }

        //  ordenar e devolver os 3 maiores
        return contagem.entrySet().stream()
                // Ordena pelos valores (número de dispositivos) de forma descendente (reversed)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(entry -> entry.getKey() + " (" + entry.getValue() + " dispositivos)")
                .collect(Collectors.toList());
    }

    public void avancarTempo(double horas) {
        this.dataHoraAtual = this.dataHoraAtual.plusMinutes((long)(horas * 60));
        
        // Manda as Casas atualizarem o tempo nas divisões
        for (Casa c : this.casas.values()) {
            c.avancarTempo(horas);
        }
    }

    /**
     * Promove um utilizador existente a Administrador de uma casa.
     */
    public void adicionarAdministradorCasa(String nifExecutor, String idCasa, String nifNovoAdmin) 
            throws IllegalArgumentException, IllegalAccessException {
        
        // Verifica se a casa existe e se quem está a executar o método é Admin (reaproveitando o teu método auxiliar)
        Casa c = validarEObterCasaParaAdmin(nifExecutor, idCasa);
        Utilizador novoAdmin = this.utilizadores.get(nifNovoAdmin);

        // Verifica se o novo utilizador existe mesmo na base de dados do sistema
        if (novoAdmin == null) {
            throw new IllegalArgumentException("O utilizador com o NIF '" + nifNovoAdmin + "' não está registado no DomusControl.");
        }

        // Verifica se por acaso a pessoa já não é admin
        if (c.isAdmin(nifNovoAdmin)) {
            throw new IllegalArgumentException("O utilizador '" + nifNovoAdmin + "' já é administrador desta casa.");
        }

        // Se passou os testes todos, a Casa promove o utilizador!
        c.addAdmin(novoAdmin);
    }

    /**
     * Remove um administrador de uma casa.
     */
    public void removerAdministradorCasa(String nifExecutor, String idCasa, String nifARemover) 
            throws IllegalArgumentException, IllegalAccessException {

        Casa c = validarEObterCasaParaAdmin(nifExecutor, idCasa);
        if (!c.isAdmin(nifARemover)) {
            throw new IllegalArgumentException("O utilizador '" + nifARemover + "' não é administrador desta casa.");
        }

        // Tenta remover (a classe Casa lança erro se for o último admin)
        try {
            c.removeAdmin(nifARemover);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Dá acesso a um utilizador normal a uma casa.
     */
    public void adicionarUtilizadorCasa(String nifExecutor, String idCasa, String nifNovoUser) 
            throws IllegalArgumentException, IllegalAccessException {
        
        Casa c = validarEObterCasaParaAdmin(nifExecutor, idCasa);
        Utilizador novoUser = this.utilizadores.get(nifNovoUser);

        if (novoUser == null) {
            throw new IllegalArgumentException("O utilizador com o NIF '" + nifNovoUser + "' não está registado no sistema.");
        }

        if (c.isAdmin(nifNovoUser)) {
            throw new IllegalArgumentException("O utilizador '" + nifNovoUser + "' já é Administrador desta casa.");
        }

        if (c.isUserNormal(nifNovoUser)) {
            throw new IllegalArgumentException("O utilizador '" + nifNovoUser + "' já é residente normal nesta casa.");
        }

        c.addUser(novoUser);
    }

    /**
     * Remove o acesso de um utilizador normal a uma casa.
     */
    public void removerUtilizadorCasa(String nifExecutor, String idCasa, String nifARemover) 
            throws IllegalArgumentException, IllegalAccessException {
        
        Casa c = validarEObterCasaParaAdmin(nifExecutor, idCasa);

        if (!c.isUserNormal(nifARemover)) {
            throw new IllegalArgumentException("O utilizador '" + nifARemover + "' não tem estatuto de residente normal nesta casa.");
        }

        // Aqui não precisamos de try-catch como nos admins, pois uma casa pode ter 0 utilizadores normais
        c.removeUser(nifARemover);
    }

    public void configurarDispositivo(String nifUser, String idCasa, String idDisp, Map<String, Object> params) throws IllegalArgumentException, IllegalAccessException {
        Casa c = this.casas.get(idCasa);
        if (c == null) throw new IllegalArgumentException("Casa não encontrada.");
        if (!c.temAcesso(nifUser)) throw new IllegalAccessException("Acesso Negado.");
        c.configurarDispositivo(idDisp, params);
    }  
    
     // METODO AUXILIAR PRIVADOS
    private Casa validarEObterCasaParaAdmin(String nifExecutor, String idCasa) 
            throws IllegalArgumentException, IllegalAccessException {
        
        Casa c = this.casas.get(idCasa);
        if (c == null) {
            throw new IllegalArgumentException("A casa com o ID '" + idCasa + "' não existe.");
        }
        if (!c.isAdmin(nifExecutor)) {
            throw new IllegalAccessException("Apenas administradores da casa podem realizar esta ação.");
        }
        return c;
    }

    // CENARIOS 
    /**
     * Guarda um novo cenário criado pelo utilizador numa casa específica.
     */
    public void adicionarCenarioACasa(String nifUser, String idCasa, Cenario cenario) throws IllegalArgumentException {
        Casa c = this.casas.get(idCasa);
        if (c == null) {
            throw new IllegalArgumentException("A casa com o ID '" + idCasa + "' não existe.");
        }
        
        // Verifica se o utilizador é Admin ou Residente Normal
        if (!c.temAcesso(nifUser)) {
            throw new IllegalArgumentException("O utilizador '" + nifUser + "' não tem acesso a esta casa.");
        }

        c.addCenario(cenario);
    }

    /**
     * Ativa um cenário existente numa casa.
     */
    public void ativarCenarioCasa(String nifUser, String idCasa, String nomeCenario) throws IllegalArgumentException {
        Casa c = this.casas.get(idCasa);
        if (c == null) {
            throw new IllegalArgumentException("A casa com o ID '" + idCasa + "' não existe.");
        }
        
        if (!c.temAcesso(nifUser)) {
            throw new IllegalArgumentException("O utilizador '" + nifUser + "' não tem acesso a esta casa.");
        }

        // Delega a ativação à casa!
        c.ativarCenario(nomeCenario);
    }
}
