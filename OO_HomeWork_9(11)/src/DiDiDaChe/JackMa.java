package DiDiDaChe;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class JackMa {
	//Overview: this is a schedulor named MaYun, 233, who pass information between taxi, request, input, traffic light and Map
	//不变式: this.map!=null&&this.taxi!=null&&this.TaxiNum!=0&&this.gui!=null&&this.T!=null&&this.light!=null
	
	public boolean repOK() {
		if(this.map==null||this.taxi==null||this.TaxiNum==0||this.gui==null||this.light==null) return false;
		return true;
	}
	public JackMa(int TaxiNum, int MapSize){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Gui, Taxi, Map, TaxiNum
		@ EFFECTS: \this.Gui, \this.T, \this.Map, \this.Taxi[] initialize
		*/
		gui=new TaxiGUI();
		//T = new Timer(this);
		this.TaxiNum = TaxiNum;
		this.map = new Map(MapSize);
		this.map.guiLoadMap(this.gui);
		this.light = new Light(MapSize, this);
		this.light.start();
		this.taxi = initTaxi();
		this.initTaxiFile();
		t = new test(taxi);
		t.start();
		//T.start();
	}
	private Map map;
	private Taxi[] taxi;
	private int TaxiNum;
	private TaxiGUI gui;
	private test t;
	private Light light;
	private Taxi[] initTaxi() {
		/* 
		@ EFFECTS: 	\all int i=0;i<100;i++: init taxi
					初始化各出租车类型
		*/
		Taxi taxi[] = new Taxi[TaxiNum];
		for(int i=0; i<70; i++){
			taxi[i] = new Taxi(this);
			this.gui.SetTaxiType(i, 0);
		}
		for(int i = 70; i<TaxiNum; i++){
			taxi[i] = new superTaxi(this);
			this.gui.SetTaxiType(i, 1);
		}
		return taxi; 
	}
	private void initTaxiFile(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Taxi[], File "Taxi#+i"
		@ EFFECTS: 	\all int i=0;i<100;i++: 
						taxi[i].name = "Taxi#+i";
						delete File "Taxi#+i"
						taxi[i].start;
					初始化各出租车，给出租车线程命名，并开始运行
		*/
		for(int i=0; i<TaxiNum; i++){
			taxi[i].setName("Taxi#"+i);
			File f = new File(taxi[i].getName()+".txt");
			if(f.exists()){
				f.delete();
			}
			taxi[i].start();
		}
	}
	public myPoint getNexrPos(myPoint Pos){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	\result this.map.getNextPos(Pos); random point that leads to minimal stream value
					返回流量最小的随机邻接点
		*/
		return this.map.getNextPos(Pos);
	}
	public myPoint getNexrPosforS(myPoint Pos){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	\result this.map.getNextPos(Pos); random point that leads to minimal stream value
					返回流量最小的随机邻接点
		*/
		return this.map.getNextPosforS(Pos);
	}
	public void assignReq(Request req) {
		/* 
		@ REQUIRES: 0<req.pos.x<80;0<req.pos.y<80;0<req.dst.x<80;0<req.dst.y<80;req.pos!=req.dst
					指令发出点在地图范围内且发出点与目的地不同
		@ MODIFIES: req
		@ EFFECTS: 	req.start
					启动指令线程
		*/
		req.start();
		//System.out.println("start");
	}
	public void reqSetGUI(Request req){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Gui
		@ EFFECTS: 	mark the request in gui
					在GUi上标记指令
		*/
		gui.RequestTaxi(req.getPos(), req.getDst());
	}
	public ArrayList<Taxi> sendReq(myPoint pos){
		/* 
		@ REQUIRES: 0<pos.x<80;0<pos.y<80;	pos在地图范围内
		@ MODIFIES: None
		@ EFFECTS: 	\all int i=0;i<100;i++: if(pos.x-2<taxi[i].pos.x<pos.x+2&&pos.y-2<taxi[i].pos.y<pos.y+2)==>ret.add(taxi[i])
					\result ret;
					获取可接单出租车集合
		*/
		ArrayList<Taxi> ret = new ArrayList<Taxi>();
		for(int i=0; i<this.TaxiNum; i++){
			if(taxi[i].Grab(pos)) ret.add(taxi[i]);
		}
		return ret;
	}
	
	public int[][] getMatrix(myPoint p){
		/* 
		@ REQUIRES: 0<p.x<80;0<p.y<80;		p在地图范围内
		@ MODIFIES: None
		@ EFFECTS: 	\result = this.map.getMat(p.getX(), p.getY());
					获取路线距离矩阵
		*/
		return this.map.getMat(p.getX(), p.getY());
	}
	public int[][] getMatrixforS(myPoint p){
		/* 
		@ REQUIRES: 0<p.x<80;0<p.y<80;		p在地图范围内
		@ MODIFIES: None
		@ EFFECTS: 	\result = this.map.getMat(p.getX(), p.getY());
					获取路线距离矩阵
		*/
		return this.map.getMat(p.getX(), p.getY());
	}
	public LinkedList<myPoint> getChild(myPoint p) {
		/* 
		@ REQUIRES: 0<p.x<80;0<p.y<80;
		@ MODIFIES: None
		@ EFFECTS: 	this.Map[p.x][p.y].U==>res.add(mypoint(p.x-1,p.y))
					this.Map[p.x][p.y].D==>res.add(mypoint(p.x+1,p.y))
					this.Map[p.x][p.y].R==>res.add(mypoint(p.x,p.y+1))
					this.Map[p.x][p.y].L==>res.add(mypoint(p.x,p.y-1))
					\result res
					获取邻接点集合
		*/
		return this.map.getChild(p.getX(), p.getY());
	}
	public LinkedList<myPoint> getChildforS(myPoint p) {
		/* 
		@ REQUIRES: 0<p.x<80;0<p.y<80;
		@ MODIFIES: None
		@ EFFECTS: 	this.Map[p.x][p.y].U==>res.add(mypoint(p.x-1,p.y))
					this.Map[p.x][p.y].D==>res.add(mypoint(p.x+1,p.y))
					this.Map[p.x][p.y].R==>res.add(mypoint(p.x,p.y+1))
					this.Map[p.x][p.y].L==>res.add(mypoint(p.x,p.y-1))
					\result res
					获取邻接点集合
		*/
		return this.map.getChildforS(p.getX(), p.getY());
	}
	public void refreshGUI(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Gui
		@ EFFECTS: \this.gui.taxi[i].position=\this.taxi[i].position刷新出租车在地图上的位置
		*/
		/*
		if(!this.light.repOK())System.out.println("false light");
		if(!this.map.repOK())System.out.println("false map");
		if(!this.repOK())System.out.println("false MAYUN");
		*/
		for(int i=0; i<100; i++){
			gui.SetTaxiStatus(i, taxi[i].toPoint(), taxi[i].getStatus());
			//if(!this.taxi[i].repOK())System.out.println("false taxi"+taxi[i].getName());
		}
	}

	public void MapChange(myPoint p1, myPoint p2,int status){
		/* 
		@ REQUIRES: (0<p1<79)&&(0<p2<79)&&((p1.x==p2.x&&abs(p1.y-p2.y)==1)||(p1.y==p2.y&&abs(p1.x-p2.x)==1))
					status==0||status==1
					p1,p2为地图范围内邻接点，status为0或1
		@ MODIFIES: Map，Taxi
		@ EFFECTS: 	status==1 ==>Map[p1.x][p1.y].road = true;Map[p2.x][p2.y].road = true;road between p1 and p2 is set true
					status==0 ==>Map[p1.x][p1.y].road = false;Map[p2.x][p2.y].road = false;road between p1 and p2 is set false
					taxi.route = taxi.getNewRoute;
					如果删除已存在边，或增加原不存在边，则改变Map连接关系
					刷新出租车运行路线。
		*/
		this.map.setMapStatus(p1, p2, status);
		for(int i=0;i<this.TaxiNum;i++){
			this.taxi[i].getNewRoute();
		}
		this.gui.SetRoadStatus(p1.toPoint(), p2.toPoint(), status);
	}
	
	public void StreamValueUP(myPoint from, myPoint to){
		/* 
		@ REQUIRES: ((from.x==to.x&&abs(from.y-to.y)==1)||(from.y==to.y&&abs(from.x-to.x)==1)) from，to为邻接点
		@ MODIFIES: Map[from.x][from.y], Map[to.x][to.y]中对应点流量
		@ EFFECTS: 	Map[from.x][from.y].streamValue++, Map[to.x][to.y].streamValue++
					改变地图流量
		*/
		this.map.StreamValueUP(from, to);
	}
	public void StreamValueDOWN(myPoint from, myPoint to){
		/* 
		@ REQUIRES: ((from.x==to.x&&abs(from.y-to.y)==1)||(from.y==to.y&&abs(from.x-to.x)==1)) from，to为邻接点
		@ MODIFIES: Map[from.x][from.y], Map[to.x][to.y]中对应点流量
		@ EFFECTS: 	Map[from.x][from.y].streamValue++, Map[to.x][to.y].streamValue++
					改变地图流量
		*/
		this.map.StreamValueDOWN(from, to);
	}
	public double checkLight(myPoint last, myPoint from, myPoint to){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	\result = light.waiting
		*/
		return this.light.waiting(last, from, to);
	}
	public void setLight(Point p, int status){
		/* 
		@ REQUIRES: None
		@ MODIFIES: GUi
		@ EFFECTS: 	gui.p.LightStatus = light.p.status
		*/
		this.gui.SetLightStatus(p, status);
	}
}
