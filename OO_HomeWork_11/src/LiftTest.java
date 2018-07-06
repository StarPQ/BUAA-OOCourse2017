import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LiftTest {

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Lift l = new Lift();
		assert(l.repOK()==true);
		Order O0 = new Order("(FR,1,UP,0)");
		Order O1 = new Order("(FR,5,UP,1)");
		Order O2 = new Order("(FR,5,UP,2)");
		Order O3 = new Order("(ER,7,3)");
		assert(l.judge(O0)==true);
		assert(l.judge(O1)==true);
		assert(l.judge(O2)==false);
		assert(l.judge(O3)==true);
		assert(l.pickup(1)==false);
		assert(l.pickup(5)==true);
		assert(l.pickup(3)==false);
		l.setFloor(O3);
		assert(l.isfloor(7)==false);
		long T = l.getTime();
		l.adjust();
		assert(l.getTime()==T+10);
		System.out.println("test STrefresh");
		l.StatusRefreash(2, 1);
		assert(l.toString().equals("UP"));
		l.StatusRefreash(2, 1);
		assert(l.toString().equals("STILL"));
		l.StatusRefreash(1, 1);
		assert(l.toString().equals("DOWN"));
		l.StatusRefreash(5, 2);
		l.getOatF(0);
		l.getTerminal();
		l.getPosition();
		l.setFloor(0);
	}

	@Test
	public void testn_pos(){
		Lift l = new Lift();
		l.Time = 0;
		l.Position = 1;
		l.Terminal = 1;
		assert(l.n_pos(0)==1);
		assertEquals(l.toString(), "STILL");
		l.Position = 1;
		l.Terminal = 2;
		assert(l.n_pos(0)==2);
		assertEquals(l.toString(), "UP");
		l.Position = 2;
		l.Terminal = 1;
		assert(l.n_pos(0)==1);
		assertEquals(l.toString(), "DOWN");
	}
}
