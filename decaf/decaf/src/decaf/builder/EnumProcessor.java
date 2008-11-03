package decaf.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import sharpen.core.framework.ByRef;
import decaf.rewrite.DecafASTNodeBuilder;
import decaf.rewrite.DecafRewritingServices;

@SuppressWarnings("unchecked")
public class EnumProcessor {

	public static final String ORDINAL_CONSTANT_NAME = "ORDINAL";
	private final DecafRewritingContext _context;
	private final EnumDeclaration _enumNode;

	public EnumProcessor(DecafRewritingContext context, EnumDeclaration enumNode) {
		_context = context;
		_enumNode = enumNode;
	}

	public TypeDeclaration run() {
		final TypeDeclaration newEnumType = newConcreteEnumClass();
		
		addEnumConstantClassesTo(newEnumType);		
		List<SimpleName> enumConstants = addEnumConstantFieldsTo(newEnumType);		
		addEnumConstructors(newEnumType);		
		addValuesMethod(newEnumType, enumConstants);
		copyEnumMembers(newEnumType, _enumNode);
		
		return newEnumType;
	}

	private void copyEnumMembers(final TypeDeclaration newEnumType, EnumDeclaration originalEnumType) {
		
		final ListRewrite newEnumRewriter = rewrite().getListRewrite(newEnumType, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		originalEnumType.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodDeclaration node) {
				if (!node.isConstructor()) {
					if (!(node.getParent() instanceof AnonymousClassDeclaration)) {					
						newEnumRewriter.insertLast(builder().clone(node), null);
					}					
				}
				return false;
			}
			
			@Override
			public boolean visit(FieldDeclaration node) {
				newEnumRewriter.insertLast(builder().clone(node), null);
				return false;
			}
		});
	}

	private List<SimpleName> addEnumConstantFieldsTo(final TypeDeclaration enumType) {
		List<SimpleName> enumConstants = new ArrayList<SimpleName>();
		for (Object item : _enumNode.enumConstants()) {
			EnumConstantDeclaration enumConstant = (EnumConstantDeclaration) item;
			enumType.bodyDeclarations().add(newEnumConstant(_enumNode, enumConstant, enumConstants.size()));
			enumConstants.add(clone(enumConstant.getName()));
		}
		return enumConstants;
	}

	private void addEnumConstantClassesTo(final TypeDeclaration enumType) {
		int ordinal = 0;
		final List<EnumConstantDeclaration> enumConstants = _enumNode.enumConstants();
		for (EnumConstantDeclaration enumConstant : enumConstants) {			 			
			enumType.bodyDeclarations().add(newConcreteEnumConstantClass(enumConstant, ordinal));
			ordinal++;
		}
	}

	private TypeDeclaration newConcreteEnumClass() {
		final TypeDeclaration enumType = builder().newTypeDeclaration(_enumNode.getName());		
		enumType.setSuperclassType(builder().newSimpleType("com.db4o.foundation.Enum4"));
		enumType.modifiers().addAll(_enumNode.modifiers());
		return enumType;
	}	
	
	private TypeDeclaration newConcreteEnumConstantClass(EnumConstantDeclaration enumConstant, int ordinal) {
		final TypeDeclaration enumConstantClass = createConcreteEnumClassDeclaration(enumConstant);
		emitOrdinalConstant(enumConstantClass, ordinal);
		
		emitConcreteEnumClassConstructor(enumConstantClass, enumConstant);
		
		copyMethods(enumConstantClass, enumConstant);
		
		return enumConstantClass;		
	}

	private void copyMethods(TypeDeclaration enumConstantClass, EnumConstantDeclaration enumConstant) {
		final ListRewrite enumConstantClassRewriter = rewrite().getListRewrite(enumConstantClass, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		enumConstant.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodDeclaration node) {
				enumConstantClassRewriter.insertLast(builder().clone(node), null);
				return false;
			}
		});		
	}

	private TypeDeclaration createConcreteEnumClassDeclaration(EnumConstantDeclaration enumConstant) {
		SimpleName enumName = _enumNode.getName();
		final TypeDeclaration enumConstantClass = builder().newTypeDeclaration(concreteEnumClassName(enumName, enumConstant.getName()));
		enumConstantClass.modifiers().add(builder().newPublicModifier());
		enumConstantClass.modifiers().add(builder().newStaticModifier());
		enumConstantClass.setSuperclassType(newSimpleType(enumName.getIdentifier()));
		
		return enumConstantClass;
	}

	private SimpleName concreteEnumClassName(SimpleName enumName, SimpleName constantName) {
		return builder().newSimpleName(concreteEnumClassName(enumName.getIdentifier(), constantName.getIdentifier()));
	}
	
	public static String concreteEnumClassName(String enumName, String constantName) {
		return constantName + enumName;
	}

	private void emitConcreteEnumClassConstructor(final TypeDeclaration enumConstantClass, EnumConstantDeclaration enumConstant) {
		final MethodDeclaration ctor = builder().newConstructorDeclaration(enumConstantClass.getName());
		
		addRequiredConstructorParameters(ctor, enumConstant);		

		ctor.setBody(builder().newBlock(newSuperConstructorInvocation(enumConstantClass, enumConstant)));
		
		addMethod(enumConstantClass, ctor);
	}

	private SuperConstructorInvocation newSuperConstructorInvocation(final TypeDeclaration newEnumConstantClass,EnumConstantDeclaration originalEnumConstant) {		
		final SuperConstructorInvocation superCtorInvocation = builder().newSuperConstructorInvocation();
		superCtorInvocation.arguments().add(builder().newStringLiteral(originalEnumConstant.getName().getIdentifier()));
		superCtorInvocation.arguments().add(builder().newFieldAccess(clone(newEnumConstantClass.getName()), ORDINAL_CONSTANT_NAME));
		
		for (int paramIndex = 0; paramIndex < originalEnumConstant.arguments().size(); paramIndex++) {
			superCtorInvocation.arguments().add(builder().newSimpleName(paramNameForIndex(paramIndex)));
		}
		return superCtorInvocation;
	}

	private List<Expression> addRequiredConstructorParameters(final MethodDeclaration ctor, EnumConstantDeclaration enumConstant) {
		int paramIndex = 0;
		final List<Expression> enumConstantArgs = enumConstant.arguments();
		for (Expression enumConstantArg : enumConstantArgs) {
			final ITypeBinding constantArgTypeBinding = enumConstantArg.resolveTypeBinding();
			ctor.parameters().add(
					builder().newSingleVariableDeclaration(							
							paramNameForIndex(paramIndex),
							builder().newType(constantArgTypeBinding),
							null));
			paramIndex++;
		}
		return enumConstantArgs;
	}

	private String paramNameForIndex(int index) {
		return "param_" + index;
	}

	private void emitOrdinalConstant(final TypeDeclaration enumConstantClass, int ordinal) {
		final FieldDeclaration ordinalDeclaration = builder().newConstant(builder().newPrimitiveType(PrimitiveType.INT), ORDINAL_CONSTANT_NAME, builder().newNumberLiteral(String.valueOf(ordinal)));
		enumConstantClass.bodyDeclarations().add(ordinalDeclaration);
	}

	private DecafRewritingServices rewrite() {
		return _context.rewrite();
	}

	private DecafASTNodeBuilder builder() {
		return _context.builder();
	}

	private void addValuesMethod(TypeDeclaration enumType, List<SimpleName> enumConstants) {
		final MethodDeclaration valuesMethod = newMethod(
													enumType, 
													"values",
													builder().newArrayType(newSimpleType(enumType.getName().getIdentifier())),
													builder().newPublicModifier(), 
													builder().newStaticModifier());
		
		final ArrayInitializer valuesInitializer = builder().newArrayInitializer();
		
		for (SimpleName enumConstant : enumConstants) {
			valuesInitializer.expressions().add(builder().newFieldAccess(clone(enumType.getName()), builder().clone(enumConstant)));
		}		
		
		final ArrayCreation valuesArray = builder().newArrayCreation(newSimpleType(enumType.getName().getIdentifier()), valuesInitializer);
		final ReturnStatement returnStmt = builder().newReturnStatement(valuesArray);
		valuesMethod.setBody(builder().newBlock(returnStmt));
		
		addMethod(enumType, valuesMethod);
	}

	private <T extends ASTNode> T clone(T node) {
		return builder().clone(node);
	}

	private MethodDeclaration newMethod(TypeDeclaration type, final String name, Type returnType, final Modifier... modifiers) {
		final MethodDeclaration method = builder().newMethodDeclaration(name);
		method.setReturnType2(returnType);
		
		for(Modifier modifier : modifiers) {
			method.modifiers().add(modifier);
		}
		
		return method;
	}

	private void addEnumConstructors(final TypeDeclaration enumType) {
		final ByRef<Boolean> constructorFound = new ByRef<Boolean>(false);
		
		_enumNode.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodDeclaration node) {
				if (!node.isConstructor()) {
					return false;
				}
				
				constructorFound.value = true;
				
				addEnumConstructor(enumType, node);
				return false;
			}
		});
		
		if (!constructorFound.value) {
			addEnumConstructor(enumType);
		}
	}
	
	private void addEnumConstructor(TypeDeclaration enumType) {
		addEnumConstructor(enumType, null);		
	}

	private void addEnumConstructor(TypeDeclaration enumType, MethodDeclaration originalEnumCtor) {
		final MethodDeclaration ctor = builder().newConstructorDeclaration(enumType.getName());
		
		final ListRewrite ctorRewrite = rewrite().getListRewrite(ctor, MethodDeclaration.PARAMETERS_PROPERTY);
		ctorRewrite.insertLast(builder().newVariableDeclaration(newSimpleType("String"), "name", null), null);
		ctorRewrite.insertLast(builder().newVariableDeclaration(builder().newPrimitiveType(PrimitiveType.INT), "ordinal", null), null);
		
		final List<SingleVariableDeclaration> parameters = originalEnumCtor != null ? originalEnumCtor.parameters() : new ArrayList<SingleVariableDeclaration>();
		for (SingleVariableDeclaration parameter : parameters) {
			ctorRewrite.insertLast(clone(parameter), null);
		}

		final SuperConstructorInvocation superCtorInvocation = builder().newSuperConstructorInvocation();
		superCtorInvocation.arguments().add(builder().newSimpleName("name"));
		superCtorInvocation.arguments().add(builder().newSimpleName("ordinal"));
		
		
		final List<Statement> ctorStatements = new ArrayList<Statement>();
		ctorStatements.add(superCtorInvocation);
		
		final List<Statement> originalCtorStatements = originalEnumCtor != null ? originalEnumCtor.getBody().statements() : new ArrayList<Statement>();
		for (Statement statement : originalCtorStatements) {
			ctorStatements.add(clone(statement));
		}
		
		ctor.setBody(builder().newBlock(ctorStatements.toArray(new Statement[ctorStatements.size()])));
		addMethod(enumType, ctor);		
	}
	
	private void addMethod(TypeDeclaration type, final MethodDeclaration method) {
		final ListRewrite rewrite = rewrite().getListRewrite(type, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		rewrite.insertLast(method, null);
	}

	private Type newSimpleType(String name) {
		return builder().newSimpleType(name);
	}

	private FieldDeclaration newEnumConstant(EnumDeclaration node, EnumConstantDeclaration originalEnumConstant, int ordinal) {
		final ClassInstanceCreation initializer = builder().newClassInstanceCreation(newEnumType(concreteEnumClassName(node.getName(), originalEnumConstant.getName())));
		final List<Expression> arguments = originalEnumConstant.arguments();
		for (Expression arg : arguments) {
			initializer.arguments().add(clone(arg));
		}
		return builder().newConstant(newEnumType(node.getName()), originalEnumConstant.getName().getIdentifier(), initializer);
	}

	private SimpleType newEnumType(SimpleName name) {
		return builder().newSimpleType(name.getIdentifier());
	}	
}
