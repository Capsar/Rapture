package pq.rapture.render.altmanager;

public class Alt {
	private String aUserName;
	private String aPassword;
	private String aInGameName;

	private boolean premium;

	public Alt(String username, String password) {
		this.premium = true;
		this.aUserName = username;
		this.aPassword = password;
	}

	public Alt(String username, String password, String ingamename) {
		this.premium = true;
		this.aUserName = username;
		this.aPassword = password;
		this.aInGameName = ingamename;
	}

	public Alt(String username) {
		this.premium = false;
		this.aUserName = username;
		this.aPassword = "N/A";
	}

	public String getFileLine() {
		if (this.premium) {
			if (this.aInGameName == null) {
				return this.aUserName.concat(":").concat(this.aPassword);
			} else {
				return this.aUserName.concat(":").concat(this.aPassword).concat(":").concat(this.aInGameName);
			}
		} else {
			return this.aUserName;
		}
	}

	public String getUsername() {
		return this.aUserName;
	}

	public String getNickname() {
		return this.aInGameName;
	}

	public String getPassword() {
		return this.aPassword;
	}

	public boolean isPremium() {
		return this.premium;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aInGameName == null) ? 0 : aInGameName.hashCode());
		result = prime * result + ((aPassword == null) ? 0 : aPassword.hashCode());
		result = prime * result + ((aUserName == null) ? 0 : aUserName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Alt other = (Alt) obj;
		if (aInGameName == null) {
			if (other.aInGameName != null) return false;
		} else if (!aInGameName.equals(other.aInGameName)) return false;
		if (aPassword == null) {
			if (other.aPassword != null) return false;
		} else if (!aPassword.equals(other.aPassword)) return false;
		if (aUserName == null) {
			if (other.aUserName != null) return false;
		} else if (!aUserName.equals(other.aUserName)) return false;
		return true;
	}
}
