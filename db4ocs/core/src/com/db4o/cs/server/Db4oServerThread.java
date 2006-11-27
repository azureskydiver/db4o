package com.db4o.cs.server;

import com.db4o.cs.server.protocol.objectStream.ObjectStreamProtocol;
import com.db4o.cs.server.protocol.Protocol;
import com.db4o.cs.server.protocol.protocol1.Protocol1;

import java.io.*;
import java.net.Socket;

/**
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 9:21:08 PM
 */
public class Db4oServerThread extends Thread {
	private Context context;
	private Socket socket = null;

	public Db4oServerThread(Context context, Socket socket) {
		super(Db4oServerThread.class.getName());
		this.context = context;
		this.socket = socket;
	}

	public void run() {
		//System.out.println("Connection accepted.");
		try {
			OutputStream out = //new BufferedOutputStream(
					socket.getOutputStream();
			InputStream in = //new BufferedInputStream(
					 socket.getInputStream();

			Session session = new DefaultSession();

			try {
				Protocol protocol = //new ObjectStreamProtocol(context, session);
						new Protocol1(context, session);
				protocol.handle(in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}

			out.close();
			in.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
