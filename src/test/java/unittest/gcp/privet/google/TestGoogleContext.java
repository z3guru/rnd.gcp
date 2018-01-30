/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package unittest.gcp.privet.google;

import guru.z3.rnd.gcp.google.GoogleContext;
import guru.z3.rnd.gcp.google.RegistrationVO;
import guru.z3.temple.toolkit.json.JsonTool;
import junit.framework.TestCase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;

public class TestGoogleContext
{
	@Test
	public void testRegistrationVO() throws IOException
	{
		String json =
				"{\n" +
				" \"success\": true,\n" +
				" \"request\": {\n" +
				"  \"time\": \"0\",\n" +
				"  \"params\": {\n" +
				"   \"proxy\": [\n" +
				"    \"ZZZ\"\n" +
				"   ],\n" +
				"   \"capabilities\": [\n" +
				"    \"{\\\"version\\\": \\\"1.0\\\"}\"\n" +
				"   ],\n" +
				"   \"printer\": [\n" +
				"    \"ZCUBEPRN\"\n" +
				"   ],\n" +
				"   \"printerid\": [\n" +
				"    \"1d54c225-35e2-bc1d-76f9-0be1905230ce\"\n" +
				"   ],\n" +
				"   \"use_cdd\": [\n" +
				"    \"true\"\n" +
				"   ]\n" +
				"  }\n" +
				" },\n" +
				" \"printers\": [\n" +
				"  {\n" +
				"   \"isTosAccepted\": false,\n" +
				"   \"capabilities\": {\n" +
				"    \"version\": \"1.0\"\n" +
				"   },\n" +
				"   \"displayName\": \"ZCUBEPRN\",\n" +
				"   \"description\": \"\",\n" +
				"   \"capsHash\": \"\",\n" +
				"   \"updateTime\": \"1516772882187\",\n" +
				"   \"type\": \"GOOGLE\",\n" +
				"   \"notificationChannel\": \"XMPP_CHANNEL\",\n" +
				"   \"tags\": [\n" +
				"    \"__cp_printer_passes_2018_cert__\\u003dUNKNOWN\"\n" +
				"   ],\n" +
				"   \"gcpVersion\": \"1.0\",\n" +
				"   \"proxy\": \"ZZZ\",\n" +
				"   \"createTime\": \"1516772882187\",\n" +
				"   \"defaultDisplayName\": \"\",\n" +
				"   \"name\": \"ZCUBEPRN\",\n" +
				"   \"id\": \"e7513d37-c393-aae9-17b5-ba70e67d5653\",\n" +
				"   \"status\": \"\",\n" +
				"   \"accessTime\": \"1516772882187\"\n" +
				"  }\n" +
				" ],\n" +
				" \"complete_invite_url\": \"https://goo.gl/printer/gGkg2\",\n" +
				" \"invite_url\": \"https://www.google.com/cloudprint/claimprinter.html\",\n" +
				" \"oauth_scope\": \"https://www.googleapis.com/auth/cloudprint\",\n" +
				" \"invite_page_url\": \"https://www.google.com/cloudprint/regtokenpage?t\\u003dCzaoK\\u0026dpi\\u003d300\\u0026pagesize\\u003d215900,279400\",\n" +
				" \"registration_token\": \"CzaoK\",\n" +
				" \"token_duration\": \"899\",\n" +
				" \"automated_invite_url\": \"https://www.google.com/cloudprint/confirm?token\\u003dCzaoK\",\n" +
				" \"polling_url\": \"https://www.google.com/cloudprint/getauthcode?printerid\\u003de7513d37-c393-aae9-17b5-ba70e67d5653\\u0026oauth_client_id\\u003d\"\n" +
				"}";

		RegistrationVO r = JsonTool.defaultLib().deserialize(json, RegistrationVO.class);

		TestCase.assertEquals(true, r.isSuccess());
		TestCase.assertEquals("CzaoK", r.getRegistration_token());
		TestCase.assertEquals("e7513d37-c393-aae9-17b5-ba70e67d5653", r.getPrinters().get(0).getId());
	}

	@Test
	public void testRegister() throws IOException
	{
		GoogleContext gtx = GoogleContext.getContext();

		//gtx.setPrinter();
		gtx.register();
	}

	@Test
	public void testHttps() throws Exception
	{
		HttpClient httpClient = HttpClients.createDefault();
		String urlOverHttps = "https://www.google.com/cloudprint/register";
		HttpGet getMethod = new HttpGet(urlOverHttps);

		HttpResponse res = httpClient.execute(getMethod);
		res.getEntity().writeTo(System.out);
	}
}
