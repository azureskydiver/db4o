/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

namespace com.db4o
{
#if NET_2_0
	using System;
	using System.Collections.Generic;
	using System.Text;

    public partial class YapStream
    {
        class GenericPredicateEvaluation<T> : DelegateEnvelope, com.db4o.query.Evaluation
        {
            public GenericPredicateEvaluation(Predicate<T> predicate) : base(predicate)
            {
            }

            public void evaluate(com.db4o.query.Candidate candidate)
            {
                // use starting _ for PascalCase conversion purposes
                Predicate<T> _predicate = (Predicate<T>)GetContent();
                candidate.include(_predicate((T)candidate.getObject()));
            }
        }

        public IList<Extent> query<Extent>(Predicate<Extent> match)
        {
            com.db4o.query.Query q = query();
            q.constrain(typeof(Extent));
            q.constrain(new GenericPredicateEvaluation<Extent>(match));
            com.db4o.inside.query.QueryResult qr = ((QQuery)q).getQueryResult();
            return new com.db4o.inside.query.GenericObjectSetFacade<Extent>(qr);
        }
    }
#endif
}
