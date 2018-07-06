package DiDiDaRen;


public class DiDiDaRenSys {
	public static void main(String[] args) {
		JackMa MaYun = new JackMa(TaxiNum, MapSize);
		InputHandler a = new InputHandler(MaYun);
		a.start();
		while(true) MaYun.refresh();
	}
	public static final int TaxiNum = 100;
	public static final int MapSize = 80;
	public static final double Start = (double)System.currentTimeMillis()/1000;
}
