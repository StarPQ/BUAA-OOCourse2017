package IFTTT;

import java.io.File;

public class fileState {
	public fileState(File f){
		synchronized (f) {
			this.ParentPath = f.getParent();
			this.f = f;
			this.name = f.getName();
			this.size = f.length();
			this.lastModified = f.lastModified();
		}
	}
	public fileState(){
		this.size = (long) 0;
		this.SIZECHANGED = true;
	}
	public void setFunc(X x, Y y){
		synchronized (f) {
			this.RENAMED = x==null? false : x.equals(X.RENAMED)|this.RENAMED;
			this.MODIFIED = x==null? false : x.equals(X.MODIFIED)|this.MODIFIED;
			this.PATHCHANGED = x==null? false : x.equals(X.PATHCHANGED)|this.PATHCHANGED;
			this.SIZECHANGED = x==null? false : x.equals(X.SIZECHANGED)|this.SIZECHANGED;
			this.RECORDDET = y==null? false : y.equals(Y.RECORDDET)|this.RECORDDET;
			this.RECORDSUM = y==null? false : y.equals(Y.RECORDSUM)|this.RECORDSUM;
			this.RECOVER = y==null? false : y.equals(Y.RECOVER)|this.RECOVER;
		}
	}
	private File f;
	private String ParentPath;
	private String name;
	private Long lastModified;
	private Long size;
	private boolean RENAMED;
	private boolean MODIFIED;
	private boolean PATHCHANGED;
	private boolean SIZECHANGED;
	private boolean RECORDSUM;
	private boolean RECORDDET;
	private boolean RECOVER;
	private boolean i;
	
	
	public synchronized void refresh(fileState a){
		System.out.println(a.name+" refreshing");
			this.name = a.getName();
			this.ParentPath = a.getParentPath();
			this.size = a.getSize();
			this.lastModified = a.getLastModified();
	}
	
	public synchronized void copy(fileState a){
		this.RENAMED = a.isRENAMED();
		this.MODIFIED = a.isMODIFIED();
		this.PATHCHANGED = a.isPATHCHANGED();
		this.SIZECHANGED = a.isSIZECHANGED();
		this.RECORDDET = a.isRECORDDET();
		this.RECORDSUM = a.isRECORDSUM();
		this.RECOVER = a.isRECOVER();
		this.i = a.getI();
	}
	@Override
	public synchronized boolean equals(Object obj) {
		if(obj instanceof fileState){
			fileState u =(fileState) obj;
			String patha = this.ParentPath;
			if(patha==null){
				return super.equals(obj);
			}
			return patha.equals(u.getParentPath()) 
					&& this.name.equals(u.getName());
		}
		return super.equals(obj);
	}
	
	public void setI() {
		this.i = true;
	}
	public boolean getI() {
		if(i){
			this.i = false;
			return true;
		}
		else return false;
	}
	public boolean isMODIFIED() {
		return MODIFIED;
	}
	public boolean isPATHCHANGED() {
		return PATHCHANGED;
	}
	public boolean isRECORDDET() {
		return RECORDDET;
	}
	public boolean isRECORDSUM() {
		return RECORDSUM;
	}
	public boolean isRECOVER() {
		return RECOVER;
	}
	public boolean isRENAMED() {
		return RENAMED;
	}
	public boolean isSIZECHANGED() {
		return SIZECHANGED;
	}
	public File getFile() {
		return f;
	}
	public Long getLastModified() {
		return lastModified;
	}
	public String getParentPath() {
		return this.ParentPath;
	}
	public Long getSize() {
		return size;
	}
	public String getName() {
		return name;
	}
	public void setMODIFIED(boolean mODIFIED) {
		MODIFIED = mODIFIED;
	}
}
