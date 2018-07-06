package DiDiDaRen;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

public class myPoint {
	public myPoint(int x, int y, int connection){
		this.x = x;//lineNum
		this.y = y;
		this.connection = connection;
	}
	private int x;
	private int y;
	private int connection;
	private boolean U;
	private boolean D;
	private boolean L;
	private boolean R;
	/////////////////////////////////////////////
	public myPoint() {
	}
	public myPoint Parent;
	public LinkedList<myPoint> Child;
	public int dis;
	//////////////////////////////////////////////
	public void setD(boolean d) {
		D = d;
	}
	public void setL(boolean l) {
		L = l;
	}
	public void setR(boolean r) {
		R = r;
	}
	public void setU(boolean u) {
		U = u;
	}
	public void setConnection(int connection) {
		this.connection = connection;
	}
	public myPoint getNextPos(){
		Random r = new Random();
		int i=0;
		LinkedList<myPoint> tmp = new LinkedList<myPoint>();
		if(this.D){
			i++;
			tmp.add(new myPoint(this.x+1, this.y, 0));
		}
		if(this.L){
			i++;
			tmp.add(new myPoint(this.x, this.y-1, 0));
		}
		if(this.R){
			i++;
			tmp.add(new myPoint(this.x, this.y+1, 0));
		}
		if(this.U){
			i++;
			tmp.add(new myPoint(this.x-1, this.y, 0));
		}
		int j = r.nextInt(i);
		return tmp.get(j);
	}
	public LinkedList<myPoint> getChild(){
		LinkedList<myPoint> tmp = new LinkedList<myPoint>();
		if(this.D){tmp.add(new myPoint(this.x+1, this.y, 0));
		}
		if(this.L){ tmp.add(new myPoint(this.x, this.y-1, 0));
		}
		if(this.R){ tmp.add(new myPoint(this.x, this.y+1, 0));
		}
		if(this.U){ tmp.add(new myPoint(this.x-1, this.y, 0));
		}
		return tmp;
	}
	@Override
	public boolean equals(Object arg0) {
		myPoint t = (myPoint) arg0;
		return this.x==t.x&&this.y==t.y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getConnection() {
		return connection;
	}
	public Point toPoint() {
		return new Point(this.x, this.y);
	}
}
