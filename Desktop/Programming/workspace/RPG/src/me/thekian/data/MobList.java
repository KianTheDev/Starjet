package me.thekian.data;

import java.util.ArrayList;

import org.bukkit.entity.EntityType;

import me.thekian.cstmobs.CustomEntityType;
import me.thekian.data.Mobs.MobData;
import me.thekian.data.Mobs.NPCData;

public class MobList 
{
	Mobs mobs = new Mobs();
	public ArrayList<MobData> mobList = new ArrayList<MobData>();
	public ArrayList<NPCData> npcList = new ArrayList<NPCData>();
	
	public ArrayList<NPCData> getNPCList()
	{
		return npcList;
	}
	
	public ArrayList<MobData> getMobList()
	{
		return mobList;
	}
	
	public void init()
	{
		
		//Hostile creature data
		mobList.add(mobs.new MobData("Shambling Dead", CustomEntityType.ZOMBIE, 4, false, 1, 30, true, 2, 1));
		mobList.add(mobs.new MobData("Death Cow", CustomEntityType.EVIL_COW, 50, false, 10, 100, true, 15, 1));
		
		//NPC data
		npcList.add(mobs.new NPCData("Civilian", CustomEntityType.VILLAGER, 250, false, 1, 0, 0, 1, false, false, new String[] {"Hello!", "Greetings."}, null));
		npcList.add(mobs.new NPCData("Armorer", CustomEntityType.VILLAGER_IMMOBILE, 500, true, 1, 0, 0, 1, false, true, new String[] {"Welcome to my shop!"}, ShopType.BATTLE_1));
		
	}
}
