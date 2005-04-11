/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.nodes.partition;

import com.db4o.browser.model.nodes.IModelNode;

public class PartitionFieldNode implements IModelNode {

    private IModelNode[] _toPartition;
    private PartitionSpec _partitionSpec;
    private int _depth;
    private int _position;
    private int _beginPartition;
    private int _endPartition;
    private int _partitionLength;

    public PartitionFieldNode(IModelNode[] toPartition, PartitionSpec partitionSpec, int depth, int position) {
        _toPartition = toPartition;
        _partitionSpec = partitionSpec;
        _depth = depth;
        _position = position;
        _beginPartition = partitionSpec.getBeginPartition(depth, position);
        _endPartition = partitionSpec.getEndPartition(depth, position);
        _partitionLength = _endPartition - _beginPartition;
    }
    
    public boolean hasChildren() {
        return true;
    }

    public IModelNode[] children() {
        if (_partitionSpec.hasPartitionChildren(_depth)) {
            int numChildren = _partitionSpec.getNumChildren(_depth, _position);
            int newNodePosition = _partitionSpec.getStartingChildPosition(numChildren, numChildren);
            IModelNode[] results = new IModelNode[numChildren];
            for (int child=0; child < numChildren; ++child) {
                results[child] = new PartitionFieldNode(_toPartition, _partitionSpec, _depth+1, newNodePosition);
                ++newNodePosition;
            }
            return results;
        } else {
            IModelNode[] results = new IModelNode[_partitionLength];
            System.arraycopy(_toPartition, _beginPartition, results, 0, _partitionLength);
            return results;
        }
    }

    public String getText() {
        return "[" + _beginPartition + "-" + _endPartition + "] ";
    }

    public String getName() {
        return getText() + "ResultSetRange";
    }

    public String getValueString() {
        return "ResultSetRange";
    }

}
