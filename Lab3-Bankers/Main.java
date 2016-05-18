
import java.io.*;
import java.util.*;
import java.text.*;

public class Main {
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Fifo myFIFO = new Fifo();
		Scanner input = new Scanner(args[0]);
		FileReader fr = new FileReader(input.next());
		Scanner scanner = new Scanner(fr);
		String firstLine = scanner.nextLine();
		String split[] = firstLine.split("\\s+");
		int numberOfTasks = Integer.parseInt(split[0]);

		
		int numberOfResources = Integer.parseInt(split[1]);
		//allocate space for necessary data structure, initialize all matrix and class members
		myFIFO.initAlgorithm(numberOfTasks, numberOfResources);
		
		//Resource Number of each type
		for(int i=1;i<=split.length-2;i++){
			myFIFO.availableResourceArray[i-1] = Integer.parseInt(split[i+1]);
		}
		
		//initialize task arrays
		Task[] tasks = new Task[numberOfTasks];
		for(int i=0;i<tasks.length;i++){
			tasks[i] = new Task(i);
		}
		//read activity lines
		while(scanner.hasNext()){
			String activities = scanner.nextLine();
			//If not null line then put this line into activities list
			if(!activities.contentEquals("")){
				String splitActivities[] = activities.split("\\s+");
				int taskID = Integer.parseInt(splitActivities[1]);
				tasks[taskID-1].Activities.add(activities);
			}
		}
		myFIFO.method(tasks);
		for(int i=0;i<tasks.length;i++){
			tasks[i].reset(i);
		}
	}
}

class BlockQueue {
	Queue<Task> blockQueue = new LinkedList<Task>();	
}

class Task {
	ArrayList<String> Activities = new ArrayList<String>();
	int iterator;//pointer,pointing to current activities
	int id;//task id
	int finishTime;
	int computeTime;
	int blockedNum;
	Boolean aborted;
	//initiate with ID
	public Task(int i){
		iterator = 0;
		aborted = false;
		id = i+1;
		computeTime = 0;
		blockedNum = 0;
	}

	public void reset(int i){
		iterator = 0;
		id = i+1;
		finishTime = 0;
		computeTime = 0;
		blockedNum = 0;
		aborted = false;
	}
	
	public Boolean hasNextActivity(){
		if(iterator == Activities.size()-1) return false;
		else return true;
	}
	//get current activity, as the iterator
	public String getNext() {
		return Activities.get(iterator);
	}
	
	public Boolean isFinished(){
		if(getNext().contains("terminate") && computeTime==0){
			return true;
		}
		else return false;
	}
	
	public Boolean isAborted(){
		if(aborted == true) return true;
		else return false;
	}

	//pointer jump to the next activities
	public void next(){
		iterator++;
	}
	
	public void finishTask(int finish){
		finishTime = finish;
	}
	
	public void abortTask(){
		//finish
		iterator = Activities.size()-1;
		aborted = true;
		blockedNum = 0;
		//System.out.println("tasks "+id+" is aborted");
	}

	public void compute() {
		computeTime--;
	}

	public void block() {
		blockedNum++;
	}
	
}

class Fifo {
	int numberOfTasks;//Max Process Number
	int numberOfResources; //Max Resource Type
	int[] availableResourceArray; //Available resource vector
	int[][] allocationResourceArray;//Allocation resource vector

	public void printAvailable(){
		System.out.println("*************FIFO AVAILABLE MATRIX*************");
		for(int i=0;i<availableResourceArray.length;i++){
			System.out.print(availableResourceArray[i]+"  ");
		}
		System.out.println();
	}

	
	
	public void printFinishTime(Task[] task){
		System.out.println("          FIFO");
		DecimalFormat dfPrint = new DecimalFormat("####");
		for(int i=0;i<task.length;i++){
			System.out.print("Task " + task[i].id+"       ");
			if(task[i].isAborted()){
				System.out.print("Aborted");
			}
			else{
				System.out.print(task[i].finishTime+"    ");
				System.out.print(task[i].blockedNum+"    ");
				float print = (float)task[i].blockedNum/task[i].finishTime;				
				System.out.print(dfPrint.format(print*100)+"%");
			}
			System.out.println();
		}
		System.out.print("Total        ");
		int totalTime = 0;
		int totalBlockedTime = 0;
		for(int i=0;i<task.length;i++){
			totalTime+=task[i].finishTime;
			totalBlockedTime+=task[i].blockedNum;
		}
		float print = (float)totalBlockedTime/totalTime;
		System.out.print(totalTime+"    ");
		System.out.print(totalBlockedTime+"    ");				
		System.out.print(dfPrint.format(print*100)+"%");
		System.out.println();
	}
		
	public void initAlgorithm(int numOfTask, int numOfResource) {
		numberOfTasks = numOfTask;
		numberOfResources = numOfResource;
		availableResourceArray = new int[numberOfResources];
		allocationResourceArray = new int[numberOfTasks][numberOfResources];
	}
	
	public void method(Task[] tasks){
		int cycle = 0;
		//System.out.println("FIFO LOOP START!!!!!!!!!------------------------------------------------------------");
		BlockQueue block = new BlockQueue();
		ArrayList<Task> blockToReady = new ArrayList<Task>();
		ArrayList<Task> wait = new ArrayList<Task>();
		Boolean isDanger = false;//if deadlock possibly happens in previous cycle
		/*executes until all process are ended or aborted*/
		while(true){
			//numbers of the total activities execute in this cycle: delayed is not a successful activities
			int activities = 0;
			//number of blocked activities, if all tasks are requesting and failed to be allocated then no doubt it is a deadlock
			int blockedReq = 0;
			Task task;//temp variable, storing task that pop from the block queue
			wait.clear();
			//modify release counter: 1 array and two matrix
			int[] releasedResource = new int[numberOfResources];	//released resource in this cycle, modified when all tasks finished reading in activities
			for(int i=0;i<releasedResource.length;i++){
				releasedResource[i] = 0;
			}
			
			cycle++;
			//has deadlock danger in the previous cycle
			if(isDanger){
				for(int i=0;i<tasks.length;i++){
					/*check unfinished or aborted tasks*/
					if(!tasks[i].isFinished() && !tasks[i].isAborted()){
						//from the minimun task number, abort task
						tasks[i].abortTask();
						//release task
						for(int j=0;j<numberOfResources;j++){
							availableResourceArray[j] += allocationResourceArray[i][j];
						}
						block.blockQueue.remove(tasks[i]);
						//if is not deadlock then stop aborting
						if(!isDeadlock(tasks)){
							isDanger = false;
							break;
						}
					}/*finish check unfinished or aborted tasks*/
				}
			}
			//check blocked task first
			while(!block.blockQueue.isEmpty()){
				task = block.blockQueue.poll();
				activities++;				
				if(tryAllocation(task)){
					blockToReady.add(task);
				}
				else{
					blockedReq++;
					wait.add(task);
				}
			}
			block.blockQueue.addAll(wait);
			/*Read in all tasks not blocked*/
			for(int i=0;i<tasks.length;i++){
				String curActivity = new String();
				/*check tasks that are not blocked*/
				if(!block.blockQueue.contains(tasks[i]) && !blockToReady.contains(tasks[i])){
					/*tasks are not in delaying*/
					if(tasks[i].computeTime==0){
						if(tasks[i].hasNextActivity()){
							curActivity = tasks[i].getNext();
						}
						/*INITIATE*/
						if(curActivity.contains("initiate")){
							activities++;		
							//pointer jump to the next activities
							tasks[i].next();
						}
						
						/*-----REQUEST-----*/
						else if(curActivity.contains("request")){
							activities++;		
							if(tryAllocation(tasks[i])){
							}
							else{
								blockedReq++;
								block.blockQueue.add(tasks[i]);
							}
						}
						
						/*-----RELEASE-----*/
						else if(curActivity.contains("release")){
							activities++;	
							String splitRequest[] = curActivity.split("\\s+");
							int resType = Integer.parseInt(splitRequest[2])-1;
							int numRel = Integer.parseInt(splitRequest[3]);
							releasedResource[resType]+=numRel;
							allocationResourceArray[i][resType] -= numRel;
							tasks[i].next();
							if(tasks[i].isFinished()){
								tasks[i].finishTask(cycle);
							}
						}

						/*-----COMPUTE-----*/
						else if(curActivity.contains("compute")){
							activities++;	
							String splitCompute[] = curActivity.split("\\s+");
							int taskNum = Integer.parseInt(splitCompute[1])-1;
							int compTime = Integer.parseInt(splitCompute[2])-1;
							tasks[taskNum].computeTime = compTime;
							tasks[taskNum].next();
							if(tasks[i].isFinished() && tasks[i].computeTime ==0){
								tasks[i].finishTask(cycle);
							}
						}

						else{
							
						}
					}/*tasks are not in delaying*/
					/*tasks are delayed*/
					else{
						activities++;		
						tasks[i].compute();
						if(tasks[i].computeTime==0 && tasks[i].isFinished()){
							tasks[i].finishTask(cycle);
						}
					}
				}/*finish check tasks that are not blocked*/
			}
			
			//collect resource released in this cycle
			for(int i=0;i<numberOfResources;i++){
				availableResourceArray[i]+=releasedResource[i];
			}

			//remove task from block to ready
			Task[] remTask = blockToReady.toArray(new Task[0]);
			for(int i=0;i<remTask.length;i++){
				blockToReady.remove(remTask[i]);
			}
			//is deadlock, used for checking at the very beginning of next cycle
			if(activities==blockedReq)	isDanger = true;
			else isDanger = false;
			
			//If all finished then stop loop
			if(ifAllTaskFinished(tasks)){
				break;
			}
		}
		printFinishTime(tasks);
	}

	//check deadlock process, this will work only when if all process are requesting resources, that may lead to deadlock
	private boolean isDeadlock(Task[] task) {
		int i;
		for(i=0;i<task.length;i++){
			//only check unfinished or unaborted
			if(!task[i].isAborted() && !task[i].isFinished()){
				String curActivity = task[i].getNext();
				String splitRequest[] = curActivity.split("\\s+");
				int resType = Integer.parseInt(splitRequest[2])-1;
				int numReq = Integer.parseInt(splitRequest[3]);
				//if one task can be allocated then of course no deadlock
				if(availableResourceArray[resType] >= numReq){
					return false;
				}
			}
		}
		//if all tasks cannot be allocated
		return true;
	}

	private Boolean tryAllocation(Task task) {
		String curActivity = task.getNext();
		String splitRequest[] = curActivity.split("\\s+");
		int resType = Integer.parseInt(splitRequest[2])-1;
		int numReq = Integer.parseInt(splitRequest[3]);
		//not enough resources
		if(availableResourceArray[resType] - numReq<0){
			task.block();
			return false;
		}
		else{
			//pointer move
			task.next();
			//allocate
			availableResourceArray[resType] -= numReq;
			allocationResourceArray[task.id-1][resType]+=numReq;
			return true;
		}
	}

	private boolean ifAllTaskFinished(Task[] task) {
		int i=0;
		for(i=0;i<task.length;i++){
			//if one process is not finished
			if(!task[i].isFinished()){
				return false;
			}
		}
		//if all processes are finished
		return true;
	}
}
