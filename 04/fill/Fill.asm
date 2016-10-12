// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

(MAIN)
// Listen to the keyboard
@KBD
D=M
@FILL
D;JNE // A key is pressed on the keyboard
@CLEAR
0;JMP

(CLEAR)
// Clear columns of the screen one after the other
// Using nested loops
@SCREEN
D=A
@addr
M=D // Screen base address

// Screen dimensions. 
@255
D=A
@n
M=D   // n = 256
@32
D=A
@m
M=D // m = 32

// Clear a whole column
@j
M=-1 // j=0
(OUTERLOOPCLEAR)
@j
D=M
@m
D=D-M   // if j>m goto END
@MAIN
D;JGT
@j
M=M+1
@SCREEN
D=A
@addr
M=D
@j
D=M
@addr
M=M+D
@i
M=0 // i=0
// Clear the rows of the column
(INNERLOOPCLEAR)
@i
D=M
@n
D=D-M
@OUTERLOOPCLEAR
D;JEQ

@addr
A=M
M=0

@i
M=M+1
@32
D=A
@addr
M=M+D
@INNERLOOPCLEAR
0;JMP




(FILL)
// Fill columns of the screen one after the other
// Using nested loops
@SCREEN
D=A
@addr
M=D // Screen base address

// Screen dimensions. 
@255
D=A
@n
M=D   // n = 256
@32
D=A
@m
M=D // m = 32

// Fill a whole column
@j
M=-1 // j=0
(OUTERLOOPFILL)
@j
D=M
@m
D=D-M   // if j>m goto END
@MAIN
D;JGT
@j
M=M+1
@SCREEN
D=A
@addr
M=D
@j
D=M
@addr
M=M+D
@i
M=0 // i=0
// Fill the rows of the column
(INNERLOOPFILL)
@i
D=M
@n
D=D-M
@OUTERLOOPFILL
D;JEQ

@addr
A=M
M=-1

@i
M=M+1
@32
D=A
@addr
M=M+D
@INNERLOOPFILL
0;JMP

@MAIN
0;JMP