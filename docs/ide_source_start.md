# 源码启动详细步骤

* 以下以 idea 为例，描述 tac 源码从 idea 启动步骤

### 代码拉取

```
git clone git@github.com:alibaba/tac.git
```

### 打开工程

* 项目通过 springboot 编写 依赖 jdk1.8
* 使用了 lombok 包，idea 需要安装 lombok 插件；

![undefined](/imgs/sourcestart/1527213111970-6a1b5031-63ef-4082-b602-4493555a40e8.png)

### 安装并启动 redis (本地默认配置)

* ip : 127.0.0.1
* port : 6379

### 启动 console

* com.alibaba.tac.console.ConsoleApplication 带上 --admin 参数启动

![undefined](/imgs/sourcestart/1527213201547-8d16dd54-d32a-4cd9-927a-4ceb509773a6.png)

* 成功后打开控制台 http://localhost:7001/#/tacMs/list

### 新建服务

![undefined](/imgs/sourcestart/1527213265713-e0e7611f-b1c2-43bd-8cf5-31dd0d9e9cc6.png)

### 编写代码

* 参考 tac-dev-source

![undefined](/imgs/sourcestart/1527213324287-63726690-1df1-45fb-afc6-e931784855d1.png)

```java
package com.alibaba.tac.biz.processor;

import com.alibaba.tac.sdk.common.TacResult;
import com.alibaba.tac.sdk.domain.Context;
import com.alibaba.tac.sdk.factory.TacInfrasFactory;
import com.alibaba.tac.sdk.handler.TacHandler;
import com.alibaba.tac.sdk.infrastracture.TacLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshuan.li
 */
public class HelloWorldTac implements TacHandler<Object> {

    /**
     * get the logger service
     */
    private TacLogger tacLogger = TacInfrasFactory.getLogger();


    /**
     * implement a class which implements TacHandler interface
     *  {@link TacHandler}
     * @param context
     * @return
     * @throws Exception
     */

    @Override
    public TacResult<Object> execute(Context context) throws Exception {

        // the code
        tacLogger.info("Hello World22");

        Map<String, Object> data = new HashMap<>();
        data.put("name", "hellotac");
        data.put("platform", "iPhone");
        data.put("clientVersion", "7.0.2");
        data.put("userName", "tac-userName");



        return TacResult.newResult(data);
    }
}
```

### 代码打包

```
cd tac-dev-source
mvn clean -Dmaven.test.skip=true package
```

### 上传 jar 包

![undefined](/imgs/sourcestart/1527213524357-bae645a8-d865-472d-a89d-c6660aeade07.png)

### 预发布

### 预发布测试

![undefined](/imgs/sourcestart/1527213630237-809d5801-c137-4e53-8709-3d4e772406df.png)

## 正式发布

### 运行

* com.alibaba.tac.container.ContainerApplication

### 控制台操作发布

![undefined](/imgs/sourcestart/1527213761239-b3548ce2-6f0d-406d-af8b-1efaf688a45d.png)
