package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * Login expects two Strings: 1) username 2) password
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 12:14:48 AM
 */
public class LoginOperationHandler implements OperationHandler {
	public Object handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		String username = (String) oin.readObject();
		String password = (String) oin.readObject();
		//System.out.println("username:" + username + " password:" + password);
		String passToCompare = (String) context.getAccessMap().get(username);
		boolean ok = false;
		if (passToCompare != null && passToCompare.equals(password)) {
			ok = true;
			oout.writeBoolean(true);
			oout.writeInt(context.getClientId());
		} else {
			oout.writeBoolean(false);
			oout.writeInt(0);
		}
		oout.flush();
		return ok;
	}
}
