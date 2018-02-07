/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp;

import guru.z3.rnd.gcp.privet.PrivetService;
import guru.z3.temple.toolkit.ToolKit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZcubePrinter implements Runnable
{
	private final Logger logger = LogManager.getContext().getLogger(ZcubePrinter.class.getName());

	private boolean stopped;

	public static void main(String[] args)
	{
		ZcubePrinter printer = new ZcubePrinter();
		printer.run();
	}

	@Override
	public void run()
	{
		try
		{
			stopped = false;
			Runtime.getRuntime().addShutdownHook(new Thread(()->{ ZcubePrinter.this.stopped = true; }));

			// Run PRIVET API Service
			PrivetService psvc = new PrivetService();
			psvc.start();

			// Run mDNS advertiser
			ToolKit.defaultWorkerPool().execute("mDNS", new MulticastDnsAgent());

			// infinite loop
			while (!stopped)
			{
				try { Thread.sleep(500); } catch(InterruptedException e) { }
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	//private class
}
