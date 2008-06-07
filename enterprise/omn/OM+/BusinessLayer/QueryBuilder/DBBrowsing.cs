using System;
using System.Collections.Generic;
using System.Text;
using System.Data;
using OManager.DataLayer.Modal;
using OManager.BusinessLayer.QueryManager;
using OManager.BusinessLayer.Common;

namespace OManager.BusinessLayer.ObjectExplorer
{
    public class QueryHelper
    {
        string m_fieldtype;
        public QueryHelper(string fieldtype)
        {
            m_fieldtype = fieldtype;
        }
        //return the operator depending upon the datatype.
        public string[] GetConditions()
        {
            string[] operatorList = new string[] { string.Empty };

            switch (m_fieldtype)
            { 
                case Common.BusinessConstants.STRING:
                    operatorList = CommonValues.StringConditions;
                    break;
                case Common.BusinessConstants.CHAR:
                    operatorList = CommonValues.CharacterCondition;
                    break;
                case Common.BusinessConstants.INT16:
                case Common.BusinessConstants.DOUBLE:
                case Common.BusinessConstants.DECIMAL:
                case Common.BusinessConstants.INT32:
                case Common.BusinessConstants.INT64:
                case Common.BusinessConstants.INTPTR:
                case Common.BusinessConstants.UINT16:
                case Common.BusinessConstants.UINT32:
                case Common.BusinessConstants.UINT64:
                case Common.BusinessConstants.UINTPTR:
                case Common.BusinessConstants.SINGLE:
                case Common.BusinessConstants.SBYTE:
                case Common.BusinessConstants.BYTE:
                    operatorList = CommonValues.NumericConditions;
                    break;
                case Common.BusinessConstants.BOOLEAN:
                    operatorList = CommonValues.BooleanConditions;
                    break;
                case Common.BusinessConstants.DATETIME:
                    operatorList = CommonValues.DateTimeConditions;
                    break;
                default:
                    break;

            }

            return operatorList;
        }

        public static string[] GetOperators()
        {
            return CommonValues.Operators;
        }
    }
}
