package pq.rapture.module.objects;

public class Friend {

	private String username;
	private String alias;

	public Friend(String username, String alias) {
		this.username = username;
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public String getName() {
		return username;
	}
}
