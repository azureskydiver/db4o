package model;

import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Nov 29, 2006
 * Time: 11:57:11 AM
 */
public class Level3a {
	private String s;
	private int k;
	private List<Level4> children = new ArrayList<Level4>();


	public Level3a() {
	}

	public Level3a(String s, int k) {
		this.s = s;

		this.k = k;
	}

	public void addLevel4(Level4 l4) {
		children.add(l4);
	}
}
