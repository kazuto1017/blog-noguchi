package blogeweb.ex.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blogeweb.ex.model.dao.BlogDao;
import blogeweb.ex.model.entity.BlogEntity;


@Service
public class BlogService {

	@Autowired
	private BlogDao blogDao;
	
	  // 特定のユーザーのすべてのブログ投稿を取得するメソッド
	public List<BlogEntity> findAllBlogPost(Long userId){

		if(userId == null) {
			return null;

		}else {
			return blogDao.findByUserId(userId);
		}
	}
    // ブログ投稿を作成するメソッド
	public boolean createBlogPost(String blogTitle,String fileName,String blogDetail,String category,Long userId) {

		BlogEntity blogList = blogDao.findByBlogTitle(blogTitle);

		if(blogList == null) {
			blogDao.save(new BlogEntity(blogTitle,fileName,blogDetail,category,userId));

			return true;
		}else {

			return false;
		}
	}
    // 特定のブログ投稿を取得するメソッド
	public BlogEntity getBlogPost(Long blogId) {
		if(blogId == null) {
			return null;
		}else {
			return blogDao.findByBlogId(blogId);
		}
	}
	
    // ブログ投稿と画像を編集するメソッド
	public boolean editBlogPostAndImage(String blogTitle, String blogDetail, String category, Long userId, Long blogId, String fileName) {
	    BlogEntity blogList = blogDao.findByBlogId(blogId);

	    if (blogList == null || userId == null) {
	        return false;
	    }

	    blogList.setBlogTitle(blogTitle);
	    blogList.setBlogDetail(blogDetail);
	    blogList.setCategory(category);
	    blogList.setUserId(userId);

	    if (fileName != null) {
	        blogList.setBlogImage(fileName);
	    }

	    blogDao.save(blogList);
	    return true;
	}

    // ブログ投稿を削除するメソッド
	public boolean deleteBlog(Long blogId) {
		if(blogId == null) {
			return false;
		}else {
			blogDao.deleteByBlogId(blogId);
			return true;
		}
	}
	
}
