package ElevSys;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Request extends Thread{
	public Request(SuperScheduler a, long start){
		super("request");
		superScheduler = a;
		this.start = start;
	}
	
	public Request(String str, double t){
		this.req = str;
		this.time = t;
		MakeNewRequest(str);
	}
	
	private String req;
	private int target;
	private int ElevId;
	private double time;
	private long start;
	private boolean FR;
	private boolean Up;
	private boolean Enable;
	private SuperScheduler superScheduler;
	
	public boolean isEnable() {
		return Enable;
	}
	
	public boolean isUp() {
		return Up;
	}
	
	public boolean isFR() {
		return FR;
	}
	
	public String getReq() {
		return req;
	}
	
	public int getTarget() {
		return target;
	}
	
	public double getTime() {
		return time;
	}
	
	public int getElevId() {
		return ElevId;
	}
	public void setEnable() {
		Enable = false;
	}
	
	public void run(){
		Scanner in = new Scanner(System.in);
		
			while(true){
				try{
					if(in.hasNextLine()){
							ReadNewReq(in);
					}
				}catch(Exception e){}
				//superScheduler.scan();
			}
		
	}
	
	private void output(String S){
		FileWriter fs = null;
		try {
				fs = new FileWriter("result.txt", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		PrintWriter p = new PrintWriter(fs);
		p.println(S);
		p.close();
	}
	
	private void ReadNewReq(Scanner in) throws FileNotFoundException{
		
		String str = new String();
		str = in.nextLine();
		double t = (double)(System.currentTimeMillis()-start)/1000;
		///System.out.println(str);
		str.replaceAll(" ", "");
		str.replaceAll("	", "");
		if(str.isEmpty())return;
		String[] strs = str.split("[;]");
		for(int i=0, j=0; i<strs.length;i++){
			if(j>=10){
				String S = System.currentTimeMillis()+":INVALID["+strs[i]+","+String.format("%.1f]", t);
				output(S);
				continue;
			}
			Request tmp = new Request(strs[i], t);
			if(tmp.isEnable()){
				j++;
				superScheduler.pressButton(tmp);
				try {
					sleep(6);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				String S = System.currentTimeMillis()+":INVALID["+strs[i]+","+String.format("%.1f]", t);
				output(S);
			}
		}
	}
	
	private void MakeNewRequest(String str){
		//System.out.println(str+"	"+Pattern.matches("\\(FR,\\+?\\d{1,2}(,UP|,DOWN)\\)", str)+"	"+Pattern.matches("\\(ER,\\#\\d{1,1},\\+?\\d{1,2}\\)", str));
		if(Pattern.matches("\\(FR,\\+?0{0,}\\d{1,2}(,UP|,DOWN)\\)", str)){
			//System.out.println("FR"+str);
			this.FR = true;
			try{
				str.replaceAll("+", "");
			}catch(Exception e){};
			String [] strs=str.split("[(),]");
			this.Enable = true;
			if(strs[2].equals("1")&&strs[3].equals("DOWN")){
				this.Enable = false;
			}
			else if(strs[2].equals("20")&&strs[3].equals("UP")){
				this.Enable = false;
			}
			this.target = Integer.parseInt(strs[2]);
			if(this.target>20||this.target<1){
				this.Enable = false;
			}
			this.Up = strs[3].equals("UP");
			//System.out.println(this.target);
		}
		if(Pattern.matches("\\(ER,#0{0,}\\d{1,1},\\+?0{0,}\\d{1,2}\\)", str)){
			//System.out.println("ER"+str);
			this.FR = false;
			try{
				str.replace("+", "");
			}catch(Exception e){};
			try{
				str.replace("#", "");
			}catch(Exception e){};
			String [] strs=str.split("[(),#]");
			this.Enable = true;
			this.ElevId = Integer.parseInt(strs[3]);
			this.target = Integer.parseInt(strs[4]);
			if(this.target>20||this.target<1){
				this.Enable = false;
			}
			this.Up = strs[3].equals("UP");
		}
	}
	
	
}
