package dev.aniket.Instagram_api.service.impl;

import dev.aniket.Instagram_api.exception.FfmpegException;
import dev.aniket.Instagram_api.utility.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FfmpegService {
    @Value("${story}")
    private String storyLocation;

    // get video duration
    public double isVideoLessThan30s(Path storyPath) throws IOException {
        // command for find the video duration
        String[] command = {
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                ".\\" + storyPath.toString()
        };

        // print ffmpeg command
        log.info("Story path is: .\\" + storyPath.toString());

        // Initialize ProcessBuilder with the ffprobe command
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // start the process
        Process process = processBuilder.start();

        // read the output
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String durationInString = bufferedReader.readLine();

        bufferedReader.close();

        double duration = Double.parseDouble(durationInString);

        return duration;
    }

    // get videos first 30s
    public Path trimVideoToFirst30s(Path storyPath, String originalFilename, long size, double videoStart, double videoEnd) throws IOException, InterruptedException, FfmpegException {
        String uniqueFilename = String.valueOf(UUID.randomUUID());

        // add extension
        uniqueFilename += ("." + UtilityClass.getFileExtension(originalFilename, size));

        Path newStoryPath = Paths.get(storyLocation, uniqueFilename);

//        String[] command = {
//                "ffmpeg", "-i", storyPath.toString(),
//                "-t", "30",
//                "-c", "copy", newStoryPath.toString()
//        };

        String[] command = {
                "ffmpeg",
                "-i", storyPath.toString(),
                "-ss", ""+videoStart,
                "-to", ""+videoEnd,
                "-c", "copy",
                newStoryPath.toString()
        };


        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        int status = process.waitFor();

        // return path of new video
        if (status == 0) {
            Files.delete(storyPath);
            return newStoryPath;
        }
        throw new FfmpegException("trimVideoToFirst30s() method return error!, with status code: " + status);
    }
}
