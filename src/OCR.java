import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

/**
 * Created by Guazi on 02.08.2015.
 */
public class OCR {
	//static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public String[] getText(Mat inputImg){

		Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping
		String[] out = new String[3];

		// convert to grayscale
		Imgproc.cvtColor(inputImg, inputImg, 6);

		/*ROI:region of interest.
		 *
		 * first resize.
		 * then gaussian blur
		 * then turn into black white with threshold
		 */
		Mat imgHeight = new Mat(inputImg,new Rect(182, 680, 100, 35));
		Imgproc.resize(imgHeight, imgHeight, new Size(imgHeight.width() * 2, imgHeight.height() * 2));
		Mat imgHSpeed = new Mat(inputImg,new Rect(864, 678, 120, 36));
		Imgproc.resize(imgHSpeed, imgHSpeed, new Size(imgHSpeed.width() * 2, imgHSpeed.height() * 2));
		Mat imgVSpeed = new Mat(inputImg,new Rect(1080, 678, 120, 36));
		Imgproc.resize(imgVSpeed, imgVSpeed, new Size(imgVSpeed.width() * 2, imgVSpeed.height() * 2));

		Imgproc.GaussianBlur(imgHeight, imgHeight, new Size(3, 3), 0);// smoothing window width and height in pixels;sigma value, determines how much the image will be blurred
		Imgproc.threshold(imgHeight, imgHeight, 100, 255, 1);
		Imgproc.GaussianBlur(imgHSpeed, imgHSpeed, new Size(3, 3), 0);// smoothing window width and height in pixels;sigma value, determines how much the image will be blurred
		Imgproc.threshold(imgHSpeed, imgHSpeed, 100, 255, 1);
		Imgproc.GaussianBlur(imgVSpeed, imgVSpeed, new Size(3, 3), 0);// smoothing window width and height in pixels;sigma value, determines how much the image will be blurred
		Imgproc.threshold(imgVSpeed, imgVSpeed, 100, 255, 1);

		//tess4j: OCR
		try {
			out[0] = instance.doOCR(mat2Img(imgHeight));
			out[1] = instance.doOCR(mat2Img(imgHSpeed));
			out[2] = instance.doOCR(mat2Img(imgVSpeed));
		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}
		return out;
	}

	public BufferedImage mat2Img(Mat in)
	{
		BufferedImage out;
		byte[] data = new byte[in.width() * in.height() * (int)in.elemSize()];
		in.get(0, 0, data);
		out = new BufferedImage(in.width(), in.height(), 10);
		out.getRaster().setDataElements(0, 0, in.width(), in.height(), data);
		return out;
	}
}


