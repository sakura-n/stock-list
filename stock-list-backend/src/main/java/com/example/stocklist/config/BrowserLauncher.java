package com.example.stocklist.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

/**
 * 桌面程序启动后自动打开默认浏览器指向首页。
 * 仅在打包场景启用（jpackage 通过 -Dapp.open-browser=true 传入），
 * IDEA / mvn spring-boot:run 等开发环境默认关闭，避免每次启动都弹网页。
 */
@Slf4j
@Component
public class BrowserLauncher {

    @Value("${app.open-browser:false}")
    private boolean openBrowser;

    @Value("${server.port:8090}")
    private int serverPort;

    @EventListener(ApplicationReadyEvent.class)
    public void openHomePage() {
        if (!openBrowser) {
            return;
        }
        if (java.awt.GraphicsEnvironment.isHeadless() || !Desktop.isDesktopSupported()) {
            log.warn("当前环境无桌面支持，请手动用浏览器访问 http://localhost:{}", serverPort);
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            log.warn("当前系统浏览器不支持自动打开，请访问 http://localhost:{}", serverPort);
            return;
        }
        String url = "http://localhost:" + serverPort;
        try {
            desktop.browse(URI.create(url));
            log.info("已自动打开浏览器: {}", url);
        } catch (Exception e) {
            log.warn("自动打开浏览器失败，请手动访问 {}（原因: {}）", url, e.getMessage());
        }
    }
}
