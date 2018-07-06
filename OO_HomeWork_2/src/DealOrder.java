import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

class expHandler{
	public static void err(int code){
		System.out.println("err code : " + code);
		return;
	}
}

class floor{
	public floor(){
		up = new boolean[11];
		down = new boolean[11];
		for(int i=1;i<11;i++){
			up[i]=false;
			down[i]=false;
		}
	}
	private boolean[] up;
	private boolean[] down;
	
	public boolean judge(Order tmp){
		if(tmp.getUpDown().equals("UP")){
			if(up[tmp.getTarget()]){
				tmp.setEnable(false);
				return false;
			}
			else{
				up[tmp.getTarget()] = true;
				return true;
			}
		}
		else if(tmp.getUpDown().equals("DOWN")){
			if(down[tmp.getTarget()]){
				tmp.setEnable(false);
				return false;
			}
			else{
				down[tmp.getTarget()] = true;
				return true;
			}
		}
		else return false;
	}
	
	public void set(Order tmp) {
		if(tmp.getUpDown().equals("UP")){
			up[tmp.getTarget()] = false;
		}
		else if(tmp.getUpDown().equals("DOWN")){
			down[tmp.getTarget()] = false;
		}
		/*else if(up[tmp.getTarget()]){
			up[tmp.getTarget()] = false;
		}
		else if(down[tmp.getTarget()]){
			down[tmp.getTarget()] = false;
		}*/
	}
}

class Lift{
	public Lift(){//初始化，电梯停在一层，不动（目的地为1），用-1表示没有开关门时间
		Terminal=1;
		Position=1;
		Time=-20L;
		floor = new boolean[11];
		for(int i=1;i<11;i++){
			floor[i] = false;
		}
	}
	
	private int Terminal;//目的楼层
	private int Position;//上一次停止位置
	private long Time;	 //到达目的楼层时间
	private boolean[] floor;
	
	public void setFloor(Order tmp) {
		if(tmp.getType().equals("ER"))
			floor[tmp.getTarget()] = false;
	}
	
	public void judge(Order tmp) {
		if(floor[tmp.getTarget()]){
			tmp.setEnable(false);
		}
		else{
			floor[tmp.getTarget()] = true;
		}
	}
	
	public int getPosition() {
		return Position;
	}
	
	public int getTerminal() {
		return Terminal;
	}
	
	public long getTime() {
		return Time;
	}
	
	public void StatusRefreash(int term, long time){
		this.Position = this.Terminal;
		this.Terminal = term;
		this.Time = time;//新出发时间
		this.Time = getArrivalTime();
	}
	
	public String status(){
		if(this.Terminal>this.Position)
			return ",UP,";
		else if(this.Terminal<this.Position)
			return ",DOWN,";
		else if(this.Terminal==this.Position){
			this.Time += 10;
			return ",STILL,";
		}
		else {
			expHandler.err(6);//
			System.exit(1);
			return null;
		}
	}
	
	public long getArrivalTime(){
		if(this.Terminal>=this.Position){
			return this.Time+10+(this.Terminal-this.Position)*5;
		}
		else{
			return this.Time+10+(this.Position-this.Terminal)*5;
		}
	}
	
	public void adjust(){
		this.Time -= 10; 
	}
}

class Order{
	public Order(String str){
		this.UpDown = "";
		if(str.isEmpty()) return;
		String pattern = "\\((FR,|ER,)\\d+(,)(UP,|DOWN,)?\\d+\\)";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(str);
		if(!matcher.matches()){
			expHandler.err(8);//输入指令错误
			return;
		}
		String [] strs=str.split("[(),]");
		this.Enable = true;
		try{
			if(strs[1].equals("FR")){
				if(strs[2].equals("1")&&strs[3].equals("DOWN")){
					expHandler.err(1);//输入指令错误
					return;
				}
				else if(strs[2].equals("10")&&strs[3].equals("UP")){
					expHandler.err(1);//输入指令错误
					return;
				}
				else{
					Type = "FR";
					try{
						target = Integer.parseInt(strs[2]);
					}
					catch (Exception e) {
						expHandler.err(9);//输入指令错误
						return;
					}
					if(target>10 || target<1){
						expHandler.err(9);//输入指令错误
						return;
					}
					try{
						time = Long.parseLong(strs[4]);
					}
					catch (Exception e) {
						expHandler.err(7);//输入指令错误
						return;
					}
					if(time>Integer.MAX_VALUE){
						expHandler.err(7);//输入指令错误
						return;
					}
					time = time*10;
					UpDown = strs[3].toString();
				}
			}
			else if (strs[1].equals("ER")) {
				Type = "ER";
				try{
					target = Integer.parseInt(strs[2]);
				}
				catch (Exception e) {
					expHandler.err(9);//输入指令错误
					return;
				}
				if(target>10 || target<1){
					expHandler.err(9);//输入指令错误
					return;
				}
				try{
					time = Long.parseLong(strs[3]);
				}
				catch (Exception e) {
					expHandler.err(7);//输入指令错误
					return;
				}
				if(time>Integer.MAX_VALUE){
					expHandler.err(7);//输入指令错误
					return;
				}
				time = time*10;
				UpDown = "DNC";
			}
			else{
				expHandler.err(2);//输入指令错误
				return;
			}
		}
		catch (Exception e) {
			expHandler.err(3);//输入指令错误
			return;
		}
	}
	
	private int target;
	private long time;
	private String Type;
	private String UpDown;
	private boolean Enable;
	
	public boolean isRight(){
		if(this.UpDown.equals(""))
			return false;
		else 
			return true;
	}
	
	public String getUpDown() {
		return UpDown;
	}
	
	public boolean isEnable() {
		return Enable;
	}
	
	public void setEnable(boolean enable) {
		Enable = enable;
	}
	
	public int getTarget() {
		return target;
	}
	public long getTime() {
		return time;
	}
	public String getType() {
		return Type;
	}
}

class OrderArray{
	public OrderArray(){
		Array = new ArrayList<Order>();
		i=-1;
	}
	private ArrayList<Order> Array;
	private static int i;
	
	public void Add(Order a){
		if(!a.isRight()){
				return;
		}
		if(i==-1){
			if (a.getTime()==0) {
				Array.add(a);
				i++;
			}
			else {
				expHandler.err(4);//
				return;
			}
		}
		else{
			if(a.getTime()>=Array.get(i).getTime()){
				
				Array.add(a);
				i++;
			}
			else {
				expHandler.err(5);//
				return;
			}
		}
	}
	
	
	public void deal(Lift lift){
		Order tmp;
		floor Floor = new floor();
		
		for(int i=0;i<Array.size();i++){
			tmp = this.Array.get(i);
			if(!tmp.isEnable())continue;
			long TA = lift.getTime();
			if(tmp.getTime()>(TA+10)){
				lift.StatusRefreash(tmp.getTarget(), tmp.getTime());
				for(int j=i;j<Array.size();j++){
					Order tmp2 = this.Array.get(j);
					if(tmp2.getTime()>=(lift.getTime()+10)) break;
					if(tmp2.getType().equals("FR")&&tmp2.getType().equals(tmp.getType())){
						Floor.judge(tmp2);
					}
					else if(tmp2.getType().equals("ER")&&tmp2.getType().equals(tmp.getType())){
						lift.judge(tmp2);
					}
				}
				lift.adjust();
				String sta = lift.status();
				System.out.println("("+lift.getTerminal()+sta+lift.getTime()/10+"."+lift.getTime()%10+")");
				if(sta.equals(",STILL,"))
					lift.adjust();
				Floor.set(tmp);
				lift.setFloor(tmp);
			}
			else if(tmp.getTime()<=(TA+10)){
				lift.StatusRefreash(tmp.getTarget(), TA);
				for(int j=i;j<Array.size();j++){
					Order tmp2 = this.Array.get(j);
					if(tmp2.getTime()>=(lift.getTime()+10)) break;
					if(tmp2.getType().equals("FR")&&tmp2.getType().equals(tmp.getType())){
						Floor.judge(tmp2);
					}
					else if(tmp2.getType().equals("ER")&&tmp2.getType().equals(tmp.getType())){
						lift.judge(tmp2);
					}
				}	
				String sta = lift.status();
				System.out.println("("+lift.getTerminal()+sta+lift.getTime()/10+"."+lift.getTime()%10+")");
				if(sta.equals(",STILL,"))
					lift.adjust();
				Floor.set(tmp);
				lift.setFloor(tmp);
			}
		}
	}
}

public class DealOrder {
	public static void main(String []args){
		try{
			Scanner in = new Scanner(System.in);
			OrderArray Array = new OrderArray();
			Lift lift = new Lift();
			String str;
			while(in.hasNextLine()){
				str = in.nextLine();
				str = str.replaceAll(" ", "");
				if(str.equals("END"))break;
				Order call = new Order(str);
				Array.Add(call);
			}
			Array.deal(lift);
			in.close();
		}
		catch (Exception e) {
			expHandler.err(6);//
			return;
		}
	}
}
