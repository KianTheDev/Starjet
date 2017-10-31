package me.thekian.magic;

public enum Ability 
{
	ASSASSINATE("assassinate"), CHAIN_LIGHTNING("chain_lightning"), FIRESTORM("firestorm"), FORCEFIELD("forcefield"), SHOCKER("shocker"), SHOCKWAVE("shockwave"), SNIPE("snipe"), WHIRLING_STRIKE("whirling_strike");
    private final String value;

    private Ability(final String value) 
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
}
