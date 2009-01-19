using System;
using System.Collections.Generic;
using Db4objects.Db4o;
using System.Collections;
using OManager.BusinessLayer.QueryManager;
using OManager.BusinessLayer.UIHelper;
using OME.Logging.Common;

namespace OManager.DataLayer.QueryParser
{
    public class RunQuery
    {
        private OMQuery m_omQuery;

        public OMQuery OmQuery
        {
            get { return m_omQuery; }
            set { m_omQuery = value; }
        }        
        private List<Hashtable> m_hashResults;

        public List<Hashtable> HashResults
        {
            get { return m_hashResults; }
            set { m_hashResults = value; }
        }
        private Hashtable m_hashPerRow;

        public Hashtable HashPerRow
        {
            get { return m_hashPerRow; }
            set { m_hashPerRow = value; }
        }
        private int m_startIndex;

        public int StartIndex
        {
            get { return m_startIndex; }
            set { m_startIndex = value; }
        }
        private int m_endIndex;

        public int EndIndex
        {
            get { return m_endIndex; }
            set { m_endIndex = value; }
        }

        public RunQuery(OMQuery omQuery)
        {            
            m_omQuery = omQuery;
        }

        public long[] ExecuteQuery()
        {
            QueryParser qParser = new QueryParser(m_omQuery);
            IObjectSet objSet = qParser.ExecuteOMQueryList();
            if (objSet != null)
                return objSet.Ext().GetIDs();

            return null;
            //return this;
        }

        public List<Hashtable> ReturnResults(PagingData pgData, bool refresh,string baseclass,Hashtable AttributeList)
        {
            try
            {
                IobjectsetConverter objSetConvertor = new IobjectsetConverter(baseclass, refresh);

                if (pgData.ObjectId.Count > 0)
                {
                    m_hashResults = objSetConvertor.convertObjectIDToUIObjects(pgData, AttributeList);
                    return m_hashResults;
                    //return objSetConvertor.convertObjectSetToUIObjects(objSet);
                }


                return null;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }
        public Hashtable ReturnResultsforEachRow(object obj, bool refresh,Hashtable attributeList)
        {
            try
            {
                Hashtable hash = new Hashtable();
                IobjectsetConverter objSetConvertor = new IobjectsetConverter(m_omQuery.BaseClass, refresh);
                if (obj != null)
                {
                    m_hashPerRow = objSetConvertor.resultsWithAttributes(obj, attributeList);
                }

                return m_hashPerRow;
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return null;
            }
        }
    }
}
