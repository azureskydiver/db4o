/* FieldAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Set;

import jode.bytecode.FieldInfo;
import jode.expr.ConstOperator;
import jode.expr.Expression;
import jode.expr.OuterLocalOperator;
import jode.type.Type;

public class FieldAnalyzer implements Analyzer
{
    ClassAnalyzer clazz;
    ImportHandler imports;
    int modifiers;
    Type type;
    String fieldName;
    Expression constant;
    boolean isSynthetic;
    boolean isDeprecated;
    boolean analyzedSynthetic = false;
    
    public FieldAnalyzer(ClassAnalyzer classanalyzer, FieldInfo fieldinfo,
			 ImportHandler importhandler) {
	clazz = classanalyzer;
	imports = importhandler;
	modifiers = fieldinfo.getModifiers();
	type = Type.tType(fieldinfo.getType());
	fieldName = fieldinfo.getName();
	constant = null;
	isSynthetic = fieldinfo.isSynthetic();
	isDeprecated = fieldinfo.isDeprecated();
	if (fieldinfo.getConstant() != null) {
	    constant = new ConstOperator(fieldinfo.getConstant());
	    constant.setType(type);
	    constant.makeInitializer(type);
	}
    }
    
    public String getName() {
	return fieldName;
    }
    
    public Type getType() {
	return type;
    }
    
    public ClassAnalyzer getClassAnalyzer() {
	return clazz;
    }
    
    public Expression getConstant() {
	return constant;
    }
    
    public boolean isSynthetic() {
	return isSynthetic;
    }
    
    public boolean isFinal() {
	return Modifier.isFinal(modifiers);
    }
    
    public void analyzedSynthetic() {
	analyzedSynthetic = true;
    }
    
    public boolean setInitializer(Expression expression) {
	if (constant != null)
	    return constant.equals(expression);
	if (isSynthetic && (fieldName.startsWith("this$")
			    || fieldName.startsWith("val$"))) {
	    if (fieldName.startsWith("val$") && fieldName.length() > 4
		&& expression instanceof OuterLocalOperator) {
		LocalInfo localinfo
		    = ((OuterLocalOperator) expression).getLocalInfo();
		localinfo.addHint(fieldName.substring(4), type);
	    }
	    analyzedSynthetic();
	} else
	    expression.makeInitializer(type);
	constant = expression;
	return true;
    }
    
    public boolean setClassConstant(String string) {
	if (constant != null)
	    return false;
	if (string.charAt(0) == '[') {
	    if (string.charAt(string.length() - 1) == ';')
		string = string.substring(0, string.length() - 1);
	    if (fieldName.equals("array" + string.replace('[', '$')
					       .replace('.', '$'))) {
		analyzedSynthetic();
		return true;
	    }
	} else if (fieldName.equals("class$" + string.replace('.', '$'))
		   || fieldName.equals("class$L" + string.replace('.', '$'))) {
	    analyzedSynthetic();
	    return true;
	}
	return false;
    }
    
    public void analyze() {
	imports.useType(type);
    }
    
    public void makeDeclaration(Set set) {
	if (constant != null) {
	    constant.makeDeclaration(set);
	    constant = constant.simplify();
	}
    }
    
    public boolean skipWriting() {
	return analyzedSynthetic;
    }
    
    public void dumpSource(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (isDeprecated) {
	    tabbedprintwriter.println("/**");
	    tabbedprintwriter.println(" * @deprecated");
	    tabbedprintwriter.println(" */");
	}
	if (isSynthetic)
	    tabbedprintwriter.print("/*synthetic*/ ");
	int i = modifiers;
	tabbedprintwriter.startOp(1, 0);
	String string = Modifier.toString(i);
	if (string.length() > 0)
	    tabbedprintwriter.print(string + " ");
	tabbedprintwriter.printType(type);
	tabbedprintwriter.print(" " + fieldName);
	if (constant != null) {
	    tabbedprintwriter.breakOp();
	    tabbedprintwriter.print(" = ");
	    constant.dumpExpression(2, tabbedprintwriter);
	}
	tabbedprintwriter.endOp();
	tabbedprintwriter.println(";");
    }
    
    public String toString() {
	return (this.getClass().getName() + "[" + clazz.getClazz() + "."
		+ getName() + "]");
    }
}
