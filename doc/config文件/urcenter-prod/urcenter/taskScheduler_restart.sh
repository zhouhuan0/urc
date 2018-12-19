#!/bin/bash
export JAVA_HOME=/jdk1.8.0_151
###启动urc-taskScheduler服务
cd /opt
/opt/urc-taskScheduler/bin/taskScheduler-shutdown.sh
sleep 2
rm -rf /opt/urc-service-provider
tar -xvf urc-taskScheduler-assembly.tar.gz
sleep 2
cd urc-taskScheduler/bin
./start.sh
