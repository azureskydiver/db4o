/* MethodAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.LocalVariableInfo;
import jode.bytecode.MethodInfo;
import jode.expr.CheckNullOperator;
import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.expr.LocalLoadOperator;
import jode.expr.OuterLocalOperator;
import jode.expr.ThisOperator;
import jode.flow.EmptyBlock;
import jode.flow.FlowBlock;
import jode.flow.Jump;
import jode.flow.StructuredBlock;
import jode.flow.TransformExceptionHandlers;
import jode.jvm.CodeVerifier;
import jode.jvm.SyntheticAnalyzer;
import jode.jvm.VerifyException;
import jode.type.MethodType;
import jode.type.Type;

public class MethodAnalyzer implements Scope, ClassDeclarer {
	
	// xxxcr where does this one go? Inspect it
	public static FlowBlock globalMethodheader;
	
    private static double STEP_COMPLEXITY = 0.01;
    private static int STRICTFP = 2048;
    ImportHandler imports;
    ClassAnalyzer classAnalyzer;
    MethodInfo minfo;
    BytecodeInfo code;
    String methodName;
    MethodType methodType;
    boolean isConstructor;
    Type[] exceptions;
    SyntheticAnalyzer synth;
    FlowBlock methodHeader;
    Vector allLocals = new Vector();
    LocalInfo[] param;
    LocalVariableTable lvt;
    MethodAnalyzer jikesConstructor;
    boolean hasJikesOuterValue;
    boolean isAnonymousConstructor;
    boolean isJikesBlockInitializer;
    Vector anonConstructors = new Vector();
    Vector innerAnalyzers;
    Collection usedAnalyzers;

    public MethodAnalyzer(
        ClassAnalyzer classanalyzer,
        MethodInfo methodinfo,
        ImportHandler importhandler) {
        classAnalyzer = classanalyzer;
        imports = importhandler;
        minfo = methodinfo;
        methodName = methodinfo.getName();
        methodType = Type.tMethod(methodinfo.getType());
        isConstructor = methodName.equals("<init>") || methodName.equals("<clinit>");
        if (methodinfo.getBytecode() != null)
            code = methodinfo.getBytecode();
        String[] strings = methodinfo.getExceptions();
        if (strings == null)
            exceptions = new Type[0];
        else {
            int i = strings.length;
            exceptions = new Type[i];
            for (int i_0_ = 0; i_0_ < i; i_0_++)
                exceptions[i_0_] = Type.tClass(strings[i_0_]);
        }
        if (methodinfo.isSynthetic() || methodName.indexOf('$') != -1)
            synth = new SyntheticAnalyzer(methodinfo, true);
    }

    public String getName() {
        return methodName;
    }

    public MethodType getType() {
        return methodType;
    }

    public FlowBlock getMethodHeader() {
        return methodHeader;
    }

    public final BytecodeInfo getBytecodeInfo() {
        return code;
    }

    public final ImportHandler getImportHandler() {
        return imports;
    }

    public final void useType(Type type) {
        imports.useType(type);
    }

    public void insertStructuredBlock(StructuredBlock structuredblock) {
        if (methodHeader != null) {
            structuredblock.setJump(new Jump(FlowBlock.NEXT_BY_ADDR));
            FlowBlock flowblock = new FlowBlock(this, 0);
            flowblock.appendBlock(structuredblock, 0);
            flowblock.setNextByAddr(methodHeader);
            flowblock.doT2(methodHeader);
            methodHeader = flowblock;
        } else
            throw new IllegalStateException();
    }

    public final boolean isConstructor() {
        return isConstructor;
    }

    public final boolean isStatic() {
        return minfo.isStatic();
    }

    public final boolean isSynthetic() {
        return minfo.isSynthetic();
    }

    public final boolean isStrictFP() {
        return (minfo.getModifiers() & STRICTFP) != 0;
    }

    public final void setJikesConstructor(MethodAnalyzer methodanalyzer_1_) {
        jikesConstructor = methodanalyzer_1_;
    }

    public final void setJikesBlockInitializer(boolean bool) {
        isJikesBlockInitializer = bool;
    }

    public final void setHasOuterValue(boolean bool) {
        hasJikesOuterValue = bool;
    }

    public final void setAnonymousConstructor(boolean bool) {
        isAnonymousConstructor = bool;
    }

    public final boolean isAnonymousConstructor() {
        return isAnonymousConstructor;
    }

    public final SyntheticAnalyzer getSynthetic() {
        return synth;
    }

    public Type getReturnType() {
        return methodType.getReturnType();
    }

    public ClassAnalyzer getClassAnalyzer() {
        return classAnalyzer;
    }

    public ClassInfo getClazz() {
        return classAnalyzer.clazz;
    }

    public final LocalInfo getParamInfo(int i) {
        return param[i];
    }

    public final int getParamCount() {
        return param.length;
    }

    public LocalInfo getLocalInfo(int i, int i_2_) {
        LocalInfo localinfo = new LocalInfo(this, i_2_);
        if (lvt != null) {
            LocalVarEntry localvarentry = lvt.getLocal(i_2_, i);
            if (localvarentry != null)
                localinfo.addHint(localvarentry.getName(), localvarentry.getType());
        }
        allLocals.addElement(localinfo);
        return localinfo;
    }

    public double getComplexity() {
        if (code == null)
            return 0.0;
        return (double) code.getInstructions().size();
    }

    private void analyzeCode(ProgressListener progresslistener, double d, double d_3_) {
        int i = 2147483647;
        if (GlobalOptions.verboseLevel > 0)
            GlobalOptions.err.print(methodName + ": ");
        if (progresslistener != null)
            i = (int) ((double) code.getInstructions().size() * STEP_COMPLEXITY / (d_3_ * 0.9));
        // xxxcr We don't have dead code
        // DeadCodeAnalysis.removeDeadCode(code);
        Handler[] handlers = code.getExceptionHandlers();
        Iterator iterator = code.getInstructions().iterator();
        while (iterator.hasNext()) {
            Instruction instruction = (Instruction) iterator.next();
            if (instruction.getPrevByAddr() == null
                || instruction.getPrevByAddr().doesAlwaysJump()
                || instruction.getPreds() != null)
                instruction.setTmpInfo(new FlowBlock(this, instruction.getAddr()));
        }
        for (int i_4_ = 0; i_4_ < handlers.length; i_4_++) {
            Instruction instruction = handlers[i_4_].start;
            if (instruction.getTmpInfo() == null)
                instruction.setTmpInfo(new FlowBlock(this, instruction.getAddr()));
            instruction = handlers[i_4_].end.getNextByAddr();
            if (instruction.getTmpInfo() == null)
                instruction.setTmpInfo(new FlowBlock(this, instruction.getAddr()));
            instruction = handlers[i_4_].catcher;
            if (instruction.getTmpInfo() == null)
                instruction.setTmpInfo(new FlowBlock(this, instruction.getAddr()));
        }
        int i_5_ = 1000;
        int i_6_ = 0;
        FlowBlock flowblock = null;
        boolean bool = false;
        Iterator iterator_7_ = code.getInstructions().iterator();
        while (iterator_7_.hasNext()) {
            Instruction instruction = (Instruction) iterator_7_.next();
            StructuredBlock structuredblock = Opcodes.readOpcode(instruction, this);
            if (GlobalOptions.verboseLevel > 0 && instruction.getAddr() > i_5_) {
                GlobalOptions.err.print('.');
                i_5_ += 1000;
            }
            if (++i_6_ >= i) {
                d += ((double) i_6_ * d_3_ / (double) code.getInstructions().size());
                progresslistener.updateProgress(d, methodName);
                i_6_ = 0;
            }
            if (bool
                && instruction.getTmpInfo() == null
                && !instruction.doesAlwaysJump()
                && instruction.getSuccs() == null)
                flowblock.appendBlock(structuredblock, instruction.getLength());
            else {
                if (instruction.getTmpInfo() == null)
                    instruction.setTmpInfo(new FlowBlock(this, instruction.getAddr()));
                FlowBlock flowblock_8_ = (FlowBlock) instruction.getTmpInfo();
                flowblock_8_.appendBlock(structuredblock, instruction.getLength());
                if (flowblock != null)
                    flowblock.setNextByAddr(flowblock_8_);
                instruction.setTmpInfo(flowblock = flowblock_8_);
                bool = (!instruction.doesAlwaysJump() && instruction.getSuccs() == null);
            }
        }
        methodHeader = ((FlowBlock) ((Instruction) code.getInstructions().get(0)).getTmpInfo());
        TransformExceptionHandlers transformexceptionhandlers = new TransformExceptionHandlers();
        for (int i_9_ = 0; i_9_ < handlers.length; i_9_++) {
            jode.type.ClassInterfacesType classinterfacestype = null;
            FlowBlock flowblock_10_ = (FlowBlock) handlers[i_9_].start.getTmpInfo();
            int i_11_ = handlers[i_9_].end.getNextByAddr().getAddr();
            FlowBlock flowblock_12_ = (FlowBlock) handlers[i_9_].catcher.getTmpInfo();
            if (handlers[i_9_].type != null)
                classinterfacestype = Type.tClass(handlers[i_9_].type);
            transformexceptionhandlers.addHandler(
                flowblock_10_,
                i_11_,
                flowblock_12_,
                classinterfacestype);
        }
        //        iterator = code.getInstructions().iterator();
        //        while (iterator.hasNext()) {
        //            Instruction instruction = (Instruction) iterator.next();
        //            instruction.setTmpInfo(null);
        //        }
        if (GlobalOptions.verboseLevel > 0)
            GlobalOptions.err.print('-');
        transformexceptionhandlers.analyze();
        methodHeader.analyze();
        if ((Options.options & 0x8) == 0 && methodHeader.mapStackToLocal())
            methodHeader.removePush();
        if ((Options.options & 0x40) != 0)
            methodHeader.removeOnetimeLocals();
        methodHeader.mergeParams(param);
        if (GlobalOptions.verboseLevel > 0)
            GlobalOptions.err.println("");
        if (progresslistener != null) {
            d += 0.1 * d_3_;
            progresslistener.updateProgress(d, methodName);
        }
    }

    public void analyze(ProgressListener progresslistener, double d, double d_13_)
        throws ClassFormatError {
        if (progresslistener != null)
            progresslistener.updateProgress(d, methodName);
        if (code != null) {
            if ((Options.options & 0x100) != 0) {
                CodeVerifier codeverifier = new CodeVerifier(getClazz(), minfo, code);
                try {
                    codeverifier.verify();
                } catch (VerifyException verifyexception) {
                    verifyexception.printStackTrace(GlobalOptions.err);
                    throw new AssertError("Verification error");
                }
            }
            if ((Options.options & 0x1) != 0) {
                LocalVariableInfo[] localvariableinfos = code.getLocalVariableTable();
                if (localvariableinfos != null)
                    lvt = new LocalVariableTable(code.getMaxLocals(), localvariableinfos);
            }
        }
        Type[] types = getType().getParameterTypes();
        int i = (isStatic() ? 0 : 1) + types.length;
        param = new LocalInfo[i];
        int i_14_ = 0;
        int i_15_ = 0;
        if (!isStatic()) {
            ClassInfo classinfo = classAnalyzer.getClazz();
            LocalInfo localinfo = getLocalInfo(0, i_15_++);
            localinfo.setExpression(new ThisOperator(classinfo, true));
            param[i_14_++] = localinfo;
        }
        for (int i_16_ = 0; i_16_ < types.length; i_16_++) {
            param[i_14_] = getLocalInfo(0, i_15_);
            param[i_14_].setType(types[i_16_]);
            i_15_ += types[i_16_].stackSize();
            i_14_++;
        }
        for (int i_17_ = 0; i_17_ < exceptions.length; i_17_++)
            imports.useType(exceptions[i_17_]);
        if (!isConstructor)
            imports.useType(methodType.getReturnType());
        if (code != null)
            analyzeCode(progresslistener, d, d_13_);
    }

    public void analyzeInnerClasses() throws ClassFormatError {
        boolean bool = false;
        Enumeration enumeration = anonConstructors.elements();
        while (enumeration.hasMoreElements()) {
            InvokeOperator invokeoperator = (InvokeOperator) enumeration.nextElement();
            analyzeInvokeOperator(invokeoperator);
        }
    }

    public void makeDeclaration(Set set) {
        if (innerAnalyzers != null) {
            Enumeration enumeration = innerAnalyzers.elements();
            while (enumeration.hasMoreElements()) {
                ClassAnalyzer classanalyzer = (ClassAnalyzer) enumeration.nextElement();
                if (classanalyzer.getParent() == this) {
                    OuterValues outervalues = classanalyzer.getOuterValues();
                    for (int i = 0; i < outervalues.getCount(); i++) {
                        Expression expression = outervalues.getValue(i);
                        if (expression instanceof OuterLocalOperator) {
                            LocalInfo localinfo = ((OuterLocalOperator) expression).getLocalInfo();
                            if (localinfo.getMethodAnalyzer() == this)
                                localinfo.markFinal();
                        }
                    }
                }
            }
        }
        Enumeration enumeration = allLocals.elements();
        while (enumeration.hasMoreElements()) {
            LocalInfo localinfo = (LocalInfo) enumeration.nextElement();
            if (!localinfo.isShadow())
                imports.useType(localinfo.getType());
        }
        for (int i = 0; i < param.length; i++) {
            param[i].guessName();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Declarable declarable = (Declarable) iterator.next();
                if (param[i].getName().equals(declarable.getName())) {
                    param[i].makeNameUnique();
                    break;
                }
            }
            set.add(param[i]);
        }
        if (code != null) {
        	globalMethodheader = methodHeader;
            methodHeader.makeDeclaration(set);
            methodHeader.simplify();
        }
        for (int i = 0; i < param.length; i++)
            set.remove(param[i]);
    }

    public boolean skipWriting() {
        if (synth != null) {
            int i = synth.getKind();
            if (this != null) {
                /* empty */
            }
            if (i == 1)
                return true;
            int i_18_ = synth.getKind();
            if (this != null) {
                /* empty */
            }
            if (i_18_ >= 2) {
                int i_19_ = synth.getKind();
                if (this != null) {
                    /* empty */
                }
                if (i_19_ <= 10 && (Options.options & 0x2) != 0 && (Options.options & 0x4) != 0)
                    return true;
            }
        }
        if (jikesConstructor == this)
            return true;
        boolean bool = isConstructor;
        int i = 0;
        if (isConstructor() && !isStatic() && classAnalyzer.outerValues != null)
            i = classAnalyzer.outerValues.getCount();
        if (jikesConstructor != null) {
            bool = true;
            i = (hasJikesOuterValue && classAnalyzer.outerValues.getCount() > 0 ? 1 : 0);
        }
        if (isJikesBlockInitializer)
            return true;
        if (getMethodHeader() == null
            || !(getMethodHeader().getBlock() instanceof EmptyBlock)
            || !getMethodHeader().hasNoJumps()
            || exceptions.length > 0)
            return false;
        if (bool
            && (((minfo.getModifiers() & 0x52f) == (getClassAnalyzer().getModifiers() & 0x5))
                || classAnalyzer.getName() == null)
            && classAnalyzer.constructors.length == 1
            && (methodType.getParameterTypes().length == i || isAnonymousConstructor))
            return true;
        if (isConstructor() && isStatic())
            return true;
        return false;
    }

    public void dumpSource(TabbedPrintWriter tabbedprintwriter) throws IOException {
        boolean bool = isConstructor;
        int i = 0;
        int i_20_ = minfo.getModifiers();
        if (isConstructor()
            && !isStatic()
            && (Options.options & 0x200) != 0
            && classAnalyzer.outerValues != null)
            i = classAnalyzer.outerValues.getCount();
        if (jikesConstructor != null) {
            bool = true;
            i = (hasJikesOuterValue && classAnalyzer.outerValues.getCount() > 0 ? 1 : 0);
            i_20_ = jikesConstructor.minfo.getModifiers();
        }
        if (minfo.isDeprecated()) {
            tabbedprintwriter.println("/**");
            tabbedprintwriter.println(" * @deprecated");
            tabbedprintwriter.println(" */");
        }
        tabbedprintwriter.pushScope(this);
        if (classAnalyzer.getClazz().isInterface())
            i_20_ &= ~0x400;
        if (isConstructor() && isStatic())
            i_20_ &= ~0x17;
        i_20_ &= STRICTFP ^ 0xffffffff;
        tabbedprintwriter.startOp(1, 1);
        String string = "";
        if (minfo.isSynthetic()) {
            tabbedprintwriter.print("/*synthetic*/");
            string = " ";
        }
        String string_21_ = Modifier.toString(i_20_);
        if (string_21_.length() > 0) {
            tabbedprintwriter.print(string + string_21_);
            string = " ";
        }
        if (isStrictFP()
            && !classAnalyzer.isStrictFP()
            && !isConstructor()
            && (i_20_ & 0x100) == 0) {
            tabbedprintwriter.print(string + "strictfp");
            string = " ";
        }
        if (!isConstructor
            || (!isStatic()
                && (classAnalyzer.getName() != null || i != methodType.getParameterTypes().length))) {
            tabbedprintwriter.print(string);
            if (bool)
                tabbedprintwriter.print(classAnalyzer.getName());
            else {
                tabbedprintwriter.printType(getReturnType());
                tabbedprintwriter.print(" " + methodName);
            }
            tabbedprintwriter.breakOp();
            if ((Options.outputStyle & 0x40) != 0)
                tabbedprintwriter.print(" ");
            tabbedprintwriter.print("(");
            tabbedprintwriter.startOp(0, 0);
            int i_22_ = i + (isStatic() ? 0 : 1);
            for (int i_23_ = i_22_; i_23_ < param.length; i_23_++) {
                if (i_23_ > i_22_) {
                    tabbedprintwriter.print(", ");
                    tabbedprintwriter.breakOp();
                }
                param[i_23_].dumpDeclaration(tabbedprintwriter);
            }
            tabbedprintwriter.endOp();
            tabbedprintwriter.print(")");
        }
        if (exceptions.length > 0) {
            tabbedprintwriter.breakOp();
            tabbedprintwriter.print(" throws ");
            tabbedprintwriter.startOp(0, 2);
            for (int i_24_ = 0; i_24_ < exceptions.length; i_24_++) {
                if (i_24_ > 0) {
                    tabbedprintwriter.print(",");
                    tabbedprintwriter.breakOp();
                    tabbedprintwriter.print(" ");
                }
                tabbedprintwriter.printType(exceptions[i_24_]);
            }
            tabbedprintwriter.endOp();
        }
        tabbedprintwriter.endOp();
        if (code != null) {
            tabbedprintwriter.openBraceNoIndent();
            tabbedprintwriter.tab();
            methodHeader.dumpSource(tabbedprintwriter);
            tabbedprintwriter.untab();
            tabbedprintwriter.closeBraceNoIndent();
        } else
            tabbedprintwriter.println(";");
        tabbedprintwriter.popScope();
    }

    public LocalInfo findLocal(String string) {
        Enumeration enumeration = allLocals.elements();
        while (enumeration.hasMoreElements()) {
            LocalInfo localinfo = (LocalInfo) enumeration.nextElement();
            if (localinfo.getName().equals(string))
                return localinfo;
        }
        return null;
    }

    public ClassAnalyzer findAnonClass(String string) {
        if (innerAnalyzers != null) {
            Enumeration enumeration = innerAnalyzers.elements();
            while (enumeration.hasMoreElements()) {
                ClassAnalyzer classanalyzer = (ClassAnalyzer) enumeration.nextElement();
                if (classanalyzer.getParent() == this
                    && classanalyzer.getName() != null
                    && classanalyzer.getName().equals(string))
                    return classanalyzer;
            }
        }
        return null;
    }

    public boolean isScopeOf(Object object, int i) {
        if (i == 2 && object instanceof ClassInfo) {
            ClassAnalyzer classanalyzer = getClassAnalyzer((ClassInfo) object);
            if (classanalyzer != null)
                return classanalyzer.getParent() == this;
        }
        return false;
    }

    public boolean conflicts(String string, int i) {
        if (i == 4 || i == 5)
            return findLocal(string) != null;
        if (i == 4 || i == 1)
            return findAnonClass(string) != null;
        return false;
    }

    public ClassDeclarer getParent() {
        return getClassAnalyzer();
    }

    public void addAnonymousConstructor(InvokeOperator invokeoperator) {
        anonConstructors.addElement(invokeoperator);
    }

    public void analyzeInvokeOperator(InvokeOperator invokeoperator) {
        ClassInfo classinfo = invokeoperator.getClassInfo();
        ClassAnalyzer classanalyzer = getParent().getClassAnalyzer(classinfo);
        if (classanalyzer == null) {
            Expression[] expressions = invokeoperator.getSubExpressions();
            Expression[] expressions_25_ = new Expression[expressions.length - 1];
            for (int i = 0; i < expressions_25_.length; i++) {
                Expression expression = expressions[i + 1].simplify();
                if (expression instanceof CheckNullOperator)
                    expression = ((CheckNullOperator) expression).getSubExpressions()[0];
                if (expression instanceof ThisOperator)
                    expressions_25_[i] =
                        new ThisOperator(((ThisOperator) expression).getClassInfo());
                else {
                    LocalInfo localinfo = null;
                    if (expression instanceof LocalLoadOperator) {
                        localinfo = ((LocalLoadOperator) expression).getLocalInfo();
                        if (!localinfo.isConstant())
                            localinfo = null;
                    }
                    if (expression instanceof OuterLocalOperator)
                        localinfo = ((OuterLocalOperator) expression).getLocalInfo();
                    if (localinfo != null)
                        expressions_25_[i] = new OuterLocalOperator(localinfo);
                    else {
                        Expression[] expressions_26_ = new Expression[i];
                        System.arraycopy(expressions_25_, 0, expressions_26_, 0, i);
                        expressions_25_ = expressions_26_;
                        break;
                    }
                }
            }
            classanalyzer = new ClassAnalyzer(this, classinfo, imports, expressions_25_);
            addClassAnalyzer(classanalyzer);
            classanalyzer.initialize();
            classanalyzer.analyze(null, 0.0, 0.0);
            classanalyzer.analyzeInnerClasses(null, 0.0, 0.0);
        } else {
            OuterValues outervalues = classanalyzer.getOuterValues();
            Expression[] expressions = invokeoperator.getSubExpressions();
            int i = 0;
                for (/**/; i < outervalues.getCount(); i++) {
                if (i + 1 < expressions.length) {
                    Expression expression = expressions[i + 1].simplify();
                    if (expression instanceof CheckNullOperator)
                        expression = ((CheckNullOperator) expression).getSubExpressions()[0];
                    if (outervalues.unifyOuterValues(i, expression))
                        continue;
                }
                outervalues.setCount(i);
                break;
            }
        }
        if (usedAnalyzers == null)
            usedAnalyzers = new ArrayList();
        usedAnalyzers.add(classanalyzer);
    }

    public ClassAnalyzer getClassAnalyzer(ClassInfo classinfo) {
        if (innerAnalyzers != null) {
            Enumeration enumeration = innerAnalyzers.elements();
            while (enumeration.hasMoreElements()) {
                ClassAnalyzer classanalyzer = (ClassAnalyzer) enumeration.nextElement();
                if (classanalyzer.getClazz().equals(classinfo)) {
                    if (classanalyzer.getParent() != this) {
                        for (ClassDeclarer classdeclarer = classanalyzer.getParent();
                            classdeclarer != this;
                            classdeclarer = classdeclarer.getParent()) {
                            if (classdeclarer instanceof MethodAnalyzer)
                                ((MethodAnalyzer) classdeclarer).innerAnalyzers.removeElement(
                                    classanalyzer);
                        }
                        classanalyzer.setParent(this);
                    }
                    return classanalyzer;
                }
            }
        }
        return getParent().getClassAnalyzer(classinfo);
    }

    public void addClassAnalyzer(ClassAnalyzer classanalyzer) {
        if (innerAnalyzers == null)
            innerAnalyzers = new Vector();
        innerAnalyzers.addElement(classanalyzer);
        getParent().addClassAnalyzer(classanalyzer);
    }

    public void fillDeclarables(Collection collection) {
        if (usedAnalyzers != null)
            collection.addAll(usedAnalyzers);
        if (innerAnalyzers != null) {
            Enumeration enumeration = innerAnalyzers.elements();
            while (enumeration.hasMoreElements()) {
                ClassAnalyzer classanalyzer = (ClassAnalyzer) enumeration.nextElement();
                if (classanalyzer.getParent() == this)
                    classanalyzer.fillDeclarables(collection);
            }
        }
    }

    public boolean isMoreOuterThan(ClassDeclarer classdeclarer) {
        for (ClassDeclarer classdeclarer_27_ = classdeclarer;
            classdeclarer_27_ != null;
            classdeclarer_27_ = classdeclarer_27_.getParent()) {
            if (classdeclarer_27_ == this)
                return true;
        }
        return false;
    }

    public String toString() {

        // xxxcr wanna see all 

        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName() + "[" + getClazz() + "." + getName() + "]");

        // the instructions

        //        if (code != null) {
        //            List list = code.getInstructions();
        //            if (list != null) {
        //                Iterator i = list.iterator();
        //                while (i.hasNext()) {
        //                    Instruction instruction = (Instruction) i.next();
        //                    StructuredBlock structuredblock = Opcodes.readOpcode(instruction, this);
        //                    sb.append("\r\n   " + structuredblock.toString());
        //                }
        //            }
        //        }
        
        if(methodHeader != null){
        	sb.append("\r\n" + methodHeader);
        }

        return sb.toString();
    }
}
