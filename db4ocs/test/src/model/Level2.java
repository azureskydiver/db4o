package model;

import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Nov 29, 2006
 * Time: 11:52:41 AM
 */
public class Level2 {
	private double v;
	private long l;
	List<Level3> children1 = new ArrayList<Level3>();
	List<Level3a> children2 = new ArrayList<Level3a>();


	public Level2() {
	}

	public Level2(double v, long l) {
		this.v = v;

		this.l = l;
	}

	public void addLevel3a(Level3a l3a) {
		children2.add(l3a);
	}

	public void addLevel3(Level3 l3) {
		children1.add(l3);
	}
}
