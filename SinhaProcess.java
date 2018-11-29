import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SinhaProcess 
{
	private int id; //process number
	private double A; 
	private double B; 
	private double C; 
	int referencenum; //number of references 
	int word; //the current word the process is referencing
	int size; //size of the process
	int numOfPages; //number of pages in process
	int residence; //total residence time for process (sum of all page residences)
	int evictions; //total number of evictions for process (sum of all page evictions)
	SinhaPage currentpage; //current page
	int currentpagenum; //the number of the current page
	int faults; //number of faults for process
	int referencechanging; //modifiable number of references that gets decremented at every reference
	boolean isFault; //indicates if process currently has page fault with the current page
	
	int size_of_page; //size of the pages
	
	ArrayList<SinhaPage> pagetable; //list of all the pages
	private double y; //used in the method for determining the next reference
	
	/*
	 * initializes a new process
	 * @param id the id of the process
	 * @param a A
	 * @param b B
	 * @param c C
	 * @param size the size of the process
	 * @param numOfPages # of pages in process
	 * @param referencenum # of references per process
	 * @param size_of_page the size of the page
	 */
	public SinhaProcess(int id, double a, double b, double c, int size, int numOfPages, int referencenum, int size_of_page) throws FileNotFoundException
	{
		this.id = id;
		this.A = a;
		this.B = b;
		this.C = c;
		this.size = size;
		this.numOfPages = numOfPages;
		this.referencenum = referencenum;
		this.word = (111*id + this.size) % this.size;
		this.residence =0;
		this.evictions = 0;
		this.currentpagenum = this.word / this.size;
		this.pagetable = new ArrayList<SinhaPage>();
		this.currentpage = null;
		referencechanging = referencenum;
		this.isFault = false;
		
		this.size_of_page = size_of_page;
		//nextWordToRef();
		//this.currentpage = this.word / this.size;
	}
	
	/*
	 * fills the page list with the appropriate number of pages
	 */
	public void fillPageTable(Scanner scan) throws FileNotFoundException
	{
		for (int i = 0; i < numOfPages; i++)
		{
			SinhaPage newpage = new SinhaPage(i, this);
			this.pagetable.add(newpage);
		}
		this.currentpagenum = this.word / this.size_of_page;
		this.currentpage = this.pagetable.get(currentpagenum);
		//nextWordToRef(scan);
	}
	
	/*
	 * returns the current page 
	 */
	public SinhaPage getCurrPage()
	{
		this.currentpagenum = this.word / this.size;
		this.currentpage = this.pagetable.get(currentpagenum);
		return this.currentpage;
	}
	
	/*
	 * returns process ID
	 */
	public int getID()
	{
		return this.id;
	}
	
	/*
	 * returns A
	 */
	public double getA()
	{
		return this.A;
	}
	
	/*
	 * returns B
	 */
	public double getB()
	{
		return this.B;
	}
	
	/*
	 * returns C
	 */
	public double getC()
	{
		return this.C;
	}
	
	/*
	 * returns the current word 
	 */
	public int getRef()
	{
		return this.word;
	}
	
	/*
	 * calculates the next word to reference according to algorithm provided by the spec
	 * sets the next page number and the next page as well
	 */
	public void nextWordToRef(Scanner scan1) throws FileNotFoundException
	{
		//File f = new File("random-numbers.txt");
		//Scanner scan1 = new Scanner(f);
		//String rstr = scan1.next();
		//System.out.println(rstr);
		int r = scan1.nextInt();
		y = r / (Integer.MAX_VALUE + 1d);
		if (y < this.A)
		{
			this.word = (this.word + 1 + this.size) % this.size;
		}
		else if (y < this.A + this.B)
		{
			this.word = (this.word - 5 + this.size) % this.size;
		}
		else if (y < this.A + this.B + this.C)
		{
			this.word = (this.word + 4 + this.size) % this.size;
		}
		else 
		{
			int new_random = scan1.nextInt();
			this.word = new_random % this.size;
		}
		
		this.currentpagenum = this.word / this.size_of_page;
		this.currentpage = this.pagetable.get(currentpagenum);
	}
}
