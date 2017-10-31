package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import org.bukkit.plugin.Plugin;

import net.minecraft.server.v1_11_R1.MobEffectList;
import thekian.nms.main.ReflectionUtils;

public class EntityEffects 
{
	public static Object entityEffectPacket(int entityId, int effectId, int duration, int level, boolean ambient, boolean showParticles)
	{
		try 
		{
			//net.minecraft.server.v1_11_R1.MobEffect test2 = new net.minecraft.server.v1_11_R1.MobEffect(MobEffectList.fromId(effectId), duration, level, ambient, showParticles);
			//net.minecraft.server.v1_11_R1.PacketPlayOutEntityEffect test = new net.minecraft.server.v1_11_R1.PacketPlayOutEntityEffect();
			Constructor<?> mobEffectConstructor = ReflectionUtils.getNMSClass("MobEffect").getConstructor(ReflectionUtils.getNMSClass("MobEffectList"), int.class, int.class, boolean.class, boolean.class);
			Constructor<?> scoreboardObjectiveConstructor = ReflectionUtils.getNMSClass("PacketPlayOutEntityEffect").getConstructor(int.class, ReflectionUtils.getNMSClass("MobEffect"));
			Object packet = scoreboardObjectiveConstructor.newInstance(entityId, mobEffectConstructor.newInstance(ReflectionUtils.getNMSClass("MobEffectList").getMethod("fromId", int.class).invoke(null, entityId), duration, level, ambient, showParticles), entityId);
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object removeEntityEffectPacket(Object entity)
	{
		try 
		{
			net.minecraft.server.v1_11_R1.PacketPlayOutRemoveEntityEffect test = new net.minecraft.server.v1_11_R1.PacketPlayOutRemoveEntityEffect();
			Constructor<?> scoreboardObjectiveConstructor = ReflectionUtils.getNMSClass("PacketPlayOutRemoveEntityEffect").getConstructor(ReflectionUtils.getNMSClass("Entity"), int.class);
			Object packet = scoreboardObjectiveConstructor.newInstance(ReflectionUtils.getNMSClass("Entity").cast(entity));
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
