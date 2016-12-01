//Proudly written by ITAI SHALOM
// File name: projects/04/Sort/Sort.tst

load Sort.hack,
output-file Sort.out,
compare-to Sort.cmp,
output-list RAM[19]%D2.6.2 RAM[20]%D2.6.2 RAM[21]%D2.6.2 RAM[22]%D2.6.2 RAM[23]%D2.6.2 ;

set RAM[14] 19,   // Set test arguments
set RAM[15] 5,

set RAM[19] 6,   // Set test arguments
set RAM[20] 3,
set RAM[21] 8,
set RAM[22] 9,
set RAM[23] 5,
repeat 500 {
  ticktock;
}
output;

set PC 0,
set RAM[19] 9,   // Set test arguments
set RAM[20] 8,
set RAM[21] 7,
set RAM[22] 6,
set RAM[23] 5,
repeat 500 {
  ticktock;
}
output;

set PC 0,
set RAM[19] 11,   // Set test arguments
set RAM[20] 15,
set RAM[21] 9,
set RAM[22] 2,
set RAM[23] 0,
repeat 500 {
  ticktock;
}

output;

set PC 0,
set RAM[19] -2,   // Set test arguments
set RAM[20] 5,
set RAM[21] 6,
set RAM[22] 1,
set RAM[23] 6,
repeat 500 {
  ticktock;
}
output;

set PC 0,
set RAM[19] -1,   // Set test arguments
set RAM[20] 0,
set RAM[21] 1,
set RAM[22] 2,
set RAM[23] 3,
repeat 700 {
  ticktock;
}

output;