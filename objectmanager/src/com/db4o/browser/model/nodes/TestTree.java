/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.nodes;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class TestTree {
    
    private Integer[] contents;
    
    private static int[][] treeSpec;

    private Node _root;
    
    public TestTree() {
        contents = new Integer[1234567];
        _root=new Node(contents,0,contents.length);
        for (int i = 0; i < contents.length; i++) {
            contents[i] = new Integer(i);
        }
    }

    private class Node {
        private final static int THRESHOLD=100;
        
        private Object[] _contents;
        private int _start;
        private int _end;
        
        public Node(Object[] contents, int start, int end) {
            _contents = contents;
            _start = start;
            _end = end;
			if(_start>=_end) {
				System.err.println("ouch!");
			}
        }
        
        public String toString() {
            return "["+_start+","+(_end-1)+"]";
        }
        
        public Object[] getChildren() {
            if(size()<=THRESHOLD) {
                Object[] children=new Object[size()];
                System.arraycopy(_contents, _start, children, 0, size());
                return children;
            }
			int[][] fullspec=computeTreeSpec(size(),THRESHOLD);
			int[] spec=fullspec[fullspec.length-1];
			Object[] children=new Object[spec.length];
			for (int childidx = 0; childidx < children.length; childidx++) {
				int end=(childidx<children.length-1 ? _start+spec[childidx+1] : _end);
				children[childidx]=new Node(_contents,_start+spec[childidx],end);
			}
			return children;
        }

        private int size() {
            return _end-_start;
        }

	    private int[][] computeTreeSpec(int numItems, int threshold) {
			// tree depth is log(numItems) to the base of threshold
			int numlevels=(int)(Math.log(numItems)/Math.log(threshold));
			// store bucket start indices (not lengths) per level
	        int[][] structure=new int[numlevels][];
	        int curnum=numItems;
	        
			int levelidx=0;
	        while(curnum>threshold) {
	            int numbuckets=curnum/threshold+1;
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
	                if (levelidx>0) {
						curlevel[bucketidx]=structure[levelidx-1][startidx];
	                }
	                else {
						curlevel[bucketidx] = startidx;
	                }
	            }
	            structure[levelidx]=curlevel;
	            curnum=numbuckets;
				levelidx++;
	        }
	        return structure;    
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
            return new Object[] {_root};
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
        tree.setInput(new Node(contents, 0, contents.length));

        shell.open();
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }
    
    public static void main(String[] args) {
        TestTree test = new TestTree();
		test.run();
//        int[][] results = test.computeTreeSpec(29, 3);
//        System.out.println("Hello");
    }

}
