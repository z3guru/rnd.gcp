/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp.privet;

import guru.z3.rnd.gcp.PrinterContext;
import guru.z3.rnd.gcp.google.GoogleContext;
import guru.z3.rnd.gcp.google.PrinterVO;
import guru.z3.rnd.gcp.google.RegistrationVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

public class RegisterServlet extends HttpServlet
{
	private final Logger logger = LogManager.getContext().getLogger(RegisterServlet.class.getName());

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		postGet(req, res);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		postGet(req, res);
	}

	private void postGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		logger.info("Register service is called");

		StringBuilder headerStr = new StringBuilder();
		for (Enumeration<String> hh = req.getHeaderNames(); hh.hasMoreElements(); )
		{
			String name = hh.nextElement();
			headerStr.append("\nKey=").append(name);
			headerStr.append(", value=").append(req.getHeader(name));
		}
		logger.info("/privet/register::headers ===========" + headerStr);

		StringBuilder paramStr = new StringBuilder();
		for ( Map.Entry<String,String[]> param : req.getParameterMap().entrySet() )
		{
			paramStr.append("\nKey=").append(param.getKey());
			paramStr.append(", value=").append(Arrays.toString(param.getValue()));
		}
		logger.info("/privet/register::params ===========" + paramStr);

		String action = req.getParameter("action");
		String user = req.getParameter("user");
		GoogleContext gtx = GoogleContext.getContext();

		String result = null;
		if ( "start".equals(action) )
		{
			result = new StringBuilder()
						 .append("{\"action\":\"").append(action)
						 .append("\", \"user\":\"").append(user).append("\"}")
						 .toString();
		}
		else if ( "getClaimToken".equals(action) )
		{
			gtx.register();
			RegistrationVO rr = PrinterContext.getInstance().getRegistration();

			result = new StringBuilder()
					.append("{\"action\":\"").append(action)
					.append("\", \"user\":\"").append(user)
					.append("\", \"token\":\"").append(rr.getRegistration_token())
					.append("\", \"claim_url\":\"").append(rr.getInvite_url()).append("\"}")
					.toString();
		}
		else if ( "complete".equals(action) )
		{
			try { gtx.getAuthcode(user); }
			catch(Exception e)
			{
				logger.warn(e.getMessage(), e);
			}

			PrinterVO pp = PrinterContext.getInstance().getPrinter();
			result = new StringBuilder()
					.append("{\"action\":\"").append(action)
					.append("\", \"user\":\"").append(user)
					.append("\", \"device_id\":\"").append(pp.getId()).append("\"}")
					.toString();
		}

		logger.info("/privet/register::contents ===========\n" + result);
		res.setContentType("application/json");
		res.setStatus(HttpServletResponse.SC_OK);
		res.getWriter().println(result);
	}
}