#  RabbitMQ

### 2.1 搜索与商品服务的问题

目前我们已经完成了商品详情和搜索系统的开发

1. 商品的原始数据保存在数据库中，CRUD都在xxx项目中完成
2. 搜索服务数据来源是es索引库，由search项目维护
3. 商品详情做了页面静态化，静态页面由template项目生成维护

如果我们在后台新增了商品。或者修改，删除商品，那么默认操作的是数据库，而es和template不会默 认更新索引库和静态页面

这里有两种解决方案：

1. 方案1：每当后台对商品做增删改操作，同时要修改索引库数据及静态页面
2. 方案2：搜索服务和商品页面服务对外提供操作接口，后台在商品增删改后，调用feign接口

以上两种方式都有同一个严重问题：就是代码耦合，后台服务中需要嵌入搜索和商品页面服务，违背了 微服务的 独立 原则。 所以，我们会通过另外一种方式来解决这个问题：消息队列

### 2.2 消息队列（MQ）

#### 2.2.1 什么是消息队列

消息队列，即MQ，Message Queue。

消息队列是典型的：生产者、消费者模型。生产者不断向消息队列中生产消息，消费者不断的从队列中 获取消息。因为消息的生产和消费都是异步的，而且只关心消息的发送和接收，没有业务逻辑的侵入， 这样就实现了生产者和消费者的解耦。

结合前面所说的问题：

1. 商品服务对商品增删改以后，无需去操作索引库(es)或静态页面(template)，只是发送一条消息， 也不关心消息被谁接收。
2. 搜索服务和静态页面服务接收消息，分别去处理索引库的更新和静态页面的创建覆盖。

#### 2.2.2 AMQP和JMS

MQ是消息通信的模型，并发具体实现。现在实现MQ的有两种主流方式：AMQP、JMS。

两者间的区别和联系：

1. JMS是定义了统一的接口，来对消息操作进行统一；AMQP是通过规定协议来统一数据交互的格式 
2. JMS限定了必须使用Java语言;AMQP只是协议，不规定实现方式，因此是跨语言的。
3. JMS规定了两种消息模型而AMQP的消息模型更加丰富

#### 2.2.3 常见MQ产品

1. ActiveMQ：基于JMS
2. RabbitMQ：基于AMQP协议，
3. erlang语言开发，稳定性好 
4. RocketMQ：基于JMS，阿里巴巴产品，目前交由Apache基金会
5. Kafka：分布式消息系统，高吞吐量
6. ZeroMQ，IBM WebSphere

#### 2.2.4 RabbitMQ

RabbitMQ是基于AMQP的一款消息管理系统

官网： http://www.rabbitmq.com/ 

官方教程：http://www.rabbitmq.com/getstarted.html

RabbitMQ基于Erlang语言开发：

### 2.3 下载和安装

开始学习https://www.rabbitmq.com/getstarted.html 

RabbitMQ提供了7种消息模型

 1、2是队列 模型 

3、4、5是订阅，交换机模型

 6、是rpc回调模型 

7、是确认模型

**新建demo项目**

 **pom.xm**l

```
	<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.1.RELEASE</version>
    </parent>

    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
    </dependencies>
```

**新建包com.mr.rabbitmq.utils**

**在utils包下新建RabbitmqConnectionUtil**

```
package com.mr.rabbitmq.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitmqConnectionUtil {

    public static Connection getConnection() throws Exception{
        //定义rabbitmq连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置超时时间
        factory.setConnectionTimeout(60000);
        //设置服务ip
        factory.setHost("127.0.0.1");
        //设置端口5672
        factory.setPort(5672);
        //设置，用户名、密码、虚拟主机
        factory.setUsername("guest");
        factory.setPassword("guest");
        // 创建连接，根据工厂
        Connection connection = factory.newConnection();
        return connection;
    }

}
```

### 2.4.1 基本消息模型

**在com.mr.rabbitmq下新建simple包**

**包下新建SendMessage**

```
package com.mr.rabbitmq.simple;

import com.mr.rabbitmq.utils.RabbitmqConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class SendMessage {
    //序列名称
    private final static String QUEUE_NAME = "simple_queue";
    public static void main(String[] arg) throws Exception {
        // 获取到连接
        Connection connection = RabbitmqConnectionUtil.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        /*
        param1:队列名称
        param2: 是否持久化
        param3: 是否排外
        param4: 是否自动删除
        param5: 其他参数
        */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    //发送的消息内容
        String message = "good good study";
        /*
        param1: 交换机名称
        param2: routingKey
        param3: 一些配置信息
        param4: 发送的消息
        */
        //发送消息
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" 消息发送 '" + message + "' 到队列 success");
        // 关闭通道和连接
        channel.close();
        connection.close();
    }
}

```

channel.queueDeclare(QUEUE_NAME, false, false, false, null);

- param1: 队列名字

- param2: durable

  是否持久化

  是否持久化, 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失，如果想重启之 后还存在就要使队列持久化，保存到Erlang自带的Mnesia数据库中，当rabbitmq重启之后会 读取该数据库

- param3: exclusive

  是否排外

   是否排外的，有两个作用，一：当连接关闭时connection.close()该队列是否会自动删除； 二：该队列是否是私有的private，如果不是排外的，可以使用两个消费者都访问同一个队 列，没有任何问题，如果是排外的，会对当前队列加锁，其他通道channel是不能访问的，如 果强制访问会报异常：com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method(reply-code=405, replytext=RESOURCE_LOCKED - cannot obtain exclusive access to locked queue 'queue_name' in vhost '/', class-id=50, method-id=20) 一般等于true的话用于一个 队列只能有一个消费者来消费的场景

- param4 : autoDelete

  是否自动删除队列，当最后一个消费者断开连接之后队列是否自动被删除，可以通过 RabbitMQ Management，查看某个队列的消费者数量，当consumers = 0时队列就会自动 删除

- param5: 相关参数

  

运行main函数

浏览器打开ip:15672 

可以看到消息队列中已经有消息了

在simple包下新建Receive

```
package com.mr.rabbitmq.simple;


import com.mr.rabbitmq.utils.RabbitmqConnectionUtil;
import com.rabbitmq.client.*;

import javax.sound.midi.Soundbank;
import java.io.IOException;

public class Receive {
    private final static String QUEUE_NAME = "simple_queue";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitmqConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                //System.out.println(1/0);
                System.out.println(new String(body, "UTF-8"));
                //手动确认消息已经收到
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };

        /*
        param1 : 队列名称
        param2 : 是否自动确认
        param3 : 消费者
         */
        channel.basicConsume(QUEUE_NAME, false, defaultConsumer);
    }
}
```

执行main函数

可以看到消息队列中的数据已经被消费完了

消费者的消息确认机制(Acknowlage)

通过刚才的案例可以看出，消息一旦被消费者接收，队列中的消息就会被删除。 那么问题来了：RabbitMQ怎么知道消息被接收了呢？ 这就要通过消息确认机制（Acknowlege）来实现了。当消费者获取消息后，会向RabbitMQ发送回执 ACK，告知消息已经被接收。不过这种回执ACK分两种情况：

1. 自动ACK：消息一旦被接收，消费者自动发送ACK
2. 手动ACK：消息接收后，不会发送ACK，需要手动调用

大家觉得哪种更好呢？

这需要看消息的重要性：

1. 如果消息不太重要，丢失也没有影响，那么自动ACK会比较方便 
2. 如果消息非常重要，不允许丢失。那么最好在消费完成后手动ACK，否则接收消息后就自动ACK，
3. RabbitMQ就会把消息从队列中删除。如果此时消费者宕机，那么消息就丢失了。

我们之前的测试都是自动ACK的，如果要手动ACK，需要改动我们的代码：

```
import com.mr.rabbitmq.utils.RabbitmqConnectionUtil;
import com.rabbitmq.client.*;
import java.io.IOException;
public class ReceiveACK {

    //队列名称
    private final static String QUEUE_NAME = "simple_queue";
	
	public static void main(String[] arg) throws Exception {
		// 获取连接
        Connection connection = RabbitmqConnectionUtil.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
        	// 监听队列中的消息，如果有消息，进行处理
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
            byte[] body) throws IOException {
            	// body： 消息中参数信息
                String msg = new String(body);
                System.out.println(" 收到消息，执行中 : " + msg + "!");
                // System.out.println(1 /0 );
                /*
                param1 : （唯一标识 ID）
                param2 : 是否进行批处理
                */
                channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
        /*
        param1 : 队列名称
        param2 : 是否自动确认消息
        param3 : 消费者
        */
        channel.basicConsume(QUEUE_NAME, false, consumer);
        //消费者需要时时监听消息，不用关闭通道与连接
		
	}	

}
```

### 2.4.2 work 消息模型

**说明**

在刚才的基本模型中，一个生产者，一个消费者，生产的消息直接被消费者消费。比较简单。 Work queues，也被称为（Task queues），任务模型。 当消息处理比较耗时的时候，可能生产消息的速度会远远大于消息的消费速度。长此以往，消息就会堆 积越来越多，无法及时处理。此时就可以使用work 模型：让多个消费者绑定到一个队列，共同消费队 列中的消息。队列中的消息一旦消费，就会消失，因此任务是不会被重复执行的

**角色**： 

- P：生产者：任务的发布者 
- C1：消费者，领取任务并且完成任务，假设完成速度较慢 
- C2：消费者2：领取任务并完成任务，假设完成速度快