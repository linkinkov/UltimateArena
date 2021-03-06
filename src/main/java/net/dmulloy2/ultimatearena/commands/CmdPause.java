package net.dmulloy2.ultimatearena.commands;

import org.bukkit.ChatColor;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.permissions.PermissionType;

public class CmdPause extends UltimateArenaCommand
{
	public CmdPause(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "pause";
		this.requiredArgs.add("arena");
		this.mode = "admin";
		this.description = "pause the start timer on an arena";
		this.permission = PermissionType.CMD_PAUSE.permission;
	}
	
	@Override
	public void perform() 
	{
		String name = args[0];
		Arena arena = plugin.getArena(name);
		if (arena == null)
		{
			player.sendMessage(ChatColor.GOLD + "No arena with that name...");
			return;
		}		
		
		arena.setPauseStartTimer(!arena.isPauseStartTimer());
		player.sendMessage(ChatColor.GOLD + "Start timer for arena " + ChatColor.AQUA + arena.getName() + ChatColor.GOLD + " is now " + (arena.isPauseStartTimer() ? "paused" : "unpaused"));
	}	
}