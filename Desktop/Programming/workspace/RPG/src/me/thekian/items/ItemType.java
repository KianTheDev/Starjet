package me.thekian.items;

public enum ItemType 
{
	HEAD("head"), WEAPON_RANGED("weapon_ranged"), WEAPON_MAGIC("weapon_magic"), WEAPON_MELEE("weapon_melee"), ARMOR("armor"), MISC("misc");
    private final String value;

    private ItemType(final String value) 
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
    	if(this.equals(WEAPON_RANGED))
    	{
    		return "Ranged weapon";
    	} else if(this.equals(WEAPON_MAGIC))
    	{
    		return "Spellcasting weapon";
    	} else if(this.equals(WEAPON_MELEE))
    	{
    		return "Melee weapon";
    	} else if(this.equals(ARMOR))
    	{
    		return "Armor";
    	} else if(this.equals(MISC))
    	{
    		return "Miscellaneous";
    	} else
    	{
    		return "Unknown";
    	}
    }
}
