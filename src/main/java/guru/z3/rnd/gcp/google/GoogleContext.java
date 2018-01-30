/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp.google;

import guru.z3.temple.toolkit.json.JsonTool;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GoogleContext
{
	private static GoogleContext ctx = new GoogleContext();
	private final Logger logger = LogManager.getContext().getLogger(GoogleContext.class.getName());

	public final static String X_CLOUDPRINT_PROXY	= "zcube";

	private final String printerName = "ZCUBEPRINTER";
	private RegistrationVO registration;
	private PrinterVO printer;

	private GoogleContext()
	{

	}

	public static GoogleContext getContext()
	{
		return ctx;
	}

	public void clear()
	{
		this.registration = null;
		this.printer = null;
	}

	public RegistrationVO register() throws IOException
	{
		clear();

		try
		{
			/*
			URIBuilder ub = new URIBuilder("https://www.google.com/cloudprint/register");
			ub.addParameter("proxy", "");
			ub.addParameter("printer", this.printerName);
			ub.addParameter("capabilities", "{\"version\": \"1.0\"}");
			ub.addParameter("use_cdd", "true");

			URI uri = ub.build();
			logger.info("/cloudprint/register uri=" + uri);
			*/
			HttpPost req = new HttpPost("https://www.google.com/cloudprint/register");
			req.addHeader("X-CloudPrint-Proxy", X_CLOUDPRINT_PROXY);

			List<NameValuePair> params = new ArrayList(4);
			params.add(new BasicNameValuePair("proxy", "ZZZ"));
			params.add(new BasicNameValuePair("printer", this.printerName));
			params.add(new BasicNameValuePair("capabilities", "{\"version\": \"1.0\"}"));
			params.add(new BasicNameValuePair("use_cdd", "true"));
			req.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpClient client = HttpClients.createDefault();
			HttpResponse res = client.execute(req);
			String json = EntityUtils.toString(res.getEntity());
			logger.info("/cloudprint/register response:\n" + json);

			RegistrationVO rr = JsonTool.defaultLib().deserialize(json, RegistrationVO.class);

			for ( PrinterVO p : rr.getPrinters() )
			{
				if ( this.printerName.equals(p.getName()) )
				{
					this.printer = p;
					break;
				}
			}

			if ( this.printer == null ) clear();
			else
				this.registration = rr;
		}
		catch(IOException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new IOException(e.getMessage(), e);
		}

		return this.registration;
	}


	public void getAuthcode(String clientId) throws IOException
	{
		if ( this.printer == null || this.registration == null ) throw new IOException("there is no registration");

		try
		{
			/*
			URIBuilder ub = new URIBuilder("https://www.google.com/cloudprint/getauthcode");
			ub.addParameter("printerid", this.printer.getId());
			ub.addParameter("oauth_client_id", clientId);
			*/
			HttpGet httpGet = new HttpGet(this.registration.getPolling_url());
			httpGet.setHeader("X-CloudPrint-Proxy", X_CLOUDPRINT_PROXY);

			HttpClient client = HttpClients.createDefault();
			HttpResponse res = client.execute(httpGet);
			String json = EntityUtils.toString(res.getEntity());
			logger.info("/cloudprint/getauthcode response:\n" + json);

			//vo = JsonTool.defaultLib().deserialize(res.getContentAsString(), RegistrationVO.class);
		}
		catch(IOException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new IOException(e.getMessage(), e);
		}
	}

	// GETTER/SETTER methods ===============================
	public String getPrinterName() { return printerName; }

	public RegistrationVO getRegistration() { return registration; }
	public void setRegistration(RegistrationVO registration) { this.registration = registration; }

	public PrinterVO getPrinter() { return printer; }
	public void setPrinter(PrinterVO printer) { this.printer = printer; }
}
