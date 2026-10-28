@SCREEN
D=A
@addr
M=D
@32
D=A
@n
M=D
@i
M=0
(DRAW)
    @i
    D=M
    @n
    D=D-M
    @END
    D;JEQ
    
    @addr
    A=M
    DM=D+1  // debe ser MD=D+1
    
    @addr
    M=M+1
    @i
    M=M+1
    @DRAW
    0;JMP
(END)
    @END
    0;JMP