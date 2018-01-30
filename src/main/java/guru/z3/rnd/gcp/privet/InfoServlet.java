/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp.privet;

import guru.z3.rnd.gcp.google.GoogleContext;
import guru.z3.temple.toolkit.nio.NioReadTool;
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

public class InfoServlet extends HttpServlet
{
	private final Logger logger = LogManager.getContext().getLogger(InfoServlet.class.getName());

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		StringBuilder headerStr = new StringBuilder();
		for (Enumeration<String> hh = req.getHeaderNames(); hh.hasMoreElements(); )
		{
			String name = hh.nextElement();
			headerStr.append("\nKey=").append(hh);
			headerStr.append(", value=").append(req.getHeader(name));
		}
		logger.info("/privet/info::headers ===========" + headerStr);

		StringBuilder paramStr = new StringBuilder();
		for ( Map.Entry<String,String[]> param : req.getParameterMap().entrySet() )
		{
			paramStr.append("\nKey=").append(param.getKey());
			paramStr.append(", value=").append(Arrays.toString(param.getValue()));
		}
		logger.info("/privet/info::params ===========" + paramStr);

		logger.info("InfoServlet is called");
		GoogleContext gtx = GoogleContext.getContext();
		boolean registered = gtx.getPrinter() == null ? false : true;

		StringBuilder info = new StringBuilder()
						.append("{")
       					.append("\"version\": \"1.0\",")
						.append("\"name\": \"ZCUBEPRINTER\",")
						.append("\"description\": \"for studying GCP\",")
						.append("\"url\": \"https://www.google.com/cloudprint\",")
						.append("\"type\": [\"printer\"],")
						.append("\"id\":\"").append(registered ? gtx.getPrinter().getId() : "").append("\",")
						.append("\"device_state\": \"idle\",")
						.append("\"connection_state\": \"online\",")
						.append("\"manufacturer\": \"Zcube\",")
						.append("\"model\": \"ZCUBE LABPRN\",")
						.append("\"serial_number\": \"1111-22222-33333-4444\",")
						.append("\"firmware\": \"1.0.1220.01\",")
						.append("\"uptime\": 600,")
						.append("\"setup_url\": \"http://support.google.com/cloudprint/answer/1686197/?hl=en\",")
						.append("\"support_url\": \"http://support.google.com/cloudprint/?hl=en\",")
						.append("\"update_url\": \"http://support.google.com/cloudprint/?hl=en\",")
						.append("\"x-privet-token\": \"AIp06DjQd80yMoGYuGmT_VDAApuBZbInsQ:1358377509659\",")
						.append("\"api\": [");

		if ( registered )
		{
			info.append("\"/privet/capabilities\",");
			info.append("\"/privet/printer/submitdoc\"");
		}
		else
		{
			info.append("\"/privet/register\",");
		}

		info.append("]}");

		logger.info("/privet/info::contents ===========\n" + info);

		res.setContentType("application/json");
		res.setStatus(HttpServletResponse.SC_OK);
		res.getWriter().println(info.toString());
	}
}
