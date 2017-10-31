package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import org.bukkit.Difficulty;
import org.bukkit.Location;

import net.minecraft.server.v1_11_R1.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import thekian.nms.main.ReflectionUtils;

public class WorldProtocol 
{
	//PacketPlayOutSpawnPosition, PacketPlayOutServerDifficulty, PacketPlayOutWorldBorder
	public static Object worldSpawnPacket(Location loc)
	{
		try 
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutSpawnPosition
			Constructor<?> blockPositionConstructor = ReflectionUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class);
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutSpawnPosition").getConstructor(ReflectionUtils.getNMSClass("BlockPosition"));
			Object packet = packetConstructor.newInstance(blockPositionConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()));
			return packet;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Object serverDifficultyPacket(Difficulty difficulty, boolean b)
	{
		try
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutServerDifficulty
			Class difficultyEnum = ReflectionUtils.getNMSClass("EnumDifficulty");
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutServerDifficulty").getConstructor(ReflectionUtils.getNMSClass("EnumDifficulty"), boolean.class);
			Object packet = packetConstructor.newInstance(Enum.valueOf(difficultyEnum, String.valueOf(difficulty)), b);
			return packet;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * 
	 * @param worldBorder - Previously created world border object
	 * @param borderAction - Valid values: SET_SIZE, LERP_SIZE, SET_CENTER, INITALIZE, SET_WARNING_TIME, SET_WARNING_BLOCKS
	 * @return
	 */
	public static Object worldBorderPacket(Object worldBorder, String borderAction)
	{
		try
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutWorldBorder b;
			Class borderActionEnum = ReflectionUtils.getNMSClass("PacketPlayOutWorldBorder").getDeclaredClasses()[0];
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutWorldBorder").getConstructor(ReflectionUtils.getNMSClass("WorldBorder"), borderActionEnum);
			Object packet = packetConstructor.newInstance(ReflectionUtils.getNMSClass("WorldBorder").cast(worldBorder), Enum.valueOf(borderActionEnum, borderAction));
			return packet;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object worldBorderCreator()
	{
		try
		{
			return ReflectionUtils.getNMSClass("WorldBorder").getConstructor().newInstance();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
