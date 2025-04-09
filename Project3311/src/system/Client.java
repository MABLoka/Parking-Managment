package system;

public abstract class Client {
	protected String name;
	protected int id;
	protected String email;
	protected String password;
	protected String type;
	protected double parking_rate;

	public abstract void setRate();

	public Client(String name, int id, String email, String password, String type) {
		super();
		this.name = name;
		this.id = id;
		this.email = email;
		this.password = password;
		this.type = type;
		// Initialize parking_rate by calling the subclass's implementation.
		setRate();
	}

	public void book() {
		// Booking logic (if needed)
	}

	public int getId() {
		return id;
	}
	
	public abstract int getUniqueId();
	
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

	public student(String name, int id, String email, String password, int unique_id, String type) {
		super(name, id, email, password, type);
		this.student_id = unique_id;
		setRate();
	}

	@Override
	public void setRate() {
		this.parking_rate = 2;
	}

	@Override
	public int getUniqueId() {
		// TODO Auto-generated method stub
		return student_id;
	}
}

class faculty extends Client {
	protected int faculty_id;

	public faculty(String name, int id, String email, String password, int unique_id, String type) {
		super(name, id, email, password, type);
		this.faculty_id = unique_id;
		setRate();
	}

	@Override
	public void setRate() {
		this.parking_rate = 3;
	}
	@Override
	public int getUniqueId() {
		// TODO Auto-generated method stub
		return faculty_id;
	}
}

class staff extends Client {
	protected int staff_id;

	public staff(String name, int id, String email, String password, int unique_id, String type) {
		super(name, id, email, password, type);
		this.staff_id = unique_id;
		setRate();
	}

	@Override
	public void setRate() {
		this.parking_rate = 3;
	}
	@Override
	public int getUniqueId() {
		// TODO Auto-generated method stub
		return staff_id;
	}
}

class visitor extends Client {
	public visitor(String name, int id, String email, String password, String type) {
		super(name, id, email, password, type);
		setRate();
	}

	@Override
	public void setRate() {
		this.parking_rate = 4;
	}
	@Override
	public int getUniqueId() {
		// TODO Auto-generated method stub
		return 0;
	}
}

class clientFactory {
	public static Client getClientType(String clientType, String name, int id, String email, String password, int unique_id) {
		if(clientType == null) {
			return null;
		} else if(clientType.equalsIgnoreCase("STUDENT")) {
			return new student(name, id, email, password, unique_id, clientType);
		} else if(clientType.equalsIgnoreCase("FACULTY")) {
			return new faculty(name, id, email, password, unique_id, clientType);
		} else if(clientType.equalsIgnoreCase("STAFF")) {
			return new staff(name, id, email, password, unique_id, clientType);
		} else if(clientType.equalsIgnoreCase("VISITOR")) {
			return new visitor(name, id, email, password, clientType);
		} else {
			return null;
		}
	}
}
