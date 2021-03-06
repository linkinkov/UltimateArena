package net.dmulloy2.ultimatearena.permissions;

/**
 * @author dmulloy2
 */

public enum PermissionType 
{
	CMD_CREATE("create"),
	CMD_DELETE("delete"),
	CMD_DISABLE("disable"),
	CMD_ENABLE("enable"),
	CMD_FORCE_JOIN("forcejoin"),
	CMD_FORCE_STOP("forcestop"),
	CMD_KICK("kick"),
	CMD_PAUSE("pause"),
	CMD_RELOAD("reload"),
	CMD_SET_DONE("setdone"),
	CMD_SET_POINT("setpoint"),
	CMD_START("start"),
	CMD_STOP("stop"),
	BUILD("build"),
	JOIN("join");
	
	public final Permission permission;
	
	PermissionType(final String node) 
	{
		permission = new Permission(node);
	}
}