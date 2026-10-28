@5 //contador en 5
D=A
@i
M=D

@R2
M=0

(LOOP)
    // Verificar si i == 0 para terminar
    @i
    D=M
    @END
    D;JEQ

    @R0
    D=M
    @R2
    M=D+M

    @i
    M=M-1

    @LOOP
    0;JMP

(END)
    @END
    0;JMP