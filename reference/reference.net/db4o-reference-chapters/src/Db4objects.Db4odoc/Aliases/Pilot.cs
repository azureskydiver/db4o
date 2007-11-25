/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Aliases
{
    class Pilot
    {
        private string name;
        private int points;

        public Pilot(string name, int points)
        {
            this.name = name;
            this.points = points;
        }

        public string Name
        {
            get
            {
                return name;
            }
            set
            {
                name = value;
            }
        }

        public override string ToString()
        {
            return string.Format("{0}/{1}", name, points);
        }
    }
}
