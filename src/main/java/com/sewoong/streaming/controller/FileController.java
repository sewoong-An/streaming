package com.sewoong.streaming.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {
    
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${file.path}")
    private String tempPath;

    

    public String videoUpload(MultipartFile file) {
        try {

            UUID uuid = UUID.randomUUID();
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            String videoUrl = "/video/" + uuid + "." + extension;

            File uploadPath = new File(tempPath + videoUrl);
            file.transferTo(uploadPath);

            return videoUrl;
        }  catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String thumbnailUpload(MultipartFile file, String origin) {
        try {

            if(!origin.equals("")) {
                // amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, "thumbnail/" + originKey));
                File imgfile = new File(tempPath + origin);
                imgfile.delete();
            }

            UUID uuid = UUID.randomUUID();
            String thumbnailUrl = "/thumbnail/" + uuid + ".png";
            File uploadPath = new File(tempPath + thumbnailUrl);
            file.transferTo(uploadPath);

            // String uploadKey = "thumbnail/" + uuid;
            // String fileUrl= "https://" + bucket + ".s3." + region + ".amazonaws.com/" + uploadKey;

            // ObjectMetadata metadata= new ObjectMetadata();
            // metadata.setContentType(file.getContentType());
            // metadata.setContentLength(file.getSize());
            // amazonS3Client.putObject(bucket, uploadKey, file.getInputStream(), metadata);

            return thumbnailUrl;
        }  catch (Exception e){
            return "";
        }
    }

    public String initialThumbnail(String videoUrl) throws Exception {
        UUID uuid = UUID.randomUUID();

        String thumbnailUrl = "/thumbnail/" + uuid + ".png";
//      String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + uploadKey;

        Runtime run = Runtime.getRuntime();
        String command = "ffmpeg -i " + tempPath + videoUrl + " -ss 00:00:01 -vcodec png -vframes 1 -vf scale=320:180 " + tempPath + thumbnailUrl; // 동영상 1초에서 Thumbnail 추출
        System.out.println(command);

        Process p = run.exec(command);

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line = null;

        while((line = br.readLine()) != null){
            System.out.println(line);
        }

        // File file = new File(tempPath + "/thumbnail/" + uuid + ".png");
        // InputStream thumbnailStream = new FileInputStream(file);

        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType("image/png");
        // metadata.setContentLength(file.length());
        // amazonS3Client.putObject(bucket, uploadKey, thumbnailStream, metadata);

        // String fileUrl = amazonS3Client.getUrl(bucket, uploadKey).toString();

        // String deleteCommand = "rm " + tempPath + "/" + uuid + ".png";
        // System.out.println(deleteCommand);
        // run.exec(deleteCommand);

        return thumbnailUrl;
    }

    public String createBasicChannelImage(String userName){
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = img.createGraphics();    // Graphics2D를 얻어와 그림을 그린다

        Color[] colorList = {Color.magenta, Color.blue, Color.cyan, Color.green, Color.yellow,
                Color.red, Color.orange, Color.pink};

        Integer colorIdx = (int)(Math.random() * 7);

        graphics.setColor(colorList[colorIdx]);                       // 색상을 지정한다(파란색)
        graphics.fillRect(0,0,200, 200);              // 사각형을 하나 그린다

        graphics.setFont(new Font("나눔고딕", Font.BOLD, 80));
        graphics.setColor(Color.white);

        FontMetrics metrics = graphics.getFontMetrics(new Font("나눔고딕", Font.BOLD, 80));

        String text = userName;
        int x = 0;
        int y = 0;

        if(userName.length() > 1){
            text = userName.substring(0, 2);
        }

        x = (200 - metrics.stringWidth(text)) / 2;
        y = ((200 - metrics.getHeight()) / 2) + metrics.getAscent();
        graphics.drawString(text, x, y); //설정한 위치에 따른 텍스트를 그림

        try{

            UUID uuid = UUID.randomUUID();
            String fileUrl = "/channel/" + uuid + ".png";
            File imgfile = new File(tempPath + fileUrl);        // 파일의 이름을 설정한다
            ImageIO.write(img, "png", imgfile);                     // write메소드를 이용해 파일을 만든다


            // aws s3에 저장
            // InputStream imgStream = new FileInputStream(imgfile);
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setContentType("image/png");
            // metadata.setContentLength(imgfile.length());
            // amazonS3Client.putObject(bucket, uploadKey, imgStream, metadata);

            // String fileUrl = amazonS3Client.getUrl(bucket, uploadKey).toString();

            // imgfile.delete();

            return fileUrl;
        }
        catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String channelImageUpload(MultipartFile file, String origin) {
        try {
            if(!origin.equals("")) {
                String originKey = origin.substring(origin.lastIndexOf("/") + 1);

                // amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, "channel/" + originKey));
                File imgfile = new File(tempPath + origin);
                imgfile.delete();
            }

            UUID uuid = UUID.randomUUID();
            String fileUrl = "/channel/" + uuid + ".png";
            File uploadPath = new File(tempPath + fileUrl);
            file.transferTo(uploadPath);

            // aws s3에 저장
            // String fileUrl= "https://" + bucket + ".s3." + region + ".amazonaws.com/" + uploadKey;

            // ObjectMetadata metadata= new ObjectMetadata();
            // metadata.setContentType(file.getContentType());
            // metadata.setContentLength(file.getSize());
            // amazonS3Client.putObject(bucket, uploadKey, file.getInputStream(), metadata);

            return fileUrl;
        }  catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

}
