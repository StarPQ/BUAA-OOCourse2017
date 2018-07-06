package DiDiDaChe;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

public class Light extends Thread{
	//Overview: this is traffic light class which read the file of traffic lights
	//不变式：this.MapSize != 0&&this.Map != null&&this.MaYun != null&&Map[i][j]==0||Map[i][j]==1for all in 0<=i,j<80
	//&&if(this.Map[i][j]==1)(trafficL[i][j]!=null)
	public boolean repOK() {
		if(this.MapSize == 0||this.Map == null||this.MaYun == null) return false;
		for(int i=0;i<80;i++){
			for(int j=0;j<80;j++){
				if(this.Map[i][j]!=0&&this.Map[i][j]!=1)return false;
				if(this.Map[i][j]==1){
					if(trafficL[i][j]==null)return false;
				}
			}
		}
		return true;
	}
	public Light(int MapSize, JackMa MaYun){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Map[][], System,out
		@ EFFECTS: 	!File(path).exist==>System.out&&System.exit
					\any char!=0&&char!=1 ==>System.out&&System.exit
					\any somewhere not cross road but has light ==>System.out&&System.exit
					读取地图文件，有问题报错
		*/
		this.MaYun = MaYun;
		this.MapSize = MapSize;
		Scanner in = new Scanner(System.in);
		Map = new int[this.MapSize][this.MapSize];
		this.trafficL = new trafficLight[this.MapSize][this.MapSize];
		System.out.println("请输入红绿灯文件存储路径");
		if(in.hasNextLine()){
			if(readTxtFile(in.nextLine())==0) System.out.println("红绿灯载入成功");
			else {
				System.out.println("红绿灯载入失败，请重新启动程序");
				System.exit(0);
			}
		}
	}
	private final int MapSize;
	public int[][] Map;
	private trafficLight[][] trafficL;
	private JackMa MaYun;
	
	private int readTxtFile(String filePath){//读取地图文件
		/* 
		@ REQUIRES: None
		@ MODIFIES: Map[][]
		@ EFFECTS: 	!File(path).exist==>System.out&&System.exit
					\any char!=0&&char!=1 ==>System.out&&System.exit
					\any somewhere not cross road but has light ==>System.out&&System.exit
					读取地图文件，找不到文件或有问题报错
		*/
		try {
			String encoding="UTF-8";
			File file=new File(filePath);
			if(file.isFile() && file.exists()){ //判断文件是否存在
				InputStreamReader read = new InputStreamReader(	new FileInputStream(file),encoding);//考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				if(loadMap(bufferedReader)<0)
					return -1;
				read.close();
				return 0;
			}else{
				System.out.println("找不到指定的文件");
				return -1;
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
			return -1;
		}
	}
	
	private int loadMap(BufferedReader Reader) throws IOException{
		/* 
		@ REQUIRES: File(path).exist
		@ MODIFIES: Map[][]
		@ EFFECTS: 	\any char!=0&&char!=1 ==>System.out&&System.exit
					\any somewhere not cross road but has light ==>System.out&&System.exit
					读取地图文件，有问题报错
		*/
		String lineTxt = null;
		for(int lineNumber = 0; lineNumber < this.MapSize; lineNumber++){
			lineTxt = Reader.readLine();
			lineTxt = lineTxt.replaceAll(" ", "");
			lineTxt = lineTxt.replaceAll("	", "");
			if(lineTxt.length()!=this.MapSize){
				System.out.println("第"+lineNumber+"行长度不等于"+this.MapSize);
				return -1;
			}
			for(int i = 0; i < this.MapSize; i++){
				if(lineTxt.charAt(i)=='1'){
					Map[lineNumber][i] = 1;
					//TODO check
					this.trafficL[lineNumber][i] = new trafficLight(this);
					this.trafficL[lineNumber][i].start();
					if(this.MaYun.getChild(new myPoint(lineNumber,i,0)).size()<3){
						System.out.println("第"+lineNumber+"行第"+i+"个字符不符合要求");
						return -1;
					}
						
				}
				else if(lineTxt.charAt(i)=='0');
				else{
					System.out.println("第"+lineNumber+"行第"+i+"个字符不符合要求");
					return -1;
				}
			}
		}
		if(Reader.readLine()!=null){
			System.out.println("超过80行");
			return -1;
		}
		return 0;
	}
	
	public double  waiting(myPoint lastP, myPoint from, myPoint to){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	(EW)&&((getDir(lastP, from)==0 && getDir(from, to)==0)||(getDir(lastP, from)==0 && getDir(from, to)==2)
				||(getDir(lastP, from)==1 && getDir(from, to)==1)||(getDir(lastP, from)==1 && getDir(from, to)==3))
				{
					double t1 = System.currentTimeMillis()/1000.0;
					synchronized (this) {
						try {
							wait(this.Time);
						} catch (InterruptedException e) {}
					}
					double t2 = System.currentTimeMillis()/1000.0;
					\return t2-t1;
				}
				}
				!EW&&(getDir(lastP, from)==2 && getDir(from, to)==2)||(getDir(lastP, from)==2 && getDir(from, to)==1)
					||(getDir(lastP, from)==3 && getDir(from, to)==3)||(getDir(lastP, from)==3 && getDir(from, to)==0))
					{
						double t1 = System.currentTimeMillis()/1000.0;
						synchronized (this) {
							try {
								wait(this.Time);
							} catch (InterruptedException e) {}
						}
						double t2 = System.currentTimeMillis()/1000.0;
						\return t2-t1;
					}
				}
				\return = 0;
		*/
		if(this.Map[from.getX()][from.getY()]==1){
			return this.trafficL[from.getX()][from.getY()].waiting(lastP, from, to);
		}
		else return 0;
	}
	@Override
	public void run() {
		while (true) {
			try {
				sleep(200);
			} catch (InterruptedException e) {}
			refresh();
		}
	}
	public void refresh(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Gui
		@ EFFECTS: 	\all int i=0;i<80;i++:int j=0;j<80;j++:gui.p.light = Map[i][j].p.light
		*/
		for(int i=0; i<80; i++){
			for(int j=0; j<80; j++){
				int sta = this.Map[i][j]==0? 0:this.trafficL[i][j].isEW()? 1:2;
				this.MaYun.setLight(new Point(i,j), sta);
			}
		}
	}
}

class trafficLight extends Thread{
	//Overview: this is traffic light class which read the file of traffic lights
	//不变式：this.L!=null
	public boolean repOK() {
		if(this.L==null) return false;
		return true;
	}
	public trafficLight(Light L) {
		this.L = L;
		Random r = new Random();
		this.Time = r.nextInt(300)+200;
		this.EW = r.nextBoolean();
	}
	private boolean EW;
	private Light L;
	private long Time;
	@Override
	public void run() {
		/* TODO
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	run
		@ THREAD_REQUIRES:locked(this)
		@ THREAD_EFFECTS:locked(this)
		*/
		while(true){
			try {
				sleep(this.Time);
			} catch (InterruptedException e) {}
			synchronized (this) {
				EW = !EW;//true 为绿 false为红
				notifyAll();
			}
		}
	}
	public double waiting(myPoint lastP, myPoint from, myPoint to){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	(EW)&&((getDir(lastP, from)==0 && getDir(from, to)==0)||(getDir(lastP, from)==0 && getDir(from, to)==2)
				||(getDir(lastP, from)==1 && getDir(from, to)==1)||(getDir(lastP, from)==1 && getDir(from, to)==3))
				{
					double t1 = System.currentTimeMillis()/1000.0;
					synchronized (this) {
						try {
							wait(this.Time);
						} catch (InterruptedException e) {}
					}
					double t2 = System.currentTimeMillis()/1000.0;
					\return t2-t1;
				}
				}
				!EW&&(getDir(lastP, from)==2 && getDir(from, to)==2)||(getDir(lastP, from)==2 && getDir(from, to)==1)
					||(getDir(lastP, from)==3 && getDir(from, to)==3)||(getDir(lastP, from)==3 && getDir(from, to)==0))
					{
						double t1 = System.currentTimeMillis()/1000.0;
						synchronized (this) {
							try {
								wait(this.Time);
							} catch (InterruptedException e) {}
						}
						double t2 = System.currentTimeMillis()/1000.0;
						\return t2-t1;
					}
				}
				\return = 0;
		@ THREAD_REQUIRES:locked(this)
		@ THREAD_EFFECTS:locked(this)
		*/
		if(EW){//南北方向红灯
			if((getDir(lastP, from)==0 && getDir(from, to)==0)||(getDir(lastP, from)==0 && getDir(from, to)==2)
			||(getDir(lastP, from)==1 && getDir(from, to)==1)||(getDir(lastP, from)==1 && getDir(from, to)==3))
			{
				double t1 = System.currentTimeMillis()/1000.0;
				synchronized (this) {
					try {
						while(EW)wait(this.Time);
					} catch (InterruptedException e) {}
				}
				double t2 = System.currentTimeMillis()/1000.0;
				return t2-t1;
			}
		}
		else{
			if((getDir(lastP, from)==2 && getDir(from, to)==2)||(getDir(lastP, from)==2 && getDir(from, to)==1)
			||(getDir(lastP, from)==3 && getDir(from, to)==3)||(getDir(lastP, from)==3 && getDir(from, to)==0))
			{
				double t1 = System.currentTimeMillis()/1000.0;
				synchronized (this) {
					try {
						while(!EW)wait(this.Time);
					} catch (InterruptedException e) {}
				}
				double t2 = System.currentTimeMillis()/1000.0;
				return t2-t1;
			}
		}
		return 0;
	}
	private int getDir(myPoint from, myPoint to){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	(to.getX() - from.getX() > 0) \return = 0;//D
					(to.getX() - from.getX() < 0) \return = 1;//U
					(to.getY() - from.getY() > 0) \return = 2;//R
					(to.getY() - from.getY() < 0) \return = 3;//L
					\return = 4;
		*/
		if(to.getX() - from.getX() > 0) return 0;//D
		if(to.getX() - from.getX() < 0) return 1;//U
		if(to.getY() - from.getY() > 0) return 2;//R
		if(to.getY() - from.getY() < 0) return 3;//L
		return 4;
	}
	public boolean isEW() {
		/*
		 @EFFECTS: \result = EW;
		 */
		return EW;
	}
}
