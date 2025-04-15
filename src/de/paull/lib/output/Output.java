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

public class Output extends OutputStream implements AutoCloseable {

    public static boolean debug;
    private final PrintStream defStream;
    private final PrintStream fileStream;
    private final PrintStream newStream;

    public Output(boolean debug) {
        super();
        Output.debug = debug;
        defStream = System.out;
        fileStream = getFileStream("LOG_FILE");
        newStream = new Stream(this);
        System.setOut(newStream);
        new ErrorStream(this);
    }

    public Stream getPrintStream() {
        return (Stream) newStream;
    }

    public PrintStream getFileStream(String logfield) {
        File f = new File(ConfigHandler.get(logfield));
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
            x = ANSI.BLUE + timestamp() + ":" + ANSI.RESET + " " + x;
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

}
