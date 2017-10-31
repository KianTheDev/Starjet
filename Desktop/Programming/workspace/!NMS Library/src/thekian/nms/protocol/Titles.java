package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import thekian.nms.main.ReflectionUtils;

public class Titles 
{	
	/***
	 * 
	 * @param p - Player to receive the title packet.
	 * @param titleType - EnumTitleAction value. Valid values: TITLE, SUBTITLE, ACTIONBAR, TIMES, CLEAR, RESET
	 * @param s - Text to be displayed to the player.
	 * @param in - Number of ticks for the title to fade in.
	 * @param dis - Number of ticks for the title to be displayed.
	 * @param out - Number of ticks for the title to fade out.
	 */
	public static Object createTitlePacket(String titleType, String s, int in, int dis, int out)
	{
		//net.minecraft.server.v1_11_R1.PacketPlayOutTitle
		//Original: PacketPlayOutTitle packet = new PacketPlayOutTitle(et, ChatSerializer.a("{\"text\":\"" + s + "\"}"), in, dis, out);
		try 
		{
			Object enumTitle = ReflectionUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField(titleType).get(null);
			Object chatSerial = ReflectionUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + s + "\"}");
			Constructor<?> titleConstructor = ReflectionUtils.getNMSClass("PacketPlayOutTitle").getConstructor(ReflectionUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], ReflectionUtils.getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
			Object packet = titleConstructor.newInstance(enumTitle, chatSerial, in, dis, out);
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object createHotbarPacket(String s) 
	{
		//net.minecraft.server.v1_11_R1.PacketPlayOutChat
		//Original: PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + s + "\"}"), (byte) 2);
		try 
		{
			Object chatSerial = ReflectionUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + s + "\"}");
			Constructor<?> titleConstructor = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(ReflectionUtils.getNMSClass("IChatBaseComponent"), byte.class);
			Object packet = titleConstructor.newInstance(chatSerial, (byte) 2 );
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
