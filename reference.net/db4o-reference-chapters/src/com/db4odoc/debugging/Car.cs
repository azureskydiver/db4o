
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
