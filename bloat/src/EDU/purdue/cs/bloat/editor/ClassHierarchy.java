/**
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and other
 * materials related to such distribution and use acknowledge that the
 * software was developed at Purdue University, West Lafayette, IN by
 * Antony Hosking, David Whitlock, and Nathaniel Nystrom.  No charge
 * may be made for copies, derivations, or distributions of this
 * material without the express written consent of the copyright
 * holder.  Neither the name of the University nor the name of the
 * author may be used to endorse or promote products derived from this
 * material without specific prior written permission.  THIS SOFTWARE
 * IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 *
 * <p>
 * Java is a trademark of Sun Microsystems, Inc.
 */

package EDU.purdue.cs.bloat.editor;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.util.*;
import java.util.*;
import java.io.*;

/**
 * ClassHierarchy maintains a graph of the subclass relationships of the
 * classes loaded by the ClassInfoLoader.
 *
 * @see ClassInfoLoader
 */
public class ClassHierarchy {
  public static final Type POS_SHORT = Type.getType("L+short!;");
  public static final Type POS_BYTE  = Type.getType("L+byte!;");

  static final int MAX_INT   = 8;
  static final int MAX_SHORT = 7;
  static final int MAX_CHAR  = 6;
  static final int MAX_BYTE  = 5;
  static final int MAX_BOOL  = 4;
  static final int MIN_CHAR  = 3;
  static final int MIN_BOOL  = 3;
  static final int ZERO      = 3;
  static final int MIN_BYTE  = 2;
  static final int MIN_SHORT = 1;
  static final int MIN_INT   = 0;

  public static boolean DEBUG = false;
  public static boolean RELAX = false;

  Set classes;             // The Types of the classes in hierarchy
  Graph extendsGraph;        // "Who extends who" graph
  Graph implementsGraph;    // "Who implements what" graph

  boolean closure;         // Do we visit all referenced classes?

  // Maps methods to the methods they may resolve to
  private Map resolvesToCache;

  // These are only needed during construction.
  EditorContext context;
  LinkedList worklist;
  Set inWorklist;

  private void db(String s) {
    if(DEBUG)
      System.out.println(s);
  }

  /**
   * Constructor.
   *
   * @param context
   *        The context in which to access an <Tt>Editor</tt> and
   *        other such things. 
   * @param initial
   *        The names of the classes that initially constitue the
   *        hierarchy.
   * @param closure
   *        Do we get the maximum amount of class information?
   */
  public ClassHierarchy(EditorContext context, Collection initial, 
			boolean closure) {
    this.context = context;
    this.closure = closure;

    classes = new HashSet();
    extendsGraph = new Graph();
    implementsGraph = new Graph();

    worklist = new LinkedList();
    inWorklist = new HashSet();
 
    this.resolvesToCache = new HashMap();

    // Need new ArrayList to avoid ConcurrentModificationException
    Iterator iter = new ArrayList(initial).iterator();

    while (iter.hasNext()) {
      String name = (String) iter.next();
      addClass(name);
    }
  }

  /**
   * Adds a class of a given name to the ClassHierarchy.
   */
  public void addClassNamed(String name) {
    addClass(name);
  }

  /**
   * Returns the immediate subclasses of a given <tt>Type</tt> as a
   * <tt>Collection</tt> of <tt>Type</tt>s.
   *
   * <p>
   *
   * The subclass relationship at the classfile level is a little
   * screwy with respect to interfaces.  An interface that extends
   * another interface is compiled into an interface that extends
   * java.lang.Object and implements the superinterface.  As a result,
   * the interface-subinterface is not captured in <tt>subclasses</tt>
   * as one may expect.  Instead, you have to look at
   * <tt>implementors</tt> and filter out the classes.
   */
  public Collection subclasses(Type type) {
    TypeNode node = (TypeNode) getExtendsNode(type);

    if (node != null) {
      List list = new ArrayList(extendsGraph.preds(node));

      ListIterator iter = list.listIterator();

      while (iter.hasNext()) {
	TypeNode v = (TypeNode) iter.next();
	iter.set(v.type);
      }

      return list;
    }

    return new ArrayList();
  }

  /** 
   * Returns the superclass of a given <tt>Type</tt>.  If the
   * <tt>Type</tt> has no superclass (that is it is
   * <tt>Type.OBJECT</tt>), then null is returned.  
   */
  public Type superclass(Type type) {
    TypeNode node = (TypeNode) getExtendsNode(type);

    if (node != null) {
      Collection succs = extendsGraph.succs(node);

      Iterator iter = succs.iterator();

      if (iter.hasNext()) {
	TypeNode v = (TypeNode) iter.next();
	return v.type;
      }
    }

    return null;
  }

  /**
   * Returns the interfaces that a given <tt>Type</tt> implements as a
   * <tt>Collection</tt> of <tt>Types</tt> 
   */
  public Collection interfaces(Type type) {
    TypeNode node = (TypeNode) getImplementsNode(type);

    if (node != null) {
      List list = new ArrayList(implementsGraph.succs(node));

      ListIterator iter = list.listIterator();

      while (iter.hasNext()) {
	TypeNode v = (TypeNode) iter.next();
	iter.set(v.type);
      }

      return list;
    }

    return new ArrayList();
  }

  /** 
   * Returns the classes (<tt>Type</tt>s) that implement a given
   * interface as a <tt>Collection</tt> of <Tt>Type</tt>s.
   *
   * <p>
   *
   * See note in <tt>subclasses</tt> for information about the
   * interface hierarchy.
   */
  public Collection implementors(Type type) {
    TypeNode node = (TypeNode) getImplementsNode(type);

    if (node != null) {
      List list = new ArrayList(implementsGraph.preds(node));

      ListIterator iter = list.listIterator();

      while (iter.hasNext()) {
	TypeNode v = (TypeNode) iter.next();
	iter.set(v.type);
      }

      return list;
    }

    return new ArrayList();
  }

  /**
   * Returns whether or not a is a subclass of b.
   */
  public boolean subclassOf(Type a, Type b) {
    // Is a <= b?
    Assert.isTrue(a.isReference() && b.isReference(),
		  "Cannot compare " + a + " and " + b);

    // a <= a: true
    if (a.equals(b)) {
      return true;
    }

    // a <= java.lang.Object: true
    if (b.equals(Type.OBJECT)) {
      return true;
    }

    // null <= null: true
    // a <= null: false
    if (b.isNull()) {
      return a.isNull();
    }

    if (a.isArray()) {
      if (b.isArray()) {
	// Both reference arrays.
	// a <= b -> a[] <= b[]
	if (a.elementType().isReference() &&
	    b.elementType().isReference()) {

	  return subclassOf(a.elementType(), b.elementType());
	}

	// a[] <= a[]: true
	return a.elementType().equals(b.elementType());
      }

      // Only one is an array (and b is not Object--tested above).
      return false;
    }

    // a <= b[]: false
    if (b.isArray()) {
      // Only one is an array.
      return false;
    }

    // Neither is an array.  Look at all of the superclasses of a.  If
    // one of those superclasses is b, then a is a subclass of b.
    for (Type t = a; t != null; t = superclass(t)) {
      if (t.equals(b)) {
	return true;
      }
    }

    return false;
  }

  /**
   * Returns (the <tt>Type</tt>s of) all of the classes and interfaces
   * in the hierarchy.  
   */
  public Collection classes() {
    Assert.isTrue(classes != null);
    return classes;
  }

  /**
   * Returns <tt>true</tt> if class closure has been computed
   */
  public boolean closure() {
    return(this.closure);
  }

  /**
   * Obtains a node from the extends graph.  If it is not in the
   * graph, we try to "bring it in". 
   */
  private TypeNode getExtendsNode(Type type) {
    GraphNode node = extendsGraph.getNode(type);

    if(node == null && type.isObject()) {
      this.addClassNamed(type.className());
    }

    return((TypeNode) extendsGraph.getNode(type));
  }

  /**
   * Obtains a node from the class graph.  If it is not in the graph,
   * we try to "bring it in".
   */
  private TypeNode getImplementsNode(Type type) {
    GraphNode node = implementsGraph.getNode(type);

    if(node == null && type.isObject()) {
      this.addClassNamed(type.className());
    }

    return((TypeNode) implementsGraph.getNode(type));
  }

  /**
   * Adds a type (and all types it references) to the extends and
   * implements graphs.
   */
  private void addClass(String name) {
   Type type = Type.getType(Type.classDescriptor(name));

    if (classes.contains(type)) {
      return;
    }

    if (inWorklist.contains(type)) {
      return;
    }

    db("ClassHierarchy: Adding " + name + " to hierarchy");

    worklist.add(type);
    inWorklist.add(type);

    while (! worklist.isEmpty()) {
      type = (Type) worklist.removeFirst();
      inWorklist.remove(type);

      if (classes.contains(type)) {
	continue;
      }

      classes.add(type);

      // Add a node in the extends graph for the type of interest
      TypeNode extendsNode = (TypeNode) getExtendsNode(type);

      if (extendsNode == null) {
	// Add a new node to the class graph
	extendsNode = new TypeNode(type);
	extendsGraph.addNode(type, extendsNode);
      }

//        TypeNode implementsNode = (TypeNode) getImplementsNode(type);

//        if (implementsNode == null) {
//  	// Add a new node to the interface graph
//  	implementsNode = new TypeNode(type);
//  	implementsGraph.addNode(type, implementsNode);
//      }

      // Obtain a ClassEditor for the class
      ClassEditor c;

      try {
	c = context.editClass(type.className());

      } catch (ClassNotFoundException ex) {
	if (RELAX) {
	  continue;
	}

	throw new RuntimeException("Class not found: " + 
				   ex.getMessage());
      }

      Type[] interfaces = c.interfaces();

      if (c.superclass() != null) {
	// Add an edge from the superclass to the class in the extends
	// graph.

	if(!c.isInterface() || interfaces.length == 0) {
	  // Ignore interfaces that implement (really extend, see
	  // below) other interfaces.  This way interfaces are put in
	  // the extends graph twice.

	  TypeNode superNode = (TypeNode)
	    getExtendsNode(c.superclass());

	  if (superNode == null) {
	    superNode = new TypeNode(c.superclass());
	    extendsGraph.addNode(c.superclass(), superNode);
	  }

	  // Make sure that we're not making java.lang.Object a
	  // superclass of itself.  We assume that the java.lang.Object
	  // has no successors in the extendsGraph.
	  if(!extendsNode.type.equals(Type.OBJECT)) {
	    extendsGraph.addEdge(extendsNode, superNode);
	  }
	}

      } else {
	// Only java.lang.Object has no superclass
	if (! type.equals(Type.OBJECT) && ! RELAX) {
	  throw new RuntimeException("Null superclass for " + type);
	}
      }

      // Consider the interfaces c implements
      if(c.isInterface()) {
	// Interfaces that extend other interfaces are compiled into
	// classes that implement those other interfaces.  So,
	// interfaces that implement other interfaces really extend
	// them.  Yes, this makes the extends graph an inverted tree.
	for(int i = 0; i < interfaces.length; i++) {
	  Type iType = interfaces[i];
	  TypeNode iNode = (TypeNode) getExtendsNode(iType);
	  if(iNode == null) {
	    iNode = new TypeNode(iType);
	    extendsGraph.addNode(iType, iNode);
	  }
	  extendsGraph.addEdge(extendsNode, iNode);
	}

      } else {
	// Class c implements its interfaces
	TypeNode implementsNode = null;
	if(interfaces.length > 0) {
	  implementsNode = getImplementsNode(type);
	  if(implementsNode == null) {
	    implementsNode = new TypeNode(type);
	    implementsGraph.addNode(type, implementsNode);
	  }
	}
	for(int i = 0; i < interfaces.length; i++) {
	  Type iType = interfaces[i];
	  TypeNode iNode = (TypeNode) getImplementsNode(iType);
	  if(iNode == null) {
	    iNode = new TypeNode(iType);
	    implementsGraph.addNode(iType, iNode);
	  }
	  implementsGraph.addEdge(implementsNode, iNode);
	}
      }

      if (c.superclass() != null) {
	// Add the super type to the worklist
	
//	db("typeref " + type + " -> " + c.superclass());

	addType(c.superclass());
      }

      for (int i = 0; i < c.interfaces().length; i++) {
	// Add all of the interface types to the worklist

//	db("typeref " + type + " -> " + c.interfaces()[i]);

	addType(c.interfaces()[i]);
      }

      if(!this.closure) {
	context.release(c.classInfo());
	continue;
      }

      for (int i = 0; i < c.methods().length; i++) {
	// TODO: Add an enumeration to ClassEditor to get this list.

	// Add all of the methods types.  Actually, we only add the
	// type involved with the methods.
	MethodInfo m = c.methods()[i];
	int typeIndex = m.typeIndex();

	String desc = (String) c.constants().constantAt(typeIndex);
	Type t = Type.getType(desc);

//	db("typeref " + type + " -> " + t);

	addType(t);
      }

      for (int i = 0; i < c.fields().length; i++) {
	// Add the types of all of the fields

	FieldInfo f = c.fields()[i];
	int typeIndex = f.typeIndex();

	String desc = (String) c.constants().constantAt(typeIndex);
	Type t = Type.getType(desc);

//	db("typeref " + type + " -> " + t);

	addType(t);
      }

      for (int i = 0; i < c.constants().numConstants(); i++) {
	// Look through the constant pool for interesting (non-LONG
	// and non-DOUBLE) constants and add them to the worklist.

	int tag = c.constants().constantTag(i);

	if (tag == Constant.LONG || tag == Constant.DOUBLE) {
	  i++;

	} else if (tag == Constant.CLASS) {
	  Type t = (Type) c.constants().constantAt(i);

//	  db("typeref " + type + " -> " + t);

	  addType(t);

	} else if (tag == Constant.NAME_AND_TYPE) {
	  NameAndType t = (NameAndType) c.constants().constantAt(i);

//	  db("typeref " + type + " -> " + t.type());

	  addType(t.type());
	}
      }

      // We're done editing the class
      context.release(c.classInfo());

    }

    /*
    // Now that we've entered the class (and at least all of its
    // subclasses) into the hierarchy, notify superclasses that
    // they've been subclassses.  This will invalidate the TypeNodes
    // in the dependence graph.
    DependenceGraph dg = BloatContext.getContext().dependenceGraph();

    for(Type superclass = superclass(type); superclass != null;
	superclass = superclass(superclass)) {
      db("ClassHierarchy: Invalidating superclass " + superclass);

      EDU.purdue.cs.bloat.depend.TypeNode typeNode = 
	dg.getTypeNode(superclass);
      typeNode.invalidate();
    }
    */
  }

  // Adds a Type to the worklist.  If the type is a method, then all
  // of the parameters types and the return types are added.
  private void addType(Type type) {
    if (type.isMethod()) {
      // Add all of the types of the parameters and the return type

      Type[] paramTypes = type.paramTypes();

      for (int i = 0; i < paramTypes.length; i++) {
//	db("typeref " + type + " -> " + paramTypes[i]);

	addType(paramTypes[i]);
      }

      Type returnType = type.returnType();

//      db("typeref " + type + " -> " + returnType);

      addType(returnType);

      return;
    }

    if (type.isArray()) {
      // TODO: Add the supertypes of the array and make it implement
      // SERIALIZABLE and CLONEABLE.  Since we're only concerned with
      // fields and since arrays have no fields, we can ignore this
      // for now.

//      db("typeref " + type + " -> " + type.elementType());

      addType(type.elementType());

      return;
    }

    if (! type.isObject()) {
      return;
    }

    if (classes.contains(type)) {
      return;
    }

    if (inWorklist.contains(type)) {
      return;
    }

    worklist.add(type);
    inWorklist.add(type);
  }

  /** 
   * Returns the intersection of two types.  Basically, the
   * interstion of two types is the type (if any) to which both types
   * may be assigned.  So, if a is a subtype of b, a is returned.
   * Otherwise, <tt>Type.NULL</tt> is returned.
   */
  public Type intersectType(Type a, Type b) {
    Assert.isTrue(a.isReference() && b.isReference(),
		  "Cannot intersect " + a + " and " + b);

    if (a.equals(b)) {
      return a;
    }

    if (a.isNull() || b.isNull()) {
      return Type.NULL;
    }

    if (a.equals(Type.OBJECT)) {
      return b;
    }

    if (b.equals(Type.OBJECT)) {
      return a;
    }

    if (a.isArray()) {
      if (b.isArray()) {
	// Both reference arrays.
	if (a.elementType().isReference() &&
	    b.elementType().isReference()) {

	  Type t = intersectType(a.elementType(), b.elementType());

	  if (t.isNull()) {
	    return Type.NULL;
	  }

	  return t.arrayType();
	}

	// Only one is a reference array.
	if (a.elementType().isReference() ||
	    b.elementType().isReference()) {
	  return Type.NULL;
	}

	// Both primitive arrays, not equal.
	return Type.NULL;
      }

      // Only one is an array.
      return Type.NULL;
    }

    if (b.isArray()) {
      // Only one is an array.
      return Type.NULL;
    }

    // Neither is an array.

    for (Type t = a; t != null; t = superclass(t)) {
      if (t.equals(b)) {
	// If a is a subtype of b, then return a.
	return a;
      }
    }

    for (Type t = b; t != null; t = superclass(t)) {
      if (t.equals(a)) {
	// If b is a subtype of a, then return b
	return b;
      }
    }

    return Type.NULL;
  }

  /**
   * Returns the most refined common supertype for a bunch of
   * <tt>Type</tt>s. 
   */
  public Type unionTypes(Collection types) {
    if(types.size() <= 0)
      return(Type.OBJECT);

    Iterator ts = types.iterator();
    Type type = (Type) ts.next();

    while(ts.hasNext()) {
      type = this.unionType(type, (Type) ts.next());
    }
    
    return(type);
  }

  /**
   * Returns the union of two types.  The union of two types is their
   * most refined common supertype.  At worst, the union is 
   * <tt>Type.OBJECT</tt>
   */
  public Type unionType(Type a, Type b) {
  
    if (a.equals(b)) {
      return a;
    }

    if (a.equals(Type.OBJECT) || b.equals(Type.OBJECT)) {
      return Type.OBJECT;
    }

    if (a.isNull()) {
      return b;
    }

    if (b.isNull()) {
      return a;
    }

    // Handle funky integral types introduced during type inferencing.
    if ((a.isIntegral() || a.equals(POS_BYTE) || a.equals(POS_SHORT)) && 
	(b.isIntegral() || b.equals(POS_BYTE) || b.equals(POS_SHORT))) {

      BitSet v1 = typeToSet(a);
      BitSet v2 = typeToSet(b);
      v1.or(v2);
      return(setToType(v1));
    }

    Assert.isTrue(a.isReference() && b.isReference(),
		  "Cannot union " + a + " and " + b);

    if (a.isArray()) {
      if (b.isArray()) {
	// Both reference arrays.
	if (a.elementType().isReference() &&
	    b.elementType().isReference()) {

	  Type t = unionType(a.elementType(), b.elementType());
	  return t.arrayType();
	}

	// Only one is a reference array.
	if (a.elementType().isReference() ||
	    b.elementType().isReference()) {
	  return Type.OBJECT;
	}

	// Both primitive arrays, not equal.
	return Type.OBJECT;
      }

      // Only one is an array.
      return Type.OBJECT;
    }

    if (b.isArray()) {
      // Only one is an array.
      return Type.OBJECT;
    }

    // Neither is an array.
    Set superOfA = new HashSet();
    Set superOfB = new HashSet();

    for (Type t = a; t != null; t = superclass(t)) {
//        if(TypeInference.DEBUG)
//  	System.out.println("  Superclass of " + a + " is " + t);
      superOfA.add(t);
    }

    for (Type t = b; t != null; t = superclass(t)) {
      if (superOfA.contains(t)) {
	return t;
      }

//        if(TypeInference.DEBUG)
//  	System.out.println("  Superclass of " + b + " is " + t);

      superOfB.add(t);
    }

//      if(TypeInference.DEBUG) {
//        System.out.println("Superclasses of A: " + superOfA);
//        System.out.println("Superclasses of B: " + superOfB);
//    }

    for (Type t = a; t != null; t = superclass(t)) {
      if (superOfB.contains(t)) {
	// Found a common superclass...
	return t;
      }
    }

    throw new RuntimeException("No common super type for " +
			       a + " (" + superOfA + ")" + " and " + 
			       b + " (" + superOfB + ")");
  }

  class TypeNode extends GraphNode {
    Type type;

    public TypeNode(Type type) {
//        if(DEBUG)
//  	System.out.println("Creating TypeNode for: " + type);
      this.type = type;
    }

    public String toString() {
      return("[" + type + "]");
    }
  }

  /**
   * Prints the class hierarchy (i.e. the "extends" hierarchy,
   * interfaces may extends other interfaces) to a
   * <tt>PrintWriter</tt>.
   */
  public void printClasses(PrintWriter out, int indent) {
    TypeNode objectNode = this.getExtendsNode(Type.OBJECT);
    indent(out, indent);
    out.println(objectNode.type);
    printSubclasses(objectNode.type, out, true, indent+2);
  }

  /**
   * Prints the implements hierarchy to a <tt>PrintWriter</tt>.
   */
  public void printImplements(PrintWriter out, int indent) {
    // There are multiple roots to the implements graph.
    indent += 2;
    Iterator roots = this.implementsGraph.roots().iterator();
    while(roots.hasNext()) {
      TypeNode iNode = (TypeNode) roots.next();
      indent(out, indent);
      out.println(iNode.type);
      printImplementors(iNode.type, out, true, indent+2);
    }
  }

  /**
   * Print the implementors of a given interface.  Do we even have
   * more than one level?
   */
  private void printImplementors(Type iType, PrintWriter out, boolean
				 recurse, int indent) {
    Iterator implementors = this.implementors(iType).iterator();
    while(implementors.hasNext()) {
      Type implementor = (Type) implementors.next();
      indent(out, indent);
      out.println(implementor);
      if(recurse)
	printImplementors(implementor, out, recurse, indent+2);
    }
  }

  /**
   * Prints a bunch of spaces to a PrintWriter.
   */
  private void indent(PrintWriter out, int indent) {
    for(int i = 0; i < indent; i++)
      out.print(" ");
  }

  /**
   * Prints the subclasses of a given to a PrintWriter.
   * 
   * @param recurse
   *        Are all subclasses printed or only direct ones?
   */
  private void printSubclasses(Type classType, PrintWriter out,
			       boolean recurse, int indent) {
    Iterator iter = this.subclasses(classType).iterator();
    while(iter.hasNext()) {
      Type subclass = (Type) iter.next();
      indent(out, indent);
      out.println(subclass);
      if(recurse)
	printSubclasses(subclass, out, recurse, indent+2);
      
    }
  }

  /**
   * Determines whether or not a class's method is overriden by any
   * of its subclasses. 
   */
  public boolean methodIsOverridden(Type classType, NameAndType nat) {
    String methodName = nat.name();
    Type methodType = nat.type();

    db("ClassHierarchy: Is " + classType + "." + methodName +
       methodType + " overridden?");
    
    Collection subclasses = this.subclasses(classType);

    Iterator iter = subclasses.iterator();
    while(iter.hasNext()) {
      Type subclass = (Type) iter.next();

      db("Examining subclass " + subclass);
            
      // Obtain a ClassEditor for the class
      ClassEditor ce = null;

      try {
	ce = context.editClass(subclass.className());

      } catch (ClassNotFoundException ex) {
	db(ex.getMessage());
	return(true);
      }

      // Examine each method in the subclass
      MethodInfo[] methods = ce.methods();
      for(int i = 0; i < methods.length; i++) {
	MethodEditor me = context.editMethod(methods[i]);
	if(me.name().equals(methodName) &&
	   me.type().equals(methodType)) {
	  db("  " + methodName + methodType + " is overridden by " +
	     me.name() + me.type());
	  context.release(ce.classInfo());
	  return(true);   // Method is overridden
	}
      }

      // Recurse over subclasses
      if(methodIsOverridden(subclass, nat)) {
	context.release(ce.classInfo());
	return(true);
      }

      context.release(ce.classInfo());
    }
    
    // Got through all subclasses and method was not overridden
    db("  NO!");

    return(false);
  }

  /**
   * Returns the <tt>MemberRef</tt> of the method that would be
   * invoked if a given method of a given type was invoked.
   * Basically, dynamic dispatch is simulated.
   */
  public MemberRef methodInvoked(Type receiver, NameAndType method) {
    // Search up class hierarchy for a class that implements the
    // method 
    for(Type type = receiver; type != null; type = superclass(type)) {
      // Construct a MemberRef for the possible method
      MemberRef m = new MemberRef(type, method);
      try {
	context.editMethod(m);
	return(m);

      } catch(NoSuchMethodException ex) {
	continue;     // Try superclass
      }
    }

    // Hmm. No superclass method was found!
    throw new IllegalArgumentException("No implementation of " +
				       receiver + "." + method);
  }

 /**
   * Given a set of bits representing the range of values some type
   * has, determines what that Type is.
   */
  public static Type setToType(BitSet v) {
    if (v.get(MAX_INT)) {
      return Type.INTEGER;
    }

    if (v.get(MAX_CHAR)) {
      if (v.get(MIN_INT) || v.get(MIN_SHORT) || v.get(MIN_BYTE)) {
	return Type.INTEGER;

      } else {
	return Type.CHARACTER;
      }
    }

    if (v.get(MAX_SHORT)) {
      if (v.get(MIN_INT)) {
	return Type.INTEGER;

      } else if (v.get(MIN_SHORT) || v.get(MIN_BYTE)) {
	return Type.SHORT;

      } else {
	return POS_SHORT;
      }
    }

    if (v.get(MAX_BYTE)) {
      if (v.get(MIN_INT)) {
	return Type.INTEGER;

      } else if (v.get(MIN_SHORT)) {
	return Type.SHORT;

      } else if (v.get(MIN_BYTE)) {
	return Type.BYTE;

      } else {
	return POS_BYTE;
      }
    }

    if (v.get(MAX_BOOL)) {
      if (v.get(MIN_INT)) {
	return Type.INTEGER;

      } else if (v.get(MIN_SHORT)) {
	return Type.SHORT;

      } else if (v.get(MIN_BYTE)) {
	return Type.BYTE;

      } else {
	return Type.BOOLEAN;
      }
    }

    if (v.get(MIN_INT)) {
      return Type.INTEGER;
    }

    if (v.get(MIN_SHORT)) {
      return Type.SHORT;
    }

    if (v.get(MIN_BYTE)) {
      return Type.BYTE;
    }

    return Type.BOOLEAN;
  }

  /**
   * Returns a BitSet representing the possible values of a given
   * integral type.
   */
  public static BitSet typeToSet(Type type) {
    BitSet v = new BitSet(MAX_INT);
    int lo;
    int hi;

    if (type.equals(Type.INTEGER)) {
      lo = MIN_INT;
      hi = MAX_INT;

    } else if (type.equals(Type.CHARACTER)) {
      lo = MIN_CHAR;
      hi = MAX_CHAR;

    } else if (type.equals(Type.SHORT)) {
      lo = MIN_SHORT;
      hi = MAX_SHORT;

    } else if (type.equals(POS_SHORT)) {
      lo = ZERO;
      hi = MAX_SHORT;

    } else if (type.equals(Type.BYTE)) {
      lo = MIN_BYTE;
      hi = MAX_BYTE;

    } else if (type.equals(POS_BYTE)) {
      lo = ZERO;
      hi = MAX_BYTE;

    } else if (type.equals(Type.BOOLEAN)) {
      lo = ZERO;
      hi = MAX_BOOL;

    } else {
      throw new RuntimeException();
    }

    for (int i = lo; i <= hi; i++) {
      v.set(i);
    }

    return v;
  }

  /**
   * Represents a method and a set of <tt>Type</tt>s.  When the method
   * is invoked on a receiver of any of these types, the method will
   * resolve to that method.
   */
  public class ResolvesToWith {
    /**
     * The method to which a call resolves
     */
    public MemberRef method;

    /**
     * The types with which the call resolves to the above method
     */
    public HashSet rTypes;
  }

  /**
   * Returns a set of <tt>ResolvesToWith</tt> that represent all
   * subclass methods that override a given method and the subclasses
   * that when used as receivers resolve to that method.
   *
   * @see ResolvesToWith
   */
  public Set resolvesToWith(MemberRef method) {
    Set resolvesTo = (Set) this.resolvesToCache.get(method);

    if(resolvesTo == null) {
      db("Resolving " + method);

      resolvesTo = new HashSet();    // All methods it could resolve to
      ResolvesToWith rtw = new ResolvesToWith();
      rtw.method = method;
      rtw.rTypes = new HashSet();

      // Remember that the method may be abstract, so the declaring
      // class is not necessarily a resolving type.

      // Basically, we go down the class and/or interface hierarchies
      // looking for concrete implementations of this method.
      MethodEditor me = null;
      try {
        me = context.editMethod(method);

      } catch(NoSuchMethodException ex1) {
        // A method may not necessarily be implemented by its
        // declaring class.  For instance, an abstract class that
        // implements an interface need not implement every method of
        // the interface.  Really?
        db("  Hmm. Method is not implemented in declaring class");
      }

      // If the method is static or is a constructor, then it can only
      // resolve to itself.
      if(me != null && (me.isStatic() || me.isConstructor())) {
        rtw.rTypes.add(method.declaringClass());
        resolvesTo.add(rtw);
        db("  Static method or constructor, resolves to itself");

      } else {
        // Now let's play with types.  Examine every type that could
        // implement this method.  If it does, add it to the resolvesTo
        // set.  Make sure to take things like interfaces into account.
        // When we find a overriding method, make a recursive call so
        // we'll have that information in the cache.
        List types = new LinkedList();
        types.add(method.declaringClass());
        while(!types.isEmpty()) {
          Type type = (Type) types.remove(0);

          db("  Examining type " + type);

          ClassEditor ce = null;
          try {
            ce = context.editClass(type);

          } catch(ClassNotFoundException ex1) {
            System.err.println("** Class not found: " + ex1.getMessage());
            ex1.printStackTrace(System.err);
            System.exit(1);
          }

          if(ce.isInterface()) {
            // Consider all subinterfaces of this interface and all
            // classes that implement this interface.
            Iterator subinterfaces = this.subclasses(type).iterator();
            while(subinterfaces.hasNext()) {
              Type subinterface = (Type) subinterfaces.next();
              types.add(subinterface);
              db("  Noting subinterface " + subinterface);
            }

            Iterator implementors = this.implementors(type).iterator();
            while(implementors.hasNext()) {
              Type implementor = (Type) implementors.next();
              types.add(implementor);
              db("  Noting implementor " + implementor);
            }

          } else {
            // We've got a class.  Does it override the method?
            NameAndType nat = method.nameAndType();
            MethodInfo[] methods = ce.methods();
            boolean overridden = false;
            for(int i = 0; i < methods.length; i++) {
    	      MethodEditor over = context.editMethod(methods[i]);
	      MemberRef ref = over.memberRef();
  	      if(ref.nameAndType().equals(nat)) {
	        // This class implements the method.  
                if(!method.declaringClass().equals(type)) {
                  // Make a recursive call. 
                  db("  Class " + type + " overrides " + method);
                  resolvesTo.addAll(resolvesToWith(ref));
                  overridden = true;
                } 
      	      }
            }

            if(!overridden) {
              db("  " + rtw.method + " called with " + type);
              rtw.rTypes.add(type);
              resolvesTo.add(rtw);

              // Examine all subclasses of this class.  They may override
              // the method also.
              Iterator subclasses = this.subclasses(type).iterator();
              while(subclasses.hasNext()) {
                Type subclass = (Type) subclasses.next();
                types.add(subclass);
                db("  Noting subclass " + subclass);
              }
            }
          }
        }
      }

      resolvesToCache.put(method, resolvesTo);
    }

    return(resolvesTo);
  }
}


