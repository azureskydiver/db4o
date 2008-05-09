package contacts;

public class Contact {

	private String _name;
	private String _email;

	public Contact(String name, String email) {
		_name = name;
		_email = email;
	}

	public String name() {
		return _name;
	}

	public String email() {
		return _email;
	}

}
