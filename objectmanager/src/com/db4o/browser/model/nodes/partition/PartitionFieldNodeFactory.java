/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.nodes.partition;

import com.db4o.browser.model.nodes.IModelNode;

public class PartitionFieldNodeFactory {
    public static final int THRESHOLD=3;
    
    public static IModelNode[] create(IModelNode[] source) {
        if (source.length <= THRESHOLD) {
            return source;
        } else {
            PartitionSpec spec = new PartitionSpec(source.length, THRESHOLD);
            int numNodes = spec.getNumRootNodes();
            IModelNode[] result = new IModelNode[numNodes];
            for (int nodePos=0; nodePos < numNodes; ++nodePos) {
                result[nodePos] = new PartitionFieldNode(source, spec, 0, nodePos);
            }
            return result;
        }
    }
}
