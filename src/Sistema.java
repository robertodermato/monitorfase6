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

    public class Word {    // cada posicao da memoria tem uma instrucao (ou um dado)
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
        INT_SYSTEM_CALL;            // Ativa chamada de I/O pelo comando TRAP
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

        // cria variável interrupção
        public Interrupts interrupts;

        private Word[] m;   // CPU acessa MEMORIA, guarda referencia 'm' a ela. memoria nao muda. ee sempre a mesma.

        public CPU(Word[] _m, int tamPaginaMemoria, int maxInt) {     // ref a MEMORIA e interrupt handler passada na criacao da CPU
            m = _m;                // usa o atributo 'm' para acessar a memoria.
            reg = new int[10];        // aloca o espaço dos registradores
            this.maxInt = maxInt;          // números aceitos -100_000 até 100_000
            this.tamPaginaMemoria = tamPaginaMemoria;
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

        public Interrupts getInterrupts(){
            return interrupts;
        }

        public int [] getReg(){
            return reg;
        }

        public int getPc(){
            return pc;
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
            try {
                return (paginasAlocadas[(endereco / tamPaginaMemoria)] * tamPaginaMemoria) + (endereco % tamPaginaMemoria);

            } catch(ArrayIndexOutOfBoundsException e) {
                System.out.println("Retorno -1 do traduz");
                return -1;
            }
        }

        public void run() {        // execucao da CPU supoe que o contexto da CPU, vide acima, esta devidamente setado
            //System.out.println("Início da execução pela CPU");

            boolean run = true;
            while (run) {            // ciclo de instrucoes. acaba cfe instrucao, veja cada caso.
                // FETCH

                //if (isAddressValid(traduzEndereco(pc))) {
                //    ir = m[traduzEndereco(pc)];
                //}

                ir = m[traduzEndereco(pc)];    // busca posicao da memoria apontada por pc, guarda em ir

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

        public VM(int tamMem, int tamanhoPaginaMemoria, int maxInt) {
            // memória
            this.tamMem = tamMem;
            this.tamanhoPaginaMemoria = tamanhoPaginaMemoria;
            m = new Word[tamMem]; // m ee a memoria
            for (int i = 0; i < tamMem; i++) {
                m[i] = new Word(Opcode.___, -1, -1, -1);
            }
            ;

            // cpu
            cpu = new CPU(m, tamanhoPaginaMemoria, maxInt);   // cpu acessa memória
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

        public GerenciadorProcessos(GerenciadorMemoria gm, Word[] memory) {
            process_id=0;
            this.gm = gm;
            this.memory = memory;
            this.prontos = new LinkedList<>();
        }

        public LinkedList<PCB> getProntos() {
            return prontos;
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

            PCB processo = new PCB(process_id, paginasAlocadas);
            process_id++;
            prontos.add(processo);

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
    }


    public class PCB {

        private int id;
        public int programCounter;
        private int[] paginasAlocadas;
        public int[] registradores;
        public Word instructionRegister;
        public Interrupts interrupt;

        public PCB(int id, int[]paginasAlocadas) {
            this.id= id;
            this.paginasAlocadas = paginasAlocadas;
        }

        public int[] getPaginasAlocadas(){
            return this.paginasAlocadas;
        }

        public int getId(){
            return this.id;
        }

        public void setProgramCounter(int pc){
            programCounter = pc;
        }

        public int getProgramCounter(){
            return programCounter;
        }

        public void setRegistradores(int [] registradores){
            this.registradores = registradores;
        }

        public int[] getRegistradores(){
            return registradores;
        }

        public void setInstructionRegister(Word ir){
            instructionRegister = ir;
        }

        public Word getInstructionRegister(){
            return instructionRegister;
        }

        public void setInterrupt(Interrupts interrupt){
            this.interrupt = interrupt;
        }

        public Interrupts getInterrupt(){
            return interrupt;
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

    public Sistema(int tamMemoria, int tamPagina, int maxInt, int quantidadeRegistradores){   // a VM com tratamento de interrupções
        vm = new VM(tamMemoria, tamPagina, maxInt);
        monitor = new Monitor();
        progs = new Programas();
        prontos = new LinkedList();
        bloqueados = new LinkedList();
        gm = new GerenciadorMemoria(vm.m, tamPagina);
        gp = new GerenciadorProcessos(gm, vm.m);
        registradores = new int[quantidadeRegistradores];
        instructionRegister = new Word(Opcode.___,-1,-1,-1);
        interrupt = Interrupts.INT_NONE;
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
        vm.cpu.setContext(0, paginasAlocadas, registradores, instructionRegister, interrupt);          // monitor seta contexto - pc aponta para inicio do programa
        vm.cpu.run();                  //                         e cpu executa
        System.out.println("---------------------------------- programa executado ");
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
    // -------------------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------
    // ------------------- instancia e testa sistema
    public static void main(String args[]) {
        // Variáveis
        int tamanhoDamemoria = 1024;
        int tamanhoDaPaginadeMemoria = 16;
        int maxInt = 100_000;
        int quantidadeRegistradores = 10;

        Sistema s = new Sistema(tamanhoDamemoria, tamanhoDaPaginadeMemoria, maxInt, quantidadeRegistradores);

        // Desenvolvidos pelo professor
        //s.roda(progs.fibonacci10);           // "progs" significa acesso/referencia ao programa em memoria secundaria
        //s.roda(progs.progMinimo);

        // Fase 1
        //s.roda(progs.fatorial);
        //s.roda(progs.fibonacci2);
        //s.roda(progs.fatorial2);
        //s.roda(progs.bubbleSort);

        // Fase 2 - Testes de Interrupções
        //s.roda(progs.invalidAddressTest);
        //s.roda(progs.overflowTest);
        //s.roda(progs.invalidRegisterTest);

        // Fase 3 - Testes de Chamadas de Sistema
        //s.roda(progs.trapTestOutput);
        //s.roda(progs.trapTestInput);
        //s.roda(progs.fibonacciComOutput);
        //s.roda(progs.fatorialComInput);

        // Fase 4 - Testa o Gerenciador de Memória - mockamos frames 0 e 2 como sempre ocupados
        //s.roda(progs.bubbleSort);
        //s.roda(progs.fatorial);
        //s.roda(progs.fatorial);
        //s.roda(progs.fatorial);
        //s.roda(progs.fatorial);
        //s.roda(progs.fibonacci10);
        //s.roda(progs.fibonacci10);
        //s.roda(progs.fibonacci10);
        //s.roda(progs.progMinimo);
        //s.roda(progs.progMinimo);
        //s.roda(progs.progMinimo);
        //s.roda(progs.bubbleSort);
        //s.roda(progs.bubbleSort);
        //s.roda(progs.bubbleSort);

        // Fase 5 - Gernciador de Processos
        s.cria(progs.bubbleSort);
        s.cria(progs.bubbleSort);
        s.cria(progs.bubbleSort);
        //s.cria(progs.fatorial);
        //s.cria(progs.fatorial);
        //s.cria(progs.fatorial);
        //s.executa(5);
        //s.dump(2);
        //s.dumpM(2,5);
        //s.desaloca(2);

    }

    // -------------------------------------------------------------------------------------------------------
    // --------------- TUDO ABAIXO DE MAIN É AUXILIAR PARA FUNCIONAMENTO DO SISTEMA - nao faz parte

    //  -------------------------------------------- programas aa disposicao para copiar na memoria (vide carga)
    public class Programas {
        public Word[] progMinimo = new Word[] {
                //       OPCODE      R1  R2  P         :: VEJA AS COLUNAS VERMELHAS DA TABELA DE DEFINICAO DE OPERACOES
                //                                     :: -1 SIGNIFICA QUE O PARAMETRO NAO EXISTE PARA A OPERACAO DEFINIDA
                new Word(Opcode.LDI, 0, -1, 999),
                new Word(Opcode.STD, 0, -1, 10),
                new Word(Opcode.STD, 0, -1, 11),
                new Word(Opcode.STD, 0, -1, 12),
                new Word(Opcode.STD, 0, -1, 13),
                new Word(Opcode.STD, 0, -1, 14),
                new Word(Opcode.STOP, -1, -1, -1) };

        public Word[] fibonacci10 = new Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
                new Word(Opcode.LDI, 1, -1, 0),  //0 coloca 0 no reg 1
                new Word(Opcode.STD, 1, -1, 20),    // 20 posicao de memoria onde inicia a serie de fibonacci gerada //1, ou seja coloca valor de reg 1 (0) na posicao 20
                new Word(Opcode.LDI, 2, -1, 1), //2 coloca 1 no reg 2
                new Word(Opcode.STD, 2, -1, 21), //3 na posição 21 coloca o que está em no reg 2, ou seja coloca 1 na posicao 21
                new Word(Opcode.LDI, 0, -1, 22), //4 coloca 22 no reg 0
                new Word(Opcode.LDI, 6, -1, 6), //5 coloca 6 no reg 6 - linha do inicio do loop
                new Word(Opcode.LDD, 7, -1, 17), //6 coloca 17 no reg 7. É o contador. será a posição one começam os dados, ou seja 20 + a quantidade de números fibonacci que queremos
                new Word(Opcode.LDI, 3, -1, 0), //7 coloca 0 no reg 3
                new Word(Opcode.ADD, 3, 1, -1), //8
                new Word(Opcode.LDI, 1, -1, 0), //9
                new Word(Opcode.ADD, 1, 2, -1), //10 add reg 1 + reg 2
                new Word(Opcode.ADD, 2, 3, -1), //11 add reg 2 + reg 3
                new Word(Opcode.STX, 0, 2, -1), //12 coloca o que está em reg 2 (1) na posição  memória do reg 0 (22), ou seja coloca 1 na pos 22
                new Word(Opcode.ADDI, 0, -1, 1), //13 add 1 no reg 0, ou seja reg fica com 23. Isso serve para mudar a posição da memória onde virá o próximo numero fbonacci
                new Word(Opcode.SUB, 7, 0, -1), //14 reg 7 = reg 7 - o que esta no reg 0, ou seja 30 menos 23 e coloca em r7. Isso é o contador regressivo que fica em r7. se for 0, pára
                new Word(Opcode.JMPIG, 6, 7, -1), //15 se r7 maior que 0 então pc recebe 6, else pc = pc + 1
                new Word(Opcode.STOP, -1, -1, -1),   // POS 16
                new Word(Opcode.DATA, -1, -1, 31), //17 numeros de fibonacci a serem calculados menos 20
                new Word(Opcode.DATA, -1, -1, -1), //18 números de fibonacci a serem calculados
                new Word(Opcode.DATA, -1, -1, -1), //19
                new Word(Opcode.DATA, -1, -1, -1),   // POS 20
                new Word(Opcode.DATA, -1, -1, -1), //21
                new Word(Opcode.DATA, -1, -1, -1), //22
                new Word(Opcode.DATA, -1, -1, -1), //23
                new Word(Opcode.DATA, -1, -1, -1), //24
                new Word(Opcode.DATA, -1, -1, -1), //25
                new Word(Opcode.DATA, -1, -1, -1), //26
                new Word(Opcode.DATA, -1, -1, -1), //27
                new Word(Opcode.DATA, -1, -1, -1), //28
                new Word(Opcode.DATA, -1, -1, -1),  // ate aqui - serie de fibonacci ficara armazenada //29
                new Word(Opcode.DATA, -1, -1, -1)
        };

        // programa que lê o número na posição 21 da memória:
        // - se número < 0: coloca -1 no início da posição de memória para saída, que é 23;
        // - se número > 0: este é o número de valores da sequencia de fibonacci a serem escritos
        // Lembrando que mais 20 números gerará overflow em nosso sistema
        public Word[] fibonacci2 = new Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
                new Word(Opcode.LDD, 4, -1, 22),    // 0- onde 22 é a posição da memória onde esta a quantidade de números Fibonacci a serem calculados

                // testa se número é menor que 0, e se for manda para final do programa
                new Word(Opcode.JMPILM, -1, 4, 23), // 1- pula para a linha amrazenada em [23], que é a linha de final do programa, se r4<0

                new Word(Opcode.ADDI, 4, -1, 24),   // 2- onde 24 é a primeira posição da memória com dados da fibonacci
                new Word(Opcode.STD, 4, -1, 21),    // 3- armazena o contador na posição 21 da memória

                // armazena valores iniciais da Fibonacci
                new Word(Opcode.LDI, 1, -1, 0),     // 4- coloca 0 no reg 1
                new Word(Opcode.STD, 1, -1, 24),    // 5- 23 posicao de memoria onde inicia a serie de fibonacci gerada, ou seja coloca valor de reg 1 (0) na posicao 23
                new Word(Opcode.LDI, 2, -1, 1),     // 6- coloca 1 no reg 2
                new Word(Opcode.STD, 2, -1, 25),    // 7- na posição 24 coloca o que está em no reg 2, ou seja coloca 1 na posicao 24
                new Word(Opcode.LDI, 0, -1, 26),    // 8- coloca 25 no reg 0

                // início do loop
                new Word(Opcode.LDI, 6, -1, 10),    // 9- coloca 9 no reg 6, onde 9 é a linha do início do loop
                new Word(Opcode.LDD, 7, -1, 21),    // 10- coloca 20 no reg 7. É a posição do o contador.
                new Word(Opcode.LDI, 3, -1, 0),     // 11- coloca 0 no reg 3
                new Word(Opcode.ADD, 3, 1, -1),     // 12-
                new Word(Opcode.LDI, 1, -1, 0),     // 13-
                new Word(Opcode.ADD, 1, 2, -1),     // 14- 0 add reg 1 + reg 2
                new Word(Opcode.ADD, 2, 3, -1),     // 15- 1 add reg 2 + reg 3
                new Word(Opcode.STX, 0, 2, -1),     // 16- coloca o que está em reg 2 (1) na posição  memória do reg 0 (22), ou seja coloca 1 na pos 22
                new Word(Opcode.ADDI, 0, -1, 1),    // 17- add 1 no reg 0, ou seja reg fica com 23. Isso serve para mudar a posição da memória onde virá o próximo numero fbonacci
                new Word(Opcode.SUB, 7, 0, -1),     // 18- reg 7 = reg 7 - o que esta no reg 0, ou seja 30 menos 23 e coloca em r7. Isso é o contador regressivo que fica em r7. se for 0, pára
                new Word(Opcode.JMPIG, 6, 7, -1),   // 19- se r7 maior que 0 então pc recebe 6, else pc = pc + 1
                new Word(Opcode.STOP, -1, -1, -1),  // 20- fim

                // memória
                new Word(Opcode.DATA, -1, -1, -1),  // 21- posição do contador
                new Word(Opcode.DATA, -1, -1, 8),   // 22- números Fibonacci a serem calculados
                new Word(Opcode.DATA, -1, -1, 20),  // 23- linha do final do programa
                new Word(Opcode.DATA, -1, -1, -1),  // 24- início do armazenamento da sequência Fibonacci
                new Word(Opcode.DATA, -1, -1, -1),  // 25-
                new Word(Opcode.DATA, -1, -1, -1),  // 26-
                new Word(Opcode.DATA, -1, -1, -1),  // 27-
                new Word(Opcode.DATA, -1, -1, -1),  // 28-
                new Word(Opcode.DATA, -1, -1, -1),  //...
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1)
        };

        // Dado um inteiro em na posição X da memória,
        // se for negativo armazena -1 na saída; se for positivo responde o fatorial do número na saída
        public Word[] fatorial2 = new Word[] { 	 // este fatorial so aceita valores positivos.   nao pode ser zero
                new Word(Opcode.DATA, -1, -1, 1),   // 0- número a ser calculado o fatorial
                new Word(Opcode.DATA, -1, -1, 12),  // 1- armazena o final do programa
                new Word(Opcode.LDD, 0, -1, 0),     // 2- coloca em reg 0 o valor da memória na posição 0
                new Word(Opcode.LDI, 1, -1, -1),    // 3- deixa reg 1 com -1 por padrão

                // testa se número é menor que 0, e se for manda para final do programa
                new Word(Opcode.JMPILM, -1, 0, 1),  // 4- pula para a linha amrazenada em [1], que é a linha de final do programa, se r0<0

                new Word(Opcode.LDI, 1, -1, 1),      // 5   	r1 é 1 para multiplicar (por r0)
                new Word(Opcode.LDI, 6, -1, 1),      // 6   	r6 é 1 para ser o decremento
                new Word(Opcode.LDI, 7, -1, 12),     // 7   	r7 tem posicao de stop do programa

                // início do loop
                new Word(Opcode.JMPIE, 7, 0, 0),     // 8   	se r0=0 pula para r7(=12)
                new Word(Opcode.MULT, 1, 0, -1),     // 9   	r1 = r1 * r0
                new Word(Opcode.SUB, 0, 6, -1),      // 10   	decrementa r0 1
                new Word(Opcode.JMP, -1, -1, 8),     // 11   	vai p posicao 8, que é o início do loop

                new Word(Opcode.STD, 1, -1, 14),      // 12   	coloca valor de r1 na posição 14
                new Word(Opcode.STOP, -1, -1, -1),    // 13   	stop
                new Word(Opcode.DATA, -1, -1, -1) };  // 14   ao final o valor do fatorial estará na posição 10 da memória


        public Word[] fatorial = new Word[] { 	 // este fatorial so aceita valores positivos.   nao pode ser zero
                // linha   coment
                new Word(Opcode.LDI, 0, -1, 6),      // 0   	r0 é valor a calcular fatorial
                new Word(Opcode.LDI, 1, -1, 1),      // 1   	r1 é 1 para multiplicar (por r0)
                new Word(Opcode.LDI, 6, -1, 1),      // 2   	r6 é 1 para ser o decremento
                new Word(Opcode.LDI, 7, -1, 8),      // 3   	r7 tem posicao de stop do programa = 8
                new Word(Opcode.JMPIE, 7, 0, 0),     // 4   	se r0=0 pula para r7(=8)
                new Word(Opcode.MULT, 1, 0, -1),     // 5   	r1 = r1 * r0
                new Word(Opcode.SUB, 0, 6, -1),      // 6   	decrementa r0 1
                new Word(Opcode.JMP, -1, -1, 4),     // 7   	vai p posicao 4
                new Word(Opcode.STD, 1, -1, 10),     // 8   	coloca valor de r1 na posição 10
                new Word(Opcode.STOP, -1, -1, -1),    // 9   	stop
                new Word(Opcode.DATA, -1, -1, -1) };  // 10   ao final o valor do fatorial estará na posição 10 da memória

        public Word[] invalidAddressTest = new Word []{
                new Word(Opcode.LDD, 0, -1, 1025),
                new Word(Opcode.STOP, -1, -1, -1)
        };

        public Word[] overflowTest = new Word []{
                new Word(Opcode.LDI, 0, -1, 80800),
                new Word(Opcode.LDI, 1, -1, 80800),
                new Word(Opcode.MULT, 0, 1, -1),
                new Word(Opcode.STOP, -1, -1, -1)
        };

        public Word[] bubbleSort = new Word[]{
                // Posições dos loops
                new Word(Opcode.DATA, -1, -1, 41),  // 0- jump do primeiro loop gm ou em
                new Word(Opcode.DATA, -1, -1, 9),   // 1- jump do segundo loop gm ou em
                new Word(Opcode.DATA, -1, -1, 34),  // 2- jump do terceiro loop lm

                // não usadas, mas mantidas devido a dificuldade de refatorar esses códigos
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),

                new Word(Opcode.LDD, 1, -1, 43), // 6- reg 1 vai guardar o tamanho do vetor para comparações
                new Word(Opcode.LDI, 2, -1, 0),  // 7- apenas inicializa vetor 2 com 0
                new Word(Opcode.LDI, 3, -1, 0),  // 8- apenas inicializa vetor 3 com 0

                // início loop externo
                new Word(Opcode.LDI, 5, -1, 0),     // linha 9
                new Word(Opcode.ADD, 5, 2, -1),
                new Word(Opcode.SUB, 5, 1, -1),
                new Word(Opcode.JMPIGM, -1, 5, 0),  // linha 12 - pula pra linha 41 que é o fim (armazenado na memória [0])
                new Word(Opcode.JMPIEM, -1, 5, 0),
                new Word(Opcode.ADDI, 2, -1, 1),
                new Word(Opcode.LDI, 3, -1, 0),     // 15-

                // início loop interno
                new Word(Opcode.LDI, 5, -1, 0),     // 16-
                new Word(Opcode.ADD, 5, 3, -1),
                new Word(Opcode.ADDI, 5, -1, 1),

                // Verifica se chegou ao final do vetor. Se sim,reinicia comparações.
                new Word(Opcode.SUB, 5, 1, -1),     // 19-
                new Word(Opcode.JMPIEM, -1, 5, 1),  // 20- Pula para linha 9 (armazenado na memória [1]). Loop externo
                new Word(Opcode.JMPIGM, -1, 5, 1),
                // fim loop externo

                // Coloca nos registradores 4 e 5 dois números adjacentes do vetor
                new Word(Opcode.LDI, 4, -1, 44),    // 21- coloca a posição da memória de início do vetor [44] no reg 4
                new Word(Opcode.ADD, 4, 3, -1),     // 22-
                new Word(Opcode.LDI, 5, -1, 1),     // 23-
                new Word(Opcode.ADD, 5, 4, -1),     // 24-
                new Word(Opcode.LDX, 4, 4, -1),
                new Word(Opcode.LDX, 5, 5, -1),

                new Word(Opcode.ADDI, 3, -1, 1),    // 27- é o incremento da posição do vetor

                // Testa se o segundo número é menor que o primeiro
                new Word(Opcode.LDI, 6, -1, 0),
                new Word(Opcode.ADD, 6, 5, -1),
                new Word(Opcode.SUB, 6, 4, -1),
                new Word(Opcode.JMPILM, -1, 6, 2),  // 32- pula pra linha 34 se o segundo número é menor que o primeiro (amazenado na memória [2])
                new Word(Opcode.JMP, -1, -1, 16),   // 33- se não for volta pro início do loop interno
                // fim loop interno

                // Faz swap de dois números, se o segundo for menor que o primeiro
                new Word(Opcode.SWAP, 5, 4, -1),    // 34-
                new Word(Opcode.LDI, 6, -1, 43),    // 35-
                new Word(Opcode.ADD, 6, 3, -1),
                new Word(Opcode.STX, 6, 4, -1),
                new Word(Opcode.ADDI, 6, -1, 1),
                new Word(Opcode.STX, 6, 5, -1),
                new Word(Opcode.JMP, -1, -1, 16),   // 40-

                new Word(Opcode.STOP, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1),  // 42- não usada
                new Word(Opcode.DATA, -1, -1, 6),   // 43- tamanho do vetor
                new Word(Opcode.DATA, -1, -1, 12),  // 44- dados do vetor a partir daqui até o final
                new Word(Opcode.DATA, -1, -1, 7),
                new Word(Opcode.DATA, -1, -1, 9),
                new Word(Opcode.DATA, -1, -1, 1),
                new Word(Opcode.DATA, -1, -1, 4),
                new Word(Opcode.DATA, -1, -1, 3)
        };

        public Word[] trapTestOutput = new Word[]{
                new Word(Opcode.LDI, 1, -1, 44),    //coloca 44 no reg 1. Esse será o valor mostrado no output
                new Word(Opcode.STD, 1, -1, 6),     // coloca o valor de reg1 na posição 6 da memória
                new Word(Opcode.LDI, 8, -1, 2),     // coloca 2 em reg 8 para criar um trap de out
                new Word(Opcode.LDI, 9,-1,6),       // coloca 6 no reg 9, ou seja a posição onde será feita a leitura
                new Word(Opcode.TRAP,-1,-1,-1),     // faz o output da posição 6
                new Word(Opcode.STOP, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1)
        };

        public Word[] trapTestInput = new Word[]{
                new Word(Opcode.LDI, 8, -1, 1),     // coloca 2 em reg 8 para criar um trap de input
                new Word(Opcode.LDI, 9,-1,4),       // coloca 4 no reg 9, ou seja a posição onde será feita a escrita
                new Word(Opcode.TRAP,-1,-1,-1),     // faz o input e armazena na posição 4
                new Word(Opcode.STOP, -1, -1, -1),
                new Word(Opcode.DATA, -1, -1, -1)   // valor será armazenado aqui
        };

        public Word[] invalidRegisterTest = new Word[]{
                new Word(Opcode.LDD, 11, -1, 1),
                new Word(Opcode.STOP, -1, -1, -1)
        };

        public Word[] fibonacciComOutput = new Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
                new Word(Opcode.LDI, 1, -1, 0),  //0 coloca 0 no reg 1
                new Word(Opcode.STD, 1, -1, 23),    // 20 posicao de memoria onde inicia a serie de fibonacci gerada //1, ou seja coloca valor de reg 1 (0) na posicao 20
                new Word(Opcode.LDI, 2, -1, 1), //2 coloca 1 no reg 2
                new Word(Opcode.STD, 2, -1, 24), //3 na posição 21 coloca o que está em no reg 2, ou seja coloca 1 na posicao 21
                new Word(Opcode.LDI, 0, -1, 25), //4 coloca 22 no reg 0
                new Word(Opcode.LDI, 6, -1, 6), //5 coloca 6 no reg 6 - linha do inicio do loop
                new Word(Opcode.LDI, 7, -1, 34), //6 coloca 34 no reg 7. É o contador. será a posição one começam os dados, ou seja 23 + a quantidade de números fibonacci que queremos
                new Word(Opcode.LDI, 3, -1, 0), //7 coloca 0 no reg 3
                new Word(Opcode.ADD, 3, 1, -1), //8
                new Word(Opcode.LDI, 1, -1, 0), //9
                new Word(Opcode.ADD, 1, 2, -1), //10 add reg 1 + reg 2
                new Word(Opcode.ADD, 2, 3, -1), //11 add reg 2 + reg 3
                new Word(Opcode.STX, 0, 2, -1), //12 coloca o que está em reg 2 (1) na posição  memória do reg 0 (22), ou seja coloca 1 na pos 22
                new Word(Opcode.ADDI, 0, -1, 1), //13 add 1 no reg 0, ou seja reg fica com 23. Isso serve para mudar a posição da memória onde virá o próximo numero fbonacci
                new Word(Opcode.SUB, 7, 0, -1), //14 reg 7 = reg 7 - o que esta no reg 0, ou seja 30 menos 23 e coloca em r7. Isso é o contador regressivo que fica em r7. se for 0, pára
                new Word(Opcode.JMPIG, 6, 7, -1), //15 se r7 maior que 0 então pc recebe 6, else pc = pc + 1

                // output
                new Word(Opcode.LDI, 8, -1, 2),     // coloca 2 em reg 8 para criar um trap de out
                new Word(Opcode.LDI, 9,-1,33),      // coloca 6 no reg 9, ou seja a posição onde será feita a leitura
                new Word(Opcode.TRAP,-1,-1,-1),     // faz o output da posição 10

                // memória
                new Word(Opcode.STOP, -1, -1, -1),   // POS 16
                new Word(Opcode.DATA, -1, -1, 31), //17 numeros de fibonacci a serem calculados menos 20
                new Word(Opcode.DATA, -1, -1, -1), //18
                new Word(Opcode.DATA, -1, -1, -1), //19
                new Word(Opcode.DATA, -1, -1, -1),   // POS 20
                new Word(Opcode.DATA, -1, -1, -1), //21
                new Word(Opcode.DATA, -1, -1, -1), //22
                new Word(Opcode.DATA, -1, -1, -1), //23
                new Word(Opcode.DATA, -1, -1, -1), //24
                new Word(Opcode.DATA, -1, -1, -1), //25
                new Word(Opcode.DATA, -1, -1, -1), //26
                new Word(Opcode.DATA, -1, -1, -1), //27
                new Word(Opcode.DATA, -1, -1, -1), //28
                new Word(Opcode.DATA, -1, -1, -1),  // ate aqui - serie de fibonacci ficara armazenada //29
                new Word(Opcode.DATA, -1, -1, -1)
        };

        // Usuário faz input de um inteiro que é armazenado na posição 3 da memória,
        // se for negativo armazena -1 na saída [17]; se for positivo responde o fatorial do número na saída[17]
        public Word[] fatorialComInput = new Word[] {
                // input
                new Word(Opcode.LDI, 8, -1, 1),    // 0- coloca 1 em reg 8 para criar um trap de input
                new Word(Opcode.LDI, 9,-1,3),      // 1- coloca 3 no reg 9, ou seja a posição onde será feita a escrita do input
                new Word(Opcode.TRAP,-1,-1,-1),    // 2- faz o input

                new Word(Opcode.DATA, -1, -1, -1),   // 3- número a ser calculado o fatorial
                new Word(Opcode.DATA, -1, -1, 15),  // 4- armazena o final do programa
                new Word(Opcode.LDD, 0, -1, 3),     // 5- coloca em reg 0 o valor da memória na posição 3, que é o número a ser calculado
                new Word(Opcode.LDI, 1, -1, -1),    // 6- deixa reg 1 com -1 por padrão

                // testa se número é menor que 0, e se for manda para final do programa
                new Word(Opcode.JMPILM, -1, 0, 1),  // 7- pula para a linha amrazenada em [1], que é a linha de final do programa, se r0<0

                new Word(Opcode.LDI, 1, -1, 1),      // 8   	r1 é 1 para multiplicar (por r0)
                new Word(Opcode.LDI, 6, -1, 1),      // 9   	r6 é 1 para ser o decremento
                new Word(Opcode.LDI, 7, -1, 15),     // 10   	r7 tem posicao de stop do programa

                // início do loop
                new Word(Opcode.JMPIE, 7, 0, 0),     // 11   	se r0=0 pula para r7(=15)
                new Word(Opcode.MULT, 1, 0, -1),     // 12   	r1 = r1 * r0
                new Word(Opcode.SUB, 0, 6, -1),      // 13   	decrementa r0 1
                new Word(Opcode.JMP, -1, -1, 11),     // 14   	vai p posicao 11, que é o início do loop

                new Word(Opcode.STD, 1, -1, 17),      // 15   	coloca valor de r1 na posição 17
                new Word(Opcode.STOP, -1, -1, -1),    // 16  	stop
                new Word(Opcode.DATA, -1, -1, -1) };  // 17   ao final o valor do fatorial estará na posição 17 da memória

    }
}


