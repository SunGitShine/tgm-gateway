/**
 * 
 */
package com.juma.tgm.gateway.web.controller;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginUser;
import com.juma.common.storage.service.DistributedFileStorageService;
import com.juma.tgm.gateway.common.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author vencent.lu
 *
 */
@Controller
public class UploadController extends AbstractController {
	
	@Autowired
	private DistributedFileStorageService distributedFileStorageService;
	
	@RequestMapping(value = "upload")
	@ResponseBody
	public String upload(@RequestParam MultipartFile uploadPic,
			LoginUser loginUser) throws IOException {
		return this.distributedFileStorageService.putInputBytes("upload/images",
				uploadPic.getOriginalFilename(), uploadPic.getBytes(),
				uploadPic.getContentType(), true);
	}

	@RequestMapping(value = "customerManager/upload")
	@ResponseBody
	public String customerManagerUpload(@RequestParam MultipartFile uploadPic,
										LoginEmployee loginEmployee) throws IOException {
		return this.distributedFileStorageService.putInputBytes("upload/images",
				uploadPic.getOriginalFilename(), uploadPic.getBytes(),
				uploadPic.getContentType(), true);
	}

}
