package me.thekian.rpg;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SecondaryEvents implements Listener
{
	
	Plugin p;
	
	public void setPlugin(Plugin plugin)
	{
		p = plugin;
	}
	
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			e.setCancelled(true);
			new BukkitRunnable()
			{
				
				public void run()
				{
					e.setCancelled(true);
				}
				
			}.runTaskLater(p, 1);
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		if(e.getEntity() instanceof Arrow)
		{
			new BukkitRunnable() 
			{
				
				public void run()
				{
					e.getEntity().remove();
				}

			}.runTaskLater(p, 1);
		}
	}
	
	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent e)
	{
		e.setCancelled(true);
		new BukkitRunnable()
		{
			public void run()
			{
				e.setFoodLevel(20);
				if(e.getEntity() instanceof Player)
				{
					Player p = (Player) e.getEntity();
					p.setFoodLevel(20);
				}
			}
		}.runTaskLater(p, 1);
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e)
	{
		new BukkitRunnable()
		{
			public void run()
			{
				if(e.getSpawnReason().equals(SpawnReason.EGG))
				{
					e.setCancelled(true);
					e.getEntity().remove();
				}
				else if(e.getSpawnReason().equals(SpawnReason.NATURAL))
				{
					e.setCancelled(true);
					e.getEntity().remove();
				}
				else if(e.getEntityType().equals(EntityType.ENDERMITE))
				{
					e.setCancelled(true);
					e.getEntity().remove();
				}
			}
		}.runTaskLater(p, 1);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		if(!e.getPlayer().isOp())
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		if(!e.getPlayer().isOp())
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent e)
	{
		if(e.getItem() != null && e.getItem().getItemStack() != null && e.getItem().getItemStack().getItemMeta() != null && e.getItem().getItemStack().getItemMeta().getDisplayName() != null)
			if(e.getItem().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("NAMETAG_BUTTON"))
				e.setCancelled(true);
	}
	
	@EventHandler
	public void onItemStack(ItemMergeEvent e)
	{
		if(e.getEntity() != null && e.getEntity().getItemStack() != null && e.getEntity().getItemStack().getType() != null)
			if(e.getEntity().getItemStack().getType().equals(Material.WOOD_BUTTON))
				e.setCancelled(true);
	}
}
