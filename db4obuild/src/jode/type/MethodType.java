/* MethodType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.type;

public class MethodType extends Type
{
    final String signature;
    final Type[] parameterTypes;
    final Type returnType;
    
    public MethodType(String string) {
	super(12);
	signature = string;
	int i = 1;
	int i_0_ = 0;
	for (/**/; string.charAt(i) != ')'; i++) {
	    i_0_++;
	    for (/**/; string.charAt(i) == '['; i++) {
		/* empty */
	    }
	    if (string.charAt(i) == 'L')
		i = string.indexOf(';', i);
	}
	parameterTypes = new Type[i_0_];
	i = 1;
	i_0_ = 0;
	while (string.charAt(i) != ')') {
	    int i_1_ = i;
	    for (/**/; string.charAt(i) == '['; i++) {
		/* empty */
	    }
	    if (string.charAt(i) == 'L')
		i = string.indexOf(';', i);
	    i++;
	    parameterTypes[i_0_++] = Type.tType(string.substring(i_1_, i));
	}
	returnType = Type.tType(string.substring(i + 1));
    }
    
    public final int stackSize() {
	int i = returnType.stackSize();
	for (int i_2_ = 0; i_2_ < parameterTypes.length; i_2_++)
	    i -= parameterTypes[i_2_].stackSize();
	return i;
    }
    
    public Type[] getParameterTypes() {
	return parameterTypes;
    }
    
    public Class[] getParameterClasses() throws ClassNotFoundException {
	Class[] var_classes = new Class[parameterTypes.length];
	int i = var_classes.length;
	while (--i >= 0)
	    var_classes[i] = parameterTypes[i].getTypeClass();
	return var_classes;
    }
    
    public Type getReturnType() {
	return returnType;
    }
    
    public Class getReturnClass() throws ClassNotFoundException {
	return returnType.getTypeClass();
    }
    
    public String getTypeSignature() {
	return signature;
    }
    
    public String toString() {
	return signature;
    }
    
    public boolean equals(Object object) {
	MethodType methodtype_3_;
	return (object instanceof MethodType
		&& signature.equals((methodtype_3_ = (MethodType) object)
				    .signature));
    }
}
