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


			ByteBuffer buf = ByteBuffer.allocate(512);

			MembershipKey key = mc.join(group, ni);
			//dc.connect(new InetSocketAddress("224.0.0.251", 5353));

			while (true)
			{
				if ( key.isValid() )
				{
					// read함수를 쓸 경우 connect를 요구한다
					SocketAddress sender = mc.receive(buf);
					buf.flip();
					System.out.println("sender=" + sender + "\n" + ByteBufferUtils.toHexString(buf, 32));
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
	public void testmDns2()
	{
		System.out.println(ByteBufferUtils.toHexString(buildAnswer2(), 32));
	}

	@Test
	public void testmDns3() throws Exception
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


			ByteBuffer buf = ByteBuffer.allocate(512);

			MembershipKey key = mc.join(group, ni);
			//mc.connect(new InetSocketAddress("224.0.0.251", 5353));

			while (true)
			{
				if ( key.isValid() )
				{
					SocketAddress sender = mc.receive(buf);
					buf.flip();
					System.out.println("sender=" + sender + "\n" + ByteBufferUtils.toHexString(buf, 32));

					int hh = buf.getInt();
					if ( hh == 0x00000000 )
					{
						System.out.println("sending answer");
						mc.write(buildAnswer2());
					}
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


	private ByteBuffer buildAnswer()
	{
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.putInt(0x00008400);
		buf.putShort((short)0);
		buf.putShort((short)3);
		buf.putShort((short)0);
		buf.putShort((short)0);

		String printerName = "ZCUBE-PRINTER";
		String[] privetNames = new String[] { "_privet", "_tcp", "local" };
		String[] subtypeNames = new String[] { "_printer", "_sub" };

		// 1st answer ======
		int pos = buf.position();   // 나중 포인터를 위해

		for ( String n : privetNames )
		{
			int len = n.length();
			buf.put((byte)len);
			for ( int i = 0; i < len; i++ ) buf.put((byte)n.charAt(i));
		}
		buf.put((byte)0x00);		// end of Name

		buf.putShort((short)0x000C);	// TYPE = PTR
		buf.putShort((short)0x0001);	// CLASS = IN
		buf.putInt(120);				// TTL = 120sec
		buf.putShort((short)(1 + printerName.length() + 2));	// RD Length

		buf.put((byte)printerName.length());
		for ( int i = 0; i < printerName.length(); i++ ) buf.put((byte)printerName.charAt(i));
		buf.put((byte)0xC0);
		buf.put((byte)pos);

		// 2nd answer ======
		int pos2 = buf.position();   // 나중 포인터를 위해

		for ( String n : subtypeNames )
		{
			int len = n.length();
			buf.put((byte)len);
			for ( int i = 0; i < len; i++ ) buf.put((byte)n.charAt(i));
		}
		buf.put((byte)0xC0);
		buf.put((byte)pos);

		buf.putShort((short)0x000C);	// TYPE = PTR
		buf.putShort((short)0x0001);	// CLASS = IN
		buf.putInt(120);				// TTL = 120sec
		buf.putShort((short)(1 + printerName.length() + 2));	// RD Length

		int pos3 = buf.position();   // 나중 포인터를 위해
		buf.put((byte)printerName.length());
		for ( int i = 0; i < printerName.length(); i++ ) buf.put((byte)printerName.charAt(i));
		buf.put((byte)0xC0);
		buf.put((byte)pos2);


		// 3rd answer ======
		buf.put((byte)0xC0);
		buf.put((byte)pos3);

		String txt = new StringBuilder()
				.append("txtvers=1")
				.append(", ty=Zcube RND Cloud Printer Model A")
				.append(", url=https://www.google.com/cloudprint")
				.append(", type=printer")
				.append(", id=")
				.append(", cs=offline")
				.toString();

		buf.putShort((short)0x0010);	// TYPE = TXT
		buf.putShort((short)0x0001);	// CLASS = IN
		buf.putInt(120);				// TTL = 120sec
		buf.putShort((short)txt.length());	// RD Length
		for ( int i = 0; i < txt.length(); i++ ) buf.put((byte)txt.charAt(i));

		buf.flip();
		return buf;
	}

	private ByteBuffer buildAnswer2()
	{
		String s = new StringBuilder()
				.append("00 00 84 00 00 00 00 03 00 00 00 00 07 5F 70 72 69 76 65 74 04 5F 74 63 70 05 6C 6F 63 61 6C 00")
				.append("00 0C 00 01 00 00 0E 10 00 10 0D 5A 43 55 42 45 2D 50 52 49 4E 54 45 52 C0 0C C0 2A 00 21 80 01")
				.append("00 00 0E 10 00 22 00 00 00 00 14 E9 19 53 49 4E 49 4C 75 69 2D 4D 61 63 42 6F 6F 6B 2D 50 72 6F")
				.append("2D 6C 6F 63 61 6C C0 19 C0 2A 00 10 80 01 00 00 0E 10 00 73 74 78 74 76 65 72 73 3D 31 2C 20 74")
				.append("79 3D 5A 63 75 62 65 20 52 4E 44 20 43 6C 6F 75 64 20 50 72 69 6E 74 65 72 20 4D 6F 64 65 6C 20")
				.append("41 2C 20 75 72 6C 3D 68 74 74 70 73 3A 2F 2F 77 77 77 2E 67 6F 6F 67 6C 65 2E 63 6F 6D 2F 63 6C")
				.append("6F 75 64 70 72 69 6E 74 2C 20 74 79 70 65 3D 70 72 69 6E 74 65 72 2C 20 69 64 3D 2C 20 63 73 3D")
				.append("6F 66 66 6C 69 6E 65")
				.toString();

		ByteBuffer buf = ByteBuffer.allocate(1024);
		int state = 0;
		int value = 0;

		for ( int i = 0; i < s.length(); i++ )
		{
			int ch = s.charAt(i);

			if ( state == 0 )
			{
				if (ch >= '0' && ch <= '9')
				{
					value = ch - '0';
					state = 1;
				} else if (ch >= 'a' && ch <= 'f')
				{
					value = ch - 'a' + 10;
					state = 1;
				} else if (ch >= 'A' && ch <= 'F')
				{
					value = ch - 'A' + 10;
					state = 1;
				}
			}
			else if ( state == 1 )
			{
				if (ch >= '0' && ch <= '9')
				{
					value <<= 4;
					value |= ch - '0';
				} else if (ch >= 'a' && ch <= 'f')
				{
					value <<= 4;
					value |= ch - 'a' + 10;
				} else if (ch >= 'A' && ch <= 'F')
				{
					value <<= 4;
					value |= ch - 'A' + 10;
				}

				buf.put((byte)value);
				state = 0;
			}
		}

		if ( state == 0 ) buf.put((byte) value);

		buf.flip();
		return buf;
	}


	@Test
	public void testJmDNS() throws IOException
	{
		//
		//ToolKit.defaultWorkerPool().execute("monitor", new MonitorJob());
		//try { Thread.sleep(1000); } catch(InterruptedException e) { }

		//
		String txt = new StringBuilder()
						 .append("txtvers=1")
						 .append(", ty=Zcube RND Cloud Printer Model A")
						 .append(", url=https://www.google.com/cloudprint")
						 .append(", type=printer")
						 .append(", id=")
						 .append(", cs=offline")
						 .toString();

		String name = "ZCUBE";
		String typePrivet = "._privet._tcp.local";
		String typePrinter = "._printer._sub._privet._tcp.local";

		//ServiceInfo svc = ServiceInfo.create(name + typePrinter, name, 5353, txt);
		ServiceInfo svc = ServiceInfo.create(typePrivet, name, typePrinter, 5353, txt);
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
