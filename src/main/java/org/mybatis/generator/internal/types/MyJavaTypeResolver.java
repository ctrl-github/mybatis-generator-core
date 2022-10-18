/*
 *    Copyright 2006-2020 the original author or authors.
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
package org.mybatis.generator.internal.types;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.sql.Types;

public class MyJavaTypeResolver extends JavaTypeResolverDefaultImpl{

    /**
     * 覆盖默认类型
     *
     * @param column      列
     * @param defaultType 默认类型
     * @return {@link FullyQualifiedJavaType}
     */
    @Override
    protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer = defaultType;

        switch (column.getJdbcType()) {
            case Types.TINYINT:
                answer = new FullyQualifiedJavaType(Boolean.class.getName());
                break;
            case Types.BIT:
                answer = calculateBitReplacement(column, defaultType);
                break;
            case Types.DATE:
                answer = calculateDateType(column, defaultType);
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                answer = calculateBigDecimalReplacement(column, defaultType);
                break;
            case Types.TIME:
                answer = calculateTimeType(column, defaultType);
                break;
            case Types.TIMESTAMP:
                answer = calculateTimestampType(column, defaultType);
                break;
            default:
                break;
        }

        return answer;
    }
}
