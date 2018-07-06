
public class Poly {
	public Poly(){
		base = null;
		TopExponent = 0;
	}
	private int TopExponent;
	private int[] base;
	public int deal(char tmp, int status){
		if(tmp=='{'){
			if(status==0) return 1;
			else return -1;
		}
		else if(tmp=='('){
			if(status==1) return 2;
			else return -1;
		}
		else if(tmp==','){
			if(status==3) return 4;
			else if(status==6) return 1;
			else return -1;
		}
		else if(tmp==')'){
			if(status==5) return 6;
			else return -1;
		}
		else if(tmp=='}'){
			if(status==7) return 8;
			else return -1;
		}
		else return -1;
	}
}
