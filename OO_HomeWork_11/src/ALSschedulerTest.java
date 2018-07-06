import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ALSschedulerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		ALSscheduler a = new ALSscheduler();
		assert(a.repOK()==true);
		Order O0 = new Order("(FR,1,UP,0)");
		Order O1 = new Order("(FR,5,DOWN,3)");
		Order O2 = new Order("(ER,5,3)");//lpick branch
		Order O3 = new Order("(FR,5,UP,3)");//fpick branch
		Order O4 = new Order("(ER,7,3)");//has further branch
		Order O5 = new Order("(ER,4,3)");//lpick branch
		Order O6 = new Order("(FR,4,UP,3)");//fpick branch
		Order O7 = new Order("(ER,7,4)");//check SAME ER
		Order O8 = new Order("(FR,5,UP,4)");//check SAME FR
		a.add(O0);
		a.add(O1);
		a.add(O2);
		a.add(O3);
		a.add(O4);
		a.add(O5);
		a.add(O6);
		a.add(O7);
		a.add(O8);
		a.deal();
	}

}
