package com.ritzjucy.technicaldrawingsbackend.service;

import com.ritzjucy.technicaldrawingsbackend.entity.ImageEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectStatus;
import com.ritzjucy.technicaldrawingsbackend.entity.ResultEntity;
import com.ritzjucy.technicaldrawingsbackend.model.AIDetection;
import com.ritzjucy.technicaldrawingsbackend.model.ResultElementModel;
import com.ritzjucy.technicaldrawingsbackend.util.ImageUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiDetectionService
{
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManager entityManager;

    @Async
    public void runAsync(Long projectId, boolean all)
    {
        log.info("started async ai detection for project id: {}", projectId);

        @SuppressWarnings("DataFlowIssue")
        long count = new TransactionTemplate(transactionManager).execute(status -> {
            try {
                ProjectEntity project = entityManager.find(ProjectEntity.class, projectId);
                entityManager.lock(project, LockModeType.PESSIMISTIC_READ);

                return all ? project.getImages().size() : project.getImages().stream()
                        .filter(i -> i.getResult() == null)
                        .count();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        try {
            //Thread.sleep(20000 * count);
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        new TransactionTemplate(transactionManager).execute(status -> {
            try {
            ProjectEntity project = entityManager.find(ProjectEntity.class, projectId);
            entityManager.lock(project, LockModeType.PESSIMISTIC_WRITE);

            for (ImageEntity image : project.getImages()) {
                if (image.getResult() != null) {
                    continue;
                }

                Set<AIDetection> detections = detect(image.getData());

                BufferedImage recognitionImage = ImageUtil.bytesToImage(image.getData());
                detections.forEach(detection -> ImageUtil.draw(recognitionImage, detection));

                byte[] recognitionBytes = ImageUtil.imageToBytes(recognitionImage, image.getType());

                ResultEntity result = ResultEntity.builder()
                        .elements(detections.stream()
                                .map(ResultElementModel::from)
                                .toList())
                        .textRecognitionImageData(recognitionBytes)
                        .image(image)
                        .build();

                image.setResult(result);
            }

            project.setStatus(ProjectStatus.COMPLETED);

            entityManager.merge(project);

            return null;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        log.info("finished async ai detection for project id: {}", projectId);
    }

    public Set<AIDetection> detect(byte[] data) throws IOException
    {
        ITesseract instance = new Tesseract();
        instance.setLanguage("deu+eng");
        instance.setDatapath("./tessdata");

        BufferedImage imageIn = ImageIO.read(new ByteArrayInputStream(data));

        return instance.getWords(imageIn, ITessAPI.TessPageIteratorLevel.RIL_WORD)
                .stream()
                .map(w -> new AIDetection(
                        w.getText(),
                        w.getBoundingBox().x,
                        w.getBoundingBox().y,
                        w.getBoundingBox().width,
                        w.getBoundingBox().height,
                        w.getConfidence()))
                .collect(Collectors.toSet());
    }
}
