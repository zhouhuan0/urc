#!/bin/bash
# java项目jenkins构建时build版本镜像使用的脚本
# 使用方法: 传递3个变量: 1.job名称 2.构建版本号 3.tar包的相对路径
# bash /var/lib/jenkins/image/build_image.sh yksomstest1 215 newoms-order-manage-server-dev /var/lib/jenkins/jobs/newoms-http-provider-prod/yks-oms/yks-oms-http-provider/target/yks-oms-http-provider-assembly.tar.gz

# jenkins名称
JOB_NAME=$1
# 构建版本号
BUILD_NUMBER=$2
# job工作空间路径
JOB_PATH=/var/lib/jenkins/jobs/${JOB_NAME}/workspace
# pod name
DES_POD_NAME=$3
# tar包的绝对路径
PACK_PATH=$4
# tar包名称
pack=`basename $PACK_PATH`

if [ ! -f $PACK_PATH ];then
    echo "tar包不存在"
    exit 1
fi

# 进入job工作空间,创建打包用的目录，workspace在经过编译后空间很大，避免将整个workspace内的文件在docker build的时候全部作为context提交上去。
cd  $JOB_PATH
[ ! -d image ] && mkdir image
cd $JOB_PATH/image

#将tar包和启动脚本移到当前路径
cp $PACK_PATH .
cp /var/lib/jenkins/image/service_start.sh .

# 记录本次构建的一些日志，例如:基于的git branch信息
git name-rev --name-only HEAD > pipeline.log  && git log --pretty=format:"%an %ad %h %s" -n 1  --date=format:'%Y-%m-%d %H:%M:%S' >> pipeline.log

#替换模板dockerfile里面的包名
cat /var/lib/jenkins/image/Base_Dockerfile | sed  "s/PACK_NAME/${pack}/g" > Dockerfile

IMAGE=registry.youkeshu.com:5000/${DES_POD_NAME}:${BUILD_NUMBER}
# 构建镜像
docker build -t $IMAGE .
echo "Build image [$IMAGE] successfully!"

# push镜像
docker push $IMAGE

# 推送到仓库后删除本地镜像,避免本地堆积的镜像过多
docker rmi $IMAGE