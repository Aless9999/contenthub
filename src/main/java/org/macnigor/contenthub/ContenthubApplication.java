package org.macnigor.contenthub;

import org.macnigor.contenthub.dto.TaskPayload;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@RegisterReflectionForBinding(classes ={
        TaskPayload.class
        })
@SpringBootApplication
public class ContenthubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContenthubApplication.class, args);
	}

}
