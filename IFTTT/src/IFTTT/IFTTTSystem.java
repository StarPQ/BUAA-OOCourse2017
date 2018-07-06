package IFTTT;

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class IFTTTSystem {
	public static void main(String[] args){
		File f = new File("result.txt");
		if(f.exists()){
			f.delete();
		}
		File f1 = new File("count.txt");
		if(f1.exists()){
			f1.delete();
		}
		LinkedList<FileCheck> workspace = new LinkedList<FileCheck>();
		Scanner in = new Scanner(System.in);
		for(int i=0;i<8;){
			String str = null;
			if(in.hasNextLine()){
				str = in.nextLine();
			}
			
			str.replaceAll(" ", "");
			str.replaceAll("	", "");
			if(str.equals("start")/*&&i>5*/) break;
			if(Pattern.matches("IF\'[A-Z]:(/[a-zA-Z0-9]*){1,}\'(RENAMED|MODIFIED|PATHCHANGED|SIZECHANGED)\'THEN\'(RECORDSUM|RECORDDET|RECOVER)", str)){
				System.out.println("pass");
				String[] strs = str.split("'");
				System.out.println(strs[0]+" "+strs[1]+" "+strs[2]+" "+strs[3]+" "+strs[4]);
				File tmp = new File(strs[1]);
				if(!tmp.exists())continue;
				if(tmp.isDirectory()){
					FileCheck res = check(workspace, tmp);
					if(res==null){
						FileCheck Watcher = new FileCheck(tmp, Xtrans(strs[2]), Ytrans(strs[4]));
						workspace.add(Watcher);
						i++;
					}
					else{
						res.setTrigger(tmp, Xtrans(strs[2]), Ytrans(strs[4]));
					}
				}
				else{
					File tmp1 = new File(tmp.getParent());
					FileCheck res = check(workspace, tmp1);
					if(res==null){
						FileCheck Watcher = new FileCheck(tmp1, null, null);
						res.setTrigger(tmp, Xtrans(strs[2]), Ytrans(strs[4]));
						workspace.add(Watcher);
						i++;
					}
					else{
						res.setTrigger(tmp, Xtrans(strs[2]), Ytrans(strs[4]));
					}
				}
			}
		}
		for(FileCheck tmp : workspace){
			tmp.start();
		}
	}
	private static FileCheck check(LinkedList<FileCheck> workspace, File file){
		if(workspace==null)return null;
		for(int i=0;i<workspace.size();i++){
			if(workspace.get(i).getFile().getAbsolutePath().equals(file.getAbsolutePath())){
				
				return workspace.get(i);
			}
		}
		return null;
	}
	private static X Xtrans(String x){
		if(x.equals("RENAMED"))return X.RENAMED;
		else if(x.equals("MODIFIED"))return X.MODIFIED;
		else if(x.equals("PATHCHANGED"))return X.PATHCHANGED;
		else return X.SIZECHANGED;
	}
	
	private static Y Ytrans(String y){
		if(y.equals("RECORDSUMR"))return Y.RECORDSUM;
		else if(y.equals("RECORDDET"))return Y.RECORDDET;
		else return Y.RECOVER;
	}
}
/*
IF'F:/OO'RENAMED'THEN'RECOVER
IF'F:/OO'SIZECHANGED'THEN'RECORDDET
IF'F:/OO'RENAMED'THEN'RECORDDET
IF'F:/OO'MODIFIED'THEN'RECORDDET
IF'F:/OO'PATHCHANGED'THEN'RECORDDET



 */