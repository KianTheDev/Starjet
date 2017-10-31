package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import thekian.nms.main.ReflectionUtils;

public class Status 
{
	public Object serverInfoPacket(Object entity, int anim)
	{
		try 
		{
			//net.minecraft.server.v1_11_R1.PacketStatusOutServerInfo test = new net.minecraft.server.v1_11_R1.PacketStatusOutServerInfo();
		//	Constructor<?> scoreboardObjectiveConstructor = ReflectionUtils.getNMSClass("PacketPlayOutAnimation").getConstructor(ReflectionUtils.getNMSClass("Entity"), int.class);
		//	Object packet = scoreboardObjectiveConstructor.newInstance(ReflectionUtils.getNMSClass("Entity").cast(entity), anim);
		//	return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
