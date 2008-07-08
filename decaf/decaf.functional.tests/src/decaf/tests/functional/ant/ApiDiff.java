/**
 * 
 */
package decaf.tests.functional.ant;

import java.io.*;
import java.util.*;

import decaf.builder.*;

class ApiDiff {
	
	interface FailureHandler {
		void fail(String message);
	}
	
	private final FailureHandler _failureHandler;
	private final Map<String, ClassEntry> _expected;
	private final Map<String, ClassEntry> _actual;
	private final DecafConfiguration _config;
	
	public ApiDiff(FailureHandler failureHandler, File expect, File actual, DecafConfiguration config) throws IOException {
		_failureHandler = failureHandler;
		_expected = classEntries(expect);
		_actual = classEntries(actual);
		_config = config;
	}
	
	public void run() {
		final Set<String> unexpected = difference(_actual, _expected);
		logFailures("Unexpected class: ", unexpected);
		
		final Set<String> missing = difference(_expected, _actual);
		logFailures("Missing class: ", missing);
		
		checkClassEntries();
	}
	
	private void fail(String message) {
		_failureHandler.fail(message);
	}
	
	private void logFailures(String prefix, Iterable<?> set) {
		for (Object o : set) {
			fail(prefix + o);
		}
	}
	
	private void checkClassEntries() {
		for (String klass : intersection(_expected.keySet(), _actual.keySet())) {
			final ClassEntry expectedEntry = _expected.get(klass);
			final ClassEntry actualEntry = _actual.get(klass);
			if (!expectedEntry.sameDeclaration(actualEntry)) {
				fail("Expecting '" + expectedEntry + "' got '" + actualEntry + "'.");
				continue;
			}
			checkMembers(expectedEntry, actualEntry);
		}
	}

	private void checkMembers(ClassEntry expected, ClassEntry actual) {
		checkMethods("Missing", expected, actual, MappingMode.NO_MAPPING);
		checkMethods("Unexpected", actual, expected, MappingMode.NO_MAPPING);
	}

	private void checkMethods(final String prefix, ClassEntry l, ClassEntry r, MappingMode mappingMode) {
		for (MethodEntry method : l.methods()) {
			if (method.name().contains("<clinit>")) {
				// static ctors are not really API
				continue;
			}
			
			final String descriptor = mappedDescriptor(method.descriptor(), mappingMode);
			final MethodEntry actual = r.method(method.name(), descriptor);
			if (null == actual) {
				fail(prefix  + " '" + method + "' on type '"  + r.name() + "'.");
				continue;
			}
		}
	}

	private String mappedDescriptor(String descriptor, MappingMode mappingMode) {
		if(mappingMode == MappingMode.NO_MAPPING) {
			return descriptor;
		}
		boolean reverse = mappingMode == MappingMode.REVERSE_MAPPING;
		for (String mappedKey : mappedKeys(reverse)) {
			String mappedValue = typeNameMapping(mappedKey, reverse);
			String replaceWhat = sourceNameToBytecodeName(mappedKey);
			String replaceWith = sourceNameToBytecodeName(mappedValue);
			descriptor = descriptor.replaceAll(replaceWhat, replaceWith);
		}
		return descriptor;
	}

	private Iterable<String> mappedKeys(boolean reverse) {
		return reverse ? _config.mappedTypeValues() : _config.mappedTypeKeys();
	}
	
	private String typeNameMapping(String key, boolean reverse) {
		return reverse ? _config.reverseTypeNameMapping(key) : _config.typeNameMapping(key);
	}
	
	private String sourceNameToBytecodeName(String origName) {
		return "L" + origName.replace('.', '/') + ";";
	}
	
	private Map<String, ClassEntry> classEntries(File file) throws IOException {
		final ClassEntryReader reader = new ClassEntryReader(file);
		try {
			final Map<String, ClassEntry> found = new HashMap<String, ClassEntry>();
			collectClassEntries(found, reader);
			return found;
		} finally {
			reader.close();
		}
	}

	private void collectClassEntries(final Map<String, ClassEntry> found,
			final ClassEntryReader reader) throws IOException {
		while (true) {
			final ClassEntry classEntry = reader.readNext();
			if (null == classEntry) {
				break;
			}
			found.put(classEntry.name(), classEntry);
		}
	}
	
	private static <T> Set<T> intersection(Set<T> x, Set<T> y) {
		return SetExtensions.intersection(x, y);
	}

	private <TKey, TValue> Set<TKey> difference(final Map<TKey, TValue> x, final Map<TKey, TValue> y) {
		return SetExtensions.difference(x.keySet(), y.keySet());
	}
	
	private enum MappingMode {
		
		NO_MAPPING("no mapping"),
		SIMPLE_MAPPING("simple mapping"),
		REVERSE_MAPPING("reverse mapping");
		
		private MappingMode(String id) {
		}
	}
}