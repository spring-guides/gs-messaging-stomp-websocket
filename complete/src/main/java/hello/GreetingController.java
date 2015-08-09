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
       
       	// modified, add random prefix of pass or fail
        Random rand = new Random();
        String data = null;
        boolean flag = (rand.nextInt(2) !=0);
        if(flag)
        {
        	data = "pass";
        	// added here 
        	message.setCV(message.countingvalue+1);
        }
        else
        {
        	data = "fail";
        }

        String countingvalue_string = Integer.toString(message.countingvalue);
        // return new Greeting("Hello, " + message.getName() + "!"+data);
        String out_string1 = "Hello, " + message.getName() + message.getName2();
        return new Greeting(data,out_string1);

    }

}
