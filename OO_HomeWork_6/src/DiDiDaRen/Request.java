package DiDiDaRen;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

public class Request extends Thread{
	public Request(myPoint Pos, myPoint Dst,String REQ, int t, JackMa Scheduler){
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
		double Time;
		this.Scheduler.reqSetGUI(this);
		sendReq();
		MatrixPos = getMatrix(this.Pos);
		sendReq();
		MatrixDst = getMatrix(this.Dst);
		double St = (double)System.currentTimeMillis()/1000;
		do{
			sendReq();
			taxi.sort(cmpCredit);
			Time = (double)System.currentTimeMillis()/1000;
		}while(Time-St<3);
		if(this.taxi.isEmpty())System.out.println(this.REQ+"无应答");
		else {
			chooseTaxi();
			if(this.TarTaxi==null)System.out.println(this.REQ+"无应答");
			else{
				System.out.println(this.REQ+this.TarTaxi.getName()+"成功接单");
				startService();
			}
		}
	}
	private void sendReq(){
		ArrayList<Taxi> taxiN = this.Scheduler.sendReq(this.Pos);
		if(taxiN==null)return;
		for(int i=0; i<taxiN.size();i++){
			if(this.taxi.isEmpty()||!this.taxi.contains(taxiN.get(i))){
				this.taxi.add(taxiN.get(i));
				taxiN.get(i).addCredit(1);
			}
			output("扫描到 "+taxiN.get(i).toString(), "scanRes"+this.REQ+".txt");
		}
	}
	private void chooseTaxi(){
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
				}
				this.TarTaxi = tmp;
			}
			output(tmp.toString(), this.REQ+".txt");
		}
		/*if(this.TarTaxi != null&&this.TarTaxi.choosed==false)this.TarTaxi.choosed=true;
		else if(this.TarTaxi != null&&this.TarTaxi.choosed==true){
			this.TarTaxi = null;
			chooseTaxi();
		}*/
	}
	private void startService(){
		//myPoint pos = this.TarTaxi.getPosition();
		myPoint pos = this.TarTaxi.getTerminal();
		int dis = MatrixPos[pos.getX()][pos.getY()];
		//myPoint tmp;
		//Double Time = (double)System.currentTimeMillis()/1000;
		String S = String.format("%.1f", this.TarTaxi.getT()-2.9)+"Req:"+this.REQ+": Taxi Position("+pos.getX()+","+pos.getY()+")";
		output(S,this.TarTaxi.getName()+".txt");
		this.TarTaxi.addCredit(3);
		this.TarTaxi.AddNewReq(this.Pos);
		while(dis>0){
			for(myPoint tmp : this.Scheduler.getChild(pos)){
				//System.out.println("child"+tmp.getX()+" "+tmp.getY());
				if(dis - MatrixPos[tmp.getX()][tmp.getY()]==1){
					this.TarTaxi.AddRoute(tmp);
					pos = tmp;
					dis--;
					break;
				}
			}
			//System.out.println(dis);
		}
		dis = MatrixDst[this.Pos.getX()][this.Pos.getY()];
		this.TarTaxi.AddNewReq(this.Dst);
		while(dis>0){
			for(myPoint tmp : this.Scheduler.getChild(pos)){
				if(dis - MatrixDst[tmp.getX()][tmp.getY()]==1){
					this.TarTaxi.AddRoute(tmp);
					pos = tmp;
					dis--;
					break;
				}
			}
		}
	}
	private void output(String S, String Target){
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
		return this.Scheduler.getMatrix(p);
	}
	private Comparator<Taxi> cmpCredit = new Comparator<Taxi>() {
		public int compare(Taxi e1, Taxi e2) {
			return (int) (e2.getCredit() - e1.getCredit());
		}
	};
	@Override
	public boolean equals(Object obj) {
		Request tmp = (Request) obj;
		return this.t==tmp.t&&this.Pos.equals(tmp.Pos)&&this.Dst.equals(tmp.Dst);
	}
	public Point getPos() {
		return Pos.toPoint();
	}
	public Point getDst() {
		return Dst.toPoint();
	}
}