# java-code-generator
根据数据库表结构自动生成Java实体类<br>
# 前言
Mybatis框架有一个自动生成实体类的工具，但是它的数据库字段映射为Java类时都是老API的类型（如mysql的datetime映射为Date），
这对于喜欢用1.8API类型的我是无法接受的。而且由于用到了lombok和mybatisplus框架提高开发效率，所以希望把一些注解也同时生成，于是就自己写了这个小项目。

**使用方法：**
## 1.配置数据库
修改jdbc.properties文件，配置数据库信息
## 2.运行Main.class的main（）方法
## 3.输入命令生成Java文件
示例：
```
请输入命令开始生成Java类(格式：g tableName className)，按Q退出!
g t_app App
生成App.java成功
11
命令有误！
Q
退出成功！
```
