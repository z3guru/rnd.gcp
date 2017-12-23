/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp.privet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InfoServlet extends HttpServlet
{
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		String info = new StringBuilder()
						.append("{")
       					.append("\"version\": \"1.0\",")
						.append("\"name\": \"ZCUBE’s printer\",")
						.append("\"description\": \"for studying GCP\",")
						.append("\"url\": \"https://www.google.com/cloudprint\",")
						.append("\"type\": [\"printer\"],")
						.append("\"id\": \"11111111-2222-3333-4444-555555555555\",")
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
						.append("\"api\": [")
						.append("\"/privet/accesstoken\",")
						.append("\"/privet/capabilities\",")
						.append("\"/privet/printer/submitdoc\",")
						.append("]")
						.append("}")
						.toString();

		res.setContentType("application/json");
		res.setStatus(HttpServletResponse.SC_OK);
		res.getWriter().println(info);
	}
}
