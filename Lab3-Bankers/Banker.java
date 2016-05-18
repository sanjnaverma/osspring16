
import java.io.*;
import java.util.*;
import java.text.*;

public class Banker {
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		LabAlgorithmGen bankersAlgo = new LabAlgorithmGen();
		LabAlgorithmGen optAlgoFIFORunning = new LabAlgorithmGen();
		// System.out.println(args[0]);
		// System.out.println(args[1]);
		if(args.length < 2) {
			System.out.println("Please run this again and specify a file and algorithm. \nFor example: java Banker input-1.txt optimistic\nor\n java Banker input-1.txt banker");
		}
		else if(args[1].contentEquals("optimistic")){
			Scanner input = new Scanner(args[0]);
			Scanner scanner = new Scanner(new FileReader(input.next()));
			String firstLine = scanner.nextLine();
			String split[] = firstLine.split("\\s+");
			int numberOfTasks = Integer.parseInt(split[0]);
			int numberOfResources = Integer.parseInt(split[1]);
			optAlgoFIFORunning.startFIFO(numberOfTasks, numberOfResources);
			
			for(int i=1;i<=split.length-2;i++){
				optAlgoFIFORunning.availableResourceArray[i-1] = Integer.parseInt(split[i+1]);
			}
			
			Task[] tasks = new Task[numberOfTasks];
			for(int i=0;i<tasks.length;i++){
				tasks[i] = new Task(i);
			}
			while(scanner.hasNext()){
				String line = scanner.nextLine();
				if(!line.contentEquals("")){
					String splitActivities[] = line.split("\\s+");
					int taskID = Integer.parseInt(splitActivities[1]);
					tasks[taskID-1].instructionList.add(line);
				}
			}
			optAlgoFIFORunning.runFIFO(tasks);

		}
		else if(args[1].contentEquals("banker")){
			Scanner input = new Scanner(args[0]);
			Scanner scanner = new Scanner(new FileReader(input.next()));
			String firstLine = scanner.nextLine();
			String split[] = firstLine.split("\\s+");
			int numberOfTasks = Integer.parseInt(split[0]);
			int numberOfResources = Integer.parseInt(split[1]);
			bankersAlgo.startBankersRun(numberOfTasks, numberOfResources);	
			
			for(int i=1;i<=split.length-2;i++){
				bankersAlgo.availableResourceArray[i-1] = Integer.parseInt(split[i+1]);
				
			}
			
			Task[] tasks = new Task[numberOfTasks];
			for(int i=0;i<tasks.length;i++){
				tasks[i] = new Task(i);
			}
			while(scanner.hasNext()){
				String line = scanner.nextLine();
				if(!line.contentEquals("")){
					String splitActivities[] = line.split("\\s+");
					int taskID = Integer.parseInt(splitActivities[1]);
					tasks[taskID-1].instructionList.add(line);
				}
			}
			bankersAlgo.runningBankers(tasks);

		}
		else {
			System.out.println("This is invalid. Please remember that the command will look like: ");
			System.out.println("java Banker input-1.txt optimistic\nor\n java Banker input-1.txt banker");
		}
		
	}
}


class LabAlgorithmGen
{
	int numberOfTasks;
	int numberOfResources;
	int[] availableResourceArray; 
	int[][] maximumRequest;
	int[][] allocationResourceArray; 
	int[][] shortageArrayNeed;
	int request_Number; 
	int[][] numProcessesThatRequestedResource ;//Number of Process requested resource
	
	
	public void startBankersRun(int numOfTasks,int numOfResourceType){//constructor
		numberOfTasks = numOfTasks;
		numberOfResources = numOfResourceType; 
		availableResourceArray=new int[numberOfResources]; 
		maximumRequest= new int[numberOfTasks][numberOfResources]; 
		allocationResourceArray=new int[numberOfTasks][numberOfResources]; 
		shortageArrayNeed=new int[numberOfTasks][numberOfResources]; 
		numProcessesThatRequestedResource = new int[numberOfTasks][numberOfResources];
	}

	public void startFIFO(int numOfTask, int numOfResource) {
		numberOfTasks = numOfTask;
		numberOfResources = numOfResource;
		availableResourceArray = new int[numberOfResources];
		allocationResourceArray = new int[numberOfTasks][numberOfResources];
	}
	
	public void runningBankers(Task[] tasks)
	{
		int cycle = 0;
		BlockList blockDeadlockList = new BlockList();
		ArrayList<Task> blockToReadyTransitionState = new ArrayList<Task>();
		ArrayList<Task> waitState = new ArrayList<Task>();
		while(true){//run until all processes are ended or aborted or terminated
			cycle++;
			Task temp;//temp variable popped from the block queue
			waitState.clear();
			//modify release counter
			int[] releasedResource = new int[numberOfResources];//released resource in immediate cycle
			for(int i=0;i<releasedResource.length;i++){
				releasedResource[i] = 0;
			}
			int[][] releasedNeed = new int[numberOfTasks][numberOfResources];//need that will change due to release in this cycle
			int[][] releasedAllocation = new int[numberOfTasks][numberOfResources];//allocation that will change due to release in this cycle
			for(int i=0;i<numberOfTasks;i++){
				for(int j=0;j<numberOfResources;j++){
					releasedNeed[i][j] = 0;
					releasedAllocation[i][j] = 0;
				}
			}
			//block queue checked first
			while(!blockDeadlockList.blockQueue.isEmpty()){
				if(!blockDeadlockList.blockQueue.isEmpty()){
					temp = blockDeadlockList.blockQueue.poll();
					if(banker(tasks,temp)){//if there is a successful allocation then we remove it from the block queue and return to normal after processing tasks list
						blockToReadyTransitionState.add(temp);
					}
					else{
						waitState.add(temp);
					}
				}
			}
			//for blocked task that cannot be allocated then still blocks
			blockDeadlockList.blockQueue.addAll(waitState);
			for(int i=0;i<tasks.length;i++){
				String instructionLineCommand = new String();
				//If is blocked then skip this task
				if(!blockDeadlockList.blockQueue.contains(tasks[i]) && !blockToReadyTransitionState.contains(tasks[i])){
					
					if(tasks[i].computeTime==0){//if this task is not in computing
						if(tasks[i].hasNextActivity()){
							instructionLineCommand = tasks[i].getNext();
						}
						if(instructionLineCommand.contains("initiate")){
							String splitInitiate[] = instructionLineCommand.split("\\s+");
							int taskNumber = Integer.parseInt(splitInitiate[1]);
							int resourceType = Integer.parseInt(splitInitiate[2]);
							int initClaim = Integer.parseInt(splitInitiate[3]);
							
							if(initClaim>availableResourceArray[resourceType-1]){//exceeds resources available
								tasks[taskNumber-1].abortTask();
							}
							else{
								maximumRequest[taskNumber-1][resourceType-1] = initClaim;
								shortageArrayNeed[taskNumber-1][resourceType-1] = initClaim;
								tasks[i].next();
							}
						}

						else if(instructionLineCommand.contains("request")){
							if(!banker(tasks,tasks[i])){//block if allocation failed
								blockDeadlockList.blockQueue.add(tasks[i]);
							}
						}

						else if(instructionLineCommand.contains("release")){//record the resource modifications, process the request after task execution for immediate cycle
							String splitRelease[] = instructionLineCommand.split("\\s+");
							int taskNumber = Integer.parseInt(splitRelease[1]);
							int resourceType = Integer.parseInt(splitRelease[2]);
							int relativeResourceNum = Integer.parseInt(splitRelease[3]);
							
							releasedResource[resourceType-1] += relativeResourceNum;
							releasedAllocation [taskNumber-1][resourceType-1] = relativeResourceNum;
							releasedNeed  [taskNumber-1][resourceType-1]  = relativeResourceNum;
							tasks[i].next();
							if(tasks[i].isFinished()){
								tasks[i].finishTask(cycle);
							}
						}

						else if(instructionLineCommand.contains("compute")){
							String splitCompute[] = instructionLineCommand.split("\\s+");
							int taskNum = Integer.parseInt(splitCompute[1])-1;
							int compTime = Integer.parseInt(splitCompute[2])-1;
							tasks[taskNum].computeTime = compTime;
							//pointer point to the next activities
							tasks[taskNum].next();
							if(tasks[i].isFinished() && tasks[i].computeTime ==0){
								tasks[i].finishTask(cycle);
							}
						}
						else{
							
						}
					}
					//if this task is in computing
					else{
						tasks[i].compute();
						if(tasks[i].computeTime==0 && tasks[i].isFinished()){
							tasks[i].finishTask(cycle);
						}
					}
				}
			
			}
			
			//collect resource released in this cycle
			collection(releasedResource, releasedAllocation, releasedNeed);
			//remove task from block to ready
			Task[] remTask = blockToReadyTransitionState.toArray(new Task[0]);
			for(int i=0;i<remTask.length;i++){
				blockToReadyTransitionState.remove(remTask[i]);
			}
		
			//If all finished then stop loop
			if(areAllTasksFinish(tasks)){
				break;
			}
		}
		print_output(tasks, "Banker");
	}

	public void runFIFO(Task[] tasks){
		ArrayList<Task> blockToReady = new ArrayList<Task>();
		ArrayList<Task> wait = new ArrayList<Task>();
		int cycle = 0;
		BlockList block = new BlockList();
		Boolean deadLockDanger = false;//if deadlock happens in previous cycle (potentially)
		while(true){
			int numActivitiesPerCycle = 0;
			int numBlockedActivities = 0;
			Task temp;//stores popped task from block queue
			wait.clear();
			int[] releasedResource = new int[numberOfResources];//release resource in immediate cycle
			for(int i=0;i<releasedResource.length;i++){
				releasedResource[i] = 0;
			}
			
			cycle++;
			
			if(deadLockDanger){//has deadlock from in the previous cycle
				for(int i=0;i<tasks.length;i++){
					if(!tasks[i].isFinished() && !tasks[i].isAborted()){//if task is not finished or not aborted
						tasks[i].abortTask();//manually abort task
						for(int j=0;j<numberOfResources;j++){
							availableResourceArray[j] += allocationResourceArray[i][j];//releaseTask
						}
						block.blockQueue.remove(tasks[i]);
						//if is not deadlock then stop abort
						if(!isDeadlock(tasks)){
							deadLockDanger = false;
							break;
						}
					}
				}
			}
			//check if task is blocked
			while(!block.blockQueue.isEmpty()){
				temp = block.blockQueue.poll();
				numActivitiesPerCycle+=1;				
				if(verifyAllocationWorks(temp)){
					blockToReady.add(temp);
				}
				else{
					numBlockedActivities+=1;
					wait.add(temp);
				}
			}
			block.blockQueue.addAll(wait);
			for(int i=0;i<tasks.length;i++){//read in tasks that are unblocked
				String currentInstruction = new String();
				if(!block.blockQueue.contains(tasks[i]) && !blockToReady.contains(tasks[i])){//verify that tasks are not blocked
					if(tasks[i].computeTime==0){//no delay time for tasks[i]
						if(tasks[i].hasNextActivity()){
							currentInstruction = tasks[i].getNext();
						}
						if(currentInstruction.contains("initiate")){
							numActivitiesPerCycle++;		
							tasks[i].next();
						}
						
						else if(currentInstruction.contains("request")){
							numActivitiesPerCycle++;		
							if(verifyAllocationWorks(tasks[i])){
							}
							else{
								numBlockedActivities++;
								block.blockQueue.add(tasks[i]);
							}
						}
						
						else if(currentInstruction.contains("release")){
							numActivitiesPerCycle++;	
							String splitRequest[] = currentInstruction.split("\\s+");
							int resType = Integer.parseInt(splitRequest[2])-1;
							int numRel = Integer.parseInt(splitRequest[3]);
							releasedResource[resType]+=numRel;
							allocationResourceArray[i][resType] -= numRel;
							tasks[i].next();
							if(tasks[i].isFinished()){
								tasks[i].finishTask(cycle);
							}
						}

						else if(currentInstruction.contains("compute")){
							numActivitiesPerCycle++;	
							String splitCompute[] = currentInstruction.split("\\s+");
							int taskNum = Integer.parseInt(splitCompute[1])-1;
							int compTime = Integer.parseInt(splitCompute[2])-1;
							tasks[taskNum].computeTime = compTime;
							tasks[taskNum].next();
							if(tasks[i].isFinished() && tasks[i].computeTime ==0){
								tasks[i].finishTask(cycle);
							}
						}
					}
					
					else{//computeTime > 0 meaning there is a delay
						numActivitiesPerCycle++;		
						tasks[i].compute();
						if(tasks[i].computeTime==0 && tasks[i].isFinished()){
							tasks[i].finishTask(cycle);
						}
					}
				}
			}
			
			//grab all resources released in this cycle
			for(int i=0;i<numberOfResources;i++){
				availableResourceArray[i]+=releasedResource[i];
			}

			//task transitions from block state to ready state
			Task[] remTask = blockToReady.toArray(new Task[0]);
			for(int i=0;i<remTask.length;i++){
				blockToReady.remove(remTask[i]);
			}
			
			if(numActivitiesPerCycle == numBlockedActivities) {	
				deadLockDanger = true;
			
			}
			else {deadLockDanger = false;}
			
			if(areAllTasksFinish(tasks)){//stop cycle if all the tasks are finished
				break;
			}
		}
		print_output(tasks, "FIFO");
	}

	//collect resources that released in one cycle into the parameters releasedAllocation and releaseNeed--bankers
	public void collection(int[] releasedResource,int[][] releasedAllocation,int[][] releasedNeed){
		//released resource
		for(int i=0;i<numberOfResources;i++){
			availableResourceArray[i]+=releasedResource[i];
		}
		//changes for the array
		for(int i=0;i<numberOfTasks;i++){
			for(int j=0;j<numberOfResources;j++){
				allocationResourceArray[i][j] -= releasedAllocation[i][j];
				shortageArrayNeed[i][j] +=releasedNeed[i][j];
			}
		}
	}
	
	//Main method banker algorithm
	public Boolean banker(Task[] tasks,Task task){
		//get current activities
		String currentInstructionSet = task.getNext();
		String splitRequest[] = currentInstructionSet.split("\\s+");
		int taskNum = Integer.parseInt(splitRequest[1])-1;
		int resType = Integer.parseInt(splitRequest[2])-1;
		int numReq = Integer.parseInt(splitRequest[3]);
		for(int k=0;k<numberOfTasks;k++){
			for(int l=0;l<numberOfResources;l++){
				numProcessesThatRequestedResource[taskNum][resType] = 0;
			}
		}
		request_Number = taskNum;
		numProcessesThatRequestedResource[taskNum][resType] = numReq;
		for(int k=0;k<numberOfResources;k++)
		{
			//allocated resource exceeds max claim
			if(shortageArrayNeed[request_Number][k] - numProcessesThatRequestedResource[request_Number][k]<0 ){
				availableResourceArray[k] = availableResourceArray[k] + numProcessesThatRequestedResource[request_Number][k];
				allocationResourceArray[request_Number][k] = allocationResourceArray[request_Number][k] - numProcessesThatRequestedResource[request_Number][k];
				shortageArrayNeed[request_Number][k] = shortageArrayNeed[request_Number][k] + numProcessesThatRequestedResource[request_Number][k];
				task.abortTask();
				return true;
			}
			
			else{//allocate (doesn't exceed claim)
				availableResourceArray[k] = availableResourceArray[k] - numProcessesThatRequestedResource[request_Number][k];
				allocationResourceArray[request_Number][k] = allocationResourceArray[request_Number][k] + numProcessesThatRequestedResource[request_Number][k];
				shortageArrayNeed[request_Number][k] = shortageArrayNeed[request_Number][k] - numProcessesThatRequestedResource[request_Number][k];	
			}
		}
		if(IsSafe(tasks))
		{
			task.next();
			for(int k=0;k<numberOfResources;k++){
				numProcessesThatRequestedResource[request_Number][k] = 0;
			}
			return true;//current instruction is safe, so the request succeeds
		}
		else
		{
			//not safe // fail request
			for(int k=0;k<numberOfResources;k++)
			{
				availableResourceArray[k] = availableResourceArray[k] + numProcessesThatRequestedResource[request_Number][k];
				allocationResourceArray[request_Number][k] = allocationResourceArray[request_Number][k] - numProcessesThatRequestedResource[request_Number][k];
				shortageArrayNeed[request_Number][k] = shortageArrayNeed[request_Number][k] + numProcessesThatRequestedResource[request_Number][k];
			}
			for(int k=0;k<numberOfResources;k++){
				numProcessesThatRequestedResource[request_Number][k] = 0;
			}
			task.block();
			return false;
		}
	}
	//bankers: determines if the transaction is a safe one
	public boolean IsSafe(Task[] tasks)
	{
		int[] work = new int[numberOfResources];
		boolean[] Finish = new boolean[numberOfTasks];
		for(int i=0;i<numberOfTasks;i++){
			if(tasks[i].isFinished()||tasks[i].isAborted()){
				Finish[i] = true;
			}
			else{
				Finish[i] = false;
			}
		}
		for(int i=0;i<numberOfResources;i++)
		{
			work[i] = availableResourceArray[i];
		}
		
		int i = 0;
		do //http://stackoverflow.com/questions/15501861/bankers-algorithm-for-deadlock-avoidance-in-c
		{
			boolean flag = true;
			for(int j=0;j<numberOfResources;j++)
			{
				if(shortageArrayNeed[i][j]>work[j])
				{
					flag = false;
					break;
				}
			}
			
			if(Finish[i]==false && flag== true)
			{
				for(int j=0;j<numberOfResources;j++)
				{
					work[j] = work[j] + allocationResourceArray[i][j];
				}
				Finish[i] = true;
				i = -1; 
			}
		}while(++i<numberOfTasks);//i++ < numberOfTasks
		i = 0;
		while(Finish[i]==true)
		{
			if(i == numberOfTasks-1){
				return true; //safe --> return true
			}
			i++;
		}
		return false; //danger --> return false
	}

	
	//Bankers and Fifo
	public static Boolean areAllTasksFinish(Task[] task){
		for(int i=0;i<task.length;i++){
			if(!task[i].isFinished()){
				return false;
			}
		}
		return true;
	}

	/*
	 * Go through deadlock processes
	 * If all processes are requesting resources, then we are at a deaadlock
	 * First check if task is unfinished or not aborted
	 * then if one task CAN be allocated, we aren't at a deadlock --> return false
	 * if there are no tasks that can be allocated, we are at a deadlock --> return true
	 */
	private boolean isDeadlock(Task[] tArray) {
		for(int i=0;i<tArray.length;i++){
			if(!tArray[i].isAborted() && !tArray[i].isFinished()){
				String curActivity = tArray[i].getNext();
				String splitRequest[] = curActivity.split("\\s+");
				int resourceType = Integer.parseInt(splitRequest[2])-1;
				int numberOfReqs = Integer.parseInt(splitRequest[3]);
				if(availableResourceArray[resourceType] >= numberOfReqs){
					return false;
				}
			}
		}
		return true;
	}

	private Boolean verifyAllocationWorks(Task task) {
		String curActivity = task.getNext();
		String splitRequest[] = curActivity.split("\\s+");
		int resType = Integer.parseInt(splitRequest[2])-1;
		int numReq = Integer.parseInt(splitRequest[3]);
		
		if(availableResourceArray[resType] - numReq<0){//if there are not enough resources
			task.block();
			return false;
		}
		else{
			task.next();//proceed to next task index
			availableResourceArray[resType] -= numReq;//allocate
			allocationResourceArray[task.task_taskID-1][resType]+=numReq;//allocate
			return true;
		}
	}


	//for bankers and optimistic/fifo
	public void print_output(Task[] task, String algo){
		System.out.println("\t\t"+algo);//either bankers or fifo
		DecimalFormat dfPrint = new DecimalFormat("####");//got from stack overflow
		for(int i=0;i<task.length;i++){
			System.out.print("Task " + task[i].task_taskID+"\t\t");
			if(task[i].isAborted()){
				System.out.print("Aborted");
			}
			else{
				System.out.print(task[i].finishTime+"\t");
				System.out.print(task[i].blockedNum+"\t");
				float print = (float)task[i].blockedNum/task[i].finishTime;				
				System.out.print(dfPrint.format(print*100)+"%");
			}
			System.out.println();
		}
		System.out.print("Total\t\t");
		int totalBlockedTime = 0;
		int totalTime = 0;
		for(int i=0;i<task.length;i++){
			totalTime+=task[i].finishTime;
			totalBlockedTime+=task[i].blockedNum;
		}
		float print = (float)totalBlockedTime/totalTime;
		System.out.print(totalTime+"\t");
		System.out.print(totalBlockedTime+"\t");				
		System.out.print(dfPrint.format(print*100)+"%");
	}
}

class BlockList {
	Queue<Task> blockQueue = new LinkedList<Task>();	
}

class Task {
	ArrayList<String> instructionList = new ArrayList<String>();
	int counter;
	int computeTime;
	int blockedNum;
	int task_taskID;
	int finishTime;
	Boolean isAborted;
	public Task(int t){//constructor
		counter = 0;
		isAborted = false;
		task_taskID = t+1;
		computeTime = 0;
		blockedNum = 0;
	}
	
	public Boolean hasNextActivity(){
		if(counter == instructionList.size()-1) {
			return false;
		}
		else {
			return true;
		}
	}
	public String getNext() {
		return instructionList.get(counter);
	}
	
	public Boolean isFinished(){
		if(getNext().contains("terminate") && computeTime==0){
			return true;
		}
		else { 
			return false;
		}
	}
	
	public Boolean isAborted(){
		if(isAborted == true) {
			return true;
		}
		else { 
			return false;
		}
	}

	public void next(){counter+=1;}
	
	public void finishTask(int finish){finishTime = finish;}
	
	public void abortTask(){
		counter = instructionList.size()-1;
		isAborted = true;
		blockedNum = 0;
	}

	public void compute() {computeTime-=1;}

	public void block() { blockedNum+=1;}
}