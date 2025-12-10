package com.example.nftfabric.service;

import com.example.nftfabric.model.ImageComposeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Service
public class ImageLowService {
    private static final Logger log = LoggerFactory.getLogger(ImageLowService.class);

    private List<BufferedImage> loadImagesBySubcategory(String subcategory) throws Exception {
        List<BufferedImage> images = new ArrayList<>();
        String pathPrefix = "/images/";

        String[] extensions = {".png"}; // можно добавить ".jpg", ".jpeg"
        for (String ext : extensions) {
            String filename = pathPrefix + subcategory + ext;
            try (InputStream is = getClass().getResourceAsStream(filename)) {
                if (is != null) {
                    images.add(ImageIO.read(is));
                }
            }
        }

        if (images.isEmpty()) {
            log.warn("⚠️ Не найдено изображений для подкатегории '{}'", subcategory);
        } else {
            log.info("🖼 Загружено {} изображений для подкатегории '{}'", images.size(), subcategory);
        }

        return images;
    }

    public BufferedImage composeImages(List<ImageComposeRequest> requests, String text) throws Exception {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("❌ Пустой список запросов на композицию изображения");
        }

        log.info("🧩 Получено {} элементов для генерации изображения", requests.size());

        // Загружаем фон
        List<BufferedImage> backgrounds = loadImagesBySubcategory("Background");
        if (backgrounds.isEmpty()) {
            throw new RuntimeException("❌ Не найден фон (Background)");
        }
        BufferedImage base = backgrounds.get(0);

        Graphics2D g = base.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Добавляем все изображения из запросов
        int offsetX = 100;
        int offsetY = 300;
        int maxHeightInRow = 0;

        for (ImageComposeRequest req : requests) {
            List<BufferedImage> overlays = loadImagesBySubcategory(req.getCategory());
            if (overlays.isEmpty()) continue;

            BufferedImage overlay = overlays.get(0);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.drawImage(overlay, offsetX, offsetY, null);

            // Позиционирование следующего изображения
            offsetX += overlay.getWidth() + 20; // 20px отступ
            maxHeightInRow = Math.max(maxHeightInRow, overlay.getHeight());
            if (offsetX > base.getWidth() - overlay.getWidth()) {
                offsetX = 0;
                offsetY += maxHeightInRow + 20;
                maxHeightInRow = 0;
            }
        }

        // Добавляем общий текст
        if (text != null && !text.isEmpty()) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString(text, 50, 50); // можно менять положение
        }

        g.dispose();
        log.info("✅ Финальное изображение успешно сгенерировано из {} слоёв", requests.size());

        return base;
    }
}
