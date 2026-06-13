import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TextUI {
    private DomusControl controller;
    private final Scanner scanner;

    private String nifLogado;

    public TextUI(){
        this.scanner = new Scanner(System.in);
        this.nifLogado = null;
    }

    public DomusControl run(DomusControl controller){
        this.controller = controller;

        Menu menu = new Menu(new String[] {
            "Registar utilizador",
            "Login",
            "Avançar Tempo (questões de simulação)",
            "Estatísticas Globais"
        });

        // Só faz sentido avançar tempo ou ver estatísticas se houver utilizadores registados
        menu.setPreCondition(2, () -> controller.temUtilizadoresRegistados());
        menu.setPreCondition(3, () -> controller.temUtilizadoresRegistados());
        menu.setPreCondition(4, () -> controller.temUtilizadoresRegistados());

        menu.setHandler(1, this::registarUtilizador);
        menu.setHandler(2, this::login);
        menu.setHandler(3, this::avancarTempo);
        menu.setHandler(4, this::verEstatisticas);

        menu.run();

        System.out.println("A gravar estado e a encerrar o DomusControl... Até logo!");
        return this.controller;
    }

    // =========================================================
    // MÉTODOS AUXILIARES PARA EVITAR CÓDIGO REPETIDO (DRY)
    // =========================================================

    /**
     * Pede ao utilizador para selecionar uma Casa.
     * @param requerAdmin Se for true, apenas mostra as casas onde o utilizador é administrador.
     * @return O objeto Casa escolhido, ou null caso ocorra um erro/não existam casas.
     */
    private Casa escolherCasa(boolean requerAdmin) {
        List<Casa> casasFiltradas = controller.getCasas().values().stream()
                .filter(c -> requerAdmin ? c.isAdmin(this.nifLogado) : c.temAcesso(this.nifLogado))
                .collect(Collectors.toList());

        if (casasFiltradas.isEmpty()) {
            System.out.println(requerAdmin ? "Erro: Não é administrador de nenhuma casa." : "Atenção: Não tem acesso a nenhuma casa.");
            return null;
        }

        casasFiltradas.forEach(c -> System.out.println("ID: " + c.getId() + " | Morada: " + c.getMorada()));
        System.out.print("\nIndique o ID da Casa: ");
        String idCasa = scanner.nextLine();
        
        Casa casaEscolhida = controller.getCasas().get(idCasa);
        if (casaEscolhida == null || (requerAdmin && !casaEscolhida.isAdmin(this.nifLogado)) || (!requerAdmin && !casaEscolhida.temAcesso(this.nifLogado))) {
            System.out.println("Erro: Casa inválida ou sem permissões.");
            return null;
        }
        return casaEscolhida;
    }

    /**
     * Pede ao utilizador para selecionar uma Divisão de uma Casa.
     */
    private String escolherDivisao(Casa casa) {
        if (casa.getDivisoes().isEmpty()) {
            System.out.println("Esta casa não tem divisões. Crie uma divisão primeiro!");
            return null;
        }
        casa.getDivisoes().keySet().forEach(nomeDiv -> System.out.println("-> " + nomeDiv));
        
        System.out.print("\nNome da Divisão: ");
        String nomeDivisao = scanner.nextLine();

        if (!casa.getDivisoes().containsKey(nomeDivisao)) {
            System.out.println("Erro: A divisão '" + nomeDivisao + "' não existe.");
            return null;
        }
        return nomeDivisao;
    }

    /**
     * Imprime todos os dispositivos agrupados por divisão.
     * @return true se a casa tiver dispositivos, false caso contrário.
     */
    /**
     * Imprime todos os dispositivos agrupados por divisão.
     * @return true se a casa tiver dispositivos, false caso contrário.
     */
    private boolean listarDispositivos(Casa casa) {
        boolean temAparelhos = false;
        for (Map.Entry<String, Divisao> entrada : casa.getDivisoes().entrySet()) {
            String nomeDivisao = entrada.getKey();
            Divisao divisao = entrada.getValue();
            
            if (!divisao.getDispositivos().isEmpty()) {
                temAparelhos = true;
                System.out.println("-> " + nomeDivisao.toUpperCase() + ":");
                for (Dispositivo d : divisao.getDispositivos().values()) {
                    String tipo = d.getClass().getSimpleName(); // Diz se é "Lampada", "Tomada", etc.
                    String estado = d.getEstado() ? "LIGADO" : "DESLIGADO";
                   
                    StringBuilder infoExtra = new StringBuilder();

                    // Recuperamos a verificação de tipo para mostrar os detalhes específicos
                    if (d instanceof Lampada l) {
                        infoExtra.append(" | Modo: ").append(l.getModo())
                                .append(" | Brilho: ").append((int)l.getLuminosidade()).append("%");
                    } 
                    else if (d instanceof Coluna c) {
                        infoExtra.append(" | Vol: ").append(c.getVolume())
                                .append(" | Canal: ").append(c.getCanal().isEmpty() ? "(vazio)" : c.getCanal());
                    } else if (d instanceof Persiana p) {
                        infoExtra.append(" | Abertura: ").append((int)p.getPercentagemAbertura()).append("%"); 
                    }

                    System.out.println("   ↳ [" + d.getId() + "] " + d.getMarca() + " (" + tipo + ") -> " + estado + infoExtra.toString());
                }
            }
        }
        return temAparelhos;
    }

    /**
     * Pede os atributos comuns a todos os dispositivos antes de os instanciar.
     * @return Array com [ID, Marca, Modelo, ConsumoBase]
     */
    private String[] pedirDadosBaseDispositivo() {
        System.out.print("ID do novo Dispositivo (ex: Disp1): ");
        String idDisp = scanner.nextLine();
        System.out.print("Marca do aparelho: ");
        String marca = scanner.nextLine();
        System.out.print("Modelo do aparelho: ");
        String modelo = scanner.nextLine();
        System.out.print("Consumo Base (W): ");
        String consumo = scanner.nextLine();
        return new String[]{idDisp, marca, modelo, consumo};
    }

    // =========================================================
    // REGISTAR, LOGIN E MENU UTILIZADOR
    // =========================================================
    private void registarUtilizador() {
        System.out.print("Insira o seu NIF: ");
        String nif = scanner.nextLine();

        System.out.print("Insira o seu Nome: ");
        String nome = scanner.nextLine();

        try {
            Utilizador u = new Utilizador(nif, nome);
            controller.registarUtilizador(u);
            System.out.println("Utilizador registado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao registar: " + e.getMessage());
        }
    }

    private void login() {
        System.out.print("Insira o NIF para Login: ");
        String nif = scanner.nextLine();

        if (controller.existeUtilizador(nif)) {
            this.nifLogado = nif;
            System.out.println("Login efetuado com sucesso. Bem-vindo!");
            menuUtilizador(); 
        } else {
            System.out.println("Erro: Utilizador não encontrado. Registe-se primeiro.");
        }
    }

    // MENU USER LOGADO 
    private void menuUtilizador(){
        Menu menuUser = new Menu(new String[]{
            "Criar nova Casa",
            "Adiciona Divisão a uma Casa",
            "Instalar Dispositivo",
            "Interagir com Dispositivo",
            "Gerir Acessos",
            "Gerir Cenários" 
        });

        // Aplicar as pre-condições
        // Opção 1 (Criar Casa) está sempre disponível
        menuUser.setPreCondition(2, this::userEAdmin);   // Só pode adicionar divisão se for admin de alguma casa
        menuUser.setPreCondition(3, this::userEAdmin);   // Só pode instalar se for admin
        menuUser.setPreCondition(4, this::userTemCasas); // Pode interagir se for pelo menos residente
        menuUser.setPreCondition(5, this::userEAdmin);   // Só pode gerir acessos se for admin
        menuUser.setPreCondition(6, this::userTemCasas); // Pode gerir cenários se for residente

        menuUser.setHandler(1, this::criarCasa);
        menuUser.setHandler(2, this::adicionarDivisao);
        menuUser.setHandler(3, this::adicionarDispositivo);
        menuUser.setHandler(4, this::interagirComDispositivo);
        menuUser.setHandler(5, this::menuGerirAcessos);
        menuUser.setHandler(6, this::menuGerirCenarios); 

        System.out.println("\n=== MENU PRINCIPAL DO UTILIZADOR ===");
        menuUser.run();
    }

    private void menuGerirAcessos() {
        Menu menuAcessos = new Menu(new String[]{
            "Adicionar Administrador",
            "Remover Administrador",
            "Adicionar Residente Normal",
            "Remover Residente Normal"
        });

        menuAcessos.setHandler(1, this::adicionarNovoAdmin);
        menuAcessos.setHandler(2, this::removerAdmin);
        menuAcessos.setHandler(3, this::adicionarNovoUserNormal);
        menuAcessos.setHandler(4, this::removerUserNormal);

        System.out.println("\n=== GERIR ACESSOS ===");
        menuAcessos.run();
    }

    private void menuGerirCenarios() {
        Menu menuCenarios = new Menu(new String[]{
            "Criar Cenário",
            "Ativar Cenário"
        });

        menuCenarios.setHandler(1, this::criarCenario);
        menuCenarios.setHandler(2, this::ativarCenario);

        System.out.println("\n=== GERIR CENÁRIOS ===");
        menuCenarios.run();
    }


    // =========================================================
    // AÇÕES PRINCIPAIS (CRIAR, GERIR ACESSOS)
    // =========================================================
    private void criarCasa(){
        System.out.print("Insira o ID da nova Casa (ex: Casa1): ");
        String idCasa = scanner.nextLine();

        System.out.print("Insira a Morada: ");
        String morada = scanner.nextLine();

        try {
            controller.criarCasa(this.nifLogado, idCasa, morada);
            System.out.println("Casa criada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Falha ao criar casa: " + e.getMessage());
        }
    }

    private void adicionarDivisao() {
        System.out.println("\n=== ADICIONAR DIVISÃO A UMA CASA ===");
        Casa casa = escolherCasa(true);
        if(casa == null) return;

        System.out.print("Nome da nova Divisão (ex: Sala): ");
        String nomeDivisao = scanner.nextLine();

        try {
            controller.criarDivisao(this.nifLogado, casa.getId(), nomeDivisao);
            System.out.println("Divisão '" + nomeDivisao + "' criada com sucesso!");
        } catch (Exception e) { // Apanha IllegalArgumentException e IllegalAccessException
            System.out.println("Acesso Negado ou Erro: " + e.getMessage());
        }
    }

    private void adicionarNovoAdmin() {
        System.out.println("\n=== ADICIONAR ADMINISTRADOR A UMA CASA ===");
        Casa casa = escolherCasa(true);         // esta casa é um clone da original, atencao
        if(casa == null) return;

        System.out.print("Indique o NIF do utilizador a promover a Administrador: ");
        String nifNovoAdmin = scanner.nextLine();

        try {
            controller.adicionarAdministradorCasa(this.nifLogado, casa.getId(), nifNovoAdmin);
            System.out.println("Utilizador " + nifNovoAdmin + " promovido a administrador com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void removerAdmin() {
        System.out.println("\n=== REMOVER ADMINISTRADOR DE UMA CASA ===");
        
        Casa casa = escolherCasa(true);
        if(casa == null) return;

        System.out.println("Admins atuais: ");
        casa.getAdministradores().forEach(u -> System.out.println(" - " + u.getNome() + " (NIF: " + u.getNif() + ")"));

        System.out.print("Indique o NIF do administrador a remover: ");
        String nifARemover = scanner.nextLine();

        // 3. Executar a ordem
        try {
            controller.removerAdministradorCasa(this.nifLogado, casa.getId(), nifARemover);
            System.out.println("Administrador " + nifARemover + " removido com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void adicionarNovoUserNormal() {
        System.out.println("\n=== ADICIONAR RESIDENTE A UMA CASA ===");
        
        Casa casa = escolherCasa(true);
        if(casa == null) return;

        System.out.print("Indique o NIF do novo residente normal: ");
        String nifNovoUser = scanner.nextLine();

        try {
            controller.adicionarUtilizadorCasa(this.nifLogado, casa.getId(), nifNovoUser);
            System.out.println("Utilizador " + nifNovoUser + " adicionado à casa com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void removerUserNormal() {
        System.out.println("\n=== REMOVER RESIDENTE DE UMA CASA ===");
        
        Casa casa = escolherCasa(true);
        if(casa == null) return;


        System.out.println("Residentes atuais: ");
        casa.getUsersNormais().forEach(u -> 
            System.out.println(" - " + u.getNome() + " (NIF: " + u.getNif() + ")")
        );

        if (casa.getUsersNormais().isEmpty()) {
            System.out.println("Esta casa ainda não tem residentes normais associados.");
            return;
        }

        System.out.print("Indique o NIF do residente a remover: ");
        String nifARemover = scanner.nextLine();

        try {
            controller.removerUtilizadorCasa(this.nifLogado, casa.getId(), nifARemover);
            System.out.println("Residente " + nifARemover + " removido com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // =========================================================
    // MENU DE INSTALAÇÃO DE DISPOSITIVOS
    // =========================================================
    private void adicionarDispositivo() {
        System.out.println("\n=== SELECIONE A CASA PARA INSTALAÇÃO ===");
        Casa casaEscolhida = escolherCasa(true);
        if (casaEscolhida == null) return;
        

        // 2. LISTAR DIVISÕES DA CASA ESCOLHIDA
        System.out.println("\n=== DIVISÕES DISPONÍVEIS EM " + casaEscolhida.getId() + " ===");
        String nomeDivisao = escolherDivisao(casaEscolhida);
        if(nomeDivisao == null) return;

        Menu menuTipoDisp = new Menu(new String[]{
            "Tomada Inteligente",
            "Lâmpada Inteligente",
            "Coluna de Som",
            "Persiana"
        });
        menuTipoDisp.setHandler(1, () -> criarInstanciaTomada(casaEscolhida.getId(), nomeDivisao));
        menuTipoDisp.setHandler(2, () -> criarInstanciaLampada(casaEscolhida.getId(), nomeDivisao));
        menuTipoDisp.setHandler(3, () -> criarInstanciaColuna(casaEscolhida.getId(), nomeDivisao));
        menuTipoDisp.setHandler(4, () -> criarInstanciaPersiana(casaEscolhida.getId(), nomeDivisao));
        System.out.println("\n--- Que tipo de dispositivo deseja instalar? ---");
        menuTipoDisp.run();            
    }

    private void criarInstanciaTomada(String idCasa, String nomeDivisao) {
        try {
            String[] b = pedirDadosBaseDispositivo();
            Tomada novoDisp = new Tomada(b[0], b[1], b[2], Double.parseDouble(b[3]));
            controller.adicionarDispositivo(this.nifLogado, idCasa, nomeDivisao, novoDisp);
            System.out.println("Tomada instalada!");
        } catch (Exception e) { System.out.println("Erro: Formato inválido ou " + e.getMessage()); }
    }

    private void criarInstanciaLampada(String idCasa, String nomeDivisao) {
        try {
            String[] b = pedirDadosBaseDispositivo();
            System.out.print("Cor (2700k a 4000k): ");
            int cor = Integer.parseInt(scanner.nextLine());
            System.out.print("Luminosidade (0 a 100): ");
            int lum = Integer.parseInt(scanner.nextLine());
            System.out.print("Modo (1-NORMAL, 2-ECO): ");
            int modoOp = Integer.parseInt(scanner.nextLine());
            
            Lampada.Modo modo = (modoOp == 2) ? Lampada.Modo.ECO : Lampada.Modo.NORMAL;
            Lampada novoDisp = new Lampada(b[0], b[1], b[2], Double.parseDouble(b[3]), cor, lum, modo);
            controller.adicionarDispositivo(this.nifLogado, idCasa, nomeDivisao, novoDisp);
            System.out.println("Lâmpada instalada!");
        } catch (Exception e) { System.out.println("Erro: Formato inválido ou " + e.getMessage()); }
    }

    private void criarInstanciaColuna(String idCasa, String nomeDivisao) {
        try {
            String[] b = pedirDadosBaseDispositivo();
            System.out.print("Estação a tocar: ");
            String canal = scanner.nextLine();
            
            Coluna novoDisp = new Coluna(b[0], b[1], b[2], Double.parseDouble(b[3]), canal);
            controller.adicionarDispositivo(this.nifLogado, idCasa, nomeDivisao, novoDisp);
            System.out.println("Coluna instalada!");
        } catch (Exception e) { System.out.println("Erro: Formato inválido ou " + e.getMessage()); }
    }

    private void criarInstanciaPersiana(String idCasa, String nomeDivisao) {
        try {
            String[] b = pedirDadosBaseDispositivo();
            Persiana novoDisp = new Persiana(b[0], b[1], b[2], Double.parseDouble(b[3]));
            controller.adicionarDispositivo(this.nifLogado, idCasa, nomeDivisao, novoDisp);
            System.out.println("Persiana instalada!");
        } catch (Exception e) { System.out.println("Erro: Formato inválido ou " + e.getMessage()); }
    }

    // =========================================================
    // MENU DE INTERAÇÃO INDIVIDUAL (MENU DINÂMICO)
    // =========================================================
    private void interagirComDispositivo() {
        System.out.println("\n=== AS MINHAS CASAS ===");
        
        Casa casaClone = escolherCasa(false);
        if (casaClone == null) return;

        System.out.println("\n=== DISPOSITIVOS NESTA CASA ===");
        if (!listarDispositivos(casaClone)) {
            System.out.println("Ainda não existem dispositivos nesta casa.");
            return;
        }

        System.out.print("\nQual é o ID do dispositivo com que deseja interagir? ");
        String idDisp = scanner.nextLine();

        // Procurar o clone do dispositivo selecionado para saber o que ele é
        Dispositivo dispSelecionado = casaClone.getTodosDispositivos().stream()
                .filter(d -> d.getId().equals(idDisp))
                .findFirst()
                .orElse(null);

        if (dispSelecionado == null) {
            System.out.println("Erro: Dispositivo não encontrado.");
            return;
        }

       //  MENU DINÂMICO
        List<String> opcoes = new ArrayList<>();
        opcoes.add("Ligar");
        opcoes.add("Desligar");

        if (dispSelecionado instanceof Lampada) {
            opcoes.add("Alterar para Modo ECO");
            opcoes.add("Alterar para Modo NORMAL");
            opcoes.add("Ajustar Luminosidade");
        } else if (dispSelecionado instanceof Coluna) {
            opcoes.add("Ajustar Volume");
            opcoes.add("Mudar Estação");
        } else if (dispSelecionado instanceof Persiana) {
            opcoes.add("Ajustar Abertura");
        }

        // Criar o objeto Menu convertendo a lista num array de Strings
        Menu menuInteracao = new Menu(opcoes.toArray(new String[0]));

        // 5. MAPEAMENTO DOS HANDLERS (Com Lambdas)
        menuInteracao.setHandler(1, () -> ligarDispositivo(casaClone.getId(), idDisp));
        menuInteracao.setHandler(2, () -> desligarDispositivo(casaClone.getId(), idDisp));

        if (dispSelecionado instanceof Lampada) {
            menuInteracao.setHandler(3, () -> configurarLampada(casaClone.getId(), idDisp, true));
            menuInteracao.setHandler(4, () -> configurarLampada(casaClone.getId(), idDisp, false));
            menuInteracao.setHandler(5, () -> ajustarLuminosidade(casaClone.getId(), idDisp));
        } else if (dispSelecionado instanceof Coluna) {
            menuInteracao.setHandler(3, () -> ajustarVolume(casaClone.getId(), idDisp));
            menuInteracao.setHandler(4, () -> mudarEstacao(casaClone.getId(), idDisp));
        } else if (dispSelecionado instanceof Persiana) {
            menuInteracao.setHandler(3, () -> ajustarAbertura(casaClone.getId(), idDisp));
        }

        // 6. CORRER O MENU
        System.out.println("\n--- A INTERAGIR COM: " + dispSelecionado.getId() + " ---");
        menuInteracao.run();
    }

    // =========================================================
    // MÉTODOS AUXILIARES DE INTERAÇÃO COM DISPOSITIVOS
    // =========================================================

    private void ligarDispositivo(String idCasa, String idDisp) {
        try {
            controller.ligarDispositivo(this.nifLogado, idCasa, idDisp);
            System.out.println("Dispositivo ligado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao ligar: " + e.getMessage());
        }
    }

    private void desligarDispositivo(String idCasa, String idDisp) {
        try {
            controller.desligarDispositivo(this.nifLogado, idCasa, idDisp);
            System.out.println("Dispositivo desligado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao desligar: " + e.getMessage());
        }
    }

   private void configurarLampada(String idCasa, String idDisp, boolean modoEco) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("modoEco", modoEco);
            controller.configurarDispositivo(this.nifLogado, idCasa, idDisp, params);
            System.out.println("Lâmpada alterada para Modo " + (modoEco ? "ECO" : "NORMAL") + "!");
        } catch (Exception e) {
            System.out.println("Erro ao configurar lâmpada: " + e.getMessage());
        }
    }

    private void ajustarLuminosidade(String idCasa, String idDisp) {
        try {
            System.out.print("Nova luminosidade (0 a 100): ");
            double lum = scanner.nextDouble();
            scanner.nextLine();
            if (lum > 100) lum = 100;
            if (lum < 0) lum = 0;
            Map<String, Object> params = new HashMap<>();
            params.put("luminosidade", lum);
            controller.configurarDispositivo(this.nifLogado, idCasa, idDisp, params);
            System.out.println("Luminosidade ajustada para " + lum + "%!");
        } catch (java.util.InputMismatchException e) {
            System.out.println("Erro: Formato inválido.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Erro ao ajustar luminosidade: " + e.getMessage());
        }
    }

    private void mudarEstacao(String idCasa, String idDisp) {
        try {
            System.out.print("Qual a nova Estação? ");
            String canal = scanner.nextLine();
            Map<String, Object> params = new HashMap<>();
            params.put("canal", canal);
            controller.configurarDispositivo(this.nifLogado, idCasa, idDisp, params);
            System.out.println("A tocar agora: " + canal);
        } catch (Exception e) {
            System.out.println("Erro ao mudar de estação: " + e.getMessage());
        }
    }

    private void ajustarAbertura(String idCasa, String idDisp) {
        try {
            System.out.print("Qual a percentagem de abertura (0 a 100)? ");
            int perc = scanner.nextInt();
            scanner.nextLine();
            if (perc > 100) perc = 100;
            if (perc < 0) perc = 0;
            Map<String, Object> params = new HashMap<>();
            params.put("percentagem", perc);
            controller.configurarDispositivo(this.nifLogado, idCasa, idDisp, params);
            System.out.println("Persiana ajustada para " + perc + "% de abertura!");
        } catch (java.util.InputMismatchException e) {
            System.out.println("Erro: Formato inválido.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Erro ao ajustar a persiana " + e.getMessage());
        }
    }

    private void ajustarVolume(String idCasa, String idDisp) {
        try {
            System.out.print("Qual o novo volume (0 a 100)? ");
            int vol = scanner.nextInt();
            scanner.nextLine();
            if (vol > 100) vol = 100;
            if (vol < 0) vol = 0;
            Map<String, Object> params = new HashMap<>();
            params.put("volume", vol);
            controller.configurarDispositivo(this.nifLogado, idCasa, idDisp, params);
            System.out.println("Volume ajustado!");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // =========================================================
    // GERIR CENÁRIOS E TEMPO
    // =========================================================
    private void criarCenario() {
        System.out.println("\n=== CRIAR NOVO CENÁRIO ===");
        
        Casa c = escolherCasa(false);
        if (c == null) return;

        System.out.print("Dêáum nome a este Cenário (ex: 'Sair de Casa'): ");
        String nomeCenario = scanner.nextLine();
        
        Cenario novoCenario = new Cenario(nomeCenario);

        System.out.println("\n--- Dispositivos Disponíveis nesta Casa ---");
        listarDispositivos(c);

        

        // 3. Loop de Construção do Cenário
        while (true) {
            System.out.println("\n------------------------------------------------");
            System.out.print("ID do Dispositivo a adicionar ('0' para concluir): ");
            String idDisp = scanner.nextLine();

            if (idDisp.equals("0")) {
                break; // Sai do loop e guarda o cenário
            }

            try {
                System.out.print("LIGAR (1) ou DESLIGAR (0) este dispositivo? ");
                boolean ligar = Integer.parseInt(scanner.nextLine()) == 1;
                
                System.out.print("Valor Extra (ex: Volume a 80, Luminosidade a 50) ou -1 para ignorar: ");
                int valor = Integer.parseInt(scanner.nextLine());
                Integer valorExtra = (valor == -1) ? null : valor;

                novoCenario.adicionarInstrucao(idDisp, ligar, valorExtra);
                System.out.println("Instrução adicionada ao cenário!");
            } catch (Exception e) { System.out.println("Formato Inválido. Tente Novamente."); }
        }

        // Gravar no sistema
        try {
            if (!novoCenario.getInstrucoes().isEmpty()) {
                controller.adicionarCenarioACasa(this.nifLogado, c.getId(), novoCenario);
                System.out.println("Cenário '" + nomeCenario + "' guardado com sucesso!");
            } else {
                System.out.println("Cenário vazio. Operação cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao guardar cenário: " + e.getMessage());
        }
    }
    private void ativarCenario() {
        System.out.println("\n=== ATIVAR CENÁRIO ===");
        
        Casa c = escolherCasa(false);
        if (c == null) return;

        if (c.getCenarios().isEmpty()) {
            System.out.println("Esta casa ainda não tem cenários criados.");
            return;
        }

        System.out.println("\n--- Cenários Disponíveis ---");
        c.getCenarios().keySet().forEach(nome -> System.out.println("-> " + nome));

        System.out.print("\nNome do Cenário a ativar: ");
        String nomeCenario = scanner.nextLine();

        try {
            controller.ativarCenarioCasa(this.nifLogado, c.getId(), nomeCenario);
            System.out.println("Cenário '" + nomeCenario + "' ativado com sucesso! Dispositivos alterados.");
        } catch (Exception e) {
            System.out.println("Erro na ativação: " + e.getMessage());
        }
    }


    // AVANÇAR TEMPO (para mostrar na defesa)
    private void avancarTempo() {
        try {
            System.out.print("Quantas horas para avançar na simulação? ");
            double horas = scanner.nextDouble();
            scanner.nextLine();

            controller.avancarTempo(horas);
            System.out.println("O tempo avançou " + horas + " horas. Todos os consumos foram atualizados!");
        } catch (InputMismatchException e) {
            System.out.println("Erro: Deve inserir um número válido.");
            scanner.nextLine();
        }
    }

    // =========================================================
    // ESTATÍSTICAS
    // =========================================================
    private void verEstatisticas() {
        Menu menuStats = new Menu(new String[]{
                "Qual é a Casa que mais gastou energia?",
                "Qual a casa mais económica",
                "Top 3 Dispositivos mais ativados (numa Casa)",
                "Top 3 Dispositivos mais tempo ligados (numa Casa)",
                "Top 3 Divisões com mais dispositivos (Global)"
        });

        menuStats.setHandler(1, this::checkCasaMaisGastadora);
        menuStats.setHandler(2, this::checkCasaMaisEconomica);
        menuStats.setHandler(3, this::checkTop3Ativacoes);
        menuStats.setHandler(4, this::checkTop3TempoLigado);
        menuStats.setHandler(5, this::checkTop3DivisoesComMaisDispositivos);

        menuStats.run();
    }

    private void checkCasaMaisGastadora() {
        Casa c = controller.getCasaMaisGastadora();
        if (c != null) {
            System.out.println("\n[A CASA MAIS GASTADORA]");
            System.out.println("Casa: " + c.getId() + " | Morada: " + c.getMorada());
            System.out.println("Consumo Total: " + String.format("%.2f", c.getConsumoTotalDaCasa()) + " W");
        } else {
            System.out.println("Ainda não existem dados de consumo no sistema.");
        }
    }

    private void checkCasaMaisEconomica() {
        Casa c = controller.getCasaMaisEconomica();
        if (c != null) {
            System.out.println("\n[A CASA MAIS ECONÓMICA]");
            System.out.println("Casa: " + c.getId() + " | Morada: " + c.getMorada());
            System.out.println("Consumo Total: " + String.format("%.2f", c.getConsumoTotalDaCasa()) + " W");
            
            if (c.getConsumoTotalDaCasa() == 0) {
                System.out.println("(Atenção: Esta casa pode ainda não ter consumido energia na simulação).");
            }
        } else {
            System.out.println("Ainda não existem dados de consumo no sistema.");
        }
    }

    private void checkTop3Ativacoes() {
        System.out.print("Indique o ID da Casa: ");
        String idCasa = scanner.nextLine();

        List<Dispositivo> top = controller.getTop3DispositivosPorAtivacoes(idCasa);
        
        if (top.isEmpty()) {
            System.out.println("Não há dispositivos registados nessa casa ou a casa não existe.");
            return;
        }

        System.out.println("\n[TOP 3 DISPOSITIVOS - Nº DE ATIVAÇÕES na casa " + idCasa + "]");
        for (int i = 0; i < top.size(); i++) {
            System.out.println((i + 1) + "º -> " + top.get(i).getId() + " (" + top.get(i).getMarca() + ") : " + 
                               top.get(i).getNumeroAtivacoes() + " ligações.");
        }
    }

    private void checkTop3TempoLigado() {
        System.out.print("Indique o ID da Casa: ");
        String idCasa = scanner.nextLine();

        List<Dispositivo> top = controller.getTop3DispositivosPorTempo(idCasa);
        
        if (top.isEmpty()) {
            System.out.println("Não há dispositivos registados nessa casa ou a casa não existe.");
            return;
        }

        System.out.println("\n[TOP 3 DISPOSITIVOS - TEMPO LIGADO na casa " + idCasa + "]");
        for (int i = 0; i < top.size(); i++) {
            System.out.println((i + 1) + "º -> " + top.get(i).getId() + " (" + top.get(i).getMarca() + ") : " + 
                               String.format("%.2f", top.get(i).getTempoTotalLigado()) + " horas.");
        }
    }

    private void checkTop3DivisoesComMaisDispositivos() {
        List<String> topDivisoes = controller.getTop3DivisoesComMaisDispositivos();
        
        if (topDivisoes.isEmpty()) {
            System.out.println("Ainda não existem divisões configuradas no sistema.");
            return;
        }

        System.out.println("\n[TOP 3 DIVISÕES COM MAIS EQUIPAMENTOS (SISTEMA GLOBAL)]");
        for (int i = 0; i < topDivisoes.size(); i++) {
            System.out.println((i + 1) + "º -> " + topDivisoes.get(i));
        }
    }

// =========================================================
    // HELPER METHODS PARA AS PRE-CONDIÇÕES DOS MENUS
    // =========================================================
    
    private boolean userTemCasas() {
        return controller.getCasas().values().stream()
                .anyMatch(c -> c.temAcesso(this.nifLogado));
    }

    private boolean userEAdmin() {
        return controller.getCasas().values().stream()
                .anyMatch(c -> c.isAdmin(this.nifLogado));
    }
}
