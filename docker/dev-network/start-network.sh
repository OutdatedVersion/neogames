#!/bin/bash

cd /mc
echo "Working directory: ${pwd}"

java -version

mkdir -p /mc/network/{proxy,live}
mkdir -p /mc/storage/{maps,plugin,server-template}
mkdir -p /mc/config/{backend,database}

cd /mc/network/proxy
wget https://ci.aquifermc.org/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar
mv Waterfall.jar waterfall.jar

java -jar waterfall.jar

# Make directories
# Copy proxy
# Copy folders
