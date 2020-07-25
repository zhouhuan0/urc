#!/bin/bash
# 生产jdbc文件替换到本地，方便调试生产环境
 ./undoCpProdJdbc.sh

cd ../../
cp ./doc/config文件/urcenter-prod/urcenter/source/urc-service-provider/src/main/resources/jdbc* ./source/urc-service-provider/src/main/resources/
cp ./doc/config文件/urcenter-prod/urcenter/source/urc-service-provider/src/main/resources/kafka* ./source/urc-service-provider/src/main/resources/
cp ./doc/config文件/urcenter-prod/urcenter/source/urc-service-provider/src/main/resources/jdbc* ./source/urc-mgr/src/main/resources/

cd ./source

sed -i "" "s#/applogs#/Users/panyun/applogs#g" ./urc-service-provider/src/main/resources/log4j.properties
sed -i "" "s#/applogs#/Users/panyun/applogs#g" ./urc-mgr/src/main/resources/log4j.properties
sed -i "" "s#urcenterdbm.kokoerp.com#172.16.107.19#g"  ./urc-mgr/src/main/resources/jdbc*
sed -i "" "s#urcenterdbm.kokoerp.com#172.16.107.19#g"  ./urc-service-provider/src/main/resources/jdbc*

mvn clean install -pl urc-service-provider -am -e -U -Dmaven.test.skip=true
