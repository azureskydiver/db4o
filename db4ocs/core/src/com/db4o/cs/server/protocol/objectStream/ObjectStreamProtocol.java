package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.Protocol;
import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;

import java.io.*;

/**
 * This is based on the protocol design at http://docs.google.com/View?docid=adb3xqft39h9_30c42mn6
 * <p/>
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 11:54:32 PM
 */
public class ObjectStreamProtocol implements Protocol {
	private Context context;
	private Session session;

	public ObjectStreamProtocol(Context context, Session session) {

		this.context = context;
		this.session = session;
	}

	public void handle(InputStream in, OutputStream out) throws IOException {
		ObjectInputStream oin = new ObjectInputStream(in);
		ObjectOutputStream oout = new ObjectOutputStream(out);
		try {
			readHeaders(oin);
			// now loop for operation
			String operation;
			while ((operation = (String) oin.readObject()) != null) {
				//System.out.println("operation received: " + operation);
				OperationHandler opHandler = getOperationHandler(operation);
				if (opHandler == null) {
					throw new UnsupportedOperationException("Operation '" + operation + "' not supported.");
				}
				opHandler.handle(context, session, oin, oout);
				if(opHandler instanceof CloseOperationHandler){
					// might be better to have a method on the handler, ie: close() or quit()
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}


	}

	private OperationHandler getOperationHandler(String operation) {
		if (operation.equals("login")) {
			return new LoginOperationHandler();
		} else if (operation.equals("set")) {
			return new SetOperationHandler();
		} else if (operation.equals("query")) {
			return new QueryOperationHandler();
		} else if (operation.equals("commit")) {
			return new CommitOperationHandler();
		} else if (operation.equals("delete")) {
			return new DeleteOperationHandler();
		} else if (operation.equals("close")) {
			return new CloseOperationHandler();
		}
		return null;
	}

	private void readHeaders(ObjectInputStream oin) throws IOException, ClassNotFoundException {
		System.out.println("reading headers");
		// first piece is the protocol version as a String
		String version = (String) oin.readObject();
		System.out.println("version:" + version);
	}
}
