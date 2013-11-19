<#assign project_id="gs-messaging-stomp-websocket">

This guide walks you through the process of creating a "hello world" STOMP messaging server with Spring. 

What you'll build
-----------------

You'll build a server that will accept a message carrying a user's name. In response, it will push a greeting into a queue that the client is subscribed to.

What you'll need
----------------

 - About 15 minutes
 - <@prereq_editor_jdk_buildtools/>


## <@how_to_complete_this_guide/>


<a name="scratch"></a>
Set up the project
------------------

<@build_system_intro/>

<@create_directory_structure_hello/>


<@create_both_builds/>

<@bootstrap_starter_pom_disclaimer/>


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

    <@snippet path="src/main/java/hello/HelloMessage.java" prefix="complete"/>

Upon receiving the message and extracting the name, the service will process it by creating a greeting and publishing that greeting on a separate queue that the client is subscribed to. The greeting will also be a JSON object, which might look something like this:

    {
        "content": "Hello, Fred!"
    }

To model the greeting representation, you add another plain old Java object with a `content` property and corresponding `getContent()` method:

    <@snippet path="src/main/java/hello/Greeting.java" prefix="complete"/>

Spring will use the [Jackson JSON][jackson] library to automatically marshal instances of type `Greeting` into JSON.

Next, you'll create a controller to receive the hello message and send a greeting message.

Create a message-handling controller
------------------------------------

In Spring's approach to working with STOMP messaging, STOMP messages can be handled by a controller. These components are easily identified by the [`@Controller`][AtController] annotation, and the `GreetingController` below is mapped to handle messages published on the "/app/hello" destination.

    <@snippet path="src/main/java/hello/GreetingController.java" prefix="complete"/>

This controller is concise and simple, but there's plenty going on. Let's break it down step by step.

The [`@MessageMapping`][AtMessageMapping] annotation ensures that if a message is published on the "/app/hello" destination, then the `greeting()` method is called.

The payload of the message is bound to a `HelloMessage` object which is passed into `greeting()`. 

Internally, the implementation of the method simulates a processing delay by causing the thread to sleep for 3 seconds. This is to demonstrate that after the client sends a message, the server can take as long as it needs to process the message asynchronously.  The client may continue with whatever work it needs to do without waiting on the response.

After the 3 second delay, the `greeting()` method creates a new `Greeting` object, setting its content to say "Hello" to the name from the `HelloMessage`. It then calls `convertAndSend()` on the injected `SimpMessageSendingOperations` to send the `Greeting` on the "/queue/greetings" destination.

The `Greeting` object must be converted to JSON. Thanks to Spring's HTTP message converter support, you don't need to do this conversion manually. When you configure Spring for STOMP messaging, you'll inject `SimpMessagingTemplate` with an instance of [`MappingJackson2MessageConverter`][MappingJackson2MessageConverter]. It will be used to convert the `Greeting` instance to JSON.

Configure Spring for STOMP messaging
------------------------------------

Now that the essential components of the service are created, you can configure Spring to enable WebSocket and STOMP messaging.

Create a Java class named `WebSocketConfig` that looks like this:

    <@snippet path="src/main/java/hello/WebSocketConfig.java" prefix="complete"/>

`WebSocketConfig` is annotated with `@Configuration` to indicate that it is a Spring configuration class.
It is also annotated [`@EnableWebSocketMessageBroker`][AtEnableWebSocketMessageBroker].
As its name suggests, `@EnableWebSocketMessageBroker` enables a WebSocket message handling, backed by a message broker.

The `configureMessageBroker()` method overrides the default method in `WebSocketMessageBrokerConfigurer` to configure the message broker.
It starts by calling `enableSimpleBroker()` to enable a simple memory-based message broker to carry the greeting messages back to the client on destinations prefixed with "/queue".
It also designates the "/app" prefix for messages that are bound for `@MessageMapping`-annotated methods.

The `registerStompEndpoints()` method registers the "/hello" endpoint, enabling SockJS fallback options so that alternative messaging options may be used if WebSocket is not available.
This endpoint, when prefixed with "/app", is the endpoint that the `GreetingController.greeting()` method is mapped to handle.

Create a browser client
-----------------------

With the server side pieces in place, now let's turn our attention to the JavaScript client that will send messages to and receive messages from the server side.

Create an index.html file that looks like this:

    <@snippet path="src/main/resources/static/index.html" prefix="complete"/>

The main piece of this HTML file to pay attention to is the JavaScript code in the `connect()` and `sendName()` functions.

The `connect()` function uses [SockJS][SockJS] and [stomp.js][Stomp_JS] to open a connection to "/gs-messaging-stomp-websocket/hello", which is where `GreetingController` is waiting for connections. Upon a successful connection, it subscribes to the "/queue/greetings" destination, where the server will publish greeting messages. When a greeting appears on that queue, it will append a paragraph element to the DOM to display the greeting message.

The `sendName()` function retrieves the name entered by the user and uses the STOMP client to send it to the "/app/hello" destination (where `GreetingController.greeting()` will receive it).

Make the application executable
-------------------------------

Although it is possible to package this service as a traditional [WAR][u-war] file for deployment to an external application server, the simpler approach demonstrated below creates a standalone application. You package everything in a single, executable JAR file, driven by a good old Java `main()` method. Along the way, you use Spring's support for embedding the [Tomcat][u-tomcat] servlet container as the HTTP runtime, instead of deploying to an external instance.

### Create an Application class

    <@snippet path="src/main/java/hello/Application.java" prefix="complete"/>

The `main()` method defers to the [`SpringApplication`][] helper class, providing `Application.class` as an argument to its `run()` method. This tells Spring to read the annotation metadata from `Application` and to manage it as a component in the [Spring application context][u-application-context].

The `@ComponentScan` annotation tells Spring to search recursively through the `hello` package and its children for classes marked directly or indirectly with Spring's [`@Component`][] annotation. This directive ensures that Spring finds and registers the `GreetingController`, because it is marked with `@Controller`, which in turn is a kind of `@Component` annotation.

The [`@EnableAutoConfiguration`][] annotation switches on reasonable default behaviors based on the content of your classpath. For example, because the application depends on the embeddable version of Tomcat (tomcat-embed-core.jar), a Tomcat server is set up and configured with reasonable defaults on your behalf. And because the application also depends on Spring MVC (spring-webmvc.jar), a Spring MVC [`DispatcherServlet`][] is configured and registered for you — no `web.xml` necessary! Auto-configuration is a powerful, flexible mechanism. See the [API documentation][`@EnableAutoConfiguration`] for further details.

<@build_an_executable_jar_subhead/>

<@build_an_executable_jar_with_both/>

<@run_the_application_with_both module="service"/>

Logging output is displayed. The service should be up and running within a few seconds.

Test the service
----------------

Now that the service is running, point your browser at http://localhost:8080 and click the "Connect" button.

Upon opening a connection, you are asked for your name. Enter your name and click "Send". Your name is sent to the server as a JSON message over STOMP. After a 3-second simulated delay, the server sends a message back with a "Hello" greeting that is displayed on the page. At this point, you can send another name, or you can click the "Disconnect" button to close the connection.


Summary
-------

Congratulations! You've just developed a STOMP-based messaging service with Spring. 


[u-rest]: /understanding/rest
[u-json]: /understanding/json
[jackson]: http://wiki.fasterxml.com/JacksonHome
[MappingJackson2MessageConverter]: http://static.springsource.org/spring/docs/4.0.x/javadoc-api/org/springframework/messaging/support/converter/MappingJackson2MessageConverter.html
[AtController]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/stereotype/Controller.html
[AtEnableWebSocket]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/web/socket/server/config/EnableWebSocket.html
[AtEnableWebSocketMessageBroker]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/messaging/simp/config/EnableWebSocketMessageBroker.html
[AtMessageMapping]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/messaging/handler/annotation/MessageMapping.html
[AtController]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/stereotype/Controller.html
.html
[SockJS]: https://github.com/sockjs
[Stomp_JS]: http://jmesnil.net/stomp-websocket/doc/

