package decaf.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

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
		final TypeDeclaration enumType = builder().newTypeDeclaration(_enumNode.getName());		
		enumType.setSuperclassType(builder().newSimpleType("com.db4o.foundation.Enum4"));
		enumType.modifiers().addAll(_enumNode.modifiers());
		
		int ordinal = 0;
		for (Object item : _enumNode.enumConstants()) {
			EnumConstantDeclaration enumConstant = (EnumConstantDeclaration) item;			
			enumType.bodyDeclarations().add(newConcreteEnumClass(enumConstant, ordinal));
			ordinal++;
		}
		
		List<SimpleName> enumConstants = new ArrayList<SimpleName>();
		for (Object item : _enumNode.enumConstants()) {
			EnumConstantDeclaration enumConstant = (EnumConstantDeclaration) item;
			enumType.bodyDeclarations().add(newEnumConstant(_enumNode, enumConstant, enumConstants.size()));
			enumConstants.add(clone(enumConstant.getName()));
		}
		
		addEnumConstructor(enumType);
		addValuesMethod(enumType, enumConstants);
		return enumType;
	}	
	
	private TypeDeclaration newConcreteEnumClass(EnumConstantDeclaration enumConstant, int ordinal) {
		final TypeDeclaration enumConstantClass = createConcreteEnumClassDeclaration(enumConstant);
		emitOrdinalConstant(enumConstantClass, ordinal);
		emitConcreteEnumClassConstructor(enumConstantClass, enumConstant.getName().getIdentifier());		
		return enumConstantClass;		
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

	private void emitConcreteEnumClassConstructor(final TypeDeclaration enumConstantClass, String name) {
		final MethodDeclaration ctor = builder().newConstructorDeclaration(enumConstantClass.getName());

		final SuperConstructorInvocation superCtorInvocation = builder().newSuperConstructorInvocation();
		superCtorInvocation.arguments().add(builder().newStringLiteral(name));
		superCtorInvocation.arguments().add(builder().newFieldAccess(clone(enumConstantClass.getName()), ORDINAL_CONSTANT_NAME));
		
		ctor.setBody(builder().newBlock(superCtorInvocation));		
		addMethod(enumConstantClass, ctor);
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

	private void addEnumConstructor(TypeDeclaration enumType) {
		final MethodDeclaration ctor = builder().newConstructorDeclaration(enumType.getName());
		
		final ListRewrite ctorRewrite = rewrite().getListRewrite(ctor, MethodDeclaration.PARAMETERS_PROPERTY);
		ctorRewrite.insertLast(builder().newVariableDeclaration(newSimpleType("String"), "name", null), null);
		ctorRewrite.insertLast(builder().newVariableDeclaration(builder().newPrimitiveType(PrimitiveType.INT), "ordinal", null), null);

		final SuperConstructorInvocation superCtorInvocation = builder().newSuperConstructorInvocation();
		superCtorInvocation.arguments().add(builder().newSimpleName("name"));
		superCtorInvocation.arguments().add(builder().newSimpleName("ordinal"));
		
		ctor.setBody(builder().newBlock(superCtorInvocation));
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
		final Expression initializer = builder().newClassInstanceCreation(newEnumType(concreteEnumClassName(node.getName(), originalEnumConstant.getName())));
		return builder().newConstant(newEnumType(node.getName()), originalEnumConstant.getName().getIdentifier(), initializer);
	}

	private SimpleType newEnumType(SimpleName name) {
		return builder().newSimpleType(name.getIdentifier());
	}	
}
