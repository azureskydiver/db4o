#
# Makefile for db4o Mono RPM
#
DB4O_VERSION=@db4oversion@
DIR_HOME=.
DIR_BIN=$(DIR_HOME)/bin
DIR_SRC=$(DIR_HOME)/src
DIR_DOC=$(DIR_HOME)/doc
DIR_SRC_CORE=$(DIR_SRC)/core/src
DIR_SRC_TOOLS=$(DIR_SRC)/tools
DIR_SRC_TEST=$(DIR_SRC)/test/src
ifdef RPM_BUILD_ROOT
	DIR_INSTALL_BIN=$(RPM_BUILD_ROOT)/usr/lib/db4o
else
	DIR_INSTALL_BIN=/usr/lib/db4o
endif
ifdef RPM_DOC_DIR
	DIR_INSTALL_DOC=$(RPM_BUILD_ROOT)/$(RPM_DOC_DIR)/$(RPM_PACKAGE_NAME)-$(RPM_PACKAGE_VERSION)
else
	DIR_INSTALL_DOC=/usr/share/doc/db4o-$(DB4O_VERSION)
endif

MAIN_TEST=com.db4o.test.AllTests

DLL_CORE_DIST=$(DIR_HOME)/dll/db4o.dll
DLL_CORE=$(DIR_BIN)/db4o.dll
DLL_TOOLS=$(DIR_BIN)/Db4oTools.dll
DEBUGGER_FILES=$(DIR_BIN)/*.mdb
EXE_TEST_FILE=Db4oMonoTest.exe
EXE_TEST_PATH=$(DIR_BIN)/$(EXE_TEST_FILE)

MCS_OPTS=-warn:0 -debug+ -define:MONO

MCS=mcs
MONO=mono

runtest: buildtest
	$(MONO) ./$(EXE_TEST_PATH)

buildtest: buildtools
	find $(DIR_SRC_TEST) -name "*.cs"|grep -v "/compact[^/]*/"|grep -v "/net2/"|grep -v "/ListElementByIdentity.cs"|grep -v "/TestCatSpeed.cs"|xargs $(MCS) $(MCS_OPTS) -target:exe -r:$(DLL_CORE) -r:$(DLL_TOOLS) -main:$(MAIN_TEST) -out:$(EXE_TEST_PATH)

buildtools: buildcore
	find $(DIR_SRC_TOOLS) -name "*.cs"|grep -v "/templates/"|grep -v "/Cecil.FlowAnalysis.Tests/"|grep -v "/AssemblyInfo.cs"|xargs $(MCS) $(MCS_OPTS) -target:library -r:$(DLL_CORE) -out:$(DLL_TOOLS)

buildcore: init
	find $(DIR_SRC_CORE) -name "*.cs"|grep -v -E "src/compact[^/]*/"|grep -v "/templates/"|xargs $(MCS) $(MCS_OPTS) -target:library -out:$(DLL_CORE)

init:
ifndef RPM_BUILD_ROOT
	echo "NO BUILD ROOT"
endif
	mkdir -p $(DIR_BIN)

clean:
	rm -rf $(DIR_BIN)
	
install:
	mkdir -p $(DIR_INSTALL_BIN)
	cp $(DLL_CORE) $(DIR_INSTALL_BIN)
	cp $(DLL_TOOLS) $(DIR_INSTALL_BIN)
	cp $(EXE_TEST_PATH) $(DIR_INSTALL_BIN)
ifndef RPM_DOC_DIR
	mkdir -p $(DIR_INSTALL_DOC)
	cp -R $(DIR_DOC)/* $(DIR_INSTALL_DOC)
endif

.PHONY: clean runtest