package cn.sp;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.*;
import java.util.*;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2021/5/8
 */
public class Main {


    public static void main(String[] args) {
        final String basePackage = "cn.sp";
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入命令开始生成Java类(格式：g tableName className)，按Q退出!");
        while (true) {
            String line = scanner.nextLine();
            if ("Q".equals(line.trim())) {
                System.out.println("退出成功！");
                break;
            }
            if (line == null || "".equals(line.trim())) {
                continue;
            }
            String[] params = line.split(" ");
            if (params.length != 3) {
                System.out.println("命令有误！");
                continue;
            }
            String tableName = params[1];
            String className = params[2];
            run(basePackage, tableName, className);
            System.out.println(String.format("生成%s.java成功", className));
        }
    }

    private static void run(String basePackage, String tableName, String className) {
        CodeGenerator generator = new CodeGenerator(getJDBCProperties());
        Collection<Map<String, String>> tableData = generator.readTableData(tableName);
        // 加载模板文件
        try {
            Configuration cfg = new Configuration();
            cfg.setDirectoryForTemplateLoading(new File("src/main/resources/template"));
            cfg.setObjectWrapper(new DefaultObjectWrapper());

            Template template = cfg.getTemplate("entity.ftl");

            Set<String> imports = generator.getImportClasses(tableData);

            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("tableName", tableName);
            modelMap.put("className", className);
            modelMap.put("pkg", basePackage);
            modelMap.put("fields", tableData);
            modelMap.put("imports", imports);

            String filePath = System.getProperty("user.dir") + "/output";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            // 输出到指定文件
            String fileName = filePath + File.separator + className + ".java";
            Writer writer = new FileWriter(fileName);
            template.process(modelMap, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 读取数据库连接配置
     * @return
     */
    public static Properties getJDBCProperties() {
        Properties jdbcProperties = null;
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream("src/main/resources/jdbc.properties"));
            jdbcProperties = new Properties();
            jdbcProperties.load(new InputStreamReader(inputStream, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jdbcProperties;
    }
}
