package hello;

import java.util.Date;

public class ChatMessage {
    private String emitter;
    private String content;
    private Date   emissionDate;

    public ChatMessage() {
    }

    public ChatMessage(String emitter, String content) {
        this.emitter      = emitter;
        this.content      = content;
        this.emissionDate = new Date();
    }

    public String getEmitter() {
        return emitter;
    }

    public String getContent() {
        return content;
    }

    public Long getEmissionDate() {
        return emissionDate.getTime();
    }
}
