
import java.io.*;
import java.util.*;
import java.text.*;

public class Banker {
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		OptimisticAlgoFIFO optAlgoFIFORunning = new OptimisticAlgoFIFO();
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

class OptimisticAlgoFIFO {
	// int numberOfTasks;
	// int numberOfResources; 
	// int[] availableResourceArray; 
	// int[][] allocationResourceArray;

	int numberOfTasks;
	int numberOfResources;
	int[] availableResourceArray; 
	int[][] maximumRequest;
	int[][] allocationResourceArray; 
	int[][] shortageArrayNeed;
	int request_Number; 
	int[][] numProcessesThatRequestedResource ;//Number of Process requested resource
	
	
	public void startFIFO(int numOfTask, int numOfResource) {
		numberOfTasks = numOfTask;
		numberOfResources = numOfResource;
		availableResourceArray = new int[numberOfResources];
		allocationResourceArray = new int[numberOfTasks][numberOfResources];
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
				numActivitiesPerCycle++;				
				if(verifyAllocationWorks(temp)){
					blockToReady.add(temp);
				}
				else{
					numBlockedActivities++;
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
		printFIFO_Output(tasks);
	}

	private boolean areAllTasksFinish(Task[] task) {
		for(int i=0;i<task.length;i++){
			if(!task[i].isFinished()){//if process is not finished, return false. 
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

	public void printFIFO_Output(Task[] task){
		System.out.println("\t\tFIFO");
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
