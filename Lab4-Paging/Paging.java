import java.util.*;
import java.io.*;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;


public class Paging {
	/*
	 * simulate demand paging and see how the number of page faults depends on page size, 
	 *    program size, replacement algorithm, and job mix (job mix is defined below and 
	 *    includes locality and multiprogramming level).
	 */
	public static void main(String args[]) {
		// System.out.println(args.length);
		// for(int i = 0; i <args.length;i++){
		// 	System.out.println(args[i]);
		// }
		if(args.length == 7) {
			
			/*
			 * M, the machine size in words.
			 * P, the page size in words.
			 * S, the size of a process, i.e., the references are to virtual 
			   addresses 0..S-1. 
			 * J, the ‘‘job mix’’, which determines A, B, and C, as described below.
			 * N, the number of references for each process.
			 * R, the replacement algorithm, FIFO, RANDOM, or LRU.
			 * then there is another number after the algo, which is normally 0
			 */
			
			int machinesize = Integer.parseInt(args[0]);
			int pagesize = Integer.parseInt(args[1]);
			int sizeOfProcess = Integer.parseInt(args[2]);
			int jobmix = Integer.parseInt(args[3]);
			int numOfRefPerProcess = Integer.parseInt(args[4]);
			String replacementAlgo = args[5];
			int debuggingLevel = Integer.parseInt(args[6]);

			int numOfFrames = machinesize/pagesize;
			int numProcesses = 1; 
			
			if(jobmix!=1){
	        	numProcesses = 4;
	        }
			

	        System.out.println("\nThe machine size is " + machinesize);
	        System.out.println("The page size is " + pagesize);
	        System.out.println("The process size is " + sizeOfProcess);
	        System.out.println("The job mix number is " + jobmix);
	        System.out.println("The number of references per process is " + numOfRefPerProcess);
	        System.out.println("The replacement algorithm is " + replacementAlgo);
	        System.out.println("The level of debugging output is "+debuggingLevel +"\n");
	        
	        int[] wordRefPerProcess = new int[numProcesses]; //current word reference per process
	        
	        //The system begins with all frames empty, i.e. no pages loaded. 
	        //So the first reference for each process is a page default
	        Integer[][] frametable = new Integer[numOfFrames][3];
	        

	        
	        int numPageFaults = 0;
	        int[] faultsArray = new int[numProcesses];
	        


	        int[] runningTime = new int[machinesize/pagesize];
	        int[] evictArray = new int[numProcesses];
	        double[] residencyArray = new double[numProcesses];


	        int[] remainingLinesArray = new int[numProcesses];

	        int firstFreeFrame = 0;
	        int evictThis = 0;
	        int matchedFrameIndex = 0;


	        
	        /*
        	 * Scanner scanner = new Scanner(new FileReader("random-numbers.txt"));
			   String firstLine = scanner.nextLine();
        	 */


	        int finalsum = 0; 
        	double sumEvictions = 0; 
        	double sumResidency = 0;
        	String avgResidency1 ="";
        	String avgResidency2 ="";
	        
	        String random_numbersString = "";
	        // if (jobmix == 2 || jobmix == 3) { 
	        // 	numProcesses= 4;
	        // }else {
	        // 	numProcesses = 1;
	        // }
	        
	        for(int i = 0; i < frametable.length;i++) {
	        	frametable[i][0] = -1;
	        	frametable[i][1] = -1;
	        	frametable[i][2] = -1;
	        }          
	        
	        for(int i = 0; i <numProcesses;i++) {
	        	faultsArray[i] = 0;
	        }
	        
	        
	         //If a run has D processes (J=1 has D=1, the others have D=4), then process k 1<=k<=D begins by referencing word 111*k mod S.
	        
	        
	        /*
	         * For each process, print the number of page faults and the average residency time. 
	         * The latter is defined as the time (measured in memory references) that the 
	         *   page was evicted minus the time it was loaded. 
	         * So at eviction calculate the current page’s residency time and add it to a running sum.
	         */
	        
	        for(int i = 0; i < numProcesses; i++) {
	        	evictArray[i] = 0; 
	        	residencyArray[i] = 0;
	        }
	        
	       

	        //words per process population
	        for (int i = 0; i < numProcesses; i++) { 
	        	wordRefPerProcess[i] = (111 * (i + 1))%sizeOfProcess;
	        	//111*k as described in the lab. Now you want to simulate q (quantum) references for each job. quatum = 3
	        	
	        }
	        
			for(int ele = 0; ele<numProcesses; ele++) {
				remainingLinesArray[ele] = numOfRefPerProcess;
			}
	        
			try {
				random_numbersString = new String(readAllBytes(get("random-numbers.txt")));//http://jdevelopment.nl/java-7-oneliner-read-file-string/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        StringTokenizer randomNumbersTokenizer = new StringTokenizer(random_numbersString);
	        
	        /*
	         * There are four possible sets of processes (i.e., values for J)
				J=1: One process with A=1 and B=C=0, the simplest (fully sequential) case.
				J=2: Four processes, each with A=1 and B=C=0.
				J=3: Four processes, each with A=B=C=0 (fully random references).
				J=4: One process with A=.75, B=.25 and C=0; one process with 
				A=.75, B=0, and C=.25; one process with A=.75, B=.125 and C=.125; and one process with A=.5, B=.125 and C=.125.

	         */
	        //System.out.println("here3");
	        ArrayList<double[]> processBasedOnJobMix = new ArrayList<double[]>();//format A, B, C
	        int a = 0;
	        int b = 0; 
	        int c = 0;
	        double[] possibility1 = new double[3];// = {0.75,0.25,0}; //a, b, c
        	double[] possibility2 = new double[3];// = {0.75,0,0.25};
        	double[] possibility3 = new double[3];// = {0.75, 0.125, 0.125};
        	double[] possibility4 = new double[3];
	        if(jobmix == 1) {
	        	a = 1; 
	        	b = 0; 
	        	c = 0;
	        	
	        }else if(jobmix  == 2) {
	        	a = 1;
	        	b = 0;
	        	c = 0;
	        	
	        }else if(jobmix == 3) {
	        	a = 0; 
	        	b = 0;
	        	c = 0;
	        	
	        	
	        }else if(jobmix == 4) {
	        	for(int i = 0; i < 3; i++) {
	        		if (i == 0) {
	        			possibility1[i]=0.75 ;
	        			possibility2[i]=0.75 ;
	        			possibility3[i]=0.75;
	        			possibility4[i]=0.5;
	        		}else if(i == 1) {
	        			possibility1[i]=0.25 ;
	        			possibility2[i]= 0 ;
	        			possibility3[i]= 0.125;
	        			possibility4[i]=0.125;
	        		}else {
	        			possibility1[i]= 0 ;
	        			possibility2[i]= 0.25;
	        			possibility3[i]= 0.125;
	        			possibility4[i]=0.125;
	        		}
	        	}
	        	
	        }
	        
	        if(jobmix == 1) {
	        	double[] assignment = {a,b,c};
	        	processBasedOnJobMix.add(assignment);
	        }
	        else if(jobmix == 2){
	        	double[] assignment = {a,b,c};
	        	for(int j = 0; j < numProcesses; j++) {
		            processBasedOnJobMix.add(assignment); 
	            }
	        }
	        else if(jobmix == 3){
	        	double[] assignment = {a,b,c};
	        	for(int j = 0; j < numProcesses; j++) {
		            processBasedOnJobMix.add(assignment); 
	            }
	        }
	        else {
	        	processBasedOnJobMix.add(possibility1);
	        	processBasedOnJobMix.add(possibility2);
	        	processBasedOnJobMix.add(possibility3);
	        	processBasedOnJobMix.add(possibility4);
	        	
	        }
	        
	        int simulationMax = numProcesses*numOfRefPerProcess+1;
	        int processcounter = 0;

	        // System.out.println(simulationMax);

	        for(int simcounter = 1; simcounter < simulationMax; simcounter++){
	        	matchedFrameIndex = -1;
	        	for(int j = 0; j < frametable.length; j++){
	        		if(frametable[j][0] == processcounter){
	        			if(frametable[j][1] == wordRefPerProcess[processcounter]/pagesize){
	        				matchedFrameIndex = j;
	        				break;
	        			}
	        		}
	        	}

	        	if(matchedFrameIndex != -1){
	        		frametable[matchedFrameIndex][2] = simcounter;
	        	}
	        	else{
	        		faultsArray[processcounter]+=1;
	        		firstFreeFrame = -1;
	        		for(int j = 0; j < frametable.length;j++){
	        			if(frametable[j][1] == -1){
	        				firstFreeFrame = j;
	        				break;
	        			}
	        		}

	        		if(firstFreeFrame == -1){
	        			if(replacementAlgo.contains("lru")){
	        				evictThis = 0; 
	        				for(int j = 0; j <frametable.length;j++){
	        					if(frametable[evictThis][2] > frametable[j][2]){
	        						evictThis = j;
	        					}
	        				}
	        			}
	        			else if(replacementAlgo.contains("random")){
	        				int numberEviction = Integer.parseInt(randomNumbersTokenizer.nextToken());
	        				evictThis = (numberEviction+1)%frametable.length;
	        			}
	        			else if(replacementAlgo.contains("fifo")){
	        				evictThis = (evictThis+1)%frametable.length;
	        			}
	        			else{
	        				return;
	        			}

	        			int updateResArray = frametable[evictThis][0];
	        			residencyArray[updateResArray]+=(simcounter-runningTime[evictThis]);
	        			evictArray[updateResArray]+=1;

	        			frametable[evictThis][0] = processcounter;
	        			frametable[evictThis][1] = wordRefPerProcess[processcounter]/pagesize;
	        			frametable[evictThis][2] = simcounter;
	        			runningTime[evictThis] = simcounter;
	        		}
	        		else{
	        			frametable[firstFreeFrame][0] = processcounter; 
                        frametable[firstFreeFrame][1] = wordRefPerProcess[processcounter]/pagesize; 
                        frametable[firstFreeFrame][2] = simcounter; 
                        runningTime[firstFreeFrame] = simcounter;
	        		}
	        	}

	        	// finds the next word for a given process given A,B,C, and S (taken from pdf)

	        	double compareMe = Integer.parseInt(randomNumbersTokenizer.nextToken())/(Integer.MAX_VALUE + 1.0);
	        	int lastword = wordRefPerProcess[processcounter]; 
                
		        /* w+1 mod S with probability A
		        w-5 mod S with probability B
		        w+4 mod S with probability C
		        a random value in 0..S-1 each with probability (1-A-B-C)/S
		        A:processBasedOnJobMix.get(processcounter)[0]
		        B:processBasedOnJobMix.get(processcounter)[1]
		        C:processBasedOnJobMix.get(processcounter)[2]
		        S: sizeOfProcess*/
		        
		        if(compareMe < processBasedOnJobMix.get(processcounter)[0]) {
		            lastword = (lastword + 1)%sizeOfProcess;
		        } 
		        else if (compareMe < processBasedOnJobMix.get(processcounter)[0] + processBasedOnJobMix.get(processcounter)[1]) {
		            lastword = (lastword - 5 + sizeOfProcess)%sizeOfProcess;
		        } 
		        else if (compareMe < processBasedOnJobMix.get(processcounter)[0] + processBasedOnJobMix.get(processcounter)[1] + processBasedOnJobMix.get(processcounter)[2]) { 
		            lastword = (lastword + 4)%sizeOfProcess;
		        } 
		        else {
		        	lastword = Integer.parseInt(randomNumbersTokenizer.nextToken())%sizeOfProcess;
		        }
		        
		        wordRefPerProcess[processcounter]= lastword;


	        	remainingLinesArray[processcounter]-=1;
	        	if(remainingLinesArray[processcounter] == 0){
	        		processcounter+=1;
	        	}
	        	else if(simcounter%3 == 0){
	        		if(remainingLinesArray[processcounter] > numOfRefPerProcess%3 -1){
	        			processcounter = (processcounter + 1)%numProcesses;
	        		}
	        	}

	        }

	        //final output stuff
        	for(int k = 0; k <numProcesses; k++){
        		finalsum+=faultsArray[k];
        		sumEvictions+=evictArray[k];
        		sumResidency+=residencyArray[k];
        	}


        	//print output
        	for(int i = 0; i < faultsArray.length; i++){
        		if(evictArray[i] == 0){
        			avgResidency1 = "undefined";
        		}
        		else{
        			avgResidency1 = (residencyArray[i]/evictArray[i])+"";
        		}
        		System.out.println("Process "+(i+1)+" had "+faultsArray[i]+" faults and "+avgResidency1+" average residency");
        	}


        	if(sumEvictions == 0){
    			avgResidency2 = "undefined";
    		}
    		else{
    			avgResidency2 = (sumResidency/sumEvictions)+"";
    		}

        	System.out.println("The total number of faults is "+finalsum+ " and the overall average residency is "+avgResidency2+".");
	        System.out.println("");
		}

		else{
			System.out.println("We cannot cater to this input");
		}
	}

}
