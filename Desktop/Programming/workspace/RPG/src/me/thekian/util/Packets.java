package me.thekian.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Packets 
{
	public void sendPacket(Player p, Object packet)
	{
		try
		{
			Object handle = p.getClass().getMethod("getHandle").invoke(p);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public Class<?> getNMSClass(String name)
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try 
		{
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
}
