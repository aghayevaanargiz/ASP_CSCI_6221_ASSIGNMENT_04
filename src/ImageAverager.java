import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Replaces every square of the image with its average color,
 * either with one thread or with several threads working on bands of rows.
 */

public class ImageAverager {

    private static final long SQUARE_DELAY_MS = 1;
    private static final long PREVIEW_INTERVAL_MS = 30;

    private final BufferedImage image;
    private final int squareSize;
    private final ImageViewer viewer;
    private final int width;
    private final int height;

    public ImageAverager(BufferedImage image, int squareSize, ImageViewer viewer) {
        this.image = image;
        this.squareSize = squareSize;
        this.viewer = viewer;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /** Number of worker threads used by the multi threaded mode. */
    public int getThreadCount() {
        return Math.min(
                Runtime.getRuntime().availableProcessors(),
                getTotalSquareRows());
    }

    // Single thread mode

    public void processWithSingleThread()
            throws InterruptedException, InvocationTargetException {
        for (int y = 0; y < height; y += squareSize) {
            for (int x = 0; x < width; x += squareSize) {
                averageSquare(x, y);
                viewer.refreshAndWait(image);
                Thread.sleep(SQUARE_DELAY_MS);
            }
        }
    }

    // Multi thread mode

    public void processWithMultipleThreads()
            throws InterruptedException, InvocationTargetException {
        int threadCount = getThreadCount();
        int squaresPerThread = (getTotalSquareRows() + threadCount - 1)
                / threadCount;

        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicReference<Throwable> workerError = new AtomicReference<>();

        for (int i = 0; i < threadCount; i++) {
            int startY = i * squaresPerThread * squareSize;
            int endY = Math.min(startY + squaresPerThread * squareSize, height);

            createWorker(startY, endY, latch, workerError).start();
        }

        Thread previewThread = createPreviewThread(latch);
        previewThread.start();

        latch.await();
        previewThread.interrupt();

        Throwable error = workerError.get();
        if (error != null) {
            throw new IllegalStateException(
                    "Image processing failed in a worker thread.", error);
        }

        viewer.refreshAndWait(image);
    }

    private Thread createWorker(int startY, int endY,
            CountDownLatch latch, AtomicReference<Throwable> workerError) {
        return new Thread(() -> {
            try {
                averageRows(startY, endY);
            } catch (Throwable e) {
                workerError.compareAndSet(null, e);
            } finally {
                latch.countDown();
            }
        });
    }

    /** Periodically refreshes the window while the workers are running. */

    private Thread createPreviewThread(CountDownLatch latch) {
        Thread previewThread = new Thread(() -> {
            try {
                while (latch.getCount() > 0) {
                    BufferedImage previewImage = new BufferedImage(
                            width, height, BufferedImage.TYPE_INT_RGB);

                    synchronized (image) {
                        previewImage.setData(image.getData());
                    }

                    viewer.refreshLater(previewImage);
                    Thread.sleep(PREVIEW_INTERVAL_MS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        previewThread.setDaemon(true);
        return previewThread;
    }

    private void averageRows(int startY, int endY) {
        for (int y = startY; y < endY; y += squareSize) {
            for (int x = 0; x < width; x += squareSize) {
                averageSquare(x, y);

                try {
                    Thread.sleep(SQUARE_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    // Shared square logic

    /** Averages the square whose top left corner is (x, y) and paints it. */
    private void averageSquare(int x, int y) {
        int endX = Math.min(x + squareSize, width);
        int endY = Math.min(y + squareSize, height);

        synchronized (image) {
            Color averageColor = calculateAverageColor(x, y, endX, endY);
            fillArea(x, y, endX, endY, averageColor);
        }
    }

    private Color calculateAverageColor(int startX, int startY,
            int endX, int endY) {
        
        long redSum = 0;
        long greenSum = 0;
        long blueSum = 0;
        long pixelCount = 0;

        for (int py = startY; py < endY; py++) {
            for (int px = startX; px < endX; px++) {
                Color color = new Color(image.getRGB(px, py));

                redSum += color.getRed();
                greenSum += color.getGreen();
                blueSum += color.getBlue();
                pixelCount++;
            }
        }

        int averageRed = (int) (redSum / pixelCount);
        int averageGreen = (int) (greenSum / pixelCount);
        int averageBlue = (int) (blueSum / pixelCount);
        
        return new Color(averageRed, averageGreen, averageBlue);
        
    private void fillArea(int startX, int startY, int endX, int endY,
            Color color) {
        for (int py = startY; py < endY; py++) {
            for (int px = startX; px < endX; px++) {
                image.setRGB(px, py, color.getRGB());
            }
        }
    }

    private int getTotalSquareRows() {
        return (height + squareSize - 1) / squareSize;
    }
}
