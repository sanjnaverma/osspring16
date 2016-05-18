
import java.io.*;
import java.util.*;
import java.text.*;

public class BankerLabBankerAlgo {
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		BankersAlgorithm bankersAlgo = new BankersAlgorithm();
		if(args.length < 1) {
			System.out.println("Please run this again and specify a file. For example: java Banker input-1.txt");
		}
		else {
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
		
	}
}


class BankersAlgorithm
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
			//modify release counter: 1 array and two matrix
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
			if(ifAllTaskFinished(tasks)){
				break;
			}
		}
		//System.out.println("main loop ends!  "+cycle);
		printFinishTime(tasks);
	}
	//collect resources that released in one cycle, parameters served as recorder
	public void collection(int[] releasedResource,int[][] releasedAllocation,int[][] releasedNeed){
		//released resource
		for(int i=0;i<numberOfResources;i++){
			availableResourceArray[i]+=releasedResource[i];
		}
		//changes for the matrix
		for(int i=0;i<numberOfTasks;i++){
			for(int j=0;j<numberOfResources;j++){
				allocationResourceArray[i][j] -= releasedAllocation[i][j];
				shortageArrayNeed[i][j] +=releasedNeed[i][j];
			}
		}
	}
	
	/*Main method for the banker algorithm*/
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
		do
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
			
			if(Finish[i]==false && flag)
			{
				for(int j=0;j<numberOfResources;j++)
				{
					work[j] = work[j] + allocationResourceArray[i][j];
				}
				Finish[i] = true;
				i = -1; 
			}
		}while(++i<numberOfTasks);
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
	
	public void printFinishTime(Task[] task){
		System.out.println("\t\tBanker");
		DecimalFormat dfPrint = new DecimalFormat("####");
		for(int i=0;i<task.length;i++){
			System.out.print("Task " + task[i].task_taskID+"\t\t");
			if(task[i].isAborted()){
				System.out.print("Aborted");
			}
			else{
				System.out.print(task[i].finishTime+"\t"+task[i].blockedNum+"\t");
				float print = (float)task[i].blockedNum/task[i].finishTime;				
				System.out.print(dfPrint.format(print*100)+"%");
			}
			System.out.println();
		}
		System.out.print("Total\t\t");
		int totalTime = 0;
		int totalBlockedTime = 0;
		for(int i=0;i<task.length;i++){
			totalTime+=task[i].finishTime;
			totalBlockedTime+=task[i].blockedNum;
		}
		float print = (float)totalBlockedTime/totalTime;
		System.out.print(totalTime+"\t");
		System.out.print(totalBlockedTime+"\t");				
		System.out.print(dfPrint.format(print*100)+"%");
	}
	
	public static Boolean ifAllTaskFinished(Task[] task){
		int i=0;
		for(i=0;i<task.length;i++){
			if(!task[i].isFinished()){
				return false;
			}
		}
		return true;
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
	public Task(int i){//constructor
		counter = 0;
		isAborted = false;
		task_taskID = i+1;
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