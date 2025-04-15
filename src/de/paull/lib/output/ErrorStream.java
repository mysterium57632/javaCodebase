package de.paull.lib.output;

import java.io.OutputStream;
import java.io.PrintStream;

public class ErrorStream extends OutputStream {

    private final PrintStream fileStream;

    public ErrorStream(Output output) {
        super();
        fileStream = output.getFileStream("ERR_FILE");
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
