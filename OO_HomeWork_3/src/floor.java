
class floor {
	public floor(){
		up = new boolean[11];
		down = new boolean[11];
		OatFU = new Order[11];
		OatFD = new Order[11];
		for(int i=1;i<11;i++){
			up[i]=false;
			down[i]=false;
		}
	}
	private boolean[] up;
	private boolean[] down;
	private Order[] OatFU;
	private Order[] OatFD;
	
	public boolean pickup(int n, String status){
		if(status.equals("UP")){
			if(up[n]){
				//up[n] = false;
				OatFU[n].setEnable(false);
				System.out.print("["+OatFU[n].getReq()+"]/");
				return true;
			}
			else return false;
		}
		else if(status.equals("DOWN")){
			if(down[n]){
				//down[n] = false;
				OatFD[n].setEnable(false);
				System.out.print("["+OatFD[n].getReq()+"]/");
				return true;
			}
			else return false;
		}
		else {//still情况
			/*System.out.println("原地捎带！！！改正");
			if(up[n]){
				up[n] = false;
				System.out.print("["+OatF[n]+"]/");
				return true;
			}
			else if(down[n]){
				down[n] = false;
				System.out.print("["+OatF[n]+"]/");
				return true;
			}*/
			return false;
		}
	}
	
	public boolean judge(Order tmp){
		if(tmp.getUpDown().equals("UP")){
			if(up[tmp.getTarget()]){
				tmp.setEnable(false);
				return false;
			}
			else{
				OatFU[tmp.getTarget()] = tmp;
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
				OatFD[tmp.getTarget()] = tmp;
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
	}
	
	public void set(int n, String status){
		if(n==10){
			down[10] = false;
			//System.out.println("finish order["+OatFD[n].getReq()+"]"+status);
		}
		else if(n==1){
			up[1] = false;
			//System.out.println("finish order["+OatFU[n].getReq()+"]"+status);
		}
		else if(status.equals("UP")){
			up[n] = false;
			//System.out.println("finish order["+OatFU[n].getReq()+"]"+status);
		}
		else if(status.equals("DOWN")){
			down[n] = false;
			//System.out.println("finish order["+OatFD[n].getReq()+"]"+status);
		}
	}
	
	public boolean isfloorU(int n) {
		if(up[n]) return true;
		else return false;
	}
	
	public boolean isfloorD(int n) {
		if(down[n]) return true;
		else return false;
	}
	
	/*public Order getO(int n,String s){
		if(s.equals("UP"))
			return OatFU[n];
		else if(s.equals("DOWN"))
			return OatFD[n];
		else 
	}*/
	
}