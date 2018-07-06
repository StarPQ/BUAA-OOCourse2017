package DiDiDaChe;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Taxi extends Thread{
	public Taxi(JackMa scheduler, Timer T) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: scheduler， r, Position. credit, status, t, ReqQueue, Route, choosed, timer 
		@ EFFECTS: 初始化scheduler， r, Position. credit, status, t, ReqQueue, Route, choosed, timer
		*/
		this.scheduler = scheduler;
		Random r = new  Random();
		this.Position = new myPoint(r.nextInt(80), r.nextInt(80), 0);
		this.credit = 0;
		this.status = 2;
		this.t = 0;
		this.ReqQueue = new LinkedList<myPoint>();
		this.Route = new LinkedBlockingQueue<myPoint>();
		this.choosed = false;
		this.timer = T;
	}
	private myPoint Position;
	private int status;
	private int credit;
	private JackMa scheduler;
	private LinkedList<myPoint> ReqQueue; 
	private LinkedBlockingQueue<myPoint> Route;
	private double t;
	public boolean choosed;
	private Timer timer;
	@Override
	public void run(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: scheduler， r, Position. credit, status, t, ReqQueue, Route, choosed, timer
		@ EFFECTS: 	出租车状态转换
					指令队列为空时，随机移动
					指令队列不为空时，按照路线队列点顺序移动
		*/
		int i=0;
		while(true){
			if(this.status == 2){//waiting
				moving();
				if(this.ReqQueue.isEmpty()){//移动期间接单成功，直接去完成 请求
					this.Position = getNextPos();
					i++;
				}
				if(!this.ReqQueue.isEmpty()){
					this.status = 3;
					if(this.Position.equals(ReqQueue.peekFirst())){//到达出发/目的地
						this.ReqQueue.removeFirst();
						stopService();
						i=0;
					}
					else {
						this.Position = getNextRoute();
						output();
						if(this.Position.equals(ReqQueue.peekFirst())){//到达出发/目的地
							this.ReqQueue.removeFirst();
							stopService();
							i=0;
						}
					}
				}
				if(this.status == 2&&i==100){//stop service
					stopService();
					i=0;
				}
			}else if(this.status == 1||this.status == 3){//Serving
				moving();
				this.Position = getNextRoute();
				output();
				//if(this.status==3)System.out.println(this.getName()+" "+this.Position.getX()+","+this.Position.getY());
				if(this.Position.equals(ReqQueue.peekFirst())){//到达出发/目的地
					this.ReqQueue.removeFirst();
					stopService();
					i=0;
				}
			}
		}
	}
	private void moving(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: t
		@ EFFECTS: 移动，暂停0.2s
		*/
		toSleep();
		toSleep();
	}
	private void stopService(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: status，t
		@ EFFECTS: 	停止服务， 暂停1s
					若为到达指令发出地点，status转换为服务状态
					若为到达目的地，status转换为等待接单状态
					若为普通停止服务，status依旧是等待服务状态
		*/
		int i = this.status;
		this.status = 0;
		toSleep();toSleep();toSleep();toSleep();toSleep();
		toSleep();toSleep();toSleep();toSleep();toSleep();
		if(this.ReqQueue.isEmpty()) this.status = 2;//已完成请求，进入等待
		else if(i == 1) this.status =3;
		else if(i == 3) this.status =1;
		else this.status = i;
		this.choosed =false;
	}
	public void toSleep() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: t
		@ EFFECTS: 	等待知道整0.1s后唤醒
					计时t增加0.1s
		*/
		this.timer.Wait();
		t+=0.1;
	}
///////////////////////////////////////////////////////////////////////////////////////
	private myPoint getNextPos(){//when status == 2
		/* 
		@ REQUIRES: None
		@ MODIFIES: 地图从当前点到下一个点之间的流量
		@ EFFECTS: 	获取写一个随机点
					增加地图从当前点到下一个点之间的流量
		*/
		myPoint p = this.scheduler.getNexrPos(Position);
		this.scheduler.changeStreamValue(Position, p);
		return p;
	}
	private myPoint getNextRoute(){//when status == 1
		/* 
		@ REQUIRES: None
		@ MODIFIES: 地图从当前点到下一个点之间的流量
		@ EFFECTS: 	获取路线队列的第一个点
					地图从当前点到下一个点之间的流量
		*/
		try {
			myPoint p = this.Route.take();
			this.scheduler.changeStreamValue(Position, p);
			return p;
		} catch (InterruptedException e) {
			return null;
		}
	}
///////////////////////////////////////////////////////////////////////////////////////	
	public void AddNewReq(myPoint toAdd){
		/* 
		@ REQUIRES: None
		@ MODIFIES: ReqQueue
		@ EFFECTS: 在队列尾增加新的行驶目的地 包括指令的发出点以及指令的目的地
		*/
		this.ReqQueue.add(toAdd);
	}
	public void AddRoute(myPoint route){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Route
		@ EFFECTS: 在队列尾增加行驶路径点
		*/
		this.Route.add(route);
	}
	public void getNewRoute(){
		/* 
		@ REQUIRES: 地图发生变换
		@ MODIFIES: Route
		@ EFFECTS: 	若当前指令队列为空，则不作任何操作
					若指令队列不为空，则获取新的最短且流量最小路径
		*/
		if(this.ReqQueue.isEmpty()) return;
		this.Route.clear();
		for(myPoint req: this.ReqQueue){
			int Pos[][] = this.scheduler.getMatrix(req);
			myPoint pos = this.getPosition();
			int dis = Pos[pos.getX()][pos.getY()];
			while(dis>0){
				myPoint next = null;
				int minDis = Integer.MAX_VALUE;
				for(myPoint tmp : this.scheduler.getChild(pos)){
					if(Pos[tmp.getX()][tmp.getY()]<minDis){
						minDis = Pos[tmp.getX()][tmp.getY()];
						next = tmp;
					}
				}
				this.AddRoute(next);
				pos = next;
				dis = minDis;
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////
	public boolean Grab(myPoint C){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 若当前出租车位置在指令周围4*4范围内则返回true， 否则返回false
		*/
		if(Math.abs(C.getX()-this.Position.getX())<=2&&Math.abs(C.getY()-this.Position.getY())<=2){
			return true;
		}
		else return false;
	}
	public void addCredit(int n){
		/* 
		@ REQUIRES: 出租车抢单或接单
		@ MODIFIES: credit
		@ EFFECTS: 本出租车的信用增加n
		*/
		this.credit+=n;
	}
	@Override
	public boolean equals(Object obj) {
		/* 
		@ REQUIRES: obj为Taxi类
		@ MODIFIES: None
		@ EFFECTS: 若两个出租车线程名相同，则返回true，否则返回false
		*/
		Taxi taxi = (Taxi) obj;
		return this.getName().equals(taxi.getName());
	}
////////////////////////////////////////////////////////////////////////////////////////
	public int getCredit() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 获取credit
		*/
		return this.credit;
	}
	public myPoint getPosition() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 获取当前位置的克隆
		*/
		myPoint p = new myPoint(this.Position.getX(), this.Position.getY(), 0);
		return p;
	}
	public myPoint getTerminal(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 若当前指令队列为空，则返回当前位置的克隆
					否则返回当前出租车目的地
		*/
		if(this.ReqQueue.isEmpty())
			return new myPoint(this.Position.getX(), this.Position.getY(), 0);
		else return this.ReqQueue.peekLast();
	}
	public int getStatus() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 获取当前状态
		*/
		return status;
	}
	public Point toPoint() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 将当前坐标转化为java.awt.Point类的点
		*/
		return new Point(this.Position.getX(), this.Position.getY());
	}
	public double getT() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 获取计时器时间t
		*/
		return t;
	}
//////////////////////////////////////////////////////////////////////////////////////////
	private void output(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 输出当前时间， 出租车位置，状态，目的地
		*/
		//Double Time = (double)System.currentTimeMillis()/1000;
		String S = String.format("%.1f", this.t)+": ("+this.Position.getX()+","+this.Position.getY()+") "+this.status+" heading("
				+this.ReqQueue.peekFirst().getX()+","+this.ReqQueue.peekFirst().getY()+")";
		FileWriter fs = null;
		try {
				fs = new FileWriter(this.getName()+".txt", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		PrintWriter p = new PrintWriter(fs);
		p.println(S);
		p.close();
	}
	@Override
	public String toString() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 获取当前时间，出租车名，位置，信用和状态
		*/
		return String.format("%.1f", this.t)+" "+this.getName()+":"+"("+this.Position.getX()+","+this.Position.getY()+") Credit"+this.credit+" Status="+this.status;
	}
}


/*[CR,(18,18),(79,79)]
[CR,(15,03),(79,78)]
[CR,(76,54),(0,0)]
[CR,(63,58),(79,79)]
[CR,(59,58),(61,56)]

[CR,(26,48),(9,7)]
[CR,(26,48),(9,10)]
[CR,(21,71),(9,7)]


[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,5),(9,7)]
[CR,(7,6),(9,7)]
[CR,(7,7),(9,7)]


[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,5),(9,7)]
[CR,(7,6),(9,7)]
[CR,(7,7),(9,7)]


[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,5),(9,7)]
[CR,(7,6),(9,7)]
[CR,(7,7),(9,7)]


[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,5),(9,7)]
[CR,(7,6),(9,7)]
[CR,(7,7),(9,7)]


[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,5),(9,7)]
[CR,(7,6),(9,7)]
[CR,(7,7),(9,7)]


[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,5),(9,7)]
[CR,(7,6),(9,7)]
[CR,(7,7),(9,7)]


[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(7,8),(9,7)]
[CR,(11,4),(11,27)]
[CR,(11,4),(11,25)]

[CR,(4,3),(4,1)]
[CR,(4,4),(4,1)]
[CR,(6,4),(4,1)]
[CR,(5,4),(4,1)]

[CR,(51,22),(8,10)]
[CR,(51,22),(8,11)]
[CR,(51,22),(8,12)]
[CR,(51,22),(8,13)]
[CR,(51,22),(8,14)]
[CR,(51,22),(8,15)]

[CR,(3,3),(8,10)]
[CR,(3,3),(8,11)]
[CR,(3,3),(8,12)]
[CR,(3,3),(8,13)]
[CR,(3,3),(8,14)]
[CR,(3,3),(8,15)]

[CR,(10,7),(10,10)]
[CR,(10,7),(10,9)]
[CR,(0,0),(12,12)]
[CR,(0,0),(12,13)]
[CR,(0,0),(12,14)]
[CR,(0,0),(12,15)]
[CR,(0,0),(12,16)]
[CR,(0,0),(12,172)]

[CR,(0,0),(79,79)]
[CR,(0,0),(79,73)]
[CR,(0,0),(79,74)]
[CR,(0,0),(79,75)]
[CR,(0,0),(79,76)]
[CR,(0,0),(79,77)]
[CM,(79,34),(79,35),0]
[CM,(79,34),(79,35),1]
[CM,(56,0),(57,0),0]

*/
