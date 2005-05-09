#!/bin/sh
cd objectmanager-builds
mkdir tmp
cd tmp
unzip ../objectmanager_linux.zip
chmod a+x *.sh
rm -f ../objectmanager_linux.tar.gz
rm -f ../objectmanager_linux.zip
tar -czvf ../objectmanager_linux.tar.gz *
cd ..
rm -rf ./tmp
cd ..