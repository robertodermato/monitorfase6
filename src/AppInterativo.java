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

        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("");
            System.out.println("---------------------------------------------");

            System.out.println("\nDigite um comando. Comandos disponíveis e exemplos de uso:\n\n" +
                    "- cria nomeDePrograma - exemplo: cria(fibo)\n" +
                    "- nomes de programas disponíveis: fibo, fato, bub\n" +
                    "- executa id          - exemplo: executa(2)\n" +
                    "- dump id             - exemplo: dump(1)\n" +
                    "- dumpM inicio fim    - exemplo: dumpM(2,5)\n" +
                    "- desaloca id         - exemplo: desaloca(1)\n" +
                    "- listaProcessos      - exemplo: lista\n" +
                    "- end                 - exemplo: end");

            String palavra = in.nextLine();

            if (palavra.equals("lista")){
                s.listaProcessos();
            }

            else if (palavra.equals("end")){
                System.out.println("Goodbye!");
                break;
            }

            else if (palavra.contains("(")){
                //s.cria(Sistema.progs.bubbleSort);
                //s.executa(2);
                //s.dump(2);
                //s.dumpM(2,5);
                //s.desaloca(2);
            }

            else{
                System.out.println("Comando desconhecido!");
            }
        }
    }
}