package prototype.mdns;

import guru.z3.temple.toolkit.ToolKit;
import guru.z3.temple.toolkit.concurrent.JobRunnable;
import guru.z3.temple.toolkit.nio.ByteBufferUtils;
import org.junit.Test;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.concurrent.RejectedExecutionException;

public class TestMdns
{
	@Test
	public void testmDns() throws Exception
	{
		DatagramChannel mc = null;

		try
		{
			InetAddress addr = InetAddress.getByName("127.0.0.1");
			InetAddress group = InetAddress.getByName("224.0.0.251");
			NetworkInterface ni = NetworkInterface.getByName("en1");

			// "StandardProtocolFamily.INET"을 생략하면
			// 예외가 발생함 ==> IPv6 socket cannot join IPv4 multicast group
			mc = DatagramChannel.open(StandardProtocolFamily.INET);
			mc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			mc.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
			mc.bind(new InetSocketAddress(5353));


			ByteBuffer buf = ByteBuffer.allocate(128);

			MembershipKey key = mc.join(group, ni);
			//dc.connect(new InetSocketAddress("224.0.0.251", 5353));

			while (true)
			{
				if ( key.isValid() )
				{
					// read함수를 쓸 경우 connect를 요구한다
					SocketAddress sender = mc.receive(buf);
					buf.flip();
					System.out.println("sender=" + sender + "\n" + ByteBufferUtils.serialize(buf));
					buf.clear();
				}
				else
				{
					System.out.println("trying to connect");
					try { Thread.sleep(1000); } catch(InterruptedException e) { }
				}
			}
		}
		catch(Exception e)
		{
			throw e;
		}

		finally
		{
			try { mc.close(); } catch(Exception e) { }
		}
	}

	@Test
	public void testJmDNS() throws IOException
	{
		//
		ToolKit.defaultWorkerPool().execute("monitor", new MonitorJob());
		try { Thread.sleep(1000); } catch(InterruptedException e) { }

		//
		String txt = new StringBuilder()
						 .append("txtvers=1")
						 .append(", ty=Zcube RND Cloud Printer Model A")
						 .append(", url=https://www.google.com/cloudprint")
						 .append(", type=printer")
						 .append(", id=")
						 .append(", cs=offline")
						 .toString();

		String name = "ZCUBE-PRINTER";
		String typePrivet = "._privet._tcp.local";
		String typePrinter = "._printer._sub._privet._tcp.local";

		ServiceInfo svc = ServiceInfo.create(name + typePrinter, name, 5353, txt);
		//JmmDNS jmDNS = JmmDNS.Factory.getInstance();
		JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

		try { Thread.sleep(1000); } catch(InterruptedException e) { }
		jmdns.registerService(svc);

		try { Thread.sleep(20000); } catch(InterruptedException e) { }
		System.out.println("Advertise SERVICE");

		jmdns.unregisterAllServices();
		System.out.println("unregisterAllServices");
	}

	private class MonitorJob implements JobRunnable
	{

		@Override
		public void setup() throws RejectedExecutionException
		{

		}

		@Override
		public boolean working()
		{
			DatagramChannel mc = null;

			try
			{
				InetAddress addr = InetAddress.getByName("127.0.0.1");
				InetAddress group = InetAddress.getByName("224.0.0.251");
				NetworkInterface ni = NetworkInterface.getByName("en1");

				// "StandardProtocolFamily.INET"을 생략하면
				// 예외가 발생함 ==> IPv6 socket cannot join IPv4 multicast group
				mc = DatagramChannel.open(StandardProtocolFamily.INET);
				mc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
				mc.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
				mc.bind(new InetSocketAddress(5353));


				ByteBuffer buf = ByteBuffer.allocate(128);

				MembershipKey key = mc.join(group, ni);
				//dc.connect(new InetSocketAddress("224.0.0.251", 5353));

				while (true)
				{
					if ( key.isValid() )
					{
						// read함수를 쓸 경우 connect를 요구한다
						SocketAddress sender = mc.receive(buf);
						buf.flip();
						System.out.println("sender=" + sender + "\n" + ByteBufferUtils.serialize(buf));
						buf.clear();
					}
					else
					{
						System.out.println("trying to connect");
						try { Thread.sleep(1000); } catch(InterruptedException e) { }
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			finally
			{
				try { mc.close(); } catch(Exception e) { }
			}

			return false;
		}

		@Override
		public int stopped(boolean abort)
		{
			return 0;
		}
	}
}
