import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SinhaPaging 
{
	public static void main(String[] args) throws FileNotFoundException 
	{
		int machine = 0; // machine size
		int pagesize = 0; // page size
		int process_size = 0; // process size
		int job = 0; // job mix number
		int referencenum = 0; // number of references
		int numOfPages = 0; // number of pages
		int numOfFrames = 0; // number of frames
		String algo = ""; // algorithm used
		if (args[0] == null) 
		{
			System.exit(0);
		} else {
			machine = Integer.parseInt(args[0]);
			pagesize = Integer.parseInt(args[1]);
			process_size = Integer.parseInt(args[2]);
			job = Integer.parseInt(args[3]);
			referencenum = Integer.parseInt(args[4]);
			algo = args[5];
			numOfPages = process_size / pagesize;
			numOfFrames = machine / pagesize;
		}

		ArrayList<SinhaProcess> processes = new ArrayList<SinhaProcess>(); // list of processes
		File f = new File("random-numbers.txt"); 
		Scanner scan = new Scanner(f); //read in the random number file

		//go through the possible job mixes and create the processes according to the spec
		if (job == 1) 
		{
			// create one process according to specs
			SinhaProcess p1 = new SinhaProcess(1, 1, 0, 0, process_size, numOfPages, referencenum, pagesize);
			p1.fillPageTable(); 
			processes.add(p1);
		} else if (job == 2) {
			// create four processes according to specs
			for (int i = 0; i < 4; i++) 
			{
				SinhaProcess p2 = new SinhaProcess(i + 1, 1, 0, 0, process_size, numOfPages, referencenum, pagesize);
				p2.fillPageTable();
				processes.add(p2);
			}
		} else if (job == 3) 
		{
			// create four process according to spec
			for (int i = 0; i < 4; i++)
			{
				SinhaProcess p3 = new SinhaProcess(1, 0, 0, 0, process_size, numOfPages, referencenum, pagesize);
				p3.fillPageTable();
				processes.add(p3);
			}

		} else if (job == 4) 
		{
			// create four processes according to spec
			//System.out.println("page size is" + pagesize);
			SinhaProcess p4 = new SinhaProcess(1, .75, .25, 0, process_size, numOfPages, referencenum, pagesize);
			SinhaProcess p5 = new SinhaProcess(2, .75, 0, .25, process_size, numOfPages, referencenum, pagesize);
			SinhaProcess p6 = new SinhaProcess(3, .75, .125, .125, process_size, numOfPages, referencenum, pagesize);
			SinhaProcess p7 = new SinhaProcess(4, .5, .125, .125, process_size, numOfPages, referencenum, pagesize);
			p4.fillPageTable();
			p5.fillPageTable();
			p6.fillPageTable();
			p7.fillPageTable();
			processes.add(p4);
			processes.add(p5);
			processes.add(p6);
			processes.add(p7);
		}

		SinhaFrameTable frametable = new SinhaFrameTable(numOfFrames); // create new frame table
		roundRobin(processes, frametable, algo, scan); // run round robin for memory references

		//PRINT RESULTS HERE 
		//START
		int totalfaults = 0;
		int totalresidence = 0;
		int totalevictions = 0;
		System.out.println("The machine size is " + machine + ".");
		System.out.println("The page size is " + pagesize + ".");
		System.out.println("The process size is " + process_size + ".");
		System.out.println("The job mix number is " + job + ".");
		System.out.println("The number of references per process is " + referencenum + ".");
		System.out.println("The replacement algorithm is " + algo + ".");
		System.out.println("The level of debugging output is " + args[6] + ".");
		System.out.println();
		
		for (SinhaProcess p : processes) 
		{
			for (SinhaPage page : p.pagetable)
			{
				p.evictions += page.numevictions;
				p.residence += page.runningsum;
			}
			
			if (p.evictions != 0)
			{
				double avgres_process = (double) p.residence / (double) p.evictions;
				//int avgres_process = 0;
				if (p.faults == 1)
				{
					System.out.print("Process " + p.getID() + " had " + p.faults + " fault and ");
				}
				else {
					System.out.print("Process " + p.getID() + " had " + p.faults + " faults and ");
				}
				
				System.out.println(avgres_process + " average residency.");
			}
			else {
				if (p.faults == 1)
				{
					System.out.println("Process " + p.getID() + " had " + p.faults + " fault.");
				}
				else {
					System.out.println("Process " + p.getID() + " had " + p.faults + " faults.");
				}
				
				System.out.println("\tWith no evictions, the average residence is undefined.");
			}
			
			totalfaults += p.faults;
			totalresidence += p.residence;
			totalevictions += p.evictions;
		}
		
		if (totalevictions != 0)
		{
			double avgresidence = (double) totalresidence / (double) totalevictions;
			System.out.println("The total number of faults is " + totalfaults +
					" and the overall average residence is " + avgresidence + ".");
		}
		else {
			System.out.println("The total number of faults is " + totalfaults + ".");
			System.out.println("\tWith no evictions, the overall average is undefined.");
		}
		
		

		//FINISHED PRINTING RESULTS 
	}

	/*
	 * this method runs the main part of the program and gives each process 3 turns to simulate memory references
	 */
	public static void roundRobin(ArrayList<SinhaProcess> processes, SinhaFrameTable frametable, String algo, Scanner scan) throws FileNotFoundException 
	{
		
		ArrayList<SinhaProcess> temp = new ArrayList<SinhaProcess>(); //create a temporary list of processes 
		//copy in the processes 
		for (SinhaProcess p : processes) 
		{
			temp.add(p);
		}
		int quantum = 3;
		int currentcycle = 1;
		SinhaProcess current = temp.get(0);
		while (temp.size() > 0) //if there are still processes 
		{
			for (int i = 0; i < temp.size(); i++) 
			{
				current = temp.get(i); // get the current process
				
				//frametable.pagerefs.add(current.currentpage);
				// this process's round robin time starts
				for (int ref = 0; ref < quantum; ref++) 
				{
					
					//System.out.println("Process " + current.getID() + " word " + current.word);
					//if we have not exhausted all the references for this process
					if (current.referencechanging > 0) 
					{
						
						current.currentpage.lastref = currentcycle;
						
						//current.referencechanging--;
						
						//frametable.setLargestFreeFrame();
						if (current.currentpage.getFrameFromPage() == -1) // if we have a page fault
						{
							
							//System.out.println("current page is " + current.currentpage);
							current.isFault = true;
							current.faults++; // increment fault
							frametable.setLargestFreeFrame();
							
							if (frametable.getLargestFreeFrame() != -1) // if there is still a free frame
							{
								System.out.println("Free frame: " + frametable.getLargestFreeFrame());
								int frametoload = frametable.getLargestFreeFrame();
								frametable.ft[frametoload] = current.currentpage;
								
								System.out.println("using free frame " + frametable.getLargestFreeFrame());
								
								
								frametable.stack.add(current.currentpage); //add the page to the stack. used for LIFO algorithm.
								//frametable.pagerefs.add(current.currentpage); //add the page to the list used for LRU algorithm
								current.lrulist.add(current.currentpage);
								
								current.currentpage.setFrame(frametoload);
								frametable.setLargestFreeFrame();
								//System.out.println("Largets free frame is now: " + frametable.getLargestFreeFrame());
								current.currentpage.start = currentcycle; 
								
								frametable.hash.put(frametoload, current.currentpage); //JUST ADDED 
								//frametable.largestfreeframe =-1;
								 
							} else 
							{
								
								// we must do the page replacement algorithm
								if (algo.equals("lru")) 
								{
									
									int evictindex = frametable.lru_replace(current);
									System.out.println("evicting frame  " + evictindex);
									if (frametable.hash.get(evictindex) != null)
									{
										SinhaPage toevict = frametable.hash.get(evictindex);
										
										toevict.numevictions++; //increment number of evictions for this page
										toevict.evict = currentcycle; //set the evict time to this cycle
										toevict.pageresidence = toevict.evict - toevict.start; //calculate this page's residence time
										toevict.runningsum += toevict.pageresidence;
										toevict.setFrame(-1); //set the evicted page's frame to -1 because it is evicted
										
										frametable.stack.add(current.currentpage); //add the process's current page to the stack for LIFO
										//frametable.pagerefs.add(toevict);
										
									
										//frametable.pagerefs.add(current.currentpage); //add the process's current page to the list for LRU
										
										frametable.ft[evictindex] = current.currentpage; //place the current page into the now empty page frame
										
										
										
										current.currentpage.setFrame(evictindex); //set the frame to the new index
										current.currentpage.start = currentcycle; //start this page's time
										frametable.hash.put(evictindex, current.currentpage); //JUST ADDED 
										//frametable.pagerefs.clear();
									}
									
									//frametable.pagerefs.clear();
									//frametable.pagerefs.add(current.currentpage);
									
								} 
								else if (algo.equals("lifo")) 
								{
									int evictindex = frametable.lifo_replace();
									if (frametable.ft[evictindex] != null) 
									{
										//current.evictions++;
										//SinhaPage toevict = frametable.ft[evictindex]; //get the page to evict COMMENTED OUT
										
										SinhaPage toevict = frametable.hash.get(evictindex); //JUST ADDED 
										
										//System.out.println("to evict: " + toevict + " current: " + current.currentpage);
										toevict.numevictions++; //increment number of evictions for this page
										toevict.evict = currentcycle; //set the evict time to this cycle
										toevict.pageresidence = toevict.evict - toevict.start; //calculate this page's residence time
										toevict.runningsum += toevict.pageresidence; 
										//current.residence += toevict.pageresidence; //add this page's residence time to the process residence time
										frametable.stack.add(current.currentpage); //add the process's current page to the stack for LIFO
										toevict.setFrame(-1); //set the evicted page's frame to -1 because it is evicted
										frametable.ft[evictindex] = current.currentpage; //place the current page into the now empty page frame
										
										frametable.hash.put(evictindex, current.currentpage);
										
										current.currentpage.setFrame(evictindex); //set the frame to the new index
										current.currentpage.start = currentcycle; //start this page's time
										
										//frametable.pagerefs.clear();
									}
									//frametable.pagerefs.clear();

								} 
								else if (algo.equals("random")) 
								{
									// System.out.println("hi");
									int evictindex = frametable.random_replace(scan);
									
									//if (frametable.ft[evictindex] != null) 
									if (frametable.hash.get(evictindex) != null)
									{
										//current.evictions++;
										//SinhaPage toevict = frametable.ft[evictindex]; COMMENTED OUT
										SinhaPage toevict = frametable.hash.get(evictindex);
										//System.out.println("to evict: " + toevict + " current: " + current.currentpage);
										toevict.numevictions++;
										toevict.evict = currentcycle; //JUST ADDED
										toevict.pageresidence = toevict.evict - toevict.start; //JUST ADDED
										toevict.runningsum += toevict.pageresidence; 
										//current.residence += toevict.pageresidence; //JUST ADDED
										frametable.stack.add(current.currentpage);
										toevict.setFrame(-1);
										frametable.ft[evictindex] = current.currentpage; 
										
										frametable.hash.put(evictindex, current.currentpage);
										
										current.currentpage.setFrame(evictindex);
										current.currentpage.start = currentcycle; //JUST ADDED
										
									}
								}
							}
						} else {
							
						}
						
						System.out.println(current.getID() + " references word " + current.word + " at cycle " + currentcycle + 
								" is fault? " + current.isFault + " page: " + current.currentpagenum); 
						
						
						current.referencechanging--; //we have completed one reference
						int r = scan.nextInt();
						System.out.println(current.getID() + " uses random number " + r);
						current.nextWordToRef(scan, r); //get the next word to reference 
						current.isFault = false; //reset the isFault boolean to false
						//frametable.pagerefs.add(current.currentpage);
						currentcycle++; //increment the cycle
						
						
						//current.nextWordToRef(scan); //get the next reference for this process
						//System.out.println(current.referencechanging);
					}
					//frametable.pagerefs.clear();
					
				}
				
				if (current.referencechanging > 0)
				{
					current.isFault = false;
					//temp.remove(current);
					temp.add(current);
					
					//frametable.pagerefs.clear();
					//temp.remove(current);
				}
				
				//frametable.pagerefs.clear();
				//temp.remove(current);
				
				//temp.remove(current);
				
			}
			temp.remove(current);
			
		}
	}
}
