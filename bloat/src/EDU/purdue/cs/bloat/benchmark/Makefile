# All files in the distribution of BLOAT (Bytecode Level Optimization and
# Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
# Research Foundation of Purdue University.  All rights reserved.
#
# Redistribution and use in source and binary forms are permitted
# provided that this entire copyright notice is duplicated in all such
# copies, and that any documentation, announcements, and other
# materials related to such distribution and use acknowledge that the
# software was developed at Purdue University, West Lafayette, IN by
# Antony Hosking, David Whitlock, and Nathaniel Nystrom.  No charge
# may be made for copies, derivations, or distributions of this
# material without the express written consent of the copyright
# holder.  Neither the name of the University nor the name of the
# author may be used to endorse or promote products derived from this
# material without specific prior written permission.  THIS SOFTWARE
# IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
# INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
#
# Java is a trademark of Sun Microsystems, Inc.

CLASS = \
	Benchmark.class\
	Shade.class\
	Stats.class\
	BenchmarkSecurityManager.class\
	Nonstop.class\
	Times.class\
	CounterDecorate.class

JNIH = EDU_purdue_cs_bloat_benchmark_Benchmark.h \
      EDU_purdue_cs_bloat_benchmark_Shade.h \
      EDU_purdue_cs_bloat_benchmark_Stats.h \
      EDU_purdue_cs_bloat_benchmark_Times.h \
      EDU_purdue_cs_bloat_benchmark_Nonstop.h

OBJ = benchmark.o shade.o stats.o nonstop.o times.o

LIB = libbenchmark_g.so libbenchmark.so libshade.so libstats.so libstats_g.so libnonstop.so libtimes.so libtimes_g.so

.SUFFIXES: .java .class

JAVA_HOME = /u/u83/pps/java
JAVAC = $(JAVA_HOME)/bin/javac
JAVAH = $(JAVA_HOME)/bin/javah
JFLAGS = -g
CLASSPATH = $(JAVA_HOME)/lib/classes.zip
CFLAGS = -K pic -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/solaris \
	-I/u/u83/pps/perfmon/include

all: class $(LIB)

clean:
	rm -f *.class *.o $(JNIH) $(OBJ) $(LIB)

class:
	@files=`$(MAKE) -n _class | grep javac | cut -d' ' -f4`; \
	cpath=$(CLASSPATH):`(cd ../../../../..; pwd)`; \
	if [ "x$$files" != "x" ]; then \
	    echo $(JAVAC) $(JFLAGS) -classpath $$cpath $$files; \
	    $(JAVAC) $(JFLAGS) -classpath $$cpath $$files; \
	fi

_class: $(CLASS)

.java.class:
	cpath=$(CLASSPATH):`(cd ../../../../..; pwd)`; \
	$(JAVAC) -classpath $$cpath $<

libbenchmark.so: benchmark.o
	cc benchmark.o -o libbenchmark.so /u/u83/pps/perfmon/lib/libperfmon.a -G

libbenchmark_g.so: benchmark.o
	cc benchmark.o -o libbenchmark_g.so /u/u83/pps/perfmon/lib/libperfmon.a -G

libshade.so: shade.o
	cc shade.o -o libshade.so -G

libstats_g.so: stats.o
	cc stats.o -o libstats_g.so -G

libstats.so: stats.o
	cc stats.o -o libstats.so -G

libtimes.so: times.o
	cc times.o -o libtimes.so -G

libtimes_g.so: times.o
	cc times.o -o libtimes_g.so -G

libnonstop.so: nonstop.o
	cc nonstop.o -o libnonstop.so -G

EDU_purdue_cs_bloat_benchmark_Benchmark.h: Benchmark.java
	cpath=$(CLASSPATH):`(cd ../../../../..; pwd)`; \
	$(JAVAH) -jni -classpath $$cpath \
	EDU.purdue.cs.bloat.benchmark.Benchmark

EDU_purdue_cs_bloat_benchmark_Shade.h: Shade.java
	cpath=$(CLASSPATH):`(cd ../../../../..; pwd)`; \
	$(JAVAH) -jni -classpath $$cpath \
	EDU.purdue.cs.bloat.benchmark.Shade

EDU_purdue_cs_bloat_benchmark_Stats.h: Stats.java
	cpath=$(CLASSPATH):`(cd ../../../../..; pwd)`; \
	$(JAVAH) -jni -classpath $$cpath \
	EDU.purdue.cs.bloat.benchmark.Stats

EDU_purdue_cs_bloat_benchmark_Times.h: Times.java
	cpath=$(CLASSPATH):`(cd ../../../../..; pwd)`; \
	$(JAVAH) -jni -classpath $$cpath \
	EDU.purdue.cs.bloat.benchmark.Times

EDU_purdue_cs_bloat_benchmark_Nonstop.h: Nonstop.java
	cpath=$(CLASSPATH):`(cd ../../../../..; pwd)`; \
	$(JAVAH) -jni -classpath $$cpath \
	EDU.purdue.cs.bloat.benchmark.Nonstop

nonstop.o: nonstop.c EDU_purdue_cs_bloat_benchmark_Nonstop.h

benchmark.o: benchmark.c EDU_purdue_cs_bloat_benchmark_Benchmark.h

times.o: times.c EDU_purdue_cs_bloat_benchmark_Times.h

shade.o: shade.c EDU_purdue_cs_bloat_benchmark_Shade.h

stats.o: stats.c EDU_purdue_cs_bloat_benchmark_Stats.h

.c.o:
	cc -c $(CFLAGS) $<

