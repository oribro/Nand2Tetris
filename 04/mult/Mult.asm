// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

//Initialize variables
@R2
M=0
@i
M=1
@R0
D=M
@a
M=D
@R1
D=M
@b
M=D
@res
M=0

(LOOP)
@i
D=M
@b
D=D-M
@STOP
D;JGT // if i>b goto STOP

@res
D=M
@a
D=M+D
@res
M=D    // res = res+a
@i
M=M+1  // i = i+1
@LOOP
0;JMP

(STOP)
@res
D=M
@R2
M=D  // RAM[2] = res

(END)
@END
0;JMP

