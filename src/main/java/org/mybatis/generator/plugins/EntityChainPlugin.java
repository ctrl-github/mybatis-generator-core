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
package org.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class EntityChainPlugin extends PluginAdapter {

    /**在为表中的特定列生成setter或mutator方法之后调用此方法。
     * 模型setter方法生成
     *
     * @param method
     * @param topLevelClass      顶级类
     * @param introspectedColumn 进行自检列
     * @param introspectedTable  进行自检表
     * @param modelClassType     模型类类型
     * @return boolean
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {

        method.setReturnType(topLevelClass.getType());
        method.addBodyLine("return this;");
        return super.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    /**
     * This plugin is always valid - no properties are required
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}