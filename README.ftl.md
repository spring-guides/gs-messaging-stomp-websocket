<#assign project_id="gs-rest-service">

# Getting Started: Messaging with WebSocket and STOMP

This guide walks you through creating a "hello world" STOMP messaging server with Spring. 

What you'll build
-----------------

The server will accept a message carying the user's name. In response, it will push a greeting into a queue that the client is subscribed to.

What you'll need
----------------

 - About 15 minutes
 - Tomcat 8 ([Tomcat 8.0.0-RC1][tomcat8] is available)
 - <@prereq_editor_jdk_buildtools/>


## <@how_to_complete_this_guide/>


<a name="scratch"></a>
Set up the project
------------------

<@build_system_intro/>

<@create_directory_structure_hello/>

### Create a Maven POM

    <@snippet path="pom.xml" prefix="initial"/>

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

To model the greeting representation, you another plain old Java object with a `content` property and corresponding `getContent()` method:

    <@snippet path="src/main/java/hello/Greeting.java" prefix="complete"/>

> **Note:** As you see in steps below, Spring uses the [Jackson JSON][jackson] library to automatically marshal instances of type `Greeting` into JSON.

Next, you'll create the controller to receive the hello message and send a greeting message.

Create a message-handling controller
------------------------------------

In Spring's approach to working with STOMP messaging, STOMP messages can be handled by a controller. These components are easily identified by the [`@Controller`][AtController] annotation, and the `GreetingController` below is mapped to handle messages published on the "/app/hello" destination.

    <@snippet path="src/main/java/hello/GreetingController.java" prefix="complete"/>

This controller is concise and simple, but there's plenty going on under the hood. Let's break it down step by step.

The `@MessageMapping` annotation ensures that if a message is published on the "/app/hello" destination, then the `greeting()` method will called.

`@RequestBody` binds the payload of the message to a `HelloMessage` object which is passed into `greeting()`. 

Internally, the implementation of the method simulates a processing delay by causing the thread to sleep for 3 seconds. This is to demonstrate that after the client sends a message, the server can take as long as it needs to process the message asynchronously.  The client may continue with whatever work it needs to do without waiting on the response.

After the 3 second delay, the `greeting()` method creates a new `Greeting` object, setting its content to say "Hello" to the name from the `HelloMessage`. It then calls `convertAndSend()` on the injected `SimpMessageSendingOperations` to send the `Greeting` on the "/queue/greetings" destination.

The `Greeting` object must be converted to JSON. Thanks to Spring's HTTP message converter support, you don't need to do this conversion manually. When you configure Spring for STOMP messaging, you'll inject `SimpMessagingTemplate` with an instance of [`MappingJackson2MessageConverter`][MappingJackson2MessageConverter]. It will be used to convert the `Greeting` instance to JSON.

Configuring Spring for STOMP messaging
--------------------------------------

TODO: This is extremely ugly at the moment, with many beans in play. Rossen says that SPR-10835 will be resolved in time for RC1, so fill in this section then.


Make the application executable
-------------------------------

In order to deploy the application to Tomcat, you'll need to add a bit more configuration.

First, you'll need to configure Spring's [`DispatcherServlet`][DispatcherServlet] to serve static resources so that it will serve index.html. This can be done by creating a configuration class that overrides the `configureDefaultServletHandling()` method of `WebMvcConfigurerAdapter` and calls `enable()` on the given `DefaultServletHandlerConfigurer`:

    <@snippet path="src/main/java/hello/WebConfig.java" prefix="complete"/>

You'll also need to configure `DispatcherServlet`. This is most easily done by creating a class that extends `AbstractAnnotationConfigDispatcherServletInitializer`:

    <@snippet path="src/main/java/hello/HelloServletInitializer.java" prefix="complete"/>

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
[`DispatcherServlet`]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html
