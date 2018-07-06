package DiDiDaChe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;

public class Map {
	public Map(int MapSize){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Map[][]
		@ EFFECTS: 读取地图文件，有问题报错
		*/
		this.MapSize = MapSize;
		Scanner in = new Scanner(System.in);
		Map = new myPoint[this.MapSize+1][this.MapSize+1];
		for(int i=0; i<this.MapSize; i++){
			for(int j=0; j<this.MapSize; j++){
				Map[i][j] = new myPoint(i, j, 0);
			}
		}
		System.out.println("请输入地图文件存储路径");
		if(in.hasNextLine()){
			if(readTxtFile(in.nextLine())==0) System.out.println("地图创建成功");
			else {
				System.out.println("地图创建失败，请重新启动程序");
				System.exit(0);
			}
		}
	}
	private final int MapSize;
	private myPoint[][] Map;
	
	private int readTxtFile(String filePath){//读取地图文件
		/* 
		@ REQUIRES: None
		@ MODIFIES: Map[][]
		@ EFFECTS: 读取地图文件，找不到文件或有问题报错
		*/
		try {
			String encoding="UTF-8";
			File file=new File(filePath);
			if(file.isFile() && file.exists()){ //判断文件是否存在
				InputStreamReader read = new InputStreamReader(	new FileInputStream(file),encoding);//考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				if(loadMap(bufferedReader)<0)
					return -1;
				read.close();
				return 0;
			}else{
				System.out.println("找不到指定的文件");
				return -1;
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
			return -1;
		}
	}
	
	private int loadMap(BufferedReader Reader) throws IOException{
		/* 
		@ REQUIRES: 地图文件存在
		@ MODIFIES: Map[][]
		@ EFFECTS: 读取地图文件，有问题报错
		*/
		String lineTxt = null;
		for(int lineNumber = 0; lineNumber < this.MapSize; lineNumber++){
			lineTxt = Reader.readLine();
			lineTxt = lineTxt.replaceAll(" ", "");
			lineTxt = lineTxt.replaceAll("	", "");
			if(lineTxt.length()!=this.MapSize){
				System.out.println("第"+lineNumber+"行长度不等于"+this.MapSize);
				return -1;
			}
			for(int i = 0; i < this.MapSize; i++){
				if(lineTxt.charAt(i)=='1'&&i!=this.MapSize-1){
					Map[lineNumber][i].setConnection(1);
					Map[lineNumber][i].setR(true);
					Map[lineNumber][i+1].setL(true);
				}
				else if(lineTxt.charAt(i)=='2'&&lineNumber!=this.MapSize-1){
					Map[lineNumber][i].setConnection(2);
					Map[lineNumber][i].setD(true);
					Map[lineNumber+1][i].setU(true);
				}
				else if(lineTxt.charAt(i)=='3'&&i!=this.MapSize-1&&lineNumber!=this.MapSize-1){
					Map[lineNumber][i].setConnection(3);
					Map[lineNumber][i].setD(true);
					Map[lineNumber][i].setR(true);
					Map[lineNumber+1][i].setU(true);
					Map[lineNumber][i+1].setL(true);
				}
				else if(lineTxt.charAt(i)=='0');
				else{
					System.out.println("第"+lineNumber+"行第"+i+"个字符不符合要求");
					return -1;
				}
			}
		}
		if(Reader.readLine()!=null){
			System.out.println("超过80行");
			return -1;
		}
		return 0;
	}
	
	public myPoint getNextPos(myPoint Pos) {
		/* 
		@ REQUIRES: None
		@ MODIFIES: None
		@ EFFECTS: 返回流量最小的随机邻接点
		*/
		return Map[Pos.getX()][Pos.getY()].getNextPos();
	}
	
	public LinkedList<myPoint> getChild(int x, int y) {
		/* 
		@ REQUIRES: p在地图内
		@ MODIFIES: None
		@ EFFECTS: 获取邻接点集合
		*/
		return Map[x][y].getChild();
	}
///////////////////////////////////////////////////////////////////////////////////
	public void guiLoadMap(TaxiGUI gui){
		/* 
		@ REQUIRES: None
		@ MODIFIES: gui
		@ EFFECTS: 向gui载入地图
		*/
		gui.LoadMap(Map, MapSize);
	}
///////////////////////////////////////////////////////////////////////////////////
	public int[][] getMat(int x, int y){
		/* 
		@ REQUIRES: p(x,y)在地图范围内
		@ MODIFIES: None
		@ EFFECTS: 获取路线距离矩阵
		*/
		int[][] dis= new int[80][80];
		boolean[][] visited = new boolean[80][80];
		LinkedList<myPoint> Array = new LinkedList<myPoint>();
		Array.add(Map[x][y]);
		visited[x][y] = true;
		dis[x][y] = 0;
		for(int i=1; i<6400;){
			myPoint parent = Array.pollFirst();
			//System.out.println(parent.getX()+","+parent.getY()+":"+parent.getChild().size());
			for(myPoint tmp: parent.getChild()){
				if(visited[tmp.getX()][tmp.getY()]==false){
					visited[tmp.getX()][tmp.getY()]=true;
					Array.add(Map[tmp.getX()][tmp.getY()]);
					dis[tmp.getX()][tmp.getY()] = dis[parent.getX()][parent.getY()]+parent.getStreamValue(tmp);
					i++;
				}
			}
		}
		return dis;
	}
//////////////////////////////////////////////////////////////////////////////////
	public void setMapStatus(myPoint from, myPoint to, int status){
		/* 
		@ REQUIRES: from，to为地图范围内邻接点，status为0或1
		@ MODIFIES: Map[][]的连接关系
		@ EFFECTS: 	如果删除已存在边，或增加原不存在边，则改变Map连接关系
		*/
		Map[from.getX()][from.getY()].connectChange(to, status);
		Map[to.getX()][to.getY()].connectChange(from, status);
	}

	public void ChangeStreamValue(myPoint from, myPoint to){
		/* 
		@ REQUIRES: from，to为邻接点
		@ MODIFIES: from和to的流量
		@ EFFECTS: 改变from和to的流量
		*/
		Map[from.getX()][from.getY()].ChangeStreamValue(to);
		Map[to.getX()][to.getY()].ChangeStreamValue(from);
	}
///////////////////////////////////////////////////////////////////////////////////////
	public void refresh(){
		/* 
		@ REQUIRES: None
		@ MODIFIES: Map[][]
		@ EFFECTS: 刷新地图流量
		*/
		synchronized (Map) {
			for(int i=0; i<80; i++){
				for(int j=0;j<80;j++){
					this.Map[i][j].refresh();
				}
			}
		}
	}

}
