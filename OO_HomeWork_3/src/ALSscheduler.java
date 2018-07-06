

public class ALSscheduler extends scheduler {
	public ALSscheduler(){
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
			long TA = lift.getTime();//涓婁竴娆＄數姊埌杈炬椂闂�
			//System.out.println(TA);
			if(tmp.getTime()>TA+10){//鍒锋柊鐢垫鐘舵�佸埌鎵ц瀹屾湰娆″懡浠�
				lift.StatusRefreash(tmp.getTarget(), tmp.getTime()-10);
			}
			else{
				lift.StatusRefreash(tmp.getTarget(), TA);
			}
			//System.out.println("TA = "+tmp.getTime());
			//浠庝笂涓�娆″叧闂ㄥ埌鏈鍏抽棬pickup
			//if(j<i)j=i;
			int t=0;
			do{
				fpick=false;
				lpick=false;
				//System.out.println(lift.n_pos(TA+10+5*t)+"  "+(TA+10+5*t));
				int n = lift.n_pos(TA+10+5*t);
				//System.out.println(n);
				if((lift.toString().equals("UP")&&n<lift.getPosition())||(lift.toString().equals("DOWN")&&n>lift.getPosition())){
					check((TA+10+5*t));
					t++;
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
					check(TA+10+5*t);
					lift.adjust();//record the stop
					if(fpick)Floor.set(n, lift.toString());
					if(lpick)lift.setFloor(n);
				}
				check((TA+10+5*t));
				t++;
				
			}while(TA+10+5*t<lift.getTime());
			//if(!furO){
			//System.out.println("hello");
			System.out.println("MAIN"+tmp.getReq());
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
						/*check((TA+10+5*t));
						check((TA+10+5*(t+1)));//妫�鏌ュ埌杈惧悗寮�鍏抽棬鏃堕棿鍐呯殑璇锋眰
						check((TA+10+5*(t+2)));*/
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
					/*check((TA+10+5*t));
					check((TA+10+5*(t+1)));//妫�鏌ュ埌杈惧悗寮�鍏抽棬鏃堕棿鍐呯殑璇锋眰
					check((TA+10+5*(t+2)));*/
					if(tmp.getType().equals("FR"))Floor.set(tmp);
					if(tmp.getType().equals("ER"))lift.setFloor(tmp);
				}
				if(furO) tmp.setEnable(false);
				furO=false;
				//tmp.setEnable(false);
				furtherOrder = this.hasfurther();
				if(furtherOrder!=null){
					//System.out.println("go further to: "+furtherOrder.getReq());
					furO=true;
				}
			//}		
		}
	}
	
	private Order hasfurther(){
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
	
	private void check(long t){
		Order tmp2;
		while(j<Array.getArray().size()){
			//System.out.println("check "+j+"  "+t);
			tmp2 = Array.getArray().get(j);
			if(tmp2.getTime()!=t)break;
			else{
				//鍒ゆ柇璇ユ椂闂寸偣鐨勬寚浠ゆ槸鍚︽湁鏁堬紝鏃犳寚浠よ烦杩�
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
