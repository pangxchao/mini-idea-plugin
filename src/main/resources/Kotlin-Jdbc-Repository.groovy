import com.mini.plugin.extension.StringKt
import com.mini.plugin.state.DbColumn
import com.mini.plugin.state.DbTable
import com.mini.plugin.util.BuilderWriter

import java.util.stream.Collectors

import static com.mini.plugin.util.Constants.T

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
String idType = idColumnList.size() > 1 ? "${info.entityName}Id" :
        idColumnList.stream().findAny().map {
            it.javaType
        }.orElse("String")


// 生成多主键 findAllById(Iterable<ID> idList 方法
@SuppressWarnings('DuplicatedCode')
static def generateFindAllByIdMethod(String placeholder, DbTable info, Collection<DbColumn> columnList, BuilderWriter out, String idType) {
    out.println """${placeholder} """
    out.println """${placeholder}@Nonnull """
    out.println """${placeholder}@Override """
    out.println """${placeholder}default Iterable<${info.entityName}> findAllById(@Nonnull Iterable<${idType}> idList) { """
    out.println """${placeholder}${T}return this.select(${info.entityName}.class, selectSql -> { """
    out.println """${placeholder}${T}${T}for (final ${idType} id : idList) { """
    out.println """${placeholder}${T}${T}${T}selectSql.or().where(w -> { """
    columnList.forEach { column ->
        String columnName = column.getColumnName().toUpperCase()
        String name = StringKt.firstUpperCase(column.getFieldName())
        out.println """${placeholder}${T}${T}${T}${T}w.whereEq(${columnName}, id.get${name}()); """
    }
    out.println """${placeholder}${T}${T}${T}}); """
    out.println """${placeholder}${T}${T}} """
    out.println """${placeholder}${T}}); """
    out.println """${placeholder}} """
}

// 生成多主键 findById(ID id) 方法
@SuppressWarnings('DuplicatedCode')
static def generateFindByIdSMethod(String placeholder, DbTable info, Collection<DbColumn> columnList, BuilderWriter out, String idType) {
    out.println """${placeholder} """
    out.println """${placeholder}@Nonnull """
    out.println """${placeholder}@Override """
    out.println """${placeholder}default Optional<${info.entityName}> findById(@Nonnull ${idType} id) { """
    out.println """${placeholder}${T}return ofNullable(selectOne(UserRole.class, it->{ """
    columnList.forEach { column ->
        String columnName = column.getColumnName().toUpperCase()
        String name = StringKt.firstUpperCase(column.getFieldName())
        out.println """${placeholder}${T}${T}it.whereEq(${columnName}, id.get${name}()); """
    }
    out.println """${placeholder}${T}})); """
    out.println """${placeholder}} """
}

// 生成多主键 findById(Id id1,Id id2...) 方法
@SuppressWarnings('DuplicatedCode')
static def generateFindByIdMMethod(String placeholder, DbTable info, Collection<DbColumn> columnList, BuilderWriter out, String idParameterAndType) {
    out.println """${placeholder} """
    out.println """${placeholder}@Nonnull """
    out.println """${placeholder}default Optional<${info.entityName}> findById(${idParameterAndType}) { """
    out.println """${placeholder}${T}return ofNullable(selectOne(UserRole.class, it->{ """
    columnList.forEach { column ->
        String columnName = column.getColumnName().toUpperCase()
        out.println """${placeholder}${T}${T}it.whereEq(${columnName}, ${column.getFieldName()}); """
    }
    out.println """${placeholder}${T}})); """
    out.println """${placeholder}} """
}

// 生成多主键 existsById(ID id) 方法
@SuppressWarnings('DuplicatedCode')
static def generateExistsByIdSMethod(String placeholder, BuilderWriter out, String idType) {
    out.println """${placeholder} """
    out.println """${placeholder}@Override """
    out.println """${placeholder}default boolean existsById(@Nonnull ${idType} id) { """
    out.println """${placeholder}${T}return findById(id).isPresent(); """
    out.println """${placeholder}} """
}

// 生成多主键 existsById(Id id1,Id id2...) 方法
@SuppressWarnings('DuplicatedCode')
static def generateExistsByIdMMethod(String placeholder, BuilderWriter out, String idParameterAndType, String idParameterNoType) {
    out.println """${placeholder} """
    out.println """${placeholder}@Override """
    out.println """${placeholder}default boolean existsById(${idParameterAndType}) { """
    out.println """${placeholder}${T}return findById(${idParameterNoType}).isPresent(); """
    out.println """${placeholder}} """
}

// 生成多主键 deleteById(ID id) 方法
@SuppressWarnings('DuplicatedCode')
static def generateDeleteByIdSMethod(String placeholder, DbTable info, Collection<DbColumn> columnList, BuilderWriter out, String idType) {
    out.println """${placeholder} """
    out.println """${placeholder}@Override """
    out.println """${placeholder}default void deleteById(@Nonnull ${idType} id) { """
    out.println """${placeholder}${T} this.delete(${info.tableName.toUpperCase()}, it->{"""
    columnList.forEach { column ->
        String columnName = column.getColumnName().toUpperCase()
        String name = StringKt.firstUpperCase(column.getFieldName())
        out.println """${placeholder}${T}${T}it.whereEq(${columnName}, id.get${name}()); """
    }
    out.println """${placeholder}${T}}); """
    out.println """${placeholder}} """
}

// 生成多主键 deleteById(Id id1,Id id2...) 方法
@SuppressWarnings('DuplicatedCode')
static def generateDeleteByIdMMethod(String placeholder, DbTable info, Collection<DbColumn> columnList, BuilderWriter out, String idParameterAndType) {
    out.println """${placeholder} """
    out.println """${placeholder}@Override """
    out.println """${placeholder}default void deleteById(${idParameterAndType}) { """
    out.println """${placeholder}${T}this.delete(${info.tableName.toUpperCase()}, it->{ """
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
out.setFileName("${info.entityName}Repository.kt")

out.println """@file:Suppress("unused") """
out.println """ """
out.println """package ${info.packageName}.repository """

// 导入包
out.println """ """
out.println """import ${info.packageName}.entity.* """
out.println """import com.mini.core.jdbc.MiniJdbcRepository """
out.println """import org.springframework.data.repository.PagingAndSortingRepository """
out.println """import org.springframework.stereotype.Repository """
out.println """ """
out.println """import javax.annotation.* """
out.println """import java.util.*"""
out.println """ """
out.println """import  java.util.Optional.ofNullable """
out.println """ """

out.println """interface ${info.entityName}Repository : PagingAndSortingRepository<${info.entityName}, ${idType}>, MiniJdbcRepository {  """
out.println """ """
out.println """} """



