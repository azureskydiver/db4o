//
// AssemblyNameReferenceCollection.cs
//
// Author:
//   Jb Evain (jbevain@gmail.com)
//
// Generated by /CodeGen/cecil-gen.rb do not edit
// Thu Sep 29 22:11:54 CEST 2005
//
// (C) 2005 Jb Evain
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

namespace Mono.Cecil {

	using System;
	using System.Collections;

	using Mono.Cecil.Cil;

	public sealed class AssemblyNameReferenceCollection : IAssemblyNameReferenceCollection {

		IList m_items;
		ModuleDefinition m_container;

		public event AssemblyNameReferenceEventHandler OnAssemblyNameReferenceAdded;
		public event AssemblyNameReferenceEventHandler OnAssemblyNameReferenceRemoved;

		public AssemblyNameReference this [int index] {
			get { return m_items [index] as AssemblyNameReference; }
			set { m_items [index] = value; }
		}

		public ModuleDefinition Container {
			get { return m_container; }
		}

		public int Count {
			get { return m_items.Count; }
		}

		public bool IsSynchronized {
			get { return false; }
		}

		public object SyncRoot {
			get { return this; }
		}

		public AssemblyNameReferenceCollection (ModuleDefinition container)
		{
			m_container = container;
			m_items = new ArrayList ();
		}

		public void Add (AssemblyNameReference value)
		{
			if (OnAssemblyNameReferenceAdded != null && !this.Contains (value))
				OnAssemblyNameReferenceAdded (this, new AssemblyNameReferenceEventArgs (value));
			m_items.Add (value);
		}

		public void Clear ()
		{
			if (OnAssemblyNameReferenceRemoved != null)
				foreach (AssemblyNameReference item in this)
					OnAssemblyNameReferenceRemoved (this, new AssemblyNameReferenceEventArgs (item));
			m_items.Clear ();
		}

		public bool Contains (AssemblyNameReference value)
		{
			return m_items.Contains (value);
		}

		public int IndexOf (AssemblyNameReference value)
		{
			return m_items.IndexOf (value);
		}

		public void Insert (int index, AssemblyNameReference value)
		{
			if (OnAssemblyNameReferenceAdded != null && !this.Contains (value))
				OnAssemblyNameReferenceAdded (this, new AssemblyNameReferenceEventArgs (value));
			m_items.Insert (index, value);
		}

		public void Remove (AssemblyNameReference value)
		{
			if (OnAssemblyNameReferenceRemoved != null && this.Contains (value))
				OnAssemblyNameReferenceRemoved (this, new AssemblyNameReferenceEventArgs (value));
			m_items.Remove (value);
		}

		public void RemoveAt (int index)
		{
			if (OnAssemblyNameReferenceRemoved != null)
				OnAssemblyNameReferenceRemoved (this, new AssemblyNameReferenceEventArgs (this [index]));
			m_items.Remove (index);
		}

		public void CopyTo (Array ary, int index)
		{
			m_items.CopyTo (ary, index);
		}

		public IEnumerator GetEnumerator ()
		{
			return m_items.GetEnumerator ();
		}

		public void Accept (IReflectionStructureVisitor visitor)
		{
			visitor.VisitAssemblyNameReferenceCollection (this);
		}
	}
}
