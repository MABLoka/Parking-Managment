package system;

public abstract class client {
	protected String name;
	protected int id;
	protected String email;
	protected String password;
	protected double parking_rate;
	
	
	public client(String name, int id, String email, String password, double parking_rate) {
		super();
		this.name = name;
		this.id = id;
		this.email = email;
		this.password = password;
		this.parking_rate = parking_rate;
	}
	
	public void book() {
		
	}
}

class student extends client{
	protected int student_id;
	
	public student(String name, int id, String email, String password, double parking_rate, int unique_id) {
		super(name, id, email, password, parking_rate);
		this.student_id = unique_id;
		
	}
}

class faculty extends client{
	protected int faculty_id;
	
	public faculty(String name, int id, String email, String password, double parking_rate, int unique_id) {
		super(name, id, email, password, parking_rate);
		this.faculty_id = unique_id;
		
	}
	
} 

class staff extends client{
	protected int staff_id;
	
	public staff(String name, int id, String email, String password, double parking_rate, int unique_id) {
		super(name, id, email, password, parking_rate);
		this.staff_id = unique_id;
		
	}
} 

class visitor extends client{

	public visitor(String name, int id, String email, String password, double parking_rate) {
		super(name, id, email, password, parking_rate);
		
	}
} 

class clientFactory{
	public client getClientType(String clientType, String name, int id, String email, String password, double parking_rate, int unique_id) {
		if(clientType == null) {
			return null;
		}
		else if(clientType.equalsIgnoreCase("STUDENT")) {
			return new student( name,  id,  email,  password,  parking_rate,  unique_id);
		}
		else if(clientType.equalsIgnoreCase("FACULTY")) {
			return new faculty( name,  id,  email,  password,  parking_rate,  unique_id);
		}
		else if(clientType.equalsIgnoreCase("STAFF")) {
			return new staff( name,  id,  email,  password,  parking_rate,  unique_id);
		}
		else if(clientType.equalsIgnoreCase("VISITOR")) {
			return new visitor( name,  id,  email,  password,  parking_rate);
		}
		else {
			return null;
		}
	}
}
