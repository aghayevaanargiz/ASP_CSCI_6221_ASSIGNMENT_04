/** The two supported processing modes, selected by a command line symbol. */

public enum ProcessingMode {

    SINGLE_THREAD("S"),
    MULTI_THREAD("M");

    private final String symbol;

    ProcessingMode(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    /** Returns the mode for the given symbol, or null if it is unknown. */
    public static ProcessingMode fromSymbol(String symbol) {
        for (ProcessingMode mode : values()) {
            if (mode.symbol.equals(symbol)) {
                return mode;
            }
        }
        return null;
    }
}
