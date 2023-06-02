package blogeweb.ex.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blogeweb.ex.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
@RequestMapping("/user")

@Controller
public class UserRegisterController {
	
	@Autowired
	private UserService userService;
	
	
	//ページ遷移用
	@GetMapping("/register")
	public String getUserRegisterPage() {
		return "register.html";
	}
	
	
	//ユーザーを新しく登録する処理
	@PostMapping("/register/process")
	public String register(@RequestParam String name,@RequestParam String password) {
		
		if(name.isEmpty() || password.isEmpty()) {
			return "register.html";
		}

		userService.createAccount(name,password);
		return "redirect:/user/login";
	}
	
	

}
