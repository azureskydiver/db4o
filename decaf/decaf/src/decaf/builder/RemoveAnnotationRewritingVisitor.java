/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package decaf.builder;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;

public class RemoveAnnotationRewritingVisitor extends DecafVisitorBase {
	
	public RemoveAnnotationRewritingVisitor(DecafRewritingContext context) {
		super(context);
	}
	
	@Override
	public boolean visit(TypeLiteral node) {
	    if (isMarkedForRemoval(node.getType().resolveBinding())) {
			rewrite().remove(node);
			return false;
		}
	    return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if (isIgnored(node.resolveBinding()) || isMarkedForRemoval(node.resolveBinding())) {
			return false;
		}
		return true;
	}
	
	@Override
	public void endVisit(ArrayInitializer node) {
		ListRewrite listRewrite = rewrite().getListRewrite(node, ArrayInitializer.EXPRESSIONS_PROPERTY);
		List rewrittenList = listRewrite.getRewrittenList();
		if(!rewrittenList.isEmpty()) {
			return;
		}
		ArrayInitializer emptyInitializer = builder().newArrayInitializer();
		rewrite().replace(node, emptyInitializer);
	}	
}
