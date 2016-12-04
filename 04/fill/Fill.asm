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
@col   // Column number
M=-1
@SCREEN   // addr = SCREEN
D=A
@addr   // current chunk of bytes' address in memory
M=D

(CLEARCOLUMNS)
@row    // Row number
M=0
@col       // for (int c=0;c<512; c++)
M=M+1
D=M
@SCREEN   // addr = SCREEN + col
D=A+D
@addr
M=D
@col
D=M
@512
D=D-A
@MAIN
D;JEQ

(CLEARPIXELCOLUMN)
@row      //for (int r=0; r<256; r++)
D=M
@256
D=D-A
@CLEARCOLUMNS
D;JEQ

@addr // RAM[addr] = 1
D=M          // Check boundaries
@KBD
D=D-A
@MAIN
D;JGE

@addr
A=M
M=0    // addr += 32
@32
D=A
@addr   
M=M+D

@row
M=M+1

@CLEARPIXELCOLUMN
0;JMP



(FILL)
@col   // Column number
M=-1
@SCREEN   // addr = SCREEN
D=A
@addr   // current chunk of bytes' address in memory
M=D

(FILLCOLUMNS)
@row    // Row number
M=0
@col       // for (int c=0;c<512; c++)
M=M+1
D=M
@SCREEN   // addr = SCREEN + col
D=A+D
@addr
M=D
@col
D=M
@512
D=D-A
@MAIN
D;JEQ

(FILLPIXELCOLUMN)
@row      //for (int r=0; r<256; r++)
D=M
@256
D=D-A
@FILLCOLUMNS
D;JEQ

@addr // RAM[addr] = 1
D=M          // Check boundaries
@KBD
D=D-A
@MAIN
D;JGE

@addr
A=M
M=-1    // addr += 32
@32
D=A
@addr   
M=M+D

@row
M=M+1

@FILLPIXELCOLUMN
0;JMP
