package thekian.nms.listener;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import thekian.nms.main.ReflectionUtils;

public class PlayerPacketListener
{

	  public static void addPlayer(Player p) 
	  {
		  try 
		  {
			  Channel channel = getNMSPlayerChannel(p);
			  channel.pipeline().addBefore("packet_handler", "PacketListener", 
			  new ChannelInboundHandlerAdapter()
			  {

			  });
		  } catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
	  }

	  public static void removePlayer(Player p) {
		  try 
		  {
			  Channel channel = getNMSPlayerChannel(p);
			  if(channel.pipeline().get("PacketListener") != null)
				  channel.pipeline().remove("PacketListener");
		  } catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
	  }

	  private static Channel getNMSPlayerChannel(Player p)
	  {
		  try 
		  {
			  return (Channel) ReflectionUtils.getNMSClass("net.minecraft.server.v1_11_R1").getField("channel").get(getNetworkManager(p));
		  } catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		  return null;
	  }

	  private static Object getNetworkManager(Player p) 
	  {
		  try
		  {
			  Object playerConnection = ReflectionUtils.getNMSPlayer(p).getClass().getField("playerConnection").get(ReflectionUtils.getNMSPlayer(p));
			  return ReflectionUtils.getNMSClass("PlayerConnection").getField("networkManager").get(playerConnection);
		  } catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return null;
	  }
}
