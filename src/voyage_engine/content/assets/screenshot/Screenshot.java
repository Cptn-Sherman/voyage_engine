package voyage_engine.content.assets.screenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import spool.IMultithreadProcess;

public class Screenshot implements IMultithreadProcess {
	public ByteBuffer buffer;
	public int width, height, bpp;
	public String format;

	public Screenshot(ByteBuffer buffer, String format, int width, int height, int bpp) {
		System.out.println("taking a screenshot...");
		this.buffer = buffer;
		this.format = format;
		this.width = width;
		this.height = height;
		this.bpp = bpp;
	}

	@Override
	public void process() {
		// create the date format for the image file name.
		final Date date = new Date();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		// create the file and generate a buffered image.
		final File file = new File("screenshots" + "//" + dateFormat.format(date) + ".png"); // The file to																						// save to.
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// convert to RGB.
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				final int i = (x + (width * y)) * bpp;
				final int r = buffer.get(i) & 0xFF;
				final int g = buffer.get(i + 1) & 0xFF;
				final int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}
		// try to write the image to the disk in the screenshots folder.
		try {
			ImageIO.write(image, format, file);
		} catch (final IOException e) {
			System.out.println("[ERROR]: could not save screenshot to: " + file.getPath());
		}
		System.out.println("[loader]: saved screenshot to: " + file.getPath());
	}
}
