package thekian.nms.protocol;

import java.lang.reflect.Constructor;

import thekian.nms.main.ReflectionUtils;

public class Scoreboards 
{

	public static Object scoreboardObjectivePacket(Object scoreboardObjective, int i)
	{
		try 
		{
			Constructor<?> scoreboardObjectiveConstructor = ReflectionUtils.getNMSClass("PacketPlayOutScoreboardObjective").getConstructor(ReflectionUtils.getNMSClass("ScoreboardObjective"), int.class);
			Object packet = scoreboardObjectiveConstructor.newInstance(scoreboardObjective, i);
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object displayScoreboardObjectivePacket(int position, Object scoreboardObjective)
	{
		try 
		{
			Constructor<?> displayScoreboardObjectiveConstructor = ReflectionUtils.getNMSClass("PacketPlayOutScoreboardDisplayObjective").getConstructor(int.class, ReflectionUtils.getNMSClass("ScoreboardObjective"));
			Object packet = displayScoreboardObjectiveConstructor.newInstance(position, scoreboardObjective);
			return packet;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Object createStatistic(String s1, String s2)
	{
		try
		{
			Constructor<?> counterConstructor = ReflectionUtils.getNMSClass("Counter").getConstructor();
			Object chatSerial = ReflectionUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + s2 + "\"}");
			Constructor<?> scoreboardConstructor = ReflectionUtils.getNMSClass("ScoreboardServer").getConstructor(String.class, ReflectionUtils.getNMSClass("IChatBaseComponent"), ReflectionUtils.getNMSClass("Counter"));
			return scoreboardConstructor.newInstance(s1, chatSerial, counterConstructor.newInstance());
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Object createScoreboardStatisticCriteria(Object statistic)
	{
		try
		{
			Constructor<?> scoreboardStatisticCriteriaConstructor = ReflectionUtils.getNMSClass("ScoreboardServer").getConstructor(ReflectionUtils.getNMSClass("Statistic"));
			return scoreboardStatisticCriteriaConstructor.newInstance(statistic);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Object createScoreboardReadOnlyCriteria(String s)
	{
		try
		{
			Constructor<?> scoreboardReadOnlyCriteriaConstructor = ReflectionUtils.getNMSClass("ScoreboardReadOnlyCriteriaConstructor").getConstructor(String.class);
			return scoreboardReadOnlyCriteriaConstructor.newInstance(s);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Object createScoreboardHealthCriteria(String s)
	{
		try
		{
			Constructor<?> scoreboardHealthCriteriaConstructor = ReflectionUtils.getNMSClass("ScoreboardHealthCriteriaConstructor").getConstructor(String.class);
			return scoreboardHealthCriteriaConstructor.newInstance(s);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Object createScoreboard(Object minecraftServer)
	{
		try
		{
			Constructor<?> scoreboardConstructor = ReflectionUtils.getNMSClass("ScoreboardServer").getConstructor(ReflectionUtils.getNMSClass("MinecraftServer"));
			return scoreboardConstructor.newInstance(ReflectionUtils.getNMSClass("MinecraftServer").cast(minecraftServer));
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Object createScoreboardObjective(Object scoreboard, String name, Object iscoreboardcriteria)
	{
		try
		{
			Constructor<?> scoreboardObjectiveConstructor = ReflectionUtils.getNMSClass("ScoreboardObjective").getConstructor(ReflectionUtils.getNMSClass("Scoreboard"), String.class, ReflectionUtils.getNMSClass("IScoreboardCriteria"));
			return scoreboardObjectiveConstructor.newInstance(ReflectionUtils.getNMSClass("Scoreboard").cast(scoreboard), name, ReflectionUtils.getNMSClass("IScoreboardCriteria").cast(iscoreboardcriteria));
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
