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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Limeth
 */
public abstract class ImageHitboxFrame extends HitboxFrame
{
	private static final int TIMEOUT_CONNECT = 5 * 1000;
	private static final int TIMEOUT_READ = 5 * 1000;

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
		BufferedImage image;

		try
		{
			URLConnection connection = url.openConnection();

			connection.setConnectTimeout(TIMEOUT_CONNECT);
			connection.setReadTimeout(TIMEOUT_READ);

			InputStream is = connection.getInputStream();
			image = ImageIO.read(is);

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
