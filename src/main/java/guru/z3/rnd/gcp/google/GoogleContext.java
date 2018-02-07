/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp.google;

import guru.z3.rnd.gcp.PrinterContext;
import guru.z3.temple.toolkit.json.JsonTool;
import javafx.print.Printer;
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

	private GoogleContext()
	{

	}

	public static GoogleContext getContext()
	{
		return ctx;
	}

	public void register() throws IOException
	{
		PrinterContext pctx = PrinterContext.getInstance();
		pctx.clear();

		try
		{
			HttpPost req = new HttpPost("https://www.google.com/cloudprint/register");
			req.addHeader("X-CloudPrint-Proxy", X_CLOUDPRINT_PROXY);

			List<NameValuePair> params = new ArrayList(4);
			params.add(new BasicNameValuePair("proxy", "ZZZ"));
			params.add(new BasicNameValuePair("printer", pctx.getPrinterName()));
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
				if ( pctx.getPrinterName().equals(p.getName()) )
				{
					pctx.setPrinter(p);
					break;
				}
			}

			if ( pctx.getPrinter() == null ) pctx.clear();
			else
				pctx.setRegistration(rr);
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


	public void getAuthcode(String clientId) throws IOException
	{
		PrinterContext pctx = PrinterContext.getInstance();
		if ( pctx.getPrinter() == null || pctx.getRegistration() == null ) throw new IOException("there is no registration");

		try
		{
			String url = pctx.getRegistration().getPolling_url() + pctx.getClientId();
			logger.info("getAuthcode URL:" + url);

			HttpGet httpGet = new HttpGet(url);
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
}
