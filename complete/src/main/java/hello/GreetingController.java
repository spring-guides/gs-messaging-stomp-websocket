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
    public void greeting(HelloMessage message) throws Exception {
        System.out.println("--------> Got the message");
        Thread.sleep(3000); // simulated delay
        Greeting greeting = new Greeting("Hello, " + message.getName() + "!");
        System.out.println("--------> Sending a response");
        messagingTemplate.convertAndSend("/queue/greetings", greeting);
        System.out.println("--------> Sent the response");
    }

}
