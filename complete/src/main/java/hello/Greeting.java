package hello;

public class Greeting {
    
    private String content;
    // qqqqq is a the second property 
    private String qqqqq;

    // public Greeting(String content) {
    //     this.content = content;
    // }

    public Greeting(String content, String qqqqq) {
        this.content = content;
        this.qqqqq = qqqqq;
    }

    public String getContent() {
        return content;
    }


}
