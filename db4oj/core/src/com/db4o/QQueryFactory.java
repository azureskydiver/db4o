package com.db4o;

public class QQueryFactory {
	public static QQuery createQQuery(Transaction a_trans, QQuery a_parent, String a_field) {
		return new QQuery(a_trans,a_parent,a_field);
	}
}
