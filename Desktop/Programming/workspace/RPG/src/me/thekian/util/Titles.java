package me.thekian.util;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

public class Titles 
{
	Packets packets = new Packets();
	
	public void createTitle(Player p, String titleType, String s, int in, int dis, int out)
	{
		//Original: PacketPlayOutTitle packet = new PacketPlayOutTitle(et, ChatSerializer.a("{\"text\":\"" + s + "\"}"), in, dis, out);
		try 
		{
			Object enumTitle = packets.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField(titleType).get(null);
			Object chatSerial = packets.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + s + "\"}");
			Constructor titleConstructor = packets.getNMSClass("PacketPlayOutTitle").getConstructor(packets.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], packets.getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
			Object packet = titleConstructor.newInstance(enumTitle, chatSerial, in, dis, out);
			packets.sendPacket(p, packet);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void createHotbar(Player p, String s) 
	{
		//Original: PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + s + "\"}"), (byte) 2);
		try 
		{
			Object chatSerial = packets.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + s + "\"}");
			Constructor titleConstructor = packets.getNMSClass("PacketPlayOutChat").getConstructor(packets.getNMSClass("IChatBaseComponent"), byte.class);
			Object packet = titleConstructor.newInstance(chatSerial, (byte) 2 );
			packets.sendPacket(p, packet);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
