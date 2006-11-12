/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package demo.objectmanager.model;

import java.util.*;


/**
 * @exclude
 */
public class Collections {

	public static class Item implements Comparable{
		
		public String name;
		
		public Item(){
			
		}
		
		public Item(String name_){
			if(name_ == null){
				throw new IllegalArgumentException();
			}
			name = name_;
		}

		public int compareTo(Object o) {
			if(! (o instanceof Item)){
				throw new IllegalArgumentException();
			}
			Item other = (Item)o;
			
			return name.compareTo(other.name);
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof Item)){
				return false;
			}
			Item other = (Item)obj;
			return name.equals(other.name);
		}
		
		public int hashCode() {
			return name.hashCode();
		}
		
	}
	
	public List list;
	
	public ArrayList arrayList;
	
	public Vector vector;
	
	public Map map;
	
	public HashMap hashMap;
	
	public TreeMap treeMap;

	public Item[] array;

	
	public Collections(){
		
	}
	
	public static Object forDemo(){
		Collections collections = new Collections();
		collections.createDemoCollections();
		return collections;
	}
	
	private void createDemoCollections(){
		list = new ArrayList();
		arrayList = new ArrayList();
		vector = new Vector();
		map = new HashMap();
		hashMap = new HashMap();
		treeMap = new TreeMap();

		array = new Item[2];
		
		fillList(list);
		fillList(arrayList);
		fillList(vector);
		
		fillMap(map);
		fillMap(hashMap);
		fillMap(treeMap);

		fillArray(array);
	}

	private void fillArray(Item[] array) {
		array[0] = new Item("one");
		array[1] = new Item("two");
	}

	private void fillList(List list){
		list.add("one");
		list.add(new Item("two"));
	}
	
	private void fillMap(Map map){
		map.put(new Item("one"), new Item("one"));
		map.put(new Item("two"), new Item("two"));
	}

}
