/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package unittest.gcp.privet;

import guru.z3.rnd.gcp.privet.PrivetService;
import org.junit.Test;

public class TestPrivetService
{
	@Test
	public void testServer()
	{
		PrivetService svc = new PrivetService();
		svc.start();

		try { Thread.sleep(20000); } catch(InterruptedException e) { }
	}
}
