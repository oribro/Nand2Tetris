(MAIN)
// Listen to the keyboard
@KBD
D=M
@FILL
D;JNE // A key is pressed on the keyboard


(CLEAR)

// Clear columns of the screen one after the other
// Using nested loops
@SCREEN
D=A
@addr
M=D // Screen base address

			
			                               // for (i=0;i<32;i++)
			                                // for (j=0;j<256;j++)
							// addr[i][j]=0
							
@256
D=A
@rows
M=D
@32
D=A
@cols
M=D
@i
M=-1

(CCOLTRAVERSE)
@i         // for (i=0;i<32;i++)
M=M+1
@cols
D=M
@i
D=M-D
@MAIN
D;JGT

@j
M=0
@SCREEN
D=A
@i
D=M+D
@addr
M=D // Screen base address

(CROWTRAVERSE)
@j         // for (j=0;j<256;j++)
M=M+1
@rows
D=M
@j
D=M-D
@CCOLTRAVERSE
D;JGT

@addr    // Set current row to white
A=M
M=0
@cols   // Advance to next row
D=M
@addr
M=D+M

@CROWTRAVERSE
0;JMP

(FILL)
// Fill columns of the screen one after the other
// Using nested loops
@SCREEN
D=A
@addr
M=D // Screen base address

			
			                               // for (i=0;i<32;i++)
			                                // for (j=0;j<256;j++)
							// addr[i][j]=-1
							
@256
D=A
@rows
M=D
@32
D=A
@cols
M=D
@i
M=-1

(COLTRAVERSE)
@i         // for (i=0;i<32;i++)
M=M+1
@cols
D=M
@i
D=M-D
@MAIN
D;JGT

@j
M=0
@SCREEN
D=A
@i
D=M+D
@addr
M=D // Screen base address

(ROWTRAVERSE)
@j         // for (j=0;j<256;j++)
M=M+1
@rows
D=M
@j
D=M-D
@COLTRAVERSE
D;JGT

@addr    // Set current row to black
A=M
M=-1
@cols   // Advance to next row
D=M
@addr
M=D+M

@ROWTRAVERSE
0;JMP