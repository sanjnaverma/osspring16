import java.io.*;
import java.util.*;

//reminder to figure out how to print original input
public class Scheduler {

	public static ArrayList processThread = new ArrayList();

	public static ArrayList inputInformation = new ArrayList();

	public static int starttime = 0;
	public static int endtime = 0;
	public static double ioTime = 0.0;
	public static double cpuTime = 0.0;

	public static boolean isVerbose = false;


	public static void fileToProcess(String fileName) throws IOException{
		BufferedReader file = new BufferedReader(new FileReader(fileName));
		String line = null;
		String fileArr[] = null;

		while((line = file.readLine()) != null) {
			//System.out.println(line);
			fileArr = line.split(" +");//stackoverflow hint to get rid of more than one space
		}

		//System.out.println("\nfilearr is:");

		// System.out.println("The original input was:");
		// for(int i = 0; i <fileArr.length; i++) {
		// 	System.out.print(fileArr[i]);
		// }

		int numberOfProcesses = Integer.parseInt(fileArr[0]);
		int indexProcess = 1;
		for(int i = 0; i <numberOfProcesses; i++) {
			Process process = new Process();
			int A = Integer.parseInt(fileArr[indexProcess++]);
			process.setArrivalTime(A);

			int B = Integer.parseInt(fileArr[indexProcess++]);
			process.setCPUBurstTime(B);

			int C = Integer.parseInt(fileArr[indexProcess++]);
			process.setRemainingCPUTime(C);
			process.setTotalCPUTime(C);

			int D = Integer.parseInt(fileArr[indexProcess++]);
			process.setIOBurstTime(D);

			processThread.add(process);
			inputInformation.add(process);
//			System.out.println(process.getArrivalTime()+", "+process.getCPUBurstTime()+", "
//					+process.getTotalCPUTime()+", "+process.getIOBurstTime());


		}

		sortByArrivalTime(inputInformation);
		System.out.print("\n\nThe (sorted) input is: \n");
		for(int i = 0; i < numberOfProcesses; i++) {
			System.out.print(((Process) inputInformation.get(i)).getArrivalTime()+" ");
			System.out.print(((Process) inputInformation.get(i)).getCPUBurstTime()+" ");
			System.out.print(((Process) inputInformation.get(i)).getTotalCPUTime()+" ");
			System.out.print(((Process) inputInformation.get(i)).getIOBurstTime());
			System.out.println("");
		}
		System.out.println("----------------------\n");

		if (isVerbose) {
			System.out.println("This detailed printout gives the state and remaining burst for each process:");
		}
//
	}

	/*
	 * public final static int UNSTARTED = 0;
	public final static int READY = 1;
	public final static int RUNNING = 2;
	public final static int BLOCKED = 3;
	public final static int ENDED = 4;
	 *
	 *
	 */

	public static void verbose(ArrayList<Process> p, int counter) {
		if(isVerbose == true) {
			String printstatement = "";
			String status = "";//blank status, reset is needed
			int remainder = 0;
			for(int i = 0; i <p.size(); i++) {
				if(p.get(i).getStatus() == 0) {
					status = "UNSTARTED";
					remainder = 0;
				}
				else if(p.get(i).getStatus() == 1) {
					status = "READY";
					remainder = 0;
				}
				else if(p.get(i).getStatus() == 2) {
					status = "RUNNING";
					remainder = p.get(i).getCurrentCPUBurstTime();
				}
				else if(p.get(i).getStatus() == 3) {
					status = "BLOCKED";
					remainder = p.get(i).getCurrentIOTime();
					// p.get(i).setRemainingCPUTime(0);
				}
				else if(p.get(i).getStatus() == 4) {
					status = "ENDED";
					remainder = 0;
				}
				//stackoverflow:
				printstatement+=String.format("%15s%5s",status, remainder);
			}
			System.out.println("Before cycle "+counter+": "+printstatement);
		}
	}

	public static void sortByArrivalTime(ArrayList<Process> process) {
		for(int i = 1; i < process.size(); i++) {
			for(int j = i; j>0; j--) {
				if(process.get(j).getArrivalTime() < process.get(j-1).getArrivalTime()) {
					//swap --> declare a temp
					Process temp = process.get(j);
					process.set(j,  process.get(j-1));
					process.set(j-1,  temp);
				}

			}
		}
	}

	public static int getFinishingTime() {
		return endtime;
	}

	public void setFinishingTime(int endtime) {
		this.endtime = endtime;
	}

	public static void setProcessOrder(ArrayList<Process> process) {
		for(int i = 0; i < process.size(); i++) {
			process.get(i).setPriority(i);
		}
	}

	public static void sortProcessOrderBased(ArrayList<Process> process)
	{
		for(int i = 1; i < process.size(); i++) {
			for(int j = i; j > 0; j--) {
				if(process.get(j).getPriority() < process.get(j-1).getPriority()) {
					//swap processes.
					Process temp = process.get(j);
					process.set(j,  process.get(j-1));
					process.set(j-1, temp);
				}
			}
		}
	}

	public static void addWaitTime(ArrayList<Process> process) {
		for(int i = 0; i < process.size(); i++) {
			if(process.get(i).getStatus() == process.get(i).RUNNING) {
				cpuTime++;
			}
			else if(process.get(i).getStatus() == process.get(i).READY) {
				process.get(i).setWaitTime(process.get(i).getWaitTime()+1);
			}
		}
	}

	//verify if all processes have terminated
	public static boolean isProcessTerminated(ArrayList<Process> process) {
		for(int i = 0; i < process.size(); i++) {
			if(process.get(i).getStatus() != process.get(i).ENDED) {
				return false;
			}
		}
		return true;
	}

	//prints info for all the processes stored in the array list
	public static void printInfo() {
		int threadNum = 0;
		// for(int i= processThread.size()-1; i >=0; i--) { //lcfs
		for(int i= 0; i < processThread.size(); i++) { //fcfs
			Process storedProcess = (Process) processThread.get(i);
			System.out.println("Process number "+ threadNum);//i+":");
			System.out.println("A, B, C, IO: "+storedProcess.getArrivalTime()+", "
					+storedProcess.getCPUBurstTime()+", "+storedProcess.getTotalCPUTime()
					+", "+storedProcess.getIOBurstTime());
			System.out.println("Finishing time: " + storedProcess.getFinishingTime());
			System.out.println("Turnaround time: " + storedProcess.getTurnAroundTime());
			System.out.println("I/O time: " + storedProcess.getTotalIOTime());
			System.out.println("Waiting time: " + storedProcess.getWaitTime());
			System.out.println("\n\n");
			threadNum++;
		}
	}

	//prints info for all the processes stored in the array list
	public static void printInfoLCFS() {
		int threadNum = 0;
		for(int i= processThread.size()-1; i >=0; i--) { //lcfs
		// for(int i= 0; i < processThread.size(); i++) { //fcfs
			Process storedProcess = (Process) processThread.get(i);
			System.out.println("Process number "+ threadNum);//i+":");
			System.out.println("A, B, C, IO: "+storedProcess.getArrivalTime()+", "
					+storedProcess.getCPUBurstTime()+", "+storedProcess.getTotalCPUTime()
					+", "+storedProcess.getIOBurstTime());
			System.out.println("Finishing time: " + storedProcess.getFinishingTime());
			System.out.println("Turnaround time: " + storedProcess.getTurnAroundTime());
			System.out.println("I/O time: " + storedProcess.getTotalIOTime());
			System.out.println("Waiting time: " + storedProcess.getWaitTime());
			System.out.println("\n\n\n");
			threadNum++;
		}
	}

	public static void printRunSummary() {
		double waiting = 0.0;
		double turnaround = 0.0;
		for(int i= 0; i < processThread.size();i++) {
			waiting += ((Process) processThread.get(i)).getWaitTime();
			turnaround += ((Process) processThread.get(i)).getTurnAroundTime();

		}

		System.out.println("Summary Data");
		System.out.println("Finishing Time:  " + endtime);
		double cpuUtil = cpuTime / endtime;
		double ioUtil = ioTime/endtime;
		System.out.printf("CPU Utilization: %.6f%n", cpuUtil );
		System.out.printf("I/O Utilization: %.6f%n", ioUtil);
		double throughput = processThread.size() / (endtime / 100.0);
		System.out.printf("Throughput: %.6f processes per hundred cycles%n", throughput);
		double avgTurnaroundTime = turnaround / processThread.size();
		System.out.printf("Average Turnaround Time: %.6f%n", avgTurnaroundTime);
		double avgWaitingTime = waiting/processThread.size();
		System.out.printf("Average Waiting Time: %.6f%n", avgWaitingTime);
	}

	public static int randomOS(int a, int b) {
		return (1 + (a % b));

	}


	public static void main(String args[]) throws IOException {
		fileToProcess("input-1.txt");
	}



}

class Process{

	private int arrivalTime = 0;
	private int cpuBurstTime = 0;
	private int currentCPUBurstTime = 0;
	private int ioBurstTime = 0;
	private int totalCPUTime = 0;
	private int remainingCPUTime = 0;
	private int totalIOTime = 0;
	private int currentIOTime = 0;

	private int endTime = 0;
	private int finishingTime = 0;
	private int waitTime = 0;
	private int priority = 0;
	private int turnAroundTime = 0;

	private int rank = 0;

	public final static int UNSTARTED = 0;
	public final static int READY = 1;
	public final static int RUNNING = 2;
	public final static int BLOCKED = 3;
	public final static int ENDED = 4;

	private int status = UNSTARTED;

	public void setStatus(int status) {
		this.status = status;
	}
	public int getStatus() {
		return status;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setCPUBurstTime(int cpuBurstTime) {
		this.cpuBurstTime = cpuBurstTime;
	}
	public int getCPUBurstTime() {
		return cpuBurstTime;
	}

	public void setCurrentCPUBurstTime(int currentCPUBurstTime) {
		this.currentCPUBurstTime = currentCPUBurstTime;
	}
	public int getCurrentCPUBurstTime() {
		return currentCPUBurstTime;
	}

	public void setTotalCPUTime(int totalCPUTime) {
		this.totalCPUTime = totalCPUTime;
	}
	public int getTotalCPUTime() {
		return totalCPUTime;
	}

	public void setRemainingCPUTime(int remainingCPUTime) {
		this.remainingCPUTime = remainingCPUTime;
	}
	public int getRemainingCPUTime() {
		return remainingCPUTime;
	}

	public void setIOBurstTime(int ioBurstTime) {
		this.ioBurstTime = ioBurstTime;
	}
	public int getIOBurstTime() {
		return ioBurstTime;
	}

	public void setTotalIOTime(int totalIOTime) {
		this.totalIOTime = totalIOTime;
	}
	public int getTotalIOTime() {
		return totalIOTime;
	}

	public void setCurrentIOTime(int currentIOTime) {
		this.currentIOTime = currentIOTime;
	}
	public int getCurrentIOTime() {
		return currentIOTime;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getRank() {
		return rank;
	}

	public void setTurnAroundTime(int turnAroundTime) {
		this.turnAroundTime = turnAroundTime;
	}
	public int getTurnAroundTime() {
		return turnAroundTime;
	}

	public void setFinishingTime(int finishingTime) {
		this.finishingTime = finishingTime;
	}
	public int getFinishingTime() {
		return finishingTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	public int getWaitTime() {
		return waitTime;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getPriority() {
		return priority;
	}

	public String toString() {
		return (this.arrivalTime + " " + this.cpuBurstTime + " " + this.remainingCPUTime + " " + this.ioBurstTime + this.getStatus());
	}
}
