/*
 * This file is part of com.db4o.browser.
 *
 * com.db4o.browser is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * com.db4o.browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.swtworkbench.ed; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.db4o.browser.model.nodes;

import java.util.*;

import com.db4o.ObjectSet;
import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.TestTree.*;
import com.db4o.reflect.ReflectClass;

/**
 * Class ClassNode.
 * 
 * @author djo
 */
public class ClassNode implements IModelNode {

	private final ReflectClass _class;
    private final Database _database;
	private static final int THRESHOLD = 100;
	private int[][] treeSpec;

	/**
	 * @param contents
	 * @param database
	 */
	public ClassNode(ReflectClass contents, Database database) {
		_class = contents;
        _database = database;
    }

	public ClassNode(ReflectClass contents, Database database,int[][] treeSpec,int start,int end) {
		_class = contents;
        _database = database;
    }

    /* (non-Javadoc)
     * @see com.db4o.browser.model.nodes.IModelNode#mayHaveChildren()
     */
    public boolean hasChildren() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.db4o.browser.model.nodes.IModelNode#children()
     */
    public IModelNode[] children() {
		System.err.println("Starting");
		long start=System.currentTimeMillis();
		// This is our bottleneck - should at least be cached.
        ObjectSet instances = _database.instances(_class);
		System.err.println("Querying: "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
		long[] ids = instances.ext().getIDs();
		System.err.println("Getting IDs: "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
		if(instances.size()<=THRESHOLD) {
	        IModelNode[] result = new IModelNode[instances.size()];
	        int i=0;
	        while (instances.hasNext()) {
	            Object object = instances.next();
	            ReflectClass clazz = _database.reflector().forObject(object);
	            result[i] = new InstanceNode(object, clazz, _database);
	            ++i;
	        }
			System.err.println("Creating instances: "+(System.currentTimeMillis()-start));
	        return result;
		}
		int[][] treeSpec=computeTreeSpec(ids.length,THRESHOLD);
		System.err.println("Building spec: "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
		int[] levelSpec=treeSpec[treeSpec.length-1];
		IModelNode[] children=new IModelNode[levelSpec.length];
		for (int childidx = 0; childidx < children.length; childidx++) {
			int end=(childidx<children.length-1 ? levelSpec[childidx+1] : instances.size());
			children[childidx]=new GroupInstanceNode(_database,_class,ids,treeSpec,treeSpec.length-1,childidx);
		}
		System.err.println("Creating children: "+(System.currentTimeMillis()-start));
		return children;
    }
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getName()
	 */
	public String getName() {
		return "";
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getValueString()
	 */
	public String getValueString() {
		return getText();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getText()
	 */
	public String getText() {
		return _class.getName();
	}

	/**
	 * Computes tree level start indices per level (into level below or
	 * into the id list, if leaf level).
	 * 
	 * Example: (29,3) should result in
	 * {{0,3,6,9,12,15,18,21,24,27},{0,3,6,8},{0,2}}
	 */
    private static int[][] computeTreeSpec(int numItems, int threshold) {
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
				curlevel[bucketidx] = startidx;
            }
            structure.add(curlevel);
			lastlevel=curlevel;
            curnum=numbuckets;
        }
        return (int[][])structure.toArray(new int[structure.size()][]);    
    }
}
