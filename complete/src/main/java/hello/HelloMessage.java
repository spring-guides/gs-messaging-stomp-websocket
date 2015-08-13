package hello;

public class HelloMessage {

    private String name;
    private String name2;

	public int countingvalue = 0 ;
	    
    public String getName() {
        return name;
    }

    public String getName2() {
        return name2;
    }

//  experimental set
    public void setCV(int value)
    {
    	this.countingvalue = value;
    }

    public int getCV()
    {
    	return countingvalue;
    }
}
