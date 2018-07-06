package ElevSys;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class Elevaltor extends Thread{
	
	public Elevaltor(RequestQueue queue, long t, int i){
		this.dst = 1;
		this.position = 1;
		this.S = 0;
		this.start = t;
		this.Status = ElevStatus.STILL;
		this.Running = false;
		this.queue = queue;
		this.Array = new LinkedList<Request>();
		this.id = i;
	}
	private  int id;
	private int dst;
	private int position;
	private double time;
	private long S;
	private long start;
	private Request mainReq;
	private ElevStatus Status;
	private boolean Running;
	private RequestQueue queue;
	private LinkedList<Request> Array;
	
	@Override
	public void run(){
		System.out.println("running"+this.getName());
			while(true){
				while(this.Array.isEmpty()){
					NewMain();
				}
				if(Running){
					try {
						sleep(2999);
					} catch (InterruptedException e) {e.printStackTrace();}
					Refresh();
				}
			}
	}
	private void output(String S){
		FileWriter fs = null;
		try {
				fs = new FileWriter("result.txt", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		PrintWriter p = new PrintWriter(fs);
		p.println(S);
		p.close();
	}
	private void Refresh(){
		this.time += 3;
		double t = this.time;
		if(this.Status.equals(ElevStatus.UP)){
			this.position++;
		}
		else if(this.Status.equals(ElevStatus.DOWN)){
			this.position--;
		}
		else{
			String S = System.currentTimeMillis()+":["+Array.getFirst().getReq()+","+String.format("%.1f",Array.getFirst().getTime())
				+"]/("+this.getName()+","+this.position+","+this.Status+","+String.format("%.1f)\n",t+3);
			output(S);
			try {
				sleep(3000);
			} catch (InterruptedException e) {e.printStackTrace();}
			this.time += 3;
			Array.removeFirst();
			return;
		}
		this.S++;
		deal(t);
		if(this.position == this.dst){
			String S = System.currentTimeMillis()+":["+Array.getFirst().getReq()+","+String.format("%.1f",Array.getFirst().getTime())
				+"]/("+this.getName()+","+this.position+","+this.Status+","+String.format("%.1f)\n",t);
			output(S);
			ElevStatus buf = this.Status;
			this.Status = ElevStatus.STILL;
			try {
				sleep(5999);
			} catch (InterruptedException e) {e.printStackTrace();}
			this.time += 6;
			Array.removeFirst();
			if(Array.isEmpty())Running = false;
			else hasFurther(buf);
			
		}
		//System.out.println(this.getName()+" "+this.position);
	}
	
	private synchronized void deal(double t){
		int i,j=0;
		boolean hasStopI = false;
		boolean hasStopJ = false;
		//System.out.println("in deal");
		for(i=1;i<Array.size();i++){
			if(Array.get(i).isFR()&&Array.get(i).isUp()&&this.Status.equals(ElevStatus.UP)&&Array.get(i).getTarget()==this.position){
				
				String S = System.currentTimeMillis()+":["+Array.get(i).getReq()+","+String.format("%.1f",Array.get(i).getTime())
					+"]/("+this.getName()+","+this.position+","+this.Status+","+String.format("%.1f)\n",t);
				output(S);
				if(!hasStopJ){
					hasStopJ = true;
					j=i;
				}
				else if(!hasStopI){
					hasStopI = true;
					break;
				}
			}else
			if(Array.get(i).isFR()&&!Array.get(i).isUp()&&this.Status.equals(ElevStatus.DOWN)&&Array.get(i).getTarget()==this.position){
				
				String S = System.currentTimeMillis()+":["+Array.get(i).getReq()+","+String.format("%.1f",Array.get(i).getTime())
					+"]/("+this.getName()+","+this.position+","+this.Status+","+String.format("%.1f)\n",t);
				output(S);
				if(!hasStopJ){
					hasStopJ = true;
					j=i;
				}
				else if(!hasStopI){
					hasStopI = true;
					break;
				}
			}else
			if(!Array.get(i).isFR()&&Array.get(i).getTarget()==this.position){
				
				String S = System.currentTimeMillis()+":["+Array.get(i).getReq()+","+String.format("%.1f",Array.get(i).getTime())
					+"]/("+this.getName()+","+this.position+","+this.Status+","+String.format("%.1f)\n",t);
				output(S);
				if(!hasStopJ){
					hasStopJ = true;
					j=i;
				}
				else if(!hasStopI){
					hasStopI = true;
					break;
				}
			}
				
		}
		
		if(hasStopJ){
			if(this.position !=this.dst){ 
				try {
					sleep(5990);
				} catch (InterruptedException e) {e.printStackTrace();}
				this.time += 6;
			}
			if(hasStopI)Array.remove(i);
			Array.remove(j);
		}
		
	}
	
	private void hasFurther(ElevStatus buf){
		int j=-1;
		for(int i=0;i<Array.size();i++){
			if(!Array.get(i).isFR()&&buf.equals(ElevStatus.UP)&&Array.get(i).getTarget()>this.position){
				if(j>=0&&Array.get(i).getTarget()>Array.get(j).getTarget())
					j = i;
				else if(j<0) j = i;
			}
			else if(!Array.get(i).isFR()&&buf.equals(ElevStatus.DOWN)&&Array.get(i).getTarget()<this.position){
				if(j>=0&&Array.get(i).getTarget()<Array.get(j).getTarget())
					j = i;
				else if(j<0) j = i;
			}
		}
		if(j>=0){
			Array.addFirst(Array.remove(j));
			this.dst = Array.getFirst().getTarget();
			this.Status = buf;
			this.queue.check();
		}
		else{
			this.dst = Array.getFirst().getTarget();
			this.Status = this.dst==this.position? ElevStatus.STILL:this.dst>this.position? ElevStatus.UP:ElevStatus.DOWN;
			this.queue.check();
		}
	}
	
	public boolean check(Request tmp){//check SAME
		//System.out.println(this.getName()+"check"+tmp.getReq());
		for(int i=0;i<Array.size();i++){
			//System.out.println("--"+Array.get(i).getReq());
			if(Array.get(i).getReq().equals(tmp.getReq())){
				String S = System.currentTimeMillis()+":SAME["+tmp.getReq()+","+String.format("%.1f]\n",tmp.getTime());
				output(S);
				return false;
			}
		}
		return true;
	}
	
	public boolean Carry(Request tmp){//checkCarry
		if(this.Status.equals(ElevStatus.UP)){
			if(tmp.isUp()&&tmp.getTarget()<=this.dst&&tmp.getTarget()>this.position)return Array.add(tmp);
			else return false;
		}else
		if(this.Status.equals(ElevStatus.DOWN)){
			if(!tmp.isUp()&&tmp.getTarget()>=this.dst&&tmp.getTarget()<this.position)return Array.add(tmp);
			else return false;
		}
		else return false;
	}
	
	public void Add(Request tmp){
		this.Array.add(tmp);
		if(!this.Running&&!this.Array.isEmpty()){
			this.dst = Array.getFirst().getTarget();
			this.Status = this.dst==this.position? ElevStatus.STILL:this.dst>this.position? ElevStatus.UP:ElevStatus.DOWN;
			this.Running = true;
			this.time = (double)(System.currentTimeMillis()-start)/1000;
			this.queue.check();
			//System.out.println(this.getName());
		}
		//notifyAll();
	}
	
	private synchronized boolean NewMain(){
		//System.out.println("getMain"+this.getName());
		this.queue.takeHead();
		
		/*if(){
			this.dst = Array.getFirst().getTarget();
			this.Status = this.dst==this.position? ElevStatus.STILL:this.dst>this.position? ElevStatus.UP:ElevStatus.DOWN;
			this.Running = true;
			System.out.println(this.getName());
		}*/
		return true;
	}
	public boolean isRunning() {
		return Running;
	}
	public Request getMainReq() {
		return mainReq;
	}
	public long getS() {
		return S;
	}
	public int getid() {
		return id;
	}
}
