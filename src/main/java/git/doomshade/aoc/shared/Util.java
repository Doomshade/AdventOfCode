package git.doomshade.aoc.shared;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Util {

    public static byte[] readByteInput(final Class<?> srcClass, final String fileName) throws IOException {
        try (InputStream inputStream = getInputStream(srcClass, fileName)) {
            return inputStream.readAllBytes();
        }
    }

    public static List<String> readStringInput(final Class<?> srcClass, final String fileName) throws IOException {
        try (InputStream inputStream = getInputStream(srcClass, fileName)) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                return br.lines().toList();
            }
        }
    }

    private static InputStream getInputStream(final Class<?> srcClass, final String fileName) throws IOException {
        final URL resource = srcClass.getResource(fileName);
        System.out.println(resource);
        return Objects.requireNonNull(resource)
                      .openStream();
    }
}
