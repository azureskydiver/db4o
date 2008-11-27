/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.events;

import com.db4o.events.*;
import com.db4o.foundation.*;

import db4ounit.fixtures.*;

public class ExceptionPropagationInEventsTestVariables {
	final static FixtureVariable EVENT_SELECTOR = new FixtureVariable("event");
	
	final static FixtureProvider EventProvider = new SimpleFixtureProvider(EVENT_SELECTOR,
		new EventInfo("query", // 0
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.activated().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}
						}),

		new EventInfo("query", // 1
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.activating().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}			
						}),
		
		new EventInfo("delete", // 2
						new Procedure4<EventRegistry>(){
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.deleted().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}					
						}),
		
		new EventInfo("delete", // 3
						new Procedure4<EventRegistry>(){
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.deleting().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}
						}),

		new EventInfo("insert", // 4
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.committing().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										if (_firstTime) {
											_firstTime = false;
											throw new NotImplementedException();
										}
									}
									
									private boolean _firstTime = true;
								});
							}
						}),
			
		new EventInfo("insert", // 5
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.committed().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										if (_firstTime) {
											_firstTime = false;
											throw new NotImplementedException();
										}
									}
									
									private boolean _firstTime = true;
								});
							}
						}),

		new EventInfo("insert", // 6
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.creating().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}
						}),

		new EventInfo("insert", // 7
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.created().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}
						}),

		new EventInfo("query", // 8
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.instantiated().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}
						}),
			
		new EventInfo("update", // 9
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.updating().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}
						}),

		new EventInfo("update", // 10
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.updated().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}					
						}),
			
		new EventInfo("query", // 11
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.queryStarted().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}
						}),
		
		new EventInfo("query", // 12
						new Procedure4<EventRegistry>() {
							public void apply(EventRegistry eventRegistry) {
								eventRegistry.queryFinished().addListener(new EventListener4() {
									public void onEvent(Event4 e, EventArgs args) {
										throw new NotImplementedException();				
									}			
								});
							}					
						}));
}