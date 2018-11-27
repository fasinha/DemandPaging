
public class SinhaPage 
{
	private int num;
	private int frame;
	private SinhaProcess p;
	private int lastref;
	int start;
	int evict; 
	int pageresidence;
	int numevictions;
	
	public SinhaPage(int num, SinhaProcess p)
	{
		this.num = num;
		this.p = p;
		this.frame = -1;
		start = 0;
		evict = 0;
		pageresidence = 0;
		numevictions = 0;
	}
	
	public void setFrame(int f)
	{
		this.frame = f;
	}
	
	public SinhaProcess getPageProcess()
	{
		return this.p;
	}
	
	public int getFrameFromPage()
	{
		return this.frame;
	}
	
	public int getLastRef()
	{
		return this.lastref;
	}
	
	public void setLastRef(int ref)
	{
		this.lastref = ref;
	}
}
