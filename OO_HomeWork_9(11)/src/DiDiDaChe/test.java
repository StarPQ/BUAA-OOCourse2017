package DiDiDaChe;

import java.util.ListIterator;

public class test extends Thread{
	//overview: zhe shi test lei ne~ test for the Iterator.
	//不变式：T!=null;
	public boolean repOK() {
		if(this.T==null)return false;
		return true;
	}
	public test(Taxi T[]) {
		/*
		 @REQUEST:None
		 @MODIFY:\this.T
		 @EFFECTS: \this.T = T;
		*/
		this.T = T;
	}
	private Taxi T[];
	@Override
	public void run() {
		/*
		 @REQUEST:None
		 @MODIFY:System.out
		 @EFFECTS: System.out("what you want in Iterator")
		*/
		while(true){
			try {
				sleep(1000);
			} catch (InterruptedException e) {}
			/*for(int i=0;i<100;i++){
				if(getReqIter(i)!=null&&getReqIter(i).hasNext())
					System.out.println(i+"--"+getReqIter(i).next());
			}*/
		}
	}
	private ListIterator<myPoint> getRouteIter(int n, int m) {
		/*
		 @REQUEST:None
		 @MODIFY:System.out
		 @EFFECTS: 	!T[n] instanceof superTaxi ==> \result = null; System.out.println("非超级出租车");
		 			t.RouteRecord(m)==null ==> \result = null; System.out.println("不存在序列");
		 			T[n] instanceof superTaxi&&t.RouteRecord(m)!=null ==> \result = t.RouteRecord(m);
		*/
		if(T[n] instanceof superTaxi){
			superTaxi t = (superTaxi)T[n];
			if(t.RouteRecord(m)==null) System.out.println("不存在序列");
			else return t.RouteRecord(m);
		}
		System.out.println("非超级出租车");
		return null;
	}
	private ListIterator<String> getReqIter(int n) {
		/*
		 @REQUEST:None
		 @MODIFY:System.out
		 @EFFECTS: 	!T[n] instanceof superTaxi ==> \result = null; System.out.println("非超级出租车");
		 			t.ReqRecord(m)==null ==> \result = null; System.out.println("不存在序列");
		 			T[n] instanceof superTaxi&&t.RouteRecord(m)!=null ==> \result = t.ReqRecord();
		*/
		if(T[n] instanceof superTaxi){
			superTaxi t = (superTaxi)T[n];
			if(t.ReqRecord()==null) System.out.println("不存在序列");
			else return t.ReqRecord();
		}
		System.out.println("非超级出租车");
		return null;
	}
}
