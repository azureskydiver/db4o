#
# Makefile to build db4o using gcj on Linux or Cygwin
#
# Make sure that this directory is the current working directory 
# when invoking make!
#

all: libdb4o.a libdb4o.so AllTests


libdb4o.a libdb4o.so:
	find ../core/src | grep java$$ | xargs gcj -c -g3 --classpath=../core/src
	ar -crs libdb4o.a *.o
	gcj --shared -g3 -o libdb4o.so *.o
	make clean


AllTests:
	gcj -c -g3 --classpath=../core/src:../tools/src:../test/src ../tools/src/com/db4o/util/io/spikes/TestDummy.java
	gcj -c -g3 --classpath=../core/src:../tools/src:../test/src ../tools/src/com/db4o/wrap/ObjectSetCollection.java
	gcj -c -g3 --classpath=../core/src:../tools/src:../test/src ../tools/src/com/db4o/tools/Logger.java
	gcj -c -g3 --classpath=../core/src:../tools/src:../test/src ../tools/src/com/db4o/tools/Statistics.java
	gcj -c -g3 --classpath=../core/src:../tools/src:../test/src ../tools/src/com/db4o/tools/Defragment.java

# Don't compile jdk5.x tests
	find ../test/src \
	| grep 'test/[0-9a-zA-Z\\.]*java$$\|soda/[0-9a-zA-Z/\\.]*java$$\|pending/[0-9a-zA-Z/\\.]*java$$\|test2/[0-9a-zA-Z/\\.]*java$$\|types/[0-9a-zA-Z/\\.]*java$$\|lib/[0-9a-zA-Z/\\.]*java$$' \
	| xargs gcj -c -g3 --classpath=../core/src:../tools/src:../test/src
	gcj -O0 -g3  -l db4o --main=com.db4o.test.AllTests *.o -oAllTests -L../gcj

	make clean


clean:
	rm -fr *.o *~ *.yap *.bak test


veryclean: clean
	rm -f *.a *.so AllTests

