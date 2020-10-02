package toolkit;

public class LazyUtils {
    //thanks to https://stackoverflow.com/questions/6558008/learning-java-how-to-make-a-short-alias-for-system-out-println
    //for the print alias
    public static <PrintableToString> void print(PrintableToString... args) {
        for(PrintableToString pts: args)
            System.out.print(pts);
        System.out.println();
    }
}
