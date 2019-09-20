package com.qindaorong.edu.api;


import com.qindaorong.edu.model.User;

public interface IHelloService {

    //
    String sayHello(double money);

    /**
     * 保存用户
     * @param user
     * @return
     */
    String saveUser(User user);

}
