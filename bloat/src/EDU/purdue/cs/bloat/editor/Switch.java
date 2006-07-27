/**
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and other
 * materials related to such distribution and use acknowledge that the
 * software was developed at Purdue University, West Lafayette, IN by
 * Antony Hosking, David Whitlock, and Nathaniel Nystrom.  No charge
 * may be made for copies, derivations, or distributions of this
 * material without the express written consent of the copyright
 * holder.  Neither the name of the University nor the name of the
 * author may be used to endorse or promote products derived from this
 * material without specific prior written permission.  THIS SOFTWARE
 * IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 *
 * <p>
 * Java is a trademark of Sun Microsystems, Inc.
 */

package EDU.purdue.cs.bloat.editor;

/**
 * Switch is used to hold the lookup values and branch targets of a tableswitch
 * or lookup switch instruction.
 * <p>
 * The tableswitch low-to-high range of values is represented by storing each
 * lookup value in the range. This allows the tableswitch to be replaced with a
 * lookupswitch if branches are deleted.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class Switch {
	private Label defaultTarget;

	private Label[] targets;

	private int[] values;

	/**
	 * Constructor.
	 * 
	 * @param defaultTarget
	 *            The default target of the switch.
	 * @param targets
	 *            The non-default branch targets of the switch.
	 * @param values
	 *            The lookup values of the switch. This array must be the same
	 *            size as the targets array.
	 */
	public Switch(final Label defaultTarget, final Label[] targets,
			final int[] values) {
		this.defaultTarget = defaultTarget;
		this.targets = targets;
		this.values = values;
		sort();
		uniq();
	}

	/**
	 * Set the default target of the switch.
	 * 
	 * @param target
	 *            The default target of the switch.
	 */
	public void setDefaultTarget(final Label target) {
		defaultTarget = target;
	}

	/**
	 * Get the default target of the switch.
	 * 
	 * @return The default target of the switch.
	 */
	public Label defaultTarget() {
		return defaultTarget;
	}

	/**
	 * Get the non-default branch targets of the switch. The targets are sorted
	 * by the corresponding lookup value.
	 * 
	 * @return The non-default branch targets of the switch.
	 */
	public Label[] targets() {
		return targets;
	}

	/**
	 * Get the lookup values of the switch, sorted low to high.
	 * 
	 * @return The lookup values of the switch.
	 */
	public int[] values() {
		return values;
	}

	/**
	 * Check if the all the values in the range of lookup values are contiguous.
	 * If they are, a table switch can be used. If not a lookupswitch can be
	 * used.
	 * 
	 * @return true if contiguous, false if not.
	 */
	public boolean hasContiguousValues() {
		return values.length == highValue() - lowValue() + 1;
	}

	/**
	 * Get the low value in the range the lookup values.
	 * 
	 * @return The low value.
	 */
	public int lowValue() {
		return values[0];
	}

	/**
	 * Get the high value in the range the lookup values.
	 * 
	 * @return The high value.
	 */
	public int highValue() {
		return values[values.length - 1];
	}

	/**
	 * Sort the targets and values arrays so that values is sorted low to high.
	 */
	private void sort() {
		quicksort(0, values.length - 1);
	}

	/**
	 * Utility function to sort the targets and values arrays so that values is
	 * sorted low to high.
	 * 
	 * @param p
	 *            The low index of the portion of the array to sort.
	 * @param r
	 *            The high index of the portion of the array to sort.
	 */
	private void quicksort(final int p, final int r) {
		if (p < r) {
			final int q = partition(p, r);
			quicksort(p, q);
			quicksort(q + 1, r);
		}
	}

	/**
	 * Utility function to sort the targets and values arrays so that values is
	 * sorted low to high.
	 * <p>
	 * Partition the arrays so that the values less than values[p] are to the
	 * left.
	 * 
	 * @param p
	 *            The low index of the portion of the array to sort.
	 * @param r
	 *            The high index of the portion of the array to sort.
	 * @return The index at which the partition finished.
	 */
	private int partition(final int p, final int r) {
		final int x = values[p];
		int i = p - 1;
		int j = r + 1;

		while (true) {
			do {
				j--;
			} while (values[j] > x);

			do {
				i++;
			} while (values[i] < x);

			if (i < j) {
				final int v = values[i];
				values[i] = values[j];
				values[j] = v;

				final Label t = targets[i];
				targets[i] = targets[j];
				targets[j] = t;
			} else {
				return j;
			}
		}
	}

	/**
	 * Remove duplicates from the values and targets arrays.
	 */
	private void uniq() {
		if (values.length == 0) {
			return;
		}

		final int[] v = new int[values.length];
		final Label[] t = new Label[values.length];

		v[0] = values[0];
		t[0] = targets[0];

		int j = 1;

		for (int i = 1; i < values.length; i++) {
			if (v[j - 1] != values[i]) {
				v[j] = values[i];
				t[j] = targets[i];
				j++;
			}
		}

		values = new int[j];
		System.arraycopy(v, 0, values, 0, j);

		targets = new Label[j];
		System.arraycopy(t, 0, targets, 0, j);
	}

	/**
	 * Convert the operand to a string.
	 * 
	 * @return A string representation of the operand.
	 */
	public String toString() {
		return "" + values.length + " pairs";
	}
}
