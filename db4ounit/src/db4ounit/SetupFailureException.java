package db4ounit;

public class SetupFailureException extends RuntimeException {

	private static final long serialVersionUID = -7835097105469071064L;
	
	public SetupFailureException(Exception cause) {
		super(cause);
	}

}
