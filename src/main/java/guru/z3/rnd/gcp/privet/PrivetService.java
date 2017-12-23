/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp.privet;

import guru.z3.temple.toolkit.ToolKit;
import guru.z3.temple.toolkit.concurrent.JobRunnable;
import guru.z3.temple.toolkit.concurrent.Worker;
import guru.z3.temple.toolkit.nio.NioReadTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import java.util.concurrent.RejectedExecutionException;

public class PrivetService implements JobRunnable
{
	private final Logger logger = LogManager.getContext().getLogger(PrivetService.class.getName());

	/** jetty embedded server */
	private Server server;
	/** Threading */
	private Worker worker;

	public void start()
	{
		logger.info("start service");
		this.worker = ToolKit.defaultWorkerPool().execute("privet service", this);
	}

	public void stop()
	{
		try { this.worker.stop(true); }
		catch(Exception e)
		{
			logger.error("error in stopping", e);
		}
	}

	@Override
	public void setup() throws RejectedExecutionException
	{
		try
		{
			this.server = new Server();
			ServerConnector c = new ServerConnector(server);
			c.setPort(8090);
			this.server.setConnectors(new Connector[] {c});

			ServletHandler handler = new ServletHandler();
			handler.addServletWithMapping(RegisterServlet.class, "/register");
			handler.addServletWithMapping(RegisterServlet.class, "/privet/register");
			handler.addServletWithMapping(InfoServlet.class, "/privet/info");
			this.server.setHandler(handler);
			this.server.start();
		}
		catch(Exception e)
		{
			throw new RejectedExecutionException(e.getMessage(), e);
		}
	}

	@Override
	public boolean working()
	{
		try { Thread.sleep(1000); } catch(InterruptedException e) { }
		return true;
	}

	@Override
	public int stopped(boolean abort)
	{
		try { this.server.stop(); }
		catch(Exception e)
		{
			logger.error("error in stopping", e);
		}

		return 0;
	}
}
