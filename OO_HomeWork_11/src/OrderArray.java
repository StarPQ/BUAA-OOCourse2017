import java.util.ArrayList;

class OrderArray{
	//OVERVIEW:this is order array which contains requests 
	//不变式：this.Array!=null
	public boolean repOK() {
		if(this.Array==null) return false;
		return true;

	}
	public OrderArray(){
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: Array = new ArrayList<Order>();
		 */
		Array = new ArrayList<Order>();
		i=-1;
	}
	private ArrayList<Order> Array;
	private static int i;
	
	public void Add(Order a){
		/*
		 @REQUIRES:None
		 @MODIFIES:System.out
		 @EFFECTS: i==-1&&a.getTime()==0 && a.getType().equals("FR") && a.getTarget()==1 && a.getUpDown().equals("UP")==>Array.add(a)&&i++
		 			i==-1&&a.getTime()!=0 || !a.getType().equals("FR") || a.getTarget()!=1 || !a.getUpDown().equals("UP")==>System.out.println("INVALID["+a.getReq()+"]")
		 			i!=-1&&a.getTime()>=Array.get(i).getTime()==>Array.add(a)&&i++;
		 			i!=1&&a.getTime()<Array.get(i).getTime()==>System.out.println("INVALID["+a.getReq()+"]")
		 */
		if(!a.isRight()){
				return;
		}
		if(i==-1){
			if (a.getTime()==0 && a.getType().equals("FR") && a.getTarget()==1 && a.getUpDown().equals("UP") ) {
				Array.add(a);
				i++;
			}
			else {
				System.out.println("INVALID["+a.getReq()+"]");//第一个请求必须为(FR,1,UP,0)
				return;
			}
		}
		else{ 
			if(a.getTime()>=Array.get(i).getTime()){
				
				Array.add(a);
				i++;
			}
			else {
				System.out.println("INVALID["+a.getReq()+"]");//乱序请求
				return;
			}
		}
	}
	
	public ArrayList<Order> getArray() {
		/*
		 @REQUIRES:None
		 @MODIFIES:None
		 @EFFECTS: \result = \this.Array
		 */
		return Array;
	}
}
	
	
