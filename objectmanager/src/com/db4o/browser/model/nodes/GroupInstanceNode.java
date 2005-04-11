package com.db4o.browser.model.nodes;

import com.db4o.browser.model.*;
import com.db4o.reflect.*;

public class GroupInstanceNode implements IModelNode {

	private Database _database;
	private long[] _ids;
	private int[][] _treeSpec;
	private int _level;
	private int _idx;
	private ReflectClass _clazz;

	public GroupInstanceNode(Database database,ReflectClass clazz,long[] ids, int[][] treeSpec,int level,int idx) {
		_database=database;
		_ids=ids;
		_treeSpec=treeSpec;
		_level=level;
		_idx=idx;
		_clazz=clazz;
	}

	public boolean hasChildren() {
		return true;
	}

	public IModelNode[] children() {
		int[] curLevelSpec=_treeSpec[_level];
		int start=curLevelSpec[_idx];
        if(_level==0) {
			int end=(_idx<curLevelSpec.length-1 ? curLevelSpec[_idx+1] : _ids.length);
	        IModelNode[] children=new IModelNode[end-start];
			for (int childidx = 0; childidx < children.length; childidx++) {
				Object instance=_database.byId(_ids[start+childidx]);
				children[childidx]=new InstanceNode(instance,_clazz,_database);
			}
            return children;
        }
		int[] nextLevelSpec=_treeSpec[_level-1];
		int end=(_idx<curLevelSpec.length-1 ? curLevelSpec[_idx+1] : nextLevelSpec.length);
		int length=end-start;
        IModelNode[] children=new IModelNode[length];
		for (int childidx = 0; childidx < children.length; childidx++) {
			children[childidx]=new GroupInstanceNode(_database,_clazz,_ids,_treeSpec,_level-1,start+childidx);
		}
		return children;
	}

	public String getText() {
		return _level+"/"+_idx;
	}

	public String getName() {
		return _level+"/"+_idx;
	}

	public String getValueString() {
		return _level+"/"+_idx;
	}
}
