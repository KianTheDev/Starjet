package me.thekian.data;

public enum PlayerClass 
{
    FIGHTER("fighter"), RANGER("ranger"), MAGICIAN("magician"), 
    TINKERER("tinkerer"), MARINE("marine"), SNIPER("sniper"), 
    TECHNOMANCER("technomancer"), ENGINEER("engineer");
    private final String value;

    private PlayerClass(final String value) 
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
