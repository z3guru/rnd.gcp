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
				"    \"132333-35e2-bbbb-7777-0001115222cc\"\n" +
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
				"    \"__cp_printer__\\u003dUNKNOWN\"\n" +
				"   ],\n" +
				"   \"gcpVersion\": \"1.0\",\n" +
				"   \"proxy\": \"ZZZ\",\n" +
				"   \"createTime\": \"1516772882187\",\n" +
				"   \"defaultDisplayName\": \"\",\n" +
				"   \"name\": \"ZCUBEPRN\",\n" +
				"   \"id\": \"eeee3d33-cccc-aaaa-1111-bbbbb6665666\",\n" +
				"   \"status\": \"\",\n" +
				"   \"accessTime\": \"1516772882187\"\n" +
				"  }\n" +
				" ],\n" +
				" \"complete_invite_url\": \"https://goo.gl/printer/...\",\n" +
				" \"invite_url\": \"https://www.google.com/cloudprint/claimprinter.html\",\n" +
				" \"oauth_scope\": \"https://www.googleapis.com/auth/cloudprint\",\n" +
				" \"invite_page_url\": \"https://www.google.com/cloudprint/regtokenpage?,279400\",\n" +
				" \"registration_token\": \"zzzzz\",\n" +
				" \"token_duration\": \"899\",\n" +
				" \"automated_invite_url\": \"https://www.google.com/cloudp\",\n" +
				" \"polling_url\": \"https://www.google.com/cloudprint/getauthcode?printerid\"\n" +
				"}";

		System.out.println(json);
		RegistrationVO r = JsonTool.defaultLib().deserialize(json, RegistrationVO.class);

		TestCase.assertEquals(true, r.isSuccess());
		TestCase.assertEquals("zzzzz", r.getRegistration_token());
		TestCase.assertEquals("eeee3d33-cccc-aaaa-1111-bbbbb6665666", r.getPrinters().get(0).getId());
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
