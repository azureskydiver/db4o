package com.db4o.devtools.ant;

import java.io.*;

import org.apache.tools.ant.*;

public class WaitingForPatternTask extends Task {

	public class LineReader {

		private Thread thread;
		private BufferedReader reader;

		public LineReader(final InputStream in, final LineReaderListener listener) {
			reader = new BufferedReader(new InputStreamReader(in));
			thread = new Thread() {
				@Override
				public void run() {
					String line;
					try {
						while ((line = reader.readLine()) != null) {
							listener.lineReady(line);
						}
					} catch (IOException e) {
					} finally {
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
				};
			};
			thread.setDaemon(true);
			thread.start();
		}

		public void dispose() {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}

	public interface LineReaderListener {

		public void lineReady(String line);

	}

	private String pattern;
	private String command;
	private long timeout;
	private Thread timeoutThread;
	protected boolean timeoutError;
	private Process process;

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Override
	public void execute() throws BuildException {

		try {

			startProcess();

			setupStdReaders();

			setupTimeoutThread(process);

			waitForProcess();

			releaseTimeoutThread();

		} catch (IOException e) {
			throw new BuildException(e);
		}
	}

	private void releaseTimeoutThread() {
		Thread t = timeoutThread;
		timeoutThread = null;
		synchronized (t) {
			t.notifyAll();
		}
	}

	private void waitForProcess() {
		try {
			process.waitFor();
		} catch (InterruptedException e1) {
		}
		if (timeoutError) {
			throw new BuildException(WaitingForPatternTask.class.getSimpleName() + " task failed due to timeout");
		}
	}

	private void setupStdReaders() {
		new LineReader(process.getInputStream(), new LineReaderListener() {

			@Override
			public void lineReady(String line) {
				evaluatePattern(process, line);
			}

		});

		new LineReader(process.getErrorStream(), new LineReaderListener() {

			@Override
			public void lineReady(String line) {
				System.err.println(line);
			}

		});
	}

	private void startProcess() throws IOException {
		process = Runtime.getRuntime().exec(getCommand());
	}

	private void setupTimeoutThread(final Process process) {
		timeoutThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						this.wait(getTimeout());
					}
					if (timeoutThread != null) {
						timeoutError = true;
						process.destroy();
					}
				} catch (InterruptedException e) {
				}
			};
		};
		timeoutThread.setDaemon(true);
		timeoutThread.start();
	}

	private void evaluatePattern(final Process process, String line) {
		if (line.contains(getPattern())) {
			process.destroy();
		}
	}
}
