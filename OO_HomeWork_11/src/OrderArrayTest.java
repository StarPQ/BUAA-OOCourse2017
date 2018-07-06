import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class OrderArrayTest {
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test 
	public void test() {
		OrderArray a = new OrderArray();
		assert(a.repOK()==true);
		assert(a.getArray()!=null);
		Order tmp = new Order("abc");
		a.Add(tmp);
		assert(a.getArray().size()==0);
		Order tmp0 = new Order("(FR,2,UP,0)");
		a.Add(tmp0);
		assert(a.getArray().size()==0);
		Order tmp1 = new Order("(FR,1,UP,0)");
		a.Add(tmp1);
		assert(a.getArray().size()==1);
		Order tmp2 = new Order("(FR,3,UP,3)");
		a.Add(tmp2);
		assert(a.getArray().size()==2);
		Order tmp3 = new Order("(FR,1,UP,1)");
		a.Add(tmp3);
		assert(a.getArray().size()==2);
	}

}
