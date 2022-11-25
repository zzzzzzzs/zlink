package com.zlink.utils;


import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author zs
 * @
 */
public class CodeGenUtil {
    private static String url = "jdbc:mysql://bigdata102:3306/TodaysMatters";
    private static String userName = "root";
    private static String password = "000000";
    private static String driverClassName = "com.mysql.cj.jdbc.Driver";

    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("zs");
        gc.setOpen(false);
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(false);


        mpg.setGlobalConfig(gc);


        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(url);
        // dsc.setSchemaName("public");
        dsc.setDriverName(driverClassName);
        dsc.setUsername(userName);
        dsc.setPassword(password);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.me.todaymatters");
        pc.setEntity("bean");
        pc.setService("service");
        pc.setMapper("dao");
        pc.setXml("dao.mapper");
        pc.setServiceImpl("service.impl");

        StrategyConfig config = new StrategyConfig();
        config.setNaming(NamingStrategy.underline_to_camel);
//        config.setTablePrefix("wx_");
        config.setInclude(new String[]{"student"});
        mpg.setStrategy(config);

        mpg.setPackageInfo(pc);

        mpg.execute();
    }

}
