package IFTTT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

public class FileCheck extends Thread{
	public FileCheck(File file, X x, Y y) {
		this.file = file;
		this.files = new LinkedList<fileState>();
		this.files_re = new LinkedList<fileState>();
		this.removed = new LinkedList<fileState>();
		scan(this.file, true, this.files, x, y);
		this.mark = false;
		sA = new ScanArray(this, files, removed);
		sA.start();
	}
	
	private File file;
	private LinkedList<fileState> files;
	private LinkedList<fileState> files_re;
	private LinkedList<fileState> removed;
	private ScanArray sA;
	private boolean mark;
	private int c;
	private static long RENAMED;
	private static long MODIFIED;
	private static long PATHCHANGED;
	private static long SIZECHANGED;
	
	public void init(){
		FileCheck.MODIFIED = 0;
		FileCheck.RENAMED = 0;
		FileCheck.PATHCHANGED = 0;
		FileCheck.SIZECHANGED = 0;
	}
	public synchronized void scan(File f, boolean init, LinkedList<fileState> list, X x, Y y) {//初始化时用的递归
       if (f.isDirectory()) {//判断是否为目录
    	   File[] files = f.listFiles();
    	   if (files != null) {
    		   for (File file : files) {
    			   scan(file, init, list, x, y);
    		   }
    	   }
    	   if (init) {
    		   fileState state = new fileState(f);
    		   state.setFunc(x, y);
    		   list.add(state);
    	   }
       } else {//是文件
    	   if (init) {
    		   fileState state = new fileState(f);
    		   state.setFunc(x, y);
    		   list.add(state);
    	   }
    	   else{
	    		   		fileState a = new fileState(f);
	    		   		if(!this.files.contains(a)){
			    			a.copy(this.files.getFirst());
			    			if(a.isSIZECHANGED()){
			    				if(a.isRECORDDET()){
			    					String S =("__SIZECHANGED__DET:"+a.getFile().getAbsolutePath()+"	"+"0-->"+a.getSize());
			    					output(S);
			    				}
			    				if(a.isRECORDSUM()){
			    					System.out.println("__SIZECHANGED__SUM:"+a.getFile().getAbsolutePath()); 
			    					countSIZ();
			    				}
			    			}
	    				    System.out.println("new"+a.getName());
	    		   			this.files.addFirst(a);
							this.mark = true;
	    		   			
							for(int i=0; i<this.removed.size();i++){
						   fileState tmp  = this.removed.get(i);
						   System.out.println(tmp.getName());
			    		   if(tmp.isRENAMED()&&a.getSize().equals(tmp.getSize())&&a.getLastModified().equals(tmp.getLastModified())
			    				   &&a.getParentPath().equals(tmp.getParentPath())&&!a.getName().equals(tmp.getName()))
			    		   {
				   				DealRenamed(tmp, a);
				   				synchronized (this) {
					   				if(tmp.isRECOVER()){
					   					System.out.println("__RENAMED__RE:"+tmp.getFile().getAbsolutePath());
					   					/*if(a.isSIZECHANGED()){
						    				if(a.isRECORDDET()){
						    					String S =("__SIZECHANGED__DET:"+a.getFile().getAbsolutePath()+"	"+a.getSize()+"-->0");
						    					output(S);
						    				}
						    				if(a.isRECORDSUM()){
						    					System.out.println("__SIZECHANGED__SUM:"+a.getFile().getAbsolutePath()); 
						    					FileCheck.countSIZ();
						    				}
						    			}*/
					   					a.getFile().renameTo(tmp.getFile());
					    				a.refresh(tmp);
					   					a.copy(tmp);
					   				}
								}
					   				
				   				removed.remove(i);
				   				i--;
			    		   }
			    		   else if(tmp.isPATHCHANGED()&&a.getSize().equals(tmp.getSize())&&a.getLastModified().equals(tmp.getLastModified())
			    				   &&!a.getParentPath().equals(tmp.getParentPath())&&a.getName().equals(tmp.getName()))
			    		   {
			    			   System.out.println(tmp.getParentPath()+" "+a.getParentPath());
			    			   
			    			   if(a.getParentPath().startsWith(tmp.getParentPath())){
			    				   DealPathChanged(tmp, a);
			    				   if(tmp.isRECOVER()){
			    						System.out.println("__PATHCHANGED__RE:"+tmp.getFile().getAbsolutePath());
			    						a.getFile().renameTo(tmp.getFile());
			    						a.refresh(tmp);
			    						a.copy(tmp);
			    					}
			    			   }
			    			   removed.remove(i);
			    			   i--;
			    		   }
			    		   else{
			    			   
			    		   }
	    			   }
			   			this.mark = false;
						
    			   }
    	   }
       }
    }


	public void setTrigger(File f, X x, Y y){
		for(fileState tmp : this.files){
			if(!f.isDirectory()){
				if(tmp.getFile().equals(f)){
					System.out.println(tmp.getName());
					tmp.setFunc(x, y);
					break;
				}
			}
			else{
				tmp.setFunc(x, y);
			}
		}
	}
	public boolean isMark() {
		return mark;
	}
	private void scanArray(){
		for(int i=0;i< this.files.size();i++){
			fileState tmp = this.files.get(i);
			if(tmp.getFile().exists()){
				fileState a = new fileState(tmp.getFile());
				
				if(tmp.isSIZECHANGED()&&!tmp.getSize().equals(a.getSize())){
					DealSizeChanged(tmp, a);
				}
				if(tmp.isMODIFIED()&&!tmp.getLastModified().equals(a.getLastModified())){
					DealModifyed(tmp, a);
				}
			}
			/*if(!tmp.getFile().exists()){
				if(tmp.isSIZECHANGED())DealSizeChanged(tmp, new fileState());
				removed.add(tmp);
				tmp.setI();
				this.files.remove(tmp);
				i--;
				System.out.println("cannotfind"+tmp.getName());
			}*/
			
		}
	}
	
	

	@Override
	public void run() {
		while(true){
			if(++c==1000){
				outputC();
				c=0;
			}
			scanArray();
			scan(this.file, false, this.files, null, null);
			//removed.clear();
			clean();
		}
	}
	
	
	
	private void outputC() {
		FileWriter fs = null;
		try {
				fs = new FileWriter("count.txt", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		PrintWriter p = new PrintWriter(fs);
		String S = "MODIFIED--"+MODIFIED+" RENAMED--"+RENAMED+" PATHCHANGED--"+PATHCHANGED+" SIZECHANGED--"+SIZECHANGED;
		p.println(S);
		p.close();
	}
	private void clean(){
		for(int i=0; i<removed.size();i++){
			if(!removed.get(i).getI())removed.remove(i);
				
		}
	}
	
	private void DealModifyed(fileState a, fileState b){
		if(a.isRECORDDET()){
			String S = ("__MODOFIED__DET:"+a.getFile().getAbsolutePath()+"	"+a.getLastModified()+"-->"+b.getLastModified());
			output(S);
		}
		if(a.isRECORDSUM()){
			System.out.println("__MODOFIED__SUM:"+a.getFile().getAbsolutePath()); 
			countMOD();
		}
		a.refresh(b);
	}
	
	private void DealSizeChanged(fileState a, fileState b){
		if(a.isRECORDDET()){
			String S =("__SIZECHANGED__DET:"+a.getFile().getAbsolutePath()+"	"+a.getSize()+"-->"+b.getSize());
			output(S);
		}
		if(a.isRECORDSUM()){
			System.out.println("__SIZECHANGED__SUM:"+a.getFile().getAbsolutePath()); 
			countSIZ();
		}
		a.refresh(b);
	}
	
	private void DealRenamed(fileState a, fileState b){
		if(a.isRECORDDET()){
			String S =("__RENAMED__DET:"+a.getFile().getAbsolutePath()+"	"+a.getName()+"-->"+b.getName());
			output(S);
		}
		if(a.isRECORDSUM()){
			System.out.println("__RENAMED__SUM:"+a.getFile().getAbsolutePath()); 
			countREN();
		}
		
		b.copy(a);
	}
	
	private void DealPathChanged(fileState a, fileState b){
		if(a.isRECORDDET()){
			String S =("__PATHCHANGED__DET:"+a.getFile().getAbsolutePath()+"	"+a.getFile().getAbsolutePath()+"-->"+b.getFile().getAbsolutePath());
			output(S);
		}
		if(a.isRECORDSUM()){
			System.out.println("__PATHCHANGED__SUM:"+a.getFile().getAbsolutePath()); 
			countPAT();
		}
		
		b.copy(a);
	}
	
	public File getFile() {
		return file;
	}
	
	private synchronized void countMOD(){
		FileCheck.MODIFIED++;
	}
	
	private synchronized void countREN(){
		FileCheck.RENAMED++;
	}
	private synchronized void countPAT(){
		FileCheck.PATHCHANGED++;
	}
	public synchronized static void countSIZ(){
		FileCheck.SIZECHANGED++;
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