package com.misboi.apas;

import com.misboi.apas.config.EmailConfig;
import com.misboi.apas.controller.EmailFileController;
import com.misboi.apas.entities.Role;
import com.misboi.apas.entities.User;
import com.misboi.apas.entities.UserRole;
import com.misboi.apas.helper.DateUtils;
import com.misboi.apas.helper.UserFoundException;
import com.misboi.apas.services.UserService;
import com.misboi.apas.services.WorkflowService;
import com.misboi.apas.services.impl.DruAuthServiceImpl;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.misboi.apas.entities.AuthDetails.*;

@EnableProcessApplication
@SpringBootApplication
@EnableScheduling
public class ApasServerApplication extends SpringBootServletInitializer implements CommandLineRunner {
	
	private final static Logger LOGGER = Logger.getLogger("APAS Application Logger");
	private static JSONObject procInstInfo;
	
	private static int schdlCount = 0;
	
	@Autowired
	private UserService userService;

	@Autowired
	private DruAuthServiceImpl druAuthService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private EmailConfig emailConfig;

	@Autowired
	private EmailFileController emailFileController;

	@Override 
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) { 
		  return application.sources(ApasServerApplication.class); 
	}
	
	@Autowired
	public RuntimeService runtimeService;
	
	@Autowired
	public TaskService taskService;
	
	@Autowired
    public WorkflowService workflowService;

	public static void main(String[] args) {
		SpringApplication.run(ApasServerApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	@Scheduled(fixedRate = 10000)
	public void triggerMail(){
		schdlCount++;
		System.out.println("Schedule " + schdlCount + " Triggered . . . . . .at " + DateUtils.getCurrentInstant());
		emailFileController.setSaveDirectory(emailConfig.getSAVEDIRECTORY());
		emailFileController.downloadEmails(emailConfig.getPROTOCOL(),emailConfig.getHOST(),emailConfig.getPORT(),emailConfig.getUSERNAME(),emailConfig.getPASSWORD(),emailConfig.getKEYWORD());
	}

	public void run(String... args) throws Exception {
		try {


			System.out.println("Starting code");

			// creating new user hard coded

		User user =  new User();
		user.setFirstName("Shivanshu");
		user.setLastName("Agrahari");
		user.setUsername("admin");
		user.setPassword(this.bCryptPasswordEncoder.encode("admin"));
		user.setEmail("example@gmail.com");
		user.setProfile("default.png");

			JSONObject authResponse = druAuthService.authenticateDruSight(authurl,userid,password);
			System.out.println(authResponse);
			String sessionToken = (String) authResponse.get("SessionToken");
			System.out.println(sessionToken);
			SESSIONTOKEN = sessionToken;
			System.out.println("This is the session token: "+SESSIONTOKEN);


		// setting roles

		Role role1 = new Role();

		role1.setRoleId(44L);
		role1.setRoleName("ADMIN");

		Set<UserRole> userRoleSet = new HashSet<>();

		UserRole userRole = new UserRole();
		userRole.setRole(role1);
		userRole.setUser(user);
		userRoleSet.add(userRole);

		User user1 =  this. userService.createUser(user,userRoleSet);
		System.out.println(user1.getUsername());
		}catch(UserFoundException e)
		{
			e.printStackTrace();

		}


	}
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "File-Name"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	} 
	
	@RestController
	@RequestMapping("/process")
	@CrossOrigin(origins = "*")
	public class WorkflowServiceController {

	    // Launching process
	    @PostMapping("/launch")
	    public JSONObject launchProcess(@RequestBody JSONObject reqData) throws Exception {

	        reqData = workflowService.launchWorkflow(runtimeService, reqData);
	        System.out.println("Inside Controller: " + reqData.toJSONString());
	        return reqData;
	    }
	    
	 // Complete user task
	    @PostMapping("/completetask")
	    public JSONObject completeTask(@RequestBody JSONObject reqData) throws Exception {

	        reqData = workflowService.completeTask(taskService, reqData);
	        System.out.println("Inside Controller: " + reqData.toJSONString());
	        return reqData;
	    }

	}
}
