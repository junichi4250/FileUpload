package com.example.controller;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.service.StorageService;

@Controller
public class FileUploaderController {

	private final StorageService storageService;

	@Autowired
	public FileUploaderController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/")
	public String listUploadFiles(Model model) throws IOException {
		model.addAttribute("files",
				storageService.loadAll().map(path ->
				MvcUriComponentsBuilder.fromMethodName(FileUploaderController.class,
				"serveFile", path.getFileName().toString()).build().toUri().toString()).collect(Collectors.toList()
						));
		return "uploadForm";
	}

	@GetMapping


	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		// リダイレクト先にパラメーターを渡す
		// redirectAttributes
		storageService.store(file);
		String message = "You successfully uploaded" + file.getOriginalFilename() + "!";
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/";
	}

	// このController内で発生した例外をキャッチ
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
