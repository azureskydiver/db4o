#
# Makefile to build db4o using gcj on Linux or Cygwin
#

all: libdb4o.a libdb4o.so test

libdb4o.a libdb4o.so:
	find ../core/src | grep java$$ | xargs gcj -c --classpath=../core/src
	ar -crs libdb4o.a *.o
	gcj --shared -o libdb4o.so *.o

test:


clean:
	rm -f *.o *~


veryclean: clean
	rm -f *.a *.so

