import java.util.*;

public class Menu {
    private static Scanner is = new Scanner(System.in);

    public interface Handler
    {
        public void execute();
    }

    public interface PreCondition
    {
        public boolean validate();
    }

    private List<String> opcoes;
    private List<PreCondition> disponivel;
    private List<Handler> handlers;

    public Menu(String[] opcoes)
    {
        this.opcoes = Arrays.asList(opcoes);
        this.disponivel = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.opcoes.forEach(op ->{
                this.disponivel.add(() -> true);
                this.handlers.add(() ->
                {System.out.println("Nao implementado");
                });
        });
    }

    public void run() {
        int op = 0;
        do {
            show();
            op = readOption();
            if (op > 0 && !this.disponivel.get(op - 1).validate()) {
                System.out.println("Essa opção não existe");
            } else if (op > 0) {
                this.handlers.get(op - 1).execute();
            }
        } while (op != 0);
    }

    public void setPreCondition(int i, PreCondition b) {
        this.disponivel.set(i-1,b);
    }

    public void setHandler(int i, Handler h) {
        this.handlers.set(i-1, h);
    }

    private void show() {
        System.out.println("\n *** DomusControl *** ");
        for (int i=0; i<this.opcoes.size(); i++) {
            System.out.print(i+1);
            System.out.print(" - ");
            System.out.println(this.disponivel.get(i).validate()?this.opcoes.get(i):"---");
        }
        System.out.println("0 - Sair");
    }

    private int readOption() {
        int op;

        System.out.print("Opção: ");
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);

        }
        catch (NumberFormatException e) { // NÃ£o foi escrito um int
            op = -1;
        }
        if (op<0 || op>this.opcoes.size()) {
            System.out.println("Opção Inválida!!!");
            op = -1;
        }
        return op;
    }
}