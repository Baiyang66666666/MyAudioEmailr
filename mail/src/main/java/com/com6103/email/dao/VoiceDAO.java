package com.com6103.email.dao;

import com.com6103.email.entity.Voice;
import com.com6103.email.service.impl.VoiceServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class VoiceDAO {
    private JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(Voice.class);

    public VoiceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertVoice(String path, String emailId) {
        String sql = """
                INSERT INTO
                tts_info(mail_id,content)
                VALUES(?,?)
                """;
        return jdbcTemplate.update(sql, emailId, path);
    }

    public Voice getVoiceByMailId(String emailId,String voiceType) {
        Voice tts;
        String regex = String.format("\\b%s\\b", voiceType);
        String sql = """
                SELECT  * FROM tts_info
                WHERE mail_id = ?  AND content REGEXP ?
                """;
        try {
            tts = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Voice.class), emailId,regex);
            logger.info("Get Voice Object");
        } catch (EmptyResultDataAccessException e) {
            tts = null;
            logger.info("Voice does not exist");
        }
        return tts;
    }
}
