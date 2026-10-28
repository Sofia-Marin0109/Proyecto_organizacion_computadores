@R0
D=M
@posi
M=D  // posi = RAM[0] (origen)

@R1
D=M
@posf
M=D  // posf = RAM[1] (destino)

@R2
D=M
@n
M=D  // n = RAM[2] (cantidad total a copiar)

@i
M=0

(LOOP)
    @i
    D=M
    @n
    D=D-M
    @END
    D;JEQ

    @posi
    D=M
    @i
    A=D+M  //posición de la que saco el dato
    D=M    //contenido de esa posición
    @valor
    M=D    //variable que almacena el contenido

    @posf
    D=M
    @i
    D=D+M
    @dir_destino
    M=D   // guardar direccion como valor
    
    @valor
    D=M   // contenido para la dirección final
    @dir_destino 
    A=M   //el contenido de dir destina (M) es la dirección, por eso apunto con A a ella
    M=D   //copio en esa dirección el valor que quiero (D)

    @i
    M=M+1
    @LOOP
    0;JMP
(END)
@END
0;JMP