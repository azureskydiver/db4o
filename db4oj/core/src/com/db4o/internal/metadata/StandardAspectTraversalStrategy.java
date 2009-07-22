/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.internal.metadata;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;

/**
 * @exclude
 */
public class StandardAspectTraversalStrategy implements AspectTraversalStrategy {
	
	private final ClassMetadata _classMetadata;

	public StandardAspectTraversalStrategy(ClassMetadata classMetadata) {
		_classMetadata = classMetadata;
	}

	public void traverseAllAspects(MarshallingInfo context, TraverseAspectCommand command,
			FieldListInfo fieldListInfo) {
		ClassMetadata classMetadata = _classMetadata;
		int currentSlot = 0;
	    while(classMetadata != null){
	        int aspectCount=command.aspectCount(classMetadata, ((ByteArrayBuffer)context.buffer()));
			context.aspectCount(aspectCount);
			for (int i = 0; i < aspectCount && !command.cancelled(); i++) {
			    final ClassAspect currentAspect = classMetadata._aspects[i];
				if(command.accept(currentAspect)){
					command.processAspect(
			        		currentAspect,
			        		currentSlot,
			        		fieldListInfo.isNull(currentSlot), classMetadata);
			    }
			    context.beginSlot();
			    currentSlot++;
			}
	        if(command.cancelled()){
	            return;
	        }
	        classMetadata = classMetadata.i_ancestor;
	    }
	}
}