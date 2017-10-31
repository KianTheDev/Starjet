package me.thekian.data;

public enum Race 
{
    HUMAN("human"), COW("cow"), PIG("pig");
    private final String value;

    private Race(final String value) 
    {
        this.value = value;
    }

    public String getValue() 
    {
        return value;
    }
    
    public String toString()
    {
    	return String.valueOf(value);
    }
    
    public String toString2()
    {
    	String s = value.substring(0, 1).toUpperCase();
    	s += value.substring(1, value.length());
    	return s;
    }
}
