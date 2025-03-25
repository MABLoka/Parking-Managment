package system;

public abstract class Client {
	protected String name;
	protected int id;
	protected String email;
	protected String password;
	protected double parking_rate;

	public abstract void setRate();

	public Client(String name, int id, String email, String password) {
		super();
		this.name = name;
		this.id = id;
		this.email = email;
		this.password = password;
		// Initialize parking_rate by calling the subclass's implementation.
		setRate();
	}

	public void book() {
		// Booking logic (if needed)
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	// New accessor for parking_rate.
	public double getParkingRate() {
		return parking_rate;
	}
}

class student extends Client {
	protected int student_id;

	public student(String name, int id, String email, String password, int unique_id) {
		super(name, id, email, password);
		this.student_id = unique_id;
		setRate();
	}

	@Override
	public void setRate() {
		this.parking_rate = 2;
	}
}

class faculty extends Client {
	protected int faculty_id;

	public faculty(String name, int id, String email, String password, int unique_id) {
		super(name, id, email, password);
		this.faculty_id = unique_id;
		setRate();
	}

	@Override
	public void setRate() {
		this.parking_rate = 3;
	}
}

class staff extends Client {
	protected int staff_id;

	public staff(String name, int id, String email, String password, int unique_id) {
		super(name, id, email, password);
		this.staff_id = unique_id;
		setRate();
	}

	@Override
	public void setRate() {
		this.parking_rate = 3;
	}
}

class visitor extends Client {
	public visitor(String name, int id, String email, String password) {
		super(name, id, email, password);
		setRate();
	}

	@Override
	public void setRate() {
		this.parking_rate = 4;
	}
}

class clientFactory {
	public static Client getClientType(String clientType, String name, int id, String email, String password, int unique_id) {
		if(clientType == null) {
			return null;
		} else if(clientType.equalsIgnoreCase("STUDENT")) {
			return new student(name, id, email, password, unique_id);
		} else if(clientType.equalsIgnoreCase("FACULTY")) {
			return new faculty(name, id, email, password, unique_id);
		} else if(clientType.equalsIgnoreCase("STAFF")) {
			return new staff(name, id, email, password, unique_id);
		} else if(clientType.equalsIgnoreCase("VISITOR")) {
			return new visitor(name, id, email, password);
		} else {
			return null;
		}
	}
}
