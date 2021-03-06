import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Order{
	//OVERVIEW:
	//不变式：
	public void repOK() {
		

	}
	
	public Order(){
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		
	}
	
	public Order(String str){
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		this.UpDown = "";
		if(str.isEmpty()) return;
		String pattern = "\\((FR,|ER,)?(\\+)?\\d+(,)(UP,|DOWN,)?(\\+)?\\d+\\)";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(str);
		if(!matcher.matches()){
			System.out.println("INVALID["+str+"]");//输入指令错误
			return;
		}
		try{
			str.replaceAll("+", "");
		}catch(Exception e){};
		String [] strs=str.split("[(),]");
		this.Enable = true;
		try{
			if(strs[1].equals("FR")){
				if(strs[2].equals("1")&&strs[3].equals("DOWN")){
					System.out.println("INVALID["+str+"]");//输入指令错误
					return;
				}
				else if(strs[2].equals("10")&&strs[3].equals("UP")){
					System.out.println("INVALID["+str+"]");//输入指令错误
					return;
				}
				else{
					Type = "FR";
					try{
						target = Integer.parseInt(strs[2]);
					}
					catch (Exception e) {
						System.out.println("INVALID["+str+"]");//输入指令错误
						return;
					}
					if(target>10 || target<1){
						System.out.println("INVALID["+str+"]");//输入指令错误
						return;
					}
					try{
						time = Long.parseLong(strs[4]);
					}
					catch (Exception e) {
						System.out.println("INVALID["+str+"]");//输入指令错误
						return;
					}
					if(time>Integer.MAX_VALUE){
						System.out.println("INVALID["+str+"]");//输入指令错误
						return;
					}
					time = time*10;
					req = str.toString();
					UpDown = strs[3].toString();
				}
			}
			else if (strs[1].equals("ER")) {
				Type = "ER";
				try{
					target = Integer.parseInt(strs[2]);
				}
				catch (Exception e) {
					System.out.println("INVALID["+str+"]");//输入指令错误
					return;
				}
				if(target>10 || target<1){
					System.out.println("INVALID["+str+"]");//输入指令错误
					return;
				}
				try{
					time = Long.parseLong(strs[3]);
				}
				catch (Exception e) {
					System.out.println("INVALID["+str+"]");//输入指令错误
					return;
				}
				if(time>Integer.MAX_VALUE){
					System.out.println("INVALID["+str+"]");//输入指令错误
					return;
				}
				time = time*10;
				req = str.toString();
				UpDown = "DNC";
			}
			else{
				System.out.println("INVALID["+str+"]");//输入指令错误
				return;
			}
		}
		catch (Exception e) {
			System.out.println("INVALID["+str+"]");//输入指令错误
			return;
		}
	}
	
	private String req;
	private int target;
	private long time;
	private String Type;
	private String UpDown;
	private boolean Enable;
	
	public boolean isRight(){
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		if(this.UpDown.equals(""))
			return false;
		else 
			return true;
	}
	
	public String getReq() {
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		return req;
	}
	
	public String getUpDown() {
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		return UpDown;
	}
	
	public boolean isEnable() {
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		return Enable;
	}
	
	public void setEnable(boolean enable) {
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		Enable = enable;
	}
	
	public int getTarget() {
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		return target;
	}
	public long getTime() {
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		return time;
	}
	public String getType() {
		/*
		 @REQUIRES:
		 @MODIFIES:
		 @EFFECTS: 
		 */
		return Type;
	}
}