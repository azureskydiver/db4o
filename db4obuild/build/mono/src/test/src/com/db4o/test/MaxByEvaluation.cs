/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;

using com.db4o.query;

namespace com.db4o.test
{
    public class MaxByEvaluation
    {
        static int MAX = 20;

        public int val;

        public void store()
        {
            for(int i = 0; i <= MAX; i++)
            {
                MaxByEvaluation mbe = new MaxByEvaluation();
                mbe.val = i;
                Test.store(mbe);
            }
        }

        public void test()
        {
            Query q = Test.query();
            q.constrain(typeof(MaxByEvaluation));
            q.descend("val").constrain(new EvalCallbackForMax());
            ObjectSet objectSet = q.execute();
            Test.ensure(objectSet.size() == 1);
            MaxByEvaluation mbe = (MaxByEvaluation)objectSet.next();
            Test.ensure(mbe.val == MAX);
        }
    }

    public class EvalCallbackForMax : Evaluation
    {
        private int currentMax;
        private Candidate currentCandidate;

        public void evaluate(Candidate candidate)
        {
            int current = (int)candidate.getObject();
            if(currentCandidate != null)
            {
                if(currentMax > current)
                {
                    candidate.include(false);
                    return;
                }
                currentCandidate.include(false);
            }
            candidate.include(true);
            currentCandidate = candidate;
            currentMax = current;
        }
    }
}
