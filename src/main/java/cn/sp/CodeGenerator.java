package cn.sp;

import java.sql.*;
import java.util.*;

/**
 * @Author: Ship
 * @Description:
 * @Date: Created in 2021/5/8
 */
public class CodeGenerator {


    public static final String DB_URL = "db.url";
    public static final String DB_USERNAME = "db.username";
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_NAME = "db.name";
    /**
     * 字段名称的index
     */
    public static final Integer COLUMN_NAME_INDEX = 3;
    /**
     * 字段数据库类型的index SQL type from java.sql.Types
     */
    public static final Integer DATA_TYPE_INDEX = 6;

    public static final Map<String, String> FIELD_TYPE_MAPPING = new HashMap<>();

    public static final Map<String, String> CLASS_NAME_MAPPING = new HashMap<>();

    static {
        FIELD_TYPE_MAPPING.put("char", "String");
        FIELD_TYPE_MAPPING.put("varchar", "String");
        FIELD_TYPE_MAPPING.put("enum", "String");
        FIELD_TYPE_MAPPING.put("text", "String");
        FIELD_TYPE_MAPPING.put("longtext", "String");
        FIELD_TYPE_MAPPING.put("tinyint", "Byte");
        FIELD_TYPE_MAPPING.put("int", "Integer");
        FIELD_TYPE_MAPPING.put("bigint", "Long");
        FIELD_TYPE_MAPPING.put("date", "LocalDate");
        FIELD_TYPE_MAPPING.put("datetime", "LocalDateTime");
        FIELD_TYPE_MAPPING.put("timestamp", "LocalDateTime");
        FIELD_TYPE_MAPPING.put("double", "Double");
        FIELD_TYPE_MAPPING.put("decimal", "BigDecimal");

        CLASS_NAME_MAPPING.put("LocalDate", "java.time.LocalDate");
        CLASS_NAME_MAPPING.put("LocalDateTime", "java.time.LocalDateTime");
        CLASS_NAME_MAPPING.put("BigDecimal", "java.math.BigDecimal");
    }

    private Properties jdbcProperties;

    public CodeGenerator(Properties jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    public Set<String> getImportClasses(Collection<Map<String, String>> tableData) {
        Set<String> imports = new HashSet<>();
        Iterator<Map<String, String>> iterator = tableData.iterator();
        while (iterator.hasNext()) {
            Map<String, String> fieldMap = iterator.next();
            String type = fieldMap.get("type");
            if (CLASS_NAME_MAPPING.containsKey(type)) {
                imports.add(CLASS_NAME_MAPPING.get(type));
            }
        }
        return imports;
    }


    /**
     * 读取表字段信息
     *
     * @param tableName
     * @return
     */
    public Collection<Map<String, String>> readTableData(String tableName) {
        Collection<Map<String, String>> tableData = new ArrayList<>();
        Connection connection = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcProperties.getProperty(DB_URL), jdbcProperties.getProperty(DB_USERNAME), jdbcProperties.getProperty(DB_PASSWORD));
            String sql = "SELECT \n" +
                    "  TABLE_SCHEMA AS tableSchema,\n" +
                    "  TABLE_NAME AS tableName,\n" +
                    "  COLUMN_NAME AS columnName,\n" +
                    "  ORDINAL_POSITION AS ordinalPosition,\n" +
                    "  IS_NULLABLE AS notNullFlag,\n" +
                    "  DATA_TYPE AS dataType,\n" +
                    "  CHARACTER_MAXIMUM_LENGTH AS columnLength,\n" +
                    "  COLUMN_KEY AS cloumnKey,\n" +
                    "  COLUMN_COMMENT AS cloumnComent \n" +
                    "FROM\n" +
                    "  information_schema.columns \n" +
                    "WHERE table_schema = ? \n" +
                    "  AND table_name = ? ;  ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, jdbcProperties.getProperty(DB_NAME));
            statement.setString(2, tableName);
//            DatabaseMetaData metaData = connection.getMetaData();
//            rs = metaData.getColumns(null, null, tableName, null);
            rs = statement.executeQuery();
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("name", genFieldName(rs.getString(COLUMN_NAME_INDEX)));
                map.put("type", genFieldType(rs.getString(DATA_TYPE_INDEX)));
                tableData.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            return tableData;
        }
    }

    /**
     * 根据表字段名获取java中的字段名
     *
     * @param field 字段名
     * @return
     */
    public static String genFieldName(String field) {
        String result = "";
        ;
        String lowerField = field.toLowerCase();
        String[] fields = lowerField.split("_");
        result += fields[0];
        if (fields.length > 1) {
            for (int i = 1; i < fields.length; i++) {
                result += fields[i].substring(0, 1).toUpperCase() + fields[i].substring(1);
            }
        }
        return result;
    }


    /**
     * 表字段数据库类型转Java类型
     *
     * @param sqlType
     * @return
     */
    public String genFieldType(String sqlType) {
        String javaType = FIELD_TYPE_MAPPING.get(sqlType);
        if (javaType == null) {
            throw new RuntimeException("不支持的sql类型:" + sqlType);
        }
        return javaType;
    }

}
