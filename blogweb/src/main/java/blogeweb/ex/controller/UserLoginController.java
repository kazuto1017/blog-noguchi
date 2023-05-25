package blogeweb.ex.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import blogeweb.ex.model.entity.UserEntity;
import blogeweb.ex.service.UserService;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/user")

@Controller
public class UserLoginController {
	@Autowired
	private UserService userService;
	@Autowired
	private HttpSession session;
	
	
	@GetMapping("/login")
	public String getUserLoginPage() {
		return "login.html";
	}
	
	@PostMapping("/login/process")
	public String login(@RequestParam String name,@RequestParam String password) {
		UserEntity userList = userService.loginAccount(name,password);
		if(userList == null) {
			return "redirect:/user/login";
		}else {
			session.setAttribute("user", userList);
			return "redirect:/main";
		}
		
	}

}
