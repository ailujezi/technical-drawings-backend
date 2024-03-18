package com.ritzjucy.technicaldrawingsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name = "id")
    private Long id;

    @Getter
    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name="app_user_id", nullable=false, updatable=false)
    private UserEntity user;

    @Getter
    @Setter
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @Column(name = "description")
    private String description;

    @Getter
    @Setter
    @Column(name = "ai_model_id")
    private Long aiModelId;

    @Getter
    @Setter
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Getter
    @Setter
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ImageEntity> images = new ArrayList<>();

    @Getter
    @Setter
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Getter
    @Setter
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

}