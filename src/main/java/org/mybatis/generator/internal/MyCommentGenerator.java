/*
 *    Copyright 2006-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.internal;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class MyCommentGenerator extends EmptyCommentGenerator {

    /**
     * 方法描述
     */
    public static final Map<String, String> methodDescription   = new HashMap<String, String>() {
        {
//            put("equals", "");
//            put("hashCode", "");
//            put("toString", "");
            put("deleteByPrimaryKey", "根据 PrimaryKey 删除表数据 - ");
            put("insert", "插入数据到表 - ");
            put("insertSelective", "有选择性的插入数据到表 - ");
            put("selectByPrimaryKey", "根据 PrimaryKey 数据查询表 - ");
            put("selectByObject", "根据 当前Object数据 查询List集合 到表 - ");
            put("updateByPrimaryKeySelective", "根据 PrimaryKey 有选择性的修改数据到表 不含TEXT类型字段 - ");
            put("updateByPrimaryKeyWithBLOBs", "根据 PrimaryKey 有选择性的修改数据到表 含TEXT类型字段 - ");
            put("updateByPrimaryKey", "根据 PrimaryKey 有选择性的修改数据到表 - ");
        }};

    private Properties properties;

    private boolean suppressAllComments;

    /** If suppressAllComments is true, this option is ignored. */
    private boolean addRemarkComments;

    private SimpleDateFormat dateFormat;

    public MyCommentGenerator() {
        super();
        properties = new Properties();
        suppressAllComments = false;
        addRemarkComments = false;
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        // 获取自定义的 properties
        this.properties.putAll(properties);
        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
        addRemarkComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));
        String dateFormatString = properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT);
        if (StringUtility.stringHasValue(dateFormatString)) {
            dateFormat = new SimpleDateFormat(dateFormatString);
        }
    }

    /**
     * XML添加元素
     *
     * @param xmlElement xml元素
     */
    @Override
    public void addComment(XmlElement xmlElement) {
        if (suppressAllComments) {
            return;
        }
        String author = properties.getProperty("xml-author");
        if (null!=author && author.trim().length()>=2){//名字必须要有2个长度
            xmlElement.addElement(new TextElement("<!-- author - @"+author+"  -->"));
        }
    }

    /**
     * 添加模型类注释
     *
     * @param topLevelClass     顶级类
     * @param introspectedTable 进行自检表
     */
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String author = properties.getProperty("author");
        String dateFormat = properties.getProperty("dateFormat", "yyyy-MM-dd");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

        // 获取表注释
        String remarks = introspectedTable.getRemarks();
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + ((null!=remarks&&!remarks.trim().equals(""))? remarks:introspectedTable.getTableConfiguration().getTableName()) );
        topLevelClass.addJavaDocLine(" *");

        String dateTimeFormat = properties.getProperty("dateTimeFormat");
        if (dateTimeFormat!=null && dateTimeFormat.equals("true")){
            long count = introspectedTable.getAllColumns().stream().filter(introspectedColumn -> introspectedColumn.getFullyQualifiedJavaType().equals(FullyQualifiedJavaType.getDateInstance())).count();
            if (count>=1){
                topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
                topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
            }
        }

        topLevelClass.addJavaDocLine(" * @author " + author);
        topLevelClass.addJavaDocLine(" * @date " + dateFormatter.format(new Date()));
        topLevelClass.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // 获取列注释
        String remarks = introspectedColumn.getRemarks();
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + remarks);
        field.addJavaDocLine(" */");
        String dateTimeFormat = properties.getProperty("dateTimeFormat");
        if (dateTimeFormat!=null && dateTimeFormat.equals("true")){
            FullyQualifiedJavaType type = field.getType();
            if (type.equals(FullyQualifiedJavaType.getDateInstance())){
                field.addAnnotation("@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
                field.addAnnotation("@JsonFormat(pattern=\"yyyy-MM-dd HH:mm:ss\", timezone=\"GMT+8\")");
            }
        }
    }

    /**
     * 添加一般方法注释
     *
     * @param method            方法
     * @param introspectedTable 进行自检表
     */
    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }

        //只有Mapper 方法才添加
        String description = methodDescription.get(method.getName());
        if (description!=null){
            method.addJavaDocLine("/**");
            //表名
            String tableName = introspectedTable.getTableConfiguration().getTableName();
            method.addJavaDocLine(" * "+description+tableName);
//            addJavadocTag(method, false);
            method.addJavaDocLine(" */");
        }

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); //$NON-NLS-1$
        method.addAnnotation("/====================/");
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source field: " //$NON-NLS-1$
                + introspectedTable.getFullyQualifiedTable().toString()
                + "." //$NON-NLS-1$
                + introspectedColumn.getActualColumnName();
        method.addAnnotation("/====================/");
    }


    /**
     * This method adds the custom javadoc tag for. You may do nothing if you do
     * not wish to include the Javadoc tag - however, if you do not include the
     * Javadoc tag then the Java merge capability of the eclipse plugin will
     * break.
     *
     * @param javaElement
     *            the java element
     */
    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        String dateFormat = properties.getProperty("dateFormat", "yyyy-MM-dd");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        String s = dateFormatter.format(new Date());
        javaElement.addJavaDocLine(" *  @date "+s);
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        String author = properties.getProperty("author");
        sb.append(" @author " + author);
        javaElement.addJavaDocLine(sb.toString());
    }
    @Override
    public void addGetterComment(Method method,IntrospectedTable introspectedTable,IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        method.addJavaDocLine("/**");
        // 获取列注释
        String remarks = introspectedColumn.getRemarks();
        method.addJavaDocLine(" * "+remarks);
        StringBuilder sb = new StringBuilder();
        sb.append(" * @return ");
        String actualColumnName = introspectedColumn.getActualColumnName();
        if (actualColumnName.equals("ID")){
            sb.append("id");
        }else {
            sb.append(captureName(actualColumnName));
        }

        sb.append(" "+remarks);
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }
    @Override
    public void addSetterComment(Method method,
                                 IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        method.addJavaDocLine("/**");
        // 获取列注释
        String remarks = introspectedColumn.getRemarks();
        method.addJavaDocLine(" * "+remarks);
        StringBuilder sb = new StringBuilder();
        sb.append(" * @param ");
        String actualColumnName = introspectedColumn.getActualColumnName();
        if (actualColumnName.equals("ID")){
            sb.append("id");
        }else {
            sb.append(captureName(actualColumnName));
        }
//        sb.append(" "+remarks);
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    public static String captureName(String name) {
        //name = name.substring(0, 1).toLowerCase() + name.substring(1);
        //return  name;

        //进行字母的ascii编码前移活后移
        char[] cs=name.toCharArray();
        if(Character.isUpperCase(cs[0])){//如果是大写 处理 改小写
            cs[0]+=32;
        }else if(Character.isLowerCase(cs[0])){//如果是小写 不处理
//                cs[0]-=32;
        }
        return String.valueOf(cs);

    }

    public static void main(String[] args) {
        System.out.println(captureName("YctualColumnNames"));
    }

}