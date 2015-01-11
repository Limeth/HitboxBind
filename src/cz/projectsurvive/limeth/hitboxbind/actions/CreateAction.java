package cz.projectsurvive.limeth.hitboxbind.actions;

import cz.projectsurvive.limeth.hitboxbind.HitboxBind;
import cz.projectsurvive.limeth.hitboxbind.HitboxMedia;
import cz.projectsurvive.limeth.hitboxbind.HitboxService;
import cz.projectsurvive.limeth.hitboxbind.Name;
import cz.projectsurvive.limeth.hitboxbind.frames.HitboxFrame;
import cz.projectsurvive.limeth.hitboxbind.util.ReadOnlyBinding;
import de.howaner.FramePicture.FrameManager;
import de.howaner.FramePicture.FramePicturePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

/**
 * @author Limeth
 */
public class CreateAction implements FrameAction
{
	private final Name                         mediaName;
	private final Class<? extends HitboxFrame> frameClass;

	public CreateAction(Name mediaName, Class<? extends HitboxFrame> frameClass)
	{
		this.mediaName = mediaName;
		this.frameClass = frameClass;
	}

	@Override
	public void onRightClick(Player player, ItemFrame itemFrame)
	{
		FrameManager manager = FramePicturePlugin.getManager();
		int id = manager.getNewFrameID();
		ReadOnlyBinding<HitboxMedia> media = HitboxBind.getHitboxService().registerMedia(mediaName, false);
		HitboxFrame frame = HitboxFrame.construct(frameClass, id, media, itemFrame.getLocation(), itemFrame.getFacing());
		HitboxService service = HitboxBind.getHitboxService();

		frame.setEntity(itemFrame);
		HitboxBind.registerFrame(frame);
		manager.sendFrame(frame);
		service.updateMedia(mediaName);
		player.sendMessage(ChatColor.GREEN + "Item frame successfully edited.");
	}

	public Name getMediaName()
	{
		return mediaName;
	}

	public Class<? extends HitboxFrame> getFrameClass()
	{
		return frameClass;
	}
}
