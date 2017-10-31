package me.thekian.util;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.MinecraftServer;
import net.minecraft.server.v1_11_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_11_R1.PlayerConnection;
import net.minecraft.server.v1_11_R1.PlayerInteractManager;
import net.minecraft.server.v1_11_R1.WorldServer;

public class NPCs 
{
	
	ArrayList<EntityPlayer> npcs = new ArrayList<EntityPlayer>();
	
	public void createNPC(World w, Player p)
	{
		System.out.println("???");
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer worldserver = ((CraftWorld) w).getHandle();
		EntityPlayer npc = new EntityPlayer(server, worldserver, new GameProfile(UUID.randomUUID(), "NPC"), new PlayerInteractManager(worldserver));
		npcs.add(npc);
		npc.teleportTo(p.getLocation(), false);
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));	
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
	}
}
