package com.db4o.events;

public interface EventRegistry {
	
	public Event4 queryStarted();
	
	public Event4 queryFinished();
}

