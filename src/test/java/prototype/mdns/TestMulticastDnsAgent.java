/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package prototype.mdns;

import guru.z3.rnd.gcp.MulticastDnsAgent;
import guru.z3.temple.toolkit.ToolKit;
import org.junit.Test;

public class TestMulticastDnsAgent
{
	@Test
	public void testAdvertise()
	{
		ToolKit.defaultWorkerPool().execute("mDNS", new MulticastDnsAgent());

		try { Thread.sleep(10000); } catch(InterruptedException e) { }
	}
}
