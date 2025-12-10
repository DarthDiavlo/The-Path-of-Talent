package com.example.nftfabric.service;

import com.example.nftfabric.model.ImageComposeRequest;
import com.example.nftfabric.model.ImageData;
import com.example.nftfabric.repository.ImageDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;

//@Service
public class ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);
    private final ImageDataRepository repository;

    public ImageService(ImageDataRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public BufferedImage composeImages(List<ImageComposeRequest> requests) throws Exception {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("❌ Пустой список запросов на композицию изображения");
        }

        log.info("🧩 Получено {} элементов для генерации изображения", requests.size());

        // 1️⃣ Ищем фон
        List<ImageData> backgrounds = repository.findBySubcategoryAndLevel("Background", 0);
        if (backgrounds.isEmpty()) {
            throw new RuntimeException("❌ Не найден фон (subcategory='Background', level=0)");
        }

        BufferedImage base = ImageIO.read(new ByteArrayInputStream(backgrounds.get(0).getImage()));
        Graphics2D g = base.createGraphics();

        // Настройки рисования
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 2️⃣ Перебираем все запросы и добавляем слои
        int index = 0;
        for (ImageComposeRequest req : requests) {
            log.info("🎨 Обрабатываем категорию='{}', уровень={}, текст='{}'",
                    req.getCategory(), req.getLevel(), req.getText());

            List<ImageData> images = repository.findBySubcategoryAndLevel(req.getCategory(), req.getLevel());
            if (images.isEmpty()) {
                log.warn("⚠️ Нет изображений для category='{}', level={}", req.getCategory(), req.getLevel());
                continue;
            }

            // Берём первое найденное изображение (или можно объединить все)
            ImageData imgData = images.get(0);
            BufferedImage overlay = ImageIO.read(new ByteArrayInputStream(imgData.getImage()));

            // Можно сделать небольшое смещение для видимости слоёв
            int offset = 500 + index * 200;

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.drawImage(overlay, offset, 0, null);
            log.debug("✅ Наложено изображение '{}', смещение ({},{})", req.getCategory(), offset, offset);

            // Добавляем текст, если есть
            if (req.getText() != null && !req.getText().isEmpty()) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.drawString(req.getText(), 50, 100 + index * 50);
                log.info("💬 Добавлен текст '{}'", req.getText());
            }

            index++;
        }

        g.dispose();
        log.info("✅ Финальное изображение успешно сгенерировано из {} слоёв", requests.size());

        return base;
    }
}
