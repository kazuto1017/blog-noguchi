package blogeweb.ex.service;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blogeweb.ex.model.dao.UserDao;
import blogeweb.ex.model.entity.UserEntity;


@Service
public class UserService {
	
	
	@Autowired
	private UserDao userDao;
	
	public boolean createAccount(String name,String password) {
		/**
		 * 現在の日時を取得して、registerDateに保存します
		 */
		LocalDateTime registerDate = LocalDateTime.now();
		/**
		 * UserDaoインターフェースのfindByEmailメソッドを使用して、 指定されたemailアドレスを持つUserEntityを検索し,
		 * 結果をuserEntityに格納する。
		 */
		UserEntity userEntity = userDao.findByUserName(name);
		/**
		 * UserEntityが見つからなかった場合、
		 */
		if (userEntity == null) {
			/**
			 * 新しいUserEntityオブジェクトを作成し、
			 * 引数として受け取ったuserName、email、password、registerDateを設定します。
			 * の後、UserDaoのsaveメソッドを呼び出して、 新しいUserEntityオブジェクトを保存します。そして、trueを返します。
			 **/
			userDao.save(new UserEntity(name,password, registerDate));
			return true;
		} else {
			/** UserEntityが見つかった場合、falseを返します。 **/
			return false;
		}
	}
	
	public UserEntity loginAccount(String name, String password) {
		/**
		 * 引数として受け取ったemailとpasswordを使用して、
		 * UserDaoインタフェースのfindByEmailAndPasswordメソッドを呼び出して、 該当するUserEntityを検索します。
		 **/
		
		UserEntity userEntity = userDao.findByUserNameAndPassword(name, password);
		/**
		 * 検索結果として取得したUserEntityがnullであるかどうかを確認し、
		 * nullである場合はログインに失敗したことを示すためにnullを返します。
		 **/
		if (userEntity == null) {
			return null;
		} else {
			/** 検索結果として取得したUserEntityがnullでない場合は、
			 * ログインに成功したことを示すために検索結果のUserEntityを返します **/
			return userEntity;
		}
		/**
		 * つまり、このメソッドは、ユーザーアカウントのログインが成功したかどうかを判断するために使用されます。
		 * UserDaoインタフェースのfindByEmailAndPasswordメソッドが、
		 * emailとpasswordを使用してデータベース内のユーザーエンティティを検索するために使用されます。
		 **/
	}

}



