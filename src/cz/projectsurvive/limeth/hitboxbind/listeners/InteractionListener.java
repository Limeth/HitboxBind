package cz.projectsurvive.limeth.hitboxbind.listeners;

import cz.projectsurvive.limeth.hitboxbind.HitboxBind;
import cz.projectsurvive.limeth.hitboxbind.actions.FrameAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @author Limeth
 */
public class InteractionListener implements Listener
{
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		Entity entity = event.getRightClicked();

		if(!(entity instanceof ItemFrame))
			return;

		Player player = event.getPlayer();
		FrameAction action = HitboxBind.removeFrameAction(player);

		if(action == null)
			return;

		ItemFrame itemFrame = (ItemFrame) entity;

		action.onRightClick(player, itemFrame);
	}
}
