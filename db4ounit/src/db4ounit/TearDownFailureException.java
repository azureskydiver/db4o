package db4ounit;

public class TearDownFailureException extends RuntimeException {
	
	private static final long serialVersionUID = -5998743679496701084L;

	public TearDownFailureException(Exception cause) {
		super(cause);
	}
}
