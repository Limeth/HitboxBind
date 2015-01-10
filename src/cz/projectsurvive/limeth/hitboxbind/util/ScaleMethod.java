package cz.projectsurvive.limeth.hitboxbind.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * @author Limeth
 */
public enum ScaleMethod
{
	ZOOM
	{
		@Override
		public BufferedImage scale(BufferedImage image)
		{
			int width = image.getWidth();
			int height = image.getHeight();

			if(width > height)
			{
				width *= (double) SIZE / height;
				height = SIZE;
			}
			else
			{
				height *= (double) SIZE / width;
				width = SIZE;
			}

			return getScaledImage(image, width, height);
		}
	},
	FIT
	{
		@Override
		public BufferedImage scale(BufferedImage image)
		{
			int width = image.getWidth();
			int height = image.getHeight();

			if(width > height)
			{
				height *= (double) SIZE / width;
				width = SIZE;
			}
			else
			{
				width *= (double) SIZE / height;
				height = SIZE;
			}

			return getScaledImage(image, width, height);
		}
	},
	STRETCH
	{
		@Override
		public BufferedImage scale(BufferedImage image)
		{
			return getScaledImage(image, SIZE, SIZE);
		}
	};

	private static final int SIZE = 128;

	public abstract BufferedImage scale(BufferedImage image);

	private static BufferedImage getScaledImage(BufferedImage image, int width, int height) {
		int imageWidth  = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double) width / imageWidth;
		double scaleY = (double) height / imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		return bilinearScaleOp.filter(image, new BufferedImage(width, height, image.getType()));
	}
}
