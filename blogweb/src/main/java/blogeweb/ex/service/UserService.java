package blogeweb.ex.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blogeweb.ex.model.dao.UserDao;
import blogeweb.ex.model.entity.UserEntity;


@Service
public class UserService {
	
	
	@Autowired
	private UserDao userDao;
	
    // アカウントを作成するメソッド
	public boolean createAccount(String name,String password) {

		UserEntity userEntity = userDao.findByUserName(name);

		if (userEntity == null) {

			userDao.save(new UserEntity(name,password));
			return true;
		} else {

			return false;
		}
	}
    // アカウントでログインするメソッド
	public UserEntity loginAccount(String name, String password) {

		
		UserEntity userEntity = userDao.findByUserNameAndPassword(name, password);

		if (userEntity == null) {
			return null;
		} else {

			return userEntity;
		}

	}

}



