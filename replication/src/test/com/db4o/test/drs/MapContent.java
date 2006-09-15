package com.db4o.test.drs;

public class MapContent {

	private String name;

	public MapContent() {

	}

	public MapContent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "name = " + name;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final MapContent that = (MapContent) o;

		if (!name.equals(that.name)) return false;

		return true;
	}

	public int hashCode() {
		return name.hashCode();
	}
}
