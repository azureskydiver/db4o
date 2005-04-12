/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class MQueryExecute extends MsgObject {
	boolean processMessageAtServer(YapSocket sock) {
		Transaction trans = getTransaction();
		YapStream stream = getStream();
		QResult qr = new QResult(trans);
		this.unmarshall();
		QQuery query = (QQuery) stream.unmarshall(payLoad);
		query.unmarshall(getTransaction());
		synchronized (stream.i_lock) {
			try {
				query.executeLocal(qr);
			} catch (Exception e) {
				// 
				qr = new QResult(getTransaction());
			}
		}
		writeQueryResult(getTransaction(), qr, sock);
		return true;
	}
}