package com.zht.genericproject.util;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 输入数据check
 */
public class InputCheck {
    /**
     * 检查name是否是中文名
     *
     * @param name   姓名
     * @return true - 是中文名;false - 不是中文名
     */
    public static boolean checkRealName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        } else if (checkChinese(name) && name.length() >= 2 && name.length() <= 6) {
            return true;
        }
        return false;
    }

    /**
     * 检测String是否全是中文
     * @param name   待检测string
     * @return true - 是中文;
     * false - 不是中文
     */
    public static boolean checkChinese(String name) {
        name = new String(name.getBytes());//用GBK编码
        String pattern = "[\u4e00-\u9fa5]+";
        Pattern p       = Pattern.compile(pattern);
        Matcher result  = p.matcher(name);
        return result.matches(); //是否含有中文字符
    }

    /**
     * 判定输入汉字
     * @param c   待检测字符
     * @return true - 是中文字符;
     * false - 不是中文字符
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 电信
		 * 中国电信手机号码开头数字
		 * 2G/3G号段（CDMA2000网络）133、153、180、181、189
		 * 4G号段 177
		 *
		 * 联通
		 * 中国联通手机号码开头数字
		 * 2G号段（GSM网络）130、131、132、155、156
		 * 3G上网卡145
		 * 3G号段（WCDMA网络）185、186
		 * 4G号段 176、185[1]
		 *
		 * 移动
		 * 中国移动手机号码开头数字
		 * 2G号段（GSM网络）有134x（0-8）、135、136、137、138、139、150、151、152、158、159、182、183、184。
		 * 3G号段（TD-SCDMA网络）有157、187、188
		 * 3G上网卡 147
		 * 4G号段 178
		 *
		 * 补充:
		 * 14号段以前为上网卡专属号段，如中国联通的是145，中国移动的是147等等。
		 * 170号段为虚拟运营商专属号段，170号段的 11 位手机号前四位来区分基础运营商，其中 “1700” 为中国电信的转售号码标识，“1705” 为中国移动，“1709” 为中国联通。
		 */
        // Pattern p = Pattern.compile("^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$");
        //新增171号段手机号码注册
        Pattern regex   = Pattern.compile("^((1[358][0-9])|(14[57])|(17[013678]))\\d{8}$");
        Matcher matcher = regex.matcher(mobiles);
        return matcher.matches();
    }

    /**
     * 验证是否是邮箱地址
     * @param email   待检测email
     * @return true - 是email;
     * false - 不是email
     */
    public static boolean isEmail(String email) {
        Pattern regex   = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher matcher = regex.matcher(email);// 获取邮箱然后匹对是否正确
        return matcher.matches();
    }

    /**
     * 验证密码规则，是否由8-16为数字+字母组成
     * @param pwd      待检测密码
     * @return true - 符合密码规则;
     * false - 不符合密码规则
     */
    public static boolean checkPwdRule(String pwd) {
        if (pwd != null){
            Pattern regex   = Pattern.compile("^(?![^a-zA-Z]+$)(?!\\D+$).{8,16}$");
            Matcher matcher = regex.matcher(pwd);
            return matcher.matches();
        }
        else return  false;
    }

    /**
     * 验证用户名规则，是否由4-16为数字+字母组成
     * @param name    待检测用户名
     * @return boolean
     * true - 符合用户名规则;
     * false - 不符合用户名规则
     */
    public static boolean checkUsernameRule(String name) {
        Pattern regex   = Pattern.compile("^(?![^a-zA-Z]+$)(?!\\D+$).{4,16}$");
        Matcher matcher = regex.matcher(name);
        return matcher.matches();
    }

    /**
     * 检验验证码是否符合规则
     * @param code      待检测验证码
     * @return true - 符合验证码规则；
     * false - 不符合验证码规则
     */
    public static boolean checkCode(String code) {
        Pattern regex   = Pattern.compile("^\\d{6}$");
        Matcher matcher = regex.matcher(code);
        return matcher.matches();
    }

    /**
     * 校验身份证是否符合规则
     * @param card
     * @return
     */
    public static boolean checkCard(String card) {
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9xX])");
        Matcher idNumMatcher = idNumPattern.matcher(card);
        return idNumMatcher.matches(); //是否含有中文字符
    }

    /**
     * 校验身份证后6位是否符合规范
     * @param cardAfter6
     * @return
     */
    public static boolean checkCardAfter6(String cardAfter6) {
        Pattern idNumPattern = Pattern.compile("(\\d{6}[0-9xX])");
        Matcher idNumMatcher = idNumPattern.matcher(cardAfter6);
        return idNumMatcher.matches();
    }

    /**
     * 校验输入的字符是否为数字
     * @param input
     * @return
     */
    public static boolean checkIsNumber(CharSequence input) {
        Pattern idNumPattern = Pattern.compile("[0-9]");
        Matcher idNumMatcher = idNumPattern.matcher(input);
        return idNumMatcher.matches();
    }

    /**
     * 校验字符是否是数字 x X
     * @param input
     * @return
     */
    public static boolean checkIsNumberOrX(CharSequence input) {
        Pattern idNumPattern = Pattern.compile("[0-9xX]");
        Matcher idNumMatcher = idNumPattern.matcher(input);
        return idNumMatcher.matches();
    }

    /**
     * 验证密码规则，是否由8-16为数字+字母组成
     * @param pwd     待检测密码
     * @return true - 符合密码规则;
     * false - 不符合密码规则
     */
    public static boolean checkPayPwd(String pwd) {
        Pattern regex   = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$");
        Matcher matcher = regex.matcher(pwd);
        return matcher.matches();
    }

    /**
     * 匹配所有键盘上可见的非(字母和数字)的符号
     */
    public static boolean checkIllagleCharacter(String input) {
        Pattern regex   = Pattern.compile("((?=[\\x21-\\x7e]+)[^A-Za-z0-9])");
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }
    /**
     * 需监听的editText list 中是否全非空
     * @return
     */
    public static boolean edListCheck(LinkedList<EditText> edList) {
        for (EditText editText : edList) {
            if (TextUtils.isEmpty(editText.getText())) {
                return false;
            }
        }
        return true;
    }
}
