/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package org.polepos.teams.db4o;

import java.util.*;

import org.polepos.circuits.magnycours.*;

import com.db4o.*;

public class MagnycoursDb4o extends Db4oDriver implements
		MagnycoursDriver {

	private List _list;

	public void getAllElements() {
		List list = retrieveList();
		for (int i = 0; i < list.size(); ++i) {
			getListElement(list, i);
		}
	}
	
	public void getFirstElement() {
		List list = retrieveList();
		getListElement(list, 0);
	}

	public void getLastElement() {
		List list = retrieveList();
		getListElement(list, setup().getObjectCount() - 1);
	}

	public void getMiddleElement() {
		List list = retrieveList();
		getListElement(list, setup().getObjectCount() / 2);
	}

	public void store() {
		generateList();
		begin();
		store(_list);
		commit();
	}

	private List retrieveList() {
		ObjectSet os = db().query(_list.getClass());
		return (List) os.next();
	}

	private void getListElement(List list, int index){
		MagnycoursItem item = (MagnycoursItem) list.get(index);
		addToCheckSum(item.checkSum());
	}

	private void generateList() {
		if (isP1FastCollection()) {
			_list = db().collections().newLinkedList();
		} else {
			_list = new ArrayList<MagnycoursItem>();
		}
		for (int i = 0; i < setup().getObjectCount(); ++i) {
			_list.add(new MagnycoursItem(i));
		}
	}

	private boolean isP1FastCollection() {
		Db4oCar car = (Db4oCar) car();
		return Db4oOptions.containsOption(car.options(),
				Db4oOptions.P1FAST_COLLECTION);
	}

}
