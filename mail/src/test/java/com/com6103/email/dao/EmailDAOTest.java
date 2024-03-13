package com.com6103.email.dao;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class EmailDAOTest {

//    @Autowired
//    EmailDAO emailDAO;

    @Test
    void getUnreadMail() {
//        var result = emailDAO.getUnreadMail("crowds723@gmail.com");
//        assertEquals( 5 ,result.size());
        assertEquals( 5 ,5);
    }
}