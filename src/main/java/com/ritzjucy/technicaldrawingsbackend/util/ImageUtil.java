package com.ritzjucy.technicaldrawingsbackend.util;

import com.ritzjucy.technicaldrawingsbackend.entity.ImageEntity;
import com.ritzjucy.technicaldrawingsbackend.model.AIDetection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ImageUtil
{
    public static String buildInputImageUrl(String appHost, ImageEntity image)
    {
        return appHost + "/media/project_" + image.getProject().getId() + "/" + image.getDisplayName();
    }

    public static String buildRecognitionOutputImageUrl(String appHost, ImageEntity image)
    {
        return appHost + "/media/outputs/project_" + image.getProject().getId()
                + "/" + image.getName() + "/text_recognition/final/" + buildImageVisualPath(image);
    }

    public static String buildImageVisualPath(ImageEntity image)
    {
        return "visual/" + image.getDisplayName();
    }

    public static void draw(BufferedImage onImage, AIDetection detection)
    {
        Graphics graphics = onImage.getGraphics();
        graphics.setColor(Color.RED);
        graphics.drawRect(detection.x(), detection.y(), detection.width(), detection.height());

        graphics.dispose();
    }

    public static BufferedImage copyImage(BufferedImage source)
    {
        BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = target.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();

        return target;
    }

    public static byte[] imageToBytes(BufferedImage image, String fileType) throws IOException
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(image, fileType, outStream);

        byte[] bytes = outStream.toByteArray();

        return Arrays.copyOf(bytes, bytes.length);
    }

    public static BufferedImage bytesToImage(byte[] bytes) throws IOException
    {
        return ImageIO.read(new ByteArrayInputStream(Arrays.copyOf(bytes, bytes.length)));
    }

}
