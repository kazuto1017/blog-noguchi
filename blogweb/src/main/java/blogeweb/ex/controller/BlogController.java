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

import blogeweb.ex.model.entity.BlogEntity;
import blogeweb.ex.model.entity.UserEntity;
import blogeweb.ex.service.BlogService;
import jakarta.servlet.http.HttpSession;
/**
 * @RequestMappingアノテーションは、HTTPリクエストに対するマッピングを設定するために使用されます。
 * "/user"と指定されているので、このコントローラーのすべてのエンドポイントは"/user/blog"で始まります。
 *エンドポイント（Endpoint）は、Webサービスにおいて、外部からアクセス可能なURLやURIのことを指します。
 *つまり、クライアント（Webブラウザやスマートフォンアプリなど）がWebサービスにリクエストを送信するために使用するURLのことです。
 */
@RequestMapping("/user/blog")
/**
 * @Controllerアノテーションは、Spring Frameworkがコンポーネントスキャン機能
 * によってこのクラスをDIコンテナに登録するために使用されます。
 * これにより、UserRegisterControllerクラスは、他のコンポーネントに注入されることができ,
 * システム内で一元的に管理されることができます。
 * コンポーネントスキャン機能は、Spring Frameworkにおいて、アノテーションで構成されたクラスを自動的に検出し、
 * DIコンテナに登録するための機能です
 * DI（Dependency Injection）コンテナは、Spring Frameworkが提供する機能の一つで、アプリケーションに必要なオブジェクトを管理する仕組みです。
 *通常、アプリケーションに必要なオブジェクトは、new演算子を使用してインスタンス化していますが、
 *DIコンテナを使用すると、オブジェクトの生成や注入を自動的に行うことができます。
 **/
@Controller
public class BlogController {
	/**
	 * @Autowiredアノテーションは、DIコンテナが自動的にBlogServiceインスタンスを
	 * 注入するために使用されます。これにより、BlogControllerクラスは、
	 * BlogServiceクラスのメソッドを呼び出すことができ、指定した処理を実行することができます。
	 **/
	@Autowired 
	private BlogService blogService;
	/**
	 * HttpSessionとは、Webアプリケーションにおいて、ユーザーごとに状態を保持するためのオブジェクトです。
	 * HTTPプロトコル()は、通信ごとに新しい接続を確立するため、ユーザーの状態を保持するためには、ユーザーが識別できる情報を保持しておく必要があります。
	 * HttpSessionは、Webアプリケーション内でユーザーごとに生成され、サーバー側で管理されます。ユーザーごとに一意なIDが割り当てられ、
	 * このIDを使用して、サーバー上でユーザーのセッション情報を特定します。
	 * HttpSessionは、主に以下のような目的で使用されます。
	 * ログイン状態の維持
	 * ショッピングカートやフォームの入力情報の保存
	 * ユーザーごとに異なる情報の表示
	 * このように、HttpSessionを使用することで、ユーザーごとに状態を保持し、よりパーソナライズされたWebアプリケーションを実現することができます。
	 * しかし、HttpSessionにはセッションの有効期限やサイズ制限などの制限があるため、適切な管理が必要です。
	 * また、セッション情報には機密性があるため、適切なセキュリティ対策が必要となります。**/
	/**
	 * プロトコル（protocol）は、通信やデータのやり取りを行う際の手順や規約のことを指します
	 * 。つまり、ある種の共通のやり取りの方式やルールを決め、それに従って通信やデータのやり取りを行うことで、
	 * 情報の正確な伝達や効率的な処理を実現するための仕組みです。**/
	@Autowired 
	private HttpSession session;
	/**@GetMappingアノテーションがgetBlogListPageメソッドに付与されています。
	 * このアノテーションは、HTTP GETリクエストを受け取るメソッドであることを示します。**/
	@GetMapping("/list")
	/**getBlogListPageメソッドは、Modelクラスのインスタンスを引数に取ります。Modelは、
	 * コントローラーからビューに渡すためのデータを格納するために使用されます。**/
	public String getBlogListPage(Model model) {
		/**
		 * セッションから現在のユーザー情報を取得するため、sessionオブジェクトを使用しています。
		 * UserEntityのインスタンスが取得できた場合、そのuserIdを取得しています。**/
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		/**
		 * userListから現在ログインしている人のユーザー名を取得**/
		String userName = userList.getUserName();
		/**
		 * blogServiceのfindAllBlogPostメソッドを呼び出して、現在のユーザーに関連するブログ投稿を取得しています。
		 * 戻り値はBlogEntityのリストであり、このリストをmodelに追加しています。**/
		List<BlogEntity>blogList = blogService.findAllBlogPost(userId);
		/**
		 * ModelクラスにuserNameとblogListを追加して、blog-list.htmlというビューを返しています。
		 * このビューは、userNameとblogListを表示するためのHTMLテンプレート**/
		model.addAttribute("userName", userName);
		model.addAttribute("blogList", blogList);
		return "blog-list.html";
	}

	@GetMapping("/register")
	public String getBlogRegisterPage(Model model) {
		/**
		 * セッションから現在のユーザー情報を取得するため、sessionオブジェクトを使用しています。
		 * UserEntityのインスタンスが取得できた場合、そのuserIdを取得しています。**/
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		/**
		 * userListから現在ログインしている人のユーザー名を取得**/
		String userName = userList.getUserName();
		model.addAttribute("userName", userName);
		model.addAttribute("registerMessage", "新規記事追加");
		return "blog-register.html";
	}

	/**このメソッドはブログの投稿を処理するためのメソッドで以下のような処理を行っています。
	 *セッションから現在のユーザー情報を取得し、そのuserIdを取得する。
	 *画像ファイル名を取得する。
	 *画像ファイルをアップロードする。
	 *createBlogPost()メソッドを呼び出して、ブログをデータベースに登録する。
	 *登録に成功した場合は、blog-register-fix.htmlにリダイレクトする。
	 *登録に失敗した場合は、エラーメッセージをモデルに追加し、blog-register.htmlに戻る。 **/
	/**
	 * @param blogTitle ブログタイトル
	 * @param registerDate　登録日
	 * @param category　カテゴリー
	 * @param blogImage　画像イメージ
	 * @param blogDetail　ブログ詳細
	 * @param model
	 * @return
	 */
	@PostMapping("/register/process")
	/**
	 * @RequestParamアノテーション：
	 * このアノテーションは、HTTPリクエストのパラメーターを引数にバインドするために使用されます。
	 * ブログの投稿時に、ブログのタイトル、投稿日、カテゴリ、画像ファイル、詳細をパラメータとして受け取ります。**/
	/**
	 * MultipartFile：
	 * MultipartFileは、Spring Frameworkが提供するインターフェースで、
	 * アップロードされたファイルの内容を扱うためのメソッドを提供しています。**/
	public String blogRegister(@RequestParam String blogTitle,
			@RequestParam LocalDate registerDate, 
			@RequestParam String category,
			@RequestParam("blogImage") MultipartFile blogImage,
			@RequestParam String blogDetail,Model model) {
		/**
		 * セッションから現在のユーザー情報を取得するため、sessionオブジェクトを使用しています。
		 * UserEntityのインスタンスが取得できた場合、そのuserIdを取得しています。**/
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		/**
		 * まず、MultipartFileを使用して、リクエストから送信されたファイルを取得しています。
		 * blogImage.getOriginalFilename()を使用して、アップロードされた画像ファイルの名前を取得し、
		 * Fileを使用して、アップロードするファイルの保存先を指定しています。
		 * ファイルが保存される場所は、"./src/main/resources/static/blog-img/"です。**/
		/**
		 * blogImage.getBytes()を使用して、ファイルのバイナリデータを取得し、BufferedOutputStreamを使用して、
		 * バッファにファイルのバイナリデータを書き込みます。そして、out.close()を呼び出すことで、
		 * バッファを閉じて、ファイルの書き込みを終了させます。
		 * このように、ファイルをアップロードするために、Java標準ライブラリのjava.ioパッケージを使用して、
		 * ファイル操作を行います。**/
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
		/**
		 * 最後に、blogService.createBlogPost()メソッドが呼び出され、入力されたデータをデータベースに保存します。
		 * blogTitle、registerDate、fileName、blogDetail、category、userIdの各引数を使用して、BlogEntityオブジェクトを生成し、
		 * blogDao.save()メソッドを使用して、データベースに保存します。
		 * createBlogPost()メソッドはboolean型の戻り値を返します。trueが返された場合は、blog-register-fix.htmlページが表示され、
		 * falseが返された場合は、既に登録済みであることを示すメッセージが含まれたblog-register.htmlページが表示されます。**/
		if(blogService.createBlogPost(blogTitle, registerDate, fileName, blogDetail, category, userId)) {
			return "blog-register-fix.html";
		}else {
			model.addAttribute("registerMessage", "既に登録済みです");
			return "blog-register.html";
		}

	}
	/**
	 * @GetMapping("/edit/{blogId}")アノテーションによって、
	 * GETメソッドで"/edit/{blogId}"にアクセスされた場合に、getBlogEditPage()メソッドが呼び出されます。
	 * このメソッドは、@PathVariableアノテーションを使用して、URLからパス変数のblogIdを取得しています。**/
	@GetMapping("/edit/{blogId}")
	public String getBlogEditPage(@PathVariable Long blogId,Model model) {
		/**
		 * セッションから現在のユーザー情報を取得するため、sessionオブジェクトを使用しています。
		 * UserEntityのインスタンスが取得できた場合、そのuserIdを取得しています。**/
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		/**
		 * userListから現在ログインしている人のユーザー名を取得**/
		String userName = userList.getUserName();
		model.addAttribute("userName", userName);
		/**blogService.getBlogPost(blogId)を使用して、
		 * 指定されたblogIdに対応するブログを取得し、blogListに代入します。**/
		BlogEntity blogList = blogService.getBlogPost(blogId);
		/**blogListがnullであれば、"/user/blog/list"にリダイレクトします。**/
		if(blogList == null) {
			return "redirecr:/user/blog/list";
		}else {
			/**blogListと"記事編集"というメッセージをModelオブジェクトに追加し、"blog-edit.html"に遷移します。**/
			model.addAttribute("blogList", blogList);
			model.addAttribute("editMessage", "記事編集");
			return "blog-edit.html";
		}

	}
	/**
	 * @param blogTitle ブログタイトル
	 * @param registerDate　登録日
	 * @param category　カテゴリー
	 * @param blogId　ブログId
	 * @param blogDetail　ブログ詳細
	 * @param model
	 * @return
	 */
	@PostMapping("/update")
	/**
	 * @RequestParamアノテーション：
	 * このアノテーションは、HTTPリクエストのパラメーターを引数にバインドするために使用されます。
	 * ブログの投稿時に、ブログのタイトル、投稿日、カテゴリ、、詳細をパラメータとして受け取ります。**/
	public String blogUpdate(@RequestParam String blogTitle,
			@RequestParam LocalDate registerDate, 
			@RequestParam String category,
			@RequestParam String blogDetail,
			@RequestParam Long blogId,Model model) {
		/**
		 * セッションから現在のユーザー情報を取得するため、sessionオブジェクトを使用しています。
		 * UserEntityのインスタンスが取得できた場合、そのuserIdを取得しています。**/
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();

		/**blogService.editBlogPost()を使用して、指定されたblogIdに対応するブログを更新します。
		 * 更新が成功した場合は、"blog-edit-fix.html"に遷移し、
		 * 更新に失敗した場合は、"blog-edit.html"に"更新に失敗しました"というメッセージを追加して遷移します。**/
		if(blogService.editBlogPost(blogTitle, registerDate, blogDetail, category, userId, blogId)) {
			return "blog-edit-fix.html";
		}else {
			model.addAttribute("registerMessage", "更新に失敗しました");
			return "blog-edit.html";
		}

	}
	/**
	 * @GetMapping("/image/edit/{blogId}")アノテーションによって、
	 * GETメソッドで"/edit/{blogId}"にアクセスされた場合に、getBlogEditImagePage()メソッドが呼び出されます。
	 * このメソッドは、@PathVariableアノテーションを使用して、URLからパス変数のblogIdを取得しています。**/
	@GetMapping("/image/edit/{blogId}")
	public String getBlogEditImagePage(@PathVariable Long blogId,Model model) {
		/**
		 * セッションから現在のユーザー情報を取得するため、sessionオブジェクトを使用しています。
		 * UserEntityのインスタンスが取得できた場合、そのuserIdを取得しています。**/
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		/**
		 * userListから現在ログインしている人のユーザー名を取得**/
		String userName = userList.getUserName();
		model.addAttribute("userName", userName);
		/**blogService.getBlogPost(blogId)を使用して、
		 * 指定されたblogIdに対応するブログを取得し、blogListに代入します。**/
		BlogEntity blogList = blogService.getBlogPost(blogId);
		/**blogListがnullであれば、"/user/blog/list"にリダイレクトします。**/
		if(blogList == null) {
			return "redirecr:/user/blog/list";
		}else {
			/**blogListと"画像編集"というメッセージをModelオブジェクトに追加し、"blog-edit.html"に遷移します。**/
			model.addAttribute("blogList", blogList);
			model.addAttribute("editImageMessage", "画像編集");
			return "blog-img-edit.html";
		}

	}
	/**このメソッドはブログの投稿を処理するためのメソッドで以下のような処理を行っています。
	 *セッションから現在のユーザー情報を取得し、そのuserIdを取得する。
	 *画像ファイル名を取得する。
	 *画像ファイルをアップロードする。
	 *createBlogPost()メソッドを呼び出して、ブログをデータベースに登録する。
	 *登録に成功した場合は、blog-register-fix.htmlにリダイレクトする。
	 *登録に失敗した場合は、エラーメッセージをモデルに追加し、blog-register.htmlに戻る。 **/
	/**
	 * @param blogTitle ブログタイトル
	 * @param registerDate　登録日
	 * @param category　カテゴリー
	 * @param blogImage　画像イメージ
	 * @param blogDetail　ブログ詳細
	 * @param model
	 * @return
	 */
	@PostMapping("/image/update")
	/**
	 * @RequestParamアノテーション：
	 * このアノテーションは、HTTPリクエストのパラメーターを引数にバインドするために使用されます。
	 * ブログの画像更新時に、ブログのタ、画像ファイル、ブログIdをパラメータとして受け取ります。**/
	/**
	 * MultipartFile：
	 * MultipartFileは、Spring Frameworkが提供するインターフェースで、
	 * アップロードされたファイルの内容を扱うためのメソッドを提供しています。**/
	public String blogRegister(
			@RequestParam("blogImage") MultipartFile blogImage,
			@RequestParam Long blogId,Model model) {
		/**
		 * セッションから現在のユーザー情報を取得するため、sessionオブジェクトを使用しています。
		 * UserEntityのインスタンスが取得できた場合、そのuserIdを取得しています。**/
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		/**
		 * まず、MultipartFileを使用して、リクエストから送信されたファイルを取得しています。
		 * blogImage.getOriginalFilename()を使用して、アップロードされた画像ファイルの名前を取得し、
		 * Fileを使用して、アップロードするファイルの保存先を指定しています。
		 * ファイルが保存される場所は、"./src/main/resources/static/blog-img/"です。**/
		/**
		 * blogImage.getBytes()を使用して、ファイルのバイナリデータを取得し、BufferedOutputStreamを使用して、
		 * バッファにファイルのバイナリデータを書き込みます。そして、out.close()を呼び出すことで、
		 * バッファを閉じて、ファイルの書き込みを終了させます。
		 * このように、ファイルをアップロードするために、Java標準ライブラリのjava.ioパッケージを使用して、
		 * ファイル操作を行います。**/
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
		/**
		 * 最後に、blogService.editBlogImage()メソッドが呼び出され、入力されたデータをデータベースに保存します。
		 * fileName,、blogId、userIdの各引数を使用して、BlogEntityオブジェクトを生成し、
		 * blogDao.save()メソッドを使用して、データベースに保存します。
		 * editBlogImageメソッドはboolean型の戻り値を返します。trueが返された場合は、blog-edit-fix.htmlページが表示され、
		 * falseが返された場合は、blogListと更新失敗であることを示すメッセージが含まれたblog-img-edit.htmlページが表示されます。**/
		if(blogService.editBlogImage(blogId, fileName, userId)) {
			return "blog-edit-fix.html";
		}else {
			BlogEntity blogList = blogService.getBlogPost(blogId);
			model.addAttribute("blogList",blogList);
			model.addAttribute("editImageMessage", "更新失敗です");
			return "blog-img-edit.html";
		}

	}
	/**@GetMappingアノテーションがgetBlogListPageメソッドに付与されています。
	 * このアノテーションは、HTTP GETリクエストを受け取るメソッドであることを示します。**/
	@GetMapping("/delete/list")
	/**getBlogListPageメソッドは、Modelクラスのインスタンスを引数に取ります。Modelは、
	 * コントローラーからビューに渡すためのデータを格納するために使用されます。**/
	public String getBlogDeleteListPage(Model model) {
		/**
		 * セッションから現在のユーザー情報を取得するため、sessionオブジェクトを使用しています。
		 * UserEntityのインスタンスが取得できた場合、そのuserIdを取得しています。**/
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		/**
		 * userListから現在ログインしている人のユーザー名を取得**/
		String userName = userList.getUserName();
		/**
		 * blogServiceのfindAllBlogPostメソッドを呼び出して、現在のユーザーに関連するブログ投稿を取得しています。
		 * 戻り値はBlogEntityのリストであり、このリストをmodelに追加しています。**/
		List<BlogEntity>blogList = blogService.findAllBlogPost(userId);
		/**
		 * ModelクラスにuserNameとblogList,deleteMessageを追加して、blog-delete.htmlというビューを返しています。
		 * このビューは、userNameとblogList,deleteMessageを表示するためのHTMLテンプレート**/
		model.addAttribute("userName", userName);
		model.addAttribute("blogList", blogList);
		model.addAttribute("deleteMessage", "削除一覧");
		return "blog-delete.html";
	}
	
	@GetMapping("/delete/detail/{blogId}")
	public String getBlogDeleteDetailPage(@PathVariable Long blogId,Model model) {
		/**
		 * セッションから現在のユーザー情報を取得するため、sessionオブジェクトを使用しています。
		 * UserEntityのインスタンスが取得できた場合、そのuserIdを取得しています。**/
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		/**
		 * userListから現在ログインしている人のユーザー名を取得**/
		String userName = userList.getUserName();
		model.addAttribute("userName", userName);
		/**blogService.getBlogPost(blogId)を使用して、
		 * 指定されたblogIdに対応するブログを取得し、blogListに代入します。**/
		BlogEntity blogList = blogService.getBlogPost(blogId);
		/**blogListがnullであれば、"/user/blog/list"にリダイレクトします。**/
		if(blogList == null) {
			return "redirecr:/user/blog/list";
		}else {
			/**blogListと"削除記事詳細というメッセージをModelオブジェクトに追加し、
			 * "blog-delete-detail.html"に遷移します。**/
			model.addAttribute("blogList", blogList);
			model.addAttribute("DeleteDetailMessage", "削除記事詳細");
			return "blog-delete-detail.html";
		}

	}
	
	/**
	 * @param blogId　ブログId
	 * @param model
	 * @return
	 */
	/**このソースコードは、Spring MVCのコントローラーにおいて、HTTP POSTリクエストが "/delete" に送信された場合に、
	 * 指定されたブログ記事を削除するblogDeleteメソッドを定義しています。
	 * メソッドの引数として、@RequestParamアノテーションを使用して、HTTPリクエストのパラメータとして送信されたblogIdを受け取ります。
	 * また、Modelクラスを引数にして、Viewに渡すためのデータを設定することができます。
	 * メソッド内では、blogService.deleteBlog(blogId)を呼び出して、指定されたブログ記事を削除します。
	 * このメソッドの戻り値がtrueであれば、"blog-delete-fix.html"にリダイレクトします。一方、戻り値がfalseであれば、
	 * Modelクラスに "DeleteDetailMessage" 属性を設定して、"blog-deletee.html"に遷移し、エラーメッセージを表示します。**/
	@PostMapping("/delete")
	public String blogDelete(@RequestParam Long blogId,Model model) {
		if(blogService.deleteBlog(blogId)) {
			return "blog-delete-fix.html";
		}else {
			model.addAttribute("DeleteDetailMessage", "記事削除に失敗しました");
			return "blog-delete.html";
		}

	}
	/**
	 * @GetMapping("/logout")は、"/logout"エンドポイントに対するHTTP GETリクエストを処理するためのアノテーションです。
	 * public String Logout()は、ログアウト処理を行うメソッドです。このメソッドは、セッションを無効にして
	 * 、"/user/login"にリダイレクトすることで、ログアウトを実行します。
	 * session.invalidate();は、現在のセッションを無効にするために使用されるコードです
	 * これにより、ユーザーがログアウトしたことが確認されます。
	 * return "redirect:/user/login";は、"/user/login"にリダイレクトするために使用されるコードです。
	 * これにより、ログアウト後にユーザーがログインページにリダイレクトされます。
	 * したがって、このコードは、セッションを無効にして、ユーザーをログアウトし、
	 * ログインページにリダイレクトすることにより、ユーザーの認証を管理します。**/
	@GetMapping("/logout")
	public String Logout() {
		
		session.invalidate();
		return "redirect:/user/login";
	}
	
}
