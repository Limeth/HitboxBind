package cz.projectsurvive.limeth.hitboxbind.frames;

import cz.projectsurvive.limeth.hitboxbind.HitboxFrameParseException;
import cz.projectsurvive.limeth.hitboxbind.HitboxMedia;
import cz.projectsurvive.limeth.hitboxbind.util.ReadOnlyBinding;
import de.howaner.FramePicture.util.Frame;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.util.function.Function;

/**
 * @author Limeth
 */
public abstract class HitboxFrame extends Frame
{
	public static final  int    MAP_SIZE             = 128;
	private static final String FRAME_PICTURE_PREFIX = "HitboxFrame";
	private static final char   DELIMITER_CHARACTER  = ':';
	private static final Color  LOADING_BACKGROUND   = Color.ORANGE;
	private static final Color  LOADING_TEXT_COLOR   = Color.BLACK;
	private static final String LOADING_TEXT_STRING  = "Loading...";
	private static final Color[] MISSING_BACKGROUND = new Color[] { Color.MAGENTA, Color.BLACK };
	public static final  Font   FONT                 = new Font("Arial", Font.PLAIN, 28);
	private final ReadOnlyBinding<HitboxMedia> liveStreamDataBinding;

	public HitboxFrame(int id, Class<? extends HitboxFrame> frameClass, ReadOnlyBinding<HitboxMedia> liveStreamDataBinding, Location loc, BlockFace face)
	{
		super(id, FRAME_PICTURE_PREFIX + DELIMITER_CHARACTER + frameClass.getCanonicalName() + DELIMITER_CHARACTER + liveStreamDataBinding.get().getUsername(), loc, face);

		this.liveStreamDataBinding = liveStreamDataBinding;
	}

	@SuppressWarnings("unchecked")
	public static HitboxFrame instanceOf(Frame frame, Function<String, ReadOnlyBinding<HitboxMedia>> mediaBindingSupplier)
	{
		String picture = frame.getPicture();
		String[] splitPicture = picture.split(Character.toString(DELIMITER_CHARACTER));
		String prefix = splitPicture[0];

		if(!FRAME_PICTURE_PREFIX.equals(prefix))
			throw new HitboxFrameParseException("Not a HitboxFrame!");

		if(splitPicture.length != 3)
			throw new HitboxFrameParseException("Invalid HitboxFrame!");

		String frameClassName = splitPicture[1];
		Class<? extends HitboxFrame> frameClass;

		try
		{
			Class<?> uncheckedFrameClass = Class.forName(frameClassName);

			if(!HitboxFrame.class.isAssignableFrom(uncheckedFrameClass))
				throw new HitboxFrameParseException("The class '" + frameClassName + "' does not extend the HitboxFrame class.");

			frameClass = (Class<? extends HitboxFrame>) uncheckedFrameClass;
		}
		catch(ClassNotFoundException e)
		{
			throw new HitboxFrameParseException("HitboxFrame class '" + frameClassName + "' not found.", e);
		}

		String mediaName = splitPicture[2];
		ReadOnlyBinding<HitboxMedia> media = mediaBindingSupplier.apply(mediaName);

		try
		{
			return construct(frameClass, frame.getId(), media, frame.getLocation(), frame.getFacing());
		}
		catch(Exception e)
		{
			throw new HitboxFrameParseException("Cannot instantiate HitboxFrame class '" + frameClass.getCanonicalName() + "'.", e);
		}
	}

	public static <T extends HitboxFrame> T construct(Class<T> frameClass, int id, ReadOnlyBinding<HitboxMedia> media, Location location, BlockFace facing)
	{
		Constructor<? extends T> constructor;

		try
		{
			constructor = frameClass.getConstructor(Integer.TYPE, Class.class, ReadOnlyBinding.class, Location.class, BlockFace.class);
		}
		catch(NoSuchMethodException e)
		{
			throw new HitboxFrameParseException("HitboxFrame class '" + frameClass.getCanonicalName() +
			                                    "' is missing the HitboxFrame(int, Class<? extends HitboxFrame>, " +
			                                    "ReadOnlyBinding<LiveStreamData>, Location, BlockFace) constructor.");
		}

		try
		{
			return constructor.newInstance(id, frameClass, media, location, facing);
		}
		catch(Exception e)
		{
			throw new HitboxFrameParseException("Cannot instantiate HitboxFrame class '" + frameClass.getCanonicalName() + "'.", e);
		}
	}

	public static boolean isReplaceable(Frame frame)
	{
		String picture = frame.getPicture();
		String[] splitPicture = picture.split(Character.toString(DELIMITER_CHARACTER));

		return splitPicture.length == 3 && FRAME_PICTURE_PREFIX.equals(splitPicture[0]);
	}

	public HitboxMedia getMedia()
	{
		return liveStreamDataBinding.get();
	}

	/**
	 * Creates the image to be shown. Clear the cache to call this method again.
	 *
	 * @return The created image
	 */
	public abstract BufferedImage createImage();

	@Override
	public BufferedImage getBufferImage()
	{
		if(!liveStreamDataBinding.get().isLoaded())
			return createLoadingImage();

		BufferedImage image = createImage();

		if(image == null)
			return createMissingImage();

		return image;
	}

	private BufferedImage createLoadingImage()
	{
		BufferedImage image = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics();

		//Background
		graphics.setColor(LOADING_BACKGROUND);
		graphics.fillRect(0, 0, MAP_SIZE, MAP_SIZE);

		//Foreground
		graphics.setFont(FONT);
		FontMetrics fontMetrics = graphics.getFontMetrics();
		Rectangle2D textBounds = fontMetrics.getStringBounds(LOADING_TEXT_STRING, graphics);
		int textX = (int) Math.round((MAP_SIZE - textBounds.getWidth()) / 2);
		int textY = (int) Math.round((MAP_SIZE + textBounds.getHeight()) / 2);

		graphics.setColor(LOADING_TEXT_COLOR);
		graphics.drawString(LOADING_TEXT_STRING, textX, textY);

		return image;
	}

	private BufferedImage createMissingImage()
	{
		BufferedImage image = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);

		for(int y = 0; y < MAP_SIZE; y++)
			for(int x = 0; x < MAP_SIZE; x++)
			{
				int colorIndex = (y + x) % MISSING_BACKGROUND.length;
				Color color = MISSING_BACKGROUND[colorIndex];

				image.setRGB(x, y, color.getRGB());
			}

		return image;
	}
}
