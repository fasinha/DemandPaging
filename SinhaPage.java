
public class SinhaPage implements Comparable<SinhaPage>
{
	private int num; //page number
	private int frame; //frame the page is currently in
	private SinhaProcess p; //process the page corresponds to 
	int lastref; //the last reference for this page
	int start; //time page is loaded in frame
	int evict; //time page is evicted from frame
	int pageresidence; //residence time for this page
	int numevictions; //number of times page was evicted
	int runningsum; //running sum used for output calculation
	
	//private SinhaFrame frame = new SinhaFrame(-1); 
	
	/*
	 * instantiates a new page with id number and corresponding process
	 */
	public SinhaPage(int num, SinhaProcess p)
	{
		this.num = num;
		this.p = p;
		this.frame = -1;
		start = 0;
		evict = 0;
		pageresidence = 0;
		numevictions = 0;
		runningsum = 0;
	}
	
	
	/*
	 * places the page into a frame
	 */
	public void setFrame(int f)
	{
		this.frame = f;
	}
	
	/*
	 * returns the page's process
	 */
	public SinhaProcess getPageProcess()
	{
		return this.p;
	}
	
	/*
	 * returns the page's frame
	 */
	public int getFrameFromPage()
	{
		return this.frame;
	}


	@Override
	public int compareTo(SinhaPage comparepage) {
		return this.lastref - comparepage.lastref;
	}
	
}