package blogeweb.ex.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;


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

	//階層へ遷移用
    @GetMapping("/newblog")
    public String newBlog() {
        return "newblog.html";
    }


    
	//データベースからユーザの名前と保存されているブログをmainにもってくる処理
	@GetMapping("/main")
	public String mainUser(Model model, HttpSession session) {
		//セッションから UserEntityを取得
		UserEntity user = (UserEntity) session.getAttribute("user");
		//ユーザーIDを取得
		Long userId = user.getUserId();
		//ユーザーIDに関連するすべてのブログ投稿を取得
		List<BlogEntity>blogList = blogService.findAllBlogPost(userId);
		//ユーザーの名前を"userName"属性として追加
		model.addAttribute("userName", user.getUserName());	
		//ブログ投稿のリストを"blogList"属性として追加
		model.addAttribute("blogList", blogList);
		return "main.html";
	}


//formに入力された内容をデータベースに入れる処理　新規投稿
	@PostMapping("/register/process")
	public String blogRegister(@RequestParam String blogTitle,
	        @RequestParam String category,
	        @RequestParam("blogImage") MultipartFile blogImage,
	        @RequestParam String blogDetail, Model model) {
		
		//セッションから UserEntityを取得
	    UserEntity userList = (UserEntity) session.getAttribute("user");
		//ユーザーIDを取得
	    Long userId = userList.getUserId();
	    
	    String fileName;
	    
	    //もしblogImageが空ではないとき
	    if (!blogImage.isEmpty()) {
	        // 画像ファイルがアップロードされた場合の処理
	        fileName = blogImage.getOriginalFilename();
	        try {
	            // 画像ファイルの保存先を指定する。
	            File blogFile = new File("./images/" + fileName);
	            // 画像ファイルからバイナリデータを取得する
	            byte[] bytes = blogImage.getBytes();
	            // 画像を保存（書き出し）するためのバッファを用意する
	            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(blogFile));
	            // 画像ファイルの書き出しする。
	            out.write(bytes);
	            // バッファを閉じることにより、書き出しを正常終了させる。
	            out.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    } else {
	        // 画像ファイルがアップロードされなかった場合の処理
	        fileName = "noimg.png"; // デフォルトの画像ファイル名を設定
	    }
	    
	    // blogServiceを使用してブログ投稿を作成し、成功した場合は"newblog-ok.html"
	    // 失敗した場合は"main.html"
	    if (blogService.createBlogPost(blogTitle, fileName, blogDetail, category, userId)) {
	        return "newblog-ok.html";
	    } else {
	        return "main.html";
	    }
	}

	//mainのブログを選択したらそのブログの編集画面に遷移する処理
	@GetMapping("/edit/{blogId}")
	public String getBlogEditPage(@PathVariable Long blogId,Model model) {
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
	        return "edit-ok.html";
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

		List<BlogEntity>blogList = blogService.findAllBlogPost(userId);

		model.addAttribute("blogList", blogList);
		return "delete.html";
	}

	//削除したいブログの詳細表示
	@GetMapping("/delete/detail/{blogId}")
	public String getBlogDeleteDetailPage(@PathVariable Long blogId,Model model) {

		BlogEntity blogList = blogService.getBlogPost(blogId);

		if(blogList == null) {
			return "redirecr:/user/blog/list";
		}else {
			model.addAttribute("blogList", blogList);
			return "delete-blog.html";
		}

	}

	//削除処理が成功したか失敗したかの処理
	@PostMapping("/delete")
	public String blogDelete(@RequestParam Long blogId,Model model) {
		if(blogService.deleteBlog(blogId)) {
			return "delete-blog-ok.html";
		}else {
			return "delete-blog.html";
		}

	}
	//ログアウト
	@GetMapping("/logout")
	public String Logout() {
		
		session.invalidate();
		return "redirect:/user/login";
	}

}
