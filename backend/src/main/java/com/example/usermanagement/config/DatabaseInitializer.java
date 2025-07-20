package com.example.usermanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * 数据库初始化器
 * 在应用启动时执行数据库表创建和初始数据插入
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // 读取并执行SQL脚本
            ClassPathResource resource = new ClassPathResource("db/migration/V1__Create_tables.sql");
            String sql = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

            // 分割SQL语句并执行
            String[] statements = sql.split(";");
            for (String statement : statements) {
                String trimmedStatement = statement.trim();
                if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("--")) {
                    jdbcTemplate.execute(trimmedStatement);
                }
            }
            
            System.out.println("数据库表初始化完成");
        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}