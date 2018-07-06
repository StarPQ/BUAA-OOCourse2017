package DiDiDaRen;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class JackMa {
	public JackMa(int TaxiNum, int MapSize){
		//this.MapSize = MapSize;
		this.TaxiNum = TaxiNum;
		this.map = new Map(MapSize);
		initTaxi();
		//System.out.println("finish init");
		gui=new TaxiGUI();
		this.map.guiLoadMap(this.gui);
	}
	private Map map;
	private Taxi[] taxi;
	private int TaxiNum;
	private TaxiGUI gui;
	//private int MapSize;
	private void initTaxi() {
		taxi = new Taxi[TaxiNum];
		for(int i=0; i<TaxiNum; i++){
			taxi[i] = new Taxi(this);
			taxi[i].setName("Taxi#"+i);
			File f = new File(taxi[i].getName()+".txt");
			if(f.exists()){
				f.delete();
			}
			taxi[i].start();
		}
	}
	public myPoint getNexrPos(myPoint Pos){
		return this.map.getNextPos(Pos);
	}
	public void assignReq(Request req) {
		req.start();
		//System.out.println("start");
	}
	public void reqSetGUI(Request req){
		gui.RequestTaxi(req.getPos(), req.getDst());
	}
	public ArrayList<Taxi> sendReq(myPoint pos){
		ArrayList<Taxi> ret = new ArrayList<Taxi>();
		for(int i=0; i<this.TaxiNum; i++){
			if(taxi[i].Grab(pos)) ret.add(taxi[i]);
		}
		return ret;
	}
	
	public int[][] getMatrix(myPoint p){
		return this.map.getMat(p.getX(), p.getY());
	}
	
	public LinkedList<myPoint> getChild(myPoint p) {
		return this.map.getChild(p.getX(), p.getY());
	}
	
	public void refresh(){
		for(int i=0; i<100; i++){
			gui.SetTaxiStatus(i, taxi[i].toPoint(), taxi[i].getStatus());
		}
	}
}
