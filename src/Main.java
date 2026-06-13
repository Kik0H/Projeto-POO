import java.io.IOException;

/*
MELHORIAS: 
ao ligar dispositivo, em vez de fazer controller -> casa -> divisao -> dispositivo, podemos fazer controller -> casa -> percorrer cada divisao e se encontrar liga 
Na casa, o Set<string> admins, pode passar instancia utilizador em vez do id do user.
*/

public class Main {
    
    public static void main(String[] args) {
        DomusControl motor;
        String ficheiroDados = "domus_estado.obj";
        //  Tentar carregar os dados guardados de sessões anteriores
        try {
            motor = GuardarEstado.carregarEstado();
            System.out.println("Sucesso: Estado anterior carregado da memória!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aviso: Nenhum ficheiro de dados encontrado (ou formato inválido).");
            System.out.println("A iniciar um DomusControl novo e limpo...");
            // Se falhar a leitura, cria um motor vazio
            motor = new DomusControl(); 
        }

        // Arrancar a Interface Gráfica (View) e passar-lhe o Motor (Controller)
        TextUI ui = new TextUI();
        motor = ui.run(motor); 

        // Ao sair da Interface (quando o utilizador prime '0' no menu principal), guardar tudo!
        try {
            GuardarEstado.guardarEstado(motor);
            System.out.println("Sucesso: Estado do sistema guardado no ficheiro '" + ficheiroDados + "'.");
            System.out.println("Obrigado por usar o DomusControl. Até à próxima!");
        } catch (IOException e) {
            System.out.println("Não foi possível guardar o estado do sistema.");
            System.out.println("Detalhes do erro: " + e.getMessage());
        }
    }
}