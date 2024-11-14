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

import com.example.foodorderback.dto.MealDTO;
import com.example.foodorderback.model.Meal;
import com.example.foodorderback.model.Role;
import com.example.foodorderback.model.User;
import com.example.foodorderback.service.MealService;
import com.google.gson.Gson;

import antlr.StringUtils;
@CrossOrigin("*")
@RestController
@RequestMapping(value = "api/meal")
public class MealController {
	
	@Autowired
	MealService mealService;
	
	@RequestMapping(value = "/getAllMeals", method = RequestMethod.GET)
	public ResponseEntity<List<MealDTO>> getAllMeals() {
		List<MealDTO> allMealDTOList = mealService.findAll();
		return new ResponseEntity<List<MealDTO>>(allMealDTOList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getMealsByMealTypeId/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<MealDTO>> getMealsByMealTypeId(@PathVariable Long id){
		List<MealDTO> mealsByMealTypeId = mealService.getMealsByMealTypeId(id);
		return new ResponseEntity<List<MealDTO>>(mealsByMealTypeId, HttpStatus.OK);
	}

	@RequestMapping(value = "/createMeal", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createMeal(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
		System.out.println(request.getParameter("meal"));
		Gson g = new Gson();
		Meal meal = g.fromJson(request.getParameter("meal"), Meal.class);
		System.out.println("MEAL " + meal);

		String responseToClient = mealService.isValidInput(meal);
		if (!"valid".equals(responseToClient)) {
			return new ResponseEntity<>(responseToClient, HttpStatus.OK);
		}

		try {
			// Đường dẫn thư mục để lưu ảnh
			String uploadDir = "src/main/resources/static/uploads/meal";
			Path uploadPath = Paths.get(uploadDir);

			// Tạo thư mục nếu chưa tồn tại
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Tạo tên file từ tên của Meal, loại bỏ các ký tự không hợp lệ
			String sanitizedMealName = meal.getName().replaceAll("[^a-zA-Z0-9]", "_");
			String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
			String fileName = sanitizedMealName + extension;

			Path filePath = uploadPath.resolve(fileName);
			Files.copy(image.getInputStream(), filePath);

			meal.setImageName(fileName);  // Lưu tên file
			meal.setImage("/uploads/meal/" + fileName);  // Đường dẫn URL để truy cập ảnh

			// Lưu meal vào cơ sở dữ liệu
			responseToClient = mealService.save(meal);
			return new ResponseEntity<>(responseToClient, HttpStatus.OK);

		} catch (IOException e) {
			e.printStackTrace();
			responseToClient = "fail";
			return new ResponseEntity<>(responseToClient, HttpStatus.OK);
		}
	}


	@RequestMapping(value = "/updateMeal", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> editMeal(
			@RequestParam(value = "image", required = false) MultipartFile image,
			HttpServletRequest request) {

		Gson g = new Gson();
		Meal updatedMeal = g.fromJson(request.getParameter("meal"), Meal.class);
		Meal existingMeal = mealService.findOne(updatedMeal.getId());

		if (existingMeal == null) {
			return new ResponseEntity<>("Meal not found", HttpStatus.NOT_FOUND);
		}

		String response;
		try {
			// Kiểm tra nếu có ảnh mới
			if (image != null && !image.isEmpty()) {
				// Xóa ảnh cũ nếu tồn tại
				String oldImagePath = "src/main/resources/static" + existingMeal.getImage();
				Path oldImageFile = Paths.get(oldImagePath);
				Files.deleteIfExists(oldImageFile);

				// Lưu ảnh mới
				String uploadDir = "src/main/resources/static/uploads/meal/";
				Path uploadPath = Paths.get(uploadDir);

				// Tạo thư mục nếu chưa tồn tại
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				// Tạo tên file từ tên của Meal và loại bỏ ký tự không hợp lệ
				String sanitizedMealName = updatedMeal.getName().replaceAll("[^a-zA-Z0-9]", "_");
				String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
				String fileName = sanitizedMealName + extension;

				Path filePath = uploadPath.resolve(fileName);

				// Lưu file ảnh mới vào thư mục
				Files.write(filePath, image.getBytes());

				// Cập nhật tên file và đường dẫn ảnh
				updatedMeal.setImage("/uploads/meal/" + fileName);
			} else {
				// Giữ nguyên ảnh hiện tại
				updatedMeal.setImage(existingMeal.getImage());
			}

			// Cập nhật các thông tin khác
			existingMeal.setName(updatedMeal.getName());
			existingMeal.setPrice(updatedMeal.getPrice());
			existingMeal.setDescription(updatedMeal.getDescription());
			existingMeal.setMealType(updatedMeal.getMealType());
			existingMeal.setImage(updatedMeal.getImage()); // Cập nhật đường dẫn ảnh mới nếu có

			// Lưu cập nhật vào cơ sở dữ liệu
			response = mealService.save(existingMeal);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to update meal", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	@RequestMapping(value = "/deleteMeal/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> delete(@PathVariable Long id) {
		String responseToClient = mealService.delete(id);;
		return new ResponseEntity<String>(responseToClient, HttpStatus.OK);
	}


	@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(value = "/getMealById/{id}")
	public ResponseEntity<MealDTO> getMealById(@PathVariable Long id) {
		MealDTO mealDTO = mealService.getMealById(id);
		if (mealDTO != null) {
			return new ResponseEntity<>(mealDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
