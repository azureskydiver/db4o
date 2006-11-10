/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

namespace Db4objects.Db4odoc.Lists
{
    public class Pilot
    {
        private string _name;
        private int _points;

        public Pilot(string name, int points)
        {
            this._name = name;
            this._points = points;
        }

        override public string ToString()
        {
            return string.Format("{0}:{1})",_name,_points);
        }
    }
}
