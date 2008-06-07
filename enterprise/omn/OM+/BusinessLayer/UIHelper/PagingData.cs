using System;
using System.Collections.Generic;
using System.Text;

namespace OManager.BusinessLayer.pagingData
{
    public class PagingData
    {
        public PagingData(int startIndex, int endIndex)
        {
            m_startIndex = startIndex;
            m_endIndex =endIndex;
        }
        int m_startIndex;

        public int StartIndex
        {
            get { return m_startIndex; }
            set { m_startIndex = value; }
        }
        int m_endIndex;

        public int EndIndex
        {
            get { return m_endIndex; }
            set { m_endIndex = value; }
        }
        IList<long> m_objectId;

        public IList<long> ObjectId
        {
            get { return m_objectId; }
            set { m_objectId = value; }
        }

        public int GetPageCount()
        {
            int defaultPageSize = 50;
            double pageCount = 0;
            int objectCount = m_objectId.Count;
            pageCount = (double)objectCount / (double)defaultPageSize;

            if (pageCount <= 0)
                pageCount = 1;

            return (int)Math.Ceiling(pageCount);
        }

    }
}
