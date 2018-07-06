package IFTTT;

import java.io.IOException;

public class File extends java.io.File{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public File(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public synchronized long length() {
		// TODO Auto-generated method stub
		return super.length();
	}
	
	@Override
	public synchronized java.io.File[] listFiles() {
		// TODO Auto-generated method stub
		return super.listFiles();
	}
	
	@Override
	public synchronized java.io.File getParentFile() {
		// TODO Auto-generated method stub
		return super.getParentFile();
	}
	
	@Override
	public synchronized String getAbsolutePath() {
		// TODO Auto-generated method stub
		return super.getAbsolutePath();
	}
	
	@Override
	public synchronized boolean renameTo(java.io.File arg0) {
		// TODO Auto-generated method stub
		if(arg0.isDirectory())return false;
		else return super.renameTo(arg0);
	}
	
	@Override
	public synchronized boolean createNewFile() throws IOException {
		// TODO Auto-generated method stub
		return super.createNewFile();
	}
	
	@Override
	public synchronized boolean delete() {
		// TODO Auto-generated method stub
		return super.delete();
	}
	
	@Override
	public synchronized boolean setLastModified(long time) {
		// TODO Auto-generated method stub
		return super.setLastModified(time);
	}
	
	@Override
	public synchronized boolean mkdir() {
		// TODO Auto-generated method stub
		return super.mkdir();
	}
	
	@Override
	public synchronized boolean exists() {
		// TODO Auto-generated method stub
		return super.exists();
	}
	
	@Override
	public synchronized boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
	@Override
	public synchronized boolean isDirectory() {
		// TODO Auto-generated method stub
		return super.isDirectory();
	}

}
