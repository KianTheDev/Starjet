package thekian.nms.protocol;

import org.bukkit.entity.Player;

import thekian.nms.main.ReflectionUtils;

public class Packets 
{
	public static void sendPacket(Player p, Object packet)
	{
		try
		{
			Object handle = p.getClass().getMethod("getHandle").invoke(p);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
