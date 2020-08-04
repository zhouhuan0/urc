#!/bin/bash
# 生产jdbc文件替换到本地，方便调试生产环境
 ./undoCpProdJdbc.sh

# 使用opsmind配置中心的配置进行替换
cd ../../
python3 `pwd`/doc/jenkins/replace_config_py.py urcenter-service-provider-prod `pwd`

chmod +x ./cover_config.sh
./cover_config.sh

cd ./source

sed -i "" "s#/applogs#/Users/panyun/applogs#g" ./urc-service-provider/src/main/resources/log4j.properties
sed -i "" "s#/applogs#/Users/panyun/applogs#g" ./urc-mgr/src/main/resources/log4j.properties

mvn clean install -Dmaven.test.skip=true
