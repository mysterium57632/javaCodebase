package de.paull.lib.output;

import de.paull.lib.files.Writer;
import de.paull.lib.files.ConfigHandler;
import de.paull.lib.files.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Modify the Output of the terminal to include a timestamp, colors and logfiles.
 */
public class Output extends OutputStream implements AutoCloseable {

    public static boolean debug;
    public static String ANSI_TS_COLOR = ANSI.BLUE;
    private static String ERR_FILE_PATH;
    private final PrintStream defStream;
    private final PrintStream fileStream;
    private final PrintStream newStream;

    /**
     * Initializes the Output and Error class
     * @param debug True to show full error with stack trace, false for simplified message
     * @param log path for the log-file
     * @param err path for the error-file
     * @param packageName the beginning of the package structure, like `com.example`
     */
    public Output(boolean debug, String log, String err, String packageName) {
        super();
        Output.debug = debug;
        ERR_FILE_PATH = err;
        defStream = System.out;
        fileStream = getFileStream(log);
        newStream = new Stream(this);
        System.setOut(newStream);
        new ErrorStream(this);
    }

    /**
     * Initializes the Output and Error class
     * <p>
     * For the log file path and error file path it checks the ConfigHandler and gets the following fields:
     * - LOG_FILE
     * - ERR_FILE
     * <p>
     * @param debug True to show full error with stack trace, false for simplified message
     * @param packageName the beginning of the package structure, like `com.example`
     */
    public Output(boolean debug, String packageName) {
        this(debug, ConfigHandler.get("LOG_FILE"), ConfigHandler.get("ERR_FILE"), packageName);
    }

    public Stream getPrintStream() {
        return (Stream) newStream;
    }

    public PrintStream getFileStream(String path) {
        File f = new File(path);
        try {
            FileUtil.createFile(f);
            Writer.write(f, "", false);
            return new PrintStream(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void write(int b) {
        defStream.write(b);
    }

    public class Stream extends PrintStream {

        private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        private boolean hasLB;
        private final String name; // Package name, like 'de.paull'
        private final String notname;

        public Stream(OutputStream out) {
            super(out, true);
            hasLB = true;
            name = "de.paull";
            notname = "de.paull.lib.output";
        }

        @Override
        public void println(String x) {
            super.println(beautify(x));
            fileStream.println(beautifyLog(x));
            hasLB = true;
        }

        @Override
        public void print(String x) {
            super.print(x);
            hasLB = false;
        }

        public void error(String message) {
            super.println(beautify(ANSI.RED + "ERROR at " + getStackTraceLine() + ":    " + message + ANSI.RESET));
            fileStream.println(beautifyLog("ERROR at " + getStackTraceLine() + ":   " + message));
            hasLB = true;
        }

        public String beautify(String x) {
            x = ANSI_TS_COLOR + timestamp() + ":" + ANSI.RESET + " " + x;
            if (!hasLB) x = "\n" + x;
            return x + "\n";
        }

        public String beautifyLog(String x) {
            x = timestamp() + ": " + x;
            if (!hasLB) x = "\n" + x;
            return x;
        }

        /**
         * Returns a formated Timestamp
         * @return timestamp
         */
        public static String timestamp() {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String time = format.format(timestamp);
            return "[" + time + "]";
        }

        private String getStackTraceLine() {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : trace) {
                if (!element.getClassName().startsWith(name)) continue;
                if (element.getClassName().startsWith(notname)) continue;
                return conv(element);
            }
            return conv(trace[0]);
        }

        private String conv(StackTraceElement element) {
            assert element.getFileName() != null;
            return element.getFileName().replace(".java", "") + ":" + element.getLineNumber();
        }

        @Override
        public void println(Object o) {
            println(String.valueOf(o));
        }

        @Override
        public void println(boolean x) {
            println(String.valueOf(x));
        }

        @Override
        public void println(char x) {
            println(String.valueOf(x));
        }

        @Override
        public void println(int x) {
            println(String.valueOf(x));
        }

        @Override
        public void println(long x) {
            println(String.valueOf(x));
        }

        @Override
        public void println(float x) {
            println(String.valueOf(x));
        }

        @Override
        public void println(double x) {
            println(String.valueOf(x));
        }

        @Override
        public void println(char[] x) {
            println(String.valueOf(x));
        }
    }

    private static class ErrorStream extends OutputStream {

        private final PrintStream fileStream;

        public ErrorStream(Output output) {
            super();
            fileStream = output.getFileStream(ERR_FILE_PATH);
            PrintStream newStream = new Stream(this, output.getPrintStream());
            System.setErr(newStream);
        }

        @Override
        public void write(int b) {
            fileStream.write(b);
        }

        public static class Stream extends PrintStream {

            private final Output.Stream printstream;
            private int errors;

            public Stream(OutputStream out, Output.Stream printstream) {
                super(out, true);
                this.printstream = printstream;
                errors = -1;
            }

            @Override
            public void print(String x) {
                assert x != null;
                if (x.trim().startsWith("Exception in thread")) return;
                if (!x.trim().startsWith("at ") && !(x.trim().startsWith("... ") && x.contains("more"))) {
                    errors++;
                    if (!Output.debug) printstream.error(x);
                }
                x = Output.Stream.timestamp() + " [" + errors + "]: " + x;
                super.print(x);
                if (Output.debug) {
                    printstream.print(ANSI.RED + x.trim() + ANSI.RESET + "\n");
                }
            }
        }
    }
}
