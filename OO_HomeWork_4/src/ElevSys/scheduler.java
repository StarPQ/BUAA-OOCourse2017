package ElevSys;

public class scheduler {
	public scheduler(){
		i=0;
		Array = new OrderArray();
		lift = new Lift();
		Floor = new floor();
	}
	private int i;
	private OrderArray Array;
	private Lift lift;
	private floor Floor;
	public void deal(){
		Order tmp;
		
		int j=0;
		for(i=0;i<Array.getArray().size();i++){
			tmp = Array.getArray().get(i);
			if(!tmp.isEnable())continue;
			long TA = lift.getTime();//上一次电梯到达时间
			if(tmp.getTime()>(TA+10)){//关门后发出的指令
				lift.StatusRefreash(tmp.getTarget(), tmp.getTime());
				for(;j<Array.getArray().size();j++){
					Order tmp2 = Array.getArray().get(j);
					if(tmp2.getTime()<tmp.getTime())continue;
					if(tmp2.getTime()>=(lift.getTime())) break;
					if(tmp2.getType().equals("FR")){
						Floor.judge(tmp2);
					}
					else if(tmp2.getType().equals("ER")){
						lift.judge(tmp2);
					}
				}
				lift.adjust();
				String sta = lift.toString();
				System.out.println("("+lift.getTerminal()+sta+lift.getTime()/10+"."+lift.getTime()%10+")");
				if(sta.equals(",STILL,"))
					lift.adjust();
				Floor.set(tmp);
				lift.setFloor(tmp);
			}
			else if(tmp.getTime()<=(TA+10)){//关门前发出的指令
				lift.StatusRefreash(tmp.getTarget(), TA);
				for(;j<Array.getArray().size();j++){
					Order tmp2 = Array.getArray().get(j);
					if(tmp2.getTime()<tmp.getTime())continue;
					if(tmp2.getTime()>=(lift.getTime()+10)) break;
					if(tmp2.getType().equals("FR")){
						if(!Floor.judge(tmp2)){
							System.out.println("["+tmp.getReq()+"]无效指令");
						}
					}
					else if(tmp2.getType().equals("ER")){
						if(!lift.judge(tmp2)){
							System.out.println("["+tmp.getReq()+"]无效指令");
						}
					}
				}	
				String sta = lift.toString();
				System.out.println("("+lift.getTerminal()+sta+lift.getTime()/10+"."+lift.getTime()%10+")");
				if(sta.equals(",STILL,"))
					lift.adjust();
				Floor.set(tmp);
				lift.setFloor(tmp);
			}
		}
	}
	
	public OrderArray getArray() {
		return Array;
	}
	
	public  void add(Order O2ADD) {
		this.Array.Add(O2ADD);
	}
}
