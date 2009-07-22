/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.internal.metadata;

import java.util.*;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.metadata.HierarchyAnalyzer.*;

/**
 * @exclude
 */
public class ModifiedAspectTraversalStrategy implements AspectTraversalStrategy {
	
	private final List<Diff> _classDiffs;

	public ModifiedAspectTraversalStrategy(ClassMetadata classMetadata,
			List<Diff> ancestors) {
		_classDiffs = new ArrayList<Diff>();
		_classDiffs.add(new HierarchyAnalyzer.Same(classMetadata));
		_classDiffs.addAll(ancestors);
	}

	public void traverseAllAspects(MarshallingInfo context,
			TraverseAspectCommand command, FieldListInfo fieldListInfo) {
		int currentSlot = 0;
	    for(HierarchyAnalyzer.Diff diff : _classDiffs){
			ClassMetadata classMetadata = diff.classMetadata();
			if(diff.isRemoved()){
		        currentSlot = skipAspectsOf(classMetadata, context, command,
						fieldListInfo, currentSlot);
				continue;
			}
	        currentSlot = traverseAspectsOf(classMetadata, context, command,
					fieldListInfo, currentSlot);
	        if(command.cancelled()){
	            return;
	        }
	    }
	}
	
	static interface TraverseAspectCommandProcessor {
		void process(TraverseAspectCommand command, ClassAspect currentAspect, int currentSlot);
	}

	private int traverseAspectsOf(final ClassMetadata classMetadata,
			MarshallingInfo context, TraverseAspectCommand command,
			final FieldListInfo fieldListInfo, int currentSlot) {
		return processAspectsOf(classMetadata, context, command, currentSlot, new TraverseAspectCommandProcessor() {
			public void process(TraverseAspectCommand command, ClassAspect currentAspect, int currentSlot) {
				command.processAspect(
		        		currentAspect,
		        		currentSlot,
		        		fieldListInfo.isNull(currentSlot), classMetadata);
		
			}
		});
	}

	private int processAspectsOf(final ClassMetadata classMetadata,
			MarshallingInfo context, TraverseAspectCommand command,
			int currentSlot, TraverseAspectCommandProcessor processor) {
		int aspectCount=command.aspectCount(classMetadata, ((ByteArrayBuffer)context.buffer()));
		context.aspectCount(aspectCount);
		for (int i = 0; i < aspectCount && !command.cancelled(); i++) {
		    final ClassAspect currentAspect = classMetadata._aspects[i];
			if(command.accept(currentAspect)){
				processor.process(command, currentAspect, currentSlot);
		    }
		    context.beginSlot();
		    currentSlot++;
		}
		return currentSlot;
	}
	
	private int skipAspectsOf(ClassMetadata classMetadata,
			final MarshallingInfo context, TraverseAspectCommand command,
			final FieldListInfo fieldListInfo, int currentSlot) {
		return processAspectsOf(classMetadata, context, command, currentSlot, new TraverseAspectCommandProcessor() {
			public void process(
					TraverseAspectCommand command,
					ClassAspect currentAspect,
					int currentSlot) {
				command.processAspectOnMissingClass(context, fieldListInfo, currentAspect, currentSlot);
			}
		});
	}


}
