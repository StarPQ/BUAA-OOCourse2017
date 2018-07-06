package ElevSys;

public interface Lifts {
	public void setFloor(Order tmp);
	public void setFloor(int n);
	public boolean judge(Order tmp);
	public boolean pickup(int n);
	public int getPosition();
	public int getTerminal();
	public long getTime();
	public Order getOatF(int i);
	public int n_pos(long t);
	public void StatusRefreash(int term, long time);
	public String toString();
	public long getArrivalTime();
	public void adjust();
	public boolean isfloor(int n);
}
