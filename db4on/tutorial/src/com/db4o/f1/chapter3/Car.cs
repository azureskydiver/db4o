namespace com.db4o.f1.chapter3
{
    using System;
    using System.Collections;
    
    public class Car
    {
        string _model;
        Pilot _pilot;
        IList _history;
        
        public Car(string model) : this(model, new ArrayList())
        {
        }
        
        public Car(string model, IList history)
        {
            _model = model;
            _pilot = null;
            _history = history;
        }
        
        public Pilot Pilot
        {
            get
            {
                return _pilot;
            }
            
            set
            {
                _pilot = value;
            }
        }
        
        public string Model
        {
            get
            {
                return _model;
            }
        }
        
        public SensorReadout[] GetHistory()
        {
            SensorReadout[] history = new SensorReadout[_history.Count];
            _history.CopyTo(history, 0);
            return history;
        }
        
        public void Snapshot()
        {
            _history.Add(new SensorReadout(Poll(), DateTime.Now, this));
        }
        
        protected double[]  Poll()
        {
            int factor = _history.Count + 1;
            return new double[] { 0.1d*factor, 0.2d*factor, 0.3d*factor };
        }
        
        override public string ToString()
        {
            return _model + "[" + _pilot + "]/" + _history.Count;
        }
    }
}
