abstract class Expression(ICodeElement):
	pass
	
abstract class Statement(ICodeElement):
	pass
	
[collection(IExpression)]
class ExpressionCollection:
	pass
	
[collection(IStatement)]
class StatementCollection:
	pass
	
class MethodInvocationExpression(IExpression):
	Target as Expression
	Arguments as ExpressionCollection
	
class MethodReferenceExpression(IExpression):
	Target as Expression
	Method as IMethodReference

class LiteralExpression(IExpression):
	Value as object
	
class UnaryExpression(IExpression):
	Operator as UnaryOperator
	Operand as Expression
	
class BinaryExpression(IExpression):
	Operator as BinaryOperator
	Left as Expression
	Right as Expression
	
class AssignExpression(IExpression):
	Target as Expression
	Expression as Expression
	
class ArgumentReferenceExpression(IExpression):
	Parameter as IParameterReference
	
class VariableReferenceExpression(IExpression):
	Variable as IVariableReference
	
class ThisReferenceExpression(IExpression):
	pass

class FieldReferenceExpression(IExpression):
	Target as Expression
	Field as IFieldReference

class PropertyReferenceExpression(IExpression):
	Target as Expression
	Property as IPropertyReference

class BlockStatement(IStatement):
	Statements as StatementCollection

class ReturnStatement(IStatement):
	Expression as Expression
