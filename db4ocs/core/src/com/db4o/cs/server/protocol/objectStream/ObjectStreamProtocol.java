package com.db4o.cs.server.protocol.objectStream;

import com.db4o.cs.server.protocol.Protocol;
import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.server.DefaultSession;
import com.db4o.cs.common.Operations;
import com.db4o.cs.common.Config;
import com.db4o.cs.common.util.Log;

import java.io.*;
import java.net.SocketException;
import java.util.List;
import java.util.ArrayList;

/**
 * This is based on the protocol design at http://docs.google.com/View?docid=adb3xqft39h9_30c42mn6
 * <p/>
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 11:54:32 PM
 */
public class ObjectStreamProtocol implements Protocol {
	private Context context;
	protected Session session;

	/**
	 * @param context
	 * @param session
	 */
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
			byte operation;
			boolean running = true;
			while (running) {
				operation = oin.readByte();
				Log.print("operation received: " + operation);
				if (operation == Operations.BATCH) {
					// then this will handle a bunch of incoming requests in a row without responding until the end
					/*
					Can use the below if all incoming are the same, but might be a good idea to let any operation come in?
					byte subOp = oin.readByte();
					OperationHandler opHandler = getOperationHandler(subOp);
					if (opHandler == null) {
						throw new UnsupportedOperationException("Operation '" + subOp + "' not supported.");
					}*/
					int size = oin.readInt();
					List ret = new ArrayList();
					for (int j = 0; j < size; j++) {
						byte subOp = oin.readByte();
						Object ret2 = handle(subOp, oin, oout);
						// for set, this returns object id, for delete returns num deleted
						ret.add(ret2);
					}
					if (Config.BLOCKING) {
						oout.writeObject(ret);
						oout.flush();
					}

				} else {
					Object ret = handle(operation, oin, oout);
					if (session.isClosed()) {
						running = false;
					} else {
						if (Config.BLOCKING && ret != null) {
							oout.writeObject(ret);
							oout.flush();
						}
					}

				}
				oout.flush();
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} catch (SocketException e) {
			System.out.println("Socket exception. closing. " + e.getMessage());
		}
	}

	public Object handle(byte operation, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		OperationHandler opHandler = getOperationHandler(operation);
		if (opHandler == null) {
			throw new UnsupportedOperationException("Operation '" + operation + "' not supported.");
		}
		Object ret = opHandler.handle(context, session, oin, oout);
		return ret;
	}

	public OperationHandler getOperationHandler(byte operation) {
		if (operation == Operations.LOGIN) {
			return new LoginOperationHandler();
		} else if (operation == Operations.SET) {
			return new SetOperationHandler();
		} else if (operation == Operations.QUERY) {
			return new QueryOperationHandler();
		} else if (operation == Operations.COMMIT) {
			return new CommitOperationHandler();
		} else if (operation == Operations.DELETE) {
			return new DeleteOperationHandler();
		} else if (operation == Operations.CLOSE) {
			return new CloseOperationHandler();
		} else if (operation == Operations.GETBYID) {
			return new GetByIDOperationHandler();
		}
		return null;
	}

	private void readHeaders(ObjectInputStream oin) throws IOException, ClassNotFoundException {
		//System.out.println("reading headers");
		// first piece is the protocol version as a String
		String version = oin.readUTF();
//System.out.println("version:" + version);
	}
}
