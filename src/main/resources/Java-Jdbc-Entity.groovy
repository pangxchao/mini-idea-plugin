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
@SuppressWarnings(['DuplicatedCode', 'unused'])
static def generateImports(String placeholder, List<DbColumn> columnList, BuilderWriter out) {
    out.println """${placeholder}"""
    columnList.stream().map {
        it.typeImport
    }.distinct().forEach {
        out.println """${placeholder}import ${it};"""
    }
}

// 定义生成字段常量的方法
@SuppressWarnings('DuplicatedCode')
static def generateColumnConstant(String placeholder, Collection<DbColumn> columnList, BuilderWriter out) {
    columnList.forEach {
        out.println """${placeholder}// ${it.comment} """
        out.println """${placeholder}public static final String ${it.name.toUpperCase()} = "${it.name}"; """
    }
}

// 定义属性生成的方法
@SuppressWarnings('DuplicatedCode')
static def generateProperty(String placeholder, Collection<DbColumn> columnList, BuilderWriter out, boolean hid) {
    columnList.forEach {
        out.println """${placeholder}"""
        out.println """${placeholder}// ${defaultIfBlank(it.comment, it.fieldName)} """
        if (hid && it.isId()) {
            out.println """${placeholder}@Id """
        }
        out.println """${placeholder}@Column(${it.name.toUpperCase()}) """
        out.println """${placeholder}private ${it.javaType} ${it.fieldName}; """
    }
}

// 定义Getter Setter生成的方法
@SuppressWarnings(['DuplicatedCode', 'unused'])
static def generateGetterSetter(String placeholder, Collection<DbColumn> columnList, BuilderWriter out) {
    columnList.forEach {
        // Getter 方法
        out.println """${placeholder}"""
        out.println """${placeholder}public ${it.javaType}  ${it.getJavaGetterName()}(){ """
        out.println """${placeholder}${T}return  ${it.fieldName}; """
        out.println """${placeholder}} """
        // Setter 方法
        out.println """${placeholder}"""
        out.println """${placeholder}public void ${it.getJavaSetterName()}(${it.javaType} ${it.fieldName}){ """
        out.println """${placeholder}${T}this.${it.fieldName} = ${it.fieldName}; """
        out.println """${placeholder}} """
    }
}

// 设置包名与文件名，确定生成代码的路径
//noinspection GrUnresolvedAccess
final BuilderWriter out = builderWriter
out.setPackageName("${info.packageName}.entity")
out.setFileName("${info.entityName}.java")

out.println """package ${info.packageName}.entity; """
// Spring 相关包导入
out.println """"""
out.println """import org.springframework.data.annotation.*; """
out.println """import org.springframework.data.relational.core.mapping.*; """
// 自动代码生成插件包
out.println """"""
out.println """import lombok.*;"""
out.println """import lombok.experimental.*;"""
// 序列化包导入
out.println """ """
out.println """import java.io.Serializable; """

// 属性类型需要包导入
generateImports("", importColumnList, out)

// 类签名
out.println """"""
out.println """@Data"""
out.println """@SuperBuilder(toBuilder = true)"""
out.println """@Table(${info.entityName}.${info.tableName.toUpperCase()}) """
out.println """public class ${info.entityName} implements Serializable { """

// 生成表名常量
out.println """"""
out.println """${T}// ${info.comment} """
out.println """${T}public static final String ${info.tableName.toUpperCase()} = "${info.tableName}"; """

// 生成字段常量
generateColumnConstant(T, info.columnList, out)

//noinspection DuplicatedCode
generateProperty(T, info.columnList, out, idColumnList.size() <= 1)

// 默认构造方法
out.println """"""
out.println """${T}@Tolerate"""
out.println """${T}public ${info.entityName}(){ """
out.println """${T}} """

// 生成 Getter Setter
// generateGetterSetter(T, info.columnList, out);

// 结尾
out.println """ """
out.println """ """
out.println """}"""
out.println """ """
out.println """ """

