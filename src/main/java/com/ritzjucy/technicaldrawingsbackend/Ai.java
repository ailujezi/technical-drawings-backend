package com.ritzjucy.technicaldrawingsbackend;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Ai
{
    public static List<Ai> all = List.of(
            Ai.builder().id(1L).name("Standard AI").description("Basic AI using NNs").build(),
            Ai.builder().id(2L).name("KNN AI").description("Simple AI using k nearest neighbors").build(),
            Ai.builder().id(3L).name("CNN AI").description("AI using Convolutional Neural Networks").build()
    );

    public Long id;
    public String name;
    public String description;

}
