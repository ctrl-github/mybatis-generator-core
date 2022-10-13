# Overview

MyBatis Generator (MBG) is a code generator for the MyBatis SQL
mapping framework.  MBG will introspect database tables (through JDBC
DatabaseMetaData) and generate SQL Map XML files, Java model object (POJOs)
that match the table, and (optionally) Java client classes that use the other
generated objects.

MBG can also generate Kotlin code for MyBatis.

For full documentation, please refer to the user's manual at http://www.mybatis.org/generator/

## Dependencies

There are no dependencies beyond the JRE.  Java 8 or above is required.
Also required is a JDBC driver that implements the DatabaseMetaData interface,
especially the "getColumns" and "getPrimaryKeys" methods.

## Support

Support is provided through the user mailing list.  Mail
questions or bug reports to:

  mybatis-user@googlegroups.com

## 魔改请查询以下链接资料
---
源码基于1.4.1 Git 路径   https://github.com/mybatis/generator/releases/tag/mybatis-generator-1.4.1

(Mybatis-generator修改源代码实现自定义方法,返回List对象 1 ) https://blog.csdn.net/hji7365039/article/details/76273012

(Mybatis-generator修改源代码实现自定义方法,返回List对象 2 ) https://blog.csdn.net/hji7365039/article/details/77745632

(Mybatis-generator修改源代码实现自定义方法,返回List对象 3 ) https://blog.csdn.net/hji7365039/article/details/77745874

---
