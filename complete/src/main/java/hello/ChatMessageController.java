package hello;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatMessageController {
    @MessageMapping("/message")
    @SendTo("/chat_messages")
    public ChatMessage greeting(ChatMessage message) throws Exception {
        return new ChatMessage(message.getEmitter(), message.getContent());
    }

}
