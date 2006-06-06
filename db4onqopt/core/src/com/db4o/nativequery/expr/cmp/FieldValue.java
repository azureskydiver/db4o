package com.db4o.nativequery.expr.cmp;


// FIXME need to carry more info, must know about Integer.class vs. Integer.TYPE

public class FieldValue extends ComparisonOperandDescendant {
	
	private String _fieldName;
	
	private Object _tag;
	
	public FieldValue(ComparisonOperandAnchor root, String name) {
		this(root, name, null);
	}
	
	public FieldValue(ComparisonOperandAnchor root, String name, Object tag) {
		super(root);
		_fieldName=name;
		_tag=tag;
	}

	public String fieldName() {
		return _fieldName;
	}
	
	public boolean equals(Object other) {
		if(!super.equals(other)) {
			return false;
		}
		FieldValue casted = (FieldValue) other;
		return _fieldName.equals(casted._fieldName);
	}
	
	public int hashCode() {
		return super.hashCode()*29+_fieldName.hashCode();
	}
	
	public String toString() {
		return super.toString()+"."+_fieldName;
	}
	
	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
	
	/**
	 * Code analysis specific information.
	 * 
	 * This is used in the .net side to preserve Mono.Cecil references
	 * for instance.
	 */
	public Object tag() {
		return _tag;
	}
	
	public void tag(Object value) {
		_tag = value;
	}
}
