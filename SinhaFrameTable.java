import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class SinhaFrameTable 
{
	SinhaPage[] ft;
	int[] frameindices;
	
	ArrayList<SinhaPage> pagerefs;
	
	Stack<SinhaPage> stack;
	
	private int largestfreeframe; //index of largest free frame in ft
	
	
	public SinhaFrameTable(int fsize)
	{
		this.ft = new SinhaPage[fsize];
		this.frameindices = new int[fsize];
		this.largestfreeframe = fsize-1;
		stack = new Stack<SinhaPage>();
		pagerefs = new ArrayList<SinhaPage>();
	}
	
	public int findProcess(SinhaProcess p)
	{
		for (SinhaPage f : ft)
		{
			if (f.getPageProcess().getID() == p.getID() && f.getPageProcess().currentpage == p.currentpage)
			{
				return 1;
			}
		}
		return -1;
	}
	
	public void setLargestFreeFrame()
	{
		int temp = -1;
		for (int i = ft.length-1; i >= 0; i--)
		{
			//if there is no page in the frame then the largest free frame is at this index
			if (ft[i] == null)
			{
				temp = i;
				break;
				//return i;
			}
			
		}
		//there are no free frames, so we set the largest index to be -1
		this.largestfreeframe = temp;
		//return -1;
	}
	
	public int getLargestFreeFrame()
	{
		return this.largestfreeframe;
	}
	
	public int lru_replace()
	{
		SinhaPage p = pagerefs.get(0);
		int answer = p.getFrameFromPage();
		pagerefs.remove(p);
		return answer;
		
	}
	
	public int lifo_replace()
	{
		return stack.pop().getFrameFromPage();
	}
	
	public int random_replace(Scanner s) throws FileNotFoundException
	{	
		//File f = new File("random-numbers.txt");
		//Scanner s = new Scanner(f);
		int random = s.nextInt();
		System.out.println("random is " + random);
		return random % ft.length; //returns the index of the randomly picked frame
	}
}
