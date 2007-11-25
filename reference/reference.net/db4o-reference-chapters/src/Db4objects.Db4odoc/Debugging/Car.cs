/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Debugging
{
    public class Car
    {
        string _model;

        public Car(string model)
        {
            _model = model;
        }

        override public string ToString()
        {
            return  _model;
        }
    }
}
