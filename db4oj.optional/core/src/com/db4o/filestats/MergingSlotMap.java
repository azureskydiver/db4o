package com.db4o.filestats;

import java.util.*;

import com.db4o.internal.slots.*;

@decaf.Ignore
public class MergingSlotMap {

	private TreeSet<Slot> _slots = new TreeSet<Slot>(new Comparator<Slot>() {
		public int compare(Slot s1, Slot s2) {
			if(s1.address() == s2.address()) {
				return 0;
			}
			if(s1.address() > s2.address()) {
				return 1;
			}
			return -1;
		}
	});

	public void add(Slot slot) {
		_slots.add(slot);
	}
	
	public List<Slot> merged() {
		List<Slot> mergedSlots = new ArrayList<Slot>();
		if(_slots.isEmpty()) {
			return mergedSlots;
		}
		Iterator<Slot> iter = _slots.iterator();
		Slot mergedSlot = iter.next();
		while(iter.hasNext()) {
			Slot curSlot = iter.next();
			if(mergedSlot.address() + mergedSlot.length() == curSlot.address()) {
				mergedSlot = new Slot(mergedSlot.address(), mergedSlot.length() + curSlot.length());
			}
			else {
				mergedSlots.add(mergedSlot);
				mergedSlot = curSlot;
			}
		}
		mergedSlots.add(mergedSlot);
		return mergedSlots;
	}
	
	public List<Slot> gaps(long length) {
		List<Slot> merged = merged();
		List<Slot> gaps = new ArrayList<Slot>();
		if(merged.isEmpty()) {
			return gaps;
		}
		Iterator<Slot> iter = merged.iterator();
		Slot prevSlot = iter.next();
		if(prevSlot.address() > 0) {
			gaps.add(new Slot(0, prevSlot.address()));
		}
		while(iter.hasNext()) {
			Slot curSlot = iter.next();
			int gapStart = prevSlot.address() + prevSlot.length();
			gaps.add(new Slot(gapStart, curSlot.address() - gapStart));
			prevSlot = curSlot;
		}
		int afterlast = prevSlot.address() + prevSlot.length();
		if(afterlast < length) {
			gaps.add(new Slot(afterlast, (int)(length - afterlast)));
		}
		return gaps;
	}
}
