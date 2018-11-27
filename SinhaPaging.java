import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SinhaPaging {
	public static void main(String[] args) throws FileNotFoundException {
		int machine = 0; // machine size
		int pagesize = 0; // page size
		int process_size = 0; // process size
		int job = 0; // job mix number
		int referencenum = 0; // number of references
		int numOfPages = 0; // number of pages
		int numOfFrames = 0; // number of frames
		String algo = ""; // algorithm used
		if (args[0] == null) {
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
		Scanner scan = new Scanner(f);

		if (job == 1) {
			// create one process according to specs
			SinhaProcess p1 = new SinhaProcess(1, 1, 0, 0, process_size, numOfPages, referencenum);
			p1.fillPageTable();
			processes.add(p1);
		} else if (job == 2) {
			// create four processes according to specs
			for (int i = 0; i < 4; i++) {
				SinhaProcess p2 = new SinhaProcess(i + 1, 1, 0, 0, process_size, numOfPages, referencenum);
				p2.fillPageTable();
				processes.add(p2);
			}
		} else if (job == 3) {
			// create one process according to spec
			SinhaProcess p3 = new SinhaProcess(1, 0, 0, 0, process_size, numOfPages, referencenum);
			p3.fillPageTable();
			processes.add(p3);

		} else if (job == 4) {
			// create four processes according to spec
			SinhaProcess p4 = new SinhaProcess(1, .75, .25, 0, process_size, numOfPages, referencenum);
			SinhaProcess p5 = new SinhaProcess(2, .75, 0, .25, process_size, numOfPages, referencenum);
			SinhaProcess p6 = new SinhaProcess(3, .75, .125, .125, process_size, numOfPages, referencenum);
			SinhaProcess p7 = new SinhaProcess(4, .5, .125, .125, process_size, numOfPages, referencenum);
			processes.add(p4);
			processes.add(p5);
			processes.add(p6);
			processes.add(p7);
			for (SinhaProcess process : processes) {
				process.fillPageTable();
			}
		}

		SinhaFrameTable frametable = new SinhaFrameTable(numOfFrames); // create new frame table
		roundRobin(processes, frametable, algo, scan); // run round robin for memory references

		//PRINT RESULTS HERE 
		//START
		int totalfaults = 0;
		int totalresidence = 0;
		System.out.println("The machine size is " + machine);
		System.out.println("The page size is " + pagesize);
		System.out.println("The process size is " + process_size);
		System.out.println("The job mix number is " + job);
		System.out.println("The number of references per process is " + referencenum);
		System.out.println("The replacement algorithm is " + algo);
		System.out.println("The level of debugging output is " + args[6]);
		System.out.println();
		
		for (SinhaProcess p : processes) 
		{
			for (SinhaPage page : p.pagetable)
			{
				p.evictions += page.numevictions;
				//p.residence += page.pageresidence;
			}
			//int avgres_process = p.residence / p.evictions;
			int avgres_process = 0;
			System.out.print("Process " + p.getID() + " had " + p.faults + " faults and ");
			System.out.println(avgres_process + " average residency.");
			totalfaults += p.faults;
			totalresidence += p.residence;
		}
		
		int avgresidence = totalresidence / processes.size();
		System.out.println("The total number of faults is " + totalfaults +
				" and the overall average residency is " + avgresidence + ".");

		//FINISHED PRINTING RESULTS 
	}

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
				// this process's round robin time starts
				for (int ref = 0; ref < quantum; ref++) 
				{
					// current.nextWordToRef(); //get the next word reference
					//if we have not exhausted all the references for this process
					if (current.referencechanging > 0) 
					{
						//current.referencechanging--;
						if (current.currentpage == null || current.currentpage.getFrameFromPage() == -1) // if we have a page fault
						{
							//System.out.println("current page is " + current.currentpage);
							current.isFault = true;
							current.faults++; // increment fault
							frametable.setLargestFreeFrame();
							if (frametable.getLargestFreeFrame() != -1) // if there is still a free frame
							{
								//System.out.println("hola");
								int frametoload = frametable.getLargestFreeFrame();
								//System.out.println(frametoload);
								frametable.ft[frametoload] = current.currentpage;
								frametable.stack.add(current.currentpage); //add the page to the stack. used for LIFO algorithm.
								//System.out.println(current.currentpage);
								current.currentpage.setFrame(frametoload);
								frametable.setLargestFreeFrame();
								current.currentpage.start = currentcycle; //JUST ADDED
								 
							} else 
							{
								// we must do the page replacement algorithm
								if (algo.equals("lru")) 
								{
									frametable.lru_replace();
								} 
								else if (algo.equals("lifo")) 
								{
									int evictindex = frametable.lifo_replace();
									if (frametable.ft[evictindex] != null) 
									{
										//current.evictions++;
										SinhaPage toevict = frametable.ft[evictindex];
										//System.out.println("to evict: " + toevict + " current: " + current.currentpage);
										toevict.numevictions++;
										toevict.evict = currentcycle; //JUST ADDED
										toevict.pageresidence = toevict.evict - toevict.start; //JUST ADDED
										current.residence += toevict.pageresidence; //JUST ADDED
										frametable.stack.add(current.currentpage);
										toevict.setFrame(-1);
										frametable.ft[evictindex] = current.currentpage;
										
										current.currentpage.setFrame(evictindex);
										current.currentpage.start = currentcycle; //JUST ADDED
										
									}

								} 
								else if (algo.equals("random")) 
								{
									// System.out.println("hi");
									int evictindex = frametable.random_replace(scan);
									if (frametable.ft[evictindex] != null) 
									{
										//current.evictions++;
										SinhaPage toevict = frametable.ft[evictindex];
										//System.out.println("to evict: " + toevict + " current: " + current.currentpage);
										toevict.numevictions++;
										toevict.evict = currentcycle; //JUST ADDED
										toevict.pageresidence = toevict.evict - toevict.start; //JUST ADDED
										current.residence += toevict.pageresidence; //JUST ADDED
										frametable.stack.add(current.currentpage);
										toevict.setFrame(-1);
										frametable.ft[evictindex] = current.currentpage;
										
										current.currentpage.setFrame(evictindex);
										current.currentpage.start = currentcycle; //JUST ADDED
										
									}
								}
							}
							//current.referencechanging--;
						} else {
							// we have a hit
							//current.referencechanging--;
						}
						System.out.println(current.getID() + " references word " + current.word + " at cycle " + currentcycle + 
								" is fault? " + current.isFault);
						current.referencechanging--; //we have completed one reference
						current.nextWordToRef(scan); 
						current.isFault = false;
						currentcycle++; //increment the cycle
						//current.nextWordToRef(scan); //get the next reference for this process
						//System.out.println(current.referencechanging);
					}
					
				}
				if (current.referencechanging > 0)
				{
					current.isFault = false;
					//temp.remove(current);
					temp.add(current);
					//temp.remove(current);
				}
				
				//temp.remove(current);
				
				//temp.remove(current);
				
			}
			temp.remove(current);
			
		}
	}
}
