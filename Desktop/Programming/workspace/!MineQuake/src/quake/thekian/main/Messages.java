package quake.thekian.main;

import org.bukkit.ChatColor;

public enum Messages 
{
	//Miscellaneous
	BORDER(ChatColor.DARK_GREEN + "========================================"),
	//General
	QUAKE(ChatColor.YELLOW + "MineQuake: "),
	TIPS(ChatColor.RED + "" + ChatColor.BOLD + "TIP: "),
	QUAKE_START(Messages.QUAKE.getValue() + "" + ChatColor.GRAY + "MineQuake 2.5 has begun!"),
	COUNTDOWN(Messages.QUAKE.getValue() + "" + ChatColor.GRAY + "The game will begin in "),
	COUNTDOWN_2(Messages.QUAKE.getValue() + "" + ChatColor.GRAY + "The game will start in "),
	COOLDOWN_1(Messages.QUAKE.getValue() + "" + ChatColor.GRAY + "You cannot use "),
	COOLDOWN_2(ChatColor.GRAY + " for "),
	COOLDOWN_3(ChatColor.GRAY + " seconds."),
	RESPAWN(Messages.QUAKE.getValue() + "" + ChatColor.GRAY + "You will respawn in 5 seconds."),
	//Kit selection
	SPECTATOR(Messages.QUAKE.getValue() + "" + ChatColor.GRAY + "You are now a spectator!");
	
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
