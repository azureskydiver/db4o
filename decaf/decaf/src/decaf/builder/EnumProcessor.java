package decaf.builder;

import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import decaf.rewrite.DecafASTNodeBuilder;
import decaf.rewrite.DecafRewritingServices;

@SuppressWarnings("unchecked")
public class EnumProcessor {

	private final DecafRewritingContext _context;
	private final EnumDeclaration _enumNode;

	public EnumProcessor(DecafRewritingContext context, EnumDeclaration enumNode) {
		_context = context;
		_enumNode = enumNode;
	}

	public TypeDeclaration run() {
		final TypeDeclaration enumType = builder().newTypeDeclaration(_enumNode.getName());		
		enumType.setSuperclassType(builder().newSimpleType("Enum4"));
		enumType.modifiers().add(builder().newFinalModifier());
		
		int constantOrdinal = 0;
		for (Object item : _enumNode.enumConstants()) {
			EnumConstantDeclaration enumConstant = (EnumConstantDeclaration) item;
			
			final FieldDeclaration newEnumConstant = newEnumConstant(_enumNode, enumConstant, constantOrdinal);
			final ListRewrite rewrite = rewrite().getListRewrite(enumType, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
			rewrite.insertLast(newEnumConstant, null);
			
			constantOrdinal++;
		}
		
		addEnumConstructor(enumType);
		
		addValuesMethod(enumType);
		
		return enumType;
	}
	
	private DecafRewritingServices rewrite() {
		return _context.rewrite();
	}

	private DecafASTNodeBuilder builder() {
		return _context.builder();
	}

	private void addValuesMethod(TypeDeclaration enumType) {
		final MethodDeclaration valuesMethod = builder().newMethodDeclaration("values");
		valuesMethod.setReturnType2(builder().newArrayType(newSimpleType(enumType.getName().getIdentifier())));
		valuesMethod.modifiers().add(builder().newPublicModifier());
		valuesMethod.modifiers().add(builder().newStaticModifier());
		
		final ArrayInitializer valuesInitializer = builder().newArrayInitializer();
		
		//TODO: check if type is the same as enumType and (maybe) if field is static; 
		for (Object field : enumType.bodyDeclarations() ) {
			valuesInitializer.expressions().add(builder().newThisExpression() );
		}
		
		final ArrayCreation valuesArray = builder().newArrayCreation(newSimpleType(enumType.getName().getIdentifier()), valuesInitializer);
		final ReturnStatement returnStmt = builder().newReturnStatement(valuesArray);
		valuesMethod.setBody(builder().newBlock(returnStmt));
		
		addMethod(enumType, valuesMethod);
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
		final ClassInstanceCreation creation = builder().newClassInstanceCreation(newEnumType(node));
		
		creation.arguments().add(builder().newStringLiteral(originalEnumConstant.getName().getIdentifier()));
		creation.arguments().add(builder().newNumberLiteral(String.valueOf(ordinal)));
		
		final FieldDeclaration enumConstant = builder().newField(newEnumType(node) , originalEnumConstant.getName().getIdentifier(), creation);
		
		enumConstant.modifiers().add(builder().newPublicModifier());
		enumConstant.modifiers().add(builder().newStaticModifier());
		enumConstant.modifiers().add(builder().newFinalModifier());
		
		return enumConstant;
	}

	private SimpleType newEnumType(EnumDeclaration node) {
		return builder().newSimpleType(node.getName().getIdentifier());
	}	
}
