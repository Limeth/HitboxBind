package cz.projectsurvive.limeth.hitboxbind.listeners;

import com.google.common.base.Optional;
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
		Optional<FrameAction> action = HitboxBind.removeFrameAction(player);

		if(!action.isPresent())
			return;

		ItemFrame itemFrame = (ItemFrame) entity;

		action.get().onRightClick(player, itemFrame);
	}
}
