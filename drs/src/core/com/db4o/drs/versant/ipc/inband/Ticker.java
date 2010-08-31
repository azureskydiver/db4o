package com.db4o.drs.versant.ipc.inband;

public abstract class Ticker implements Runnable {

	private Thread thread;
	private volatile boolean running = true;
	private final long sleepInMillis;

	public Ticker(String name, long sleepInMillis) {
		this.sleepInMillis = sleepInMillis;
		thread = new Thread(this, name);
		thread.setDaemon(true);
	}

	public abstract boolean tick();

	public void run() {
		while(running) {
			snooze();
			if (!running) {
				break;
			}
			if (!tick()) {
				break;
			}
		}
	}

	private synchronized void snooze() {
		try {
			wait(sleepInMillis);
		} catch (InterruptedException e) {
			running = false;
		}
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		running = false;
	}

	public void join() throws InterruptedException {
		thread.join();
	}
	
	public boolean isRunning() {
		return running;
	}
	
}