/* 
This file is part of the PolePosition database benchmark
http://www.polepos.org

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

package org.polepos.teams.db4o;

import java.util.*;

import org.polepos.circuits.magnycours.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

public class MagnycoursDb4o extends Db4oDriver implements
		MagnycoursDriver {

	
	@Override
	public void configure(Configuration config) {
		
	}
	
	private List _list;

	public void addFirstElement() {
		List list = retrieveList();
		list.add(0, new MagnycoursItem(0));
		db().commit();
	}

	public void addLastElement() {
		List list = retrieveList();
		list.add(new MagnycoursItem(0));
		db().commit();
	}

	public void addMiddleElement() {
		List list = retrieveList();
		list.add(setup().getObjectCount()/2, new MagnycoursItem(0));
		db().commit();
	}
	
	public void getAllElements() {
		List list = retrieveList();
		Iterator iter = list.iterator();
		while(iter.hasNext()) {
			MagnycoursItem item = (MagnycoursItem) iter.next();
			addToCheckSum(item.checkSum());
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
	    Query q = db().query();
	    q.constrain(_list.getClass());
		ObjectSet os = q.execute();
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
