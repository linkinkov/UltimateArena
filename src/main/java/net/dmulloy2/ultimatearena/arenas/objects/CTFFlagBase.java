package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.CTFArena;
import net.dmulloy2.ultimatearena.util.Util;

public class CTFFlagBase extends FlagBase 
{
	private CTFArena ctf;
	private CTFflag flag;
	private CTFflag enemyflag;
	
	private int team;
	
	public CTFFlagBase(Arena arena, Location loc, int team) 
	{
		super(arena, loc);
		this.arena = arena;
		this.team = team;
		this.ctf = (CTFArena)arena;
		this.getFlag().setTeam(team);
		this.getFlag().colorize();
	}
	
	@Override
	public void setup() 
	{
		super.setup();
		this.setFlag(new CTFflag(getArena(), getLoc().clone().add(0,1,0), team));

		Location flag = getLoc().clone().add(0, 5, 0);
		setNotify(flag.getBlock());
		getNotify().setType(Material.AIR);
	}
	
	@Override
	public void checkNear(List<ArenaPlayer> arenaplayers) 
	{
		getFlag().checkNear(arenaplayers);
		if (!enemyflag.isPickedUp()) 
			return;
		if (enemyflag.getRiding() == null)
			return;
		
		for (int i = 0; i < arenaplayers.size(); i++)
		{
			ArenaPlayer a = arenaplayers.get(i);
			if (!a.isOut() && a.getPlayer().isOnline() && !a.getPlayer().isDead()) 
			{
				if (a.getTeam() == team)
				{ 
					//if the arena player is on my team
					Player p = a.getPlayer();
					if (enemyflag.getRiding().getName().equals(p.getName()))
					{
						//if the player selected is carrying the enemy flag
						if (Util.pointDistance(p.getLocation(), getLoc().clone().add(0, 1, 0)) < 2.75) 
						{ 
							//if hes close to my flag stand, REWARD!
							enemyflag.respawn();
							p.sendMessage(ChatColor.GRAY + "Flag Captured! " + ChatColor.RED + " +500 XP");
							
							p.removePotionEffect(PotionEffectType.SLOW);
							p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
							
							for (int ii = 0; ii < arenaplayers.size(); ii++) 
							{
								ArenaPlayer ap = arenaplayers.get(ii);
								if (!ap.isOut()) 
								{
									if (ap.getTeam() == a.getTeam())
									{
										ap.getPlayer().sendMessage(ChatColor.GREEN + "Unlocked 10 seconds of crits!");
										ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 1));
										ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 1));
									}
								}
							}
								
							a.setGameXP(a.getGameXP() + 500);
							getArena().tellPlayers(ChatColor.GREEN + a.getPlayer().getName() + ChatColor.GRAY + " captured the " + enemyflag.getFlagType() + ChatColor.GRAY + " flag");
							if (team == 1)
							{
								ctf.redcap++;
								getArena().tellPlayers("&c{0} &7team has &a{1}&7/&a3 &7captures!", TeamHelper.getTeam(team), ctf.redcap);
							}
							if (team == 2) 
							{
								ctf.bluecap++;
								getArena().tellPlayers("&c{0} &7team has &a{1}&7/&a3 &7captures!", TeamHelper.getTeam(team), ctf.bluecap);
							}
							return;
						}
					}
				}
			}
		}
	}

	public void initialize() 
	{
		if (team == 1)
			this.enemyflag = ctf.flagblue.getFlag();
		if (team == 2)
			this.enemyflag = ctf.flagred.getFlag();
	}

	public CTFflag getFlag() 
	{
		return flag;
	}

	public void setFlag(CTFflag flag)
	{
		this.flag = flag;
	}
}