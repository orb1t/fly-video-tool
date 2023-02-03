package ua.orb1t.wrx;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Config {

    public static final Path pathToFFmpeg    = FileSystems.getDefault().getPath(".\\bin");
    public static final String  videoDir        = "D:\\_data_\\";
    public static final String  videoTmpDir     = videoDir + "tmp/";
    public static final String  videoOutDir     = "data/" + "out/";
    public static final long outputDurationMillisec = 500;

    public static final String extension = ".mp4";
    public static final String outFilename = "finalClip";
    public static final String tempPattern = "part_";
    public static boolean cleanUpTmp = true;
    public static boolean useAllParts = true;
}
