/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.nodes;

import java.util.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class TestTree {
    
    private Integer[] _contents;
    private static int[][] treeSpec;
	private int _threshold;
    
    public TestTree(int numitems,int threshold) {
        _contents = new Integer[numitems];
		_threshold=threshold;
        for (int i = 0; i < _contents.length; i++) {
            _contents[i] = new Integer(i);
        }
    }

    private static class Node {
        private Object[] _contents;
        private int _start;
        private int _end;
		private int _threshold;
        
        public Node(Object[] contents, int start, int end, int threshold) {
            _contents = contents;
            _start = start;
            _end = end;
			_threshold=threshold;
			if(_start>=_end) {
				System.err.println("ouch!");
			}
        }
        
        public String toString() {
            return "["+_start+","+(_end-1)+"]";
        }
        
        public Object[] getChildren() {
            if(size()<=_threshold) {
                Object[] children=new Object[size()];
                System.arraycopy(_contents, _start, children, 0, size());
                return children;
            }
			int[][] fullspec=computeTreeSpec(size(),_threshold);
			int[] spec=fullspec[fullspec.length-1];
			Object[] children=new Object[spec.length];
			for (int childidx = 0; childidx < children.length; childidx++) {
				int end=(childidx<children.length-1 ? _start+spec[childidx+1] : _end);
				children[childidx]=new Node(_contents,_start+spec[childidx],end,_threshold);
			}
			return children;
        }

        private int size() {
            return _end-_start;
        }

		// TODO: move me somewhere i can be cached or get rid of the history array
	    public static int[][] computeTreeSpec(int numItems, int threshold) {
	        java.util.List structure=new ArrayList();
	        int curnum=numItems;
	        
			int[] lastlevel=null;
	        while(curnum>threshold) {
	            int numbuckets=(int)Math.ceil((float)curnum/threshold);
	            int minbucketsize=curnum/numbuckets;
	            int numexceeding=curnum%numbuckets;
	            int[] curlevel=new int[numbuckets];
	            int startidx=0;
				curlevel[0]=0;
	            for (int bucketidx = 1; bucketidx < curlevel.length; bucketidx++) {
	                int curfillsize=minbucketsize;
	                if(bucketidx <= numexceeding) {
	                    curfillsize++;
	                }
					startidx+=curfillsize;
	                if (!structure.isEmpty()) {
						curlevel[bucketidx]=lastlevel[startidx];
	                }
	                else {
						curlevel[bucketidx] = startidx;
	                }
	            }
	            structure.add(curlevel);
				lastlevel=curlevel;
	            curnum=numbuckets;
	        }
	        return (int[][])structure.toArray(new int[structure.size()][]);    
	    }
	}

    private IContentProvider contentProvider = new ITreeContentProvider() {

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        // Get the top level
        public Object[] getElements(Object inputElement) {
//            return _root.getChildren();
            return new Object[] {new Node(_contents,0,_contents.length,_threshold)};
        }

        // Get subsequent levels
        public Object[] getChildren(Object parentElement) {
            return ((Node)parentElement).getChildren();
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return (element instanceof Node);
        }
    };
    
    private ILabelProvider labelProvider = new ILabelProvider() {

        public void addListener(ILabelProviderListener listener) {
        }

        public void removeListener(ILabelProviderListener listener) {
        }

        public void dispose() {
            // TODO Auto-generated method stub
            
        }

        public boolean isLabelProperty(Object element, String property) {
            return true;
        }

        public Image getImage(Object element) {
            return null;
        }

        public String getText(Object element) {
            return element.toString();
        }
        
    };

    public void run() {
        Display display = new Display();
        Shell shell = new Shell(display);
        
        shell.setLayout(new FillLayout());
        
        TreeViewer tree = new TreeViewer(shell, SWT.NULL);
        tree.setContentProvider(contentProvider);
        tree.setLabelProvider(labelProvider);
//        tree.setInput(new Node(_contents,0,_contents.length,_threshold));
		tree.setInput(new Object());

        shell.open();
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }
    
    public static void main(String[] args) {
        TestTree test = new TestTree(29,3);
		test.run();
//		int[][] spec=Node.computeTreeSpec(29,3);
//		System.out.println(spec);
    }

}
