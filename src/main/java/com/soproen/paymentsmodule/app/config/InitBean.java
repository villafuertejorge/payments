package com.soproen.paymentsmodule.app.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InitBean implements InitializingBean {

	@Autowired
	ResourceLoader resourceLoader;
	@Value("${app.file-name-csv-schema-validation}")
	private String fileNameCsvSchemaValidation;
	@Value("${app.app-resources-folder}")
	private String appResourceFolder;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			Resource resource = resourceLoader.getResource("classpath:csv-schema/"+fileNameCsvSchemaValidation);
			File newFile = new File(appResourceFolder.concat(fileNameCsvSchemaValidation));
			Files.copy(resource.getInputStream(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			log.error("afterPropertiesSet = {} ", e.getMessage());
		}
	}
}
