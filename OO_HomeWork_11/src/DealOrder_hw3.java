
import java.util.Scanner;

public class DealOrder_hw3{
	//OVERVIEW:
	//不变式：
	public void repOK() {
		

	}
	public static void main(String []args){
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		//try{
			Scanner in = new Scanner(System.in);
			ALSscheduler lift1 = new ALSscheduler();
			String str;
			while(in.hasNextLine()){
				str = in.nextLine();
				str = str.replaceAll(" ", "");
				str = str.replaceAll("	", "");
				if(str.equals("END"))break;
				Order call = new Order(str);
				lift1.add(call);
			}
			lift1.deal();
			in.close();
		//}
		//catch (Exception e) {
		//	System.out.println("输入错误！");
		//	return;
		//}
	}
}
