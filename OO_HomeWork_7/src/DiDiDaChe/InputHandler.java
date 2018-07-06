package DiDiDaChe;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InputHandler extends Thread{
	public InputHandler(JackMa MaYun) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: reqList MapreqLst
		@ EFFECTS: 	初始化请求队列，
					初始化地图改变请求队列
		*/
		this.MaYun = MaYun;
		this.reqList = new LinkedList<String>();
		this.MapreqList = new LinkedList<String>();
	}
	private JackMa MaYun;
	private LinkedList<String> reqList;
	private LinkedList<String> MapreqList;
	@Override
	public void run() {
		/* 
		@ REQUIRES: None
		@ MODIFIES: reqList MapreqLst
		@ EFFECTS: 处理读入的指令，无效输出，有效调用相应函数执行，并通过指令队列检查重复
		*/
		Scanner scanner = new Scanner(System.in);
		int T0 = 0;
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			line = line.replaceAll(" ", "");
			line = line.replaceAll("	", "");
			if(Pattern.matches("\\[CR,\\(\\+?0*\\d{1,2},\\+?0*\\d{1,2}\\),\\(\\+?0*\\d{1,2},\\+?0*\\d{1,2}\\)\\]", line)){
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
				if(x1>=DiDiDaCheSys.MapSize||x2>=DiDiDaCheSys.MapSize||y1>=DiDiDaCheSys.MapSize||y2>=DiDiDaCheSys.MapSize||(x1==x2&&y1==y2)){
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
			else if(Pattern.matches("\\[CM,\\(\\+?0*\\d{1,2},\\+?0*\\d{1,2}\\),\\(\\+?0*\\d{1,2},\\+?0*\\d{1,2}\\),\\+?(0+|0*1)\\]", line)){
				String SAVEDline = line;
				line = line.replaceAll("\\[CM,\\(", "");
				line = line.replaceAll("\\),\\(", ",");
				line = line.replaceAll("\\)", "");
				line = line.replaceAll("\\]", "");
				//System.out.println(line);
				String[] strs = line.split(",");
				int x1 = Integer.parseInt(strs[0]);
				int y1 = Integer.parseInt(strs[1]);
				int x2 = Integer.parseInt(strs[2]);
				int y2 = Integer.parseInt(strs[3]);
				int status = Integer.parseInt(strs[4]);
				if(x1>=DiDiDaCheSys.MapSize||x2>=DiDiDaCheSys.MapSize||y1>=DiDiDaCheSys.MapSize||y2>=DiDiDaCheSys.MapSize
						||(x1==x2&&y1==y2)){
					System.out.println("输入不符合要求");
					continue;
				}
				else if(!(x1-x2==1&&y1==y2)&&!(x2-x1==1&&y1==y2)&&!(x1==x2&&y1-y2==1)&&!(x1==x2&&y2-y1==1)){
					System.out.println("输入不符合要求");
					continue;
				}
				myPoint pos = new myPoint(x1, y1, 0);
				myPoint dst = new myPoint(x2, y2, 0);
				int T = (int)System.currentTimeMillis()/100;
				if(T0 == 0)T0 = T;
				if(T-T0>1){
					this.MapreqList.clear();
					T0 = T;
					System.out.println("clear");
				}
				if(!this.MapreqList.contains(SAVEDline)){
					if(this.MapreqList.size()>5){
						System.out.println(SAVEDline+"超过5条指令！");
					}
					else{
						this.MapreqList.add(SAVEDline);
						this.MaYun.MapChange(pos, dst, status);
					}
				}
				else System.out.println("SAME"+SAVEDline);
			}
			else{
				System.out.println("输入不符合要求");
			}
		}
	}
}
