import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class SinhaFrameTable 
{
	SinhaPage[] ft; //the list of pages currently in the frame table
	
	HashMap<Integer, SinhaPage> hash = new HashMap<Integer, SinhaPage>(); 
	
	ArrayList<SinhaPage> pagerefs; //list used for LRU
	
	Stack<SinhaPage> stack; //stack used for LIFO
	
	int largestfreeframe; //index of largest free frame in frame table 
	
	
	public SinhaFrameTable(int fsize)
	{
		for (int i=0; i <fsize; i++)
		{
			hash.put(i, null);
		}
		
		this.ft = new SinhaPage[fsize];
		this.largestfreeframe = fsize-1;
		stack = new Stack<SinhaPage>();
		pagerefs = new ArrayList<SinhaPage>();
	}
	
	/*
	 * sets the largest free frame
	 * loop from the end of the frame table and if there is nothing in the frame, set the largest free frame to that index
	 * if there are no free frames, set the largest to -1;
	 */
	public void setLargestFreeFrame()
	{
		int temp = -1;
		for (int i = ft.length-1; i >= 0; i--)
		{
			//if there is no page in the frame then the largest free frame is at this index
			//if (ft[i] == null)
			if (hash.get(i) == null)
			{
				temp = i;
				break;
				//return i;
			}
			
		}
		//there are no free frames, so we set the largest index to be -1
		this.largestfreeframe = temp;
		
	}
	
	/*
	 * returns largest free frame
	 */
	public int getLargestFreeFrame()
	{
		return this.largestfreeframe;
	}
	
	/*
	 * LRU replacement algorithm
	 * this method sorts the pages currently in the frame table by their last reference variable
	 * non evicted pages are not members of this list
	 * returns the frame of the page with the lowest last reference 
	 */
	public int lru_replace(SinhaProcess c)
	{
		
		ArrayList<SinhaPage> temp = new ArrayList<SinhaPage>(); //copy the elements into a temporary arraylist
		for (int i : hash.keySet())
		{
			temp.add(hash.get(i));
		}
		
		Collections.sort(temp); //sort the elements by their last reference variable 
		return temp.get(0).getFrameFromPage(); //first element in sorted list is the least recently used 
	
	}
	
	/*
	 * LIFO replacement algorithm
	 * this method removes the first page in the stack, which references the last inserted page
	 * non evicted pages are not members of this list
	 * returns the frame of the page
	 */
	public int lifo_replace()
	{
		return stack.pop().getFrameFromPage();
	}
	
	/*
	 * random replacement algorithm
	 * this method removes a random page in the list
	 * non evicted pages are not members of this list
	 * returns the frame of the page
	 */
	public int random_replace(Scanner s) throws FileNotFoundException
	{	
		int random = s.nextInt();
		System.out.println("random is " + random);
		return random % ft.length; //returns the index of the randomly picked frame
	}
}
