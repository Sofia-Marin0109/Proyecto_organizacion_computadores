// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

//// Replace this comment with your code.
(STOP)
    @KBD
    D=M
    @LOOP
    D;JNE
(BLANCO)
    @SCREEN
    D=A
    @cont
    D=D+M
    @addr
    M=D
    A=M
    M=0
    @cont
    M=M+1
    @8192
    D=A
    @cont
    D=D-M
    @BLANCO
    D;JNE
    @RESET
    D;JEQ
(LOOP)
    @SCREEN
    D=A
    @cont
    D=D+M
    @addr
    M=D
    A=M
    M=-1
    @cont
    M=M+1
    @8192
    D=A
    @cont
    D=D-M
    @LOOP
    D;JNE
    @RESET
    D;JEQ

(RESET)
    @cont
    M=0
    @STOP
    0;JMP