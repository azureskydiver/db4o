/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

using com.db4o.events;
using com.db4o.@internal;

namespace com.db4o.constraints
{
	class ConstraintPlatform
	{
		class CommitEventAdapter
		{
			private ObjectContainerBase _container;
			private Constraint _constraint;

			public CommitEventAdapter(ObjectContainerBase container, Constraint constraint)
			{
				_container = container;
				_constraint = constraint;
			}

			public void Check(object sender, CommitEventArgs cea)
			{
				_constraint.Check(_container, cea);
			}
		}

		public static void AddCommittingConstraint(ObjectContainerBase container, Constraint constraint)
		{
			EventRegistryFactory.ForObjectContainer(container).Committing +=
				new CommitEventHandler(new CommitEventAdapter(container, constraint).Check);
		}
	}
}
