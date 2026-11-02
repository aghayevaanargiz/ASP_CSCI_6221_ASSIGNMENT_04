import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                System.out.println("Usage: java Main <image> <squareSize> <S/M>");
                return;
            }
            
            File file = new File(args[0]);

            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                System.out.println("The file is not a valid image.");
                return;
            }

            int squareSize = 20;

            JFrame window = new JFrame("Image Averaging");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JLabel imageLabel = new JLabel();
            window.add(imageLabel);

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

            window.pack();
            window.setSize(displayWidth, displayHeight);
            window.setLocationRelativeTo(null);
            window.setVisible(true);

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

                    Color averageColor = new Color(
                            averageRed,
                            averageGreen,
                            averageBlue);

                    for (int py = y; py < endY; py++) {
                        for (int px = x; px < endX; px++) {
                            image.setRGB(px, py, averageColor.getRGB());
                        }
                    }

                    Image resizedImage = image.getScaledInstance(
                            displayWidth,
                            displayHeight,
                            Image.SCALE_FAST);

                    imageLabel.setIcon(new ImageIcon(resizedImage));
                    imageLabel.repaint();

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            ImageIO.write(image, "jpg", new File("result.jpg"));

            System.out.println("Image processing completed.");
            System.out.println("Result saved as result.jpg");

        } catch (Exception e) {
            System.out.println("Could not process the image.");
            e.printStackTrace();
        }
    }
}
