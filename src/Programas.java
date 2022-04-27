public class Programas {

    // -------------------------------------------------------------------------------------------------------
    // --------------- TUDO ABAIXO DE MAIN É AUXILIAR PARA FUNCIONAMENTO DO SISTEMA - nao faz parte

    //  -------------------------------------------- programas aa disposicao para copiar na memoria (vide carga)
    public Sistema.Word[] progMinimo = new Sistema.Word[] {
            //       OPCODE      R1  R2  P         :: VEJA AS COLUNAS VERMELHAS DA TABELA DE DEFINICAO DE OPERACOES
            //                                     :: -1 SIGNIFICA QUE O PARAMETRO NAO EXISTE PARA A OPERACAO DEFINIDA
            new Sistema.Word(Sistema.Opcode.LDI, 0, -1, 999),
            new Sistema.Word(Sistema.Opcode.STD, 0, -1, 10),
            new Sistema.Word(Sistema.Opcode.STD, 0, -1, 11),
            new Sistema.Word(Sistema.Opcode.STD, 0, -1, 12),
            new Sistema.Word(Sistema.Opcode.STD, 0, -1, 13),
            new Sistema.Word(Sistema.Opcode.STD, 0, -1, 14),
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1) };

    public Sistema.Word[] fibonacci10 = new Sistema.Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 0),  //0 coloca 0 no reg 1
            new Sistema.Word(Sistema.Opcode.STD, 1, -1, 20),    // 20 posicao de memoria onde inicia a serie de fibonacci gerada //1, ou seja coloca valor de reg 1 (0) na posicao 20
            new Sistema.Word(Sistema.Opcode.LDI, 2, -1, 1), //2 coloca 1 no reg 2
            new Sistema.Word(Sistema.Opcode.STD, 2, -1, 21), //3 na posição 21 coloca o que está em no reg 2, ou seja coloca 1 na posicao 21
            new Sistema.Word(Sistema.Opcode.LDI, 0, -1, 22), //4 coloca 22 no reg 0
            new Sistema.Word(Sistema.Opcode.LDI, 6, -1, 6), //5 coloca 6 no reg 6 - linha do inicio do loop
            new Sistema.Word(Sistema.Opcode.LDD, 7, -1, 17), //6 coloca 17 no reg 7. É o contador. será a posição one começam os dados, ou seja 20 + a quantidade de números fibonacci que queremos
            new Sistema.Word(Sistema.Opcode.LDI, 3, -1, 0), //7 coloca 0 no reg 3
            new Sistema.Word(Sistema.Opcode.ADD, 3, 1, -1), //8
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 0), //9
            new Sistema.Word(Sistema.Opcode.ADD, 1, 2, -1), //10 add reg 1 + reg 2
            new Sistema.Word(Sistema.Opcode.ADD, 2, 3, -1), //11 add reg 2 + reg 3
            new Sistema.Word(Sistema.Opcode.STX, 0, 2, -1), //12 coloca o que está em reg 2 (1) na posição  memória do reg 0 (22), ou seja coloca 1 na pos 22
            new Sistema.Word(Sistema.Opcode.ADDI, 0, -1, 1), //13 add 1 no reg 0, ou seja reg fica com 23. Isso serve para mudar a posição da memória onde virá o próximo numero fbonacci
            new Sistema.Word(Sistema.Opcode.SUB, 7, 0, -1), //14 reg 7 = reg 7 - o que esta no reg 0, ou seja 30 menos 23 e coloca em r7. Isso é o contador regressivo que fica em r7. se for 0, pára
            new Sistema.Word(Sistema.Opcode.JMPIG, 6, 7, -1), //15 se r7 maior que 0 então pc recebe 6, else pc = pc + 1
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1),   // POS 16
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 31), //17 numeros de fibonacci a serem calculados menos 20
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //18 números de fibonacci a serem calculados
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //19
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),   // POS 20
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //21
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //22
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //23
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //24
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //25
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //26
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //27
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //28
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  // ate aqui - serie de fibonacci ficara armazenada //29
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1)
    };

    // programa que lê o número na posição 21 da memória:
    // - se número < 0: coloca -1 no início da posição de memória para saída, que é 23;
    // - se número > 0: este é o número de valores da sequencia de fibonacci a serem escritos
    // Lembrando que mais 20 números gerará overflow em nosso sistema
    public Sistema.Word[] fibonacci2 = new Sistema.Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
            new Sistema.Word(Sistema.Opcode.LDD, 4, -1, 22),    // 0- onde 22 é a posição da memória onde esta a quantidade de números Fibonacci a serem calculados

            // testa se número é menor que 0, e se for manda para final do programa
            new Sistema.Word(Sistema.Opcode.JMPILM, -1, 4, 23), // 1- pula para a linha amrazenada em [23], que é a linha de final do programa, se r4<0

            new Sistema.Word(Sistema.Opcode.ADDI, 4, -1, 24),   // 2- onde 24 é a primeira posição da memória com dados da fibonacci
            new Sistema.Word(Sistema.Opcode.STD, 4, -1, 21),    // 3- armazena o contador na posição 21 da memória

            // armazena valores iniciais da Fibonacci
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 0),     // 4- coloca 0 no reg 1
            new Sistema.Word(Sistema.Opcode.STD, 1, -1, 24),    // 5- 23 posicao de memoria onde inicia a serie de fibonacci gerada, ou seja coloca valor de reg 1 (0) na posicao 23
            new Sistema.Word(Sistema.Opcode.LDI, 2, -1, 1),     // 6- coloca 1 no reg 2
            new Sistema.Word(Sistema.Opcode.STD, 2, -1, 25),    // 7- na posição 24 coloca o que está em no reg 2, ou seja coloca 1 na posicao 24
            new Sistema.Word(Sistema.Opcode.LDI, 0, -1, 26),    // 8- coloca 25 no reg 0

            // início do loop
            new Sistema.Word(Sistema.Opcode.LDI, 6, -1, 10),    // 9- coloca 9 no reg 6, onde 9 é a linha do início do loop
            new Sistema.Word(Sistema.Opcode.LDD, 7, -1, 21),    // 10- coloca 20 no reg 7. É a posição do o contador.
            new Sistema.Word(Sistema.Opcode.LDI, 3, -1, 0),     // 11- coloca 0 no reg 3
            new Sistema.Word(Sistema.Opcode.ADD, 3, 1, -1),     // 12-
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 0),     // 13-
            new Sistema.Word(Sistema.Opcode.ADD, 1, 2, -1),     // 14- 0 add reg 1 + reg 2
            new Sistema.Word(Sistema.Opcode.ADD, 2, 3, -1),     // 15- 1 add reg 2 + reg 3
            new Sistema.Word(Sistema.Opcode.STX, 0, 2, -1),     // 16- coloca o que está em reg 2 (1) na posição  memória do reg 0 (22), ou seja coloca 1 na pos 22
            new Sistema.Word(Sistema.Opcode.ADDI, 0, -1, 1),    // 17- add 1 no reg 0, ou seja reg fica com 23. Isso serve para mudar a posição da memória onde virá o próximo numero fbonacci
            new Sistema.Word(Sistema.Opcode.SUB, 7, 0, -1),     // 18- reg 7 = reg 7 - o que esta no reg 0, ou seja 30 menos 23 e coloca em r7. Isso é o contador regressivo que fica em r7. se for 0, pára
            new Sistema.Word(Sistema.Opcode.JMPIG, 6, 7, -1),   // 19- se r7 maior que 0 então pc recebe 6, else pc = pc + 1
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1),  // 20- fim

            // memória
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  // 21- posição do contador
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 8),   // 22- números Fibonacci a serem calculados
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 20),  // 23- linha do final do programa
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  // 24- início do armazenamento da sequência Fibonacci
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  // 25-
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  // 26-
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  // 27-
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  // 28-
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  //...
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1)
    };

    // Dado um inteiro em na posição X da memória,
    // se for negativo armazena -1 na saída; se for positivo responde o fatorial do número na saída
    public Sistema.Word[] fatorial2 = new Sistema.Word[] { 	 // este fatorial so aceita valores positivos.   nao pode ser zero
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 1),   // 0- número a ser calculado o fatorial
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 12),  // 1- armazena o final do programa
            new Sistema.Word(Sistema.Opcode.LDD, 0, -1, 0),     // 2- coloca em reg 0 o valor da memória na posição 0
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, -1),    // 3- deixa reg 1 com -1 por padrão

            // testa se número é menor que 0, e se for manda para final do programa
            new Sistema.Word(Sistema.Opcode.JMPILM, -1, 0, 1),  // 4- pula para a linha amrazenada em [1], que é a linha de final do programa, se r0<0

            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 1),      // 5   	r1 é 1 para multiplicar (por r0)
            new Sistema.Word(Sistema.Opcode.LDI, 6, -1, 1),      // 6   	r6 é 1 para ser o decremento
            new Sistema.Word(Sistema.Opcode.LDI, 7, -1, 12),     // 7   	r7 tem posicao de stop do programa

            // início do loop
            new Sistema.Word(Sistema.Opcode.JMPIE, 7, 0, 0),     // 8   	se r0=0 pula para r7(=12)
            new Sistema.Word(Sistema.Opcode.MULT, 1, 0, -1),     // 9   	r1 = r1 * r0
            new Sistema.Word(Sistema.Opcode.SUB, 0, 6, -1),      // 10   	decrementa r0 1
            new Sistema.Word(Sistema.Opcode.JMP, -1, -1, 8),     // 11   	vai p posicao 8, que é o início do loop

            new Sistema.Word(Sistema.Opcode.STD, 1, -1, 14),      // 12   	coloca valor de r1 na posição 14
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1),    // 13   	stop
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1) };  // 14   ao final o valor do fatorial estará na posição 10 da memória


    public Sistema.Word[] fatorial = new Sistema.Word[] { 	 // este fatorial so aceita valores positivos.   nao pode ser zero
            // linha   coment
            new Sistema.Word(Sistema.Opcode.LDI, 0, -1, 6),      // 0   	r0 é valor a calcular fatorial
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 1),      // 1   	r1 é 1 para multiplicar (por r0)
            new Sistema.Word(Sistema.Opcode.LDI, 6, -1, 1),      // 2   	r6 é 1 para ser o decremento
            new Sistema.Word(Sistema.Opcode.LDI, 7, -1, 8),      // 3   	r7 tem posicao de stop do programa = 8
            new Sistema.Word(Sistema.Opcode.JMPIE, 7, 0, 0),     // 4   	se r0=0 pula para r7(=8)
            new Sistema.Word(Sistema.Opcode.MULT, 1, 0, -1),     // 5   	r1 = r1 * r0
            new Sistema.Word(Sistema.Opcode.SUB, 0, 6, -1),      // 6   	decrementa r0 1
            new Sistema.Word(Sistema.Opcode.JMP, -1, -1, 4),     // 7   	vai p posicao 4
            new Sistema.Word(Sistema.Opcode.STD, 1, -1, 10),     // 8   	coloca valor de r1 na posição 10
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1),    // 9   	stop
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1) };  // 10   ao final o valor do fatorial estará na posição 10 da memória

    public Sistema.Word[] invalidAddressTest = new Sistema.Word[]{
            new Sistema.Word(Sistema.Opcode.LDD, 0, -1, 1025),
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1)
    };

    public Sistema.Word[] overflowTest = new Sistema.Word[]{
            new Sistema.Word(Sistema.Opcode.LDI, 0, -1, 80800),
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 80800),
            new Sistema.Word(Sistema.Opcode.MULT, 0, 1, -1),
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1)
    };

    public Sistema.Word[] bubbleSort = new Sistema.Word[]{
            // Posições dos loops
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 41),  // 0- jump do primeiro loop gm ou em
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 9),   // 1- jump do segundo loop gm ou em
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 34),  // 2- jump do terceiro loop lm

            // não usadas, mas mantidas devido a dificuldade de refatorar esses códigos
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),

            new Sistema.Word(Sistema.Opcode.LDD, 1, -1, 43), // 6- reg 1 vai guardar o tamanho do vetor para comparações
            new Sistema.Word(Sistema.Opcode.LDI, 2, -1, 0),  // 7- apenas inicializa vetor 2 com 0
            new Sistema.Word(Sistema.Opcode.LDI, 3, -1, 0),  // 8- apenas inicializa vetor 3 com 0

            // início loop externo
            new Sistema.Word(Sistema.Opcode.LDI, 5, -1, 0),     // linha 9
            new Sistema.Word(Sistema.Opcode.ADD, 5, 2, -1),
            new Sistema.Word(Sistema.Opcode.SUB, 5, 1, -1),
            new Sistema.Word(Sistema.Opcode.JMPIGM, -1, 5, 0),  // linha 12 - pula pra linha 41 que é o fim (armazenado na memória [0])
            new Sistema.Word(Sistema.Opcode.JMPIEM, -1, 5, 0),
            new Sistema.Word(Sistema.Opcode.ADDI, 2, -1, 1),
            new Sistema.Word(Sistema.Opcode.LDI, 3, -1, 0),     // 15-

            // início loop interno
            new Sistema.Word(Sistema.Opcode.LDI, 5, -1, 0),     // 16-
            new Sistema.Word(Sistema.Opcode.ADD, 5, 3, -1),
            new Sistema.Word(Sistema.Opcode.ADDI, 5, -1, 1),

            // Verifica se chegou ao final do vetor. Se sim,reinicia comparações.
            new Sistema.Word(Sistema.Opcode.SUB, 5, 1, -1),     // 19-
            new Sistema.Word(Sistema.Opcode.JMPIEM, -1, 5, 1),  // 20- Pula para linha 9 (armazenado na memória [1]). Loop externo
            new Sistema.Word(Sistema.Opcode.JMPIGM, -1, 5, 1),
            // fim loop externo

            // Coloca nos registradores 4 e 5 dois números adjacentes do vetor
            new Sistema.Word(Sistema.Opcode.LDI, 4, -1, 44),    // 21- coloca a posição da memória de início do vetor [44] no reg 4
            new Sistema.Word(Sistema.Opcode.ADD, 4, 3, -1),     // 22-
            new Sistema.Word(Sistema.Opcode.LDI, 5, -1, 1),     // 23-
            new Sistema.Word(Sistema.Opcode.ADD, 5, 4, -1),     // 24-
            new Sistema.Word(Sistema.Opcode.LDX, 4, 4, -1),
            new Sistema.Word(Sistema.Opcode.LDX, 5, 5, -1),

            new Sistema.Word(Sistema.Opcode.ADDI, 3, -1, 1),    // 27- é o incremento da posição do vetor

            // Testa se o segundo número é menor que o primeiro
            new Sistema.Word(Sistema.Opcode.LDI, 6, -1, 0),
            new Sistema.Word(Sistema.Opcode.ADD, 6, 5, -1),
            new Sistema.Word(Sistema.Opcode.SUB, 6, 4, -1),
            new Sistema.Word(Sistema.Opcode.JMPILM, -1, 6, 2),  // 32- pula pra linha 34 se o segundo número é menor que o primeiro (amazenado na memória [2])
            new Sistema.Word(Sistema.Opcode.JMP, -1, -1, 16),   // 33- se não for volta pro início do loop interno
            // fim loop interno

            // Faz swap de dois números, se o segundo for menor que o primeiro
            new Sistema.Word(Sistema.Opcode.SWAP, 5, 4, -1),    // 34-
            new Sistema.Word(Sistema.Opcode.LDI, 6, -1, 43),    // 35-
            new Sistema.Word(Sistema.Opcode.ADD, 6, 3, -1),
            new Sistema.Word(Sistema.Opcode.STX, 6, 4, -1),
            new Sistema.Word(Sistema.Opcode.ADDI, 6, -1, 1),
            new Sistema.Word(Sistema.Opcode.STX, 6, 5, -1),
            new Sistema.Word(Sistema.Opcode.JMP, -1, -1, 16),   // 40-

            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  // 42- não usada
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 6),   // 43- tamanho do vetor
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 12),  // 44- dados do vetor a partir daqui até o final
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 7),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 9),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 4),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 3)
    };

    public Sistema.Word[] trapTestOutput = new Sistema.Word[]{
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 44),    //coloca 44 no reg 1. Esse será o valor mostrado no output
            new Sistema.Word(Sistema.Opcode.STD, 1, -1, 6),     // coloca o valor de reg1 na posição 6 da memória
            new Sistema.Word(Sistema.Opcode.LDI, 8, -1, 2),     // coloca 2 em reg 8 para criar um trap de out
            new Sistema.Word(Sistema.Opcode.LDI, 9,-1,6),       // coloca 6 no reg 9, ou seja a posição onde será feita a leitura
            new Sistema.Word(Sistema.Opcode.TRAP,-1,-1,-1),     // faz o output da posição 6
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1)
    };

    public Sistema.Word[] trapTestInput = new Sistema.Word[]{
            new Sistema.Word(Sistema.Opcode.LDI, 8, -1, 1),     // coloca 2 em reg 8 para criar um trap de input
            new Sistema.Word(Sistema.Opcode.LDI, 9,-1,4),       // coloca 4 no reg 9, ou seja a posição onde será feita a escrita
            new Sistema.Word(Sistema.Opcode.TRAP,-1,-1,-1),     // faz o input e armazena na posição 4
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1),
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1)   // valor será armazenado aqui
    };

    public Sistema.Word[] invalidRegisterTest = new Sistema.Word[]{
            new Sistema.Word(Sistema.Opcode.LDD, 11, -1, 1),
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1)
    };

    public Sistema.Word[] fibonacciComOutput = new Sistema.Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 0),  //0 coloca 0 no reg 1
            new Sistema.Word(Sistema.Opcode.STD, 1, -1, 23),    // 20 posicao de memoria onde inicia a serie de fibonacci gerada //1, ou seja coloca valor de reg 1 (0) na posicao 20
            new Sistema.Word(Sistema.Opcode.LDI, 2, -1, 1), //2 coloca 1 no reg 2
            new Sistema.Word(Sistema.Opcode.STD, 2, -1, 24), //3 na posição 21 coloca o que está em no reg 2, ou seja coloca 1 na posicao 21
            new Sistema.Word(Sistema.Opcode.LDI, 0, -1, 25), //4 coloca 22 no reg 0
            new Sistema.Word(Sistema.Opcode.LDI, 6, -1, 6), //5 coloca 6 no reg 6 - linha do inicio do loop
            new Sistema.Word(Sistema.Opcode.LDI, 7, -1, 34), //6 coloca 34 no reg 7. É o contador. será a posição one começam os dados, ou seja 23 + a quantidade de números fibonacci que queremos
            new Sistema.Word(Sistema.Opcode.LDI, 3, -1, 0), //7 coloca 0 no reg 3
            new Sistema.Word(Sistema.Opcode.ADD, 3, 1, -1), //8
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 0), //9
            new Sistema.Word(Sistema.Opcode.ADD, 1, 2, -1), //10 add reg 1 + reg 2
            new Sistema.Word(Sistema.Opcode.ADD, 2, 3, -1), //11 add reg 2 + reg 3
            new Sistema.Word(Sistema.Opcode.STX, 0, 2, -1), //12 coloca o que está em reg 2 (1) na posição  memória do reg 0 (22), ou seja coloca 1 na pos 22
            new Sistema.Word(Sistema.Opcode.ADDI, 0, -1, 1), //13 add 1 no reg 0, ou seja reg fica com 23. Isso serve para mudar a posição da memória onde virá o próximo numero fbonacci
            new Sistema.Word(Sistema.Opcode.SUB, 7, 0, -1), //14 reg 7 = reg 7 - o que esta no reg 0, ou seja 30 menos 23 e coloca em r7. Isso é o contador regressivo que fica em r7. se for 0, pára
            new Sistema.Word(Sistema.Opcode.JMPIG, 6, 7, -1), //15 se r7 maior que 0 então pc recebe 6, else pc = pc + 1

            // output
            new Sistema.Word(Sistema.Opcode.LDI, 8, -1, 2),     // coloca 2 em reg 8 para criar um trap de out
            new Sistema.Word(Sistema.Opcode.LDI, 9,-1,33),      // coloca 6 no reg 9, ou seja a posição onde será feita a leitura
            new Sistema.Word(Sistema.Opcode.TRAP,-1,-1,-1),     // faz o output da posição 10

            // memória
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1),   // POS 16
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 31), //17 numeros de fibonacci a serem calculados menos 20
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //18
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //19
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),   // POS 20
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //21
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //22
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //23
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //24
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //25
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //26
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //27
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1), //28
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),  // ate aqui - serie de fibonacci ficara armazenada //29
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1)
    };

    // Usuário faz input de um inteiro que é armazenado na posição 3 da memória,
    // se for negativo armazena -1 na saída [17]; se for positivo responde o fatorial do número na saída[17]
    public Sistema.Word[] fatorialComInput = new Sistema.Word[] {
            // input
            new Sistema.Word(Sistema.Opcode.LDI, 8, -1, 1),    // 0- coloca 1 em reg 8 para criar um trap de input
            new Sistema.Word(Sistema.Opcode.LDI, 9,-1,3),      // 1- coloca 3 no reg 9, ou seja a posição onde será feita a escrita do input
            new Sistema.Word(Sistema.Opcode.TRAP,-1,-1,-1),    // 2- faz o input

            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1),   // 3- número a ser calculado o fatorial
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, 15),  // 4- armazena o final do programa
            new Sistema.Word(Sistema.Opcode.LDD, 0, -1, 3),     // 5- coloca em reg 0 o valor da memória na posição 3, que é o número a ser calculado
            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, -1),    // 6- deixa reg 1 com -1 por padrão

            // testa se número é menor que 0, e se for manda para final do programa
            new Sistema.Word(Sistema.Opcode.JMPILM, -1, 0, 1),  // 7- pula para a linha amrazenada em [1], que é a linha de final do programa, se r0<0

            new Sistema.Word(Sistema.Opcode.LDI, 1, -1, 1),      // 8   	r1 é 1 para multiplicar (por r0)
            new Sistema.Word(Sistema.Opcode.LDI, 6, -1, 1),      // 9   	r6 é 1 para ser o decremento
            new Sistema.Word(Sistema.Opcode.LDI, 7, -1, 15),     // 10   	r7 tem posicao de stop do programa

            // início do loop
            new Sistema.Word(Sistema.Opcode.JMPIE, 7, 0, 0),     // 11   	se r0=0 pula para r7(=15)
            new Sistema.Word(Sistema.Opcode.MULT, 1, 0, -1),     // 12   	r1 = r1 * r0
            new Sistema.Word(Sistema.Opcode.SUB, 0, 6, -1),      // 13   	decrementa r0 1
            new Sistema.Word(Sistema.Opcode.JMP, -1, -1, 11),     // 14   	vai p posicao 11, que é o início do loop

            new Sistema.Word(Sistema.Opcode.STD, 1, -1, 17),      // 15   	coloca valor de r1 na posição 17
            new Sistema.Word(Sistema.Opcode.STOP, -1, -1, -1),    // 16  	stop
            new Sistema.Word(Sistema.Opcode.DATA, -1, -1, -1) };  // 17   ao final o valor do fatorial estará na posição 17 da memória

}
