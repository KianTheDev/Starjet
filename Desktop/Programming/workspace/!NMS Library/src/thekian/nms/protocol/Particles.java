package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import thekian.nms.main.ReflectionUtils;

public class Particles 
{
	/**
	 * Create a 
	 * 
	 * @param p - Player to whom the packet is to be sent
	 * @param b - Determines whether a particle can be seen from longer than 256 blocks
	 * @param em - Particle type to be displayed
	 * @param x - X position of the particle
	 * @param y - Y position of the particle
	 * @param z - Z position of the particle
	 * @param ox - X offset of the particle. This moves the particle in the X axis for a distance of <= ox.
	 * @param oy - Y offset of the particle. This moves the particle in the Y axis for a distance of <= oy.
	 * @param oz - Z offset of the particle. This moves the particle in the Z axis for a distance of <= oz.
	 * @param dat - Particle data
	 * @param num - Number of particles
	 */
	public static Object createParticle(boolean b, ParticleTypeEnum em, float x, float y, float z, float ox, float oy, float oz, float dat, int num)
	{
		/* PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(em, //particle type
				true, //???
				x, y, z, //position
				0f, 0f, 0f, //offsets
				dat, //speed
				num, //number of particles
				null); */
		try 
		{
			Object enumParticle = ReflectionUtils.getNMSClass("EnumParticle").getField(em.toString().toUpperCase()).get(null);
			Constructor<?> titleConstructor = ReflectionUtils.getNMSClass("PacketPlayOutWorldParticles").getConstructor(ReflectionUtils.getNMSClass("EnumParticle"), boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
			Object packet = titleConstructor.newInstance(enumParticle, b, x, y, z, ox, oy, oz, dat, num, null);
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public enum ParticleTypeEnum 
	{
		EXPLOSION_NORMAL, EXPLOSION_LARGE, EXPLOSION_HUGE, 
		FIREWORKS_SPARK, WATER_BUBBLE, WATER_SPLASH, 
		WATER_WAKE, SUSPENDED, SUSPENDED_DEPTH, 
		CRIT, CRIT_MAGIC, SMOKE_NORMAL, 
		SMOKE_LARGE, SPELL, SPELL_INSTANT, 
		SPELL_MOB, SPELL_MOB_AMBIENT, SPELL_WITCH, 
		DRIP_WATER, DRIP_LAVA, VILLAGER_ANGRY, 
		VILLAGER_HAPPY, TOWN_AURA, NOTE, 
		PORTAL, ENCHANTMENT_TABLE, FLAME, 
		LAVA, FOOTSTEP, CLOUD, 
		REDSTONE, SNOWBALL, SNOW_SHOVEL, 
		SLIME, HEART, BARRIER, 
		ITEM_CRACK, BLOCK_CRACK, BLOCK_DUST, 
		WATER_DROP, ITEM_TAKE, MOB_APPEARANCE, 
		DRAGON_BREATH, END_ROD, DAMAGE_INDICATOR, 
		SWEEP_ATTACK, FALLING_DUST;
	}
}
