
import javax.swing.*;
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

            JLabel imageLabel = new JLabel(new ImageIcon(image));

            window.add(new JScrollPane(imageLabel));
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            System.out.println("Image loaded successfully.");
            System.out.println("Width: " + image.getWidth());
            System.out.println("Height: " + image.getHeight());

        } catch (Exception e) {
            System.out.println("Could not load the image.");
            e.printStackTrace();
        }
    }
}
