package IFTTT;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class ScanArray extends Thread{
	
	public ScanArray(FileCheck Check, LinkedList<fileState> files, LinkedList<fileState> removed) {
		this.check = Check;
		this.files = files;
		this.removed = removed;
	}
	
	private FileCheck check;
	private LinkedList<fileState> files;
	private LinkedList<fileState> removed;
	@Override
	public void run() {
		while(true){
			synchronized (this) {
				while(check.isMark()){
					try {
						wait(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			scanArray();
		}
	}
	private  void scanArray(){
		synchronized (removed) {
			for(int i=0;i< this.files.size();i++){
				fileState tmp = this.files.get(i);
				if(!tmp.getFile().exists()){
					removed.add(tmp);
					this.files.remove(tmp);
					tmp.setI();
					if(tmp.isSIZECHANGED()){
	    				if(tmp.isRECORDDET()){
	    					String S =("__SIZECHANGED__DET:"+tmp.getFile().getAbsolutePath()+"	"+tmp.getSize()+"-->0");
	    					output(S);
	    				}
	    				if(tmp.isRECORDSUM()){
	    					System.out.println("__SIZECHANGED__SUM:"+tmp.getFile().getAbsolutePath()); 
	    					FileCheck.countSIZ();
	    				}
	    			}
					i--;
					System.out.println("cannotfind"+tmp.getName());
				}
			}
		}
			
	}
	
	private void output(String S){
		FileWriter fs = null;
		try {
				fs = new FileWriter("result.txt", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		PrintWriter p = new PrintWriter(fs);
		p.println(S);
		p.close();
	}
	
}
