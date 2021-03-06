package us.myles.tenjava.tasks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class RocketVelocity implements Runnable {

	private List<Entity> block;
	private int id;
	private boolean cancel = false;

	public RocketVelocity(List<Entity> entities) {
		this.block = entities;
	}

	@Override
	public void run() {
		for (Entity entity : block) {
			if (entity instanceof Player) {
				if (entity.getLocation().getY() > 300) {
					Bukkit.getScheduler().cancelTask(this.id);
					return;
				}
			}
		}
		if (block.get(0).isDead()) {
			Bukkit.getScheduler().cancelTask(this.id);
			if (block.get(0).getLocation().getY() > 250) {
				for (Entity entity : block) {
					if (entity instanceof Player && !cancel) {
						Player player = (Player) entity;
						player.eject();
						String r = player.getWorld().getName().equals("moon") ? "Normal World" : "Moon";
						player.sendMessage(ChatColor.AQUA + "You are entering the " + r + ".");
						player.teleport((player.getWorld().getName().equals("moon") ? Bukkit.getWorlds().get(0) : Bukkit.getWorld("moon")).getHighestBlockAt((int) player.getLocation().getX(),
								(int) player.getLocation().getZ()).getLocation().add(0, 10, 0), TeleportCause.COMMAND);
					}
				}
			}
			return;
		}
		for (Entity entity : block) {
			if (entity.getVehicle() == null && entity instanceof Player) {
				cancel = true;
			}
			if (entity.getLocation().getBlock().getType() != Material.AIR && entity.getLocation().getY() < 256) {
				Bukkit.getScheduler().cancelTask(this.id);
				entity.getWorld().createExplosion(entity.getLocation(), 2.5F);
				return;
			}
			entity.setVelocity(new Vector(0, 2L, 0));
		}
		block.get(0).getWorld().playEffect(block.get(0).getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
	}

	public void setTaskID(int id) {
		this.id = id;
	}

}
