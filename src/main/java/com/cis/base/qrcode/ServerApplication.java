package com.cis.base.qrcode;

import com.cis.base.qrcode.config.core.boot.AbstractApplicationConfig;
import com.cis.base.qrcode.config.core.boot.ExitUtil;
import com.cis.base.qrcode.config.core.boot.TomcatConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import java.io.IOException;

public class ServerApplication {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext ctx = SpringApplication.run(Configuration.class, args);
		ExitUtil.waitForKeyPressToCleanlyExit(ctx);
	}

	@Import({TomcatConfig.class})
	private static class Configuration extends AbstractApplicationConfig {
	}

}
