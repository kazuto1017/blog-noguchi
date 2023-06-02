package blogeweb.ex.controller;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blogeweb.ex.service.UserService;



@SpringBootTest
@AutoConfigureMockMvc
public class UserRejisterControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@BeforeEach
	public void prepareData() {
		  // "John"と"password"の組み合わせの場合、createAccountメソッドはtrueを返す
		when(userService.createAccount(eq("John"),eq("password"))).thenReturn(true);
	    // "John"と"existingPassword"の組み合わせの場合、createAccountメソッドはfalseを返す
		when(userService.createAccount(eq("John"),eq("existingPassword"))).thenReturn(false);
	}

	@Test
	public void testGetUserRegisterPage() throws Exception {
	    // /user/register に対する GET リクエストを作成
		RequestBuilder request = MockMvcRequestBuilders.get("/user/register");
		 // リクエストを実行し、返されるビュー名が "register.html" であることを検証
		mockMvc.perform(request).andExpect(view().name("register.html"));
	}

	//ユーザーの登録が成功した場合を検証
	@Test
	public void testRegisterForm_SubmitForm_Successful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process")
	            .param("name", "John")
	            .param("password", "password");

	    mockMvc.perform(request)
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/user/login"));
	}
	
	
	
	
//name しか入力してないとき	
	@Test
	public void testRegisterForm_OnlyNameOrPassword_Empty() throws Exception {
	    RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process")
	            .param("name", "John")
	            .param("password", "");

	    mockMvc.perform(request)
	            .andExpect(status().isOk())
	            .andExpect(view().name("register.html"));
	}



//passwordしか入力してないとき	
	@Test
	public void testRegisterForm_OnlyPasswordandName_Empty() throws Exception {
	    RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process")
	            .param("name", "")
	            .param("password", "password");

	    mockMvc.perform(request)
	            .andExpect(status().isOk())
	            .andExpect(view().name("register.html"));
	}
	
	//何も入力してないとき	
		@Test
		public void testRegisterForm_Password_EmptyandName_Empty() throws Exception {
		    RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process")
		            .param("name", "")
		            .param("password", "");

		    mockMvc.perform(request)
		            .andExpect(status().isOk())
		            .andExpect(view().name("register.html"));
		}
		
		
		//ユーザーの登録が成功した場合を検証
		@Test
		public void testRegisterForm_SubmitForm_Successful2() throws Exception {
			RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process")
		            .param("name", "John")
		            .param("password", "password");

		    mockMvc.perform(request)
		            .andExpect(status().is3xxRedirection())
		            .andExpect(redirectedUrl("/user/login"));
		}
		

}
