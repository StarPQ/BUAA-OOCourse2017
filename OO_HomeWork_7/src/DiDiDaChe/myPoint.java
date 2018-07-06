package DiDiDaChe;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class myPoint {
	public myPoint(int x, int y, int connection){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 初始化赋值
		*/
		this.x = x;//lineNum
		this.y = y;
		this.connection = connection;
		this.OLDstreamValueU = new AtomicInteger(1000);
		this.OLDstreamValueD = new AtomicInteger(1000);
		this.OLDstreamValueL = new AtomicInteger(1000);
		this.OLDstreamValueR = new AtomicInteger(1000);
		this.streamValueU = new AtomicInteger(1000);
		this.streamValueD = new AtomicInteger(1000);
		this.streamValueL = new AtomicInteger(1000);
		this.streamValueR = new AtomicInteger(1000);
	}
	private int x;
	private int y;
	private int connection;
	private boolean U;
	private boolean D;
	private boolean L;
	private boolean R;
	private AtomicInteger streamValueU;
	private AtomicInteger streamValueD;
	private AtomicInteger streamValueL;
	private AtomicInteger streamValueR;
	private AtomicInteger OLDstreamValueU;
	private AtomicInteger OLDstreamValueD;
	private AtomicInteger OLDstreamValueL;
	private AtomicInteger OLDstreamValueR;
	/////////////////////////////////////////////
	public myPoint() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 建立默认实例
		*/
	}
	public myPoint Parent;
	public LinkedList<myPoint> Child;
	public int dis;
	//////////////////////////////////////////////
	public void setD(boolean d) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this.D
		@ EFFECTS: 设置该点对应方向是否有道路
		*/
		D = d;
	}
	public void setL(boolean l) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this.L
		@ EFFECTS: 设置该点对应方向是否有道路
		*/
		L = l;
	}
	public void setR(boolean r) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this.R
		@ EFFECTS: 设置该点对应方向是否有道路
		*/
		R = r;
	}
	public void setU(boolean u) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this.U
		@ EFFECTS: 设置该点对应方向是否有道路
		*/
		U  = u;
	}
	public void setConnection(int connection) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this.connection
		@ EFFECTS: 记录该点地图文件上的值
		*/
		this.connection = connection;
	}
	public myPoint getNextPos(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 返回流量最小的随机邻接点
		*/
		Random r = new Random();
		int i=0;
		int minStreamValue = 9999;
		LinkedList<myPoint> tmp = new LinkedList<myPoint>();
		if(this.D && this.OLDstreamValueD.get()<=minStreamValue){
			minStreamValue = this.OLDstreamValueD.get();
			i++;
			tmp.add(new myPoint(this.x+1, this.y, 0));
		}
		if(this.L && this.OLDstreamValueL.get()<=minStreamValue){
			if(this.OLDstreamValueL.get()<minStreamValue){
				minStreamValue = this.OLDstreamValueL.get();
				tmp.clear();
				i=0;
			}
			i++;
			tmp.add(new myPoint(this.x, this.y-1, 0));
		}
		if(this.R && this.OLDstreamValueR.get()<=minStreamValue){
			if(this.OLDstreamValueR.get()<minStreamValue){
				minStreamValue = this.OLDstreamValueR.get();
				tmp.clear();
				i=0;
			}
			i++;
			tmp.add(new myPoint(this.x, this.y+1, 0));
		}
		if(this.U && this.OLDstreamValueU.get()<=minStreamValue){
			if(this.OLDstreamValueU.get()<minStreamValue){
				minStreamValue = this.OLDstreamValueU.get();
				tmp.clear();
				i=0;
			}
			i++;
			tmp.add(new myPoint(this.x-1, this.y, 0));
		}
		int j = r.nextInt(i);
		return tmp.get(j);
	}
//////////////////////////////////////////////////////////////
	public void ChangeStreamValue(myPoint to){
		/* 
		@ REQUIRES: to在地图内
		@ MODIFIES: 流量值
		@ EFFECTS: 更改对应方向上的流量值
		*/
		if(to.x - this.x > 0) this.streamValueD.incrementAndGet();
		else if(to.x - this.x < 0) this.streamValueU.incrementAndGet();
		else if(to.y - this.y > 0) this.streamValueR.incrementAndGet();
		else if(to.y - this.y < 0) this.streamValueL.incrementAndGet();
	}
	public int getStreamValue(myPoint to){
		/* 
		@ REQUIRES: to在地图内,且与当前点邻接
		@ MODIFIES: None
		@ EFFECTS: 获取相对应方向前200ms的流量值
		*/
		if(to.x - this.x > 0) return this.OLDstreamValueD.get();
		else if(to.x - this.x < 0) return this.OLDstreamValueU.get();
		else if(to.y - this.y > 0) return this.OLDstreamValueR.get();
		else if(to.y - this.y < 0) return this.OLDstreamValueL.get();
		else {
			System.out.println("NOCONNECTION");
			return 10000;
		}
	}
	public void refresh(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: streamValue,OLDstreamValue
		@ EFFECTS: 保存当前流量值到OLD，刷新地图为1000
		*/
		this.OLDstreamValueU.set(this.streamValueU.get());
		this.OLDstreamValueD.set(this.streamValueD.get());
		this.OLDstreamValueL.set(this.streamValueL.get());
		this.OLDstreamValueR.set(this.streamValueR.get());
		this.streamValueD.set(1000);
		this.streamValueL.set(1000);
		this.streamValueR.set(1000);
		this.streamValueU.set(1000);
	}
//////////////////////////////////////////////////////////////
	public void connectChange(myPoint to, int status){
		/* 
		@ REQUIRES: to在地图内且与当前点邻接
		@ MODIFIES: \this.D \this.U \this.L \this.R
		@ EFFECTS: 将to与当前点之间的路设为
					若status为1则设为有路；否则设为没有路
		*/
		if(to.x - this.x > 0){
			this.D = status==1?true:false;
		}
		else if(to.x - this.x < 0){
			this.U = status==1?true:false;
		}
		else if(to.y - this.y > 0){
			this.R = status==1?true:false;
		}
		else if(to.y - this.y < 0){
			this.L = status==1?true:false;
		}
	}
//////////////////////////////////////////////////////////////
	public LinkedList<myPoint> getChild(){
		/* 
		@ REQUIRES: p在地图内
		@ MODIFIES: None
		@ EFFECTS: 获取邻接点集合
		*/
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
		/* 
		@ REQUIRES: arg0为myPoint类
		@ MODIFIES: None
		@ EFFECTS: 若x, y分别对应相等则返回true否则返回false
		*/
		myPoint t = (myPoint) arg0;
		return this.x==t.x&&this.y==t.y;
	}
	public int getX() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 返回当前点x
		*/
		return x;
	}
	public int getY() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 返回当前点y
		*/
		return y;
	}
	public int getConnection() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 返回当前点connction
		*/
		return connection;
	}
	public Point toPoint() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 将坐标转化成java.awt.Point类
		*/
		return new Point(this.x, this.y);
	}
}
