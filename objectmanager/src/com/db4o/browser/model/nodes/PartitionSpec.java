/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.nodes;

import java.util.LinkedList;

public class PartitionSpec {
    
    private int _numItems;
    private int _threshold;
    
    private int[][] _treeLayout;
    
    public PartitionSpec(int numItems, int threshold) {
        _numItems = numItems;
        _threshold = threshold;
        _treeLayout = computeTreePartition(numItems, threshold);
    }
    
    public int getBeginPartition(int nodeDepth, int nodePosition) {
        return 0;
    }
    
    public int getEndPartition(int nodeDepth, int nodePosition) {
        return 0;
    }
    
    public int getNumChildren(int nodeDepth, int nodePosition) {
        int childCoverage = _treeLayout[nodeDepth][nodePosition];
        int foundCoverage = 0;
        int numChildren = 0;
        int currentChild = 0;
        
        while (foundCoverage < childCoverage) {
            foundCoverage += _treeLayout[nodeDepth+1][currentChild];
            ++currentChild;
            ++numChildren;
        }
        
        return numChildren;
    }
    
    public boolean hasPartitionChildren(int nodeDepth) {
        return nodeDepth < _treeLayout.length;
    }
    
    /*
     * This method will partition the specified number of elements
     * into a balanced tree represented according to the following
     * example:
     * 
     * threshold = 3
     * 29 elements:
     *
     * xxx xxx xxx xxx xxx xxx xxx xxx xxx xx
     * ----------- ----------- ------- ------
     * ----------------------- --------------
     *
     * Results:
     * 
     * new int[][] {
     *  {3, 3, 3, 3, 3, 3, 3, 3, 3, 2},
     *  {9, 9, 6, 5},
     *  {18, 11}
     * }
     *
     */
    private int[][] computeTreePartition(int numItems, int threshold) {
        LinkedList structure=new LinkedList();
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
            structure.addFirst(curlevel);
            lastlevel = curlevel;
            curnum=numbuckets;
        }
        return (int[][])structure.toArray(new int[structure.size()][]);    
    }
    
    public static void main(String[] args) {
        PartitionSpec test = new PartitionSpec(29, 3);
    }

}
