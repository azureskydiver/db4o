using System;
using System.Collections.Generic;
using System.Text;

namespace OManager.DataLayer.QueryParser
{
    public class Node
    {
        string node_name;

      
        string node_type;
        Node Parent;
        Node[] children;

        public string Name
        {
            get { return node_name; }
            set { node_name = value; }
        }

        public void HasChildren(Node node)
        { }


    }
}
