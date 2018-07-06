package DiDiDaChe;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class JackMa {
	public JackMa(int TaxiNum, int MapSize){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Gui, Taxi, Map, TaxiNum
		@ EFFECTS: 初始化Gui, Timer, Map, Taxi
		*/
		gui=new TaxiGUI();
		T = new Timer(this);
		this.TaxiNum = TaxiNum;
		this.map = new Map(MapSize);
		this.map.guiLoadMap(this.gui);
		initTaxi();
		T.start();
	}
	private Map map;
	private Taxi[] taxi;
	private int TaxiNum;
	private TaxiGUI gui;
	private Timer T;
	private void initTaxi() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: Taxi[]
		@ EFFECTS: 初始化各出租车，给出租车线程命名，并开始运行
		*/
		taxi = new Taxi[TaxiNum];
		for(int i=0; i<TaxiNum; i++){
			taxi[i] = new Taxi(this, this.T);
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
		@ EFFECTS: 返回流量最小的随机邻接点
		*/
		return this.map.getNextPos(Pos);
	}
	public void assignReq(Request req) {
		/* 
		@ REQUIRES: 指令发出点在地图范围内且发出点与目的地不同
		@ MODIFIES: req
		@ EFFECTS: 启动指令线程
		*/
		req.start();
		//System.out.println("start");
	}
	public void reqSetGUI(Request req){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Gui
		@ EFFECTS: 在GUi上标记指令
		*/
		gui.RequestTaxi(req.getPos(), req.getDst());
	}
	public ArrayList<Taxi> sendReq(myPoint pos){
		/* 
		@ REQUIRES: pos在地图范围内
		@ MODIFIES: None
		@ EFFECTS: 获取可接单出租车集合
		*/
		ArrayList<Taxi> ret = new ArrayList<Taxi>();
		for(int i=0; i<this.TaxiNum; i++){
			if(taxi[i].Grab(pos)) ret.add(taxi[i]);
		}
		return ret;
	}
	
	public int[][] getMatrix(myPoint p){
		/* 
		@ REQUIRES: p在地图范围内
		@ MODIFIES: None
		@ EFFECTS: 获取路线距离矩阵
		*/
		return this.map.getMat(p.getX(), p.getY());
	}
	
	public LinkedList<myPoint> getChild(myPoint p) {
		/* 
		@ REQUIRES: p在地图内
		@ MODIFIES: None
		@ EFFECTS: 获取邻接点集合
		*/
		return this.map.getChild(p.getX(), p.getY());
	}
	
	public void refreshGUI(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Gui
		@ EFFECTS: 刷新出租车在地图上的位置
		*/
		for(int i=0; i<100; i++){
			gui.SetTaxiStatus(i, taxi[i].toPoint(), taxi[i].getStatus());
		}
	}
	public void refreshMap(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Map
		@ EFFECTS: 刷新地图流量
		*/
		this.map.refresh();
	}
	public void MapChange(myPoint p1, myPoint p2,int status){
		/* 
		@ REQUIRES: p1,p2为地图范围内邻接点，status为0或1
		@ MODIFIES: Map，Taxi
		@ EFFECTS: 	如果删除已存在边，或增加原不存在边，则改变Map连接关系
					刷新出租车运行路线。
		*/
		this.map.setMapStatus(p1, p2, status);
		for(int i=0;i<this.TaxiNum;i++){
			this.taxi[i].getNewRoute();
		}
		this.gui.SetRoadStatus(p1.toPoint(), p2.toPoint(), status);
	}
	public void changeStreamValue(myPoint from, myPoint to){
		/* 
		@ REQUIRES: from，to为邻接点
		@ MODIFIES: Map中对应点流量
		@ EFFECTS: 改变地图流量
		*/
		this.map.ChangeStreamValue(from, to);
	}
}
