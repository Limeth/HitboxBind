package cz.projectsurvive.limeth.hitboxbind.actions;

import cz.projectsurvive.limeth.hitboxbind.HitboxBind;
import cz.projectsurvive.limeth.hitboxbind.HitboxMedia;
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
	private final String                       mediaName;
	private final Class<? extends HitboxFrame> frameClass;

	public CreateAction(String mediaName, Class<? extends HitboxFrame> frameClass)
	{
		this.mediaName = mediaName;
		this.frameClass = frameClass;
	}

	@Override
	public void onRightClick(Player player, ItemFrame itemFrame)
	{
		FrameManager manager = FramePicturePlugin.getManager();
		int id = manager.getNewFrameID();
		ReadOnlyBinding<HitboxMedia> media = HitboxBind.getHitboxService().registerMedia(mediaName);
		HitboxFrame frame = HitboxFrame.construct(frameClass, id, media, itemFrame.getLocation(), itemFrame.getFacing());

		frame.setEntity(itemFrame);
		HitboxBind.addFrame(frame);
		manager.sendFrame(frame);
		player.sendMessage(ChatColor.GREEN + "Item frame successfully edited.");
	}

	public String getMediaName()
	{
		return mediaName;
	}

	public Class<? extends HitboxFrame> getFrameClass()
	{
		return frameClass;
	}
}
