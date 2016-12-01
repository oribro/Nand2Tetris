//The program input will be at R13,R14 while the result R13/R14 will be store at R15.
//The remainder should be discarded.
//You may assume both numbers are positive.
//The program should have a running time logarithmic with respect to the input.

// Initialize variables for division
@base
M=1
@R13
D=M
@divident
M=D
@R14
D=M
@divisor
M=D
@R14
D=M
@curDiv
M=D

(LOOP)
@divident   // while (divident-divisor >= 0)
D=M
@divisor
D=D-M
@END
D;JLT

@divident   // if (divident-curDiv >= 0)
D=M
@curDiv
D=D-M
@DECREASEDIVISOR
D;JLT

@curDiv // divident -= curDiv
D=M
@divident
M=M-D
@base   // quotient += base
D=M
@R15    // Store result here
M=M+D
@curDiv // Double the div and base
M<<
@base
M<<
@LOOP
0;JMP

(DECREASEDIVISOR)
@curDiv
M>>
@base
M>>

@LOOP
0;JMP

(END)
