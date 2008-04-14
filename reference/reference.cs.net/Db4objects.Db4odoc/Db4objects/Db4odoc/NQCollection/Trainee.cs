/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

namespace Db4objects.Db4odoc.NQCollection
{
    class Trainee: Person
    {
        private string _name;
        private Pilot _instructor;

        public Trainee(string name, Pilot pilot)
        {
            _name = name;
            _instructor = pilot;
        }

        public string Name
        {
            get
            {
                return _name;
            }
        }

        public Pilot Instructor
        {
            get
            {
                return _instructor;
            }
        }

        public override string ToString()
        {
            return string.Format("{0}({1})", _name, _instructor);
        }
    }
}
