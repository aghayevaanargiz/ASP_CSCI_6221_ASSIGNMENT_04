import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length != 3) {
                System.out.println(
                        "Usage: java Main <image> <squareSize> <S/M>");
                return;
            }

            File file = new File(args[0]);

            int squareSize;
            try {
                squareSize = Integer.parseInt(args[1]);

                if (squareSize <= 0) {
                    System.out.println(
                            "Square size must be positive.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println(
                        "Square size must be an integer.");
                return;
            }

            String mode = args[2].toUpperCase(Locale.ROOT);

            if (!mode.equals("S") && !mode.equals("M")) {
                System.out.println("Mode must be S or M.");
                return;
            }

            if (!file.exists() || !file.isFile()) {
                System.out.println(
                        "Image file not found: " + file.getPath());
                return;
            }

            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                System.out.println(
                        "The file is not a valid image format.");
                return;
            }

            int width = image.getWidth();
            int height = image.getHeight();

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            int maxWidth = Math.max(1, screenSize.width - 100);
            int maxHeight = Math.max(1, screenSize.height - 150);

            double scale = Math.min(
                    1.0,
                    Math.min(
                            (double) maxWidth / width,
                            (double) maxHeight / height));

            int displayWidth = Math.max(
                    1, (int) (width * scale));
            int displayHeight = Math.max(
                    1, (int) (height * scale));

            JFrame window = new JFrame("Image Averaging");
            window.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE);

            JLabel imageLabel = new JLabel();
            imageLabel.setIcon(new ImageIcon(
                    image.getScaledInstance(
                            displayWidth,
                            displayHeight,
                            Image.SCALE_FAST)));

            window.add(imageLabel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            if (mode.equals("S")) {

                for (int y = 0; y < height; y += squareSize) {
                    for (int x = 0; x < width; x += squareSize) {

                        int redSum = 0;
                        int greenSum = 0;
                        int blueSum = 0;
                        int pixelCount = 0;

                        int endX = Math.min(
                                x + squareSize, width);
                        int endY = Math.min(
                                y + squareSize, height);

                        for (int py = y; py < endY; py++) {
                            for (int px = x; px < endX; px++) {

                                Color color = new Color(
                                        image.getRGB(px, py));

                                redSum += color.getRed();
                                greenSum += color.getGreen();
                                blueSum += color.getBlue();
                                pixelCount++;
                            }
                        }

                        Color averageColor = new Color(
                                redSum / pixelCount,
                                greenSum / pixelCount,
                                blueSum / pixelCount);

                        for (int py = y; py < endY; py++) {
                            for (int px = x; px < endX; px++) {
                                image.setRGB(
                                        px, py,
                                        averageColor.getRGB());
                            }
                        }

                        Image resizedImage = image.getScaledInstance(
                                displayWidth,
                                displayHeight,
                                Image.SCALE_FAST);

                        SwingUtilities.invokeAndWait(() -> {
                            imageLabel.setIcon(
                                    new ImageIcon(resizedImage));
                            imageLabel.repaint();
                        });

                        Thread.sleep(1);
                    }
                }

            } else {

                int totalSquareRows = (height + squareSize - 1) / squareSize;

                int threadCount = Math.min(
                        Runtime.getRuntime().availableProcessors(),
                        totalSquareRows);

                int squaresPerThread = (totalSquareRows + threadCount - 1)
                        / threadCount;

                CountDownLatch latch = new CountDownLatch(threadCount);

                for (int i = 0; i < threadCount; i++) {
                    final int startY = i * squaresPerThread * squareSize;

                    final int endY = Math.min(
                            startY + squaresPerThread * squareSize,
                            height);

                    Thread worker = new Thread(() -> {
                        try {
                            for (int y = startY; y < endY; y += squareSize) {

                                for (int x = 0; x < width; x += squareSize) {

                                    int redSum = 0;
                                    int greenSum = 0;
                                    int blueSum = 0;
                                    int pixelCount = 0;

                                    int squareEndX = Math.min(
                                            x + squareSize, width);
                                    int squareEndY = Math.min(
                                            y + squareSize, height);

                                    for (int py = y; py < squareEndY; py++) {
                                        for (int px = x; px < squareEndX; px++) {

                                            Color color = new Color(
                                                    image.getRGB(px, py));

                                            redSum += color.getRed();
                                            greenSum += color.getGreen();
                                            blueSum += color.getBlue();
                                            pixelCount++;
                                        }
                                    }

                                    Color averageColor = new Color(
                                            redSum / pixelCount,
                                            greenSum / pixelCount,
                                            blueSum / pixelCount);

                                    for (int py = y; py < squareEndY; py++) {
                                        for (int px = x; px < squareEndX; px++) {
                                            image.setRGB(
                                                    px, py,
                                                    averageColor.getRGB());
                                        }
                                    }

                                    try {
                                        Thread.sleep(1);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return;
                                    }
                                }
                            }
                        } finally {
                            latch.countDown();
                        }
                    });

                    worker.start();
                }

                Thread previewThread = new Thread(() -> {
                    try {
                        while (latch.getCount() > 0) {
                            SwingUtilities.invokeLater(() -> {
                                Image resizedImage = image.getScaledInstance(
                                        displayWidth,
                                        displayHeight,
                                        Image.SCALE_FAST);

                                imageLabel.setIcon(
                                        new ImageIcon(resizedImage));
                                imageLabel.repaint();
                            });

                            Thread.sleep(30);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                previewThread.setDaemon(true);
                previewThread.start();

                latch.await();
                previewThread.interrupt();

                SwingUtilities.invokeAndWait(() -> {
                    Image resizedImage = image.getScaledInstance(
                            displayWidth,
                            displayHeight,
                            Image.SCALE_FAST);

                    imageLabel.setIcon(
                            new ImageIcon(resizedImage));
                    imageLabel.repaint();
                });
            }

            ImageIO.write(image, "jpg", new File("result.jpg"));

            System.out.println("Image processing completed.");
            System.out.println("Result saved as result.jpg");
            System.out.println("Processing mode: " + mode);

            if (mode.equals("S")) {
                System.out.println("Threads used: 1");
            } else {
                System.out.println(
                        "Threads used: " + Math.min(
                                Runtime.getRuntime().availableProcessors(),
                                (height + squareSize - 1) / squareSize));
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Image processing was interrupted.");
        } catch (Exception e) {
            System.out.println("Could not process the image.");
            e.printStackTrace();
        }
    }
}
