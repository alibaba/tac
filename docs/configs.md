# TAC 配置

* TAC 使用 springboot 构建，可是用 springboot 的标准配置文件来替换其默认配置；
* 如 启动参数 --spring.config.location=file:/override.properties

## 通用配置

```properties
# http 服务器端口

server.port=8001

# endpoint 配置

management.port=8002

# 使用的存储 redis
tac.default.store=redis

# 扩展点扫描的包名 逗号分隔
scan.package.name=com.tmall.tac.test

# 扩展jar包路径
tac.extend.lib=extendlibs

# 日志路径
logging.config=classpath:tac/default-logback-spring.xml


# 编译相关

# 参考类 TacDataPathProperties
# 编译结果路径 默认值 不建议修改
tac.data.path.outputPathPrefix=${user.home}/tac/data/classes

# 运行时加载的类路径  不建议修改
tac.data.path.classLoadPathPrefix=${user.home}/tac/data/ms

# 编译代码的包名 修改成自定义包名
tac.data.path.pkgPrefix=com.alibaba.tac.biz;

# redis存储相关配置 参考类 TacRedisConfigProperties  以下配置不建议修改
# msInst元数据路径
tac.redis.config.msInstMetaDataPath=com.alibaba.tac.msInstMetaData
# ms元数据路径
tac.redis.config.msMetaDataPath=com.alibaba.tac.msMetaData

# 数据路径的前缀
tac.redis.config.dataPathPrefix=msInstFile


# 服务列表的路径
tac.redis.config.msListPath=msPublishedList

# 发布时候订阅的channel
tac.redis.config.publishEventChannel=tac.inst.publish.channel



```

## tac-console 配置

```properties
# tac-container的http接口 在线上验证时使用
tac.container.web.api=http://localhost:8001/api/tac/execute
```


## gitlab配置

```properties
# gitlab服务器地址
tac.gitlab.config.hostURL=http://127.0.0.1

# gitlab帐号token
tac.gitlab.config.token=xxxxx

# gitlab仓库groupName
tac.gitlab.config.groupName=tac-admin


# gitlab仓库帐号名
tac.gitlab.config.userName=tac-admin

# gitlab仓库帐号密码
tac.gitlab.config.password=tac-admin


# gitlab代码下载存储路径 （各微服务代码会下载到这个路径下）
tac.gitlab.config.basePath=/home/admin/tac/git_codes
```