import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        try {
            File file = new File("sample.jpg");

            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                System.out.println("The file is not a valid image.");
                return;
            }

            JFrame window = new JFrame("Image Averaging");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            int maxWidth = screenSize.width - 100;
            int maxHeight = screenSize.height - 150;

            double scale = Math.min(
                    1.0,
                    Math.min(
                            (double) maxWidth / image.getWidth(),
                            (double) maxHeight / image.getHeight()));

            int displayWidth = (int) (image.getWidth() * scale);
            int displayHeight = (int) (image.getHeight() * scale);

            Image resizedImage = image.getScaledInstance(
                    displayWidth,
                    displayHeight,
                    Image.SCALE_SMOOTH);

            JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));

            window.add(imageLabel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            System.out.println("Image loaded successfully.");
            System.out.println("Width: " + image.getWidth());
            System.out.println("Height: " + image.getHeight());

            int squareSize = 20;

            for (int y = 0; y < image.getHeight(); y += squareSize) {
            for (int x = 0; x < image.getWidth(); x += squareSize) {

            int redSum = 0;
            int greenSum = 0;
            int blueSum = 0;
            int pixelCount = 0;
    
            int endX = Math.min(x + squareSize, image.getWidth());
            int endY = Math.min(y + squareSize, image.getHeight());
    
            for (int py = y; py < endY; py++) {
                for (int px = x; px < endX; px++) {
    
                    Color color = new Color(image.getRGB(px, py));
    
                    redSum += color.getRed();
                    greenSum += color.getGreen();
                    blueSum += color.getBlue();
    
                    pixelCount++;
                }
            }
    
            int averageRed = redSum / pixelCount;
            int averageGreen = greenSum / pixelCount;
            int averageBlue = blueSum / pixelCount;
    
            System.out.println(
                "Square at (" + x + ", " + y + "): RGB("
                + averageRed + ", "
                + averageGreen + ", "
                + averageBlue + ")"
            );
        }
    }

        } catch (Exception e) {
            System.out.println("Could not load the image.");
            e.printStackTrace();
        }
    }
}
