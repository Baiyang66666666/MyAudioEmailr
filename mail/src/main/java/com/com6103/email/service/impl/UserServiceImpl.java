package com.com6103.email.service.impl;

import com.com6103.email.dao.UserDAO;
import com.com6103.email.entity.Account;
import com.com6103.email.entity.AccountSchedule;
import com.com6103.email.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public int insertAccount(Account account) {
        return userDAO.insertAccount(account);
    }

    @Override
    public List<Account> getAccountList(String deviceId) {
        return userDAO.getAccountList(deviceId);
    }

    @Override
    public Account getUserById(String userId) {
        return userDAO.getUserById(userId);
    }

    @Override
    public Account getUserByDevAndAddr(String deviceId, String address) {
        return userDAO.getUserByDevAndAddr(deviceId,address);
    }

    @Override
    public boolean isUserExistsByEmail(String address) {
        return userDAO.isUserExistsByEmail(address);
    }

    @Override
    public AccountSchedule getUserScheduleById(String userId) {
        return userDAO.getUserScheduleById(userId);
    }

    public boolean isScheduleExist(String userId){
        return userDAO.isScheduleExist(userId);
    }

    @Override
    @Transactional
    public int addAccountSchedule(AccountSchedule accountSchedule) {
        return userDAO.addAccountSchedule(accountSchedule);
    }

    @Override
    @Transactional
    public int UpdateAccountSchedule(AccountSchedule accountSchedule) {
        return userDAO.UpdateAccountSchedule(accountSchedule);
    }

    @Override
    public int deleteAccountById(Integer userId) {
        return userDAO.deleteAccountById(userId);
    }

    @Override
    public int deleteScheduleById(Integer userId) {
        return userDAO.deleteSchduleById(userId);
    }


}
