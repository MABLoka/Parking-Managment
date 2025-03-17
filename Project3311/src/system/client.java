package system;

public abstract class client {
	String name;
	int id;
	String email;
	String password;
	
	double parking_rate;
	
	public abstract void set_rate();
	public void book() {
		
	}
}

class student extends client{
	String student_id;

	@Override
	public void set_rate() {
		// TODO Auto-generated method stub
		
	}
}

class faculty extends client{
	String faculty_id;

	@Override
	public void set_rate() {
		// TODO Auto-generated method stub
		
	}
} 

class staff extends client{
	String staff_id;

	@Override
	public void set_rate() {
		// TODO Auto-generated method stub
		
	}
} 

class visitor extends client{

	@Override
	public void set_rate() {
		// TODO Auto-generated method stub
		
	}
	
} 