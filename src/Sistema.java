// PUCRS - Escola Politécnica - Sistemas Operacionais
// Prof. Fernando Dotti
// Código fornecido como parte da solução do projeto de Sistemas Operacionais
//
// Fase 1 - máquina virtual (vide enunciado correspondente)
//


import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;


public class Sistema {


    // -------------------------------------------------------------------------------------------------------
    // --------------------- H A R D W A R E - definicoes de HW ----------------------------------------------

    // -------------------------------------------------------------------------------------------------------
    // --------------------- M E M O R I A -  definicoes de opcode e palavra de memoria ----------------------

    public static class Word {    // cada posicao da memoria tem uma instrucao (ou um dado)
        public Opcode opc;    //
        public int r1;        // indice do primeiro registrador da operacao (Rs ou Rd cfe opcode na tabela)
        public int r2;        // indice do segundo registrador da operacao (Rc ou Rs cfe operacao)
        public int p;        // parametro para instrucao (k ou A cfe operacao), ou o dado, se opcode = DADO

        public Word(Opcode _opc, int _r1, int _r2, int _p) {
            opc = _opc;
            r1 = _r1;
            r2 = _r2;
            p = _p;
        }
    }
    // -------------------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------
    // --------------------- C P U  -  definicoes da CPU -----------------------------------------------------

    public enum Opcode {
        DATA, ___,            // se memoria nesta posicao tem um dado, usa DATA, se não usada é NULO ___
        JMP, JMPI, JMPIG, JMPIL, JMPIE, JMPIM, JMPIGM, JMPILM, JMPIEM, STOP,   // desvios e parada
        ADDI, SUBI, ADD, SUB, MULT,             // matemáticos
        LDI, LDD, STD, LDX, STX, SWAP,          // movimentação
        TRAP;                                   //
    }

    public enum Interrupts {
        INT_NONE,
        INT_INVALID_INSTRUCTION,    // Nunca será usada, pois o Java não deixará compilar
        INT_INVALID_ADDRESS,        // Nossa memória tem 1024 posições
        INT_OVERFLOW,               // Nossa memória só trabalha com inteiros, ou seja de -2,147,483,648 até 2,147,483,647
        INT_SYSTEM_CALL,            // Ativa chamada de I/O pelo comando TRAP
        INT_SCHEDULER;              // Aciona o Escalonador
    }

    public class CPU {
        // característica do processador: contexto da CPU ...
        private int pc;            // ... composto de program counter,
        private Word ir;            // instruction register,
        private int[] reg;        // registradores da CPU
        public int maxInt;          // criado para podermos simular overflow
        private int[] paginasAlocadas;
        private int[] tabelaDePaginas;
        private int tamPaginaMemoria;

        // usado pelo escalonador
        int delta;
        int deltaMax;
        boolean escalonadorState;

        // cria variável interrupção
        public Interrupts interrupts;

        private Word[] m;   // CPU acessa MEMORIA, guarda referencia 'm' a ela. memoria nao muda. ee sempre a mesma.

        public CPU(Word[] _m, int tamPaginaMemoria, int maxInt, int deltaMax, int [] reg, Interrupts interrupts, Word ir) {     // ref a MEMORIA e interrupt handler passada na criacao da CPU
            m = _m;                // usa o atributo 'm' para acessar a memoria.
            reg = new int[10];        // aloca o espaço dos registradores
            this.maxInt = maxInt;          // números aceitos -100_000 até 100_000
            this.tamPaginaMemoria = tamPaginaMemoria;
            this.reg = reg;
            this.interrupts = interrupts;
            this.ir = ir;

            delta = 0;
            this.deltaMax = deltaMax;
            escalonadorState = false;
        }

        public void setContext(int _pc, int [] paginasAlocadas, int [] registradores, Word instructionRegister, Interrupts interrupt) {  // no futuro esta funcao vai ter que ser
            pc = _pc;                                   // limite e pc (deve ser zero nesta versão)
            this.interrupts = Interrupts.INT_NONE;      // inicializa interrupção com NONE
            this.paginasAlocadas = paginasAlocadas;
            this.tabelaDePaginas = tabelaDePaginas;
            this.reg = registradores;
            ir = instructionRegister;
            this.interrupts = interrupt;
        }

        public void setEscalonadorState(boolean state){
            this.escalonadorState = state;
        }

        public Interrupts getInterrupts(){
            return interrupts;
        }

        public int [] getReg(){
            return reg;
        }

        public int getPc(){
            if (pc==0) return 0;
            return traduzEndereco(pc);
        }

        public Word getIr(){
            return ir;
        }

        private void dump(Word w) {
            System.out.print("[ ");
            System.out.print(w.opc);
            System.out.print(", ");
            System.out.print(w.r1);
            System.out.print(", ");
            System.out.print(w.r2);
            System.out.print(", ");
            System.out.print(w.p);
            System.out.println("  ] ");
        }

        private void showState() {
            System.out.println("       " + pc);
            System.out.print("           ");
            for (int i = 0; i < reg.length; i++) {
                System.out.print("r" + i);
                System.out.print(": " + reg[i] + "     ");
            }
            ;
            System.out.println("");
            System.out.print("           ");
            dump(ir);
        }


        private boolean isRegisterValid(int register) {
            if (register < 0 || register >= reg.length) {
                interrupts = Interrupts.INT_INVALID_INSTRUCTION;
                return false;
            }
            return true;
        }

        private boolean isAddressValid(int address) {
            if (address < 0 || address >= m.length) {
                interrupts = Interrupts.INT_INVALID_ADDRESS;
                return false;
            }
            return true;
        }

        private boolean isNumberValid(int number) {
            if (number < maxInt * -1 || number > maxInt) {
                interrupts = Interrupts.INT_OVERFLOW;
                return false;
            }
            return true;
        }

        public int traduzEndereco (int endereco){
            System.out.println(endereco);
            System.out.println("paginas alocadas" + paginasAlocadas.length);

            try {
                return (paginasAlocadas[(endereco / tamPaginaMemoria)] * tamPaginaMemoria) + (endereco % tamPaginaMemoria);

            } catch(ArrayIndexOutOfBoundsException e) {
                System.out.println("Retorno -1 do traduz");
                return -1;
            }
        }

        public void run() {        // execucao da CPU supoe que o contexto da CPU, vide acima, esta devidamente setado

            boolean run = true;
            while (run) {            // ciclo de instrucoes. acaba cfe instrucao, veja cada caso.
                // FETCH

                //if (isAddressValid(traduzEndereco(pc))) {
                //    ir = m[traduzEndereco(pc)];
                //}

                //System.out.println("rodando a cpu");

                ir = m[traduzEndereco(pc)];    // busca posicao da memoria apontada por pc, guarda em ir

                delta++;
                System.out.println("Delta em " + delta);

                //só para debug
                //showState();

                // EXECUTA INSTRUCAO NO ir
                switch (ir.opc) { // para cada opcode, sua execução

                    case LDI: // Rd ← k
                        if (isRegisterValid(ir.r1) && isNumberValid(ir.p)) {
                            reg[ir.r1] = ir.p;
                            pc++;
                            break;
                        } else
                            break;

                    case LDD: // Rd ← [A]
                        if (isRegisterValid(ir.r1) && isAddressValid(traduzEndereco(ir.p)) && isNumberValid(m[ir.p].p)) {
                            reg[ir.r1] = m[traduzEndereco(ir.p)].p;
                            pc++;
                            break;
                        } else
                            break;

                    case STD: // [A] ← Rs
                        if (isRegisterValid(ir.r1) && isAddressValid(traduzEndereco(ir.p)) && isNumberValid(reg[ir.r1])) {
                            m[traduzEndereco(ir.p)].opc = Opcode.DATA;
                            m[traduzEndereco(ir.p)].p = reg[ir.r1];
                            pc++;
                            break;
                        } else
                            break;

                    case ADD: // Rd ← Rd + Rs
                        if (isRegisterValid(ir.r2) && isRegisterValid(ir.r1) && isNumberValid(reg[ir.r1]) && isNumberValid(reg[ir.r2]) && isNumberValid(reg[ir.r1] + reg[ir.r2])) {
                            reg[ir.r1] = reg[ir.r1] + reg[ir.r2];
                            pc++;
                            break;
                        } else {
                            interrupts = Interrupts.INT_OVERFLOW;
                            pc++;
                            break;
                        }

                    case MULT: // Rd ← Rd * Rs
                        if (isRegisterValid(ir.r2) && isRegisterValid(ir.r1)) {
                            if (isNumberValid(reg[ir.r1] * reg[ir.r2]) && isNumberValid(reg[ir.r1]) && isNumberValid(reg[ir.r2])) {
                                reg[ir.r1] = reg[ir.r1] * reg[ir.r2];
                                pc++;
                                break;
                            } else {
                                pc++;
                                break;
                            }
                        } else
                            break;

                    case ADDI: // Rd ← Rd + k
                        if (isRegisterValid(ir.r1) && isNumberValid(reg[ir.r1]) && isNumberValid(ir.p) && isNumberValid(reg[ir.r1] + ir.p)) {
                            reg[ir.r1] = reg[ir.r1] + ir.p;
                            pc++;
                            break;
                        } else {
                            interrupts = Interrupts.INT_OVERFLOW;
                            pc++;
                            break;
                        }


                    case STX: // [Rd] ←Rs
                        if (isRegisterValid(ir.r1) && isRegisterValid(ir.r2) && isAddressValid(traduzEndereco(reg[ir.r1]))) {
                            m[traduzEndereco(reg[ir.r1])].opc = Opcode.DATA;
                            m[traduzEndereco(reg[ir.r1])].p = reg[ir.r2];
                            pc++;
                            break;
                        } else
                            break;

                    case LDX: // Rd ← [Rs]
                        if (isRegisterValid(ir.r1) && isRegisterValid(ir.r2) && isAddressValid(traduzEndereco(reg[ir.r2])) && isNumberValid(m[reg[ir.r2]].p)) {
                            reg[ir.r1] = m[traduzEndereco(reg[ir.r2])].p;
                            pc++;
                            break;
                        } else
                            break;

                    case SUB: // Rd ← Rd - Rs
                        if (isRegisterValid(ir.r1) && isRegisterValid(ir.r2) && isNumberValid(reg[ir.r2]) && isNumberValid(reg[ir.r1]) && isNumberValid(reg[ir.r1] - reg[ir.r2])) {
                            reg[ir.r1] = reg[ir.r1] - reg[ir.r2];
                            pc++;
                            break;
                        } else {
                            interrupts = Interrupts.INT_OVERFLOW;
                            pc++;
                            break;
                        }

                    case SUBI: // Rd ← Rd - k
                        if (isRegisterValid(ir.r1) && isNumberValid(reg[ir.r1]) && isNumberValid(ir.p) && isNumberValid(reg[ir.r1] - ir.p)) {
                            reg[ir.r1] = reg[ir.r1] - ir.p;
                            pc++;
                            break;
                        } else {
                            interrupts = Interrupts.INT_OVERFLOW;
                            pc++;
                            break;
                        }

                    case JMP: //  PC ← k
                        if (isAddressValid(traduzEndereco(ir.p))) {
                            pc = ir.p;
                            break;
                        } else
                            break;

                    case JMPI: //  PC ← Rs
                        if (isRegisterValid(traduzEndereco(ir.r1)) && isAddressValid(traduzEndereco(reg[ir.r1]))) {
                            pc = reg[ir.r1];
                            break;
                        } else
                            break;


                    case JMPIG: // If Rc > 0 Then PC ← Rs Else PC ← PC +1
                        if (isRegisterValid(ir.r2) && isRegisterValid(ir.r1) && isAddressValid(traduzEndereco(reg[ir.r1]))) {
                            if (reg[ir.r2] > 0) {
                                pc = reg[ir.r1];
                            } else {
                                pc++;
                            }
                            break;
                        } else
                            break;

                    case JMPIGM: // If Rc > 0 Then PC ← [A] Else PC ← PC +1
                        if (isRegisterValid(ir.r2) && isAddressValid(traduzEndereco(ir.p)) && isAddressValid(traduzEndereco(m[ir.p].p))) {
                            if (reg[ir.r2] > 0) {
                                pc = m[traduzEndereco(ir.p)].p;
                            } else {
                                pc++;
                            }
                            break;
                        } else
                            break;

                    case JMPILM: // If Rc < 0 Then PC ← [A] Else PC ← PC +1
                        if (isRegisterValid(ir.r2) && isAddressValid(traduzEndereco(ir.p)) && isAddressValid(traduzEndereco(m[ir.p].p))) {
                            if (reg[ir.r2] < 0) {
                                pc = m[traduzEndereco(ir.p)].p;
                            } else {
                                pc++;
                            }
                            break;
                        } else
                            break;

                    case JMPIEM: // If Rc = 0 Then PC ← [A] Else PC ← PC +1
                        if (isRegisterValid(ir.r2) && isAddressValid(traduzEndereco(ir.p)) && isAddressValid(traduzEndereco(m[ir.p].p))) {
                            if (reg[ir.r2] == 0) {
                                pc = m[traduzEndereco(ir.p)].p;
                            } else {
                                pc++;
                            }
                            break;
                        } else
                            break;


                    case JMPIE: // If Rc = 0 Then PC ← Rs Else PC ← PC +1
                        if (isRegisterValid(ir.r1) && isRegisterValid(ir.r2) && isAddressValid(traduzEndereco(reg[ir.r1]))) {
                            if (reg[ir.r2] == 0) {
                                pc = reg[ir.r1];
                            } else {
                                pc++;
                            }
                            break;
                        } else
                            break;

                    case JMPIL: //  PC ← Rs
                        if (isRegisterValid(ir.r1) && isRegisterValid(ir.r2) && isAddressValid(traduzEndereco(reg[ir.r1]))) {
                            if (reg[ir.r2] < 0) {
                                pc = reg[ir.r1];
                            } else {
                                pc++;
                            }
                            break;
                        } else
                            break;

                    case JMPIM: //  PC ← [A]
                        if (isAddressValid(traduzEndereco(m[ir.p].p)) && isAddressValid(traduzEndereco(ir.p))) {
                            pc = m[traduzEndereco(ir.p)].p;
                            break;
                        } else
                            break;

                    case SWAP: // t <- r1; r1 <- r2; r2 <- t
                        if (isRegisterValid(ir.r1) && isRegisterValid(ir.r2) && isNumberValid(reg[ir.r1]) && isNumberValid(reg[ir.r2])) {
                            int temp;
                            temp = reg[ir.r1];
                            reg[ir.r1] = reg[ir.r2];
                            reg[ir.r2] = temp;
                            pc++;
                            break;
                        } else
                            break;

                    case STOP: // por enquanto, para execucao
                        break;

                    case TRAP:
                        interrupts = Interrupts.INT_SYSTEM_CALL;
                        pc++;
                        break;

                    case DATA:
                        pc++;
                        break;

                    default:
                        // opcode desconhecido
                        interrupts = Interrupts.INT_INVALID_INSTRUCTION;
                }

                // VERIFICA INTERRUPÇÃO !!! - TERCEIRA FASE DO CICLO DE INSTRUÇÕES
                if (ir.opc == Opcode.STOP) {
                    break; // break sai do loop da cpu
                }

                // Aciona o Escalonador
                if (delta==deltaMax && escalonadorState==true){
                    delta=0;
                    interrupts = Interrupts.INT_SCHEDULER;
                }

                if (interrupts != Interrupts.INT_NONE) {
                    run = monitor.interruptHandler(reg, m, pc, interrupts);
                    interrupts = Interrupts.INT_NONE; // sai da chamada de sistema. talvez seja preciso criar um handler pra system call
                }




            }
        }
    }
    // ------------------ C P U - fim ------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------


    // ------------------- V M  - constituida de CPU e MEMORIA -----------------------------------------------
    // -------------------------- atributos e construcao da VM -----------------------------------------------
    public class VM {
        public int tamMem;
        public Word[] m;
        public CPU cpu;
        private int tamanhoPaginaMemoria;

        public VM(int tamMem, int tamanhoPaginaMemoria, int maxInt, int deltaMax, int [] registradors, Interrupts interrupt, Word instructionRegister) {
            // memória
            this.tamMem = tamMem;
            this.tamanhoPaginaMemoria = tamanhoPaginaMemoria;
            m = new Word[tamMem]; // m ee a memoria
            for (int i = 0; i < tamMem; i++) {
                m[i] = new Word(Opcode.___, -1, -1, -1);
            }
            ;

            // cpu
            cpu = new CPU(m, tamanhoPaginaMemoria, maxInt, deltaMax, registradores, interrupt, instructionRegister);   // cpu acessa memória
        }

        public int getTamMem() {
            return tamMem;
        }
    }
    // ------------------- V M  - fim ------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    // --------------------H A R D W A R E - fim -------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------
    // ------------------- S O F T W A R E - inicio ----------------------------------------------------------

    // -------------------------------------------  funcoes de um monitor
    public class Monitor {
        public void dump(Word w) {
            System.out.print("[ ");
            System.out.print(w.opc);
            System.out.print(", ");
            System.out.print(w.r1);
            System.out.print(", ");
            System.out.print(w.r2);
            System.out.print(", ");
            System.out.print(w.p);
            System.out.println("  ] ");
        }

        public void dump(Word[] m, int ini, int fim) {
            for (int i = ini; i < fim; i++) {
                System.out.print(i);
                System.out.print(":  ");
                dump(m[i]);
            }
        }

        public void carga(Word[] p, Word[] m) {    // significa ler "p" de memoria secundaria e colocar na principal "m"
            for (int i = 0; i < p.length; i++) {
                m[i].opc = p[i].opc;
                m[i].r1 = p[i].r1;
                m[i].r2 = p[i].r2;
                m[i].p = p[i].p;
            }
        }

        public void executa() {
            vm.cpu.setContext(0, gm.getFramesAlocados(), vm.cpu.getReg(), vm.cpu.getIr(), vm.cpu.getInterrupts());          // monitor seta contexto - pc aponta para inicio do programa
            vm.cpu.run();                  //                         e cpu executa
            // note aqui que o monitor espera que o programa carregado acabe normalmente
            // nao ha protecoes...  o que poderia acontecer ?
        }

        // deveria ter esse método para manejo correto do input, mas falta arrumar o paginas alocadas
        /*
        public int traduzEndereco (int endereco){
            try {
                return (paginasAlocadas[(endereco / 16)] * 16) + (endereco % 16);
            } catch(ArrayIndexOutOfBoundsException e) {
                return -1;
            }
        }
         */

        public boolean interruptHandler(int[] registers, Word[] memory, int programCounter, Interrupts interrupts) {
            switch (interrupts) {
                case INT_SCHEDULER:
                    System.out.println("Escalonador acionado");
                    gp.runEscalonador(programCounter, registers, instructionRegister, interrupts, gp.running.getPaginasAlocadas());
                    return true;

                case INT_INVALID_ADDRESS:
                    System.out.println("Endereço inválido, na linha: " + programCounter);
                    dump(memory[programCounter]);
                    return false;

                // Consideramos, além de uma instrução inválida, o uso de um registrador inválido também
                case INT_INVALID_INSTRUCTION:
                    System.out.println("Comando desconhecido ou registrador inválido, na linha: " + programCounter);
                    dump(memory[programCounter]);
                    return false;

                case INT_OVERFLOW:
                    programCounter--;
                    System.out.println("Deu overflow, na linha: " + programCounter);
                    dump(memory[programCounter]);
                    return false;

                case INT_SYSTEM_CALL:
                    // Entrada (in) (reg[8]=1): o programa lê um inteiro do teclado.
                    // O parâmetro para IN, em reg[9], é o endereço de memória a armazenar a leitura
                    // Saída (out) (reg[8]=2): o programa escreve um inteiro na tela.
                    // O parâmetro para OUT, em reg[9], é o endereço de memória cujo valor deve-se escrever na tela

                    Scanner in = new Scanner(System.in);

                    if (registers[8] == 1) {
                        int address_destiny = registers[9];
                        System.out.println("Insira um número:");
                        int value_to_be_written = in.nextInt();
                        memory[address_destiny].p = value_to_be_written;
                        return true;
                    }

                    if (registers[8] == 2) {
                        int source_adress = registers[9];
                        System.out.println("Output: " + memory[source_adress].p);
                        return true;
                    }
            }
            return true;
        }
    }


    public class GerenciadorMemoria {

        private Word[] mem;
        private int tamPagina;
        private int tamFrame;
        private int nroFrames;
        private boolean[] tabelaPaginas;
        //private int quantidadeDePaginasUsadas; // só para debug
        public int [] framesAlocados;

        public GerenciadorMemoria(Word[] mem, int tamPagina) {
            this.mem = mem;
            this.tamPagina = tamPagina;
            tamFrame = tamPagina;
            nroFrames = mem.length / tamPagina;
            tabelaPaginas = initFrames();
            //quantidadeDePaginasUsadas = 0;
        }

        private boolean[] initFrames() {
            boolean[] free = new boolean[nroFrames];
            for (int i = 0; i < nroFrames; i++) {
                free[i] = true;
            }

            //mockando frames ocupados
            free[0]=false;
            free[2]=false;

            return free;
        }

        public void dump(Word w) {
            System.out.print("[ ");
            System.out.print(w.opc);
            System.out.print(", ");
            System.out.print(w.r1);
            System.out.print(", ");
            System.out.print(w.r2);
            System.out.print(", ");
            System.out.print(w.p);
            System.out.println("  ] ");
        }

        public void dumpMem(Word[] m, int ini, int fim) {
            for (int i = ini; i < fim; i++) {
                System.out.print(i);
                System.out.print(":  ");
                dump(m[i]);
            }
        }

        public int getQuantidadePaginasUsadas(){
            int quantidade = 0;
            for (int i=0; i<tabelaPaginas.length; i++){
                if (tabelaPaginas[i]==false) quantidade++;
            }
            return quantidade;
        }

        public void dumpMemoriaUsada(Word[] m) {
            int fim = getQuantidadePaginasUsadas() * tamPagina;
            for (int i = 0; i < fim; i++) {
                System.out.print(i);
                System.out.print(":  ");
                dump(m[i]);
            }
        }

        public void dumpPagina (Word[]m, int pagina){
            int ini = tamPagina * pagina;
            int fim = ini + tamPagina;
            for (int i = ini; i < fim; i++) {
                System.out.print(i);
                System.out.print(":  ");
                dump(m[i]);
            }
        }


        // retorna null se não conseguir alocar, ou um array com os frames alocadas
        public int[] aloca(Word[] programa) {
            int quantidadePaginas = programa.length / tamPagina;
            if (programa.length % tamPagina > 0) quantidadePaginas++; // vê se ainda tem código além da divisão inteira
            framesAlocados = new int[quantidadePaginas];
            int indiceAlocado = 0;
            int indicePrograma = 0;   //indice do programa

            // testa se tem espaço para alocar o programa
            int framesLivres =0;
            for (int i = 0; i < nroFrames; i++) {
                if (tabelaPaginas[i]) //vê se o frame está vazio e conta 1
                    framesLivres++;
            }

            // se não existe memória suficiente retorna um array com -1
            if (framesLivres <= quantidadePaginas){
                framesAlocados [0] = -1;
                return framesAlocados;
            }

            for (int i = 0; i < nroFrames; i++) {
                if (quantidadePaginas == 0) break;
                if (tabelaPaginas[i]) { //vê se o frame está vazio e aloca o programa ali
                    tabelaPaginas[i] = false; // marca o frame como ocupado

                    for (int j = tamPagina * i; j < tamPagina * (i + 1); j++) {
                        if (indicePrograma >= programa.length) break;
                        mem[j].opc = programa[indicePrograma].opc;
                        mem[j].r1 = programa[indicePrograma].r1;
                        mem[j].r2 = programa[indicePrograma].r2;
                        mem[j].p = programa[indicePrograma].p;
                        indicePrograma++;
                    }
                    framesAlocados[indiceAlocado] = i;
                    indiceAlocado++;
                    quantidadePaginas--;
                }

            }

            return framesAlocados;
        }

        public int[] getFramesAlocados(){
            return framesAlocados;
        }

        public boolean[] getTabelaDePaginas(){
            return tabelaPaginas;
        }

        public void desaloca(PCB processo){
            int[] paginas = processo.getPaginasAlocadas();
            for(int i = 0; i < paginas.length; i ++) {
                tabelaPaginas[paginas[i]] = true; // libera o frame

                // reseta as posicoes da memória
                for (int j = tamPagina * paginas[i]; j < tamPagina * (paginas[i] + 1); j++) {
                    mem[j].opc = Opcode.___;
                    mem[j].r1 = -1;
                    mem[j].r2 = -1;
                    mem[j].p = -1;
                }
            }
        }

    }

    public class GerenciadorProcessos {
        private GerenciadorMemoria gm;
        private Word[] memory;
        private LinkedList<PCB> prontos;
        private int process_id;
        public PCB running;
        public int posicaoEscalonador;

        public GerenciadorProcessos(GerenciadorMemoria gm, Word[] memory) {
            process_id=0;
            this.gm = gm;
            this.memory = memory;
            this.prontos = new LinkedList<>();
            this.posicaoEscalonador = 0;
        }

        public LinkedList<PCB> getProntos() {
            return prontos;
        }

        public void setProntos(LinkedList<PCB> prontos){
            this.prontos = prontos;
        }

        public PCB getRunning(){
            return running;
        }

        public void setRunning(PCB processo){
            running = processo;
        }

        public int[] getPaginasAlocadas (int process_id){
            int [] paginasAlocadas = new int[1];
            boolean achou = false;
            for (int i = 0; i < prontos.size(); i++) {
                if (prontos.get(i).id==process_id){
                    paginasAlocadas = prontos.get(i).paginasAlocadas;
                    achou = true;
                }
            }

            if (achou==false){
                paginasAlocadas= new int[1];
                paginasAlocadas[0]=-1;
            }

            return paginasAlocadas;
        }

        public PCB getProcesso (int process_id){
            PCB processo = null;
            for (int i = 0; i < prontos.size(); i++) {
                if (prontos.get(i).id==process_id){
                    processo = prontos.get(i);
                }
            }
            return processo;
        }

        public int criaProcesso(Word [] programa){
            System.out.println("Processo " + process_id + " criado");
            int[] paginasAlocadas = gm.aloca(programa);

            // Se o processo não foi criado por falta de memória, retorna falso
            if (paginasAlocadas[0]==-1){
                return -1;
            }

            PCB processo = new PCB(process_id, paginasAlocadas, vm.cpu.getPc(), vm.cpu.getReg(), vm.cpu.getIr(), vm.cpu.getInterrupts());
            prontos.add(processo);

            process_id++;

            //debug
            System.out.println("Páginas alocadas");
            for (int i=0; i<paginasAlocadas.length; i++){
                System.out.println(paginasAlocadas[i] + " ");
            }

            return process_id-1;
        }


        public void finalizaProcesso(PCB processo){
            gm.desaloca(processo);
            prontos.remove(processo);
        }

        public void runEscalonador(int programCounter, int [] registradores, Word instructionRegister, Interrupts interrupt, int[] paginasAlocadas){
            running = prontos.get(posicaoEscalonador);

            // seta as variáveis do processo atual com estado atual da CPU
            running.setContext(programCounter, registradores, instructionRegister, interrupt);

            // para poder ciclar a posição do escalonador
            posicaoEscalonador =  (posicaoEscalonador + 1) % prontos.size();

            running = prontos.get(posicaoEscalonador);
            System.out.println("running id " + running.getId());

            // pega o contexto do processo que ira rodar agora
            int programCounterDoRunning = running.getProgramCounter();
            int [] paginasAlocadasDoRunning = running.getPaginasAlocadas();
            int [] registradoresdoRunning = running.getRegistradores();
            Word instructionRegisterDoRunning = running.getInstructionRegister();
            Interrupts interruptsDoRunning = running.getInterrupt();

            vm.cpu.setContext(programCounterDoRunning, paginasAlocadasDoRunning, registradoresdoRunning, instructionRegisterDoRunning, interruptsDoRunning);

        }
    }


    public class PCB {

        private int id;
        public int programCounter;
        private int[] paginasAlocadas;
        public int[] registradores;
        public Word instructionRegister;
        public Interrupts interrupt;

        public PCB(int id, int[]paginasAlocadas, int pc, int [] reg, Word ir, Interrupts interrupt) {
            this.id= id;
            this.paginasAlocadas = paginasAlocadas;
            this.programCounter = pc;
            this.registradores = reg;
            this.instructionRegister = ir;
            this.interrupt = interrupt;

        }

        public int[] getPaginasAlocadas(){
            return this.paginasAlocadas;
        }

        public int getId(){
            return this.id;
        }

        public int getProgramCounter(){
            return programCounter;
        }

        public int[] getRegistradores(){
            return registradores;
        }

        public Word getInstructionRegister(){
            return instructionRegister;
        }

        public Interrupts getInterrupt(){
            return interrupt;
        }

        public void setContext (int programCounter, int[] registradores, Word instructionRegister, Interrupts interrupt){
            this.programCounter = programCounter;
            this.registradores =registradores;
            this.instructionRegister = instructionRegister;
            this.interrupt = interrupt;
        }

    }

    // -------------------------------------------




    // -------------------------------------------------------------------------------------------------------
    // -------------------  S I S T E M A --------------------------------------------------------------------

    public VM vm;
    public Monitor monitor;
    public static Programas progs;
    public GerenciadorMemoria gm;
    public GerenciadorProcessos gp;
    private Escalonador escalonador;
    public int[] registradores;
    public Word instructionRegister;
    public Interrupts interrupt;
    private LinkedList<PCB> prontos;
    private LinkedList<PCB> bloqueados;

    public Sistema(int tamMemoria, int tamPagina, int maxInt, int quantidadeRegistradores, int deltaMax){   // a VM com tratamento de interrupções
        registradores = new int[quantidadeRegistradores];
        interrupt = Interrupts.INT_NONE;
        instructionRegister = new Word(Opcode.___,-1,-1,-1);

        vm = new VM(tamMemoria, tamPagina, maxInt, deltaMax, registradores, interrupt, instructionRegister);
        monitor = new Monitor();
        progs = new Programas();
        prontos = new LinkedList();
        bloqueados = new LinkedList();
        gm = new GerenciadorMemoria(vm.m, tamPagina);
        gp = new GerenciadorProcessos(gm, vm.m);

        this.escalonador = new Escalonador(prontos, vm.cpu);
    }

    public void roda(Word[] programa){
        //monitor.carga(programa, vm.m);
        if (gp.criaProcesso(programa)==-1){
            System.out.println("Falta memoria para rodar o programa");
            return;
        }

        System.out.println("---------------------------------- programa carregado ");

        //monitor.dump(vm.m, 0, programa.length);
        gm.dumpMemoriaUsada(vm.m);

        monitor.executa();
        System.out.println("---------------------------------- após execucao ");

        //monitor.dump(vm.m, 0, programa.length);
        gm.dumpMemoriaUsada(vm.m);

    }

    // Fase 5 - Para demonstrar o funcionamento, você deve ter um sistema iterativo.
    // Uma vez que o sistema esteja funcionando, ele fica esperando comandos.
    // Os comandos possíveis são:
    // cria nomeDePrograma - cria um processo com memória alocada, PCB, etc. que fica em uma lista de processos.
    // esta chamada retorna um identificador único do processo no sistema (ex.: 1, 2, 3 …)
    // executa id - executa o processo com id fornecido. se não houver processo, retorna erro.
    // dump id - lista o conteúdo do PCB e o conteúdo de cada frame de memória do processo com id
    // dumpM inicio fim - lista os frames de memória entre início e fim, independente do processo
    // desaloca id - retira o processo id do sistema, tenha ele executado ou não

    public int cria(Word[] programa){
        int idDoProcessoCriado;
        idDoProcessoCriado = gp.criaProcesso(programa);
        if (idDoProcessoCriado==-1){
            System.out.println("Falta memoria para rodar o programa");
            return idDoProcessoCriado;
        }

        System.out.println("---------------------------------- programa carregado ");
        gm.dumpMemoriaUsada(vm.m);
        return idDoProcessoCriado;
    }

    public void executa(int processId) {
        System.out.println("Iniciando execução do processo");
        int [] paginasAlocadas = gp.getPaginasAlocadas(processId);
        if (paginasAlocadas[0]==-1){
            System.out.println("Processo não existe");
            return;
        }
        System.out.println("Páginas alocadas");
        for (int i=0; i<paginasAlocadas.length; i++){
            System.out.println(paginasAlocadas[i] + " ");
        }

        PCB running = gp.getProcesso(processId);

        // pega o contexto do processo que está rodando
        int programCounterDoRunning = running.getProgramCounter();
        int [] paginasAlocadasDoRunning = running.getPaginasAlocadas();
        int [] registradoresdoRunning = running.getRegistradores();
        Word instructionRegisterDoRunning = running.getInstructionRegister();
        Interrupts interruptsDoRunning = running.getInterrupt();

        vm.cpu.setContext(programCounterDoRunning, paginasAlocadasDoRunning, registradoresdoRunning, instructionRegisterDoRunning, interruptsDoRunning);
        //vm.cpu.setContext(0, paginasAlocadas, registradores, instructionRegister, interrupt);          // monitor seta contexto - pc aponta para inicio do programa
        vm.cpu.run();                  //                         e cpu executa
        System.out.println("---------------------------------- programa executado ");
        gm.dumpMemoriaUsada(vm.m);
    }

    public void executaComEscalonador() {
        System.out.println("Iniciando execução dos processos prontos com escalonador");
        vm.cpu.setEscalonadorState(true);
        prontos = gp.getProntos();
        PCB running = prontos.getFirst();
        gp.setRunning(running);

        // pega o contexto do processo que está rodando
        int programCounterDoRunning = running.getProgramCounter();
        int [] paginasAlocadasDoRunning = running.getPaginasAlocadas();
        int [] registradoresdoRunning = running.getRegistradores();
        Word instructionRegisterDoRunning = running.getInstructionRegister();
        Interrupts interruptsDoRunning = running.getInterrupt();

        vm.cpu.setContext(programCounterDoRunning, paginasAlocadasDoRunning, registradoresdoRunning, instructionRegisterDoRunning, interruptsDoRunning);

        vm.cpu.run();
        System.out.println("---------------------------------- Escalonador executado ");
        gm.dumpMemoriaUsada(vm.m);
    }

    public void dump (int processId){
        System.out.println("----------- dump do processo " + processId + "------------------");
        int [] paginasAlocadas = gp.getPaginasAlocadas(processId);

        for (int i=0; i<paginasAlocadas.length; i++){
            gm.dumpPagina(vm.m, paginasAlocadas[i]);
        }
    }

    public void dumpM (int inicio, int fim){
        System.out.println("----------- dump com inicio em " + inicio + " e fim em " + fim);
        for (int i = inicio; i<=fim; i++){
            //System.out.println("fazendo dumop da página " + i);
            gm.dumpPagina(vm.m, i);
        }
    }

    public void desaloca (int processId){
        PCB processo = gp.getProcesso(processId);
        int [] paginasAlocadas = gp.getPaginasAlocadas(processId);
        gp.finalizaProcesso(processo);
        System.out.println("--------------Processo " + processId + " desalocado---------------");
        for (int i=0; i<paginasAlocadas.length; i++){
            gm.dumpPagina(vm.m, paginasAlocadas[i]);
        }

    }

    // Fase 6 - Escalonador rodando no Sistema
    public void runEscalonador() {
        System.out.println("Iniciando Escalonador");
        escalonador.run();
    }

    public class Escalonador {

        private LinkedList<PCB> prontos;
        private int pointer;
        private PCB runningProcess;
        private CPU cpu;

        public Escalonador(LinkedList<PCB> prontos, CPU cpu) {
            this.prontos = prontos;
            this.pointer = 0;
            this.cpu = cpu;
        }

        public void run() {
            while(true){
                if(prontos.isEmpty()==false) break; //Se esvaziou lista de prontos, termina.
                PCB pcb = prontos.get(pointer);
                this.runningProcess = pcb;
                int old = pointer;
                pointer = pointer + 1;
                prontos.remove(old);
                cpu.setContext(cpu.getPc(), pcb.getPaginasAlocadas(), cpu.getReg(), cpu.getIr(), cpu.getInterrupts());
            }
        }

        public PCB getRunningProcess() {
            return runningProcess;
        }

        public void setRunningProcessAsNull(){
            this.runningProcess = null;
        }

        public LinkedList<PCB> getProntos() {
            return prontos;
        }
    }

    // -------------------  S I S T E M A - fim --------------------------------------------------------------

}


