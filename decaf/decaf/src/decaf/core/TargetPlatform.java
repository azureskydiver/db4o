/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package decaf.core;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import decaf.builder.*;



public enum TargetPlatform {
	
	NONE {
		@Override
		public DecafConfiguration defaultConfig() {
			return new DecafConfiguration();
		}
		
		@Override
		public String appendPlatformId(String orig, String separator) {
			return orig;
		}
		
		@Override
		public boolean isNone() {
			return true;
		}

		@Override
		public IterablePlatformMapping iterablePlatformMapping() {
			return JDK12_ITERABLE_MAPPING;
		}
	},
	JDK11 {
		@Override
		public DecafConfiguration defaultConfig() {
			return DecafConfiguration.forJDK11();
		}

		@Override
		public IterablePlatformMapping iterablePlatformMapping() {
			return JDK11_ITERABLE_MAPPING;
		}
	},
	JDK12 {
		@Override
		public DecafConfiguration defaultConfig() {
			return DecafConfiguration.forJDK12();
		}

		@Override
		public IterablePlatformMapping iterablePlatformMapping() {
			return JDK12_ITERABLE_MAPPING;
		}
	};
	
	public String appendPlatformId(String orig, String separator) {
		return orig + separator + platformId();
	}

	private String platformId() {
		return toString().toLowerCase();
	}

	public abstract DecafConfiguration defaultConfig();

	public abstract IterablePlatformMapping iterablePlatformMapping();
	
	public boolean isNone() {
		return false;
	}
	
	private static class IterableJdk11Mapping implements IterablePlatformMapping {
		
		public String iteratorClassName() {
			return "com.db4o.foundation.Iterator4";
		}

		public String iteratorNextCheckName() {
			return "moveNext";
		}

		public String iteratorNextElementName() {
			return "current";
		}

		public Expression coerceIterableExpression(Expression iterableExpr, DecafASTNodeBuilder builder, DecafRewritingServices rewrite) {
			return iterableExpr;
		}
		
	}

	private static class IterableJdk12Mapping implements IterablePlatformMapping {

		public String iteratorClassName() {
			return Iterator.class.getName();
		}

		public String iteratorNextCheckName() {
			return "hasNext";
		}

		public String iteratorNextElementName() {
			return "next";
		}
		
		public Expression coerceIterableExpression(Expression iterableExpr, DecafASTNodeBuilder builder, DecafRewritingServices rewrite) {
			MethodInvocation coerceInvocation = builder.newMethodInvocation(builder.newQualifiedName("com", "db4o", "foundation", "IterableBaseFactory"), "coerce");
			coerceInvocation.arguments().add(rewrite.safeMove(iterableExpr));
			return coerceInvocation;
		}
	}

	private static IterablePlatformMapping JDK11_ITERABLE_MAPPING = new IterableJdk11Mapping();
	private static IterablePlatformMapping JDK12_ITERABLE_MAPPING = new IterableJdk12Mapping();

}
