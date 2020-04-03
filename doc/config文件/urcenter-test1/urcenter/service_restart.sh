#!/bin/bash
export JAVA_HOME=/jdk1.8.0_151

###启动urc-service-provider服务
/opt/urc-service-provider/bin/shutdown.sh
sleep 2
cd /opt
rm -rf /opt/urc-service-provider/
tar -xvf urc-service-provider-assembly.tar.gz
sleep 2
cd urc-service-provider/bin
./start.sh


###启动urc-service-provider服务
/opt/urc-mgr/bin/shutdown.sh
sleep 2
cd /opt
rm -rf /opt/urc-mgr/
tar -xvf urc-mgr-assembly.tar.gz
sleep 2

# 8002端口改为8003
sed -in-place -e s/8002/8003/g urc-mgr/conf/motan/motan-service-config.xml
cd urc-mgr/bin
./start.sh
