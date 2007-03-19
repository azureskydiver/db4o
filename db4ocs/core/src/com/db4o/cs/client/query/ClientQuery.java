package com.db4o.cs.client.query;

import java.io.Serializable;
import java.util.*;

import com.db4o.ObjectSet;
import com.db4o.ObjectContainer;
import com.db4o.cs.client.*;
import com.db4o.cs.common.query.QueryConverter;
import com.db4o.query.*;

/**
 *
 * User: treeder
 * Date: Nov 25, 2006
 * Time: 4:10:46 PM
 */
public class ClientQuery implements Query, Serializable {
	// didn't work using existing QQuery since it uses YapStream and what not
	//private QQuery qquery;
	private String fieldName;
	private List<Constraint> constraints = new ArrayList();
	private Map<String, ClientQuery> children = new HashMap<String, ClientQuery>();
	private int order = 0;
	private static final int ASC = 1;
	private static final int DESC = -1;
	private transient ObjectContainer client;

	/**
	 * This can accept a normal db4o ObjectContainer or a Db4oClient.
	 * @param client
	 * @param fieldName
	 */
	public ClientQuery(ObjectContainer client, String fieldName) {
		this.client = client;
		this.fieldName = fieldName;
	}

	/**
	 * I don't like requiring the query to have an open client just to create it.
	 * You should create a query, then call client.execute(query);, not query.execute();
	 * @param client
	 */
	public ClientQuery(ObjectContainer client){
		this.client = client;
	}

	public ClientQuery() {

	}



	public Constraint constrain(Object constraint) {
		// todo: if it's a Class, then send special format or just className since class might not be on server
		ClientConstraint c = new ClientConstraint(constraint);
		constraints.add(c);
		return c;
	}

	public Constraints constraints() {
		throw new UnsupportedOperationException("Use List#getConstraints() instead.");
	}


	public List<Constraint> getConstraints() {
		return constraints;
	}

	public Query descend(String fieldName) {
		ClientQuery q = children.get(fieldName);
		if(q == null){
			q = new ClientQuery(client, fieldName);
			children.put(fieldName, q);
		}
		return q;
	}

	public ObjectSet execute() {
		try {
			if(client instanceof Db4oClient){
				Db4oClient c = (Db4oClient) client;
				return new ObjectSetListWrapper(c.execute(this));
			} else {
				Query q = client.query();
				QueryConverter.applyConstraints(q, this);
				return q.execute();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Query orderAscending() {
		order = ASC;
		return this;
	}

	public Query orderDescending() {
		order = DESC;
		return this;
	}

	public Query sortBy(QueryComparator comparator) {
		throw new UnsupportedOperationException();
	}

	public Query sortBy(Comparator comparator) {
		throw new UnsupportedOperationException();
	}

	public String getFieldName() {
		return fieldName;
	}

	public Map<String, ClientQuery> getChildren() {
		return children;
	}
}
