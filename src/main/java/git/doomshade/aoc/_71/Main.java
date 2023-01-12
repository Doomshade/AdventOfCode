package git.doomshade.aoc._71;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * <h2>--- Day 7: No Space Left On Device ---</h2><p>You can hear birds chirping and raindrops hitting leaves as the expedition proceeds. Occasionally, you can even hear
 * much louder sounds in the distance; how big do the animals get out here, anyway?</p>
 * <p>The device the Elves gave you has problems with more than just its communication system. You try to run a system update:</p>
 * <pre><code>$ system-update --please --pretty-please-with-sugar-on-top
 * <span title="E099 PROGRAMMER IS OVERLY POLITE">Error</span>: No space left on device
 * </code></pre>
 * <p>Perhaps you can delete some files to make space for the update?</p>
 * <p>You browse around the filesystem to assess the situation and save the resulting terminal output (your puzzle input). For example:</p>
 * <pre><code>
 * $ cd /
 * $ ls
 * dir a
 * 14848514 b.txt
 * 8504156 c.dat
 * dir d
 * $ cd a
 * $ ls
 * dir e
 * 29116 f
 * 2557 g
 * 62596 h.lst
 * $ cd e
 * $ ls
 * 584 i
 * $ cd ..
 * $ cd ..
 * $ cd d
 * $ ls
 * 4060174 j
 * 8033020 d.log
 * 5626152 d.ext
 * 7214296 k
 * </code></pre>
 * <p>The filesystem consists of a tree of files (plain data) and directories (which can contain other directories or files). The outermost directory is called
 * <code>/</code>. You can navigate around the filesystem, moving into or out of directories and listing the contents of the directory you're currently in.</p>
 * <p>Within the terminal output, lines that begin with <code>$</code> are <em>commands you executed</em>, very much like some modern computers:</p>
 * <ul>
 * <li><code>cd</code> means <em>change directory</em>. This changes which directory is the current directory, but the specific result depends on the argument:
 *   <ul>
 *   <li><code>cd x</code> moves <em>in</em> one level: it looks in the current directory for the directory named <code>x</code> and makes it the current directory.</li>
 *   <li><code>cd ..</code> moves <em>out</em> one level: it finds the directory that contains the current directory, then makes that directory the current directory.</li>
 *   <li><code>cd /</code> switches the current directory to the outermost directory, <code>/</code>.</li>
 *   </ul>
 * </li>
 * <li><code>ls</code> means <em>list</em>. It prints out all of the files and directories immediately contained by the current directory:
 *   <ul>
 *   <li><code>123 abc</code> means that the current directory contains a file named <code>abc</code> with size <code>123</code>.</li>
 *   <li><code>dir xyz</code> means that the current directory contains a directory named <code>xyz</code>.</li>
 *   </ul>
 * </li>
 * </ul>
 * <p>Given the commands and output in the example above, you can determine that the filesystem looks visually like this:</p>
 * <pre><code>
 * - / (dir)
 *   - a (dir)
 *     - e (dir)
 *       - i (file, size=584)
 *     - f (file, size=29116)
 *     - g (file, size=2557)
 *     - h.lst (file, size=62596)
 *   - b.txt (file, size=14848514)
 *   - c.dat (file, size=8504156)
 *   - d (dir)
 *     - j (file, size=4060174)
 *     - d.log (file, size=8033020)
 *     - d.ext (file, size=5626152)
 *     - k (file, size=7214296)
 * </code></pre>
 * <p>Here, there are four directories: <code>/</code> (the outermost directory), <code>a</code> and <code>d</code> (which are in <code>/</code>), and <code>e</code> (which
 * is in <code>a</code>). These directories also contain files of various sizes.</p>
 * <p>Since the disk is full, your first step should probably be to find directories that are good candidates for deletion. To do this, you need to determine the <em>total
 * size</em> of each directory. The total size of a directory is the sum of the sizes of the files it contains, directly or indirectly. (Directories themselves do not count
 * as having any intrinsic size.)</p>
 * <p>The total sizes of the directories above can be found as follows:</p>
 * <ul>
 * <li>The total size of directory <code>e</code> is <em>584</em> because it contains a single file <code>i</code> of size 584 and no other directories.</li>
 * <li>The directory <code>a</code> has total size <em>94853</em> because it contains files <code>f</code> (size 29116), <code>g</code> (size 2557), and <code>h.lst</code>
 * (size 62596), plus file <code>i</code> indirectly (<code>a</code> contains <code>e</code> which contains <code>i</code>).</li>
 * <li>Directory <code>d</code> has total size <em>24933642</em>.</li>
 * <li>As the outermost directory, <code>/</code> contains every file. Its total size is <em>48381165</em>, the sum of the size of every file.</li>
 * </ul>
 * <p>To begin, find all of the directories with a total size of <em>at most 100000</em>, then calculate the sum of their total sizes. In the example above, these
 * directories are <code>a</code> and <code>e</code>; the sum of their total sizes is <code><em>95437</em></code> (94853 + 584). (As in this example, this process can count
 * files more than once!)</p>
 * <p>Find all of the directories with a total size of at most 100000. <em>What is the sum of the total sizes of those directories?</em></p>
 */
public class Main implements Runnable {
    /**
     * Parameters biconsumer are: current FS instance and arguments
     */
    private static final Map<String, BiConsumer<VirtualFS, String[]>> COMMANDS = new HashMap<>();
    public static final int MAX_ACCEPTED_SIZE = 100_000;

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


            sumSize(fs.root, "");
            System.out.println(size);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int size = 0;

    private static void sumSize(VirtualFile file, String indent) {
        System.out.printf("%s- %s (%s)%n", indent, file.name, file.directory ? "dir, " + file.getSize() : "file, size=" + file.size);
        if (file.directory) {
            // calculate the sum of the current dir
            // and add it to the result if the size is within limits
            final int temp = file.getSize();
            if (temp <= MAX_ACCEPTED_SIZE) {
                System.out.printf("Adding %s (%d)%n", file.name, temp);
                size += temp;
            }

            // loop over other directories
            // and call their sum recursively
            // if the dir sum is <= accepted size, add it to the result
            for (VirtualFile child : file.children) {
                if (child.directory) {
                    sumSize(child, indent + "\t");
                }
            }
        }
    }
}
