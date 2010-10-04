/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.*;
import com.versant.event.*;

public class VodEventClient {
	
	private final EventClient _client;
	
	public VodEventClient(EventConfiguration eventConfiguration, ExceptionListener exceptionListener) {
		_client = EventProcessorImpl.newEventClient(eventConfiguration);
	    _client.addExceptionListener(exceptionListener);
	}
	
	public EventChannel produceClassChannel(String className, boolean registerTransactionEvents) {
		String channelName = channelName(className);
		try {
			EventChannel channel = _client.getChannel (channelName);
			if(channel != null){
				if(DrsDebug.verbose){
					System.out.println("Reusing existing channel " + channelName);
				}
				return channel;
			}
			if(DrsDebug.verbose){
				System.out.println("Creating new channel " + channelName);
			}
			ClassChannelBuilder builder = new ClassChannelBuilder (className);
			if(registerTransactionEvents){
				builder.addEventType(EventTypes.BEGIN_TRANSACTION);
				builder.addEventType(EventTypes.END_TRANSACTION);
			}
			return _client.newChannel (channelName, builder);
		} catch (IOException e) {
			EventProcessorImpl.unrecoverableExceptionOccurred(e);
		}
		return null;
	}
	
	
	private String channelName(final String className) {
		String name =  "dRS_Channel_For_" + className;
		name = name.replaceAll("\\.", "");
		int beginIndex = name.length() - 32;
		if(beginIndex > 0){
			name = name.substring(beginIndex);
		}
		return name;
	}

	public void shutdown() {
		_client.shutdown();
	}

}
