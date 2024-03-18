package com.ritzjucy.technicaldrawingsbackend.model;

public record AIDetection(String text, int x, int y, int width, int height, double confidence) {}
