/* LocalInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import jode.AssertError;
import jode.GlobalOptions;
import jode.expr.Expression;
import jode.expr.LocalVarOperator;
import jode.type.Type;

public class LocalInfo implements Declarable {
    private static int serialnr = 0;
    private static int nextAnonymousSlot = -1;
    private int slot;
    private MethodAnalyzer methodAnalyzer;
    private boolean nameIsGenerated = false;
    private boolean isUnique;
    private String name;
    private Type type;
    private LocalInfo shadow;
    private Vector operators;
    private Vector hints;
    private boolean removed;
    private boolean isFinal;
    private Expression constExpr;
    private int loopCount;

    static class Hint {
        String name;
        Type type;

        public Hint(String string, Type type) {
            name = string;
            this.type = type;
        }

        public final Type getType() {
            return type;
        }

        public final String getName() {
            return name;
        }

        public boolean equals(Object object) {
            if (object instanceof Hint) {
                Hint hint_0_ = (Hint) object;
                return name.equals(hint_0_.name) && type.equals(hint_0_.type);
            }
            return false;
        }

        public int hashCode() {
            return name.hashCode() ^ type.hashCode();
        }
    }

    public LocalInfo() {
        operators = new Vector();
        hints = new Vector();
        removed = false;
        isFinal = false;
        constExpr = null;
        loopCount = 0;
        name = null;
        type = Type.tUnknown;
        slot = nextAnonymousSlot--;
    }

    public LocalInfo(MethodAnalyzer methodanalyzer, int i) {
        operators = new Vector();
        hints = new Vector();
        removed = false;
        isFinal = false;
        constExpr = null;
        loopCount = 0;
        name = null;
        type = Type.tUnknown;
        methodAnalyzer = methodanalyzer;
        slot = i;
    }

    public static void init() {
        serialnr = 0;
    }

    public void setOperator(LocalVarOperator localvaroperator) {
        getLocalInfo().operators.addElement(localvaroperator);
    }

    public void addHint(String string, Type type) {
        getLocalInfo().hints.addElement(new Hint(string, type));
    }

    public int getUseCount() {
        return getLocalInfo().operators.size();
    }

    public void combineWith(LocalInfo localinfo_1_) {
        if (shadow != null)
            getLocalInfo().combineWith(localinfo_1_);
        else {
            localinfo_1_ = localinfo_1_.getLocalInfo();
            if (this != localinfo_1_) {
                shadow = localinfo_1_;
                if (!nameIsGenerated)
                    localinfo_1_.name = name;
                if (constExpr != null) {
                    if (localinfo_1_.constExpr != null)
                        throw new AssertError("local has multiple constExpr");
                    localinfo_1_.constExpr = constExpr;
                }
                localinfo_1_.setType(type);
                boolean bool = !localinfo_1_.type.equals(type);
                Enumeration enumeration = operators.elements();
                while (enumeration.hasMoreElements()) {
                    LocalVarOperator localvaroperator =
                        (LocalVarOperator) enumeration.nextElement();
                    if (bool) {
                        if ((GlobalOptions.debuggingFlags & 0x4) != 0)
                            GlobalOptions.err.println("updating " + localvaroperator);
                        localvaroperator.updateType();
                    }
                    localinfo_1_.operators.addElement(localvaroperator);
                }
                enumeration = hints.elements();
                while (enumeration.hasMoreElements()) {
                    Object object = enumeration.nextElement();
                    if (!localinfo_1_.hints.contains(object))
                        localinfo_1_.hints.addElement(object);
                }
                type = null;
                name = null;
                operators = null;
            }
        }
    }

    public LocalInfo getLocalInfo() {
        if (shadow != null) {
                for (/**/; shadow.shadow != null; shadow = shadow.shadow) {
                /* empty */
            }
            return shadow;
        }
        return this;
    }

    public boolean hasName() {
        return getLocalInfo().name != null;
    }

    public String guessName() {
    	return guessName1();
    	
//    	String name = guessName1();
//    	//xxxxx
//		// if (name.equals("this$0")) {
//			System.out.println(name);
//		// }
//    	return name;

    }

    private String guessName1() {
		if (shadow != null) {
				for (/**/; shadow.shadow != null; shadow = shadow.shadow) {
				/* empty */
			}
			return shadow.guessName();
		}
		if (name == null) {
			Enumeration enumeration = hints.elements();
			while (enumeration.hasMoreElements()) {
				Hint hint = (Hint) enumeration.nextElement();
				if (type.isOfType(hint.getType())) {
					name = hint.getName();
					setType(hint.getType());
					return name;
				}
			}
			nameIsGenerated = true;
			if ((GlobalOptions.debuggingFlags & 0x4) != 0)
				GlobalOptions.err.println(getName() + " set type to getHint()");
			setType(type.getHint());
			if ((Options.options & 0x10) != 0)
				name = type.getDefaultName();
			else {
				name =
					(type.getDefaultName() + (slot >= 0 ? "_" + slot : "") + "_" + serialnr++ +"_");
				isUnique = true;
			}
			if ((GlobalOptions.debuggingFlags & 0x100) != 0) {
				GlobalOptions.err.println("Guessed name: " + name + " from type: " + type);
				Thread.dumpStack();
			}
		}
		return name;
    }

    public String getName() {
        if (shadow != null) {
                for (/**/; shadow.shadow != null; shadow = shadow.shadow) {
                /* empty */
            }
            return shadow.getName();
        }
        if (name == null)
            return ("local_" + slot + "_" + Integer.toHexString(this.hashCode()));
        return name;
    }

    public boolean isNameGenerated() {
        return getLocalInfo().nameIsGenerated;
    }

    public int getSlot() {
        return getLocalInfo().slot;
    }

    public void setName(String string) {
        LocalInfo localinfo_2_ = getLocalInfo();
        localinfo_2_.name = string;
    }

    public void makeNameUnique() {
        LocalInfo localinfo_3_ = getLocalInfo();
        String string = localinfo_3_.getName();
        if (!localinfo_3_.isUnique) {
            localinfo_3_.name = string + "_" + serialnr++ +"_";
            localinfo_3_.isUnique = true;
        }
    }

    public Type getType() {
        return getLocalInfo().type;
    }

    public Type setType(Type type) {
        LocalInfo localinfo_4_ = getLocalInfo();
        if (localinfo_4_.loopCount++ > 5) {
            GlobalOptions.err.println(
                "Type error in local "
                    + getName()
                    + ": "
                    + localinfo_4_.type
                    + " seems to be recursive.");
            Thread.dumpStack();
            type = Type.tError;
        }
        Type type_5_ = localinfo_4_.type.intersection(type);
        if (type_5_ == Type.tError && type != Type.tError && localinfo_4_.type != Type.tError) {
            GlobalOptions.err.println(
                "Type error in local " + getName() + ": " + localinfo_4_.type + " and " + type);
            Thread.dumpStack();
        } else if ((GlobalOptions.debuggingFlags & 0x4) != 0)
            GlobalOptions.err.println(
                getName() + " setType, new: " + type_5_ + " old: " + localinfo_4_.type);
        if (!localinfo_4_.type.equals(type_5_)) {
            localinfo_4_.type = type_5_;
            Enumeration enumeration = localinfo_4_.operators.elements();
            while (enumeration.hasMoreElements()) {
                LocalVarOperator localvaroperator = (LocalVarOperator) enumeration.nextElement();
                if ((GlobalOptions.debuggingFlags & 0x4) != 0)
                    GlobalOptions.err.println("updating " + localvaroperator);
                localvaroperator.updateType();
            }
        }
        localinfo_4_.loopCount--;
        return localinfo_4_.type;
    }

    public void setExpression(Expression expression) {
        setType(expression.getType());
        getLocalInfo().constExpr = expression;
    }

    public Expression getExpression() {
        return getLocalInfo().constExpr;
    }

    public boolean isShadow() {
        return shadow != null;
    }

    public boolean equals(Object object) {
        return (
            object instanceof LocalInfo && ((LocalInfo) object).getLocalInfo() == getLocalInfo());
    }

    public void remove() {
        removed = true;
    }

    public boolean isRemoved() {
        return removed;
    }

    public boolean isConstant() {
        return true;
    }

    public MethodAnalyzer getMethodAnalyzer() {
        return methodAnalyzer;
    }

    public boolean markFinal() {
        LocalInfo localinfo_6_ = getLocalInfo();
        Enumeration enumeration = localinfo_6_.operators.elements();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            if (((LocalVarOperator) enumeration.nextElement()).isWrite())
                i++;
        }
        localinfo_6_.isFinal = true;
        return true;
    }

    public boolean isFinal() {
        return getLocalInfo().isFinal;
    }

    public String toString() {
        return getName();
    }

    public void dumpDeclaration(TabbedPrintWriter tabbedprintwriter) throws IOException {
        LocalInfo localinfo_7_ = getLocalInfo();
        if (localinfo_7_.isFinal)
            tabbedprintwriter.print("final ");
        tabbedprintwriter.printType(localinfo_7_.getType().getHint());
        tabbedprintwriter.print(" " + localinfo_7_.getName().toString());
    }
}
