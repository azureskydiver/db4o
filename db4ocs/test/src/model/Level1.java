package model;

import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Nov 29, 2006
 * Time: 11:52:35 AM
 */
public class Level1 {
	int x;
	String y;
	List<Level2> children = new ArrayList<Level2>();


	public Level1() {
	}

	public Level1(int x, String y) {
		this.x = x;

		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public List<Level2> getChildren() {
		return children;
	}

	public void setChildren(List<Level2> children) {
		this.children = children;
	}

	public void addLevel2(Level2 l2) {
		children.add(l2);
	}
}
