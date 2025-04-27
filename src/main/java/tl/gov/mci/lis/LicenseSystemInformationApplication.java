package tl.gov.mci.lis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LicenseSystemInformationApplication {

	public static void main(String[] args) {
		SpringApplication.run(LicenseSystemInformationApplication.class, args);
	}

}
