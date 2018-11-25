import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
public class SinhaProcess 
{
	private int id;
	private double A; 
	private double B; 
	private double C; 
	int referencenum;
	int word; 
	int size; 
	int numOfPages; 
	int residence;
	int evicted;
	SinhaPage currentpage; 
	int currentpagenum;
	int faults;
	int referencechanging;
	boolean isFault; 
	
	ArrayList<SinhaPage> pagetable;
	private double y;
	
	public SinhaProcess(int id, double a, double b, double c, int size, int numOfPages, int referencenum) throws FileNotFoundException
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
		this.evicted = 0;
		this.currentpagenum = this.word / this.size;
		this.pagetable = new ArrayList<SinhaPage>();
		this.currentpage = null;
		referencechanging = referencenum;
		this.isFault = false;
		//nextWordToRef();
		//this.currentpage = this.word / this.size;
	}
	
	public void fillPageTable()
	{
		for (int i = 0; i < numOfPages; i++)
		{
			SinhaPage newpage = new SinhaPage(i, this);
			this.pagetable.add(newpage);
		}
		this.currentpage = pagetable.get(0);
	}
	
	public SinhaPage getCurrPage()
	{
		this.currentpagenum = this.word / this.size;
		this.currentpage = this.pagetable.get(currentpagenum);
		return this.currentpage;
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public double getA()
	{
		return this.A;
	}
	
	public double getB()
	{
		return this.B;
	}
	
	public double getC()
	{
		return this.C;
	}
	
	public int getRef()
	{
		return this.word;
	}
	
	public void nextWordToRef(Scanner scan1) throws FileNotFoundException
	{
		//File f = new File("random-numbers.txt");
		//Scanner scan1 = new Scanner(f);
		String rstr = scan1.next();
		//System.out.println(rstr);
		int r = Integer.parseInt(rstr);
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
		
		this.currentpagenum = this.word / this.size;
		this.currentpage = this.pagetable.get(currentpagenum);
	}
}
