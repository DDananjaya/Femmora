package com.femmora.productservice.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_colors")
public class ProductColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String hex;

    @ElementCollection
    @CollectionTable(name = "product_color_images", joinColumns = @JoinColumn(name = "product_color_id"))
    @Column(name = "image_url", length = 1000)
    private List<String> images;

    public ProductColor() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHex() { return hex; }
    public void setHex(String hex) { this.hex = hex; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}