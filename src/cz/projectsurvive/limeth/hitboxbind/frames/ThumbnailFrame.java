package cz.projectsurvive.limeth.hitboxbind.frames;

import com.google.common.base.Optional;
import cz.projectsurvive.limeth.hitboxbind.HitboxMedia;
import cz.projectsurvive.limeth.hitboxbind.util.ReadOnlyBinding;
import cz.projectsurvive.limeth.hitboxbind.util.ScaleMethod;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.net.URL;

/**
 * @author Limeth
 */
public class ThumbnailFrame extends ImageHitboxFrame
{
	public static final ScaleMethod SCALE_METHOD = ScaleMethod.ZOOM;

	public ThumbnailFrame(int id, Class<? extends HitboxFrame> frameClass, ReadOnlyBinding<HitboxMedia> liveStreamDataBinding, Location loc, BlockFace face)
	{
		super(id, frameClass, liveStreamDataBinding, loc, face);
	}

	@Override
	public URL getImageURL()
	{
		return getMedia().getThumbnailSmall();
	}

	@Override
	public Optional<ScaleMethod> getScaleMethod()
	{
		return Optional.of(SCALE_METHOD);
	}
}
