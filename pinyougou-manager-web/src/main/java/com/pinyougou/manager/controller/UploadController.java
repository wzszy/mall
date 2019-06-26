package com.pinyougou.manager.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import utils.FastDFSClient;

@RestController
public class UploadController {
	@Value("${FILE_SERVER_URL}")
	private String file_server_url;
	@RequestMapping("upload")
	public Result upload(MultipartFile file) {
		//获取文件全名称
		String originalFilename = file.getOriginalFilename();
		//获取文件扩展名
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		
		try {
			utils.FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			String string = client.uploadFile(file.getBytes(), extName);
			String url = file_server_url+string;
			return new Result(true, url);
		} catch (Exception e) {
	
			return new Result(false, "上传失败");
		}
	}
}










