package com.ritzjucy.technicaldrawingsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "image")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name = "id")
    private Long id;

    @Getter
    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name="project_id", nullable=false, updatable=false)
    private ProjectEntity project;

    @Getter
    @Setter
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @Column(name = "old_name")
    private String oldName;

    @Getter
    @Setter
    @Column(name = "type")
    private String type;

    @Getter
    @Setter
    @Lob
    @Column(name = "data")
    private byte[] data;

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

    @Getter
    @Setter
    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private ResultEntity result;

    public String getDisplayOldName()
    {
        return oldName + "." + type;
    }

    public String getDisplayName()
    {
        return name + "." + type;
    }

}