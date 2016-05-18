readMe.md

Sanjna Verma
sv1058
Linker Lab

To compile: javac -Xlint linker_sv1058.java java linker_sv1058 input-1.txt
After mauler:  javac -Xlint linker.java filename.txt


Started with building this linker lab with multiple hashmaps --> they provide fast look up, deletion, and adding times, which seemed beneficial at firest.
However, the number of hashmaps i began using got to be too many. My thought process was as follows:

The files are a compilation of modules. Each module is built with 3 lines each. 
Line 1: definition
Line 2: usage
Line 3: actual code 

Meaning - in the example
Line 1: 1 xy 2 -> there is one definition in this definition line, the definition is xy, and it is defined as 2 + baseAddr (0 in this case)
Line 2: 1 z 4 -> there is 1 var that is used in the code below, which is z, and it is used in spot 4. 
Line 3: 5   R 1004  I 5678  E 2777  R 8002  E 7002
-> there are 5 "pieces of code". Since z gets used in the 4th, count 0, 1, 2, 3, 4 from the beginning, which gets me to [E 7002]. E means external, so it makes sense that an external var is being used in the code. 
Nevertheless, you change 7002 to 7015 (because that's the value of Z, once you look at its definition). 
Really, this Line 3 is like a linked list. Since spot 4's value was 7002, ending wth 2, now go to the 2nd spot, [E 2777]. E means external, so I'm good. I change the value to 2015 (the value of Z again), but since it ends in *777* the linked list ends.

So I started off with three hashmaps. 
But as soon as I got into the error handling section, i had to keep track internally of the symbols in the current module I was in and the address that I was in. Using a Hashmap for this got to be too much, and after a suggestion from StackOverFlow and the Stanford OS class website, a mixed use of ArrayLists and HashMaps were most efficient. This way, I could utilize a quick lookup (not every key needs a value, and it was confusing having to store (symbol, symbol) when i could just use an arraylist and add it to the arraylist).

.equals() vs ==
One of the reasons why im turning this lab in late is that I was utilizing the '==' operator as opposed to .equals() when I was comparing items in my hashmap. Basically if a class does not override the equals method, then it defaults to the equals(Object o) method of the closest parent class that has overridden this method. Essentially I needed to be testing for object equality and not functional equality.

Set Relative ADdress method: Returns the relocated relative address. This method I couldn't do without, reason being that I was getting confused by resolving for external references and doing the relocations all at the same time in the second pass. Therefore, it was easier to separate the two. I keep the external reference resolving in the pass2() method, and the resolving relative address in the other method. 

If you want to see all the various sources I used to create this program, let me know! Primary source was Finkel textbook for understanding on linkers, and stackoverflow. 
