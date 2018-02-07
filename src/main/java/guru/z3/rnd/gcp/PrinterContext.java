/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp;

import guru.z3.rnd.gcp.google.PrinterVO;
import guru.z3.rnd.gcp.google.RegistrationVO;

public class PrinterContext
{
	private static PrinterContext instance = new PrinterContext();

	public final static String 	X_CLOUDPRINT_PROXY	= "zcube";
	public final static String 	PRIVET_SVC_IP		= "127.0.0.1";
	public final static int 	PRIVET_SVC_PORT		= 8090;

	private final String printerName = "ZCUBEPRINTER2";
	private final String clientId = "....apps.googleusercontent.com";
	private RegistrationVO registration;
	private PrinterVO printer;

	private PrinterContext()
	{
	}

	public static PrinterContext getInstance()
	{
		return instance;
	}

	public void clear()
	{
		this.registration = null;
		this.printer = null;
	}

	//GETTER/SETTER methods ////////////////////////////////
	public String getPrinterName() { return printerName; }

	public String getClientId() { return clientId; }

	public RegistrationVO getRegistration() { return registration; }
	public void setRegistration(RegistrationVO registration) { this.registration = registration; }

	public PrinterVO getPrinter() { return printer; }
	public void setPrinter(PrinterVO printer) { this.printer = printer; }
}
