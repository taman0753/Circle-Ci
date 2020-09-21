package com.springboot.Demo1;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloWorld {
	
	@GetMapping("/welcome")
	public String welcome() {
	 return "welcome to demo page";
		
	}

}
