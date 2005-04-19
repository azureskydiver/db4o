/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
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
			EvaluationDelegate evaluation = GetEvaluationDelegate();
			evaluation(candidate);
		}
	}
}