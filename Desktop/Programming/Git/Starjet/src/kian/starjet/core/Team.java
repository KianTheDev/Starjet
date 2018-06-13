package kian.starjet.core;

import org.bukkit.ChatColor;

public enum Team
{
	RED("Red", ChatColor.RED), GREEN("Green", ChatColor.GREEN), BLUE("Blue", ChatColor.BLUE), YELLOW("Yellow", ChatColor.YELLOW);

	private ChatColor color;
	private String value;
	private int points;
	
	private Team(String value, ChatColor color)
	{
		this.value = value;
		this.color = color;
		points = 0;
	}
	
	public ChatColor getColor()
	{
		return color;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void reset()
	{
		points = 0;
	}
	
	public void addPoints(int i)
	{
		points += i;
	}
	
	public int getPoints()
	{
		return points;
	}
}
