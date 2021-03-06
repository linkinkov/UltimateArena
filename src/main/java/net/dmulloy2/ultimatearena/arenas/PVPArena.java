package net.dmulloy2.ultimatearena.arenas;

import org.bukkit.ChatColor;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;

public class PVPArena extends Arena 
{
	public PVPArena(ArenaZone az) 
	{
		super(az);
		
		setType("Pvp");
		setStarttimer(120);
		setGametimer(0);
		setMaxgametime(60 * 10);
		setMaxDeaths(3);
	}
	
	@Override
	public int getTeam() 
	{
		return getBalancedTeam();
	}

	@Override
	public void check() 
	{
		if (getStarttimer() <= 0)
		{
			if (!simpleTeamCheck(false)) 
			{
				this.setWinningTeam(-1);
				this.stop();
				this.rewardTeam(-1, ChatColor.BLUE + "You won!", false);
			}
		}
	}
}