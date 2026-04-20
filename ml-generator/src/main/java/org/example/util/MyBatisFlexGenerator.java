package org.example.util;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.EntityConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.JdbcTypeMapping;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.util.Set;
/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-19 14:29
 */
public class MyBatisFlexGenerator {

    @Accessors(chain = true)
    @Setter
    public static class Builder {
        /** MySQL数据库名（必填） */
        private String dbName;
        /** 生成位置目录（必填）：推荐使用绝对路径 */
        private String dir;
        /** 表名列表：默认生成所有表 */
        private Set<String> tableNames;
        /** MySQL主机地址 */
        private String mysqlHost = "192.168.227.128:3306";
        /** MySQL数据库账号 */
        private String mysqlUsername = "root";
        /** MySQL数据库密码 */
        private String mysqlPassword = "root";
        /** 作者 */
        private String author = "WuWenJin";
        /** 注释版本 */
        private String since = "v1.0.0";
        /** 根包 */
        private String basePackage = "org.example";
        /** 控制器前缀 */
        private String controllerPrefix = "/api/v1";
        /** JDK版本 */
        private Integer jdkVersion = 17;
        /** 是否生成 Swagger 注释 */
        private boolean withSwagger = true;
        /** Swagger 版本 */
        private EntityConfig.SwaggerVersion swaggerVersion = EntityConfig.SwaggerVersion.DOC;
        /** 是否生成 Lombok 注释 */
        private boolean withLombok = true;
        /** 是否开启覆盖模式 */
        private boolean overwriteEnable = true;
        /** 逻辑删除字段 */
        private String logicDeleteColumn = "deleted";
        /** 乐观锁字段 */
        private String versionColumn = "version";

        public Generator build() {
            // 将 BigDecimal 类型转为 Double 类型
            JdbcTypeMapping.registerMapping(BigDecimal.class, Double.class);
            // 配置 HikariDataSource 对象（Hikari 数据源）
            HikariDataSource dataSource = buildDataSource();
            // 配置 GlobalConfig 对象（MyBatisFlex 生成器配置）
            GlobalConfig globalConfig = buildGlobalConfig();
            // 创建并返回 MyBatisFlex 生成器对象
            return new Generator(dataSource, globalConfig);
        }

        private HikariDataSource buildDataSource() {
            HikariDataSource hikariDataSource = new HikariDataSource();
            hikariDataSource.setJdbcUrl("jdbc:mysql://%s/%s?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8".formatted(mysqlHost, dbName));
            hikariDataSource.setUsername(mysqlUsername);
            hikariDataSource.setPassword(mysqlPassword);
            return hikariDataSource;
        }

        private GlobalConfig buildGlobalConfig() {
            // 创建 GlobalConfig 对象（MyBatisFlex 生成器配置）
            GlobalConfig globalConfig = new GlobalConfig();
            // 设置逻辑删除字段
            globalConfig.setLogicDeleteColumn(logicDeleteColumn);
            // 设置乐观锁字段
            globalConfig.setVersionColumn(versionColumn);
            // 注释配置: 作者 + 注释版本
            globalConfig.getJavadocConfig().setAuthor(author).setSince(since);
            // 包配置: src目录 + 根包
            globalConfig.getPackageConfig().setSourceDir(dir).setBasePackage(basePackage);
            // 策略配置: 指定数据库
            globalConfig.getStrategyConfig().setGenerateSchema(dbName);
            // 生成实体类：Lombok注解 + Swagger注解 + Swagger版本 +JDK版本 + 重新生成时覆盖
            globalConfig.enableEntity().setWithLombok(withLombok).setWithSwagger(withSwagger).setSwaggerVersion(swaggerVersion).setJdkVersion(jdkVersion).setOverwriteEnable(overwriteEnable);
            // 生成实体类辅助类：重新生成时覆盖
            globalConfig.enableTableDef().setOverwriteEnable(overwriteEnable);
            // 生成数据层代码（接口）：重新生成时覆盖
            globalConfig.enableMapper().setOverwriteEnable(overwriteEnable);
            // 生成业务层代码（接口）：重新生成时覆盖
            globalConfig.enableService().setOverwriteEnable(overwriteEnable);
            // 生成业务层代码（实现类）：重新生成时覆盖
            globalConfig.enableServiceImpl().setOverwriteEnable(overwriteEnable);
            // 生成控制层代码：请求统一前缀 + 重新生成时覆盖
            globalConfig.enableController().setRequestMappingPrefix(controllerPrefix).setOverwriteEnable(overwriteEnable);
            // 指定生成哪些表
            if (tableNames != null && !tableNames.isEmpty()) globalConfig.setGenerateTables(tableNames);
            // 返回 GlobalConfig 对象
            return globalConfig;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}

