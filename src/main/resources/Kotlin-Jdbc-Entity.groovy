import com.mini.plugin.state.DbColumn
import com.mini.plugin.state.DbTable
import com.mini.plugin.util.BuilderWriter

import java.util.stream.Collectors

import static com.mini.plugin.util.Constants.T
import static org.apache.commons.lang3.StringUtils.defaultIfBlank

//noinspection GrUnresolvedAccess
DbTable info = tableInfo

// 获取所有ID字段
@SuppressWarnings('DuplicatedCode')
List<DbColumn> idColumnList = info.columnList.stream().filter {
    it -> it.isId()
}.collect(Collectors.toList())

// 获取所有需要import语句的字段
@SuppressWarnings('DuplicatedCode')
List<DbColumn> importColumnList = info.columnList.stream().filter {
    it.typeImport.length() > 0
}.collect(Collectors.toList())

// 定义生成import语句的方法
@SuppressWarnings('DuplicatedCode')
static def generateImports(String placeholder, List<DbColumn> columnList, BuilderWriter out) {
    out.println """${placeholder}"""
    columnList.stream().map { it.typeImport }.distinct().forEach {
        out.println """${placeholder}import ${it}"""
    }
}

// 定义生成字段常量的方法
@SuppressWarnings('DuplicatedCode')
static def generateColumnConstant(String placeholder, Collection<DbColumn> columnList, BuilderWriter out) {
    columnList.forEach {
        out.println """${placeholder}// ${it.comment} """
        out.println """${placeholder}const val ${it.name.toUpperCase()} = "${it.name}" """
    }
}

// 定义属性生成方法
@SuppressWarnings('DuplicatedCode')
static def generateProperty(String placeholder, Collection<DbColumn> columnList, BuilderWriter out, boolean hid) {
    int index = 0
    columnList.forEach {
        out.println """${placeholder}// ${defaultIfBlank(it.comment, it.fieldName)} """
        if (hid && it.isId()) {
            out.println """${placeholder}@Id """
        }
        final String end = index < columnList.size() - 1 ? ", " : " "
        out.println """${placeholder}@Column(${it.name.toUpperCase()}) """
        out.println """${placeholder}var ${it.fieldName}: ${it.kotlinType}?${end} """
        index++
    }
}

// 设置包名与文件名，确定生成代码的路径
//noinspection GrUnresolvedAccess
final BuilderWriter out = builderWriter
out.setPackageName("${info.packageName}.entity")
out.setFileName("${info.entityName}.kt")

// 文件包声明
out.println """@file:Suppress("unused") """
out.println """ """
out.println """package ${info.packageName}.entity """
out.println """ """

// Spring 相关包导入
out.println """"""
out.println """import org.springframework.data.annotation.* """
out.println """import org.springframework.data.relational.core.mapping.* """

// 属性类型需要导入
generateImports("", importColumnList, out)

// 生成表名常量
out.println """"""
out.println """${T}// ${info.comment} """
out.println """${T}const val ${info.tableName.toUpperCase()} = "${info.tableName}" """

// 生成字段常量
generateColumnConstant(T, info.columnList, out)

// 类的声明
out.println """"""
out.println """@Table(${info.tableName.toUpperCase()}) """
out.println """data class ${info.entityName} constructor( """

// 生成字段属性
generateProperty(T, info.columnList, out, idColumnList.size() <= 1)

// 类声明结尾
out.println """) """
out.println """ """
out.println """ """
out.println """ """



