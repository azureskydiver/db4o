/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System.Text;

namespace Db4objects.Db4odoc.Typehandler
{
    public class Car
    {

        private StringBuilder model;
        private StringBuilder modelCopy;

        public Car(string model)
        {
            this.model = new StringBuilder(model);
            this.modelCopy = new StringBuilder("Copy: " + model);
        }


        public string getModel()
        {
            return model.ToString();
        }

        public override string ToString()
        {
            return model == null ? "null" : model.ToString() + " " + modelCopy.ToString();
        }
    }
}
