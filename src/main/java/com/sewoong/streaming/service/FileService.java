package com.sewoong.streaming.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class FileService {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${file.path}")
    private String tempPath;

    @Value("${file.ffprobe.path}")
    private String ffmpegPath;

    private SimpMessagingTemplate messagingTemplate;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
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

    public SseEmitter sseConnect(String sseId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L * 10); // 10분 타임아웃
        emitters.put(sseId, emitter);

        emitter.onCompletion(() -> emitters.remove(sseId));
        emitter.onTimeout(() -> emitters.remove(sseId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect") // 이벤트 이름
                    .data("connected!")); // 데이터 내용
        } catch (IOException e) {
            emitters.remove(sseId);
        }
        return emitter;
    }

    private void sendToClient(String sseId, Object data) {
        SseEmitter emitter = emitters.get(sseId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("progress").data(data));
            } catch (IOException e) {
                emitters.remove(sseId);
            }
        }
    }

    public void saveAsAv1(String inputPath, String sseId) {
        try {

            UUID uuid = UUID.randomUUID();
            String extension = inputPath.substring(inputPath.lastIndexOf(".") + 1);
            String outputPath = "/video/" + uuid + "." + extension;

            // 설치된 FFmpeg 경로 지정
            FFmpeg ffmpeg = new FFmpeg("C:/ffmpeg/bin/ffmpeg"); 
            FFprobe ffprobe = new FFprobe("C:/ffmpeg/bin/ffprobe");

            double durationNs = ffprobe.probe(tempPath + inputPath).format.duration * 1_000_000_000.0;

            FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(tempPath + inputPath)
                .overrideOutputFiles(true)
                .addOutput(tempPath + outputPath)
                    .setVideoCodec("libsvtav1") // AV1 코덱 지정
                    .setVideoResolution(1920, 1080)
                    .setVideoFrameRate(30, 1)
                    .addExtraArgs("-preset", "10") // SVT-AV1 전용 프리셋 (0~13, 높을수록 빠름)
                    .addExtraArgs("-crf", "35")   // 화질 설정 (낮을수록 고화질)
                .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            executor.createJob(builder, progress -> {
                double percentage = Math.min(100.0, (progress.out_time_ns / durationNs) * 100);

                // SSE로 데이터 전송
                sendToClient(sseId, Map.of("percent", Math.round(percentage), "status", "ENCODING"));
                
            }).run();

            sendToClient(sseId, Map.of("percent", 100, "status", "COMPLETE"));

        } catch (Exception e) {
            throw new RuntimeException("비디오 인코딩 실패", e);
        }
    }
}
