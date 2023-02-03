package ua.orb1t.wrx;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegProgress;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.ProgressListener;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

public class Splinter
{

    public static void main(String[] args) {
        new Splinter().split(Config.videoDir, Config.videoTmpDir, "Battlefield V 2023.02.02 - 22.15.41.52.DVR", Config.outputDurationMillisec);
    }

    void split(String videoDir, String videoTmpDir, String filename, long outputDuration)
    {
        long    inputDuration   = 0;

        //
        // Get input duration
        final AtomicLong durationMillis = new AtomicLong();

        FFmpeg.atPath ( Config.pathToFFmpeg )
        .addInput(UrlInput.fromUrl( videoDir + filename + Config.extension ))
        .addOutput(new NullOutput())
        .setProgressListener(
                new ProgressListener()
                {
                    @Override
                    public void onProgress(FFmpegProgress progress)
                    {
                        durationMillis.set(progress.getTimeMillis());
                    }
                }
            )
        .execute();

        inputDuration = durationMillis.get();

        System.out.println ( "\n\nSplitting : " + filename + " duration: " + inputDuration + " milliseconds");

        //
        // Split Video
        int nVideoOut = (int) Math.ceil( 1.0 * inputDuration / outputDuration );

        System.out.println("Output video count: " + nVideoOut);

        File splitFileDirPath = new File( videoTmpDir + "\\" + filename + "\\" );//Destination folder to save.
        if (!splitFileDirPath.exists()) {
            splitFileDirPath.mkdirs();
            System.out.println("Directory Created -> "+ splitFileDirPath.getAbsolutePath());
        }


        long currPoint = 0;

        for(int n=0; n < nVideoOut; n++)
        {
            long remaining = inputDuration - ( outputDuration * n );

            long currOutputDuration = remaining > outputDuration ? outputDuration : remaining;

            String videoPartSourceName = videoDir + filename + Config.extension;
            String videoPartName = splitFileDirPath.getPath() + "\\" + Config.tempPattern + n + Config.extension;
            System.out.println( videoPartName + " : " + currPoint + " / " + currOutputDuration);

            FFmpeg.atPath(Config.pathToFFmpeg)
            .addInput(
                    UrlInput.fromUrl( videoPartSourceName )
                    .setPosition(currPoint, TimeUnit.MILLISECONDS)
                    .setDuration(currOutputDuration, TimeUnit.MILLISECONDS)
                    )
            .addOutput(
                    UrlOutput.toPath(FileSystems.getDefault().getPath( videoPartName ))
                    .setPosition(0, TimeUnit.MILLISECONDS)
                    )
            .setOverwriteOutput(true)
            .execute();

            currPoint += outputDuration;
        }

        System.out.println ( "Splitting Completed !\n\n" );
    }
}