package kian.starjet.core;

import org.bukkit.ChatColor;

public enum Messages 
{
	//Miscellaneous
	BORDER(ChatColor.DARK_GREEN + "========================================"),
	//General
	STARJET(ChatColor.BLUE + "Starjet: "),
	TIPS(ChatColor.RED + "" + ChatColor.BOLD + "TIP: "),
	STARJET_START(Messages.STARJET.getValue() + "" + ChatColor.GRAY + "Starjet has begun!"),
	COUNTDOWN(Messages.STARJET.getValue() + "" + ChatColor.GRAY + "The game will begin in "),
	COUNTDOWN_2(Messages.STARJET.getValue() + "" + ChatColor.GRAY + "The game will start in "),
	RESPAWN(Messages.STARJET.getValue() + "" + ChatColor.GRAY + "You will respawn in 15 seconds.");

    private final String value;

    private Messages(final String value) 
    {
        this.value = value;
    }

    public String getValue() 
    {
        return value;
    }
}
