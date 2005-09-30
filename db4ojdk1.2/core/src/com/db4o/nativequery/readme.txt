This is a first spike on native queries. The nomenclature is still our
initial one (i.e. Filter instead of Predicate, filter instead of match,
etc.) Apologies, the code is an absolute mess, it's not much more than
a diary of my struggle with the different bytecode mangling APIs I've
tried.

The general idea is this: A query is represented by a filter instance.
A filter is applied to a collection. Then there are three base cases:

1. The collection is a plain one that doesn't implement ActiveCollection.
   The filter will simply iterate over the collection and select matching
   candidates.  
   
2. The collection implements ActiveCollection. The filter will delegate
   the selection process to the collection itself. Currently there only
   is one db4o-specific implementation, Db4oListFacade.
   
   2a. The filter is unoptimized and doesn't implement Db4oEnhancedFilter.
       Db4oListFacade will simply use the given type information to
       produce the specified extent from the database and wrap the call
       to the filter method in a db4o Evaluation.
   2b. The filter implements Db4oEnhancedFilter. Then Db4oListFacade will
       pass a 'fresh' query into its optimizeQuery() method and execute
       the resulting query against the database.
       
Optimization currently is done at class loading time. (To handle virtual
methods correctly, we'll probably have to delay this until query execution
time.) The optimization process spans three steps:

1. Convert the filter method body to a (non db4o-specific) boolean expression
   tree with boolean operators as internal nodes and field/constant
   comparisons or boolean constants as leaves.
2. Convert the expression tree to a (desirably minimal) normal form. (Not
   implemented yet.)
3. Convert the expression tree to a optimizeQuery() method generating
   an equivalent SODA query. Only this step is db4o/SODA specific, the
   previous ones could be reused for SQL, etc.

Code analysis as well as generation of the optimized query code is handled
by the bloat library.

The code spans these packages:

analysis
  Step 1 of the optimization process - converting a method body to an
  expression tree.
bloat
  Simple utils for access to bloat representations (class editors, flow
  graphs).
core
  The generic NQ classes, independent of db4o or APIs used for optimization.
example
  Just a very basic manual test case for the optimization process.
expr
  The expression tree nodes.
expr.build
  Helper classes for generating expression trees (and a future home for
  step 2 of the optimization process).
main
  Code to start an application with an optimizing class loader.
optimization.db4o
  All the db4o specific classes, covering step 2a/b of the filtering process
  and step 3 of the optimization process.
  
For a first glance, execute SimpleMain from the example package to run
the query defined in this class in unoptimized mode. Then run
Db4oRunner from the main package with the fully qualified
name of SimpleMain as a command line argument to run the query in
optimized mode. The output should also show the flow graph representation
of the query, the generated expression tree and the resulting body of the
generated optimizeQuery() method. The code should run against any recent
db4o build. To statically instrument class files, use Db4oFileEnhancer
or refer to the Ant build-nqtest.xml file. Beware: This is still buggy, and
again I'm quite sure we won't be able to stick to compile/load time, anyway.

Currently optimization should be able to handle most cases of:

- primitive comparison expressions
- String#equals()
- boolean expressions
- simple getter methods

(See BloatExprBuilderVisitorTest for the test cases I've used.)

There's no error handling at all - as said before, the code's a mess.
Nevertheless, any input is appreciated. It can only get better. :)