// The program input will be at R14(starting address),R15(length of array).
// The program should sort the array starting at the address in R14 with length specified in R15.
// The sort is in descending order - the largest number at the head of the array.
// The array is allocated in the heap address 2048-16383.

// We will use insertion sort

@R14 // Starting address
D=M
@arr
M=D
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
@temp  // temp variable for swapping
M=0
@arrjminus1  //var for swapping: address arr+j-1
M=0

(OUTERLOOP)  // for (i=1;i<length;i++)
@i
M=M+1
@len
D=M-1
@i
D=M-D
@END
D;JGT

@i   // j=i
D=M
@j
M=D

(INNERLOOP) // while (j>0 and A[j-1] > A[j])
@j     // j>0
D=M
@OUTERLOOP
D;JLE
@arr    // A[j-1] < A[j]
D=M
@j
D=D+M
A=D
D=M
A=A-1
D=M-D
@OUTERLOOP
D;JGE
		 // Swap A[j] and A[j-1]:
 		// temp = A[j]
		// A[j] = A[j-1]
		// A[j-1] = temp          
@arr  
D=M
@j
D=D+M
A=D 
D=M      // temp = A[j]
@temp
M=D

@arr  // A[j] = A[j-1]
D=M
@j
D=D+M
A=D
A=A-1
D=M
A=A+1
M=D
A=A-1
D=A
@arrjminus1
M=D

@temp  //A[j-1] = temp
D=M
@arrjminus1
A=M
M=D

@j     // j=j-1
M=M-1

@INNERLOOP
0;JMP

(END)

