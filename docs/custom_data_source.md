## 接入你自己的数据源

* 以下以 idea 为例，描述 tac 源码级别添加数据源步骤

### 代码拉取

```
git clone git@github.com:alibaba/tac.git
```

###

### 打开工程

* 为了方便大家理解，demo 模块加了 demo 字样；
* 在这里我们添加天猫商品服务(当然是 mock 的)

![image.png | left | 827x406](https://cdn.yuque.com/lark/0/2018/png/2827/1527908670979-41bc49e4-bee3-422b-9898-0089dfc9e3b8.png)

```java
package com.alibaba.tac.infrastracture.demo.itemcenter;

import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TmallItemService {

    /**
     * get a item
     *
     * @param id
     * @return
     */
    public ItemDO getItem(Long id) {

        // mock data  这里可以进行PRC、HTTP 调用  和自己的业务系统交互
        return new ItemDO(id, "A Song of Ice and Fire", "￥222.00");
    }

}
```

### 安装 jar 包到本地仓库

```plain
mvn clean -Dmaven.test.skip=true package install
```

### 在微服务里引用新的数据源

* 在 仍然以 tac-dev-source 为例 【注意】在新的 pom 文件中引入了刚刚打包的 jar 包 tac-custom-datasource-demo

![image.png | left | 827x339](https://cdn.yuque.com/lark/0/2018/png/2827/1527908958075-f5f22b21-87c0-4850-ac9e-2d48b6f8f4ca.png)

* 实例代码

```java
package com.alibaba.tac.biz.processor;

import com.alibaba.tac.sdk.common.TacResult;
import com.alibaba.tac.sdk.domain.Context;
import com.alibaba.tac.sdk.factory.TacInfrasFactory;
import com.alibaba.tac.sdk.handler.TacHandler;
import com.alibaba.tac.sdk.infrastracture.TacLogger;
import com.tmall.itemcenter.ItemDO;
import com.tmall.itemcenter.TmallItemService;

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

    private TmallItemService tmallItemService = TacInfrasFactory.getServiceBean(TmallItemService.class);

    /**
     * implement a class which implements TacHandler interface {@link TacHandler}
     *
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

        ItemDO item = tmallItemService.getItem(1L);
        data.put("item", item);
        return TacResult.newResult(data);
    }
}
```

### 从 IDEA 源码运行

* 在 tac-infrastructure 的 pom 文件中加入依赖 (理论上来说任意一个 pom 都行，保证在 classpath 里)

```xml
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>tac-custom-datasource-demo</artifactId>
            <version>0.0.4-SNAPSHOT</version>
        </dependency>
```

![image.png | left | 827x298](https://cdn.yuque.com/lark/0/2018/png/2827/1527909372611-d351cd8b-2429-4d3f-8ea9-e2dd1e172759.png)

* 修改加载路径，让新的 bean 能改被加载

![image.png | left | 799x289](https://cdn.yuque.com/lark/0/2018/png/2827/1527909790663-00749a83-9d99-43a1-a08c-3e2df8060507.png)

* 源码启动 console [参考](ide_source_start.md)

* 像打包 helloworld 一样打包新实例代码   mvn clean -Dmaven.test.skip=true package

* 正常预发布测试

![image.png | left | 827x284](https://cdn.yuque.com/lark/0/2018/png/2827/1527909925642-7c07329e-2a63-436e-8403-5a1bc87639a3.png)

### 从 Jar 包执行

* 为了实现对源码无侵入，tac 改造了 classloader 的顺序以支持从外部加载数据源的 jar 包；
* 只需将数据源 jar 包放入 extendlibs 中即可

![image.png | left | 827x276](https://cdn.yuque.com/lark/0/2018/png/2827/1527910365188-43a1a4c3-fb9b-4fa5-bbbb-3f7f514fc1b9.png)

![image.png | left | 766x296](https://cdn.yuque.com/lark/0/2018/png/2827/1527910444921-c5c32034-e174-431a-b7c8-59077c11577b.png)

* 运行 java -jar tac-console-0.0.4.jar --admin
