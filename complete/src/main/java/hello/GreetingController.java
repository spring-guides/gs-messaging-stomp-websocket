package hello;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Random;

@Controller
public class GreetingController {


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Random rand = new Random();
        // String qq = Integer.toString(rand.nextInt(2));
        String data = null;
        boolean flag = (rand.nextInt(2) !=0);
        if(flag)
        {
        	data = "pass";
        }
        else
        {
        	data = "fail";
        }
        return new Greeting("Hello, " + message.getName() + "!"+data);
    }

}
