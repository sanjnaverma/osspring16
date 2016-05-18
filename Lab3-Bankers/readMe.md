Sanjna Verma
sv1058 (line by line comments included in this readMe)

I have coded this lab in Java. To run the program to figure out the results via the optimistic manager operation, please type the following command in (you may change the specific input file if you wish):
javac Banker.java
java Banker input-1.txt optimistic
java Banker input-2.txt optimistic
java Banker input-3.txt optimistic
java Banker input-4.txt optimistic
java Banker input-5.txt optimistic
java Banker input-6.txt optimistic
java Banker input-7.txt optimistic
java Banker input-8.txt optimistic
java Banker input-9.txt optimistic
java Banker input-10.txt optimistic
java Banker input-11.txt optimistic
java Banker input-12.txt optimistic
java Banker input-13.txt optimistic


To run the bankers algorithm, please use this command:
java Banker input-1.txt banker
java Banker input-2.txt banker
java Banker input-3.txt banker
java Banker input-4.txt banker
java Banker input-5.txt banker
java Banker input-6.txt banker
java Banker input-7.txt banker
java Banker input-8.txt banker
java Banker input-9.txt banker
java Banker input-10.txt banker
java Banker input-11.txt banker
java Banker input-12.txt banker
java Banker input-13.txt banker

Generic outline of the program:
1) take in 1 command line argument: the name of file containing the input

2) read input

3a) perform optimistic manager operation

      Deadlock can occur

      If deadlock is detected, print a message and abort the lowest numbered deadlocked task after releasing all its resources. If deadlock remains, print another message and abort the next lowest numbered deadlocked task, etc.

3b) perform banker operation

4) At the end of the run, print, for each task, the time taken, the waiting time, and the percentage of time spent waiting. Also print the total time for all tasks, the total waiting time, and the overall percentage of time spent waiting.

Here is a step by step layout of my lab (for my own sake, I prefer to keep my commenting in the readMe. If this proves to be too much of an issue, do let me know):

After some online reading via the CMU operating systems class, the best way to approach this was to first identify the data structure that I would be utilizing for task management and deadlock management. I tried using ArrayLists for both, but when it came to figuring out the deadlock errors with the bankers portion of the lab, it was more useful to use a linkedlist queue. I created a class for the deadlock data structure after reading on StackOverflow that someone had a similar issue (but in Python) and created a separate class for this. 
I had a Task class that keeps the characteristics of each task:
each task includes an instruction, has a compute time, the potential to be blocked, a taskID, a finish time, and a boolean to determine whether the task has been aborted (rather than finished because if it is finished, it does so within the finishTime period). The constructor sets the initial task details. 
I have characteristic traits like getters and setters (like I did with lab2) to make finding information much easier. Some of these getters are whether or not there is another activity in the instruction series, get the next activity in the instruction series, determine whether or not the current activity is finished or if it is aborted (again remember that these are two different traits), an incrementor so I can traverse my arrayList, a computeTime method, a block counter method, and an abortTask method that manually aborts the task. 

Let's now go to my Banker class. You will notice an interesting class: LabAlgorithmGen class is the basic skeleton framework for my algorithm, and of course, depending on whether I am running the FIFO or optimistic algorithm vs Bankers algorithm, I will call different methods. Firstly, I read in the input in my main method of my Banker class. I take in the file, and split the first line to get the number of tasks, the number of resources, and set arrays accordingly (the startFIFO method). I also keep track of the number of available resources based on the number of resources. I create an array of tasks, and while I can continue to parse through the array, I record the activity and taskID and add it to my arrayList. then I run my FIFO algorithm. 

Running my FIFO algorithm first needs me to set a counter for which cycle I am on, whether I am in danger of deadlock because of the previous cycle (which we learned in class/midterm prep, can happen), a waitState arrayList to keep track of activities that have to wait their turn, and a queue to keep track of blocked activities. While I am able to traverse the list, I keep track of the number of activities in a cycle and the number of blocked activities in the cycle, a temp task that is popped from the block queue, and an array to keep track of released resources in the immediate cycle. I initialize the released resources array, and increment my cycle count. If i am in danger of a deadlock because I have a deadlock from my previous cycle, then I have to check if the specific task I am on (tasks[i]) is finished or aborted. If it is not, then I have to manually abort the task, and then add the number of allocated resources for that task to the number of available resources total. I remove the blocked task (tasks[i] still) and then verify if there are not any future deadlocks, i stop the aborting and exit the loop. 
If my task IS blocked, i pop the top task, increment the number of activities per cycle by 1 because now i have another task to take care of, and then verify that my allocation works (is safe for the optimist algo). The way the algorithm verifies if the allocation is safe or not is similar to the method that the professor described in class. I parse the current task for the number of requests and the resource type. if there are not enough resources, then I have to block and return FALSE (not a safe allocation). Otherwise, i can proceed to the next task index, I allocate the number of requested resources from my available resource array to the allocation resource array. This requried a bit more math to figure (and was stuck here for a bit). If the allocation works, then i phase this specific task to my BlockToReady transition state array. If it's not a safe allocation, then i have to increase the number of blocked activities, and add this to my waitState array list. Continuing on, I have to read in all the tasks that are UNBLOCKED and verify that they are not blocked. Then, if the computeTime is 0, there is no delay time for the current task, and depending on the specific instruction, i can carry out the algorithm. These instructions are initiate, request, release, compute, terminate. If compute time is not 0 or greater than 0, then there is a delay and I have to icrease the activitiy per cycle count. I grab all the resources released in this cycle, add it to the number of available resources (bank vault is collecting its dues basically) and then I transition tasks from block state to ready states. if all my tasks are finished, I stop the cycle and end the algorithm and print. The printing part is pretty rudimentary and I had a lot of print statements throughout the algorithm to help me out. 

Now for the bankers algorithm: 
Again, in the same way I started FIFO/optimistic, I am going to start by reading the input for bankers. The difference is in the type of algorithm initiation. In starting my bankers algorithm run, I need to keep track of the maximum requests possible and a shortage array (or arraylist) to see whether or not I am in a shortage of resources. I also am keeping track of the number of processes that have requested a particular resource. Once I initiate the algorithm like so, I can run the bankers algo. Again, I have to keep track of what cycle i am on, a list of the deadlocked tasks, a block to ready transition state arraylist so I can phase out the block tasks, and then a waitState arraylist to keep track of the tasks that are blocked. I run the algorithm until all processes are finished or aborted or terminated. I keep a temporary variable that is popped from the blocked queue (just like FIFO). I recommend going through the comments in the algorithm here because its pretty hard to go through again. This algorithm was really confusing and I had to go through a lot of the skeleton write ups from emails and whatever Professor could clarify in class. 

