package com.example.onlybuns.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String description;

    public String imagePath;

    public Double latitude;
    public Double longitude;

    public LocalDateTime creationTime;

    
    @Column(name = "compress_Time")
    public LocalDateTime compressTime;

    
    @Column(name = "is_Compressed")
    public int isCompressed;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "acc_id")
    private Account account;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String image) {
        this.imagePath = image;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getCompressTime() {
        return compressTime;
    }

    public void setCompressTime(LocalDateTime compressTime) {
        this.compressTime = compressTime;
    }

    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    public boolean getDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    public int getIsCompressed() {
        return isCompressed;
    }
    public void setIsCompressed(int isCompressed) {
        this.isCompressed = isCompressed;
    }

    

}
