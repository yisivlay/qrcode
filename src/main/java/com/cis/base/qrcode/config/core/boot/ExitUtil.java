package com.cis.base.qrcode.config.core.boot;

import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author YSivlay
 */
public abstract class ExitUtil {

    private ExitUtil() {
    }

    public static void waitForKeyPressToCleanlyExit(ConfigurableApplicationContext ctx) throws IOException {

        System.out.println("\nHit Enter to quit...");
        BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
        d.readLine();

        ctx.stop();
        ctx.close();
    }
}
