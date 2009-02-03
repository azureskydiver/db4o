using Sharpen.Lang;

namespace OManager.BusinessLayer.Common
{
    public static class CommonValues
    {
        public enum PrimitiveTypes
        {
            
            STRING=1,
            FLOAT,
            LONG,
            DOUBLE,
            BOOL,
            SINGLE
        }

        public enum LogicalOperators
        {   
            EMPTY=1,
            AND ,
            OR                   
        }

        public static string[] StringConditions = new string[]{
                                                BusinessConstants.CONDITION_CONTAINS,
                                                BusinessConstants.CONDITION_STARTSWITH,
                                                BusinessConstants.CONDITION_ENDSWITH,
                                                BusinessConstants.CONDITION_EQUALS,
                                                BusinessConstants.CONDITION_NOTEQUALS
                                                };

        public static string[] NumericConditions = new string[] {
                                                BusinessConstants.CONDITION_GREATERTHAN,
                                                BusinessConstants.CONDITION_GREATERTHANEQUAL,
                                                BusinessConstants.CONDITION_LESSTHAN,
                                                BusinessConstants.CONDITION_LESSTHANEQUAL,
                                                BusinessConstants.CONDITION_EQUALS,
                                                BusinessConstants.CONDITION_NOTEQUALS
                                                };       

        public static string[] BooleanConditions = new string[] {
                                                BusinessConstants.CONDITION_EQUALS,
                                                BusinessConstants.CONDITION_NOTEQUALS
                                                };

        public static string[] DateTimeConditions = new string[] {
                                                BusinessConstants.CONDITION_GREATERTHAN,
                                                BusinessConstants.CONDITION_LESSTHAN,
                                                BusinessConstants.CONDITION_EQUALS  
                                                };
        public static string[] CharacterCondition = new string[] {
                                                BusinessConstants.CONDITION_EQUALS,
                                                BusinessConstants.CONDITION_NOTEQUALS
                                                };


        public static string[] Operators = new string[] {
                                                BusinessConstants.OPERATOR_AND,
                                                BusinessConstants.OPERATOR_OR
                                                };
        public static char[] charArray = new char[] { 'G', 'e', 'n', 'e', 'r', 'i', 'c', 'C', 'l', 'a', 's', 's', ' ' };

        public static bool IsPrimitive(string type)
        {
            bool isPrimitive;

            switch (type)
            {
                case BusinessConstants.STRING:
                case BusinessConstants.SINGLE:
                case BusinessConstants.DATETIME:
                case BusinessConstants.BYTE:
                case BusinessConstants.CHAR:
                case BusinessConstants.BOOLEAN:
                case BusinessConstants.DECIMAL:
                case BusinessConstants.DOUBLE:
                case BusinessConstants.INT16:
                case BusinessConstants.INT32:
                case BusinessConstants.INT64:
                case BusinessConstants.INTPTR:
                case BusinessConstants.SBYTE:
                case BusinessConstants.UINT16:
                case BusinessConstants.UINT32:
                case BusinessConstants.UINT64:
                case BusinessConstants.UINTPTR:
                case "":
                    isPrimitive = true;
                    break;
                default:
                    isPrimitive = false;
                    break;
            }

            return isPrimitive;
        }

        public static bool IsDateTimeOrString(string type)
        {
            bool isDateTimeOrString;

            switch (type)
            {
                case BusinessConstants.SINGLE:
                case BusinessConstants.BYTE:
                case BusinessConstants.CHAR:
                case BusinessConstants.BOOLEAN:
                case BusinessConstants.DECIMAL:
                case BusinessConstants.DOUBLE:
                case BusinessConstants.INT16:
                case BusinessConstants.INT32:
                case BusinessConstants.INT64:
                case BusinessConstants.INTPTR:
                case BusinessConstants.SBYTE:
                case BusinessConstants.UINT16:
                case BusinessConstants.UINT32:
                case BusinessConstants.UINT64:
                case BusinessConstants.UINTPTR:
                    isDateTimeOrString = false;
                    break;
                case BusinessConstants.DATETIME :
                case BusinessConstants.STRING: 
                    isDateTimeOrString = true;
                    break;
                default:
                    isDateTimeOrString = false;
                    break;
            }

            return isDateTimeOrString;
        }

    	//TODO: Remove
		public static bool IsNullable(string typeName)
    	{
    		return typeName.StartsWith("System.Nullable") || typeName.StartsWith("Nullable<");
    	}

    	public static string DecorateNullableName(string nullableTypeName)
    	{
			GenericTypeReference typeRef = (GenericTypeReference) TypeReference.FromString(nullableTypeName);
    		TypeReference wrappedType = typeRef.GenericArguments[0];
    		
			return "Nullable<" + wrappedType.SimpleName + ">";
    	}

    	public static string UndecorateFieldName(string fieldName)
    	{
    		int index = fieldName.IndexOf('(');
    		return index >= 0 ? fieldName.Remove(index - 1, fieldName.Length - index + 1) : fieldName;
    	}
    }
}
