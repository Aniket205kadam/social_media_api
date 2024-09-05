package dev.aniket.Instagram_api;

import dev.aniket.Instagram_api.service.impl.FfmpegService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InstagramApplicationTests {

	@Autowired
	private FfmpegService ffmpegService;

	@Test
	public void isVideoLessThan30sTest() {

	}

}