package model;

import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Nov 29, 2006
 * Time: 11:52:46 AM
 */
public class Level3 {
	private int k;
	private int j;
	private List<Level4> children = new ArrayList<Level4>();


	public Level3() {
	}

	public Level3(int k, int j) {
		this.k = k;

		this.j = j;
	}

	public void addLevel4(Level4 l4) {
		children.add(l4);
	}
}
