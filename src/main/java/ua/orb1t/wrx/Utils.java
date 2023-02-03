package ua.orb1t.wrx;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Utils {

    static class Tuple<A, B> {
        public A a;
        public B b;

        public Tuple(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static File[] getSourceFiles(String path, String extension) {
        File dir = new File(path);
        File[] lst = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(extension);
            }
        });
        return lst;
    }

    public static void splitSourceFiles(File[] lst, String videoDir, String videoTmpDir, String extension, long outputDuration) {
        for (int i = 0; i < lst.length; i++) {
//            System.out.println(lst[i].getName());
            String fname = lst[i].getName();
            var idx = fname.lastIndexOf(extension);
            new Splinter().split(videoDir, videoTmpDir, fname.substring(0, idx), outputDuration);
        }
    }

    public static File[] getSplittedFolders(String videoTmpDir, String extension) {
        File tmpdir = new File ( videoTmpDir );
        File[] tmplst = tmpdir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return dir.isDirectory() || name.toLowerCase().endsWith(extension);
            }
        });
        return tmplst;
    }

    public static void concatenateResuls(int count, File[] tmplst) {
        var ca = new Concattener();
        ca.split(count, Arrays.asList(tmplst));
    }

    public static Tuple<Integer, Integer> getSplittedFilesMinMax(File[] tmplst, String extension) {
        Map<String, List<File>> sblstall = new HashMap<>();
        for (int i = 0; i < tmplst.length; i++) {
            File subtmpdir = new File(tmplst[i].getPath().toString());
            File[] subtmplst = subtmpdir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return dir.isDirectory() || name.toLowerCase().endsWith(extension);
                }
            });
            List<File> sblst = List.of(subtmplst);
            sblstall.put(subtmpdir.getName(), sblst);
        }
        var largestCount = 0;
        var smallestCount = 999;
        for (var sbval : sblstall.values()
        ) {
            largestCount = sbval.size() > largestCount ? sbval.size() : largestCount;
            smallestCount = sbval.size() > 0 && smallestCount > sbval.size() ? sbval.size() : smallestCount;
        }

        return new Tuple<>(smallestCount, largestCount);
    }
}
