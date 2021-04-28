package com.sky.lli.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code-generator
 * @date 2020/04/26
 */
public class MysqlGenerator {

    public static final String DATABASE_URL = "jdbc:mysql://vm-1.lli.com:3306/lli-db1?useUnicode=true"
            + "&serverTimezone=GMT&useSSL=false&characterEncoding=utf8";
    public static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DATABASE_USERNAME = "root";
    public static final String DATABASE_PASSWORD = "root123";

    public static final String OUT_PUT_PATH = "/src/main/java";
    public static final String TEMPLATES_MAPPER_XML_PATH = "/templates/mapper.xml.ftl";
    public static final String XML_PATH = "/src/main/resources/mapper/";
    public static final String XML_POSTFIX = "Mapper";

    public static final String AUTHOR = "lli";
    protected static final String[] SUPER_ENTITY_COLUMNS = {"id", "create_time", "update_time", "create_user",
            "update_user", "lock_version"};
    public static final String PARENT_PACKAGE = "com.sky.lli";
    public static final String ENTITY_PACKAGE = "dao.entity";
    public static final String MAPPER_PACKAGE = "dao.mapper";
    public static final boolean ONLY_ENTITY = false;

    /**
     * RUN THIS
     */
    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        //文件输出目录
        String projectPath = System.getProperty("user.dir") + "/target" + OUT_PUT_PATH;
        gc.setOutputDir(projectPath);
        gc.setAuthor(AUTHOR);
        gc.setOpen(false);
        gc.setServiceName("%sService");
        gc.setBaseResultMap(true);
        gc.setActiveRecord(true);
        gc.setBaseColumnList(true);
        gc.setSwagger2(true);
        gc.setFileOverride(true);
        // 默认雪花算法
        gc.setIdType(IdType.ASSIGN_ID);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(DATABASE_URL);
        dsc.setDriverName(DATABASE_DRIVER);
        dsc.setUsername(DATABASE_USERNAME);
        dsc.setPassword(DATABASE_PASSWORD);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(null);
        pc.setParent(PARENT_PACKAGE);
        pc.setEntity(ENTITY_PACKAGE);
        pc.setMapper(MAPPER_PACKAGE);
        pc.setXml(XML_PATH);

        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig(TEMPLATES_MAPPER_XML_PATH) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return projectPath + XML_PATH + tableInfo.getEntityName() + XML_POSTFIX + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        TemplateConfig templateConfig = new TemplateConfig().setXml(null);

        //打开说明不需要覆盖或者生成以下类
        if (ONLY_ENTITY) {
            templateConfig.setController(null);
            templateConfig.setService(null);
            templateConfig.setServiceImpl(null);
            templateConfig.setMapper(null);
        }

        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //自定义抽象类,包含公共的属性
        strategy.setSuperEntityClass(BaseModel.class);
        strategy.setEntityLombokModel(true);
        strategy.setSuperEntityColumns(SUPER_ENTITY_COLUMNS);
        strategy.setChainModel(true);

        //表名
        String[] tables = new String[]{"base_user"};
        strategy.setInclude(tables);
        strategy.setControllerMappingHyphenStyle(true);
        //乐观锁字段
        strategy.setVersionFieldName("lockVersion");
        strategy.setRestControllerStyle(true);

        // 配置自动填充字段（Entity 会添加相应注解）
        List<TableFill> tableFillList = new ArrayList<>();
        strategy.setTableFillList(tableFillList);

        mpg.setStrategy(strategy);
        // 选择 freemarker 引擎需要指定如下加，注意 pom 依赖必须有！
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

}
