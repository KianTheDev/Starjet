package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import org.bukkit.Location;

import thekian.nms.main.ReflectionUtils;

public class Animation
{
	//Animation, BlockBreakAnimation
	/***
	 * Valid animation IDs:
	 * 0 - Swing main arm;
	 * 1 - Take damage;
	 * 2 - Leave bed;
	 * 3 - Swing offhand;
	 * 4 - Critical effect;
	 * 5 - Magical critical effect.
	 * @param entity - Entity to display the animation.
	 * @param anim - Integer ID of the animation.
	 * @return net.minecraft.server.v1_11_R1.PacketPlayOutAnimation
	 */
	public static Object animationPacket(Object entity, int anim)
	{
		try 
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutAnimation test = new net.minecraft.server.v1_11_R1.PacketPlayOutAnimation((net.minecraft.server.v1_11_R1.Entity) entity, i);
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutAnimation").getConstructor(ReflectionUtils.getNMSClass("Entity"), int.class);
			Object packet = packetConstructor.newInstance(ReflectionUtils.getNMSClass("Entity").cast(entity), anim);
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * 
	 * @param entityId - ID of the entity 'breaking' the block. Whether it needs to be a real entity is unclear.
	 * @param loc - Block location.
	 * @param anim - Animation level. Values are 0 to 9.
	 * @return
	 */
	public static Object blockBreakAnimationPacket(int entityId, Location loc, int anim)
	{
		try
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutBlockBreakAnimation test = new net.minecraft.server.v1_11_R1.PacketPlayOutBlockBreakAnimation();
			Constructor<?> blockPositionConstructor = ReflectionUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class);
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutBreakBlockAnimation").getConstructor(int.class, ReflectionUtils.getNMSClass("BlockPosition"), int.class);
			Object packet = packetConstructor.newInstance(entityId, blockPositionConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()), anim);
			return packet;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
