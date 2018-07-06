package DiDiDaRen;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InputHandler extends Thread{
	public InputHandler(JackMa MaYun) {
		this.MaYun = MaYun;
		this.reqList = new LinkedList<String>();
	}
	private JackMa MaYun;
	private LinkedList<String> reqList;
	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		int T0 = 0;
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			line = line.replaceAll(" ", "");
			line = line.replaceAll("	", "");
			if(Pattern.matches("\\[CR,\\(0*\\d{1,2},0*\\d{1,2}\\),\\(0*\\d{1,2},0*\\d{1,2}\\)\\]", line)){
				String SAVEDline = line;
				line = line.replaceAll("\\[CR,\\(", "");
				line = line.replaceAll("\\),\\(", ",");
				line = line.replaceAll("\\)\\]", "");
				//System.out.println(line);
				String[] strs = line.split(",");
				int x1 = Integer.parseInt(strs[0]);
				int y1 = Integer.parseInt(strs[1]);
				int x2 = Integer.parseInt(strs[2]);
				int y2 = Integer.parseInt(strs[3]);
				if(x1>=DiDiDaRenSys.MapSize||x2>=DiDiDaRenSys.MapSize||y1>=DiDiDaRenSys.MapSize||y2>=DiDiDaRenSys.MapSize||(x1==x2&&y1==y2)){
					System.out.println("输入不符合要求");
					continue;
				}
				myPoint pos = new myPoint(x1, y1, 0);
				myPoint dst = new myPoint(x2, y2, 0);
				int T = (int)System.currentTimeMillis()/100;
				if(T0 == 0)T0 = T;
				if(T-T0>1){
					this.reqList.clear();
					T0 = T;
					System.out.println("clear");
				}
				Request tmp = new Request(pos, dst, SAVEDline, T, MaYun);
				if(!this.reqList.contains(SAVEDline)){
					this.reqList.add(SAVEDline);
					this.MaYun.assignReq(tmp);
				}
				else System.out.println("SAME"+SAVEDline);
			}
			else{
				System.out.println("输入不符合要求");
			}
		}
	}
}
