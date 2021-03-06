package DiDiDaChe;

public class Timer extends Thread{
	public Timer(JackMa MaYun){
		/* 
		@ REQUIRES: None
		@ MODIFIES: MaYun
		@ EFFECTS: \this.MaYun = MaYun
		*/
		this.MaYun = MaYun;
	}
	private JackMa MaYun;
	@Override
	public void run() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: Map中各点流量
		@ EFFECTS: 	计时并唤醒所有等待的线程
					每0.2s刷新流量值
		@ THREAD_REQUIRES:locked(this)
		@ THREAD_EFFECTS:locked(this)
		*/
		int T=0;
		while(true){
			try {
				sleep(100);
			} catch (InterruptedException e) {}
			T+=1;
			if(T==2){
				this.MaYun.refreshMap();
				T=0;
			}
			synchronized (this) {
				notifyAll();
			}
		}
	}
	public void Wait(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 	使访问本方法的线程等待
					直到被run唤醒
		@ THREAD_REQUIRES:locked(this)
		@ THREAD_EFFECTS:locked(this)
		*/
		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
