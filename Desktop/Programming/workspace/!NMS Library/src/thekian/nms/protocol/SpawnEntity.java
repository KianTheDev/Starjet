package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import thekian.nms.main.ReflectionUtils;

public class SpawnEntity 
{
	
	public static Object spawnEntityPacket(Object entity, int pitch, int yaw, double x, double y, double z)
	{
		try 
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntity test = new net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntity(null, 1, 0, null);
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutSpawnEntity").getConstructor(ReflectionUtils.getNMSClass("Entity"), int.class, int.class, ReflectionUtils.getNMSClass("BlockPosition"));
			Constructor<?> blockPositionConstructor = ReflectionUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class);
			Object packet = packetConstructor.newInstance(ReflectionUtils.getNMSClass("Entity").cast(entity), pitch, yaw, blockPositionConstructor.newInstance(x, y, z));
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object createEntityLightning(Location loc, boolean isEffect, boolean isSilent)
	{
		//net.minecraft.server.v1_11_R1.EntityLightning test = new net.minecraft.server.v1_11_R1.EntityLightning(((org.bukkit.craftbukkit.v1_11_R1.CraftWorld) loc.getWorld()).getHandle(), 1, 1, 1, isEffect, isSilent);
		try 
		{
			Constructor<?> entityLightningConstructor = ReflectionUtils.getNMSClass("EntityLightning").getConstructor(ReflectionUtils.getNMSClass("World"), double.class, double.class, double.class, boolean.class, boolean.class);
			Object lightning = entityLightningConstructor.newInstance(ReflectionUtils.getNMSWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ(), isEffect, isSilent);
			return lightning;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object spawnEntityWeatherPacket(Object entity)
	{
		try 
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityWeather test = new net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityWeather(null);
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutSpawnEntityWeather").getConstructor(ReflectionUtils.getNMSClass("Entity"));
			Object packet = packetConstructor.newInstance(ReflectionUtils.getNMSClass("Entity").cast(entity));
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object createEntityPainting(Location loc, Direction dir)
	{
		//net.minecraft.server.v1_11_R1.EntityPainting test = new net.minecraft.server.v1_11_R1.EntityPainting(null, null, null);
		try
		{
			Constructor<?> blockPositionConstructor = ReflectionUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class);
			Object enumDirection = ReflectionUtils.getNMSClass("EnumDirection").getField(dir.toString().toUpperCase()).get(null);
			Constructor<?> entityPaintingConstructor = ReflectionUtils.getNMSClass("EntityPainting").getConstructor(ReflectionUtils.getNMSClass("World"), ReflectionUtils.getNMSClass("BlockPosition"), ReflectionUtils.getNMSClass("EnumDirection"));
			Object painting = entityPaintingConstructor.newInstance(ReflectionUtils.getNMSWorld(loc.getWorld()),
					blockPositionConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ()),
					enumDirection);
			return painting;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object spawnEntityPaintingPacket(Object entity)
	{
		try
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityPainting test = new net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityPainting(null);
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutSpawnEntityPainting").getConstructor(ReflectionUtils.getNMSClass("EntityPainting"));
			Object packet = packetConstructor.newInstance(ReflectionUtils.getNMSClass("EntityPainting").cast(entity));
			return packet;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object createEntityExperienceOrb(Location loc, int value)
	{
		try
		{
			Constructor<?> entityExperienceOrbConstructor = ReflectionUtils.getNMSClass("EntityExperienceOrb").getConstructor(ReflectionUtils.getNMSClass("World"), double.class, double.class, double.class, int.class);
			Object entityExperienceOrb = entityExperienceOrbConstructor.newInstance(ReflectionUtils.getNMSWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ(), value);
			return entityExperienceOrb;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object spawnEntityExperienceOrbPacket(Object entity)
	{
		try
		{
			//net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityExperienceOrb test = new net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityExperienceOrb(null);
			Constructor<?> packetConstructor = ReflectionUtils.getNMSClass("PacketPlayOutSpawnEntityExperienceOrb").getConstructor(ReflectionUtils.getNMSClass("EntityExperienceOrb"));
			Object packet = packetConstructor.newInstance(ReflectionUtils.getNMSClass("EntityExperienceOrb").cast(entity));
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public enum Direction
	{
		NORTH, EAST, SOUTH, WEST, UP, DOWN;
	}
}
