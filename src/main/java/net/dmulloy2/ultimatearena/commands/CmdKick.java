package net.dmulloy2.ultimatearena.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.permissions.PermissionType;
import net.dmulloy2.ultimatearena.util.Util;

public class CmdKick extends UltimateArenaCommand
{
	public CmdKick(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "kick";
		this.aliases.add("k");
		this.requiredArgs.add("player");
		this.mode = "admin";
		this.description = "kick a player from an arena";
		this.permission = PermissionType.CMD_KICK.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		Player p = Util.matchPlayer(args[0]);
		if (p != null) 
		{
			ArenaPlayer ap = plugin.getArenaPlayer(p);
			if (ap != null) 
			{
				Arena a = plugin.getArena(p);
				if (a != null)
				{
					a.endPlayer(ap, false);
					ap.setOut(true);
					ap.setDeaths(999999999);
					ap.setPoints(0);
					ap.setKills(0);
					ap.setGameXP(0);
					sendMessage(ChatColor.GRAY + "Kicked player: " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " from arena: " + ChatColor.GOLD + a.getName());
				}
			}
			else
			{
				sendMessage(ChatColor.GRAY + "Player: " + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " is not in an Arena");
			}
		}
		else
		{
			sendMessage(ChatColor.GRAY + "Player: \"" + ChatColor.GOLD + args[0] + ChatColor.GRAY + "\" is not online");
		}
	}
}