package DiDiDaChe;


public class DiDiDaCheSys {
	//OVERVIEW: This is the overview of the entrance of the whole system. 
	//不变式：TaxiNum!=0&&MapSize!=0&&Start!=0
	public boolean repOK() {
		if(TaxiNum==0||MapSize==0||Start==0) return false;
		return true;
	}
	public static void main(String[] args) {
	/* 
	@ REQUIRES: None
	@ MODIFIES: gui								Gui中taxi位置信息
	@ EFFECTS: 	initialize InputHandler			 初始化并启动输入读取线程
				\all int i; i<100; i++: gui.taxi[i].position = Taxi[i[.Position刷新gui出租车位置
				initialize MaYun 				初始化控制器类
	*/
		JackMa MaYun = new JackMa(TaxiNum, MapSize);
		InputHandler a = new InputHandler(MaYun);
		a.start();
		while(true) MaYun.refreshGUI();
	}
	public static final int TaxiNum = 100;
	public static final int MapSize = 80;
	public static final double Start = (double)System.currentTimeMillis()/1000;
}
