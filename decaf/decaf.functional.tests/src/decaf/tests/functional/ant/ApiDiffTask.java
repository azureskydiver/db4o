package decaf.tests.functional.ant;

import java.io.*;

import org.apache.tools.ant.*;

/**
 * Checks that two jars have the same API surface (same public types with same public methods).
 */
public class ApiDiffTask extends Task {
	
	private File _from;
	private File _to;

	public void setFrom(File from) {
		_from = from;
	}
	
	public void setTo(File to) {
		_to = to;
	}

	@Override
	public void execute() throws BuildException {
		if (null == _from) throw new IllegalStateException("Missing 'from'.");
		if (null == _to) throw new IllegalStateException("Missing 'to'.");
		
		try {
			final FailureHandler failureHandler = new FailureHandler();
			new ApiDiff(failureHandler, _from, _to).run();
			if (failureHandler.failures() > 0) {
				throw new BuildException("API surfaces do not match. "  + format(failureHandler) + " been reported.", getLocation());
			}
		} catch (IOException e) {
			throw new BuildException(e, getLocation());
		}
	}

	private String format(final FailureHandler failureHandler) {
		final int failures = failureHandler.failures();
		return failures == 1
			? "1 failure has"
			: failures + " failures have";
	}
	
	class FailureHandler implements ApiDiff.FailureHandler {
		
		private int count = 0;
		
		public int failures() {
			return count;
		}
		
		public void fail(String message) {
			log(message);
			++count;
		}
	}
}
