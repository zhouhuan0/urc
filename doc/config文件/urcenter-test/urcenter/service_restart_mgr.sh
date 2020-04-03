#!/bin/bash
export JAVA_HOME=/jdk1.8.0_151

###启动urc-mgr 服务
/opt/urc-mgr/bin/shutdown.sh
sleep 2
cd /opt
rm -rf /opt/urc-mgr/
tar -xvf urc-mgr-assembly.tar.gz
sleep 2

cd urc-mgr/bin
./start.sh