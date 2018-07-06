package DiDiDaChe;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

public class Request extends Thread{
	//Overview: this is a request class. In this class it contains the request in string form. the request search for taxis through MaYun.and then get connected with the taxi and send route to it.
	//不变式：this.REQ!=null&&this.Pos!=null&&this.Dst!=null&&this.Scheduler!=null&&taxi[i]!=taxi[j] for all in 0<i,j<taxi.size i!=j;
	public boolean repOK() {
		if(this.REQ==null||this.Pos==null||this.Dst==null||this.Scheduler==null) return false;
		for(int i=0;i<taxi.size();i++){
			for(int j=i+1;j<taxi.size();j++){
				if(taxi.get(i).equals(taxi.get(j)))return false;
			}
		}
		return true;
	}
	public Request(myPoint Pos, myPoint Dst,String REQ, int t, JackMa Scheduler){
		/* 
		@ REQUIRES: None
		@ MODIFIES: taxi， REQ， Pos， Dst，t，Scheduler
		@ EFFECTS: 	taxi = new ArrayList<Taxi>();
					\this.REQ = REQ;
					\this.Pos = Pos;
					\this.Dst = Dst;
					\this.Scheduler = Scheduler;
					\this.t = t;
					初始化taxi， REQ， Pos， Dst，t，Scheduler
		*/
		taxi = new ArrayList<Taxi>();
		this.REQ = REQ;
		this.Pos = Pos;
		this.Dst = Dst;
		this.Scheduler = Scheduler;
		this.t = t;
	}
	private myPoint Pos;
	private myPoint Dst;
	private String REQ;
	private int t;
	private JackMa Scheduler;
	private Taxi TarTaxi = null; 
	private ArrayList<Taxi> taxi;
	private int[][] MatrixPos = new int[80][80];
	private int[][] MatrixDst = new int[80][80];
	@Override
	public void run() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: taxi， REQ，t，Scheduler
		@ EFFECTS: 	\this.taxi = \all int i=0;i<100;i++: if(pos.x-2<taxi[i].pos.x<pos.x+2&&pos.y-2<taxi[i].pos.y<pos.y+2)==>ret.add(taxi[i])
					\this.taxi!=null==>\this.TarTaxi = the taxi that has highest credit ,if there are several taxis have same credit choose the nearest one
					发送请求寻找可接单的出租车
					
		*/
		double Time;
		this.Scheduler.reqSetGUI(this);
		double St = (double)System.currentTimeMillis()/1000;
		do{
			sendReq();
			taxi.sort(cmpCredit);
			Time = (double)System.currentTimeMillis()/1000;
		}while(Time-St<3);
		if(this.taxi.isEmpty())System.out.println(this.REQ+"无应答");
		else {
			MatrixPos = getMatrix(this.Pos);
			chooseTaxi();
			if(this.TarTaxi==null)System.out.println(this.REQ+"无应答");
			else{
				System.out.println(this.REQ+this.TarTaxi.getName()+"成功接单");
				startService();
			}
		}
	}
	private void sendReq(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: taxi
		@ EFFECTS: 	\all int i=0;i<100;i++: if(pos.x-2<taxi[i].pos.x<pos.x+2&&pos.y-2<taxi[i].pos.y<pos.y+2)==>ret.add(taxi[i])
					\result ret;
					获取抢单的出租车队列
					若当前出租车不为等待服务状态则跳过
					若重复则忽略，将不重复的加入taxi
		*/
		ArrayList<Taxi> taxiN = this.Scheduler.sendReq(this.Pos);
		if(taxiN==null)return;
		for(int i=0; i<taxiN.size();i++){
			if(taxiN.get(i).getStatus()!=2){
				output("扫描到 "+taxiN.get(i).toString(), "scanRes"+this.REQ+".txt");
				continue;
			}
			if(this.taxi.isEmpty()||!this.taxi.contains(taxiN.get(i))){
				this.taxi.add(taxiN.get(i));
				taxiN.get(i).addCredit(1);
			}
			output("扫描到 "+taxiN.get(i).toString(), "scanRes"+this.REQ+".txt");
		}
	}
	private void chooseTaxi(){
		/* 
		@ REQUIRES: \this.taxi!=null&&\this.taxi is sorted by credit	出租车队列已经按照信用值排序
		@ MODIFIES: \this.TarTaxi
		@ EFFECTS: 	\any int i=0;i<this.taxi.size;i++:taxi.get(i).choosed==false&&taxi.status==wait&&同信用值距离最短==> \this.Tartaxi = taxi.get(i);
					在taxi队列中选择接单的出租车
					出租车应当在等待服务状态，且当前瞬间未接单
		*/
		File f = new File(this.REQ+".txt");
		if(f.exists()){
			f.delete();
		}
		for(Taxi tmp : this.taxi){
			//System.out.println(this.REQ+tmp.getName()+" "+tmp.getCredit());
			if(tmp.getStatus()!=2||tmp.choosed==true);
			else if(this.TarTaxi==null){
				if(tmp.choosed==false){
					tmp.choosed=true;
					this.TarTaxi = tmp;
				}
			}
			else if(tmp.getCredit()==this.TarTaxi.getCredit()
					&& MatrixPos[this.TarTaxi.getPosition().getX()][this.TarTaxi.getPosition().getY()]
					>MatrixPos[tmp.getPosition().getX()][tmp.getPosition().getY()]){
				if(tmp.choosed==false){
					tmp.choosed=true;
					this.TarTaxi.choosed = false;
					this.TarTaxi = tmp;
				}
			}
			output(tmp.toString(), this.REQ+".txt");
		}
	}
	private void startService(){
		/* 
		@ REQUIRES: TarTaxi!=null
		@ MODIFIES: None
		@ EFFECTS: 	\this.TarTaxi.reqList.add(pos);
					\this.TarTaxi.reqList.add(dst);
					\this.TarTaxi add points in route with shortest route and minimal Stream value
					向出租车发送行驶路线
					向出租车发送请求的发出位置和目的地
		*/
		myPoint pos = this.TarTaxi.getTerminal();
		String S = String.format("%.1f", this.TarTaxi.getT()-2.8)+"Req:"+this.REQ+": Taxi Position("+pos.getX()+","+pos.getY()+")";
		this.TarTaxi.addRequest(S);
		output(S,this.TarTaxi.getName()+".txt");
		this.TarTaxi.addCredit(3);
		this.TarTaxi.AddNewReq(this.Pos);
		this.TarTaxi.AddNewReq(this.Dst);
		this.TarTaxi.getNewRoute();
		/*
		myPoint pos = this.TarTaxi.getTerminal();
		MatrixPos = getMatrix(this.Pos);
		int dis = MatrixPos[pos.getX()][pos.getY()];
		String S = String.format("%.1f", this.TarTaxi.getT()-2.8)+"Req:"+this.REQ+": Taxi Position("+pos.getX()+","+pos.getY()+")";
		output(S,this.TarTaxi.getName()+".txt");
		this.TarTaxi.addCredit(3);
		this.TarTaxi.AddNewReq(this.Pos);
		this.TarTaxi.AddNewReq(this.Dst);
		while(dis>0){
			myPoint next = null;
			int minDis = Integer.MAX_VALUE;
			for(myPoint tmp : this.Scheduler.getChild(pos)){
				if(MatrixPos[tmp.getX()][tmp.getY()]<minDis){
					minDis = MatrixPos[tmp.getX()][tmp.getY()];
					next = tmp;
				}
			}
			this.TarTaxi.AddRoute(next);
			pos = next;
			dis = minDis;
		}
		MatrixDst = getMatrix(this.Dst);
		dis = MatrixDst[this.Pos.getX()][this.Pos.getY()];
		while(dis>0){
			myPoint next = null;
			int minDis = Integer.MAX_VALUE;
			for(myPoint tmp : this.Scheduler.getChild(pos)){
				if(MatrixDst[tmp.getX()][tmp.getY()]<minDis){
					minDis = MatrixDst[tmp.getX()][tmp.getY()];
					next = tmp;
				}
			}
			this.TarTaxi.AddRoute(next);
			pos = next;
			dis = minDis;
		}
		*/
	}
	public void output(String S, String Target){
		/* 
		@ REQUIRES: None
		@ MODIFIES: FILE("Target")
		@ EFFECTS: 	FILE("Target").println(S)
					将指定字符串写入至Target文件
		*/
		FileWriter fs = null;
		try {
				fs = new FileWriter(Target, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		PrintWriter p = new PrintWriter(fs);
		p.println(S);
		p.close();
	}
	private int[][] getMatrix(myPoint p){
		/* 
		@ REQUIRES: 0<p.x<80;0<p.y<80; 	p在地图内
		@ MODIFIES: None
		@ EFFECTS: \result = this.Scheduler.getMatrix(p);
					获取以p为基准点的距离矩阵
		*/
		return this.Scheduler.getMatrix(p);
	}
	private Comparator<Taxi> cmpCredit = new Comparator<Taxi>() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	\result = (int) (e2.getCredit() - e1.getCredit());
					比较taxi信用值
		*/
		public int compare(Taxi e1, Taxi e2) {
			return (int) (e2.getCredit() - e1.getCredit());
		}
	};
	@Override
	public boolean equals(Object obj) {
		/* 
		@ REQUIRES: obj为Request类
		@ MODIFIES: None
		@ EFFECTS: 	\result = obj.t&&this.Pos.equals(obj.Pos)&&this.Dst.equals(obj.Dst);
					若指令发出时间相同，发出位置相同，目的地相同，则返回true，否则返回false
		*/
		Request tmp = (Request) obj;
		return this.t==tmp.t&&this.Pos.equals(tmp.Pos)&&this.Dst.equals(tmp.Dst);
	}
	public Point getPos() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	\result = this.Pos.toPoint();
					获取指令发出位置
		*/
		return Pos.toPoint();
	}
	public Point getDst() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	\result = this.Dst.toPoint();
					获取指令目的地
		*/
		return Dst.toPoint();
	}
}