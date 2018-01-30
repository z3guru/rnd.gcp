/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp.google;

import java.util.List;

public class RegistrationVO
{
	private boolean success;
	private List<PrinterVO> printers;

	private String invite_url;
	private String polling_url;
	private String registration_token;

	// GETTER/SETTER methods ===============================
	public boolean isSuccess() { return success; }
	public void setSuccess(boolean success) { this.success = success; }

	public List<PrinterVO> getPrinters() { return printers; }
	public void setPrinters(List<PrinterVO> printers) { this.printers = printers; }

	public String getInvite_url() { return invite_url; }
	public void setInvite_url(String invite_url) { this.invite_url = invite_url; }

	public String getPolling_url() { return polling_url; }
	public void setPolling_url(String polling_url) { this.polling_url = polling_url; }

	public String getRegistration_token() { return registration_token; }
	public void setRegistration_token(String registration_token) { this.registration_token = registration_token; }
}
