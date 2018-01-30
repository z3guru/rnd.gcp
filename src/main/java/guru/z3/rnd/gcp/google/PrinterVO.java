/*
This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
*/
package guru.z3.rnd.gcp.google;

public class PrinterVO
{
	private String id;
	private String displayName;
	private String name;
	private String status;
	private String type;
	private String gcpVersion;

	// GETTER/SETTER methods ===============================
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public String getDisplayName() { return displayName; }
	public void setDisplayName(String displayName) { this.displayName = displayName; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public String getGcpVersion() { return gcpVersion; }
	public void setGcpVersion(String gcpVersion) { this.gcpVersion = gcpVersion; }
}
