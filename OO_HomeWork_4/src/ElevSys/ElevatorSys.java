package ElevSys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class ElevatorSys {
	public static void main(String[] args){
		File f = new File("result.txt");
		if(f.exists()){
			f.delete();
		}
		long start = System.currentTimeMillis();
		RequestQueue queue = new RequestQueue();
		Elevaltor a = new Elevaltor(queue, start, 1);a.setName("#1");
		Elevaltor b = new Elevaltor(queue, start, 2);b.setName("#2");
		Elevaltor c = new Elevaltor(queue, start, 3);c.setName("#3");
		SuperScheduler scheduler = new SuperScheduler(queue, a,b,c);
		Request req = new Request(scheduler, start);
		req.start();
		a.start();
		b.start();
		c.start();
	}
}
