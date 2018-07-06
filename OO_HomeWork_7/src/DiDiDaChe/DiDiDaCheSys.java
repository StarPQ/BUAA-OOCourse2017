package DiDiDaChe;


public class DiDiDaCheSys {
	/* 
	@ REQUIRES: None
	@ MODIFIES: Gui中taxi位置信息
	@ EFFECTS: 	初始化并启动输入读取线程
				刷新gui出租车位置
				初始化控制器类
	*/
	public static void main(String[] args) {
		JackMa MaYun = new JackMa(TaxiNum, MapSize);
		InputHandler a = new InputHandler(MaYun);
		a.start();
		while(true) MaYun.refreshGUI();
	}
	public static final int TaxiNum = 100;
	public static final int MapSize = 80;
	public static final double Start = (double)System.currentTimeMillis()/1000;
}
