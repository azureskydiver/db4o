using System;

namespace Spikes {
    public class Milking : CowEvent {
        float _amount;

        public Milking(Cow cow, DateTime date, float amount)
            : base(cow, date) {
            _amount = amount;
        }

        public float Amount {
            get { return _amount; }
        }
    }
}
