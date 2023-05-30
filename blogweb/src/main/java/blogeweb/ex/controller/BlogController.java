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
import org.springframework.web.bind.annotation.RequestPart;
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


	//データベースからユーザの名前と保存されているブログをmainにもってくる処理
	@GetMapping("/main")
	public String mainUser(Model model, HttpSession session) {
		UserEntity user = (UserEntity) session.getAttribute("user");
		Long userId = user.getUserId();
		List<BlogEntity>blogList = blogService.findAllBlogPost(userId);
		model.addAttribute("userName", user.getUserName());	
		model.addAttribute("blogList", blogList);
		return "main.html";
	}


//formに入力された内容をデータベースに入れる処理　新規投稿
	@PostMapping("/register/process")
	public String blogRegister(@RequestParam String blogTitle,
			@RequestParam String category,
			@RequestParam("blogImage") MultipartFile blogImage,
			@RequestParam String blogDetail,Model model) {
		
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		//画像ファイル名を取得する
		String fileName = blogImage.getOriginalFilename();
		//ファイルのアップロード処理
		try {
			//画像ファイルの保存先を指定する。
			File blogFile = new File("./images/"+fileName);
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
		if (blogService.createBlogPost(blogTitle, fileName, blogDetail, category, userId)) {
		    return "blogok.html";
		} else {
		    return "main.html";
		}
	}
	
	
	
	
	
	//mianのブログを選択したらそのブログの編集画面に遷移する処理
	@GetMapping("/edit/{blogId}")
	public String getBlogEditPage(@PathVariable Long blogId,Model model) {
		UserEntity userList = (UserEntity) session.getAttribute("user");
		String userName = userList.getUserName();
		model.addAttribute("userName", userName);

		BlogEntity blogList = blogService.getBlogPost(blogId);
		if(blogList == null) {
			return "redirecr:/user/blog/main";
		}else {
			model.addAttribute("blogList", blogList);
			return "edit.html";
		}

	}
	//投稿したブログの編集処理
	@PostMapping("/update")
	public String updateBlog(@RequestParam String blogTitle,
	                         @RequestParam String category,
	                         @RequestParam String blogDetail,
	                         @RequestParam("blogImage") MultipartFile blogImage,
	                         @RequestParam Long blogId,
	                         Model model) {

	    UserEntity userList = (UserEntity) session.getAttribute("user");
	    Long userId = userList.getUserId();

	    String fileName = null;
	    try {
	        if (blogImage != null && !blogImage.isEmpty()) {
	            // 画像ファイル名を取得する
	            fileName = blogImage.getOriginalFilename();
	            // 画像ファイルの保存先を指定する
				File blogFile = new File("./images/"+fileName);
	            // 画像ファイルからバイナリデータを取得する
	            byte[] bytes = blogImage.getBytes();
	            // 画像を保存（書き出し）するためのバッファを用意する
	            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(blogFile));
	            // 画像ファイルの書き出しする
	            out.write(bytes);
	            // バッファを閉じることにより、書き出しを正常終了させる
	            out.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    if (blogService.editBlogPostAndImage(blogTitle, blogDetail, category, userId, blogId, fileName)) {
	        return "blogok.html";
	    } else {
	        BlogEntity blogList = blogService.getBlogPost(blogId);
	        model.addAttribute("blogList", blogList);
	        return "edit.html";
	    }
	}
	
	
	//削除したいブログリストを表示
	@GetMapping("/delete/list")
	public String getBlogDeleteListPage(Model model) {

		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();

		String userName = userList.getUserName();
		List<BlogEntity>blogList = blogService.findAllBlogPost(userId);

		model.addAttribute("userName", userName);
		model.addAttribute("blogList", blogList);
		return "delete.html";
	}

	//削除したいブログの詳細表示
	@GetMapping("/delete/detail/{blogId}")
	public String getBlogDeleteDetailPage(@PathVariable Long blogId,Model model) {

		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();

		String userName = userList.getUserName();
		model.addAttribute("userName", userName);

		BlogEntity blogList = blogService.getBlogPost(blogId);

		if(blogList == null) {
			return "redirecr:/user/blog/list";
		}else {
			model.addAttribute("blogList", blogList);
			return "deleteblog.html";
		}

	}

	//削除処理が成功したか失敗したかの処理
	@PostMapping("/delete")
	public String blogDelete(@RequestParam Long blogId,Model model) {
		if(blogService.deleteBlog(blogId)) {
			return "deleteblogok.html";
		}else {
			return "deleteblog.html";
		}

	}
	//ログアウト
	@GetMapping("/logout")
	public String Logout() {
		
		session.invalidate();
		return "redirect:/user/login";
	}

}
