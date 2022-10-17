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
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 选择对象元素生成器
 *
 * @author ctrl-github https://github.com/ctrl-github
 * @date 2022/10/14
 */
public class SelectByObjectElementGenerator extends AbstractXmlElementGenerator {

    public SelectByObjectElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        //先创建一个select标签
        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        //设置该select标签的id，正式第二篇里在枚举中设置的值
        answer.addAttribute(new Attribute("id", introspectedTable.getSelectByObject())); //$NON-NLS-1$

        //添加parameterType
        String parameterType;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            parameterType = introspectedTable.getRecordWithBLOBsType();
        } else {
            parameterType = introspectedTable.getBaseRecordType();
        }

        answer.addAttribute(new Attribute("parameterType", parameterType));
        //设置resultMap为BaseResultMap
        answer.addAttribute(new Attribute("resultMap",introspectedTable.getBaseResultMapId()));

        context.getCommentGenerator().addComment(answer);

        //接下去是拼接我们的sql
        StringBuilder sb = new StringBuilder();
        buildSelectList("select ", introspectedTable.getAllColumns()).forEach(answer::addElement); //$NON-NLS-1$

        sb.append("from "); //$NON-NLS-1$
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        //在这里添加where条件
        XmlElement selectTrimElement = new XmlElement("trim"); //设置trim标签
        selectTrimElement.addAttribute(new Attribute("prefix", "where"));
        selectTrimElement.addAttribute(new Attribute("prefixOverrides", "and")); //添加where和and

        answer.addElement(selectTrimElement);
        //循环所有的列
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement selectNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null ");
            selectNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            sb.setLength(0);
            sb.append(" and "); //添加and
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //添加等号
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            selectNotNullElement.addElement(new TextElement(sb.toString()));
            selectTrimElement.addElement(selectNotNullElement);
        }

        String orderByClause = introspectedTable.getTableConfigurationProperty(
                PropertyRegistry.TABLE_SELECT_ALL_ORDER_BY_CLAUSE);
        boolean hasOrderBy = StringUtility.stringHasValue(orderByClause);
        if (hasOrderBy) {
            sb.setLength(0);
            sb.append("order by "); //$NON-NLS-1$
            sb.append(orderByClause);
            answer.addElement(new TextElement(sb.toString()));
        }

        if (context.getPlugins().sqlMapSelectAllElementGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
