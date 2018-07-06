import java.util.Scanner;

public class Work1 {
	public static void main(String []args){
		Scanner in = new Scanner(System.in);
		String poly = new String();
		poly = in.nextLine();
		int base;
		int exponent;
		char op = '+';
		Poly polynomial = new Poly();
		for(int i=0; i<poly.length();i++){
			char tmp = poly.charAt(i);
			if(status==-1){
				System.out.println("ÊäÈë´íÎó");
				break;
			}
			else if(Character.isDigit(tmp)&&status==2){
				int j=i;
				while(poly.charAt(j)!=','){
					if(Character.isDigit(poly.charAt(j))){
						//TODO find digits
					}
				}
				status = 3;
			}
			else if(Character.isDigit(tmp)&&status==4){
				int j=i;
				while(poly.charAt(j)!=','){
					if(Character.isDigit(poly.charAt(j))){
						//TODO find digits
					}
				}
				status = 5;
			}
			else if(tmp=='{'||tmp=='('||tmp==','||tmp==')'|tmp=='}'){
				status = polynomial.deal(tmp, status);
			}
			else if(tmp=='+'||tmp=='-'){
				if(status==8){
					op=tmp;
					status=0;
				}
				else status=-1;
			}
			else if(Character.isSupplementaryCodePoint(tmp)){
				i++;
			}
		}
	}
	static int status=0;
}

