# fabric节点前置服务
## 一、简介
fabric-front-gateway使用fabric-gateway-java接入fabric链，提供接口管理链码和查询链信息，包括节点、区块、交易等。

## 二、前提条件

| name | age |
|:----:|:----:|
| Java | JDK8或以上版本 |
| HyperLedger Fabric | V1.4.x以上版本 |

**备注：** 可参考[案例](./FABRIC_INSTALL.md )安装Fabric

## 三、安装
### 3.1. 拉取代码

```
git clone https://github.com/dwusiq/fabric-front-gateway.git
```

进入目录：

```
cd fabric-front-gateway
```

### 3.2. 编译代码

方式一：如果服务器已安装Gradle，且版本为Gradle-4.10或以上

```shell
gradle build -x test
```

方式二：如果服务器未安装Gradle，或者版本不是Gradle-4.10或以上，使用gradlew编译

```shell
chmod +x ./gradlew && ./gradlew build -x test
```

构建完成后，会在根目录fabric-front-gateway下生成已编译的代码目录dist。

### 3.3. 修改配置
* 替换证书目录./crypto-config
* 替换连接文件./network/connection-org1.yaml
  **备注：** 如果是按照前面的案例搭建网络，那么crypto-config目录和connection-orgx.yaml所在的位置是：
  ```shell
  cd $GOPATH/src/github.com/hyperledger/fabric-samples/first-network
  ```
* 修改配置`vi application.yml`（根据实际情况修改）：

  ```
  spring:
      fabric:
        organization:
          mspid: "Org1MSP"
        channel: mychannel
        gateway:
          wallet:
            identify: "Admin"
            certFile: "./crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem"
            privateKeyFile: "./crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/25de627907d35aae2c59b5ee131b4a328212437059029059a3d35a3c16722ffc_sk"
        network:
          file: "./network/connection-org1.yaml"
  ```


### 3.4. 配置host
由于fabric证书是颁发给节点域名的，sdk也是通过域名访问节点，所以需要配置host关联节点的ip
* centos服务器：vi /etc/hosts
* window host路径：C:\Windows\System32\drivers\etc
* host内容范例：
```
127.0.0.1   peer0.org1.example.com
127.0.0.1   peer1.org1.example.com
127.0.0.1   peer0.org2.example.com
127.0.0.1   peer1.org2.example.com
127.0.0.1   orderer.example.com
```

### 3.5. 服务启停

返回到dist目录执行：
```shell
启动: bash start.sh
停止: bash stop.sh
检查: bash status.sh
```
**备注**：服务进程起来后，需通过日志确认是否正常启动，出现以下内容表示正常；如果服务出现异常，确认修改配置后，重启提示服务进程在运行，则先执行stop.sh，再执行start.sh。

```
...
	Application() - main run success...
```

### 3.6. 访问swagger页面

```
http://{deployIP}:{frontPort}/Fabric-Front/swagger-ui.html
示例：http://localhost:8081/Fabric-Front/swagger-ui.html
```

- 部署服务器IP和服务端口需对应修改，网络策略需开通


### 3.7. 查看日志

在dist目录查看：

```
前置服务日志：tail -f log/fabric-front.log
```