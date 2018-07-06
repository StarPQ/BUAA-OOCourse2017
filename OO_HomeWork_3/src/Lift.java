class Lift implements Lifts{
	public Lift(){//初始化，电梯停在一层，不动（目的地为1），用-1表示没有开关门时间
		Terminal=1;
		Position=1;
		Time=-10L;
		floor = new boolean[11];
		OatF = new Order[11];
		for(int i=1;i<11;i++){
			floor[i] = false;
		}
	}
	
	private int Terminal;//目的楼层
	private int Position;//上一次停止位置
	private long Time;	 //到达目的楼层时间
	private boolean[] floor;
	private Order[] OatF;
	
	public void setFloor(Order tmp) {
		if(tmp.getType().equals("ER"))
			floor[tmp.getTarget()] = false;
	}
	
	public void setFloor(int n){
		floor[n] = false;
	}
	
	public boolean judge(Order tmp) {
		if(floor[tmp.getTarget()]){
			//System.out.println("["+tmp.getReq()+"]无效指令");
			tmp.setEnable(false);
			return false;
		}
		else{
			OatF[tmp.getTarget()] = tmp;
			floor[tmp.getTarget()] = true;
			return true;
		}
	}
	
	public boolean pickup(int n){
		if(n==1||n==10){
			return false;
		}
		else if(floor[n]){
			System.out.print("["+OatF[n].getReq()+"]/");
			OatF[n].setEnable(false);
			//floor[n] = false;
			return true;
		}
		else return false;
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
	
	public Order getOatF(int i) {
		return OatF[i];
	}
	
	public int n_pos(long t){
		if(this.Position>this.Terminal){
			return this.Terminal+(int)(this.Time-t)/5;
		}
		else if(this.Terminal>this.Position){
			return this.Terminal-(int)(this.Time-t)/5;
		}
		else return this.Position;
	}
	
	public void StatusRefreash(int term, long time){
		this.Position = this.Terminal;
		this.Terminal = term;
		this.Time = time;//新出发时间
		this.Time = getArrivalTime();
	}
	
	public String toString(){
		if(this.Terminal>this.Position)
			return "UP";
		else if(this.Terminal<this.Position)
			return "DOWN";
		else if(this.Terminal==this.Position){
			return "STILL";
		}
		else {
			System.out.println("输入错误");
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
		this.Time += 10; 
	}
	
	public boolean isfloor(int n) {
		if(floor[n]) return true;
		else return false;
	}
	
}