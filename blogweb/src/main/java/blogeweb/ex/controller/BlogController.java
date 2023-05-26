package blogeweb.ex.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import blogeweb.ex.model.dao.BlogDao;
import blogeweb.ex.model.entity.BlogEntity;
import blogeweb.ex.model.entity.UserEntity;
import blogeweb.ex.service.BlogService;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/user/blog")

@Controller
public class BlogController {
	
	@Autowired
	private HttpSession session;

	@Autowired 
	private BlogService blogService;
	
    @Autowired
    private BlogDao blogRepository;
	
	
	//main に行く処理とデータベースからnameをもってくる処理
    @GetMapping("/main")
    public String mainUser(Model model, HttpSession session) {
    	UserEntity user = (UserEntity) session.getAttribute("user");
        model.addAttribute("name", user.getName());        
        return "main.html";
    }
    


    

    @PostMapping("/register/process")
    public String registerBlog(@RequestParam("blogTitle") String blogTitle,
                               @RequestParam("category") String category,
                               @RequestParam("blogDetail") String blogDetail

    		) {
    	BlogEntity blog = new BlogEntity();
        blog.setBlogTitle(blogTitle);
        blog.setCategory(category);
        blog.setBlogDetail(blogDetail);


        
        blogRepository.save(blog);
        return "redirect:/user/blog/main";
    }
    
	@PostMapping("/image/update")

	public String blogRegister(
			@RequestParam("blogImage") MultipartFile blogImage,
			@RequestParam Long blogId,Model model) {

		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		//画像ファイル名を取得する
		String fileName = blogImage.getOriginalFilename();
		//ファイルのアップロード処理
		try {
			//画像ファイルの保存先を指定する。
			File blogFile = new File("./src/main/resources/static/blog-img/"+fileName);
			//画像ファイルからバイナリデータを取得する
			byte[] bytes = blogImage.getBytes();
			//画像を保存（書き出し）するためのバッファを用意する
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(blogFile));
			//画像ファイルの書き出しする。
			out.write(bytes);
			//バッファを閉じることにより、書き出しを正常終了させる。
			out.close();
		}catch(Exception e) {
			e.printStackTrace();
		}

		if(blogService.editBlogImage(blogId, fileName, userId)) {
			return "blog-edit-fix.html";
		}else {
			BlogEntity blogList = blogService.getBlogPost(blogId);
			model.addAttribute("blogList",blogList);
			model.addAttribute("editImageMessage", "更新失敗です");
			return "blog-img-edit.html";
		}

	}

 	
}
