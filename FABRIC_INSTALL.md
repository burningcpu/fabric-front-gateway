# fabric搭链文档

## 一、简介
本文档介绍fabric环境搭建步骤

## 二、注意
搭建过程中请使用root用户进行操作

## 三、环境准备
如果此前已安装这些依赖，请直接跳转到下一节

### 3.1、 安装git&wget&vim&gcc&node环境
```
yum install -y git
yum install -y wget
yum install -y vim
yum install -y gcc
yum install -y gcc-c++
yum install epel-release
yum install nodejs
```

### 3.2、 安装Go环境 （ version 1.12.x以上）

* 下载go安装包： `curl -LO https://dl.google.com/go/go1.12.13.linux-amd64.tar.gz`
* 解压到/usr/local目录下：`tar -C /usr/local -xzf go1.12.13.linux-amd64.tar.gz`
* 创建gopath路径（go源文件、编译文件和可执行文件路径）：`mkdir -p /home/gopath`
* 配置环境变量
  - 编辑文件：`vim ~/.bash_profile` 或 `vim /etc/profile`
  - 在文件文件末添加如下内容后，保存退出:
    ```
    export GOPATH=/home/gopath
    export GOROOT=/usr/local/go
    export PATH=$PATH:$GOROOT/bin
    ```
  - 刷新环境变量:`source ~/.bash_profile`或`source /etc/profile`
  - 查看版本号：`go version`

### 3.3、 安装Docker环境(17.06.2-ce以上版本)
* 安装依赖软件包: `yum install -y yum-utils device-mapper-persistent-data lvm2`
* 添加yml源: `yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo`
* 更新yml缓存: `yum makecache fast`
* 安装docker-ce: `yum -y install docker-ce`
* 启动 Docker 后台服务: `systemctl start docker`
* 查看版本号：`docker -v`
* 镜像加速:
  - 修改文件（如果没有，自己新建一个）：`vi /etc/docker/daemon.json`
  - 添加自己的加速地址后，保存退出，如：`{"registry-mirrors": ["http://hub-mirror.c.163.com"]}`
  - 重启服务:`systemctl daemon-reload && systemctl restart docker`
  - 查看当前镜像仓库:`docker info`
  - 测试:`docker run hello-world`

### 3.4、 安装Docker Compose环境(1.14.0以上版本)
 * 下载安装包到指定目录：
 > curl -L https://github.com/docker/compose/releases/download/1.25.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose

 * 添加文件的执行权限： `chmod +x /usr/local/bin/docker-compose`
 * 查看版本号：`docker-compose --version`

## 四、fabric搭建

### 4.1、 新建目录
`mkdir -p $GOPATH/src/github.com/hyperledger`

### 4.2、 源码下载（比较慢，请耐心等待）
`cd $GOPATH/src/github.com/hyperledger`
`git clone -b release-1.4 https://github.com/hyperledger/fabric.git`
`git clone -b release-1.4 https://github.com/hyperledger/fabric-samples.git`

### 4.3、 源码编译
`cd $GOPATH/src/github.com/hyperledger/fabric && make release`

### 4.4、 配置环境变量
 * 编辑文件：`vim ~/.bash_profile` 或 `vim /etc/profile`
 * 在文件文件末添加如下内容后，保存退出:
   > export PATH=$PATH:$GOPATH/src/github.com/hyperledger/fabric/release/linux-amd64/bin
 * 刷新环境变量: `source ~/.bash_profile`或`source /etc/profile`

### 4.4、 启停
 * 目录：`cd $GOPATH/src/github.com/hyperledger/fabric-samples/first-network`
 * 启动（默认mychannel）：  ./byfn.sh up
 * 自定义名称启动：         ./byfn.sh -m up -c firstchannel
 * 停止                   ./byfn.sh down

### 4.5、 发送交易
如果使用默认脚本搭建，会自带一个已部署好的链码，可以直接发交易,可以用来测试是否搭链成功。
* 进入cli容器：` docker exec -it cli bash`
* 执行链码查询函数：
    ```
    peer chaincode query -C mychannel -n mycc -c '{"Args":["query","a"]}'
    ```
* 执行链码发交易函数：
  ```
  peer chaincode invoke -o orderer.example.com:7050 --tls true --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses peer0.org2.example.com:9051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt -c '{"Args":["invoke","b","a","10"]}'
  ```


## 五、附录

### 5.1、 链码安装
 * 进入go目录：`cd  $GOPATH`
 * 创建链码文件：`touch hello.go`
 * 编辑链码，添加内容（内容参考[案例](./mycc/src/hello.go)）： `vi hello.go`
 * 复制链码到cli容器:
   `docker cp ./hello.go cli:/opt/gopath/src/github.com/chaincode/lyg/`
   或根据容器id复制（可通过此命令查容器编号： docker ps -a|grep cli）：
   `docker cp ./hello.go 0ae20fa2f960:/opt/gopath/src/github.com/chaincode/lyg/`
 * 进入容器：`docker exec -it cli bash`
 * 安装链码：
   ```
   peer chaincode install -n hello -v 1.0 -p github.com/chaincode/lyg/
   ```
 * 实例化链码:
   ```
   peer chaincode instantiate -o orderer.example.com:7050 --tls true --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n hello -v 1.0 -c '{"Args":["init","a","100","b","200"]}' -P 'OR ('\''Org1MSP.peer'\'','\''Org2MSP.peer'\'')'
   ```
 * 链码调用:
   - 查询：
     ```
     peer chaincode query -C mychannel -n hello -c '{"Args":["query","a"]}'
     ```
   - 转账：
     ```
     peer chaincode invoke -C mychannel -n hello -c '{"Args":["invoke","a","b","10"]}' -o orderer.example.com:7050 --tls true --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

     ```