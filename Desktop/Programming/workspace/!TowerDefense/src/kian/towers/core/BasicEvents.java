package kian.towers.core;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/***
 * Non-game specific events; different modules can be enabled or disabled at a whim.
 */
public class BasicEvents implements Listener
{
	
	Plugin plugin;
	
	public BasicEvents(Plugin plugin) //Needs to register with a plugin to function correctly
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) //Keeps player food at 20
	{
		if(e.getEntity() instanceof org.bukkit.entity.Player)
			new BukkitRunnable(){ public void run()
			{ ((org.bukkit.entity.Player) e.getEntity()).setFoodLevel(20); } 
			}.runTaskLaterAsynchronously(plugin, 1);
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof LivingEntity) //Prevents entities from being harmed by players or (highly unlikely) malfunctions in monster AI
			e.setCancelled(true);
	}

	//Prevents undead from catching fire in the sunlight
	@EventHandler
	public void onEntityCombust(EntityCombustEvent e)
	{
		//At least one of these should stop it.
		e.setCancelled(true);
		e.setDuration(0);
		e.getEntity().setFireTicks(0);
	}
	
	//Trio of events to stop players from messing with their inventory
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onHandSwap(PlayerSwapHandItemsEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e)
	{
		if(!e.getEntityType().equals(EntityType.PLAYER))
			e.setCancelled(true);
	}
	
	//Kill any entities that naturally spawn where they aren't wanted
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent e)
	{
		if(e.getSpawnReason().equals(SpawnReason.NATURAL))
		{
			new BukkitRunnable(){ 
				public void run(){e.getEntity().remove();} 
			}.runTaskLater(plugin, 1);
		}
		//Prevent chicken jockies
		if(e.getEntity() instanceof Chicken)
		{
			new BukkitRunnable(){ 
				public void run()
				{
					if(e.getEntity().getPassengers().size() > 0)
					{
						for(Entity ent : e.getEntity().getPassengers())
							ent.leaveVehicle();
						e.getEntity().remove();
					}
				} 
			}.runTaskLater(plugin, 1);
		}
	}
	
	@EventHandler
	public void onTeleport(EntityTeleportEvent e)
	{
		if(e.getEntityType().equals(EntityType.ENDERMAN))
			e.setCancelled(true);
	}

}
