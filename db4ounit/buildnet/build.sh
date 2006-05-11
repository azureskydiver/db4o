#cp ../../db4ounitcsharp/
cp -R src/* ../../db4ounitcsharp/src
find ../../db4ounitcsharp/src -name *.cs | xargs mcs -r:db4o.dll -main:com.db4o.test.unit.test.RegressionTest -out:unitcsharp.exe
mono unitcsharp.exe