public class App {

    public static void main(String args[]) {
        // Variáveis
        int tamanhoDamemoria = 1024;
        int tamanhoDaPaginadeMemoria = 16;
        int maxInt = 100_000;
        int quantidadeRegistradores = 10;

        Sistema s = new Sistema(tamanhoDamemoria, tamanhoDaPaginadeMemoria, maxInt, quantidadeRegistradores);

        // Desenvolvidos pelo professor
        //s.roda(Sistema.progs.fibonacci10);
        //s.roda(Sistema.progs.progMinimo);

        // Fase 1
        //s.roda(Sistema.progs.fatorial);
        //s.roda(Sistema.progs.fibonacci2);
        //s.roda(Sistema.progs.fatorial2);
        //s.roda(Sistema.progs.bubbleSort);

        // Fase 2 - Testes de Interrupções
        //s.roda(Sistema.progs.invalidAddressTest);
        //s.roda(Sistema.progs.overflowTest);
        //s.roda(Sistema.progs.invalidRegisterTest);

        // Fase 3 - Testes de Chamadas de Sistema
        //s.roda(Sistema.progs.trapTestOutput);
        //s.roda(Sistema.progs.trapTestInput);
        //s.roda(Sistema.progs.fibonacciComOutput);
        //s.roda(Sistema.progs.fatorialComInput);

        // Fase 4 - Testa o Gerenciador de Memória - mockamos frames 0 e 2 como sempre ocupados
        //s.roda(Sistema.progs.bubbleSort);
        //s.roda(Sistema.progs.fatorial);
        //s.roda(Sistema.progs.fatorial);
        //s.roda(Sistema.progs.fatorial);
        //s.roda(Sistema.progs.fatorial);
        //s.roda(Sistema.progs.fibonacci10);
        //s.roda(Sistema.progs.fibonacci10);
        //s.roda(Sistema.progs.fibonacci10);
        //s.roda(Sistema.progs.progMinimo);
        //s.roda(Sistema.progs.progMinimo);
        //s.roda(Sistema.progs.progMinimo);
        //s.roda(Sistema.progs.bubbleSort);
        //s.roda(Sistema.progs.bubbleSort);
        //s.roda(Sistema.progs.bubbleSort);

        // Fase 5 - Gernciador de Processos
        s.cria(Sistema.progs.bubbleSort);
        s.cria(Sistema.progs.bubbleSort);
        s.cria(Sistema.progs.bubbleSort);
        //s.cria(Sistema.progs.fatorial);
        //s.cria(Sistema.progs.fatorial);
        //s.cria(Sistema.progs.fatorial);
        //s.executa(5);
        //s.dump(2);
        //s.dumpM(2,5);
        //s.desaloca(2);
    }
}