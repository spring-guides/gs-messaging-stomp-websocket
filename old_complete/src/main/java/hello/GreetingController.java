package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GreetingController {

    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public GreetingController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        System.out.println("==========================================================HERE");
    }


    @RequestMapping("/")
    public @ResponseBody String hello() {
        System.out.println("HEllloooo!!!");
        return "Hello!";
    }
    
    @MessageMapping("/app/hello")
    public void greeting(@RequestBody HelloMessage message) throws Exception {
        Thread.sleep(3000); // simulated delay
        Greeting greeting = new Greeting("Hello, " + message.getName() + "!");
        messagingTemplate.convertAndSend("/queue/greetings", greeting);
    }
}
