import com.mini.plugin.extension.StringKt
import com.mini.plugin.state.DbColumn
import com.mini.plugin.state.DbTable
import com.mini.plugin.util.BuilderWriter

import java.util.stream.Collectors

import static com.mini.plugin.util.Constants.T

// 接收表配置信息
//noinspection GrUnresolvedAccess
DbTable info = tableInfo

// 获取所有ID字段
@SuppressWarnings('DuplicatedCode')
List<DbColumn> idColumnList = info.columnList.stream().filter {
    it -> it.isId()
}.collect(Collectors.toList())

// 生成所有ID在调用方法的时候传递的参数串
String idParameterNoType = idColumnList.stream().map {
    return it.getFieldName()
}.collect(Collectors.joining(", "))

// 生成所在ID在方法签名时候的参数串
String idParameterAndType = idColumnList.stream().map {
    return it.getJavaType() + " " + it.getFieldName()
}.collect(Collectors.joining(", "));

// 获取ID字段的类型
@SuppressWarnings('DuplicatedCode')
String idType = idColumnList.size() > 1 ? "Object" : idColumnList.stream().findAny().map {
    it.javaType
}.orElse("String")

// 获取父类Class名称
@SuppressWarnings('DuplicatedCode')
String repositoryClass = idColumnList.size() > 1 ? "Repository" : "PagingAndSortingRepository"

// 生成多主键 findById(Id id1,Id id2...) 方法
@SuppressWarnings('DuplicatedCode')
static def generateFindByIdMethod(String placeholder, DbTable info, Collection<DbColumn> columnList, BuilderWriter out, String idParameterAndType) {
    out.println """${placeholder} """
    out.println """${placeholder}default Optional<${info.entityName}> findById(${idParameterAndType}) { """
    out.println """${placeholder}${T}return ofNullable(selectOne(${info.entityName}.class, it->{ """
    columnList.forEach { column ->
        String columnName = column.getColumnName().toUpperCase()
        out.println """${placeholder}${T}${T}it.whereEq(${columnName}, ${column.getFieldName()}); """
    }
    out.println """${placeholder}${T}})); """
    out.println """${placeholder}} """
}

// 生成多主键 existsById(Id id1,Id id2...) 方法
@SuppressWarnings('DuplicatedCode')
static def generateExistsByIdMethod(String placeholder, BuilderWriter out, String idParameterAndType, String idParameterNoType) {
    out.println """${placeholder} """
    out.println """${placeholder}default boolean existsById(${idParameterAndType}) { """
    out.println """${placeholder}${T}return findById(${idParameterNoType}).isPresent(); """
    out.println """${placeholder}} """
}

// 生成多主键 deleteById(Id id1,Id id2...) 方法
@SuppressWarnings('DuplicatedCode')
static def generateDeleteByIdMethod(String placeholder, DbTable info, Collection<DbColumn> columnList, BuilderWriter out, String idParameterAndType) {
    out.println """${placeholder} """
    out.println """${placeholder}default int deleteById(${idParameterAndType}) { """
    out.println """${placeholder}${T}return this.delete(${info.tableName.toUpperCase()}, it->{ """
    columnList.forEach { column ->
        String columnName = column.getColumnName().toUpperCase()
        out.println """${placeholder}${T}${T}it.whereEq(${columnName}, ${column.getFieldName()}); """
    }
    out.println """${placeholder}${T}}); """
    out.println """${placeholder}} """
}

// 设置包名与文件名，确定生成代码的路径
//noinspection GrUnresolvedAccess
final BuilderWriter out = builderWriter
out.setPackageName("${info.packageName}.repository")
out.setFileName("${info.entityName}Repository.java")

// 包名
out.println """package ${info.packageName}.repository; """
out.println """ """
out.println """import com.mini.core.jdbc.MiniRepository; """
out.println """import ${info.packageName}.entity.${info.entityName}; """
out.println """import org.springframework.data.repository.*; """
out.println """ """
out.println """import java.util.*;"""
out.println """ """
out.println """import static java.util.Optional.ofNullable; """
out.println """import static ${info.packageName}.entity.${info.entityName}.*; """
out.println """ """
out.println """@org.springframework.stereotype.Repository("${StringKt.firstLowerCase(info.entityName)}Repository") """
out.println """public interface ${info.entityName}Repository extends ${repositoryClass}<${info.entityName}, ${idType}>, MiniRepository {  """
// 多主键时，需要自己实现主键查询和删除的方法
if (idColumnList != null && idColumnList.size() > 1) {
    // 生成多主键 findById(Id id1,Id id2...) 方法
    generateFindByIdMethod(T, info, idColumnList, out, idParameterAndType)

    // 生成多主键 existsById(Id id1,Id id2...) 方法
    generateExistsByIdMethod(T, out, idParameterAndType, idParameterNoType)

    // 生成多主键 deleteById(Id id1,Id id2...) 方法
    generateDeleteByIdMethod(T, info, idColumnList, out, idParameterAndType)
}
out.println """ """
out.println """} """







