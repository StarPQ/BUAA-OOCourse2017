package old;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;

import IFTTT.X;
import IFTTT.Y;

public class WatchFile extends Thread
{
    public WatchFile(String Path, X x, Y y) throws IOException{
    	 String filePath = Path;//("F:\\其他文件\\b.txt");
    	 this.x = x;
    	 this.y = y;
    	 file = new File(filePath);
         // 获取文件系统的WatchService对象
    	 if(!file.exists()){
    		 System.out.println("file do not exists");
    		 return;
    	 }
    	 watchService = FileSystems.getDefault().newWatchService();
    	 if(file.isDirectory()){
    		 Paths.get(file.getPath()).register(watchService 
                 , StandardWatchEventKinds.ENTRY_CREATE
                 , StandardWatchEventKinds.ENTRY_MODIFY
                 , StandardWatchEventKinds.ENTRY_DELETE);
    	 }else{
    		 Paths.get(file.getPath()+"\\..").register(watchService 
                     , StandardWatchEventKinds.ENTRY_CREATE
                     , StandardWatchEventKinds.ENTRY_MODIFY
                     , StandardWatchEventKinds.ENTRY_DELETE);
    	 }
    	/* LinkedList<File> fList = new LinkedList<File>();
         fList.addLast(file);
         while (fList.size() > 0 ) {
             File f = fList.removeFirst();
             if(f.listFiles() == null)
              continue;*/
             for(File file2 : file.listFiles()){
                     if (!file2.isHidden()&&file2.isDirectory()){//下一级目录
                     //fList.addLast(file2);
                     //依次注册子目录
                     WatchFile t = new WatchFile(file2.getAbsolutePath(), x, y);
                     t.start();
                     /*Paths.get(file2.getAbsolutePath()).register(watchService 
                             , StandardWatchEventKinds.ENTRY_CREATE
                             , StandardWatchEventKinds.ENTRY_MODIFY
                             , StandardWatchEventKinds.ENTRY_DELETE);*/
                 }
             }
         //}
    }
    private File file;
    private WatchService watchService;
    private X x;
    private Y y;
    
    
    @Override
    public  void run(){
    	while(true)
        {
            System.out.println("running"+this.file.getPath());
        	// 获取下一个文件改动事件
            WatchKey key = null;
			try {
				key = watchService.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("get!");
            for (WatchEvent<?> event : key.pollEvents()) 
            {
            	System.out.println(this.file.getPath()+"\\"+event.context() +" --> " + event.kind());
            }
            // 重设WatchKey
            boolean valid = key.reset();
            // 如果重设失败，退出监听
            if (!valid) 
            {
                break;
            }
        }
    }
    
    
}


