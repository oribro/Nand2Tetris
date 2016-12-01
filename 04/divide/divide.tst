output-file divide.out,
compare-to divide.cmp,
output-list RAM[13]%D2.6.2 RAM[14]%D2.6.2 RAM[15]%D2.6.2 ;

// Set test arguments
set RAM[13] 35,   
set RAM[14] 5,
set RAM[15] 0,
set RAM[16] 0,
set RAM[17] 0,
set RAM[18] 0,
set RAM[19] 0,
set RAM[20] 0,
set RAM[21] 0,
set RAM[22] 0,

repeat 250 {
  ticktock;
}
output;

set PC 0,
// Set test arguments
set RAM[13] 100,   
set RAM[14] 10,
set RAM[15] 0,
set RAM[16] 0,
set RAM[17] 0,
set RAM[18] 0,
set RAM[19] 0,
set RAM[20] 0,
set RAM[21] 0,
set RAM[22] 0,

repeat 250 {
  ticktock;
}
output;

set PC 0,
// Set test arguments
set RAM[13] 10,   
set RAM[14] 100,
set RAM[15] 0,
set RAM[16] 0,
set RAM[17] 0,
set RAM[18] 0,
set RAM[19] 0,
set RAM[20] 0,
set RAM[21] 0,
set RAM[22] 0,

repeat 250 {
  ticktock;
}
output;

set PC 0,
// Set test arguments
set RAM[13] 28,   
set RAM[14] 9,
set RAM[15] 0,
set RAM[16] 0,
set RAM[17] 0,
set RAM[18] 0,
set RAM[19] 0,
set RAM[20] 0,
set RAM[21] 0,
set RAM[22] 0,

repeat 250 {
  ticktock;
}
output;

set PC 0,
// Set test arguments
set RAM[13] 64,   
set RAM[14] 7,
set RAM[15] 0,
set RAM[16] 0,
set RAM[17] 0,
set RAM[18] 0,
set RAM[19] 0,
set RAM[20] 0,
set RAM[21] 0,
set RAM[22] 0,

repeat 250 {
  ticktock;
}
output;