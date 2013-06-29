package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandReload extends UltimateArenaCommand
{
	public PCommandReload(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.mode = "admin";
		this.description = "reload UltimateArena";
		this.permission = PermissionType.CMD_RELOAD.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		sendMessage("&aReloading UltimateArena...");
		
		long start = System.currentTimeMillis();
			
		plugin.onDisable();
		plugin.onEnable();
		
		long finish = System.currentTimeMillis();
			
		sendMessage("&aReload Complete! Took {0} ms!", finish - start);
	}
}