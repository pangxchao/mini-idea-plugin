@file:JvmName("StringKt")
@file:Suppress("unused")

package com.mini.plugin.extension

import java.lang.Character.isUpperCase
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

// *: 0 次或者多次, +: 一次或者多次, ?: 0次或者1次, {n}:刚刚n次, {n,m}: n到m次
// \d: [0-9], \D: [^0-9], \w:[a-zA-Z_0-9], \W:[^a-zA-Z_0-9], \s: [\t\n\r\f], \S: [^\t\n\r\f]
val PHONE = Regex("((010|02[0-9]|0[3-9]{2,3})[-])?[0-9]{6,8}")
val ID_CARD = Regex("\\d{15}(\\d{2}[A-Za-z0-9])?")
val EMAIL = Regex("\\S+[@]\\S+[.]\\S+")
val CHINESE = Regex("[\u4E00-\u9FA5]+")

val REQUIRE = Regex("[a-z_][a-z0-9_]*")
val MOBILE = Regex("1\\d{10}")
val NUMBER = Regex("\\d+")
val LETTER = Regex("\\w+")

/**
 * 将日期格式的字符串转化成日期
 * @param format 字符串格式
 * @return 日期
 */
fun String.toDate(format: String = DATE_TIME): Date {
    return Format(format).parse(this)
}

/**
 * 将字符串转换成URL
 * @return 转换结果
 */
fun String.toURL(): URL {
    return URL(this)
}

/**
 * 将字符串转换成URI
 * @return URI
 */
fun String.toURI(): URI {
    return URI(this)
}

/**
 * 验证字符串与正则表达式是否匹配
 * @param pattern  正则表达式
 * @return true-是
 */
fun String.matches(pattern: String): Boolean {
    return matches(Regex(pattern))
}

/**
 * 验证传入字符串是否为邮箱格式
 * @return true-是
 */
fun String.isEmail(): Boolean {
    return matches(EMAIL)
}

/**
 * 验证传入字符串是否为电话号码
 * @return true-是
 */
fun String.isPhone(): Boolean {
    return matches(PHONE)
}

/**
 * 验证传入字符串是否为手机号
 * @return true-是
 */
fun String.isMobile(): Boolean {
    return matches(MOBILE)
}

/**
 * 验证传入字符串是否为手机号或者电话号码
 * @return true-是
 */
fun String.isMobilePhone(): Boolean {
    return matches(MOBILE) || matches(PHONE)
}

/**
 * 验证传入字符串是否为纯英文字母
 * @return true-是
 */
fun String.isLetter(): Boolean {
    return matches(LETTER)
}

/**
 * 验证传入字符串是否为纯数字
 * @return true-是
 */
fun String.isNumber(): Boolean {
    return matches(NUMBER)
}

/**
 * 验证传入字符串是否为纯中文
 * @return true-是
 */
fun String.isChinese(): Boolean {
    return matches(CHINESE)
}

/**
 * 验证传入字符串是否为身份证号码
 * @return true-是
 */
fun String.isIdCard(): Boolean {
    return matches(ID_CARD)
}

/**
 * 验证传入字符串是否为字母、数字和下划线组成，数字不能开头
 * @return true-是
 */
fun String.isRequire(): Boolean {
    return matches(REQUIRE)
}


/**
 * 将字符串转成URL编码
 * @param charset 编码方式
 */
fun String.urlEncode(charset: String = "UTF-8"): String {
    return URLEncoder.encode(this, charset)
}

/**
 * - 将字符串转成URL编码
 * @param charset 编码方式
 */
fun String.urlEncode(charset: Charset = UTF_8): String {
    return URLEncoder.encode(this, charset.name())
}

/**
 * - 将URL编码还原成原来的字符串
 * @param charset 编码
 */
fun String.urlDecode(charset: String = "UTF-8"): String {
    return URLDecoder.decode(this, charset)
}

/**
 * - 将URL编码还原成原来的字符串
 * @param charset 编码
 */
fun String.urlDecode(charset: Charset = UTF_8): String {
    return URLDecoder.decode(this, charset.name())
}

/**
 * 把传入字符串首字母大写
 * @return 如：First
 */
fun String.firstUpperCase(): String = firstOrNull()?.let {
    this.replaceFirst(it, it.toUpperCase(), false)
} ?: ""

/**
 * 把传入字符串首字母小写
 * @return 如: first
 */
fun String.firstLowerCase() = firstOrNull()?.let {
    this.replaceFirst(it, it.toLowerCase(), false)
} ?: ""

/**
 * 把字符串改写成java的驼峰命名法
 * @return 驼峰命名字符串
 */
fun String.toJavaName(firstUpperCase: Boolean = false) = split("_").joinToString("") {
    return@joinToString it.firstUpperCase()
}.let { if (firstUpperCase) it else it.firstLowerCase() }

/**
 * 把用java驼峰命名法的字符串字符串转换成 用"_"连接的数据库命名
 * @return 返回字符串都是小写字母
 */
fun String.toDBName() = toCharArray().joinToString {
    if (isUpperCase(it)) "_$it" else "$it"
}

fun String.toFieldName(prefix: String): String {
    val regex = Regex("(($prefix)(_)*)")
    return replaceFirst(regex, "")
}
