package IFTTT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class File_operator {
	
	public synchronized void renamed(String path, String oldname, String newname){
		if(!oldname.equals(newname)){
			//System.out.println(path + File.separator + oldname);
			File oldfile = new File(path + File.separator + oldname);
			File newfile = new File(path + File.separator + newname);
			
			if (!oldfile.exists()){
				System.out.println("The oldfile doesn't exist!");
				return;
			}
			
			if(oldfile.isDirectory()){
				System.out.println("Directory can't be renamed!");
				return;
			}
			
			if(newfile.exists()){
				System.out.println("The newfile has existed already!");
				return;
			}
			
			oldfile.renameTo(newfile);
		}else{
			System.out.println("The newfile's name is as same as the oldfile's!");
		}
	}
	
	public synchronized void path_change(String oldpath, String newpath, String filename){
		if(!oldpath.equals(newpath)){
			File oldfile = new File(oldpath + File.separator + filename);
			File newfile = new File(newpath + File.separator + filename);
			
			if (!oldfile.exists()){
				System.out.println("The oldfile doesn't exist!");
				return;
			}
			
			if(oldfile.isDirectory()){
				System.out.println("Directory can't be moved!");
				return;
			}
			
			if(newfile.exists()){
				System.out.println("The newfile has existed already!");
				return;
			}
			
			oldfile.renameTo(newfile);
		}else{
			System.out.println("The newfile's name is as same as the oldfile's!");
		}
	}
	
	public synchronized void modified(String Absolutepath, String addition){
		File file = new File(Absolutepath);
		if (!file.exists()){
			System.out.println("The file doesn't exist!");
			return;
		}
		
		if (file.isDirectory()){
			System.out.println("Directory can't be modified directly!");
			return;
		}
		
		try{
			BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
			output.append(addition);
			output.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	
	public synchronized void create(String Absolutepath, int mark){
		File file = new File(Absolutepath);
		if (file.exists()){
			System.out.println("The file has existed already!");
			return;
		}
		
		if (!file.getParentFile().exists()){
			System.out.println("The parent directory of the file:"+ Absolutepath +" does not exist, the process will create it first.");
			if (!file.getParentFile().mkdirs()){
				System.out.println("Fail to create the parent directory!");
				return;
			}
		}
		try{
			if (mark == 0) file.createNewFile();
			else file.mkdir();
		}catch(Exception e){
			System.out.println("Fail to create the file!");
		}
		
	}
	
	public synchronized void delete(String Absolutepath){
		File file = new File(Absolutepath);
			if (!file.exists()){
				System.out.println("The file doesn't exist!");
				return;
			}
		
			if (file.isDirectory()){
				Dir_delete(file.getAbsolutePath());
				file.delete();
				return;
			}
		
			file.delete();
	}
	
	public synchronized void Dir_delete(String Absolutepath){
		File root = new File(Absolutepath);
			File[] files = root.listFiles();
			for (File file : files){
				if (file.isDirectory()){
					Dir_delete(file.getAbsolutePath());
					file.delete();
				}else {
					file.delete();
				}
			}
	}
}
