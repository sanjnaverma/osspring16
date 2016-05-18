import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Iterator;

public class linker_sv1058 {

	public static void main(String[] args) throws IOException {
		String fileName = args[0];//"input-1.txt";//args[0];
		Scanner input = new Scanner(new BufferedReader(new FileReader(fileName)));
		pass1(input);
		
		String fileName2 = args[0];//"input-1.txt";//args[0];
		Scanner input2 = new Scanner(new BufferedReader(new FileReader(fileName2)));
		pass2(input2);
		printMemoryMap(finalList);
	}

	
	static HashMap symbolTable = new HashMap();// symbol and absolute address	
	static HashMap symbolDef = new HashMap();// symbol and its module
	static HashMap module = new HashMap();//module number and base address 	
	static HashMap moduleSize = new HashMap();// module and its size
	static ArrayList originalMod = new ArrayList(); // Stores base address at a given module
	static ArrayList finalList      = new ArrayList(); 
	static int moduleCount = 0;
	public static HashMap useList2 = new HashMap();
	/* First pass relocates relative addresses */
	/*
	 * The first pass simply finds the base address of 
	 * each module and produces the symbol table giving the values for 
	 * xy and z (2 and 15 respectively). 
	 * The second pass does the real work using the symbol table and base addresses produced in pass one.
	 * 
	 */
	public static void pass1(Scanner input){
		ArrayList symbolsInCurrentMod = new ArrayList();   
	    ArrayList symbolAddressinMod = new ArrayList();    
	    int line = 0; 
	    ArrayList symbolList = new ArrayList();     
	    ArrayList useList = new ArrayList(); 
	    int currentMod = -1; 
	    int absoluteaddress;  
	    int difference      = 0;  
	    int integerperline;   //which line: definition, use or mmconst line?        
	    String symbol       = ""; // I,A,R, or E
	    int symboladdr            = 0;  //4 numbers following symbol from above

	    // Loops through the entire input file
	    while(input.hasNext()){
	      line++;
	      integerperline = input.nextInt();

	      if((line-1)%3 == 0){
	        originalMod.add(difference);
	        symbolsInCurrentMod.clear();
	        symbolAddressinMod.clear();
	        currentMod++;
	      }
	      
	      int i = 0;
	      while(i< integerperline) {
	        
	        symbol = input.next();
	        symboladdr   = input.nextInt();
	        
	        if((line-1)%3 == 0){
	          // Checks if there are multiple definitions, prints error if not
	          if(symbolTable.get(symbol) == null) {//!symbolTable.containsKey(symbol)){
	            absoluteaddress = symboladdr + (Integer) originalMod.get(currentMod);
	            symbolTable.put(symbol,absoluteaddress);
	            symbolList.add(symbol);
	            symbolsInCurrentMod.add(symbol);
	            symbolAddressinMod.add(symboladdr);
	          }
	          else
	            System.out.println("Error: "+symbol+" is multiple defined. Will use first value");
	        }
	        // Use list
	        if((line-2)%3 == 0){
	            if(!useList.contains(symbol))
	              useList.add(symbol);
	        }
	        if (line%3 == 0 && line > 0){
	          difference++;
	          int k = 0;
	          while(k<symbolsInCurrentMod.size()) {
	            if((Integer) symbolAddressinMod.get(k) >= integerperline){
	              System.out.println("Error: "+ symbolsInCurrentMod.get(k)+" is outside module. Will use 0");
	              symbolTable.put(symbolsInCurrentMod.get(k), originalMod.get(currentMod));
	            }
	            k++;
	          }
	        }
	        i++;
	      }
	    }
	    
	    int j = 0;
	    while(j<symbolList.size()) {
	      if(!useList.contains(symbolList.get(j))) {
	    	  System.out.println("Warning: "+symbolList.get(j)+" is defined but never used");
	      }
	      j++;  
	      
	    }
	    //Print table
	    System.out.println("Symbol Table:");
		Set keySet = symbolTable.keySet();
		Iterator i = keySet.iterator();
		
		while(i.hasNext()) {
			String key = (String) i.next();
			Integer val = (Integer) symbolTable.get(key);//i.nextInt();
			System.out.println(key+": "+val);
		}
		
		System.out.println("\n\n\n");
	}
	
	
	
	
	
	  public static void pass2(Scanner input){
		ArrayList useList= new ArrayList(); 
	    int line         = 0; 
	    ArrayList mmconst = new ArrayList();
	    ArrayList symbolList  = new ArrayList(); 
	    int numItemsOnLine;              
	    String symbol= ""; 
	    int address= 0;  
	    
	    while(input.hasNext()){
	      line++;
	      numItemsOnLine = input.nextInt();
	      
	      int i = 0;
	      while(i<numItemsOnLine) {
	    	int numSymbols = 0;
	        symbol          = input.next();
	        address            = input.nextInt();
	        	        
	        // verify if line three exists, then add the address and the word
	        if (line%3 == 0 && line > 0){
	          symbolList.add(symbol);
	          mmconst.add(address); 
	        }
	        
	     // Use list line 2
	        if((line-2)%3 == 0){
	          if(symbolTable.containsKey(symbol)){
	            useList.add(symbol);
	            useList.add(address);
	          }
	          else{
	            useList.add(symbol);
	            useList.add(0);
	            System.out.println("Error: used but not defined: placing 0");
	          }
	        }
	        i++;
	      }
	      if (line%3 == 0 && line > 0){
	        finalList = createFinalList(finalList, useList, mmconst, symbolList);
	        useList.clear();
	        mmconst.clear();
	        symbolList.clear();
	      }
	    }
	  }

	  public static int setRelativeAddr(ArrayList programList, ArrayList symbolList, int addr){
	    int nextAddr = (Integer) programList.get(addr);
	     // Checks if it's the 'initial' value
	    int initial = 777;
	    if(nextAddr % 1000 == initial){
	      symbolList.set(addr, "resolved");
	      return addr;
	    }      else if(symbolList.get(addr).equals("resolved")){//symbolList.get(addr) == "resolved"
	      symbolList.set(addr, "resolved");
	      return addr;
	    }      else if((nextAddr % 1000) > (Integer) symbolList.size()){
	      symbolList.set(addr, "resolved");
	      System.out.println("Error: Pointer exceeds module size;terminated");
	      return addr;
	    }	    else if(symbolList.get(nextAddr % 1000).equals("resolved")){
	      symbolList.set(addr, "resolved");
	      int addrValue     = (Integer) programList.get(addr);
	      int relativeValue = (Integer) programList.get(nextAddr % 1000);
	      int value = (addrValue / 1000)*1000 + (relativeValue%1000);
	      programList.set(addr, value);
	      return nextAddr % 1000;
	    }
	    
	    if(!symbolList.get(addr % 1000).equals("E") &&  !symbolList.get(addr % 1000).equals("resolved") && !symbolList.get(addr % 1000).equals("chained")){
		      System.out.println("Error: type address on use chain; treated as E type");
		    }	   
	    
	    symbolList.set(addr, "chained");

	    return setRelativeAddr(programList, symbolList, nextAddr % 1000);
	  }

	  
	  // printList - prints a given memory map
	  public static void printMemoryMap(ArrayList finallist){
	    System.out.println("Memory Map");
	    
	    System.out.println("Int\t\t\tValue");
	    int i = 0;
	    while(i<finallist.size()) {
	      System.out.print(i+"\t\t\t");
	      System.out.println(finallist.get(i));
	      i++;
	    }
	   
	  }
	  
	  public static ArrayList createFinalList(ArrayList finalList, ArrayList useList, ArrayList mmconst, ArrayList symbolList) {//ArrayList symbolList, ArrayList useList, ArrayList mmconst, ArrayList finalList){
		    Integer val = 0;
		    Integer listaddr  = 0;
		    int root      = 0;
		    
		    int i = 0;
		    while(i < useList.size()) {
		      val = (Integer) symbolTable.get(useList.get(i));
		      listaddr  = (Integer) useList.get(i+1);
		      listaddr  = setRelativeAddr(mmconst, symbolList, listaddr);

		      if(val != null) {
		        val = (((Integer) mmconst.get(listaddr) / 1000) * 1000) + val;
		      } else {
		        val = (((Integer) mmconst.get(listaddr) / 1000) * 1000);
		      }mmconst.set(listaddr, val);
		      i+=2;
		    }
		    
		    int m = 0;
		    while(m<symbolList.size()) {
		      if(symbolList.get(m).equals("chained")){
		    	int addrValue = (Integer) mmconst.get(m);
			    int relativeValue = (Integer) mmconst.get(listaddr);
		        listaddr = setRelativeAddr(mmconst,symbolList, m);
		        val  = (addrValue / 1000)*1000 + (relativeValue%1000);
		        mmconst.set(m, val);
		      } else if(symbolList.get(m).equals("R")){
		        val = ((Integer) mmconst.get(m) % 1000) + (Integer) originalMod.get(moduleCount) + ((Integer) mmconst.get(m)/1000 * 1000);
		        mmconst.set(m, val);
		      }
		      m++;
		    }
		    int z = 0;
		    while(z<symbolList.size()) { 
		      if (symbolList.get(z).equals("E")) {
		        System.out.println("Error"); //e type treated as I type
		      }
		      z++;
		    }

		    // Creates the final list
		    finalList.addAll(mmconst);
		    moduleCount++;
		    return finalList;
		  }

}