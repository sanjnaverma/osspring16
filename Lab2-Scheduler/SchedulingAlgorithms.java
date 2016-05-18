import java.io.*;
import java.util.*;

public class SchedulingAlgorithms {
	public static void main(String[] args) throws IOException{//hprn(String[] args) throws IOException{\



//start FCFS
				Scheduler fcfs = new Scheduler();
				Scanner input = null;

				if(args[0].equals("--verbose")) {
				  fcfs.isVerbose = true;
					if(args[0].equals("--verbose") && args[1].equals("fcfs") || args[0].equals("fcfs")){
					  fcfs.fileToProcess(args[2]);
					}
				  input = new Scanner(new BufferedReader(new FileReader(args[3])));

				}
				else {
					if(args[0].equals("--verbose") && args[1].equals("fcfs") || args[0].equals("fcfs")){
				  	fcfs.fileToProcess(args[1]);
					}
				  input = new Scanner(new BufferedReader(new FileReader(args[2])));
				}

				ArrayList<Process> processThread = fcfs.processThread;
				fcfs.sortByArrivalTime(processThread);

				Process processChecker = null;
				int time = 0;//counter to be incremented

				ArrayList<Process> readyProcesses = new ArrayList<Process>();
				if(args[0].equals("--verbose") && args[1].equals("fcfs") || args[0].equals("fcfs")){
					fcfs.verbose(processThread, time);
				}


				while(!fcfs.isProcessTerminated(processThread)) {
				  for(int i = 0; i <processThread.size(); i++) {
				    if(!readyProcesses.contains(processThread.get(i))){
				      if(processThread.get(i).getArrivalTime() <= time) {
				        if(processThread.get(i).getStatus() == processThread.get(i).UNSTARTED || processThread.get(i).getStatus() == processThread.get(i).READY) {
				          processThread.get(i).setStatus(processThread.get(i).READY);
				          readyProcesses.add(processThread.get(i));
				        }
				      }
				    }
				  }

				  if(readyProcesses.isEmpty()) {
				    boolean blocker = false;
				    time++;
						if(args[0].equals("--verbose") && args[1].equals("fcfs") || args[0].equals("fcfs")){

					    fcfs.verbose(processThread, time);
						}
				    fcfs.addWaitTime(processThread);

				    for(int i = 0; i <processThread.size(); i++) {
				      if(processThread.get(i).getStatus() == processThread.get(i).BLOCKED) {
				        processThread.get(i).setCurrentIOTime(processThread.get(i).getCurrentIOTime() -1);
				        processThread.get(i).setTotalIOTime(processThread.get(i).getTotalIOTime() + 1);
				        blocker = true;
				        if(processThread.get(i).getCurrentIOTime() == 0) {
				          processThread.get(i).setStatus(processThread.get(i).READY);//RUNNING
				        }
				      }
				    }
				    if(blocker == true) {
				      fcfs.ioTime++;
				    }
				  }

				  else {
				    Process runProcess = readyProcesses.get(0);
				    readyProcesses.remove(0); //delete it from the readyProcesses ArrayList because its running now

				    runProcess.setCurrentCPUBurstTime(fcfs.randomOS(input.nextInt(), runProcess.getCPUBurstTime()));

				    if(runProcess.getCurrentCPUBurstTime() > runProcess.getRemainingCPUTime()) {
				      runProcess.setCurrentCPUBurstTime(runProcess.getRemainingCPUTime());
				    }

				    runProcess.setCurrentIOTime(runProcess.getCurrentCPUBurstTime() * runProcess.getIOBurstTime());
				    runProcess.setStatus(runProcess.RUNNING);
				    while(runProcess.getCurrentCPUBurstTime() > 0) {
				      time++;
							if(args[0].equals("--verbose") && args[1].equals("fcfs") || args[0].equals("fcfs")){
								fcfs.verbose(processThread, time);
							}
				      fcfs.addWaitTime(processThread);
				      boolean blocked = false;

				      for(int j = 0; j < processThread.size(); j++) {
				        if(processThread.get(j).getStatus() == processThread.get(j).BLOCKED) {
				          blocked = true;
				          processThread.get(j).setCurrentIOTime(processThread.get(j).getCurrentIOTime() - 1);
				          processThread.get(j).setTotalIOTime(processThread.get(j).getTotalIOTime() +1);

				          if(processThread.get(j).getCurrentIOTime() == 0) {
				            processThread.get(j).setStatus(processThread.get(j).READY);
				          }

				        }

				      }
				      if(blocked == true) {
				        fcfs.ioTime++;
				      }

				      for(int k = 0; k < processThread.size(); k++) {
				        if(!readyProcesses.contains(processThread.get(k))){
				          if(processThread.get(k).getArrivalTime() <= time) {
				            if(processThread.get(k).getStatus() == processThread.get(k).UNSTARTED || processThread.get(k).getStatus() == processThread.get(k).READY) {
				              processThread.get(k).setStatus(processThread.get(k).READY);
				              readyProcesses.add(processThread.get(k));
				            }
				          }
				        }
				      }

				      if(runProcess.getRemainingCPUTime() >0) {
				        runProcess.setCurrentCPUBurstTime(runProcess.getCurrentCPUBurstTime() -1);
				        runProcess.setRemainingCPUTime(runProcess.getRemainingCPUTime() -1);

				        if(runProcess.getRemainingCPUTime()==0) {
				          runProcess.setStatus(runProcess.ENDED);
				          runProcess.setFinishingTime(time);
				          runProcess.setTurnAroundTime(runProcess.getFinishingTime() - runProcess.getArrivalTime());
				              break;
				        }
				      }
				    }

				    if(runProcess.getStatus() == runProcess.RUNNING) {
				      runProcess.setStatus(runProcess.BLOCKED);
				    }

				  }
				}
				if(args[0].equals("--verbose") && args[1].equals("fcfs") || args[0].equals("fcfs")){
					fcfs.setFinishingTime(time);
					System.out.println("The scheduling algorithm used was First Come First Served:");
					fcfs.printInfo();
					fcfs.printRunSummary();
					//end fcfsAlgo

				}


//start rr
				System.out.println("\n\n\n");
				Process processToRun = null;


				Scheduler roundrobin = new Scheduler();
				int QUANTUM = 2;
				Scanner input2 = null;

				if(args[0].equals("--verbose")) {//if verbose command is indicated then print out verbose output
				  roundrobin.isVerbose = true;
					if(args[0].equals("--verbose") && args[1].equals("rr") || args[0].equals("rr")){
					  roundrobin.fileToProcess(args[2]);
					}
				  input2 = new Scanner(new BufferedReader(new FileReader(args[3])));

				}
				else {
					if(args[0].equals("--verbose") && args[1].equals("rr") || args[0].equals("rr")){
					  roundrobin.fileToProcess(args[1]);
					}
				  input2 = new Scanner(new BufferedReader(new FileReader(args[2])));

				}

				ArrayList<Process> processThreadRR = roundrobin.processThread;
				roundrobin.sortByArrivalTime(processThreadRR);
				roundrobin.setProcessOrder(processThreadRR);



				int time2 = 0;
				int io2 = 0;
				boolean isBlockedRR = false;


				ArrayList<Process> readyProcessRR = new ArrayList<Process>();

				if(args[0].equals("--verbose") && args[1].equals("rr") || args[0].equals("rr")){
					roundrobin.verbose(processThreadRR, time2);
				}



				while(!roundrobin.isProcessTerminated(processThreadRR)){
				  for(int i = 0; i < processThreadRR.size(); i++){
				    Process temp = processThreadRR.get(i);
				    if(!readyProcessRR.contains(temp)){
				      if(temp.getArrivalTime() <= time2){
				        if(temp.getStatus() == temp.UNSTARTED || temp.getStatus() == temp.READY){
				          temp.setStatus(temp.READY);
				          readyProcessRR.add(temp);
				        }//end if emp.getStatus() == temp.UNSTARTED || temp.getStatus() == temp.READY
				      }//end if temp.getArrivalTime() <= time2
				    }//end if !readyProcessRR.contains(temp)
				  }//end for loop int i


				  if(readyProcessRR.isEmpty()){
				    time2++;
						if(args[0].equals("--verbose") && args[1].equals("rr") || args[0].equals("rr")){
					    roundrobin.verbose(processThreadRR, time2);
						}
				    roundrobin.addWaitTime(processThreadRR);
				    for(int i = 0; i < processThreadRR.size(); i++){
				      Process temp = processThreadRR.get(i);
				      if(temp.getStatus() == temp.BLOCKED){
				        isBlockedRR = true;
				        temp.setCurrentIOTime(temp.getCurrentIOTime() - 1);
				        temp.setTotalIOTime(temp.getTotalIOTime() +1);
				        if(temp.getCurrentIOTime() == 0){
				          temp.setStatus(temp.READY);
				        }//end if temp.getCurrentIOTime() == 0
				      }//temp.getStatus() == temp.BLOCKED
				    }//end for int i

				    if(isBlockedRR == true){
				      roundrobin.ioTime++;
				    }
				  }//end if readyProcessRR.is empty()
				  else{
				    //processes are ready to run therefore reset QUANTUM to 2
				    QUANTUM = 2;
				    processToRun = readyProcessRR.get(0);
				    readyProcessRR.remove(0);

				    if(processToRun.getCurrentCPUBurstTime() == 0){
				      processToRun.setCurrentCPUBurstTime(roundrobin.randomOS(input2.nextInt(), processToRun.getCPUBurstTime()));

				      if(processToRun.getCurrentCPUBurstTime() > processToRun.getRemainingCPUTime()){
				        processToRun.setCurrentCPUBurstTime(processToRun.getRemainingCPUTime());
				      }//end if processToRun.getCurrentCPUBurstTime() > processToRun.getRemainingCPUTime()
				      processToRun.setCurrentIOTime(processToRun.getCurrentCPUBurstTime() * processToRun.getIOBurstTime());
				    }//end if processToRun.getCurrentCPUBurstTime() == 0

				    processToRun.setStatus(processToRun.RUNNING);

				    while(QUANTUM > 0 ){
				      time2++;
							if(args[0].equals("--verbose") && args[1].equals("rr") || args[0].equals("rr")){
					      roundrobin.verbose(processThreadRR, time2);
							}
				      roundrobin.addWaitTime(processThreadRR);
				      isBlockedRR = false;

				      for(int i = 0; i < processThreadRR.size(); i++){
				        Process temp = processThreadRR.get(i);
				        if (temp.getStatus() == temp.BLOCKED) {
				          isBlockedRR = true;
				          temp.setCurrentIOTime(temp.getCurrentIOTime() - 1);
				          temp.setTotalIOTime(temp.getTotalIOTime() + 1);

				          if(temp.getCurrentIOTime() == 0 ){
				            temp.setStatus(temp.READY);
				          }
				        }// end if (temp.getStatus() == temp.BLOCKED)
				      }//end for int i
				      if(isBlockedRR == true){
				        roundrobin.ioTime++;
				      }
				      if(processToRun.getRemainingCPUTime() > 0){
				        QUANTUM-=1;
				        processToRun.setCurrentCPUBurstTime(processToRun.getCurrentCPUBurstTime() - 1);
				        processToRun.setRemainingCPUTime(processToRun.getRemainingCPUTime() - 1);
				      }
				      if(processToRun.getRemainingCPUTime() == 0){
				        processToRun.setStatus(processToRun.ENDED);
				        processToRun.setFinishingTime(time2);
				        processToRun.setTurnAroundTime(processToRun.getFinishingTime() - processToRun.getArrivalTime());
				        break;
				      }
				      if(processToRun.getCurrentCPUBurstTime() == 0){
				        processToRun.setStatus(processToRun.BLOCKED);
				        break;
				      }

				      if(QUANTUM > 0 ){
				        for(int i = 0; i <processThreadRR.size(); i++){
				          Process temp = processThreadRR.get(i);
				          if(!readyProcessRR.contains(temp)){
				            if(temp.getArrivalTime() <= time2){
				              if(temp.getStatus() == temp.UNSTARTED || temp.getStatus() == temp.READY){
				                temp.setStatus(temp.READY);
				                readyProcessRR.add(temp);
				              }
				            }
				          }
				        }
				      }//end if quantum > 0
				    }//end while QUANTUM > 0

				    if(processToRun.getStatus() == processToRun.RUNNING){
				      processToRun.setStatus(processToRun.READY);
				      QUANTUM = 2;
				    }//processToRun.getStatus() == processToRun.RUNNING
				  }//end else statement if readyProcessRR.is not empty()
				}//end while(!roundrobin.isProcessTerminated(processThreadRR))
				// System.out.println("am i here?");

				if(args[0].equals("--verbose") && args[1].equals("rr") || args[0].equals("rr")){

					roundrobin.setFinishingTime(time2);
					System.out.println("The sorting algorithm that was used is Round Robin:\n");
					roundrobin.printInfo();
					roundrobin.printRunSummary();


					//====================================\\

				}
//end rr


//start lcfs

					//====================================\\
					//lcfs
					Scheduler lcfs = new Scheduler();
					Scanner input3 = null;
					boolean blockerLCFS = false;

					if(args[0].equals("--verbose")) {
						lcfs.isVerbose = true;
						if(args[0].equals("--verbose") && args[1].equals("lcfs") || args[0].equals("lcfs")){
							lcfs.fileToProcess(args[2]);
						}
						input3 = new Scanner(new BufferedReader(new FileReader(args[3])));

					}
					else {
						if(args[0].equals("--verbose") && args[1].equals("lcfs") || args[0].equals("lcfs")){
							lcfs.fileToProcess(args[1]);
						}
						input3 = new Scanner(new BufferedReader(new FileReader(args[2])));

					}

					ArrayList<Process> processThreadLCFS = lcfs.processThread;
					// Stack<Process> processThreadLCFS = lcfs.processThread
					lcfs.sortByArrivalTime(processThreadLCFS);

					Process processCheckerLCFS = null;
					int time3 = 0;//counter to be incremented

					ArrayList<Process> readyProcessesLCFS = new ArrayList<Process>();
					if(args[0].equals("--verbose") && args[1].equals("lcfs") || args[0].equals("lcfs")){
						lcfs.verbose(processThreadLCFS, time3);
					}

					while(!lcfs.isProcessTerminated(processThreadLCFS)) {
						//add processes to the readyProcessesLCFS stack in ready state
						for(int i = processThreadLCFS.size()-1; i >= 0; i--) {
						// for(int i = 0; i < processThreadLCFS.size(); i++) {
							if(!readyProcessesLCFS.contains(processThreadLCFS.get(i))){
								if(processThreadLCFS.get(i).getArrivalTime() <= time3) {
									if(processThreadLCFS.get(i).getStatus() == processThreadLCFS.get(i).UNSTARTED || processThreadLCFS.get(i).getStatus() == processThreadLCFS.get(i).READY) {
										processThreadLCFS.get(i).setStatus(processThreadLCFS.get(i).READY);
										readyProcessesLCFS.add(processThreadLCFS.get(i));
									}
								}
							}
						}

						//if nothing in the stack
						if(readyProcessesLCFS.isEmpty()) {
							// boolean blockerLCFS = false;
							blockerLCFS = false;
							time3++;
							if(args[0].equals("--verbose") && args[1].equals("lcfs") || args[0].equals("lcfs")){
								lcfs.verbose(processThreadLCFS, time3);
							}
							lcfs.addWaitTime(processThreadLCFS);

							for(int i = processThreadLCFS.size()-1; i >= 0; i--) {
							// for(int i = 0; i <processThreadLCFS.size(); i++) {
								if(processThreadLCFS.get(i).getStatus() == processThreadLCFS.get(i).BLOCKED) {
									processThreadLCFS.get(i).setCurrentIOTime(processThreadLCFS.get(i).getCurrentIOTime() -1);
									processThreadLCFS.get(i).setTotalIOTime(processThreadLCFS.get(i).getTotalIOTime() + 1);
									blockerLCFS = true;
									if(processThreadLCFS.get(i).getCurrentIOTime() == 0) {
										processThreadLCFS.get(i).setStatus(processThreadLCFS.get(i).READY);//RUNNING
									}
								}
							}
							if(blockerLCFS == true) {
								lcfs.ioTime++;
							}
						}

						else {
							Process topOfStack = readyProcessesLCFS.get(readyProcessesLCFS.size()-1);//0); //take top off stack
							readyProcessesLCFS.remove(readyProcessesLCFS.size()-1);//0); //delete it from the readyProcessesLCFS ArrayList because its running now

							topOfStack.setCurrentCPUBurstTime(lcfs.randomOS(input3.nextInt(), topOfStack.getCPUBurstTime()));

							if(topOfStack.getCurrentCPUBurstTime() > topOfStack.getRemainingCPUTime()) {
								topOfStack.setCurrentCPUBurstTime(topOfStack.getRemainingCPUTime());
							}

							topOfStack.setCurrentIOTime(topOfStack.getCurrentCPUBurstTime() * topOfStack.getIOBurstTime());
							topOfStack.setStatus(topOfStack.RUNNING);
							while(topOfStack.getCurrentCPUBurstTime() > 0) {
								time3++;
								if(args[0].equals("--verbose") && args[1].equals("lcfs") || args[0].equals("lcfs")){
									lcfs.verbose(processThreadLCFS, time3);
								}
								lcfs.addWaitTime(processThreadLCFS);
								blockerLCFS = false;

								for(int j = processThreadLCFS.size()-1; j >= 0 ; j--) {
								// for(int j = 0; j < processThreadLCFS.size(); j++) {
									if(processThreadLCFS.get(j).getStatus() == processThreadLCFS.get(j).BLOCKED) {
										blockerLCFS = true;
										processThreadLCFS.get(j).setCurrentIOTime(processThreadLCFS.get(j).getCurrentIOTime() - 1);
										processThreadLCFS.get(j).setTotalIOTime(processThreadLCFS.get(j).getTotalIOTime() +1);

										if(processThreadLCFS.get(j).getCurrentIOTime() == 0) {
											processThreadLCFS.get(j).setStatus(processThreadLCFS.get(j).READY);
										}

									}

								}
								if(blockerLCFS == true) {
									lcfs.ioTime++;
								}

								for(int k = processThreadLCFS.size()-1; k >= 0 ; k--) {
								// for(int k = 0; k < processThreadLCFS.size(); k++) {
									if(!readyProcessesLCFS.contains(processThreadLCFS.get(k))){
										if(processThreadLCFS.get(k).getArrivalTime() <= time3) {
											if(processThreadLCFS.get(k).getStatus() == processThreadLCFS.get(k).UNSTARTED || processThreadLCFS.get(k).getStatus() == processThreadLCFS.get(k).READY) {
												processThreadLCFS.get(k).setStatus(processThreadLCFS.get(k).READY);
												readyProcessesLCFS.add(processThreadLCFS.get(k));
											}
										}
									}
								}

								if(topOfStack.getRemainingCPUTime() >0) {
									topOfStack.setCurrentCPUBurstTime(topOfStack.getCurrentCPUBurstTime() -1);
									topOfStack.setRemainingCPUTime(topOfStack.getRemainingCPUTime() -1);

									if(topOfStack.getRemainingCPUTime()==0) {
										topOfStack.setStatus(topOfStack.ENDED);
										topOfStack.setFinishingTime(time3);
										topOfStack.setTurnAroundTime(topOfStack.getFinishingTime() - topOfStack.getArrivalTime());
												break;
									}
								}
							}

							if(topOfStack.getStatus() == topOfStack.RUNNING) {
								topOfStack.setStatus(topOfStack.BLOCKED);
							}

						}
					}
					if(args[0].equals("--verbose") && args[1].equals("lcfs") || args[0].equals("lcfs")){
						lcfs.setFinishingTime(time3);
						System.out.println("\nThe scheduling algorithm used was Last Come First Served:\n----------------------------------------------------------\n");
						lcfs.printInfo();
						lcfs.printRunSummary();
					}



//end lcfs

//====================================\\
						//hprn
						Scheduler hprn = new Scheduler();
						Scanner input4 = null;
						// boolean hprn = false;
						int timeProcessInSystem = 0;
						int runningTimeToDate = 0;
						boolean blockerhprn = false;


						if(args[0].equals("--verbose")) {
						  hprn.isVerbose = true;
							if(args[0].equals("--verbose") && args[1].equals("hprn") || args[0].equals("hprn")){
								hprn.fileToProcess(args[2]);
							}
						  input4 = new Scanner(new BufferedReader(new FileReader(args[3])));

						}
						else {
							if(args[0].equals("--verbose") && args[1].equals("hprn") || args[0].equals("hprn")){
								hprn.fileToProcess(args[1]);
							}

						  input4 = new Scanner(new BufferedReader(new FileReader(args[2])));
						}

						ArrayList<Process> processThreadhprn = hprn.processThread;
						hprn.sortByArrivalTime(processThreadhprn);

						Process processCheckerhprn = null;//CHANGE THIS IN hprn
						int time4 = 0;//counter to be incremented

						ArrayList<Process> readyProcesseshprn = new ArrayList<Process>();//queue
						if(args[0].equals("--verbose") && args[1].equals("hprn") || args[0].equals("hprn")){
							hprn.verbose(processThreadhprn, time4);
						}

						while(!hprn.isProcessTerminated(processThreadhprn)) {
							//add processes to the readyProcesseshprn queue in ready state
						  // for(int i = processThreadhprn.size()-1; i >= 0; i--) {
							for(int i = 0; i < processThreadhprn.size(); i++) {
						    if(!readyProcesseshprn.contains(processThreadhprn.get(i))){
						      if(processThreadhprn.get(i).getArrivalTime() <= time4) {
						        if(processThreadhprn.get(i).getStatus() == processThreadhprn.get(i).UNSTARTED || processThreadhprn.get(i).getStatus() == processThreadhprn.get(i).READY) {
						          processThreadhprn.get(i).setStatus(processThreadhprn.get(i).READY);
						          readyProcesseshprn.add(processThreadhprn.get(i));
						        }
						      }
						    }
						  }

							//if nothing in the stack
						  if(readyProcesseshprn.isEmpty()) {
							    // boolean blockerhprn = false;
								blockerhprn = false;
						    time4++;
								if(args[0].equals("--verbose") && args[1].equals("hprn") || args[0].equals("hprn")){
							    hprn.verbose(processThreadhprn, time4);
								}
						    hprn.addWaitTime(processThreadhprn);

								// for(int i = processThreadhprn.size()-1; i >= 0; i--) {
						    for(int i = 0; i <processThreadhprn.size(); i++) {
						      if(processThreadhprn.get(i).getStatus() == processThreadhprn.get(i).BLOCKED) {
						        processThreadhprn.get(i).setCurrentIOTime(processThreadhprn.get(i).getCurrentIOTime() -1);
						        processThreadhprn.get(i).setTotalIOTime(processThreadhprn.get(i).getTotalIOTime() - 1);
						        blockerhprn = true;
						        if(processThreadhprn.get(i).getCurrentIOTime() == 0) {
						          processThreadhprn.get(i).setStatus(processThreadhprn.get(i).READY);//RUNNING
						        }
						      }
						    }
						    if(blockerhprn == true) {
						      hprn.ioTime++;
						    }
								runningTimeToDate++;
						  }

						  else {
						    Process topOfStack = readyProcesseshprn.get(0);//readyProcesseshprn.size()-1);//0); //take top off stack
						    readyProcesseshprn.remove(0);//readyProcesseshprn.size()-1);//0); //delete it from the readyProcesseshprn ArrayList because its running now
						    topOfStack.setCurrentCPUBurstTime(hprn.randomOS(input4.nextInt(), topOfStack.getCPUBurstTime()));

						    if(topOfStack.getCurrentCPUBurstTime() > topOfStack.getRemainingCPUTime()) {
						      topOfStack.setCurrentCPUBurstTime(topOfStack.getRemainingCPUTime());
						    }

						    topOfStack.setCurrentIOTime(topOfStack.getCurrentCPUBurstTime() * topOfStack.getIOBurstTime());
						    topOfStack.setStatus(topOfStack.RUNNING);
						    while(topOfStack.getCurrentCPUBurstTime() > 0) {
						      time4++;
									if(args[0].equals("--verbose") && args[1].equals("hprn") || args[0].equals("hprn")){
							      hprn.verbose(processThreadhprn, time4);
									}
						      hprn.addWaitTime(processThreadhprn);
						      blockerhprn = false;
									// for(int j = processThreadhprn.size()-1; j >= 0 ; j--) {
						      for(int j = 0; j < processThreadhprn.size(); j++) {
						        if(processThreadhprn.get(j).getStatus() == processThreadhprn.get(j).BLOCKED) {
						          blockerhprn = true;
						          processThreadhprn.get(j).setCurrentIOTime(processThreadhprn.get(j).getCurrentIOTime() - 1);
						          processThreadhprn.get(j).setTotalIOTime(processThreadhprn.get(j).getTotalIOTime() +1);

						          if(processThreadhprn.get(j).getCurrentIOTime() == 0) {
						            processThreadhprn.get(j).setStatus(processThreadhprn.get(j).READY);
						          }

						        }
						      }
						      if(blockerhprn == true) {
						        hprn.ioTime++;
						      }
									// for(int k = processThreadhprn.size()-1; k >= 0 ; k--) {
						      for(int k = 0; k < processThreadhprn.size(); k++) {
						        if(!readyProcesseshprn.contains(processThreadhprn.get(k))){//if its not in the queue
						          if(processThreadhprn.get(k).getArrivalTime() <= time4) { //if arrival time is less than time4
						            if(processThreadhprn.get(k).getStatus() == processThreadhprn.get(k).UNSTARTED || processThreadhprn.get(k).getStatus() == processThreadhprn.get(k).READY) {
						              processThreadhprn.get(k).setStatus(processThreadhprn.get(k).READY); //switch to ready
						              readyProcesseshprn.add(processThreadhprn.get(k));//add to queue
						            }
						          }
						        }
						      }

						      if(topOfStack.getRemainingCPUTime() >0) {
						        topOfStack.setCurrentCPUBurstTime(topOfStack.getCurrentCPUBurstTime() -1);
						        topOfStack.setRemainingCPUTime(topOfStack.getRemainingCPUTime() -1);

						        if(topOfStack.getRemainingCPUTime()==0) {
						          topOfStack.setStatus(topOfStack.ENDED);
						          topOfStack.setFinishingTime(time4);
						          topOfStack.setTurnAroundTime(topOfStack.getFinishingTime() - topOfStack.getArrivalTime());
						          break;
						        }
						      }
						    }
						    if(topOfStack.getStatus() == topOfStack.RUNNING) {
						      topOfStack.setStatus(topOfStack.BLOCKED);
						    }
						  }
							timeProcessInSystem++;
							runningTimeToDate++;
						}

						if(runningTimeToDate != 0){
							int ratio = timeProcessInSystem/runningTimeToDate;//T is the wall clock time this process has been in system and t is the running time of the process to date.

						}
						
						if(args[0].equals("--verbose") && args[1].equals("hprn") || args[0].equals("hprn")){
							hprn.setFinishingTime(time4);
							System.out.println("\nThe scheduling algorithm used was Highest Penalty Ratio Next :\n----------------------------------------------------------\n");
							hprn.printInfo();
							hprn.printRunSummary();
							//end hprn
						}



						

//end main
	}



//end class
}
