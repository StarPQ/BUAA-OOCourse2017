import java.util.ArrayList;

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
		return Array;
	}
}
	
	
