import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;
import javax.imageio.ImageIO;

/**
 * Entry point. Reads the arguments, loads the image, runs the averaging
 * in the chosen mode and saves the result.
 */
public class Main {

    private static final String OUTPUT_FILE = "result.jpg";

    public static void main(String[] args) {
        try {
            run(args);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Image processing was interrupted.");
        } catch (Exception e) {
            System.out.println("Could not process the image.");
            e.printStackTrace();
        }
    }

    private static void run(String[] args) throws Exception {
        Optional<ProgramArguments> parsed = ProgramArguments.parse(args);
        if (!parsed.isPresent()) {
            return;
        }
        ProgramArguments arguments = parsed.get();

        BufferedImage image = loadImage(arguments.getImageFile());
        if (image == null) {
            return;
        }

        ImageViewer viewer = new ImageViewer(image);
        ImageAverager averager = new ImageAverager(
                image, arguments.getSquareSize(), viewer);

        if (arguments.getMode() == ProcessingMode.SINGLE_THREAD) {
            averager.processWithSingleThread();
        } else {
            averager.processWithMultipleThreads();
        }

        boolean saved = ImageIO.write(
                image, "jpg", new File(OUTPUT_FILE));

        if (!saved) {
            throw new IllegalStateException(
                    "No suitable writer found for JPEG format.");
        }

        printSummary(arguments, averager);
    }

    /**
     * Returns the image, or null (after printing the reason) if it cannot be
     * loaded.
     */
    private static BufferedImage loadImage(File file) throws Exception {
        if (!file.exists() || !file.isFile()) {
            System.out.println("Image file not found: " + file.getPath());
            return null;
        }

        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            System.out.println("The file is not a valid image format.");
        }
        return image;
    }

    private static void printSummary(ProgramArguments arguments,
            ImageAverager averager) {
        System.out.println("Image processing completed.");
        System.out.println("Result saved as " + OUTPUT_FILE);
        System.out.println("Processing mode: " + arguments.getMode().getSymbol());

        if (arguments.getMode() == ProcessingMode.SINGLE_THREAD) {
            System.out.println("Threads used: 1");
        } else {
            System.out.println("Threads used: " + averager.getThreadCount());
        }
    }
}
