package com.ritzjucy.technicaldrawingsbackend.entity;

import com.ritzjucy.technicaldrawingsbackend.entity.converter.ResultElementConverter;
import com.ritzjucy.technicaldrawingsbackend.model.ResultElementModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "result")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name = "id")
    private Long id;

    @Getter
    @Setter
    @OneToOne(optional = false)
    @JoinColumn(name="image_id", nullable=false, updatable=false)
    private ImageEntity image;

    @Getter
    @Setter
    @Lob
    @Column(name = "text_recognition_image_data")
    private byte[] textRecognitionImageData;

    @Getter
    @Setter
    @Builder.Default
    @Lob
    @Column(name = "elements", columnDefinition = "CLOB")
    @Convert(converter = ResultElementConverter.class)
    private List<ResultElementModel> elements = new ArrayList<>();

    @Getter
    @Setter
    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Getter
    @Setter
    @Column(name = "updated_at")
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

}