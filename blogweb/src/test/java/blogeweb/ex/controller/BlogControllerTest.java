package blogeweb.ex.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import blogeweb.ex.model.entity.BlogEntity;
import blogeweb.ex.model.entity.UserEntity;
import blogeweb.ex.service.BlogService;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BlogService blogService;

	private MockHttpSession session;


    @InjectMocks
    private BlogController blogController;
    
    
	// 前準備
	@BeforeEach
	public void prepareData() {
		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setUserName("John");

		List<BlogEntity> blogList = new ArrayList<>();
		blogList.add(new BlogEntity());
		blogList.add(new BlogEntity());

		session = new MockHttpSession();
		session.setAttribute("user", user);

		when(blogService.findAllBlogPost(1L)).thenReturn(blogList);
	}

	@Test
	public void testGetBlogListPage() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/blog/main").session(session);

		mockMvc.perform(request).andExpect(status().isOk()).andExpect(view().name("main.html"))
				.andExpect(model().attributeExists("userName", "blogList"));
	}

	// ブログ登録ページを取得
	@Test
	public void testNewBlogPage() throws Exception {
		mockMvc.perform(get("/user/blog/newblog"))
		.andExpect(status()
		.isOk())
		.andExpect(view().name("newblog.html"));
	}
	
	

	//新規登録ができるかどうかの処理
	@Test
	public void testGetBlogRegisterPage() throws Exception {
		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setUserName("John");

		session.setAttribute("user", user);

		mockMvc.perform(get("/user/blog/newblog").session(session))
		.andExpect(status().isOk())
		.andExpect(view().name("newblog.html"));
	}
	
	
	
	
	/////////////////////////////////////////////////////無理
	//新規登録が失敗する時の処理
	@Test
	public void testBlogRegister_DuplicateTitle() throws Exception {
	    // モックの動作設定
	    UserEntity userEntity = new UserEntity(1L, "John");
	    when(session.getAttribute("user")).thenReturn(userEntity); // セッションからのユーザーエンティティのモックを設定
	    when(blogService.createBlogPost(anyString(), anyString(), anyString(), anyString(), anyLong()))
	            .thenReturn(false); // ブログサービスのモックの動作を設定

	    // テストデータの準備
	    MockMultipartFile blogImage = new MockMultipartFile("blogImage", "test-image.jpg", "image/jpeg", new byte[0]);

	    // テスト実行
	    mockMvc.perform(MockMvcRequestBuilders.multipart("/register/process")
	            .file(blogImage)
	            .param("blogTitle", "Test Blog")
	            .param("category", "Test Category")
	            .param("blogDetail", "Test Blog Detail")
	            .session(session))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.view().name("main.html"));

	    // モックのメソッドが正しく呼び出されたことを検証
	    verify(blogService, times(1)).createBlogPost(eq("Test Blog"), anyString(), eq("Test Blog Detail"), eq("Test Category"), eq(userEntity.getUserId()));
	}
	
	


	
	

	@Test
	public void testGetBlogEditPage() throws Exception {
	    UserEntity user = new UserEntity();
	    user.setUserId(1L);
	    user.setUserName("John");

	    session.setAttribute("user", user);

	    BlogEntity blog = new BlogEntity();
	    blog.setBlogId(1L);
	    blog.setBlogTitle("Test Blog");

	    // Mock the behavior
	    when(blogService.getBlogPost(1L)).thenReturn(blog);

	    // Perform the test
	    mockMvc.perform(get("/user/blog/edit/{blogId}", 1L).session(session))
	            .andExpect(status().isOk())
	            .andExpect(view().name("edit.html"))
	    		.andExpect(model().attributeExists("userName", "blogList", "editMessage"))
	    		.andExpect(model().attribute("userName", "John"))
	    		.andExpect(model().attribute("editMessage", "記事編集"))
	    		.andExpect(model().attribute("blogList", blog));
	    // Verify that the mock method was called exactly once
	    verify(blogService, times(1)).getBlogPost(1L);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}