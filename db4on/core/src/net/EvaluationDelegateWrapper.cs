/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;
using com.db4o;
using com.db4o.query;

namespace com.db4o
{
	internal class EvaluationDelegateWrapper : Evaluation
	{
		object _target;
		string _type;
		string _method;
		
		[Transient]
		EvaluationDelegate _evaluation;
		
		public EvaluationDelegateWrapper()
		{
		}
		
		public EvaluationDelegateWrapper(EvaluationDelegate evaluation)
		{
			_target = evaluation.Target;
			_method = evaluation.Method.Name;
			_type = evaluation.Method.DeclaringType.AssemblyQualifiedName;
		}
		
		EvaluationDelegate GetEvaluationDelegate()
		{
			if (null == _evaluation)
			{
				_evaluation = CreateEvaluationDelegate();
			}
			return _evaluation;
		}
		
		EvaluationDelegate CreateEvaluationDelegate()
		{				
			object result = (null == _target)
				? System.Delegate.CreateDelegate(typeof(EvaluationDelegate), GetTargetType(), _method)
				: System.Delegate.CreateDelegate(typeof(EvaluationDelegate), _target, _method);
			return (EvaluationDelegate)result;
		}
		
		private Type GetTargetType()
		{
			return Type.GetType(_type, true);
		}
		
		public void evaluate(Candidate candidate)
		{
			// use starting _ for PascalCase conversion purposes
			EvaluationDelegate _evaluation = GetEvaluationDelegate();
			_evaluation(candidate);
		}
	}
}