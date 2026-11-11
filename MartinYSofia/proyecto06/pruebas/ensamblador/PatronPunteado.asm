// Dirección inicial
@SCREEN
D=A
@addr
M=D

// Contador = 0
@0
D=A
@i
M=D

(LOOP)

    // Si i >= 256, terminar
    @i
    D=M
    @256
    D=D-A
    @END
    D;JGE

    // Poner el patrón 0101010101010101
    @21845
    D=A

    @addr
    A=M
    M=D

    // addr = addr + 32
    @addr
    D=M
    @32
    D=D+A
    @addr
    M=D

    // i++
    @i
    M=M+1

    // repetir
    @LOOP
    0;JMP

(END)
    @END
    0;JMP