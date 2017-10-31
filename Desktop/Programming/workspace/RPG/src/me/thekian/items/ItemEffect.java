package me.thekian.items;

public enum ItemEffect 
{
	MAGIC("magic"), RANGED("ranged"), MELEE("melee"), DEFENSE("defense"), MISC("misc");
    private final String value;

    private ItemEffect(final String value) 
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
    	if(this.equals(MAGIC))
    	{
    		return "Magic damage";
    	} else if(this.equals(RANGED))
    	{
    		return "Ranged damage";
    	} else if(this.equals(MELEE))
    	{
    		return "Melee damage";
    	} else if(this.equals(DEFENSE))
    	{
    		return "Defense";
    	} else if(this.equals(MISC))
    	{
    		return "nil";
    	} else
    	{
    		return "Unknown";
    	}
    }
}
