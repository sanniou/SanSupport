package com.sanniou.support.helper;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 辅助EditText操作的帮助类
 */
public class EditTextHelper {

    /**
     * 禁止EditText输入空格
     */
    public static void setEditTextNoSpace(TextView editText) {
        setFilter(editText, new SpaceInputFilter());
    }

    public static void setEditTextNoSpace(TextView... editText) {
        for (TextView text : editText) {
            setFilter(text, new SpaceInputFilter());
        }
    }

    /**
     * 字符转小写
     */
    public static void setEditTextToLower(TextView editText) {
        setFilter(editText, new InputFilter.AllCaps());
    }

    /**
     * 控制EditText长度
     */
    public static void setEditTextMaxLength(TextView editText, int max) {
        setFilter(editText, new InputFilter.LengthFilter(max));
    }

    /**
     * 禁止EditText输入特殊字符
     */
    public static void setEditTextNoSpeChat(TextView editText) {
        setFilter(editText, new SpeCharInputFilter());
    }

    private static void setFilter(TextView editText, InputFilter filter) {
        List<InputFilter> filters1 = new ArrayList<>();
        Collections.addAll(filters1, editText.getFilters());
        filters1.add(filter);
        editText.setFilters(filters1.toArray(new InputFilter[filters1.size()]));
    }

    /*************************  *************************/
    private static class SpaceInputFilter implements InputFilter {

        /**
         * @param source 新输入的字符串
         * @param start  新输入的字符串起始下标，一般为0
         * @param end    新输入的字符串终点下标，一般为source长度-1
         * @param dest   输入之前文本框内容
         * @param dstart 原内容起始坐标，一般为0
         * @param dend   原内容终点坐标，一般为dest长度-1
         * @return 验证后的字符串 ,返回null时等于原始字符串
         */
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {
            String s = source.toString();
            if (s.contains(" ")) {
                return s.replace(" ", "");
            }
            return null;
        }
    }

    /*************************  *************************/
    private static class SpeCharInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {
            String speChat = "[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？]";
            Pattern pattern = Pattern.compile(speChat);
            Matcher matcher = pattern.matcher(source.toString());
            if (matcher.find()) {
                return "";
            } else {
                return null;
            }
        }
    }
}
