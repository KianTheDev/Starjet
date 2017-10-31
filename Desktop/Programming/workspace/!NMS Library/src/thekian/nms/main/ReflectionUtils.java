package thekian.nms.main;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ReflectionUtils 
{

	public static Object getMinecraftServer()
	{
		try
		{
			return Bukkit.getServer().getClass().getMethod("getHandle").invoke(Bukkit.getServer());
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static Object getNMSBlock(Block block)
	{
		try {
			return getCraftbukkitClass("util.CraftMagicNumbers").getMethod("getBlock", Block.class).invoke(null, block);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getNMSWorld(World world)
	{
		try {
			return getCraftbukkitClass("CraftWorld").getMethod("getHandle").invoke(getCraftbukkitClass("CraftWorld").cast(world));
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getServerHandle(Server minecraftServer)
	{
		try
		{
			Object handle = minecraftServer.getClass().getMethod("getServer").invoke(minecraftServer);
			return handle;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	static public Object getNMSPlayer(Player p)
	{
		try
		{
			return p.getClass().getMethod("getHandle").invoke(p);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	static public Class<?> getNMSClass(String name)
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try 
		{
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	static public Class<?> getCraftbukkitClass(String name)
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try 
		{
			return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
}
/*

Packets by class:
Animation.class -> 		PacketPlayOutAnimation, PacketPlayOutBlockBreakAnimation
Blocks.class -> 		PacketPlayOutBlockAction, PacketPlayOutBlockChange
BossBars.class -> 		PacketPlayOutBoss (and variable actions)
EntityEffects.class -> 	PacketPlayOutEntityEffect, PacketPlayOutRemoveEntityEffect
Particles.class ->		PacketPlayOutWorldParticles
Scoreboards.class ->	PacketPlayOutScoreboardObjective, PacketPlayOutScoreboardDisplayObjective
SpawnEntity.class ->	PacketPlayOutSpawnEntity, PacketPlayOutSpawnEntityWeather, PacketPlayOutSpawnEntityPainting, PacketPlayOutSpawnEntityExperienceOrb
Status.class ->			//TODO
Titles.class ->			PacketPlayOutTitle, PacketPlayOutChat
WorldProtocol.class ->	PacketPlayOutSpawnPosition, PacketPlayOutServerDifficulty, PacketPlayOutWorldBorder
*/
