class Lift implements Lifts{
	//OVERVIEW:this is the lift hahaha ,it is dangerous DO NOT use this lift in reality
	//不变式：0<this.Terminal<=10, 0<this.Position<=10, this.floor!=null, this.OatF!=null
	public boolean repOK() {
		if(this.floor==null||this.OatF==null) return false;
		if(this.Terminal<0||this.Terminal>10) return false;
		if(this.Position<0||this.Position>10) return false;
		return true;

	}
	public Lift(){//初始化，电梯停在一层，不动（目的地为1），用-1表示没有开关门时间
		/*
		 @REQUIRES:None
		 @MODIFIES:\this
		 @EFFECTS: normal behaver
		 			Initialize lift
		 */
		Terminal=1;
		Position=1;
		Time=-10L;
		floor = new boolean[11];
		OatF = new Order[11];
		for(int i=1;i<11;i++){
			floor[i] = false;
		}
	}
	
	public int Terminal;//目的楼层
	public int Position;//上一次停止位置
	public long Time;	 //到达目的楼层时间
	private boolean[] floor;
	private Order[] OatF;
	
	public void setFloor(Order tmp) {
		/*
		 @REQUIRES:None
		 @MODIFIES:\this.floor
		 @EFFECTS: tmp.getType().equals("ER")==>floor[tmp.getTarget()] = false;
		 */
		if(tmp.getType().equals("ER"))
			floor[tmp.getTarget()] = false;
	}
	
	public void setFloor(int n){
		/*
		 @REQUIRES:None
		 @MODIFIES:\this.floor
		 @EFFECTS: floor[n] = false;
		 */
		floor[n] = false;
	}
	
	public boolean judge(Order tmp) {
		/*
		 @REQUIRES:None
		 @MODIFIES:\this.floor
		 @EFFECTS: floor[tmp.getTarget()]==>tmp.setEnable(false);
		 			!floor[tmp.getTarget()]==>OatF[tmp.getTarget()] = tmp;&&tmp.setEnable(true);
		 */
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
		/*
		 @REQUIRES:None
		 @MODIFIES:System.out,OatF
		 @EFFECTS: 	n==1||n==10==>\result=false
		 			floor[n]==>System.out.print("["+OatF[n].getReq()+"]/");&&OatF[n].setEnable(false)&&\result = false;
		 			\result = false;
		 */
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
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: \result = \this.Position
		 */
		return Position;
	}
	
	public int getTerminal() {
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: \result = \this.Terminal
		 */
		return Terminal;
	}
	
	public long getTime() {
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: \result = \this.Time
		 */
		return Time;
	}
	
	public Order getOatF(int i) {
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: \result = \this.OstF[i]
		 */
		return OatF[i];
	}
	
	public int n_pos(long t){
		/*
		 @REQUIRES: None
		 @MODIFIES: None
		 @EFFECTS: 	\this.Position>\this.Terminal==>\result = \this.Terminal+(int)(\this.Time-t)/5;
		 			\this.Terminal>\this.Position==>\result = \this.Terminal-(int)(\this.Time-t)/5;
		 			return \this.Position
		 */
		if(this.Position>this.Terminal){
			return this.Terminal+(int)(this.Time-t)/5;
		}
		else if(this.Terminal>this.Position){
			return this.Terminal-(int)(this.Time-t)/5;
		}
		else return this.Position;
	}
	
	public void StatusRefreash(int term, long time){
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: 	\this.Position = \this.Terminal;
					\this.Terminal = term;
					\this.Time = time;//新出发时间
					\this.Time = getArrivalTime();
		 */
		this.Position = this.Terminal;
		this.Terminal = term;
		this.Time = time;//新出发时间
		this.Time = getArrivalTime();
	}
	
	public String toString(){
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: 	this.Terminal>this.Position==>\result = "UP"
		 			this.Terminal<this.Position==>\result = "DOWN"
		 			this.Terminal==this.Position==>\result = "STILL"
		 */
		if(this.Terminal>this.Position)
			return "UP";
		else if(this.Terminal<this.Position)
			return "DOWN";
		else// if(this.Terminal==this.Position)
			return "STILL";
		
	}
	
	public long getArrivalTime(){
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: 	\this.Terminal>=\this.Position==>\result = \this.Time+10+(\this.Terminal-\this.Position)*5;
		 			\this.Terminal<\this.Position ==》、result = \this.Time+10+(\this.Position-\this.Terminal)*5;
		 */
		if(this.Terminal>=this.Position){
			return this.Time+10+(this.Terminal-this.Position)*5;
		}
		else{
			return this.Time+10+(this.Position-this.Terminal)*5;
		}
	}
	
	public void adjust(){
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: \this.Time+=10
		 */
		this.Time += 10; 
	}
	
	public boolean isfloor(int n) {
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: \result = \this.floor[n]
		 */
		if(floor[n]) return true;
		else return false;
	}
	
}