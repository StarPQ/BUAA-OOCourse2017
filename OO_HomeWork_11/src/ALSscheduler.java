

public class ALSscheduler extends scheduler {
	//OVERVIEW: ALSscheduler this lift can pick up requests on its way to the destination.
	//不变式：this.lift!=null&&this.floor!=null&&this.Array!=null;
	public boolean repOK() {
		if(this.Array==null||this.Floor==null||this.lift==null) return false;
		return true;
	}
	public ALSscheduler(){
		/*
		 @REQUIRES: None
		 @MODIFIES: \this.lift, \this.Floor, \this.Array
		 @EFFECTS: normal behaver 
		 			initialize this scheduler
		 */
		super();
		lift = new Lift();
		Floor = new floor();
		Array = super.getArray();
		j=0;
	}
	private OrderArray Array;
	private Lift lift;
	private floor Floor;
	private int j;
	public void deal(){
		/*
		 @REQUIRES: None
		 @MODIFIES: \this.lift, \this.Floor, \this.Array, System.out
		 @EFFECTS: \all i; 0<i<Array.size;i++:
		 				!Array(i).isEnable ==> continue;
		 				futO=true ==> Array(i)=furtherOrder
		 				Array(i).isEnable&&Array(i).getTime<=lift.getTime+10 ==》 lift.StatusRefreash(Array(i).getTarget,Array(i).getTime)
		 				Array(i).isEnable&&Array(i).getTime>lift.getTime+10 ==》 lift.StatusRefreash(Array(i).getTarget,lift.getTime)
		 				\all j in lift.position to lift.target: 
		 					lift.pickup(j) ==> System.out("("+n+","+lift.toString()+","+((TA+10+5*t)/10)+"."+((TA+10+5*t)%10)+")\n"&&check(t+2)
		 					floor.pickup(j) ==> System.out("("+n+","+lift.toString()+","+((TA+10+5*t)/10)+"."+((TA+10+5*t)%10)+")\n")&&check(t+2)
		 				lift.tostring!=Still&&lift.pickup(j) ==> System.out("("+n+","+lift.toString()+","+((TA+10+5*t)/10)+"."+((TA+10+5*t)%10)+")\n"&&check(t+2)
		 				lift.tostring!=Still&&floor.pickup(j) ==> System.out("("+n+","+lift.toString()+","+((TA+10+5*t)/10)+"."+((TA+10+5*t)%10)+")\n")&&check(t+2)
		 				lift.tostring==Still==> System.out("["+tmp.getReq()+"]/("+lift.getTerminal()+","+lift.toString()+","+(lift.getTime()/10+1)+"."+lift.getTime()%10+")");
		 				futO=false
		 				furtherOrder = this.hasfurther()!=null ==> futO=true
		 */
		int i;
		boolean fpick=false, lpick=false;
		boolean furO=false;
		Order furtherOrder = new Order(),tmp;
		for(i=0; i<Array.getArray().size();i++){
			if(furO){
				tmp = furtherOrder;
				i--;
			}
			else{
				tmp = Array.getArray().get(i);
			}
			if(!tmp.isEnable())continue;
			long TA = lift.getTime();//上一次电梯到达时间
			if(tmp.getTime()>TA+10){//刷新电梯状态到执行完本次命令
				lift.StatusRefreash(tmp.getTarget(), tmp.getTime()-10);
				TA = tmp.getTime()-10;
			}
			else{
				lift.StatusRefreash(tmp.getTarget(), TA);
			}
			int t=0;
			do{
				fpick=false;
				lpick=false;
				int n = lift.n_pos(TA+10+5*t);
				//System.out.println(lift.getPosition()+" "+lift.toString()+" : pos"+n);
				if((lift.toString().equals("UP")&&n<lift.getPosition())||(lift.toString().equals("DOWN")&&n>lift.getPosition())){
					check((TA+10+5*t));
					t++;
					//System.out.println("here???");
					continue;
				}
				if(!lift.toString().equals("STILL") && lift.getPosition()!=n){
					if(fpick = Floor.pickup(n, lift.toString())){
						System.out.print("("+n+","+lift.toString()+","+((TA+10+5*t)/10)+"."+((TA+10+5*t)%10)+")\n");
					}
					if(lpick = lift.pickup(n)){
						//System.out.println(TA+10+5*t);
						System.out.print("("+n+","+lift.toString()+","+((TA+10+5*t)/10)+"."+((TA+10+5*t)%10)+")\n");
					}
				}
				if(fpick|lpick){
					check(TA+10+5*t);
					t++;
					check(TA+10+5*t);
					t++;//times of pick up +1
					lift.adjust();//record the stop
					if(fpick)Floor.set(n, lift.toString());
					if(lpick)lift.setFloor(n);
				}
				check((TA+10+5*t));
				t++;
				
			}while(TA+10+5*t<lift.getTime());
			//System.out.println("主指令："+tmp.getReq());
				if(!lift.toString().equals("STILL")){
					if(fpick = Floor.pickup(lift.getTerminal(), lift.toString())){
						System.out.print("("+lift.getTerminal()+","+lift.toString()+","+(lift.getTime()/10)+"."+(lift.getTime()%10)+")\n");
					}
					if(lpick = lift.pickup(lift.getTerminal())){
						//System.out.printlnlift.getTime();
						System.out.print("("+lift.getTerminal()+","+lift.toString()+","+(lift.getTime()/10)+"."+(lift.getTime()%10)+")\n");
					}
					if(fpick|lpick){
						check(lift.getTime());
						check(lift.getTime()+5);
						check(lift.getTime()+10);
						if(fpick)Floor.set(lift.getTerminal(), lift.toString());
						if(lpick)lift.setFloor(lift.getTerminal());
					}
				}
				if(tmp.isEnable()){
					if(lift.toString().equals("STILL")){
						System.out.println("["+tmp.getReq()+"]/("+lift.getTerminal()+","+lift.toString()+","+(lift.getTime()/10+1)+"."+lift.getTime()%10+")");
					}
					else
						System.out.println("["+tmp.getReq()+"]/("+lift.getTerminal()+","+lift.toString()+","+lift.getTime()/10+"."+lift.getTime()%10+")");

					check(lift.getTime());
					check(lift.getTime()+5);
					check(lift.getTime()+10);
					if(tmp.getType().equals("FR"))Floor.set(tmp);
					if(tmp.getType().equals("ER"))lift.setFloor(tmp);
				}
				if(furO) tmp.setEnable(false);
				furO=false;
				furtherOrder = this.hasfurther();
				if(furtherOrder!=null){
					furO=true;
				}
		}
	}
	
	private Order hasfurther(){
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: lift.toString()==UP&&\any i=lift.getTerminal();i<11;i++:lift.isfloor(i)&&i>lift.getTerminal()==>\result = lift.getOatF(i)
		 			lift.toString()==DOWN&&\any i=lift.getTerminal();i>0;i--:lift.isfloor(i)&&i<lift.getTerminal()==>\result = = lift.getOatF(i)
		 */
		//Order n= new Order();
		if(this.lift.toString().equals("UP")){
			for(int i=lift.getTerminal();i<11;i++){
				if(lift.isfloor(i)){
					if(i>lift.getTerminal())
						return lift.getOatF(i);
				}
				else if(Floor.isfloorU(i)){
					if(i>lift.getTerminal());
						//return Floor.getOU(i);
				}
			}
		}
		else if(this.lift.toString().equals("DOWN")){
			for(int i=lift.getTerminal();i>0;i--){
				if(lift.isfloor(i)){
					if(i<lift.getTerminal())
						return lift.getOatF(i);
				}
				else if(Floor.isfloorD(i)){
					if(i<lift.getTerminal());
						//return Floor.getOD(i);
				}
			}
		}
		return null;
	}
	
	public void check(long t){
		/*
		 @REQUIRES:None
		 @MODIFIES:\this.Array
		 @EFFECTS: 	\all tmp2.getTime()<=t;j++:
		 			Array(j).getType().equals("FR")&&!Floor.judge(tmp2)==>System.out.println("SAME["+tmp2.getReq()+"]");
		 			Array(j).getType().equals("ER")&&!lift.judge(tmp2)==>System.out.println("SAME["+tmp2.getReq()+"]");
		 			j++
		 */
		Order tmp2;
		while(j<Array.getArray().size()){
			//System.out.println("check "+j+"  "+t);
			tmp2 = Array.getArray().get(j);
			if(tmp2.getTime()>t)break;
			else{
				//判断该时间点的指令是否有效，无指令跳过
				if(tmp2.getType().equals("FR")){
					if(!Floor.judge(tmp2)){
						System.out.println("SAME["+tmp2.getReq()+"]");
					}
				}
				else if(tmp2.getType().equals("ER")){
					if(!lift.judge(tmp2)){
						System.out.println("SAME["+tmp2.getReq()+"]");
					}
				}
				//System.out.println(j+"-check:"+tmp2.getReq()+" "+tmp2.isEnable());
				j++;
			}
		}
	}
}
