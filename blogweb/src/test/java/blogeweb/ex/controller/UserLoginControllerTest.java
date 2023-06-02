package blogeweb.ex.controller;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;



import jakarta.servlet.http.HttpSession;
import blogeweb.ex.model.entity.UserEntity;
import blogeweb.ex.service.UserService;

/**@SpringBootTest：Spring Bootアプリケーションのコンテキストをロードしてテストを実行するための注釈です。**/
@SpringBootTest
/**@AutoConfigureMockMvc：Spring MVCテストのためにMockMvcを自動的に設定するための注釈です。。**/
@AutoConfigureMockMvc
public class UserLoginControllerTest {
	/**MockMvc mockMvc：テスト対象のコントローラと対話するためのモックされたMVCモックオブジェクトです。**/
	@Autowired
	private MockMvc mockMvc;
	/** @MockBean：モックオブジェクトを作成して注入するための注釈です。このテストでは、UserServiceのモックオブジェクトが作成されます。**/
	@MockBean
	private UserService userService;
	

    private UserEntity userEntity;

	//テストケースの前処理
	@BeforeEach
	public void prepareData() {
		//正解のオブジェクト
	       userEntity = new UserEntity(1L, "aaaa", "1234");
	       
	       //ログインが正しい時のモック
			when(userService.loginAccount(eq("aaaa"), eq("1234"))).thenReturn(userEntity);
			//ログインが間違っている時のモック
			when(userService.loginAccount(eq("aaaa"), eq("4321"))).thenReturn(null);
			when(userService.loginAccount(eq("bbbb"), eq("1234"))).thenReturn(null);
			when(userService.loginAccount(eq("bbbb"), eq("4321"))).thenReturn(null);
	}

	
	//ログインページが正しく取得されているか確認
	@Test
	public void testGetLoginPage_Succeed() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/login");

		mockMvc.perform(request).andExpect(view().name("login.html"));
	}
	
	
	
	
	
	
	//正常なログインができるかどうかのテスト
	@Test
	public void testLogin_Successful() throws Exception {
		//ログインリクエストの制作
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process")
				.param("name", "aaaa")
				.param("password", "1234");

		//リクエストの実行と結果の取得
		MvcResult result = mockMvc.perform(request)
				.andExpect(redirectedUrl("/user/blog/main"))
				.andReturn();

		//セッションの取得
		HttpSession session = result.getRequest().getSession();

		//セッションからログインユーザー情報を取得
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		
		//ログインユーザーがnullではないことの検証
		assertNotNull(loggedInUser);
		
		//ログインユーザーの名前とパスワードが正解かどうか検証
		assertEquals("aaaa",loggedInUser.getUserName());
		assertEquals("1234",loggedInUser.getPassword());
	}

	
	
	
	
	
	//間違ったパスワードでのログインをテスト
	@Test
	public void testLogin_WrongPassword_Unsuccessful() throws Exception {
		
		//ログインリクエストを制作
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process")
				.param("name", "aaaa")
				.param("password", "4321");
		
		//リクエストを実行し結果を検証
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));

		//ログイン後にセッションを取得しログインユーザーがnullであることを検証
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest()
				.getSession();

		// セッションからログインユーザーを取得
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		//ログインユーザーがnull であることを検証
		assertNull(loggedInUser);
	}

	//間違ったnameでのログインをテスト
	@Test
	public void testLogin_WrongName_Unsuccessful() throws Exception {
		//ログインリクエストを制作
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process")
				.param("name", "bbbbb")
				.param("password", "1234");

		//リクエストを実行し結果を検証
		mockMvc.perform(request).andExpect(status()
				.is3xxRedirection())
				.andExpect(redirectedUrl("/user/login"));

		//ログイン後にセッションを取得し、ログインユーザーがnullであることを検証
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest()
				.getSession();

		
		// セッションからログインユーザーを取得
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		//ログインユーザーがnull であることを検証
		assertNull(loggedInUser);
	}
	
	
	
	
	//間違ったnameとpasswordでログインしたときのテスト
	@Test
	public void testLogin_WrongNameAndWrongPassword_Unsuccessful() throws Exception {
		//ログインリクエストを制作
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process")
				.param("name", "bbbb")
				.param("password", "4321");

		//リクエストの実行
		mockMvc.perform(request).andExpect(status()
				.is3xxRedirection())
				.andExpect(redirectedUrl("/user/login"));

		//ログイン後のセッションを取得しログインユーザーを取得
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login"))
				.andReturn()
				.getRequest()
				.getSession();

		//ログインユーザーがnull であることを検証
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);
	}
	
	
	
	//何も入力しないｄnameとpasswordでログインしたときのテスト
	@Test
	public void testLogin_NullNameAndNullPassword_Unsuccessful() throws Exception {
		//ログインリクエストを制作
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process")
				.param("name", "bbbb")
				.param("password", "4321");

		//リクエストの実行
		mockMvc.perform(request).andExpect(status()
				.is3xxRedirection())
				.andExpect(redirectedUrl("/user/login"));

		//ログイン後のセッションを取得しログインユーザーを取得
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login"))
				.andReturn()
				.getRequest()
				.getSession();

		//ログインユーザーがnull であることを検証
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);
	}



}
