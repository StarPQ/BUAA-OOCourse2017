package ElevSys;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class RequestQueue{
	public RequestQueue(){
		Array = new LinkedBlockingQueue<Request>(100);
		size = 0;
	}
	private LinkedBlockingQueue<Request> Array;
	private PriorityBlockingQueue<Elevaltor> queue,forcheck;
	private int size;
	
	public void makePriQueue(Elevaltor E1, Elevaltor E2, Elevaltor E3){
		this.queue = new PriorityBlockingQueue<Elevaltor>(3, cmp);
		queue.add(E1);
		queue.add(E2);
		queue.add(E3);
		this.forcheck = new PriorityBlockingQueue<Elevaltor>(3, cmp);
		forcheck.add(E1);
		forcheck.add(E2);
		forcheck.add(E3);
	}
	private Comparator<Elevaltor> cmp = new Comparator<Elevaltor>() {
		public int compare(Elevaltor e1, Elevaltor e2) {
			while(e1==null||e2==null){
				System.out.print("nullp");
			}
			return (int) (e1.getS() - e2.getS());
		}
	};
	
	public synchronized void add(Request tmp){
		this.Array.offer(tmp);
		size++;
		notifyAll();
	}
	public int getSize() {
		return size;
	}
	public Request peekHead(){
		return this.Array.peek();
	}
	public void check(){
		Elevaltor E1,E2,E3;
		E1 = forcheck.poll();
		E2 = forcheck.poll();
		E3 = forcheck.poll();
		forcheck.add(E1);
		forcheck.add(E2);
		forcheck.add(E3);
		for(Request tmp:Array){
			if(tmp.isEnable()){
				if(E1.Carry(tmp))tmp.setEnable();
				else if(E2.Carry(tmp))tmp.setEnable();
				else if(E3.Carry(tmp))tmp.setEnable();
			}
		}
	}
	
	public synchronized boolean takeHead(){
		Request tmp = null;
		do{
			tmp = Array.poll();
		}while(tmp!=null&&!tmp.isEnable());
		//if(tmp!=null)System.out.println("deal:"+tmp.getReq());
		size--;
		Elevaltor E1,E2,E3;
		E1 = queue.poll();
		E2 = queue.poll();
		E3 = queue.poll();
		queue.add(E1);queue.add(E2);queue.add(E3);
		if(tmp!=null&&tmp.isFR()){
			//System.out.println(" dealing "+tmp.getReq()+E1.getName()+" "+E1.getS()+" "+E2.getid()+" "+E2.getS()+" "+E3.getid()+" "+E3.getS());
			/*
			System.out.println("ER dealing "+tmp.getElevId()+E1.getid()+E2.getid()+E3.getid());
			if(tmp.getElevId()==E1.getid()){
				E1.Add(tmp);
				System.out.println(E1.getid()+"run");
			}else if(tmp.getElevId()==E2.getid()){
				E2.Add(tmp);(FR,6,DOWN);(FR,4,DOWN);(FR,3,DOWN);(FR,2,DOWN);(FR,5,DOWN);
				System.out.println(E2.getid()+"run");
			}else if(tmp.getElevId()==E3.getid()){
				E3.Add(tmp);
				System.out.println(E3.getid()+"run");
			}
			return null;
		}*/
		//else{
			//System.out.println(E1.isRunning()+" "+E2.isRunning()+" "+E3.isRunning());
			if(!E1.isRunning())E1.Add(tmp);
			else if(!E2.isRunning())E2.Add(tmp);
			else if(!E3.isRunning())E3.Add(tmp);
			return true;
		}else return false;
	}
}
