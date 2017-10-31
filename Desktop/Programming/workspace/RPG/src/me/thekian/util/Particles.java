package me.thekian.util;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Particles
{
	
	Packets packets = new Packets();
	
	public void createParticle(Player p, String em, float x, float y, float z, float ox, float oy, float oz, float sp, int num)
	{
		/* PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(em, //particle type
				true, //???
				x, y, z, //position
				0f, 0f, 0f, //offsets
				sp, //speed
				num, //number of particles
				null); */
		try 
		{
			Object enumParticle = packets.getNMSClass("EnumParticle").getField(em).get(null);
			Constructor titleConstructor = packets.getNMSClass("PacketPlayOutWorldParticles").getConstructor(packets.getNMSClass("EnumParticle"), boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
			Object packet = titleConstructor.newInstance(enumParticle, true, x, y, z, ox, oy, oz, sp, num, null);
			packets.sendPacket(p, packet);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
