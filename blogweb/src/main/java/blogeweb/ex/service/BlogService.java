package blogeweb.ex.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blogeweb.ex.model.dao.BlogDao;
import blogeweb.ex.model.entity.BlogEntity;


/**Spring Frameworkの@Serviceアノテーションを使用して、
 * ブログ投稿に関するビジネスロジックを実装したBlogServiceクラスを定義*/
@Service
public class BlogService {
	/**
	 * @Autowired private BlogDao blogDao;
	 * userテーブルにアクセスして操作するため、UserDaoクインタフェース
	 * を使えるようにしておきます。
	 */
	@Autowired
	private BlogDao blogDao;
	/**findAllBlogPostメソッドは、引数としてuserIdを取ります。
	 * userIdがnullであれば、nullを返します。それ以外の場合は
	 * blogDao.findByUserId(userId)を呼び出して、指定されたuserIdに対応する
	 * すべてのブログ投稿を取得し、それらをBlogEntityオブジェクトのリストとして返します。**/
	/**
	 * @param userId ユーザーId
	 * @return
	 */
	public List<BlogEntity> findAllBlogPost(Long userId){
		/** userIdがnullであれば、nullを返します。**/
		if(userId == null) {
			return null;
			/**それ以外の場合は
			 * blogDao.findByUserId(userId)を呼び出して、指定されたuserIdに対応する
			 * すべてのブログ投稿を取得し、それらをBlogEntityオブジェクトのリストとして返します。**/
		}else {
			return blogDao.findByUserId(userId);
		}
	}
	/**このソースコードは、ブログ記事を作成するためのメソッド createBlogPostです。
	 * メソッドは、ブログ記事のタイトル、登録日、ファイル名、詳細などの情報を受け取ります。**/

	/**
	 * @param blogTitle　ブログのタイトル
	 * @param registerDate　登録日
	 * @param fileName　画像のファイル名
	 * @param blogDetail ブログの詳細文
	 * @param category　カテゴリー
	 * @param userId　ユーザーId
	 * @return
	 */
	public boolean createBlogPost(String blogTitle,String fileName,String blogDetail,String category,Long userId) {
		/**
		 * blogDao.findByBlogTitleAndRegisterDate(blogTitle, registerDate) によって、
		 * 既に同じタイトルと登録日のブログ記事が存在するかを検索します。**/
		BlogEntity blogList = blogDao.findByBlogTitle(blogTitle);
		/**
		 * もし、存在しなければ、新しいブログ記事を作成して blogDao.save() によってデータベースに保存します**/
		if(blogList == null) {
			blogDao.save(new BlogEntity(blogTitle,fileName,blogDetail,category,userId));
			/**新しいブログ記事が作成され、データベースに保存された場合は、true を返します。**/
			return true;
		}else {
			/**既に同じタイトルと登録日のブログ記事が存在した場合は、false を返します**/
			return false;
		}
	}
	/**getBlogPostメソッドは、与えられたblogIdに基づいて、blogDaoから該当する
	 * ブログエンティティを取得して返します。blogIdがnullである場合は、nullを返します**/
	public BlogEntity getBlogPost(Long blogId) {
		if(blogId == null) {
			return null;
		}else {
			return blogDao.findByBlogId(blogId);
		}
	}
	
	
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


	public boolean deleteBlog(Long blogId) {
		if(blogId == null) {
			return false;
		}else {
			blogDao.deleteByBlogId(blogId);
			return true;
		}
	}
	
}
