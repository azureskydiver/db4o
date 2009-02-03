using System;
using System.Collections.Generic;
using System.Text;
using OManager.BusinessLayer.QueryManager;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using OManager.DataLayer.Connection;
using OManager.DataLayer.Modal;
using OManager.BusinessLayer.Common;
using OManager.DataLayer.CommonDatalayer;
using OME.Logging.Common;
using OME.Logging.Tracing;


namespace OManager.DataLayer.QueryParser
{
    // change name to QueryParser
    public class QueryParser
    {
        

        
        //OMQueryClause qmclause = new OMQueryClause();
        IObjectContainer objectContainer;
        OMQuery m_OmQuery;


        public QueryParser(OMQuery OmQuery)
        {
            objectContainer = Db4oClient.Client;
            this.m_OmQuery = OmQuery;
        }
        
        public IObjectSet ExecuteOMQueryList()
        {
            try
            {
                
                IConstraint ConCatClauses = null;
                IConstraint buildClause = null;
                IConstraint buildGroup = null;
                IConstraint conCatGroup = null; 

                objectContainer = Db4oClient.Client;
                IQuery query = objectContainer.Query();

                //ToCheckQueryDirectly();

                FormulateRootConstraints(query, m_OmQuery.BaseClass);
                int Groupcount = 0;
                foreach (OMQueryGroup qmGroup in m_OmQuery.ListQueryGroup)
                {

                    int clausecount = 0;
                    Groupcount++;
                    buildClause = null;
                    foreach (OMQueryClause qmclause in qmGroup.ListQueryClauses)
                    {
                        clausecount++;
                        buildClause = FormulateFieldConstraints(query, qmclause);//.Classname, qmclause.Fieldname, qmclause.Value);
                        if (qmclause.Operator != null)
                        {
                            if (qmclause.FieldType !=  BusinessConstants.DATETIME)
                                 buildClause = ApplyOperator(buildClause, qmclause.Operator);
                        }

                        if (qmclause.ClauseLogicalOperator == CommonValues.LogicalOperators.OR)
                        {
                            if (buildClause != null)
                            {
                                if (clausecount == 1)
                                {
                                    ConCatClauses = buildClause;
                                }
                                if (clausecount > 1)
                                {
                                    ConCatClauses = buildClause.Or(ConCatClauses);
                                }

                            }
                        }
                        if (qmclause.ClauseLogicalOperator == CommonValues.LogicalOperators.AND)
                        {
                            if (buildClause != null)
                            {
                                if (clausecount == 1)
                                {
                                    ConCatClauses = buildClause;
                                }
                                if (clausecount > 1)
                                {
                                    ConCatClauses = buildClause.And(ConCatClauses);
                                }
                            }
                        }           
                    }

                    if (ConCatClauses != null)
                    {
                        buildGroup = ConCatClauses;
                    }
                    else
                    {
                        buildGroup = buildClause;

                    }


                    if (qmGroup.GroupLogicalOperator != CommonValues.LogicalOperators.EMPTY)
                    {
                        if (qmGroup.GroupLogicalOperator == CommonValues.LogicalOperators.OR)
                        {

                            if (buildGroup != null)
                            {
                                conCatGroup = conCatGroup.Or(buildGroup);
                            }
                        }
                        if (qmGroup.GroupLogicalOperator == CommonValues.LogicalOperators.AND)
                        {

                            if (buildGroup != null)
                            {
                                conCatGroup = conCatGroup.And(buildGroup);
                            }
                        }
                    }
                    else
                    {
                        conCatGroup = buildGroup;
                    }                   

                }
                IObjectSet objSet= query.Execute();
                return objSet;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }

        public void ToCheckQueryDirectly()
        {
            //Common.LogicalOperators groupOperator = Common.LogicalOperators.EMPTY;

            //IQuery query1 = objectContainer.Query();
            //IConstraint cn = query1.Constrain(clsDetails.GetType(OMQuery.BaseClass));
            //IConstraint cn1 = query1.Descend("_pilot").Descend("_points").Constrain(0).Greater();
            //IConstraint cn2 = query1.Descend("_pilot").Descend("dbl").Constrain(0).Greater();
            //// IConstraint groupcn = cn1.And(cn2);
            //// IConstraint cn2= query.Descend("_pilot").Descend("_name").Constrain("Vidisha")   ;
            //IConstraint cn3 = query1.Descend("_pilot").Descend("single").Constrain(1).Greater();
            //// IConstraint groupcn1 = cn3;
            ////IConstraint cnx = cn2.And(cn3);
            ////groupcn.And(groupcn1);  


            //IObjectSet objSet1 = query1.Execute();

            //objectContainer = Db4oClient.Client;
            //IQuery query = objectContainer.Query(); 
        }
        
        public IConstraint ApplyOperator(IConstraint cons, string db4oOperator)
        {
            try
            {
                switch (db4oOperator)
                {
                    case BusinessConstants.CONDITION_STARTSWITH: 
                        cons.StartsWith(false);
                        break;
                    case BusinessConstants.CONDITION_ENDSWITH:
                        cons.EndsWith(false);
                        break;
                    case BusinessConstants.CONDITION_EQUALS:
                        cons.Equal();
                        break;
                    case BusinessConstants.CONDITION_NOTEQUALS:
                        cons.Not();
                        break;
                    case BusinessConstants.CONDITION_GREATERTHAN :
                        cons.Greater();
                        break;
                    case BusinessConstants.CONDITION_GREATERTHANEQUAL:
                        cons.Greater().Equal();
                        break;
                    case BusinessConstants.CONDITION_LESSTHAN :
                        cons.Smaller();
                        break;
                    case BusinessConstants.CONDITION_LESSTHANEQUAL:
                        cons.Smaller().Equal();
                        break;
                    case BusinessConstants.CONDITION_CONTAINS:
                        cons.Like();
                        break;
                }
                return cons;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }


        //public IConstraint ApplyOperatorForDate(IConstraint cons, string db4oOperator)
        //{
        //    try
        //    {
                 
        //        switch (db4oOperator)
        //        {
                       
        //            //case BusinessConstants.CONDITION_STARTSWITH:
        //            //    cons.StartsWith(false);
        //            //    break;
        //            //case BusinessConstants.CONDITION_ENDSWITH:
        //            //    cons.EndsWith(false);
        //            //    break;
        //            case BusinessConstants.CONDITION_EQUALS:
        //                cons.Equal();
        //                break;
        //            case BusinessConstants.CONDITION_NOTEQUALS:
        //                cons.Like().Not();
        //                break;
        //            case BusinessConstants.CONDITION_GREATERTHAN:
        //                cons.Greater();
        //                break;
        //            case BusinessConstants.CONDITION_GREATERTHANEQUAL:
        //                cons.Greater().Equal();
        //                break;
        //            case BusinessConstants.CONDITION_LESSTHAN:
        //                cons.Smaller();
        //                break;
        //            case BusinessConstants.CONDITION_LESSTHANEQUAL:
        //                cons.Smaller().Equal();
        //                break;
        //            //case BusinessConstants.CONDITION_CONTAINS:
        //            //    cons.Like();
        //            //    break;
        //        }
        //        return cons;
        //    }
        //    catch (Exception oEx)
        //    {
        //        LoggingHelper.HandleException(oEx);
        //        return null;
        //    }
        //}

        public void FormulateRootConstraints(IQuery query, string classname)
        {
            try
            {
                query.Constrain(DataLayerCommon.ReturnReflectClass(classname));
                
                
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                
            }
        }

//        public IConstraint FormulateFieldConstraints(IQuery query, string classname, string fieldname, string value)
        public IConstraint FormulateFieldConstraints(IQuery query, OMQueryClause clause)
        {            
            try
            {
                IConstraint cons = null;
                string[] str = clause.Fieldname.Split('.');
                IQuery q = AddAsDescends(query, str);
                //classname = DataLayerCommon.RemoveGFromClassName(classname);
                //string valueType = ConvertToValue(classname, str[str.Length - 1]);              

                switch (clause.FieldType)
                {
                    case BusinessConstants.INT16:
                        cons = q.Constrain(Convert.ToInt32(clause.Value));
                        break;
                    case BusinessConstants.INT32:
                        cons = q.Constrain(Convert.ToInt32(clause.Value));
                        break;
                    case BusinessConstants.INT64:
                        cons = q.Constrain(Convert.ToInt64(clause.Value));
                        break;
                    case BusinessConstants.SINGLE:
                        cons = q.Constrain(Convert.ToSingle(clause.Value));
                        break;
                    case BusinessConstants.DOUBLE:
                        cons = q.Constrain(Convert.ToDouble(clause.Value));
                        break;
                    case BusinessConstants.DECIMAL:
                        cons = q.Constrain(Convert.ToDecimal(clause.Value));
                        break;
                    case BusinessConstants.CHAR:
                        cons = q.Constrain(Convert.ToChar(clause.Value));
                        break;
                    case BusinessConstants.BYTE:
                        cons = q.Constrain(Convert.ToByte(clause.Value));
                        break;
                    case BusinessConstants.DATETIME:
                        {
                            IConstraint c1=null, c2=null;
                            DateTime dt = Convert.ToDateTime(clause.Value);
                            DateTime dt1 = dt.AddDays(-1);
                            DateTime dt2 = dt.AddDays(1);
                            if (clause.Operator.Equals(BusinessConstants.CONDITION_EQUALS))
                                cons = q.Constrain(dt2).Smaller().And(q.Constrain(dt1).Greater());
                           
                            else if (clause.Operator.Equals(BusinessConstants.CONDITION_GREATERTHAN))
                            {
                                c1 = q.Constrain(dt2).Greater();
                                c2=q.Constrain(dt2.AddDays(1)).Smaller().And(q.Constrain(dt).Greater());
                                cons =c1.Or(c2);
                                c1 = null;
                                c2 = null;
                            }
                            else if (clause.Operator.Equals(BusinessConstants.CONDITION_LESSTHAN))
                            {
                                c1 = q.Constrain(dt1).Smaller();
                                c2 = q.Constrain(dt).Smaller().And(q.Constrain(dt1.AddDays(-1)).Greater());
                                cons = c1.Or(c2);
                            }
                                break;
                        }
                    case BusinessConstants.BOOLEAN:
                        cons = q.Constrain(Convert.ToBoolean(clause.Value));
                        break;
                    case BusinessConstants.SBYTE:
                        cons = q.Constrain(Convert.ToSByte(clause.Value));
                        break;
                    case BusinessConstants.UINT16:
                        cons = q.Constrain(Convert.ToUInt32(clause.Value));
                        break;
                    case BusinessConstants.UINT32:
                        cons = q.Constrain(Convert.ToUInt32(clause.Value));
                        break;
                    case BusinessConstants.UINT64:
                        cons = q.Constrain(Convert.ToUInt64(clause.Value));
                        break;
                    default:
                        cons = q.Constrain(clause.Value);
                        break;
                }

                return cons;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }

        }

        public string ConvertToValue(string clauseClass, string clausefield)
        {
            try
            {

                FieldDetails fDetails = new FieldDetails(clauseClass, clausefield);
                return fDetails.GetDataType();
                
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }

        }
        public IQuery AddAsDescends(IQuery query, string[] str)
        {
            try
            {
                IQuery result = query;
                int count = 1;                
                while (str.Length > 0 && count <= str.Length - 1)
                {
                    result = result.Descend(str[count]);
                    count++;
                }
                return result;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }

        }
    }

}
