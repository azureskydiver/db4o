/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.nodes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestTree {
    
    private Integer[] contents;
    
    private static int[][] treeSpec;

    private Node _root;
    
    public TestTree() {
        contents = new Integer[123456];
        _root=new Node(contents,0,contents.length);
        for (int i = 0; i < contents.length; i++) {
            contents[i] = new Integer(i);
        }
        
        treeSpec = computeTreeSpec(contents.length, 100);
    }

    public int[][] computeTreeSpec(int numItems, int threshold) {
        List structure=new ArrayList();
        int curnum=numItems;
        int[] lastlevel = null;
        
        while(curnum>threshold) {
            int numbuckets=(int)Math.round((float)curnum/threshold+0.5);
            int minbucketsize=curnum/numbuckets;
            int numexceeding=curnum%numbuckets;
            int[] curlevel=new int[numbuckets];
            int startidx=0;
            for (int bucketidx = 0; bucketidx < curlevel.length; bucketidx++) {
                int curfillsize=minbucketsize;
                if(bucketidx < numexceeding) {
                    curfillsize++;
                }
                if (lastlevel == null) {
                    curlevel[bucketidx] = curfillsize;
                } else {
                    for(int lastidx=startidx;lastidx<startidx+curfillsize;lastidx++) {
                        curlevel[bucketidx]+=lastlevel[lastidx];
                    }
                    startidx+=curfillsize;
                }
            }
            structure.add(curlevel);
            lastlevel = curlevel;
            curnum=numbuckets;
        }
        return (int[][])structure.toArray(new int[structure.size()][]);    
    }
    
    private class Node {
        private final static int THRESHOLD=100;
        
        private Integer[] _contents;
        private int _start;
        private int _end;
        
        public Node(Integer[] contents, int start, int end) {
            _contents = contents;
            _start = start;
            _end = end;
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

            int curnum=size();
            while(curnum>THRESHOLD) {
                int numbuckets=(int)Math.round((float)curnum/THRESHOLD+0.5);
                int minbucketsize=curnum/numbuckets;
                int numexceeding=curnum%numbuckets;
                stack.add(new BucketSpec(numbuckets,minbucketsize,numexceeding));
                System.err.println(curnum+"/"+numbuckets+"/"+minbucketsize+"/"+numexceeding);
                curnum=numbuckets;
            }
            Node[] children=new Node[curnum];
            int curstartidx=0;
            for(int bucketidx=0;bucketidx<curnum;bucketidx++) {
                int currange=0;
                for(int stackidx=stack.size()-1;stackidx>00;stackidx++) {
                    
                }
                int cursize=minbucketsize;
                if(bucketidx<numexceeding) {
                    cursize++;
                }
                children[bucketidx]=new Node(_contents,curstartidx,curstartidx+cursize);
                curstartidx+=cursize;
            }
            return children;
        }

        private int size() {
            return _end-_start;
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
        int[][] results = test.computeTreeSpec(29, 3);
        System.out.println("Hello");
    }

}
