# fabric节点前置服务
## 简介
fabric-front-gateway使用fabric-gateway-java接入fabric链，提供接口查询链信息，包括节点、区块、交易等。

## 注意
* 需要替换network/connection-org1.yaml,并将connection-org1.yaml文件中的localhost更改为实际ip
* 如果网络有重启过，要替换本工程的crypto-config目录,同时也要改application.yaml相应的配置
