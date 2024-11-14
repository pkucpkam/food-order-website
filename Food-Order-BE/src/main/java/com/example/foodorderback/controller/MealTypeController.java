package com.example.foodorderback.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.foodorderback.dto.MealTypeDTO;
import com.example.foodorderback.model.Meal;
import com.example.foodorderback.model.MealType;
import com.example.foodorderback.service.MealTypeService;
import com.google.gson.Gson;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "api/mealType")
public class MealTypeController {

	@Autowired
	MealTypeService mealTypeService;

	@RequestMapping(value = "/getAllMealTypes", method = RequestMethod.GET)
	public ResponseEntity<List<MealTypeDTO>> getAllMaelTypeList() {
		List<MealTypeDTO> allMealTypeDTOList = mealTypeService.getAllMealTypes();
		return new ResponseEntity<List<MealTypeDTO>>(allMealTypeDTOList, HttpStatus.OK);
	}


	@RequestMapping(value = "/createMealType", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createMeal(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
		System.out.println(request.getParameter("mealType"));

		// Chuyển đổi JSON 'mealType' từ request thành đối tượng Java
		Gson g = new Gson();
		MealType mealType = g.fromJson(request.getParameter("mealType"), MealType.class);

		String responseToClient = mealTypeService.isValidInput(mealType);
		if (responseToClient.equals("valid")) {
			try {
				// Đường dẫn thư mục lưu ảnh
				String uploadDir = "src/main/resources/static/uploads/typeMeal/";
				Path uploadPath = Paths.get(uploadDir);

				// Tạo thư mục nếu chưa tồn tại
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				// Tạo tên file từ tên của mealType
				String typeName = mealType.getTypeName().replaceAll("[^a-zA-Z0-9]", "_"); // Loại bỏ các ký tự không hợp lệ
				String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")); // Lấy phần mở rộng của file
				String fileName = typeName + extension;
				Path filePath = uploadPath.resolve(fileName);

				// Lưu file ảnh vào thư mục
				Files.write(filePath, image.getBytes());

				// Lưu tên file hoặc đường dẫn ảnh vào cơ sở dữ liệu
				mealType.setImageName(fileName);  // Lưu tên file
				mealType.setImage("/uploads/typeMeal/" + fileName);  // Đường dẫn URL để truy cập ảnh

				responseToClient = mealTypeService.save(mealType);
			} catch (IOException e) {
				e.printStackTrace();
				responseToClient = "fail";
			}
			return new ResponseEntity<>(responseToClient, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("invalid", HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/updateMealType", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> editMealType(
			@RequestParam(value = "image", required = false) MultipartFile image,
			HttpServletRequest request) {

		Gson g = new Gson();
		MealType updatedMealType = g.fromJson(request.getParameter("mealType"), MealType.class);
		MealType existingMealType = mealTypeService.findOne(updatedMealType.getId());

		if (existingMealType == null) {
			return new ResponseEntity<>("Meal type not found", HttpStatus.NOT_FOUND);
		}

		String response;
		try {
			if (image != null && !image.isEmpty()) {
				// Xóa ảnh cũ nếu tồn tại
				String oldImagePath = "src/main/resources/static" + existingMealType.getImage();
				Path oldImageFile = Paths.get(oldImagePath);
				Files.deleteIfExists(oldImageFile);

				// Đường dẫn thư mục lưu ảnh mới
				String uploadDir = "src/main/resources/static/uploads/typeMeal/";
				Path uploadPath = Paths.get(uploadDir);

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				// Đặt tên file mới dựa trên tên của mealType
				String typeName = updatedMealType.getTypeName().replaceAll("[^a-zA-Z0-9]", "_");
				String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
				String fileName = typeName + extension;
				Path filePath = uploadPath.resolve(fileName);

				// Lưu ảnh mới vào thư mục
				Files.write(filePath, image.getBytes());

				// Cập nhật tên file và đường dẫn ảnh mới
				updatedMealType.setImageName(fileName);
				updatedMealType.setImage("/uploads/typeMeal/" + fileName);
			} else {
				// Nếu không có ảnh mới, giữ lại ảnh cũ
				updatedMealType.setImage(existingMealType.getImage());
				updatedMealType.setImageName(existingMealType.getImageName());
			}

			// Cập nhật các thông tin khác vào cơ sở dữ liệu
			response = mealTypeService.save(updatedMealType);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to update meal type", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@RequestMapping(value = "/deleteMealType/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> delete(@PathVariable Long id) {
		MealType mealType = mealTypeService.findOne(id);

		if (mealType == null) {
			return new ResponseEntity<>("MealType not found", HttpStatus.NOT_FOUND);
		}

		String responseToClient;
		try {
			// Xóa file ảnh nếu tồn tại
			if (mealType.getImage() != null) {
				String imagePath = "src/main/resources/static" + mealType.getImage();
				Path imageFile = Paths.get(imagePath);
				Files.deleteIfExists(imageFile);  // Xóa ảnh khỏi thư mục
			}

			// Xóa MealType khỏi cơ sở dữ liệu
			responseToClient = mealTypeService.delete(id);
			return new ResponseEntity<>(responseToClient, HttpStatus.OK);

		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to delete image file", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



}