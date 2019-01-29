package cn.jinelei.rainbow.blog.controller.impl;

import cn.jinelei.rainbow.blog.controller.FileController;
import cn.jinelei.rainbow.blog.exception.BlogException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author zhenlei
 */

@RestController
@ResponseBody
@RequestMapping(
        value = "/file",
        consumes = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_JSON_UTF8_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.MULTIPART_FORM_DATA_VALUE,
                MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                MediaType.ALL_VALUE
        },
        produces = {
                MediaType.APPLICATION_JSON_UTF8_VALUE,
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                MediaType.ALL_VALUE
        })
public class FileControllerImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileControllerImpl.class);

    @Value("${upload.file.image}")
    String uploadImageDir;

    @Autowired
    HttpServletRequest request;

    @RequestMapping(value = "/image")
    public String updateImage(@RequestParam MultipartFile file) throws BlogException {
        if (!file.isEmpty()) {
            LOGGER.debug("upload image: " + file.getOriginalFilename() + " size: " + file.getSize());
            String uploadPathString = request.getServletContext().getRealPath(uploadImageDir);
            File tmp = new File(uploadPathString);
            if (!tmp.exists()) {
                tmp.mkdirs();
            }
            File imageFile = new File(tmp, file.getOriginalFilename());
            while (imageFile.exists()) {
                imageFile = new File(tmp, UUID.randomUUID().toString().substring(0, 4) + file.getOriginalFilename());
            }
            try {
                file.transferTo(imageFile);
                return String.format("%s/%s", uploadImageDir, imageFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
                throw new BlogException.UploadImageFailed();
            }
        }
        throw new BlogException.EmptyImage();
    }
}
