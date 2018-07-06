package ElevSys;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class SuperScheduler2 extends ALSscheduler{
	public SuperScheduler2(RequestQueue queue, Elevaltor a, Elevaltor b, Elevaltor c){
		super();
		E1 = a;
		E2 = b;
		E3 = c;
		E = new PriorityBlockingQueue<Elevaltor>(3, cmp);
		E.add(E1);
		E.add(E2);
		E.add(E3);
		this.queue = queue;
		queue.makePriQueue(E1,E2,E3);
	}
	private Comparator<Elevaltor> cmp = new Comparator<Elevaltor>() {
		public int compare(Elevaltor e1, Elevaltor e2) {
			while(e1==null||e2==null){
				;
			}
			return (int) (e1.getS() - e2.getS());
		}
	};
	
	private PriorityBlockingQueue<Elevaltor> E;
	private Elevaltor E1;
	private Elevaltor E2;
	private Elevaltor E3;
	private RequestQueue queue;
	
	public synchronized void pressButton(Request tmp){
		if(!E1.check(tmp)||!E2.check(tmp)||!E3.check(tmp)){
			//System.out.print(System.currentTimeMillis()+":SAME["+tmp.getReq()+",");System.out.printf("%.1f]\n",tmp.getTime());
			return;
		}
		if(tmp.isFR()){
			Elevaltor E1,E2,E3;
			E1 = E.poll();
			E2 = E.poll();
			E3 = E.poll();
			E.add(E1);
			E.add(E2);
			E.add(E3);
			if(E1.Carry(tmp));
			else if(E2.Carry(tmp));
			else if(E3.Carry(tmp));
			else queue.add(tmp);
		}
		else{
			if(tmp.getElevId()==1){
				E1.Add(tmp);
			}else if(tmp.getElevId()==2){
				E2.Add(tmp);
			}else if(tmp.getElevId()==3){
				E3.Add(tmp);
			}
			notifyAll();
		}
	}
	
	
}
