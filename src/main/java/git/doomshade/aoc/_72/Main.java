package git.doomshade.aoc._72;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>Now, you're ready to choose a directory to delete.</p>
 * <p>The total disk space available to the filesystem is <code><em>70000000</em></code>. To run the update, you need unused space of at least
 * <code><em>30000000</em></code>. You need to find a directory you can delete that will <em>free up enough space</em> to run the update.</p>
 * <p>In the example above, the total size of the outermost directory (and thus the total amount of used space) is <code>48381165</code>; this means that the size of the
 * <em>unused</em> space must currently be <code>21618835</code>, which isn't quite the <code>30000000</code> required by the update. Therefore, the update still requires a
 * directory with total size of at least <code>8381165</code> to be deleted before it can run.</p>
 * <p>To achieve this, you have the following options:</p>
 * <ul>
 * <li>Delete directory <code>e</code>, which would increase unused space by <code>584</code>.</li>
 * <li>Delete directory <code>a</code>, which would increase unused space by <code>94853</code>.</li>
 * <li>Delete directory <code>d</code>, which would increase unused space by <code>24933642</code>.</li>
 * <li>Delete directory <code>/</code>, which would increase unused space by <code>48381165</code>.</li>
 * </ul>
 * <p>Directories <code>e</code> and <code>a</code> are both too small; deleting them would not free up enough space. However, directories <code>d</code> and <code>/</code>
 * are both big enough! Between these, choose the <em>smallest</em>: <code>d</code>, increasing unused space by <code><em>24933642</em></code>.</p>
 * <p>Find the smallest directory that, if deleted, would free up enough space on the filesystem to run the update. <em>What is the total size of that directory?</em></p>
 */
public class Main implements Runnable {
    /**
     * Parameters biconsumer are: current FS instance and arguments
     */
    private static final Map<String, BiConsumer<VirtualFS, String[]>> COMMANDS = new HashMap<>();
    public static final int MAX_ACCEPTED_SIZE = 100_000;

    public static final int MAX_FS_SIZE = 70_000_000;
    public static final int TARGET_UNUSED_SPACE = 30_000_000;

    static {
        COMMANDS.put("cd", (fs, args) -> {
            final String dir = args[0];
            if (dir.equals("/")) {
                fs.cwd = fs.root;
                return;
            }

            if (dir.equals("..")) {
                fs.cwd = fs.cwd.parent;
                return;
            }

            for (VirtualFile child : fs.cwd.children) {
                if (child.directory && child.name.equals(dir)) {
                    fs.cwd = child;
                    return;
                }
            }
        });

        COMMANDS.put("ls", (fs, args) -> {
            if (fs.cwd.initialized) {
                return;
            }

            for (final String singleFile : args) {
                final String[] singleFileArgs = singleFile.split(" ");
                final String fileName = singleFileArgs[1];
                if (singleFileArgs[0].equals("dir")) {
                    fs.cwd.children.add(new VirtualFile(-1, fileName, true, fs.cwd));
                } else {
                    final int size = Integer.parseInt(singleFileArgs[0]);
                    fs.cwd.children.add(new VirtualFile(size, fileName, false, fs.cwd));
                }
                fs.cwd.initialized = true;
            }
        });
    }

    private static class VirtualFS {
        private final VirtualFile root = new VirtualFile(-1, "/", true, null);
        private VirtualFile cwd = root;
    }

    private static class VirtualFile {
        private final int size;
        private final String name;
        private final boolean directory;
        private final List<VirtualFile> children = new ArrayList<>();
        private VirtualFile parent;

        private boolean initialized = false;

        private VirtualFile(final int size, final String name, final boolean directory, final VirtualFile parent) {
            this.size = size;
            this.name = name;
            this.directory = directory;
            this.parent = parent;
        }

        public void addChildren(Collection<? extends VirtualFile> children) {
            this.children.addAll(children);
        }

        public int getSize() {
            // if it's a regular file just return its size
            if (!directory) {
                return size;
            }
            // if it's a directory compute the size of the children
            return children.stream()
                           .mapToInt(VirtualFile::getSize)
                           .sum();
        }
    }

    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            final VirtualFS fs = new VirtualFS();
            fs.root.parent = fs.root;

            for (int i = 0; i < input.size(); i++) {
                final String s = input.get(i);
                final String[] split = s.split(" ");
                final String cmd = split[1];
//                System.out.printf("Found command: \"%s\"%n", cmd);
                final String[] args;

                if (cmd.equals("cd")) {
                    args = new String[] {split[2]};
                } else {
                    final List<String> temp = new ArrayList<>();
                    for (int j = i + 1; j < input.size(); j++) {
                        final String lsLine = input.get(j);
                        if (lsLine.startsWith("$")) {
                            break;
                        }
                        temp.add(lsLine);
                        i++;
                    }
                    args = temp.toArray(new String[0]);
                }
//                System.out.printf("Args: \"%s\"%n", Arrays.toString(args));
                COMMANDS.get(cmd)
                        .accept(fs, args);
//                System.out.printf("Cwd: %s%n%n", fs.cwd.name);
            }


            final int totalSize = fs.root.getSize();
            final int unusedSize = MAX_FS_SIZE - totalSize;
            final int targetDeleteSize = TARGET_UNUSED_SPACE - unusedSize;
            System.out.printf("Current size: %d%nSize to delete: %d%n", totalSize, targetDeleteSize);
            sumSize(fs.root, "", targetDeleteSize);
            System.out.println(size);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int size = Integer.MAX_VALUE;

    private static void sumSize(VirtualFile file, String indent, int targetDeleteSize) {
//        System.out.printf("%s- %s (%s)%n", indent, file.name, file.directory ? "dir, " + file.getSize() : "file, size=" + file.size);
        if (file.directory) {
            // calculate the sum of the current dir
            // and add it to the result if the size is within limits
            final int temp = file.getSize();
            if (temp <= size && temp >= targetDeleteSize) {
                size = temp;
            }

            // loop over other directories
            // and call their sum recursively
            // if the dir sum is <= accepted size, add it to the result
            for (VirtualFile child : file.children) {
                if (child.directory) {
                    sumSize(child, indent + "\t", targetDeleteSize);
                }
            }
        }
    }
}
