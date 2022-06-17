package com.cosmopredictor.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.cosmopredictor.db.ImageRepository;
import com.cosmopredictor.model.ImageModel;

@RequestMapping(path = "image")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@ComponentScan(basePackages = { "com.cosmopredictor.db", "com.cosmopredictor.model" })
public class ImageUploadController {
	
	String imageName;
	
	@Autowired
	ImageRepository imageRepository;

	@PostMapping("/upload")
	public BodyBuilder uplaodImage(@RequestParam("file") MultipartFile imageFile)
			throws IOException, SerialException, SQLException {
		imageName = imageFile.getOriginalFilename();
		
		// To save the image to file system
		ByteArrayInputStream bis = new ByteArrayInputStream(imageFile.getBytes());
		
		BufferedImage buffImage = ImageIO.read(bis);
		
		ImageIO.write(buffImage, "jpg", new File("input-images/"+imageName));
		
		System.out.println("Original Image Byte Size - " + imageFile.getBytes().length);
		
		ImageModel img = new ImageModel(imageName, imageFile.getContentType(),
				compressBytes(imageFile.getBytes()));
		imageRepository.save(img);
		
		
		// Call post method
		postImage();
		
		System.out.println("Image created!");
		return ResponseEntity.status(HttpStatus.OK);
	}

	// post the image to the python dominant color detection service
	public File postImage() throws IOException {
		
		//create a HttpPost object to post image to python flask service
		String url = "http://10.10.10.116:8050/postImage";
		HttpPost postToFlask = new HttpPost(url);
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		byte[] imageContent = FileUtils.readFileToByteArray(new File("input-images/"+imageName));
		
		//encode image to base64 String
		String encodedString = Base64.getEncoder().encodeToString(imageContent);
		
		//build image file to a multipart enitity
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		entityBuilder.addTextBody("image", encodedString);
		entityBuilder.addTextBody("imageName", imageName);
		HttpEntity entity = entityBuilder.build();
		
		//post to Python Flask service and wait for response
		postToFlask.setEntity(entity);
		HttpResponse responseFromFlask = httpClient.execute(postToFlask);
		HttpEntity httpEntity = responseFromFlask.getEntity();
		String codedPlot = EntityUtils.toString(httpEntity);
		
		//decode recieved base64 String to bytes
		byte[] decodedPlotBytes = Base64.getDecoder().decode(codedPlot);
		ByteArrayInputStream bis = new ByteArrayInputStream(decodedPlotBytes);
		BufferedImage colorPlot = ImageIO.read(bis);
		bis.close();

		// write the image to file system
		File dominantColorPlot = new File("image-results/"+imageName);
		ImageIO.write(colorPlot, "jpeg", dominantColorPlot);
		
		//returns the resultant dominant color plot to frontend
		return dominantColorPlot;
	}

	// Return the result to the frontend
	@GetMapping(path = { "/get/skinTone" })
	public ResponseEntity<String> getImage()
			throws IOException, SerialException, SQLException {
		byte[] bytes = FileUtils.readFileToByteArray(new File("image-results/"+imageName));
	        System.out.println(bytes);
	        String encodedString = Base64.getEncoder().encodeToString(bytes);
	        return ResponseEntity
	                .ok()
	                .contentType(MediaType.IMAGE_JPEG)
	                .body(encodedString);
	}

	
	// compress the image bytes before storing it in the database
	public static byte[] compressBytes(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		deflater.finish();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		try {
			outputStream.close();
		} catch (IOException e) {
		}
		System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

		return outputStream.toByteArray();
	}

	// uncompress the image bytes before returning it to the angular application
	public static byte[] decompressBytes(byte[] data) {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		try {
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
		} catch (IOException ioe) {
		} catch (DataFormatException e) {
		}
		return outputStream.toByteArray();
	}
}
