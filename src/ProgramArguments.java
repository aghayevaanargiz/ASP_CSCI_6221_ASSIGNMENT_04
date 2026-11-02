import java.io.File;
import java.util.Locale;
import java.util.Optional;

/** Validated command line arguments: image, square size and processing mode. */

public class ProgramArguments {

    private final File imageFile;
    private final int squareSize;
    private final ProcessingMode mode;

    private ProgramArguments(File imageFile, int squareSize, ProcessingMode mode) {
        this.imageFile = imageFile;
        this.squareSize = squareSize;
        this.mode = mode;
    }

    public File getImageFile() {
        return imageFile;
    }

    public int getSquareSize() {
        return squareSize;
    }

    public ProcessingMode getMode() {
        return mode;
    }

    /**
     * Parses and validates the arguments. Prints the reason and returns
     * an empty result if any argument is invalid.
     */

    public static Optional<ProgramArguments> parse(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Main <image> <squareSize> <S/M>");
            return Optional.empty();
        }

        File file = new File(args[0]);

        int squareSize;
        try {
            squareSize = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Square size must be an integer.");
            return Optional.empty();
        }
        if (squareSize <= 0) {
            System.out.println("Square size must be positive.");
            return Optional.empty();
        }

        ProcessingMode mode = ProcessingMode.fromSymbol(
                args[2].toUpperCase(Locale.ROOT));
        if (mode == null) {
            System.out.println("Mode must be S or M.");
            return Optional.empty();
        }

        return Optional.of(new ProgramArguments(file, squareSize, mode));
    }
}

