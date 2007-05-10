package com.db4o.ta.tests.collections.internal;

import com.db4o.*;
import com.db4o.ta.*;
import com.db4o.ta.internal.*;

/**
 * Shared implementation for a paged collection.
 */
public 
class PagedBackingStore /* TA BEGIN */ implements Activatable /* TA END */ {

	public final static int INITIAL_PAGE_COUNT = 16;	
	
	private Page[] _pages = new Page[INITIAL_PAGE_COUNT];
	private int _top = 0;

	// TA BEGIN
	transient Activator _activator;
	// TA END

	public PagedBackingStore() {
		addNewPage();
	}
	
	public boolean add(Object item) {
		// TA BEGIN
		activate();
		// TA END
		return getPageForAdd().add(item);
	}
	
	public int size() {
		// TA BEGIN
		activate();
		// TA END
		return _top * Page.PAGESIZE - lastPage().capacity();
	}
	
	public Object get(int itemIndex) {
		// TA BEGIN
		activate();
		// TA END
		Page page = pageHolding(itemIndex);
		return page.get(indexInPage(itemIndex));
	}

	private Page lastPage() {
		return (Page) _pages[_top - 1];
	}

	private Page getPageForAdd() {
		Page lastPage = lastPage();
		if (lastPage.atCapacity()) {
			lastPage = addNewPage();
		}
		return lastPage;
	}
	
	private Page addNewPage() {
		final Page page = new Page(_top);
		if(_top == _pages.length) {
			growPages();
		}
		_pages[_top] = page;
		_top++;
		return page;
	}
	
	private void growPages() {
		Page[] grown = new Page[_pages.length*2];
		System.arraycopy(_pages, 0, grown, 0, _pages.length);
		_pages = grown;
	}

	/**
	 * Will return the page that holds the index passed in.
	 * For example, if pagesize == 100 and index == 525, then this will return page 5.
	 *
	 * @param itemIndex
	 * @return
	 */
	private Page pageHolding(int itemIndex) {
		return (Page) _pages[pageIndex(itemIndex)];
	}
	
	private int pageIndex(int itemIndex) {
		return itemIndex / Page.PAGESIZE;
	}
	
	private int indexInPage(int itemIndex) {
		return itemIndex % Page.PAGESIZE;
	}
	
	// TA BEGIN
	public void bind(ObjectContainer container) {
		if (null != _activator) {
			_activator.assertCompatible(container);
			return;
		}
		_activator = new Activator(container, this);
	}
	
	private void activate() {
		if (_activator == null) return;
		_activator.activate();
	}
	// TA END
}
