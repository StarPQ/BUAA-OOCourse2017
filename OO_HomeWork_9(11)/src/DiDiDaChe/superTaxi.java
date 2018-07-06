package DiDiDaChe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

public class superTaxi extends Taxi{
	//overview: this is a class of super Taxi 
	//不变式：this.scheduler!=null&&this.Position!=null&&0<=this.position.x<80
	public boolean repOK() {
		if(this.scheduler==null||this.Position==null) return false;
		if(this.Position.getX()<0||this.Position.getX()>=80||this.Position.getY()<0||this.Position.getY()>=80) return false;
		return true;
	}
	public superTaxi(JackMa scheduler) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: scheduler， r, Position. credit, status, t, ReqQueue, Route, choosed, timer 
		@ EFFECTS: 	\this.scheduler = scheduler;
					\this.Position = new myPoint(r.nextInt(80), r.nextInt(80), 0);
					\this.credit = 0;
					\this.status = 2;
					\this.t = 0;
					\this.ReqQueue = new LinkedList<myPoint>();
					\this.Route = new LinkedBlockingQueue<myPoint>();
					\this.choosed = false;
					\this.timer = T;
					\this.lastP = this.Position;
					初始化scheduler， r, Position. credit, status, t, ReqQueue, Route, choosed, timer
		*/
		super(scheduler);
		this.list = new LinkedList<LinkedList<myPoint>>();
	}
	public ListIterator RouteRecord(int i){
		/*
		 @EFFECTS: /result = this.list.listIterator
		 */
		try {
			synchronized (list) {
				return list.get(i).listIterator();
			}
		} catch (Exception e) {
			return null;
		}
	}
	public ListIterator ReqRecord(){
		/*
		 @EFFECTS: /result = this.list.listIterator
		 */
		try {
			return this.Requests.listIterator();
		} catch (Exception e) {
			return null;
		}
	}
	private LinkedList<LinkedList<myPoint>> list;
	@Override
	public void getNewRoute(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Route
		@ EFFECTS: 	this.ReqQueue.isEmpty() ==>return
					else ==> this.route.clear&&this.route set new shortest route 
					若当前指令队列为空，则不作任何操作
					若指令队列不为空，则获取新的最短且流量最小路径
		*/
		if(this.ReqQueue.isEmpty()) return;
		if(!this.Route.isEmpty()){
			this.Route.clear();
			this.list.pollLast();
		}
		LinkedList<myPoint> L = new LinkedList<myPoint>();
		for(myPoint req: this.ReqQueue){
			int Pos[][] = this.scheduler.getMatrixforS(req);
			myPoint pos = this.getPosition();
			int dis = Pos[pos.getX()][pos.getY()];
			while(dis>0){
				myPoint next = null;
				int minDis = Integer.MAX_VALUE;
				for(myPoint tmp : this.scheduler.getChildforS(pos)){
					if(Pos[tmp.getX()][tmp.getY()]<minDis){
						minDis = Pos[tmp.getX()][tmp.getY()];
						next = tmp;
					}
				}
				this.AddRoute(next);
				L.add(next);
				pos = next;
				dis = minDis;
			}
		}
		synchronized (list) {
			this.list.add(L);
		}
	}
	@Override
	public myPoint getNextPos(){//when status == 2
		/* 
		@ REQUIRES: None
		@ MODIFIES: Map[from.x][from.y], Map[to.x][to.y]
					地图从当前点到下一个点之间的流量
		@ EFFECTS: 	\result this.map.getNextPos(Pos); random point that leads to minimal stream value
					Map[from.x][from.y].streamValue++, Map[to.x][to.y].streamValue++
					获取下一个随机点
					增加地图从当前点到下一个点之间的流量
		*/
		myPoint p = this.scheduler.getNexrPosforS(Position);
		this.scheduler.StreamValueUP(Position, p);
		return p;
	}
}
