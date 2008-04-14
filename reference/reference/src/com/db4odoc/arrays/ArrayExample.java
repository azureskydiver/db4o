package com.db4odoc.arrays;

import java.io.*;

import com.db4o.*;

public class ArrayExample {

	public static void main(String[] args) {
		Team team = new Team(3);
		Pilot pilot = new Pilot("Pilot # 1");
		team.addPilot(pilot);
		pilot = new Pilot("Pilot # 2");
		team.addPilot(pilot);
		pilot = new Pilot("Pilot # 3");
		team.addPilot(pilot);
		new File("reference.db4o").delete();
		ObjectContainer db = Db4o.openFile("reference.db4o");
		db.store(team);
		db.close();
	}

	
	private static class Pilot {
		private String _name;
		 
		public Pilot(String name){
			_name = name;
		}
		
		public String toString(){
			return _name;
		}
	}
	
	private static class Team {
		Pilot[] _pilots;
		int lastPos = 0;
		
		public Team(int size){
			_pilots = new Pilot[size];
		}
		
		public void addPilot(Pilot pilot){
			_pilots[lastPos ++] = pilot;
		}
		
		public String toString(){
			return _pilots.toString();
		}
	}
}
