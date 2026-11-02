import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Shows the image in a window. If the image is bigger than the screen,
 * only the displayed copy is scaled down. The original image is never resized.
 */

public class ImageViewer {

    private static final int HORIZONTAL_MARGIN = 100;
    private static final int VERTICAL_MARGIN = 150;

    private final JLabel imageLabel = new JLabel();
    private final int displayWidth;
    private final int displayHeight;

    public ImageViewer(BufferedImage image) {
        Dimension displaySize = calculateDisplaySize(image);
        displayWidth = displaySize.width;
        displayHeight = displaySize.height;

        imageLabel.setIcon(createIcon(image));

        JFrame window = new JFrame("Image Averaging");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(imageLabel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    /** Scales on the calling thread, then updates the window and waits for it. */
    public void refreshAndWait(BufferedImage image)
            throws InterruptedException, InvocationTargetException {
        ImageIcon icon = createIcon(image);
        SwingUtilities.invokeAndWait(() -> setIcon(icon));
    }

    /** Scales and updates on the UI thread without waiting. */
    public void refreshLater(BufferedImage image) {
        SwingUtilities.invokeLater(() -> setIcon(createIcon(image)));
    }

    private void setIcon(ImageIcon icon) {
        imageLabel.setIcon(icon);
        imageLabel.repaint();
    }

    private ImageIcon createIcon(BufferedImage image) {
        return new ImageIcon(image.getScaledInstance(
                displayWidth, displayHeight, Image.SCALE_FAST));
    }

    private static Dimension calculateDisplaySize(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = Math.max(1, screenSize.width - HORIZONTAL_MARGIN);
        int maxHeight = Math.max(1, screenSize.height - VERTICAL_MARGIN);

        double scale = Math.min(
                1.0,
                Math.min((double) maxWidth / width,
                        (double) maxHeight / height));

        return new Dimension(
                Math.max(1, (int) (width * scale)),
                Math.max(1, (int) (height * scale)));
    }
}
