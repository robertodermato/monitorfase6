import java.util.Scanner;
import java.util.concurrent.Semaphore;

/*
RATE MY OS
 */
public class AppInterativo {

    public static void main(String[] args) throws InterruptedException {

        // Variáveis
        int tamanhoDamemoria = 1024;
        int tamanhoDaPaginadeMemoria = 16;
        int maxInt = 100_000;
        int quantidadeRegistradores = 10;
        int deltaMax = 5; // usado pelo escalonador para interromper a CPU a cada x ciclos

        Sistema s = new Sistema(tamanhoDamemoria, tamanhoDaPaginadeMemoria, maxInt, quantidadeRegistradores, deltaMax);

        BootAnimation ba = new BootAnimation();
        ba.load();
        int entrada;

        Scanner in = new Scanner(System.in);

        while (true) {

            try {
                System.out.println("\nDigite um comando. Comandos disponíveis: cria nomeDePrograma\n" +
                        "executa id\n" +
                        "dump id\n" +
                        "dumpM inicio fim \n" +
                        "desaloca id\n" +
                        "listaProcessos");
                String palavra = in.nextLine();
                entrada = Integer.parseInt(palavra.split(" ")[1]);

                if (palavra.split(" ")[0].equals("s")) {

                    continue;
                } else if(palavra.split(" ")[0].equals("c")) {

                    continue;
                }
                //entrada = Integer.parseInt(in.nextLine());
                if (entrada == -1)
                    Runtime.getRuntime().exit(1); break;

            } catch (NumberFormatException nfe) {
                System.out.println("Apenas números!");
                continue;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Formato inválido.");
                continue;
            }


        }
    }
}