package com.freighthub.core.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UploadToCloudinary {

    private String logoUrl;

    public String uploadImage(Cloudinary cloudinary, String base64Image) {

        cloudinary.config.secure = true;

        try {
            // Upload the image
            Map uploadResult = cloudinary.uploader().upload(base64Image, ObjectUtils.asMap("resource_type", "image"));
            this.logoUrl = (String) uploadResult.get("url");

            System.out.println("Logo URL: " + logoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return logoUrl;
    }
}
