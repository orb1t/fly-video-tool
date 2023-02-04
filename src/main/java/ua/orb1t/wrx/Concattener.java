package ua.orb1t.wrx;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Concattener
    {

        void split(int count, List<File> fnames) {

            FFmpeg ffmpeg = FFmpeg.atPath(Config.pathToFFmpeg);

            int ttlParts = 0;
            System.out.println("\n\nConcattener Started" );
            System.out.println("Parts for final Clip :" );
            for (int j = 0; j <= count; j++) {
                for (int i = 0; i < fnames.size(); i++) {
                    var path = fnames.get(i).getPath();
                    String partPath = path + "\\" + Config.tempPattern + j + Config.extension;

                    File partPathFile = new File ( partPath );
                    if ( partPathFile.exists() ) {
                        ffmpeg
                                .addInput(
                                        UrlInput.fromUrl( partPath )
                                                .setDuration ( Config.outputDurationMillisec, TimeUnit.MILLISECONDS)
                                );
                                //.addArguments("-crf", "0");
                        ttlParts++;
                        System.out.println( "#" + ttlParts + " @ path = " + partPath );
                    }
                }
            }

            StringBuilder concatBuilder = new StringBuilder("\"");
            for (int i = 0; i < ttlParts; ++i) {
                concatBuilder
                        .append("[")
                        .append(i)
                        .append(":v:0]");
                concatBuilder
                        .append("[")
                        .append(i)
                        .append(":a:0]");
            }
            concatBuilder.append("concat=n=")
                    .append ( ttlParts )
                    .append(":v=1:a=1[outv][outa]")
                    .append("\"");

            String filter_complex = concatBuilder.toString();
            System.out.println("filter_complex = " + filter_complex);
            ffmpeg.addArguments("-filter_complex", filter_complex );
            ffmpeg.addArguments("-map", "\"[outv]\"");
            ffmpeg.addArguments("-map", "\"[outa]\"");

            File videoOutDirPath = new File( Config.videoOutDir );//Destination folder to save.
            if (!videoOutDirPath.exists()) {
                videoOutDirPath.mkdirs();
                System.out.println("Directory Created -> "+ videoOutDirPath.getAbsolutePath());
            }

//            String outName = Config.videoOutDir + Config.outFilename + Config.extension;
            String outName = videoOutDirPath + "\\" + Config.outFilename + Config.extension;
            System.out.println("outName = " + outName);
            ffmpeg.addOutput(
                            UrlOutput.toPath(FileSystems.getDefault().getPath( outName ))
                                    .setFrameRate(30)
//                                    .addArguments("-maxrate", "1M")
//                                    .addArguments("-minrate", "1M")
//                                    .addArguments("-ac", "1")
//                                    .addArguments("-ar", Integer.toString(15000))
//                                    .setFrameSize ( 800, 600 )
                    )
//                    .addArguments("-ac", "1")
//                    .addArguments("-ar", Integer.toString(15000))

                    .addArguments("-b", "12000k")
                    .addArguments("-maxrate", "12000k")
                    .addArguments("-minrate", "12000k")
                    .addArguments("-bufsize", "12000k")
//                    .addArguments("-crf", "26")
////                    .addArguments("-ar", Integer.toString(15000))
////                    .addArguments("-crf", "0")
//                    .addArguments("-maxrate", "1M")
//                    .addArguments("-minrate", "1M")
                    .setOverwriteOutput(true)
                    .execute();
        }

    }