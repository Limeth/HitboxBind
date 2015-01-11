package cz.projectsurvive.limeth.hitboxbind.listeners;

import com.google.common.base.Optional;
import cz.projectsurvive.limeth.hitboxbind.HitboxBind;
import cz.projectsurvive.limeth.hitboxbind.HitboxMedia;
import cz.projectsurvive.limeth.hitboxbind.actions.FrameAction;
import cz.projectsurvive.limeth.hitboxbind.frames.HitboxFrame;
import de.howaner.FramePicture.FrameManager;
import de.howaner.FramePicture.FramePicturePlugin;
import de.howaner.FramePicture.util.Frame;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.net.URL;

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
		ItemFrame itemFrame = (ItemFrame) entity;

		if(action.isPresent())
		{
			action.get().onRightClick(player, itemFrame);
			event.setCancelled(true);
		}
		else
		{
			FrameManager manager = FramePicturePlugin.getManager();
			Frame frame = manager.getFrame(itemFrame);

			if(frame == null || !(frame instanceof HitboxFrame))
				return;

			event.setCancelled(true);

			HitboxFrame hitboxFrame = (HitboxFrame) frame;
			HitboxMedia media = hitboxFrame.getMedia();
			URL link = media.getLink();
			FancyMessage message = new FancyMessage("Click on the link to open the livestream in a browser: ")
					.color(ChatColor.GREEN).style(ChatColor.BOLD)
					.then(link.toString()).link(link.toString()).color(ChatColor.YELLOW).style(ChatColor.BOLD);

			message.send(player);
		}
	}
}
