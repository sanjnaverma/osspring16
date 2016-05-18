readMe.md

sv1058
Sanjna Verma


***TO RUN PROGRAM:***
***NOTE: it is very important to declare which algorithm in the exact spot as listed below. You can decide whether or not to use the verbose command***
***When taking in the file, it doesn't take into account the parentheses.***

FCFS
javac Scheduler.java
javac SchedulingAlgorithms.java

java SchedulingAlgorithms --verbose fcfs input-1.txt random-numbers.txt
java SchedulingAlgorithms fcfs input-1.txt random-numbers.txt

java SchedulingAlgorithms --verbose fcfs input-2.txt random-numbers.txt
java SchedulingAlgorithms fcfs input-3.txt random-numbers.txt

java SchedulingAlgorithms --verbose fcfs input-3.txt random-numbers.txt
java SchedulingAlgorithms fcfs input-3.txt random-numbers.txt

java SchedulingAlgorithms --verbose fcfs input-4.txt random-numbers.txt
java SchedulingAlgorithms fcfs input-4.txt random-numbers.txt

java SchedulingAlgorithms --verbose fcfs input-5.txt random-numbers.txt
java SchedulingAlgorithms fcfs input-5.txt random-numbers.txt

java SchedulingAlgorithms --verbose fcfs input-6.txt random-numbers.txt
java SchedulingAlgorithms fcfs input-6.txt random-numbers.txt

java SchedulingAlgorithms --verbose fcfs input-7.txt random-numbers.txt
java SchedulingAlgorithms fcfs input-7.txt random-numbers.txt





Round Robin
javac Scheduler.java
javac SchedulingAlgorithms.java

java SchedulingAlgorithms --verbose rr input-1.txt random-numbers.txt
java SchedulingAlgorithms rr input-1.txt random-numbers.txt

java SchedulingAlgorithms --verbose rr input-2.txt random-numbers.txt
java SchedulingAlgorithms rr input-3.txt random-numbers.txt

java SchedulingAlgorithms --verbose rr input-3.txt random-numbers.txt
java SchedulingAlgorithms rr input-3.txt random-numbers.txt

java SchedulingAlgorithms --verbose rr input-4.txt random-numbers.txt
java SchedulingAlgorithms rr input-4.txt random-numbers.txt

java SchedulingAlgorithms --verbose rr input-5.txt random-numbers.txt
java SchedulingAlgorithms rr input-5.txt random-numbers.txt

java SchedulingAlgorithms --verbose rr input-6.txt random-numbers.txt
java SchedulingAlgorithms rr input-6.txt random-numbers.txt

java SchedulingAlgorithms --verbose rr input-7.txt random-numbers.txt
java SchedulingAlgorithms rr input-7.txt random-numbers.txt


LCFS
javac Scheduler.java
javac SchedulingAlgorithms.java

java SchedulingAlgorithms --verbose lcfs input-1.txt random-numbers.txt
java SchedulingAlgorithms lcfs input-1.txt random-numbers.txt

java SchedulingAlgorithms --verbose lcfs input-2.txt random-numbers.txt
java SchedulingAlgorithms lcfs input-3.txt random-numbers.txt

java SchedulingAlgorithms --verbose lcfs input-3.txt random-numbers.txt
java SchedulingAlgorithms lcfs input-3.txt random-numbers.txt

java SchedulingAlgorithms --verbose lcfs input-4.txt random-numbers.txt
java SchedulingAlgorithms lcfs input-4.txt random-numbers.txt

java SchedulingAlgorithms --verbose lcfs input-5.txt random-numbers.txt
java SchedulingAlgorithms lcfs input-5.txt random-numbers.txt

java SchedulingAlgorithms --verbose lcfs input-6.txt random-numbers.txt
java SchedulingAlgorithms lcfs input-6.txt random-numbers.txt

java SchedulingAlgorithms --verbose lcfs input-7.txt random-numbers.txt
java SchedulingAlgorithms lcfs input-7.txt random-numbers.txt

HPRN
javac Scheduler.java
javac SchedulingAlgorithms.java

java SchedulingAlgorithms --verbose hprn input-1.txt random-numbers.txt
java SchedulingAlgorithms hprn input-1.txt random-numbers.txt

java SchedulingAlgorithms --verbose hprn input-2.txt random-numbers.txt
java SchedulingAlgorithms hprn input-3.txt random-numbers.txt

java SchedulingAlgorithms --verbose hprn input-3.txt random-numbers.txt
java SchedulingAlgorithms hprn input-3.txt random-numbers.txt

java SchedulingAlgorithms --verbose hprn input-4.txt random-numbers.txt
java SchedulingAlgorithms hprn input-4.txt random-numbers.txt

java SchedulingAlgorithms --verbose hprn input-5.txt random-numbers.txt
java SchedulingAlgorithms hprn input-5.txt random-numbers.txt

java SchedulingAlgorithms --verbose hprn input-6.txt random-numbers.txt
java SchedulingAlgorithms hprn input-6.txt random-numbers.txt

java SchedulingAlgorithms --verbose hprn input-7.txt random-numbers.txt
java SchedulingAlgorithms hprn input-7.txt random-numbers.txt





Each process maintains its own state, that includes its text and data, procedure call stack, etc

The operating system stores process states for each process. This state is called the PCB/process control block, and it includes the PC, SP, register states, execution state, etc.

For each execution state, the OS maintains a state queue. All of the processes that the OS is currently managing reside in one and only one of these state queues.
This document proved to be of great use to me:http://www.cs.utexas.edu/users/witchel/372/lectures/lec03-witchel.pdf

FCFS: First come first served
Same as FIFo. The scheduler executes jobs in completion in arrival order. In early FCFS schedulers, the job did not relinquish the CPU even when it was doing I/O. We will assume a FCFS scheduler that runs when processes are blocked on I/O, but that is non-preemptive, i.e. the job keeps the CPU until it blocks (say on an I/O device).


Something to note (from email conversations and in class assistance from substitute prof Lakshmi) is that it might be useful to have a processthread.java file that houses the characteristics of the process or thread we are essentially trying to schedule.
Each process has 5 different statuses:
1. unstarted
2. ready
3. running
4. blocked
5. ended

by default, we set each process to unstarted and change based on the information from the input file.

Some of the characteristics that I have to measure are:
rank = 0;
arrivalTime = 0;
cpuBurstTime = 0;
currentCPUBurstTime = 0;
totalCPUTime = 0;
remainingCPUTime = 0;
ioBurstTime = 0;
totalIOTime = 0;
currentIOTime = 0;
endTime = 0;
finishingTime = 0;
waitTime = 0;
priority = 0;
turnAroundTime = 0;

My processthread object will get and set all of these characteristics


Scheduler.java:
find a data structure to hold the process information and the input information
two options are linkedlists or array lists or hash table
better to go with array list because ArrayList has direct references to every element in the list, so it can get the n-th element in constant time.
LinkedList has to traverse the list from the beginning to get to the n-th element.
keeping track of hash sequencing would be too difficult

fileToProcess(String fileName) is my first method. it takes in a file name and handles the file (with however many 'n' processes) and puts them into the process arraylist.



1) I have to declare a new process for every line that is in the file (via while loop or for loop)
2) the first number, A, is our arrival time, so i will set and get the arrival time
3) the second number, B, is our CPU burst time, so i will set and get the CPU burst time
4) the third number, C, is the total CPU time. This may be tricky BUT I can also set the remainingCPUTime with this number C because at time 0, our remainingCPUTime is the totalCPUTime. It will change by iteration
5) the fourth number, D, is the ioBurstTime. So will set and get this.

I need to add all of this to the arrayLists that we created first (process and inputInformation).

then just use a simple conditional statement to see if(verbose){
  then print out the state and remaining burst for each of the processes.
}

Verbose is a method that will print out the verbose output. essentially, taking in two parameters (one being the arraylist process, and the other being the number of cycles within the process), we can get a detailed print out based on the flag.
given 4 scheduling algorithms and input will be given in form of 4 tuples
A, B, C, IO

A: arrival time
B: CPU burst time
C: total CPU time
IO: io burst multiplier
Ex:
(1, 1, 7, 1)

start at time t = 1
get allocated in units of 1 which is the CPU burst time
need 7 computations total
