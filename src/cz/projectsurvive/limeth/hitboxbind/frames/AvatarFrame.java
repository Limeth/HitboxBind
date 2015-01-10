package cz.projectsurvive.limeth.hitboxbind.frames;

import com.google.common.base.Preconditions;
import cz.projectsurvive.limeth.hitboxbind.HitboxBind;
import cz.projectsurvive.limeth.hitboxbind.HitboxMedia;
import cz.projectsurvive.limeth.hitboxbind.util.ReadOnlyBinding;
import cz.projectsurvive.limeth.hitboxbind.util.ScaleMethod;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * @author Limeth
 */
public class AvatarFrame extends HitboxFrame
{
	public static final ScaleMethod SCALE_METHOD = ScaleMethod.ZOOM;

	public AvatarFrame(int id, Class<? extends HitboxFrame> frameClass, ReadOnlyBinding<HitboxMedia> liveStreamDataBinding, Location loc, BlockFace face)
	{
		super(id, frameClass, liveStreamDataBinding, loc, face);
	}

	@Override
	public BufferedImage createImage()
	{
		HitboxMedia media = getMedia();
		URL logoURL = media.getLogoSmall();
		BufferedImage image = null;

		try
		{
			image = ImageIO.read(logoURL);

			Preconditions.checkNotNull(image);
		}
		catch(IOException | NullPointerException e)
		{
			HitboxBind.warn("Could not load an avatar from URL: " + logoURL);
			e.printStackTrace();
			return null;
		}

		return SCALE_METHOD.scale(image);
	}
}
