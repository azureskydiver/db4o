/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.gui.controllers;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.db4o.browser.gui.views.ISelectionSource;
import com.db4o.browser.model.GraphPosition;
import com.db4o.browser.model.IGraphIterator;
import com.db4o.browser.model.IGraphIteratorSelectionListener;

/**
 * NavigationController.   Manages the forward and back buttons in the UI.
 *
 * @author djo
 */
public class NavigationController implements IBrowserController {
	
	private IGraphIterator model = null;
	
	private final int STACK_LIMIT = 50;
	private GraphPosition[] undoRedoStack = new GraphPosition[STACK_LIMIT];
	private int stackPosition;
	private int stackMax;
	
	/**
	 * Construct a NavigationController on a specific UI element
	 * 
	 * @param ui The DbBrowserPane to which to attach.
	 */
	public NavigationController(ISelectionSource leftButton, ISelectionSource rightButton) {
		leftButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				undo();
			}
		});
		rightButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				redo();
			}
		});
		resetUndoRedoStack();
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.controllers.IBrowserController#setInput(com.db4o.browser.model.IGraphIterator, com.db4o.browser.model.GraphPosition)
	 */
	public void setInput(IGraphIterator input, GraphPosition selection) {
		if (model != null) {
			model.removeSelectionChangedListener(selectionChangedListener);
		}
		
		model = input;
		model.addSelectionChangedListener(selectionChangedListener);
		resetUndoRedoStack();
	}
	
	/**
	 * Set the UndoRedoStack to be empty;
	 */
	private void resetUndoRedoStack() {
		stackPosition=-1;
		stackMax=-1;
		for (int i = 0; i < undoRedoStack.length; i++) {
			undoRedoStack[i] = null;		// Encourage the GC...
		}
	}
	
	/**
	 * Returns if no additional items can be added to the stack without
	 * the stack scrolling.
	 * 
	 * @return true if the stack must scroll in order for an item to be added.
	 */
	private boolean isFull() {
		return stackMax >= STACK_LIMIT;
	}
	
	/**
	 * If the stack is full, we must scroll an element off the bottom.
	 */
	private void scrollStack() {
		if (!isFull())
			return;
		
		for (int i = 0; i < undoRedoStack.length-1; i++) {
			undoRedoStack[i] = undoRedoStack[i+1];
		}
		--stackPosition;
		--stackMax;
	}
	
	/**
	 * @param element
	 */
	private void add(GraphPosition element) {
		scrollStack();
		++stackPosition;
		if (stackPosition > stackMax) {
			stackMax = stackPosition;
		}
		undoRedoStack[stackPosition] = element;
	}
	
	private void undo() {
		if (stackPosition > 0) {
			--stackPosition;
			model.setSelectedPath(undoRedoStack[stackPosition]);
		}
	}
	
	private void redo() {
		if (stackPosition < stackMax) {
			++stackPosition;
			model.setSelectedPath(undoRedoStack[stackPosition]);
		}
	}

	private IGraphIteratorSelectionListener selectionChangedListener = new IGraphIteratorSelectionListener() {
		public void selectionChanged() {
			add(model.getPath());
		}
	};
}
