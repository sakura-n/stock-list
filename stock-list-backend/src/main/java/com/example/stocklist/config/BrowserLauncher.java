package com.example.stocklist.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

/**
 * 桌面程序启动后自动打开默认浏览器指向首页。
 * 仅在打包场景启用（jpackage 通过 -Dapp.open-browser=true 传入），
 * IDEA / mvn spring-boot:run 等开发环境默认关闭，避免每次启动都弹网页。
 *
 * 打开策略：优先系统原生命令（不受 java.awt.headless 影响），失败再退回 AWT Desktop。
 */
@Slf4j
@Component
public class BrowserLauncher {

    @Value("${app.open-browser:false}")
    private boolean openBrowser;

    @Value("${server.port:8090}")
    private int serverPort;

    /** 实际绑定端口（打包版用 server.port=0 随机端口时，以此为准） */
    private volatile int actualPort = -1;

    @EventListener(WebServerInitializedEvent.class)
    public void onWebServerInitialized(WebServerInitializedEvent event) {
        this.actualPort = event.getWebServer().getPort();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openHomePage() {
        if (!openBrowser) {
            return;
        }
        int port = actualPort > 0 ? actualPort : serverPort;
        String url = "http://localhost:" + port;
        if (openWithOsCommand(url)) {
            log.info("已自动打开浏览器: {}", url);
            return;
        }
        if (openWithAwtDesktop(url)) {
            log.info("已自动打开浏览器(AWT): {}", url);
            return;
        }
        log.warn("无法自动打开浏览器，请手动访问 {}", url);
    }

    /** 调用系统命令打开 URL，不受 java.awt.headless 影响 */
    private boolean openWithOsCommand(String url) {
        String os = System.getProperty("os.name", "").toLowerCase();
        try {
            ProcessBuilder pb;
            if (os.contains("mac")) {
                pb = new ProcessBuilder("open", url);
            } else if (os.contains("win")) {
                pb = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
            } else {
                pb = new ProcessBuilder("xdg-open", url);
            }
            pb.redirectErrorStream(true);
            int code = pb.start().waitFor();
            return code == 0;
        } catch (Exception e) {
            log.debug("系统命令打开浏览器失败: {}", e.getMessage());
            return false;
        }
    }

    /** AWT Desktop 兜底（需非 headless） */
    private boolean openWithAwtDesktop(String url) {
        try {
            if (java.awt.GraphicsEnvironment.isHeadless() || !Desktop.isDesktopSupported()) {
                return false;
            }
            Desktop desktop = Desktop.getDesktop();
            if (!desktop.isSupported(Desktop.Action.BROWSE)) {
                return false;
            }
            desktop.browse(URI.create(url));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
