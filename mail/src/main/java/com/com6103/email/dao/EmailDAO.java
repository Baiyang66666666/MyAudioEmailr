package com.com6103.email.dao;

import com.com6103.email.entity.Mail;
import com.com6103.email.service.impl.EmailServiceImpl;
import com.com6103.email.utils.Utils;
import jdk.jshell.execution.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class EmailDAO {
    JdbcTemplate jdbcTemplate;
    private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mail> getReceiveList(String address) {
        String sql = """
                SELECT * FROM mail_info 
                WHERE to_user=? and delete_flag = '0'
                ORDER BY mail_id DESC 
                """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Mail.class), address);
    }

    public List<Mail> getSendList(String address) {
        String sql = """
                SELECT * FROM mail_info WHERE from_user=? and delete_flag = '0'
                """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Mail.class), address);
    }

    public Mail getMailById(String emailId) {
        Mail mail;
        String sql = """
                SELECT * FROM mail_info WHERE mail_id=? and delete_flag = '0'
                """;
        try {
            mail = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Mail.class), emailId);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Email Does not exist:" + emailId);
            mail = null;
        }
        return mail;
    }

    public int[] insertEmails(List<Mail> mailList) {
        var mails = Utils.transposeArrayList(mailList);
        String sql = """
                INSERT INTO
                mail_info(message_id,to_user,from_user,subject,content,read_flag,delete_flag)
                VALUES(?,?,?,?,?,?,?)
                """;
        List<Object[]> batchArgs = new ArrayList<>();
        for (Mail mail : mails) {
            Object[] args = new Object[]{mail.getMessage_id(), mail.getTo_user()
                    , mail.getFrom_user(), mail.getSubject(), mail.getContent()
                    , mail.getRead_flag(), mail.getDelete_flag()};
            batchArgs.add(args);
        }
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public int insertEmail(Mail mail) {
        String sql = """
                INSERT INTO
                mail_info(to_user,from_user,subject,content,read_flag,create_time,delete_flag)
                VALUES(?,?,?,?,?,?,?)
                """;
        return jdbcTemplate.update(sql, mail.getTo_user(), mail.getFrom_user(), mail.getSubject(), mail.getContent(), mail.getRead_flag(), mail.getCreate_time(), mail.getDelete_flag());
    }

    public int setRead(String mailId) {
        String sql = """
                UPDATE mail_info 
                SET read_flag = ?
                WHERE mail_id = ?
                """;
        return jdbcTemplate.update(sql, "1", mailId);
    }

    public Mail getLatestReceivedMail(String receiver) {
        String sql = """
                SELECT  * FROM mail_info
                WHERE to_user = ? 
                ORDER BY mail_id DESC 
                LIMIT 1
                """;
        // TODO null
        Mail mail = null;
        try {
            mail = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Mail.class), receiver);
        } catch (DataAccessException e) {
            return new Mail();
        }
        return mail;
    }

    public List<Mail> getUnreadMail(String address){
        String sql = """
                SELECT * FROM mail_info 
                WHERE read_flag=0 AND to_user=? and delete_flag = '0'
                ORDER BY  create_time DESC , mail_id ASC
                """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Mail.class), address);
    }

    public int deleteEmail(String mailId, String userId){
        String sql = """
                UPDATE mail_info 
                SET delete_flag = "1", deleteby= ?, delete_time = now()
                WHERE mail_id = ?
                """;
        return jdbcTemplate.update(sql, userId,mailId);
    }

}
