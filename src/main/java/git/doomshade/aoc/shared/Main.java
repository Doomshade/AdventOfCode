package git.doomshade.aoc.shared;

public class Main {
    private static final String BASE_PACKAGE = "git.doomshade.aoc._";

    public static void main(String[] args) throws ReflectiveOperationException {
        final int aocDay = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        final Class<?> mainClass = Class.forName(BASE_PACKAGE.concat(String.valueOf(aocDay))
                                                             .concat(".Main"));
        final Object main = mainClass.getConstructor()
                                     .newInstance();
        if (main instanceof Runnable runnable) {
            runnable.run();
        }
    }
}
