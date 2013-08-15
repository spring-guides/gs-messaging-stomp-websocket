
# Getting Started: Messaging with WebSocket and STOMP

This guide walks you through creating a "hello world" STOMP messaging server with Spring. 

What you'll build
-----------------

The server will accept a message carying the user's name. In response, it will push a greeting into a queue that the client is subscribed to.

What you'll need
----------------

 - About 15 minutes
 - Tomcat 8 ([Tomcat 8.0.0-RC1][tomcat8] is available)
 - A favorite text editor or IDE
 - [JDK 6][jdk] or later
 - [Maven 3.0][mvn] or later

[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[mvn]: http://maven.apache.org/download.cgi


How to complete this guide
--------------------------

Like all Spring's [Getting Started guides](/guides/gs), you can start from scratch and complete each step, or you can bypass basic setup steps that are already familiar to you. Either way, you end up with working code.

To **start from scratch**, move on to [Set up the project](#scratch).

To **skip the basics**, do the following:

 - [Download][zip] and unzip the source repository for this guide, or clone it using [Git][u-git]:
`git clone https://github.com/springframework-meta/gs-rest-service.git`
 - cd into `gs-rest-service/initial`.
 - Jump ahead to [Create a resource representation class](#initial).

**When you're finished**, you can check your results against the code in `gs-rest-service/complete`.
[zip]: https://github.com/springframework-meta/gs-rest-service/archive/master.zip
[u-git]: /understanding/Git


<a name="scratch"></a>
Set up the project
------------------

First you set up a basic build script. You can use any build system you like when building apps with Spring, but the code you need to work with [Maven](https://maven.apache.org) and [Gradle](http://gradle.org) is included here. If you're not familiar with either, refer to [Building Java Projects with Maven](/guides/gs/maven) or [Building Java Projects with Gradle](/guides/gs/gradle/).

### Create the directory structure

In a project directory of your choosing, create the following subdirectory structure; for example, with `mkdir -p src/main/java/hello` on *nix systems:

    └── src
        └── main
            └── java
                └── hello

### Create a Maven POM

`pom.xml`
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.springframework.samples</groupId>
    <artifactId>gs-messaging-stomp-websocket-initial</artifactId>
    <packaging>war</packaging>
    <version>0.1.0</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>4.0.0.BUILD-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-messaging</artifactId>
            <version>4.0.0.BUILD-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-websocket</artifactId>
            <version>4.0.0.BUILD-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.2.2</version>
        </dependency>
        <dependency>
            <groupId>javax.websocket</groupId>
            <artifactId>javax.websocket-api</artifactId>
            <version>1.0-rc5</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.1-b09</version>
            <scope>provided</scope>
        </dependency>
        
        
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.6.4</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>jcl-over-slf4j</artifactId>
            <version>1.6.4</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.6.4</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.16</version>
            <scope>runtime</scope>
        </dependency>
        
        <dependency>
            <groupId>org.projectreactor</groupId>
            <artifactId>reactor-core</artifactId>
            <version>1.0.0.M1</version>
        </dependency>

        <!-- Required when the "stomp-broker-relay" profile is enabled -->
        <dependency>
            <groupId>org.projectreactor</groupId>
            <artifactId>reactor-tcp</artifactId>
            <version>1.0.0.M1</version>
        </dependency>
        

        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.1</version>
            <exclusions>
                <exclusion>
                    <groupId>javax.servlet</groupId>
                    <artifactId>servlet-api</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        
    </dependencies>

    <repositories>
        <repository>
            <id>spring-snapshots</id>
            <url>http://repo.springsource.org/libs-snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>tomcat-snapshots</id>
            <url>https://repository.apache.org/content/repositories/snapshots</url>
            <snapshots><enabled>true</enabled></snapshots>
            <releases><enabled>false</enabled></releases>
        </repository>
        <repository>
            <id>java-net-snapshots</id>
            <url>https://maven.java.net/content/repositories/snapshots</url>
            <snapshots><enabled>true</enabled></snapshots>
            <releases><enabled>false</enabled></releases>
        </repository>
    </repositories>

    <build>
        <finalName>${project.artifactId}</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.3.2</version>
                <configuration>
                    <source>1.7</source>
                    <target>1.7</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>2.2</version>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```


<a name="initial"></a>
Create a resource representation class
--------------------------------------

Now that you've set up the project and build system, you can create your STOMP message service.

Begin the process by thinking about service interactions.

The service will accept messages containing a name in a STOMP message whose body is a [JSON][u-json] object. If the name given is "Fred", then the message might look something like this:

    {
        "name": "Fred"
    }

To model the message carrying the name, you can create a plain old Java object with a `name` property and a corresponding `getName()` method:

`src/main/java/hello/HelloMessage.java`
```java
package hello;

public class HelloMessage {

    private String name;
    
    public String getName() {
        return name;
    }

}
```

Upon receiving the message and extracting the name, the service will process it by creating a greeting and publishing that greeting on a separate queue that the client is subscribed to. The greeting will also be a JSON object, which might look something like this:

    {
        "content": "Hello, Fred!"
    }

To model the greeting representation, you another plain old Java object with a `content` property and corresponding `getContent()` method:

`src/main/java/hello/Greeting.java`
```java
package hello;

public class Greeting {
    
    private String content;

    public Greeting(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
```

> **Note:** As you see in steps below, Spring uses the [Jackson JSON][jackson] library to automatically marshal instances of type `Greeting` into JSON.

Next, you'll create the controller to receive the hello message and send a greeting message.

Create a message-handling controller
------------------------------------

In Spring's approach to working with STOMP messaging, STOMP messages can be handled by a controller. These components are easily identified by the [`@Controller`][AtController] annotation, and the `GreetingController` below is mapped to handle messages published on the "/app/hello" destination.

`src/main/java/hello/GreetingController.java`
```java
package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class GreetingController {

    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public GreetingController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/app/hello")
    public void greeting(@RequestBody HelloMessage message) throws Exception {
        Thread.sleep(3000); // simulated delay
        Greeting greeting = new Greeting("Hello, " + message.getName() + "!");
        messagingTemplate.convertAndSend("/queue/greetings", greeting);
    }
}
```

This controller is concise and simple, but there's plenty going on under the hood. Let's break it down step by step.

The `@MessageMapping` annotation ensures that if a message is published on the "/app/hello" destination, then the `greeting()` method will called.

`@RequestBody` binds the payload of the message to a `HelloMessage` object which is passed into `greeting()`. 

Internally, the implementation of the method simulates a processing delay by causing the thread to sleep for 3 seconds. This is to demonstrate that after the client sends a message, the server can take as long as it needs to process the message asynchronously.  The client may continue with whatever work it needs to do without waiting on the response.

After the 3 second delay, the `greeting()` method creates a new `Greeting` object, setting its content to say "Hello" to the name from the `HelloMessage`. It then calls `convertAndSend()` on the injected `SimpMessageSendingOperations` to send the `Greeting` on the "/queue/greetings" destination.

The `Greeting` object must be converted to JSON. Thanks to Spring's HTTP message converter support, you don't need to do this conversion manually. When you configure Spring for STOMP messaging, you'll inject `SimpMessagingTemplate` with an instance of [`MappingJackson2MessageConverter`][MappingJackson2MessageConverter]. It will be used to convert the `Greeting` instance to JSON.

Configure Spring for STOMP messaging
------------------------------------

TODO: What follows is the configuration for the STOMP/WebSocket-specific piece.

`src/main/java/hello/StompConfig.java`
```java
package hello;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.websocket.SubProtocolWebSocketHandler;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.handler.AnnotationMethodMessageHandler;
import org.springframework.messaging.simp.handler.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.handler.SimpleUserQueueSuffixResolver;
import org.springframework.messaging.simp.handler.UserDestinationMessageHandler;
import org.springframework.messaging.simp.stomp.StompProtocolHandler;
import org.springframework.messaging.support.channel.ExecutorSubscribableChannel;
import org.springframework.messaging.support.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.support.converter.MessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsHttpRequestHandler;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.socket.sockjs.transport.handler.DefaultSockJsService;

@Configuration
public class StompConfig {
    private final MessageConverter<?> messageConverter = new MappingJackson2MessageConverter();

    private final SimpleUserQueueSuffixResolver userQueueSuffixResolver = new SimpleUserQueueSuffixResolver();


    @Bean
    public SimpleUrlHandlerMapping handlerMapping() {

        SockJsService sockJsService = new DefaultSockJsService(taskScheduler());
        HttpRequestHandler requestHandler = new SockJsHttpRequestHandler(sockJsService, webSocketHandler());

        SimpleUrlHandlerMapping hm = new SimpleUrlHandlerMapping();
        hm.setOrder(-1);
        hm.setUrlMap(Collections.singletonMap("/hello/**", requestHandler));
        return hm;
    }

    // WebSocketHandler supporting STOMP messages

    @Bean
    public WebSocketHandler webSocketHandler() {

        StompProtocolHandler stompHandler = new StompProtocolHandler();
        stompHandler.setUserQueueSuffixResolver(this.userQueueSuffixResolver);

        SubProtocolWebSocketHandler webSocketHandler = new SubProtocolWebSocketHandler(dispatchChannel());
        webSocketHandler.setDefaultProtocolHandler(stompHandler);
        webSocketHandlerChannel().subscribe(webSocketHandler);

        return webSocketHandler;
    }

    // MessageHandler for processing messages by delegating to @Controller annotated methods

    @Bean
    public AnnotationMethodMessageHandler annotationMessageHandler() {

        AnnotationMethodMessageHandler handler =
                new AnnotationMethodMessageHandler(dispatchMessagingTemplate(), webSocketHandlerChannel());

        handler.setDestinationPrefixes(Arrays.asList("/app/"));
        handler.setMessageConverter(this.messageConverter);
        dispatchChannel().subscribe(handler);
        return handler;
    }

    // MessageHandler that acts as a "simple" message broker
    // See DispatcherServletInitializer for enabling/disabling the "simple-broker" profile

    @Bean
    public SimpleBrokerMessageHandler simpleBrokerMessageHandler() {
        SimpleBrokerMessageHandler handler = new SimpleBrokerMessageHandler(webSocketHandlerChannel());
        handler.setDestinationPrefixes(Arrays.asList("/topic/", "/queue/"));
        dispatchChannel().subscribe(handler);
        return handler;
    }

    // MessageHandler that resolves destinations prefixed with "/user/{user}"
    // See the Javadoc of UserDestinationMessageHandler for details

    @Bean
    public UserDestinationMessageHandler userMessageHandler() {
        UserDestinationMessageHandler handler = new UserDestinationMessageHandler(
                dispatchMessagingTemplate(), this.userQueueSuffixResolver);
        dispatchChannel().subscribe(handler);
        return handler;
    }

    // MessagingTemplate (and MessageChannel) to dispatch messages to for further processing
    // All MessageHandler beans above subscribe to this channel

    @Bean
    public SimpMessageSendingOperations dispatchMessagingTemplate() {
        SimpMessagingTemplate template = new SimpMessagingTemplate(dispatchChannel());
        template.setMessageConverter(this.messageConverter);
        return template;
    }

    @Bean
    public SubscribableChannel dispatchChannel() {
        return new ExecutorSubscribableChannel(asyncExecutor());
    }

    // Channel for sending STOMP messages to connected WebSocket sessions (mostly for internal use)

    @Bean
    public SubscribableChannel webSocketHandlerChannel() {
        return new ExecutorSubscribableChannel(asyncExecutor());
    }

    // Executor for message passing via MessageChannel

    @Bean
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setCorePoolSize(8);
        executor.setThreadNamePrefix("MessageChannel-");
        return executor;
    }

    // Task executor for use in SockJS (heartbeat frames, session timeouts)

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("SockJS-");
        taskScheduler.setPoolSize(4);
        return taskScheduler;
    }

}
```

TODO: This is extremely ugly at the moment, with many beans in play. Rossen says that SPR-10835 will be resolved in time for RC1. There's no need to write this section to describe all of these beans now. It's better to hold off and wait for SPR-10835 to be resolved and then adjust the configuration accordingly and write about that. At that time, assuming the solution is simple enough, the configuration in StompConfig.java can be merged back into WebConfig.java to have only a single configuration class.

Create a Browser Client
-----------------------

With the server side pieces in place, now let's turn our attention to the JavaScript client that will send messages to and receive messages from the server side.

Create an index.html file that looks like this:

`src/main/webapp/index.html`
```html
<!DOCTYPE html>
<html>
<head>
    <title>Hello WebSocket</title>
    <script src="sockjs-0.3.4.js"></script>
    <script src="stomp.js"></script>
    <script type="text/javascript">
        var stompClient = null;
        
        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }
        
        function connect() {
            var socket = new SockJS('/gs-messaging-stomp-websocket/hello');
            stompClient = Stomp.over(socket);            
            stompClient.connect('', '', function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/queue/greetings', function(greeting){
                    showGreeting(JSON.parse(greeting.body).content);
                });
            });
        }
        
        function disconnect() {
            stompClient.disconnect();
            setConnected(false);
            console.log("Disconnected");
        }
        
        function sendName() {
            var name = document.getElementById('name').value;
            stompClient.send("/app/hello", {}, JSON.stringify({ 'name': name }));
        }
        
        function showGreeting(message) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            response.appendChild(p);
        }
    </script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div>
    <div>
        <button id="connect" onclick="connect();">Connect</button>
        <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
    </div>
    <div id="conversationDiv">
        <label>What is your name?</label><input type="text" id="name" />
        <button id="sendName" onclick="sendName();">Send</button>
        <p id="response"></p>
    </div>
</div>
</body>
</html>
```

The main piece of this HTML file to pay attention to is the JavaScript code in the `connect()` and `sendName()` functions.

The `connect()` function uses [SockJS][SockJS] and [stomp.js][Stomp_JS] to open a connection to "/gs-messaging-stomp-websocket/hello", which is where `GreetingController` is waiting for connections. Upon a successful connection, it subscribes to the "/queue/greetings" destination, where the server will publish greeting messages. When a greeting appears on that queue, it will append a paragraph element to the DOM to display the greeting message.

The `sendName()` function retrieves the name entered by the user and uses the STOMP client to send it to the "/app/hello" destination (where `GreetingController.greeting()` will receive it).

Make the application executable
-------------------------------

In order to deploy the application to Tomcat, you'll need to add a bit more configuration.

First, you'll need to configure Spring's [`DispatcherServlet`][DispatcherServlet] to serve static resources so that it will serve index.html. This can be done by creating a configuration class that overrides the `configureDefaultServletHandling()` method of `WebMvcConfigurerAdapter` and calls `enable()` on the given `DefaultServletHandlerConfigurer`:

`src/main/java/hello/WebConfig.java`
```java
package hello;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan
@Import(StompConfig.class)
public class WebConfig extends WebMvcConfigurerAdapter {
    
    // Allow serving HTML files through the default Servlet
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}
```

You'll also need to configure `DispatcherServlet`. This is most easily done by creating a class that extends `AbstractAnnotationConfigDispatcherServletInitializer`:

`src/main/java/hello/HelloServletInitializer.java`
```java
package hello;

import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class HelloServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected void customizeRegistration(Dynamic registration) {
        registration.setInitParameter("dispatchOptionsRequest", "true");
    }

}
```

Here, `DispatcherServlet` is mapped to "/". Also, `WebConfig` and `EndpointConfig` are specified, respectively, as the servlet and root configuration classes.

Now you're ready to build and deploy the application to Tomcat 8. Start by building the WAR file:

```sh
mvn package
```

Then copy the WAR file to Tomcat 8's `trunk/output/webapps` directory. 

Finally, restart Tomcat 8:

```sh
output/bin/shutdown.sh
output/bin/startup.sh
```

After the application starts, point your browser at http://localhost:8080/gs-messaging-stomp-websocket and click the "Connect" button.

Upon opening a connection, you will be asked for your name. Enter your name and click "Send". Your name will be sent to the server as a JSON message over STOMP. The server will send a message back with a "Hello" greeting that will be displayed on the page. At this point, you may choose to send another name or you can click the "Disconnect" button to close the connection.


Summary
-------

Congratulations! You've just developed a STOMP-based messaging service with Spring. 


[tomcat8]: http://tomcat.apache.org/download-80.cgi
[u-rest]: /understanding/rest
[u-json]: /understanding/json
[jackson]: http://wiki.fasterxml.com/JacksonHome
[MappingJackson2MessageConverter]: http://static.springsource.org/spring/docs/4.0.x/javadoc-api/org/springframework/messaging/support/converter/MappingJackson2MessageConverter.html
[`AtController`]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/stereotype/Controller.html
[DispatcherServlet]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html
[SockJS]: https://github.com/sockjs
[Stomp_JS]: http://jmesnil.net/stomp-websocket/doc/

