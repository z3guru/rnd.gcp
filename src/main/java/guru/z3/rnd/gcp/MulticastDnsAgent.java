/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp;

import guru.z3.rnd.gcp.google.GoogleContext;
import guru.z3.temple.toolkit.concurrent.JobRunnable;
import guru.z3.temple.toolkit.nio.NioReadTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.RejectedExecutionException;

/**
 */
public class MulticastDnsAgent implements JobRunnable
{
	private final Logger logger = LogManager.getContext().getLogger(NioReadTool.class.getName());

	private final static String IP_mDNS	= "224.0.0.251";
	private final static int	PORT_mDNS = 5353;

	/** whether this joined mDNS multicast group */
	private boolean joined;
	/** channel for multicast */
	private DatagramChannel channel;
	/** to use NIO of the channel */
	private Selector selector;
	/** buffer for receiving data */
	private ByteBuffer recvBuf;
	/** buffer for sending data */
	private ByteBuffer sendBuf;

	private DatagramChannel open() throws IOException
	{
		DatagramChannel ch = null;

		try
		{
			InetAddress group = InetAddress.getByName(IP_mDNS);

			// if "StandardProtocolFamily.INET" is omitted, exception occurred
			// ==> IPv6 socket cannot join IPv4 multicast group
			ch = DatagramChannel.open(StandardProtocolFamily.INET);
			ch.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			ch.bind(new InetSocketAddress(PORT_mDNS));

			return ch;
		}
		catch(IOException e)
		{
			throw e;
		}
	}

	/**
	 * Advertise printer information on mDNS net (224.0.0.251:5353)
	 */
	private void advertise()
	{
		advertise(new InetSocketAddress(IP_mDNS, PORT_mDNS));
	}

	/**
	 * Advertise printer information to a node that ask information.
	 * @param addr : asking node
	 */
	private void advertise(SocketAddress addr)
	{
		try
		{
			PrinterContext pctx = PrinterContext.getInstance();

			ByteBuffer buf = answerStream(pctx.getPrinterName(), PrinterContext.PRIVET_SVC_IP, PrinterContext.PRIVET_SVC_PORT);
			this.channel.send(buf, addr);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * pack mDNS answers into {@link ByteBuffer}
	 *
	 * @param printerName
	 * @return
	 */
	private ByteBuffer answerStream(String printerName, String privetServiceIp, int privetServicePort) throws Exception
	{
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.putInt(0x00008000);	// set QRBIT == answer
		buf.putShort((short)0);	// NO Questions
		buf.putShort((short)5);	// 5 Answers
		buf.putShort((short)0);	// 0 Authority RR
		buf.putShort((short)0);	// 0 Additional RR

		String[] subtypeNames = new String[] { "_printer", "_sub" };

		// 1st answer PTR:_privet._tcp.local ======
		// privet QNAME
		int ptrPrivetQname = buf.position();   // privet qname pointer in the below
		String[] privetNames = new String[] { "_privet", "_tcp", "local" };

		for ( String n : privetNames )
		{
			int len = n.length();
			buf.put((byte)len);
			for ( int i = 0; i < len; i++ ) buf.put((byte)n.charAt(i));
		}
		buf.put((byte)0x00); // end of QNAME

		// define PTR
		buf.putShort((short)0x000C);	// TYPE = PTR
		buf.putShort((short)0x0001);	// CLASS = IN
		buf.putInt(120);				// TTL = 120sec
		buf.putShort((short)(1 + printerName.length() + 2));	// RD Length

		ByteBuffer prtNameBuf = ByteBuffer.allocate(1024);
		prtNameBuf.put((byte)printerName.length());
		prtNameBuf.put(printerName.getBytes());
		prtNameBuf.put((byte)0xC0);
		prtNameBuf.put((byte)ptrPrivetQname);	// pointing "_privet._tcp.local"
		prtNameBuf.flip();
		buf.put(prtNameBuf.duplicate());		// [printer name]._privet._tcp.local


		// 2nd answer : TXT ======
		buf.put(prtNameBuf.duplicate());		// [printer name]._privet._tcp.local

		// define TXT
		buf.putShort((short)0x0010);	// TYPE = TXT
		buf.putShort((short)0x0001);	// CLASS = IN
		buf.putInt(120);				// TTL = 120sec

		String[] txt = new String[] {
				"txtvers=1"
				, "ty=Zcube RND Printer"
				, "url=https://www.google.com/cloudprint"
				, "type=printer"
				, "id="
		};

		int len = 0;
		for ( String t : txt ) len += (t.length() + 1);
		buf.putShort((short)len);		// RD Length

		for ( String t : txt )
		{
			buf.put((byte)t.length());
			buf.put(t.getBytes());
		}

		// 3rd answer : SRV ======
		buf.put(prtNameBuf.duplicate());		// [printer name]._privet._tcp.local

		// define SRV
		buf.putShort((short)0x0021);	// TYPE = SRV
		buf.putShort((short)0x0001);	// CLASS = IN
		buf.putInt(120);				// TTL = 120sec

		ByteBuffer hostnameBuf = ByteBuffer.allocate(1024);
		hostnameBuf.put((byte)printerName.length());
		hostnameBuf.put(printerName.getBytes());
		hostnameBuf.put((byte)"local".length());
		hostnameBuf.put("local".getBytes());
		hostnameBuf.put((byte)0x00); // end...
		hostnameBuf.flip();

		buf.putShort((short)(hostnameBuf.remaining() + 6));	// RD Length
		buf.putShort((short)0);					// priority
		buf.putShort((short)0);					// weight
		buf.putShort((short)privetServicePort);	// port
		buf.put(hostnameBuf.duplicate());		// [printer name].local


		// 4th answer : A ======
		buf.put(hostnameBuf.duplicate());		// [printer name].local

		// define A
		buf.putShort((short)0x0001);	// TYPE = A
		buf.putShort((short)0x0001);	// CLASS = IN
		buf.putInt(120);				// TTL = 120sec

		buf.putShort((short)0x04);		// RD Length
		InetAddress addr = Inet4Address.getByName(privetServiceIp);
		int ipv4 = 0;
		for ( byte b : addr.getAddress() )
		{
			ipv4 <<= 8;
			ipv4 |= (b & 0xFF);
		}
		buf.putInt(ipv4);				// x.x.x.x


		// 4th answer : AAAA ======
		buf.put(hostnameBuf.duplicate());		// [printer name].local

		buf.putShort((short)0x001C);	// TYPE = AAAA
		buf.putShort((short)0x0001);	// CLASS = IN
		buf.putInt(120);				// TTL = 120sec

		buf.putShort((short)0x10);		// RD Length
		buf.putInt(0x00000000);			// IPv6 00000::FFFF::x.x.x.x
		buf.putInt(0x00000000);
		buf.putInt(0x0000FFFF);
		buf.putInt(ipv4);

		buf.flip();
		return buf;
	}

	@Override
	public void setup() throws RejectedExecutionException
	{
		try
		{
			this.channel = open();
			//this.selector = prepareNonBlocking(this.channel, SelectionKey.OP_READ);
			this.recvBuf = ByteBuffer.allocate(1024);

			//
			advertise();
		}
		catch(Exception e)
		{
			throw new RejectedExecutionException(e.getMessage(), e);
		}
	}

	@Override
	public boolean working()
	{
		int count = 0;

		// TODO listen QUERY and ANSWER
		try { Thread.sleep(1000); } catch(InterruptedException e) { }

		return true;
	}

	@Override
	public int stopped(boolean abort)
	{
		return 0;
	}
}
