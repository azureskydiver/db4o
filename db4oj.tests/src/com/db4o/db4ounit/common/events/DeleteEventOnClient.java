/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */
package com.db4o.db4ounit.common.events;

import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;

import db4ounit.Assert;
import db4ounit.CodeBlock;
import db4ounit.extensions.fixtures.OptOutSolo;

public class DeleteEventOnClient extends EventsTestCaseBase implements OptOutSolo {
	public static void main(String[] args) {
		new DeleteEventOnClient().runAll();
	}
	
	public void testAttachingToDeletingEventThrows() {
		if (isMTOC()) return;
		
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				eventRegistry().deleting().addListener(new EventListener4(){
					public void onEvent(Event4 e, EventArgs args) {
					}
				});
			}
		});			
	}
	
	public void testAttachingToDeleteEventThrows() {
			if (isMTOC()) return;
				
			Assert.expect(IllegalArgumentException.class, new CodeBlock() {
				public void run() throws Throwable {
					eventRegistry().deleted().addListener(new EventListener4(){
						public void onEvent(Event4 e, EventArgs args) {
						}
					});
				}
			});			
	}
}
