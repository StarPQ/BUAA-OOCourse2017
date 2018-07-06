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
	//Overview: this is a point class that records the position(x, y), the connection(to gui.Map), the Stream Value.
	//			And in this class taxi may get a random point to move to. in some condition it is used same as java.awt.point
	//不变式：this.x>0&&this.x<80&&this.y>0&&this.y<80&&this.OLDstreamValueU>=1000&&this.OLDstreamValueD>=1000&&this.OLDstreamValueL>=1000&&this.OLDstreamValueR>=1000&&this.streamValueU>=1000&&this.streamValueD>=1000&&this.streamValueL >=1000&&this.streamValueR>=1000;
	public boolean repOK() {
		if(this.x<0||this.x>80||this.y<0||this.y>80) return false;
		if(this.streamValueD.get()<1000||this.streamValueU.get()<1000||this.streamValueL.get()<1000||this.streamValueR.get()<1000
				/*||this.OLDstreamValueD.get()<1000||this.OLDstreamValueU.get()<1000||this.OLDstreamValueL.get()<1000||this.OLDstreamValueR.get()<1000*/)return false;
		return true;
	}
	public myPoint(int x, int y, int connection){
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this
		@ EFFECTS: 	\this.x = x;//lineNum
					\this.y = y;
					\this.connection = connection;
					\this.OLDstreamValueU = new AtomicInteger(1000);
					\this.OLDstreamValueD = new AtomicInteger(1000);
					\this.OLDstreamValueL = new AtomicInteger(1000);
					\this.OLDstreamValueR = new AtomicInteger(1000);
					\this.streamValueU = new AtomicInteger(1000);
					\this.streamValueD = new AtomicInteger(1000);
					\this.streamValueL = new AtomicInteger(1000);
					\this.streamValueR = new AtomicInteger(1000);
					初始化赋值
		*/
		this.x = x;//lineNum
		this.y = y;
		this.connection = connection;
		/*
		this.OLDstreamValueU = new AtomicInteger(1000);
		this.OLDstreamValueD = new AtomicInteger(1000);
		this.OLDstreamValueL = new AtomicInteger(1000);
		this.OLDstreamValueR = new AtomicInteger(1000);
		*/
		this.streamValueU = new AtomicInteger(1000);
		this.streamValueD = new AtomicInteger(1000);
		this.streamValueL = new AtomicInteger(1000);
		this.streamValueR = new AtomicInteger(1000);
	}
	private int x;
	private int y;
	private int connection;
	private boolean OldU;
	private boolean OldD;
	private boolean OldL;
	private boolean OldR;
	private boolean U;
	private boolean D;
	private boolean L;
	private boolean R;
	private AtomicInteger streamValueU;
	private AtomicInteger streamValueD;
	private AtomicInteger streamValueL;
	private AtomicInteger streamValueR;
	/*private AtomicInteger OLDstreamValueU;
	private AtomicInteger OLDstreamValueD;
	private AtomicInteger OLDstreamValueL;
	private AtomicInteger OLDstreamValueR;*/
	/////////////////////////////////////////////
	public myPoint() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: None建立默认实例
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
		@ EFFECTS: \this.D = d	设置该点对应方向是否有道路
		*/
		D = d;
		OldD = d;
	}
	public void setL(boolean l) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this.L
		@ EFFECTS: \this.L=l	设置该点对应方向是否有道路
		*/
		L = l;
		OldL = l;
	}
	public void setR(boolean r) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this.R
		@ EFFECTS: \this.R = r	设置该点对应方向是否有道路
		*/
		R = r;
		OldR = r;
	}
	public void setU(boolean u) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this.U
		@ EFFECTS: \this.U = u设置该点对应方向是否有道路
		*/
		U  = u;
		OldU  = u;
	}
	public void setConnection(int connection) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: \this.connection
		@ EFFECTS: \this.connection = connection	记录该点地图文件上的值
		*/
		this.connection = connection;
	}
	public myPoint getNextPos(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: \result this.map.getNextPos(Pos); random point that leads to minimal stream value
					返回流量最小的随机邻接点
		*/
		Random r = new Random();
		int i=0;
		int minStreamValue = 9999;
		LinkedList<myPoint> tmp = new LinkedList<myPoint>();
		if(this.D && this.streamValueD.get()<=minStreamValue){
			minStreamValue = this.streamValueD.get();
			i++;
			tmp.add(new myPoint(this.x+1, this.y, 0));
		}
		if(this.L && this.streamValueL.get()<=minStreamValue){
			if(this.streamValueL.get()<minStreamValue){
				minStreamValue = this.streamValueL.get();
				tmp.clear();
				i=0;
			}
			i++;
			tmp.add(new myPoint(this.x, this.y-1, 0));
		}
		if(this.R && this.streamValueR.get()<=minStreamValue){
			if(this.streamValueR.get()<minStreamValue){
				minStreamValue = this.streamValueR.get();
				tmp.clear();
				i=0;
			}
			i++;
			tmp.add(new myPoint(this.x, this.y+1, 0));
		}
		if(this.U && this.streamValueU.get()<=minStreamValue){
			if(this.streamValueU.get()<minStreamValue){
				minStreamValue = this.streamValueU.get();
				tmp.clear();
				i=0;
			}
			i++;
			tmp.add(new myPoint(this.x-1, this.y, 0));
		}
		int j = r.nextInt(i);
		return tmp.get(j);
	}
	public myPoint getNextPosforS(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: \result this.map.getNextPos(Pos); random point that leads to minimal stream value
					返回流量最小的随机邻接点
		*/
		Random r = new Random();
		int i=0;
		int minStreamValue = 9999;
		LinkedList<myPoint> tmp = new LinkedList<myPoint>();
		if(this.OldD && this.streamValueD.get()<=minStreamValue){
			minStreamValue = this.streamValueD.get();
			i++;
			tmp.add(new myPoint(this.x+1, this.y, 0));
		}
		if(this.OldL && this.streamValueL.get()<=minStreamValue){
			if(this.streamValueL.get()<minStreamValue){
				minStreamValue = this.streamValueL.get();
				tmp.clear();
				i=0;
			}
			i++;
			tmp.add(new myPoint(this.x, this.y-1, 0));
		}
		if(this.OldR && this.streamValueR.get()<=minStreamValue){
			if(this.streamValueR.get()<minStreamValue){
				minStreamValue = this.streamValueR.get();
				tmp.clear();
				i=0;
			}
			i++;
			tmp.add(new myPoint(this.x, this.y+1, 0));
		}
		if(this.OldU && this.streamValueU.get()<=minStreamValue){
			if(this.streamValueU.get()<minStreamValue){
				minStreamValue = this.streamValueU.get();
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
	public void StreamValueUP(myPoint to){
		/* 
		@ REQUIRES: ((\this.x==to.x&&abs(\this.y-to.y)==1)||(\this.y==to.y&&abs(\this.x-to.x)==1)) \this，to为邻接点
		@ MODIFIES: Map[\this.x][\this.y], Map[to.x][to.y]中对应点流量
		@ EFFECTS: 	(to.x - this.x > 0 && this.D) ==> this.streamValueD++;
					(to.x - this.x < 0 && this.U) ==> this.streamValueU++;
					(to.y - this.y > 0 && this.R ==> this.streamValueR++;
					(to.y - this.y < 0 && this.L) ==> this.streamValueL++;
		*/
		if(to.x - this.x > 0 && this.D) this.streamValueD.incrementAndGet();
		else if(to.x - this.x < 0 && this.U) this.streamValueU.incrementAndGet();
		else if(to.y - this.y > 0 && this.R) this.streamValueR.incrementAndGet();
		else if(to.y - this.y < 0 && this.L) this.streamValueL.incrementAndGet();
	}
	public void StreamValueDOWN(myPoint to){
		/* 
		@ REQUIRES: ((\this.x==to.x&&abs(\this.y-to.y)==1)||(\this.y==to.y&&abs(\this.x-to.x)==1)) \this，to为邻接点
		@ MODIFIES: Map[\this.x][\this.y], Map[to.x][to.y]中对应点流量
		@ EFFECTS: 	(to.x - this.x > 0 && this.D) ==> this.streamValueD--;
					(to.x - this.x < 0 && this.U) ==> this.streamValueU--;
					(to.y - this.y > 0 && this.R ==> this.streamValueR--;
					(to.y - this.y < 0 && this.L) ==> this.streamValueL--;
		*/
		if(to.x - this.x > 0 && this.D) this.streamValueD.decrementAndGet();
		else if(to.x - this.x < 0 && this.U) this.streamValueU.decrementAndGet();
		else if(to.y - this.y > 0 && this.R) this.streamValueR.decrementAndGet();
		else if(to.y - this.y < 0 && this.L) this.streamValueL.decrementAndGet();
	}
	public int getStreamValue(myPoint to){
		/* 
		@ REQUIRES: (0<to.x<80&&0<to.y<80)&&((\this.x==to.x&&abs(\this.y-to.y)==1)||(\this.y==to.y&&abs(\this.x-to.x)==1)) \this，to为邻接点
					to在地图内,且与当前点邻接
		@ MODIFIES: None
		@ EFFECTS: 	(to.x - this.x > 0) \result = this.streamValueD;
					(to.x - this.x < 0) \result = this.streamValueU;
					(to.y - this.y > 0) \result = this.streamValueR;
					(to.y - this.y < 0) \result = this.streamValueL;
					else {
						System.out.println("NOCONNECTION");
						\result = 10000;
					}
					获取相对应方向前200ms的流量值
		*/
		if(to.x - this.x > 0) return this.streamValueD.get();
		else if(to.x - this.x < 0) return this.streamValueU.get();
		else if(to.y - this.y > 0) return this.streamValueR.get();
		else if(to.y - this.y < 0) return this.streamValueL.get();
		else {
			return 1000;
		}
	}

//////////////////////////////////////////////////////////////
	public void connectChange(myPoint to, int status){
		/* 
		@ REQUIRES: (0<to.x<80&&0<to.y<80)&&((\this.x==to.x&&abs(\this.y-to.y)==1)||(\this.y==to.y&&abs(\this.x-to.x)==1)) \this，to为邻接点
					to在地图内且与当前点邻接
		@ MODIFIES: \this.D \this.U \this.L \this.R
		@ EFFECTS: 	(to.x - \this.x > 0)&&status==1 ==> \this.D = true;
					(to.x - \this.x > 0)&&status==0 ==> \this.D = false;
					(to.x - \this.x < 0)&&status==1 ==> \this.U = true;
					(to.x - \this.x < 0)&&status==0 ==> \this.U = false;
					(to.y - \this.y > 0)&&status==1 ==> \this.R = true;
					(to.y - \this.y > 0)&&status==0 ==> \this.R = false;
					(to.y - \this.y < 0)&&status==1 ==> \this.L = true;
					(to.y - \this.y < 0)&&status==0 ==> \this.L = false;
					将to与当前点之间的路设为
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
		@ REQUIRES: (0<to.x<80&&0<to.y<80)	p在地图内
		@ MODIFIES: None
		@ EFFECTS: this.U==>res.add(mypoint(p.x-1,p.y))
					this.D==>res.add(mypoint(p.x+1,p.y))
					this.R==>res.add(mypoint(p.x,p.y+1))
					this.L==>res.add(mypoint(p.x,p.y-1))
					\result res
					获取邻接点集合
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
	public LinkedList<myPoint> getChildforS(){
		/* 
		@ REQUIRES: (0<to.x<80&&0<to.y<80)	p在地图内
		@ MODIFIES: None
		@ EFFECTS: this.U==>res.add(mypoint(p.x-1,p.y))
					this.D==>res.add(mypoint(p.x+1,p.y))
					this.R==>res.add(mypoint(p.x,p.y+1))
					this.L==>res.add(mypoint(p.x,p.y-1))
					\result res
					获取邻接点集合
		*/
		LinkedList<myPoint> tmp = new LinkedList<myPoint>();
		if(this.OldD){tmp.add(new myPoint(this.x+1, this.y, 0));
		}
		if(this.OldL){ tmp.add(new myPoint(this.x, this.y-1, 0));
		}
		if(this.OldR){ tmp.add(new myPoint(this.x, this.y+1, 0));
		}
		if(this.OldU){ tmp.add(new myPoint(this.x-1, this.y, 0));
		}
		return tmp;
	}
	@Override
	public boolean equals(Object arg0) {
		/* 
		@ REQUIRES: arg0为myPoint类
		@ MODIFIES: None
		@ EFFECTS: 	this.x=arg0.x&&this.y=arg0.y==>\result = true
					else \result = false;
					若x, y分别对应相等则返回true否则返回false
		*/
		myPoint t = (myPoint) arg0;
		return this.x==t.x&&this.y==t.y;
	}
	public int getX() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	\result = this.x
					返回当前点x
		*/
		return x;
	}
	public int getY() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: \result = this.y
					返回当前点y
		*/
		return y;
	}
	public int getConnection() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: \result = this.connection
					返回当前点connction
		*/
		return connection;
	}
	public Point toPoint() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: \result = new Point(this.x, this.y);
					将坐标转化成java.awt.Point类
		*/
		return new Point(this.x, this.y);
	}
}
