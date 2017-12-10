/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp;

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
 * Created by jaeda on 2017. 12. 4..
 */
public class MulticastDnsClient implements JobRunnable
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
			NetworkInterface ni = NetworkInterface.getByName("en1");

			// "StandardProtocolFamily.INET"을 생략하면
			// 예외가 발생함 ==> IPv6 socket cannot join IPv4 multicast group
			ch = DatagramChannel.open(StandardProtocolFamily.INET);
			ch.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			ch.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
			ch.bind(new InetSocketAddress(PORT_mDNS));
			MembershipKey key = ch.join(group, ni);
			if ( !key.isValid() ) throw new IOException("fail to join");

			this.joined = key.isValid();
			return ch;
		}
		catch(IOException e)
		{
			throw e;
		}
		finally
		{
			try { ch.close(); } catch(Exception e) { }
		}
	}

	private Selector prepareNonBlocking(SelectableChannel ch, int option) throws IOException
	{
		ch.configureBlocking(false);

		Selector selector = Selector.open();
		ch.register(this.selector, SelectionKey.OP_READ);
		if ( logger.isTraceEnabled() ) logger.trace("prepareNonBlocking[ch={}] regist selector", ch);

		return selector;
	}

	private void checkMessage(SocketAddress sender, ByteBuffer buf)
	{
		// skip 2bytes of header
		buf.position(buf.position() + 2);
		int qbit = buf.get() & 0x80;

		// query만 처리한다
		if ( qbit == 0 )
		{
			logger.debug("query...");

			// TODO check name

			// advertise myself
			advertise(sender);
		}
	}

	private void advertise(SocketAddress sender)
	{
		ByteBuffer buf = ByteBuffer.allocate(512);

		buf.put(new byte[] { 0x00, 0x00, (byte)0x84, 0x00});

		String[] svc = new String[] { "_privet", "_tcp", "local" };
		String[] subtype = new String[] { "Z3-Printer", "_printer", "_sub", "_privet", "_tcp" };


	}

	@Override
	public void setup() throws RejectedExecutionException
	{
		try
		{
			this.channel = open();
			//this.selector = prepareNonBlocking(this.channel, SelectionKey.OP_READ);
			this.recvBuf = ByteBuffer.allocate(1024);
			this.sendBuf = ByteBuffer.allocate(512);
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

		try
		{
			this.recvBuf.clear();
			SocketAddress sender = this.channel.receive(this.recvBuf);
			this.recvBuf.flip();
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
			return false;
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	@Override
	public int stopped(boolean abort)
	{
		return 0;
	}
}
