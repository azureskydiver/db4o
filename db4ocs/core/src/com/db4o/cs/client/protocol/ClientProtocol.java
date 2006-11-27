package com.db4o.cs.client.protocol;

import com.db4o.query.Query;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 7:32:36 AM
 */
public interface ClientProtocol {
	void writeHeaders() throws IOException;

	boolean login(String username, String password) throws IOException;

	void set(Object o) throws IOException;

	void commit() throws IOException;

	/**
	 * Just to get a quick query going, will change to full query
	 * @param aClass
	 */
	List query(Class aClass) throws IOException, ClassNotFoundException;

	void close() throws IOException;

	void delete(Object o) throws IOException;

	List execute(Query query) throws IOException, ClassNotFoundException;
}
