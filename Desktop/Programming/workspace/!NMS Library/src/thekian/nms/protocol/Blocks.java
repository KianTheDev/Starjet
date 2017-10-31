package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import org.bukkit.Location;
import org.bukkit.block.Block;

import thekian.nms.main.ReflectionUtils;

public class Blocks 
{
	//BlockAction, BlockChange

	/***
	 * See wiki.vg/Block_Actions for ID data.
	 * @param block - Block on which to take action.
	 * @param id - Action ID.
	 * @param id2 - Action param.
	 * @return
	 */
	public static Object blockActionPacket(Block block, int id, int id2)
	{
		try 
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutBlockAction test = new net.minecraft.server.v1_11_R1.PacketPlayOutBlockAction(null, null, 1, 1);
			Location loc = block.getLocation();
			Constructor<?> blockPositionConstructor = ReflectionUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class);
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutBlockAction").getConstructor(ReflectionUtils.getNMSClass("BlockPosition"), ReflectionUtils.getNMSClass("Block"), int.class, int.class);
			Object packet = packetConstructor.newInstance(blockPositionConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()), ReflectionUtils.getNMSBlock(block), id, id2);
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Object blockChangePacket(Location loc)
	{
		try 
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutBlockChange
			Constructor<?> blockPositionConstructor = ReflectionUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class);
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutBlockChange").getConstructor(ReflectionUtils.getNMSClass("World"), ReflectionUtils.getNMSClass("BlockPosition"));
			Object packet = packetConstructor.newInstance(ReflectionUtils.getNMSWorld(loc.getWorld()), blockPositionConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()));
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
