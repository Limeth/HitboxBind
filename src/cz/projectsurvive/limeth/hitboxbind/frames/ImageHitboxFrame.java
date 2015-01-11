package cz.projectsurvive.limeth.hitboxbind.frames;

import com.google.common.base.Optional;
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
public abstract class ImageHitboxFrame extends HitboxFrame
{
	public ImageHitboxFrame(int id, Class<? extends HitboxFrame> frameClass, ReadOnlyBinding<HitboxMedia> liveStreamDataBinding, Location loc, BlockFace face)
	{
		super(id, frameClass, liveStreamDataBinding, loc, face);
	}

	public abstract URL getImageURL();
	public abstract Optional<ScaleMethod> getScaleMethod();

	@Override
	public BufferedImage createImage()
	{
		URL url = getImageURL();
		BufferedImage image = null;

		try
		{
			image = ImageIO.read(url);

			Preconditions.checkNotNull(image);
		}
		catch(IOException | NullPointerException e)
		{
			HitboxBind.warn("Could not load an image from URL: " + url);
			e.printStackTrace();
			return null;
		}

		Optional<ScaleMethod> scaleMethod = getScaleMethod();

		if(scaleMethod.isPresent())
			image = scaleMethod.get().scale(image);

		return image;
	}
}
