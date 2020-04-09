package pq.rapture.module.objects;

public class WayPoint {

	private double x;
	private double y;
	private double z;
	private String name;
	private String ip;

	public WayPoint(double x, double y, double z, String name, String ip) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
		this.ip = ip;

	}

	public WayPoint(String x, String y, String z, String name, String ip) {
		try {
			this.x = Double.parseDouble(x);
			this.y = Double.parseDouble(y);
			this.z = Double.parseDouble(z);
			this.name = name;
			this.ip = ip;

		} catch (Exception e) {

		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public String getIP() {
		return this.ip;
	}

}
