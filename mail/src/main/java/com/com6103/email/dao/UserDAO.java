package com.com6103.email.dao;

import com.com6103.email.entity.Account;
import com.com6103.email.entity.AccountSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertAccount(Account account) {
        String sql = """
                INSERT INTO 
                system_account(address, password,username,smtp,imap,accountType,deviceId) 
                VALUES(?,?,?,?,?,?,?)
                """;
        return jdbcTemplate.update(sql, account.getAddress(), account.getPassword()
                , account.getUsername(), account.getSmtp(), account.getImap()
                , account.getAccountType(), account.getDeviceId());
    }

    public List<Account> getAccountList(String deviceId) {
        String sql = """
                SELECT * 
                FROM system_account 
                WHERE deviceId = ?
                """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Account.class), deviceId);
    }

    public Account getUserById(String userId) {
        String sql = """
                SELECT * 
                FROM system_account
                WHERE userId = ?
                """;
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Account.class), userId);


    }

    @Transactional
    public Account getUserByDevAndAddr(String deviceId, String address) {
        String sql = """
                SELECT * FROM system_account 
                WHERE deviceId = ? AND address = ?
                """;
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Account.class), deviceId, address);
    }

    public boolean isUserExistsByEmail(String address) {
        String sql = """
                SELECT COUNT(*) 
                FROM system_account 
                WHERE address= ?
                """;
        try {
            Integer count = jdbcTemplate.query(sql, new Object[]{address},
                    (rs, rowNum) -> {
                        return rs.getInt(1); // Get the first column
                    }).get(0);  // A list contains some Integer
            return count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public AccountSchedule getUserScheduleById(String userId) {
        String sql = """
                SELECT * FROM account_schedule 
                WHERE userId = ? 
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(AccountSchedule.class), userId);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isScheduleExist(String userId) {
        String sql = """
                SELECT COUNT(*) 
                FROM account_schedule 
                WHERE userId= ?
                """;
        try {
            Integer count = jdbcTemplate.query(sql, new Object[]{userId},
                    (rs, rowNum) -> {
                        return rs.getInt(1); // Get the first column
                    }).get(0);  // A list contains some Integer
            return count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public int addAccountSchedule(AccountSchedule accountSchedule) {
        String sql = """
                INSERT INTO 
                account_schedule(userId, schedule_time, voice_type) 
                VALUES(?,?,?)
                """;
        return jdbcTemplate.update(sql, accountSchedule.getUserId(), accountSchedule.getSchedule_time(), accountSchedule.getVoice_type());
    }

    public int UpdateAccountSchedule(AccountSchedule accountSchedule) {
        String sql = """
                UPDATE account_schedule 
                SET schedule_time=?, voice_type=?
                WHERE userId=?
                """;
        return jdbcTemplate.update(sql, accountSchedule.getSchedule_time(), accountSchedule.getVoice_type(), accountSchedule.getUserId());
    }

    public int deleteAccountById(Integer userId) {
        String sql = """
                delete from system_account where userId = ?
                 """;
        return jdbcTemplate.update(sql, userId);
    }

    public int deleteSchduleById(Integer userId) {
        String sql = """
                delete from account_schedule where userId = ?
                 """;
        return jdbcTemplate.update(sql, userId);
    }

}
