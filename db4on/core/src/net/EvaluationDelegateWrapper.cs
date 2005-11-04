/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;
using com.db4o;
using com.db4o.query;

namespace com.db4o
{
    // TODO: Use DelegateEnvelope to build a generic delegate translator
    internal class DelegateEnvelope
    {
        System.Type _delegateType;
        object _target;
        System.Type _type;
        string _method;

        [Transient]
        Delegate _content;

        public DelegateEnvelope()
        {
        }

        public DelegateEnvelope(Delegate content)
        {
            _content = content;
            Marshal();
        }

        protected Delegate GetContent()
        {
            if (null == _content)
            {
                _content = Unmarshal();
            }
            return _content;
        }

        private void Marshal()
        {
            _delegateType = _content.GetType();
            _target = _content.Target;
            _method = _content.Method.Name;
            _type = _content.Method.DeclaringType;
        }

        private Delegate Unmarshal()
        {
            return (null == _target)
                ? System.Delegate.CreateDelegate(_delegateType, _type, _method)
                : System.Delegate.CreateDelegate(_delegateType, _target, _method);
        }
    }

	internal class EvaluationDelegateWrapper : DelegateEnvelope, Evaluation
	{	
		public EvaluationDelegateWrapper()
		{
		}
		
		public EvaluationDelegateWrapper(EvaluationDelegate evaluation) : base(evaluation)
		{	
		}
		
		EvaluationDelegate GetEvaluationDelegate()
		{
            return (EvaluationDelegate)GetContent();
		}
		
		public void evaluate(Candidate candidate)
		{
			// use starting _ for PascalCase conversion purposes
			EvaluationDelegate _evaluation = GetEvaluationDelegate();
			_evaluation(candidate);
		}
	}
}