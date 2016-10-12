// The program input will be at R14(starting address),R15(length of array).
// The program should sort the array starting at the address in R14 with length specified in R15.
// The sort is in descending order - the largest number at the head of the array.
// The array is allocated in the heap address 2048-16383.

// We will use insertion sort

@R14 // Starting address
D=M
@arr
M=D
@left // Variables for sorting
M=0
@right
M=0
// length of array
@R15
D=M
@len
M=D

// Insertion sort
@i
M=0 // i = 0
@j
M=0 // j = 0
(OUTERLOOP)
@i
M=M+1
D=M
@len
D=D-M
@END
D;JEQ

@i
D=M
@j
M=D
(INNERLOOP)
@j
D=M
@OUTERLOOP
D;JLE       // If j <= 0  goto OUTERLOOP
@arr
D=M
@j
A=M+D  
D=M    
@right
M=D	   // A[j]
@j
D=M-1
@arr
A=M+D
D=M
@left
M=D
@right
D=M-D
@OUTERLOOP
D;JGE     // If A[j-1] <= A[j] goto OUTERLOOP

// Swap A[j] and A[j-1]
@arr
D=M
@j
A=D+M
D=M
@temp
M=D         // temp = A[j]
@arr
D=M
@j
A=D+M
A=A-1
D=M
A=A+1
M=D	    // A[j] = A[j-1]
@temp
D=M
@arr
D=M
@j
A=D+M
A=A-1
              ///////  NEED TO FIX THIS AND FIGURE HOW TO INSERT TO ARRAY//////
M=D        // A[j-1] = temp

@j
M=M-1
@INNERLOOP
0;JMP



(END)

