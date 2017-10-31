package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import thekian.nms.main.ReflectionUtils;

public class BossBars 
{
	/***
	 * Creates a BossBattleServer object to be sent to players with other packet methods. Can be modified with modifyBossBar().
	 * @param s - The title of the boss bar.
	 * @param h - The progression of the boss bar, ranging from 0.0 to 1.0.
	 * @param color - The color of the boss bar. Valid values: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
	 * @param style - The style of the boss bar. Valid values: PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20
	 * @return
	 */
	public static Object createBossBattleServer(String s, double h, String color, String style)
	{
		try
		{
			float healthPortion = (float) h;
			Class colorEnum = ReflectionUtils.getNMSClass("BossBattle").getDeclaredClasses()[1];
			Class styleEnum = ReflectionUtils.getNMSClass("BossBattle").getDeclaredClasses()[0];
			Object chatSerial = ReflectionUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + s + "\"}");
			Constructor<?> bossConstructor = ReflectionUtils.getNMSClass("BossBattleServer").getConstructor(ReflectionUtils.getNMSClass("IChatBaseComponent"), colorEnum, styleEnum);
			Object bossBar = bossConstructor.newInstance(chatSerial, Enum.valueOf(colorEnum, color), Enum.valueOf(styleEnum, style));
			ReflectionUtils.getNMSClass("BossBattleServer").getMethod("setProgress", float.class).invoke(bossBar, healthPortion);
			return bossBar;
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Boss bar object creation error. Returning null.");
			return null;
		}
	}

	/***
	 * 
	 * @param bossBar - Boss battle object to be modified.
	 * @param h - Progression of the boss bar. Ranges from 0.0 to 1.0. Set to -1 to not modify it.
	 * @param name - Title of the boss bar. Pass a null argument to not modify it.
	 */
	public static void modifyBossBar(Object bossBar, double h, String name)
	{
		try
		{
			if(h != -1)
			{
				float healthPortion = (float) h;
				ReflectionUtils.getNMSClass("BossBattle").getMethod("a", float.class).invoke(bossBar, healthPortion);
			}
			
			if(name != null)
			{
				Object chatSerial = ReflectionUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + name + "\"}");
				ReflectionUtils.getNMSClass("BossBattle").getMethod("a", ReflectionUtils.getNMSClass("IChatBaseComponent")).invoke(bossBar, chatSerial);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/***
	 * This method should only be used to initialize the player's boss bar display. Use the updateBossBar() method to change it.
	 * @param p - Player to receive the boss bar packet.
	 * @param bossBar - BossBattleServer object to initialize the display.
	 */
	public static Object displayBossBarPacket(Object bossBar)
	{
		try 
		{
			Class actionEnum = ReflectionUtils.getNMSClass("PacketPlayOutBoss$Action");
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutBoss").getConstructor(actionEnum, ReflectionUtils.getNMSClass("BossBattle"));
			Object packet = packetConstructor.newInstance(Enum.valueOf(actionEnum, "ADD"), bossBar);
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * Used to update the display of a particular BossBattleServer to a player.
	 * @param bossBar - Boss battle object to be sent. Must be the same as the one used to initialize the boss bar.
	 * @param action - Action to be taken. 0 = Update progress. 1 = Update title.
	 */
	public static Object updateBossBarPacket(Object bossBar, int action)
	{
		try 
		{
			Class actionEnum = ReflectionUtils.getNMSClass("PacketPlayOutBoss$Action");
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutBoss").getConstructor(actionEnum, ReflectionUtils.getNMSClass("BossBattle"));
			if(action == 0)
			{
				Object packet = packetConstructor.newInstance(Enum.valueOf(actionEnum, "UPDATE_PCT"), bossBar);
				return packet;
			} else if(action == 1)
			{
				Object packet = packetConstructor.newInstance(Enum.valueOf(actionEnum, "UPDATE_NAME"), bossBar);
				return packet;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * Use this method to remove a boss bar from a server.
	 * @param bossBar - Boss battle object to be sent. Must be the same as the one used to initialize the boss bar.
	 */
	public static Object removeBossBarPacket(Object bossBar)
	{
		try 
		{
			Class actionEnum = ReflectionUtils.getNMSClass("PacketPlayOutBoss$Action");
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutBoss").getConstructor(actionEnum, ReflectionUtils.getNMSClass("BossBattle"));
			Object packet = packetConstructor.newInstance(Enum.valueOf(actionEnum, "REMOVE"), bossBar);
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
