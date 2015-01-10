package cz.projectsurvive.limeth.hitboxbind.frames;

import cz.projectsurvive.limeth.hitboxbind.HitboxMedia;
import cz.projectsurvive.limeth.hitboxbind.util.ReadOnlyBinding;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Limeth
 */
public class StateFrame extends HitboxFrame
{
	private static final Color BACKGROUND_ONLINE  = Color.decode("0xACE600");
	private static final Color BACKGROUND_OFFLINE = Color.DARK_GRAY;
	private static final String TEXT_ONLINE       = "Live";
	private static final String TEXT_OFFLINE      = "Offline";
	private static final Color TEXT_COLOR         = Color.WHITE;

	public StateFrame(int id, Class<? extends HitboxFrame> frameClass, ReadOnlyBinding<HitboxMedia> mediaBinding, Location loc, BlockFace face)
	{
		super(id, frameClass, mediaBinding, loc, face);
	}

	@Override
	public BufferedImage createImage()
	{
		BufferedImage image = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics();
		HitboxMedia data = getMedia();

		//Background
		Color backgroundColor = data.isLive() ? BACKGROUND_ONLINE : BACKGROUND_OFFLINE;

		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, MAP_SIZE, MAP_SIZE);

		//Foreground
		graphics.setFont(FONT);
		FontMetrics fontMetrics = graphics.getFontMetrics();
		String text = data.isLive() ? TEXT_ONLINE : TEXT_OFFLINE;
		Rectangle2D textBounds = fontMetrics.getStringBounds(text, graphics);
		int textX = (int) Math.round((MAP_SIZE - textBounds.getWidth()) / 2);
		int textY = (int) Math.round((MAP_SIZE + textBounds.getHeight()) / 2);

		graphics.setColor(TEXT_COLOR);
		graphics.drawString(text, textX, textY);

		return image;
	}
}
