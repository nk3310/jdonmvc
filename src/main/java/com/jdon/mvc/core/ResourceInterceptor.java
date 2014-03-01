package com.jdon.mvc.core;

import com.jdon.mvc.represent.Represent;
import com.jdon.mvc.rs.java.Handler;

import java.util.regex.Pattern;

/**
 * 资源拦截器
 * 可以用来在不行资源处理前和处理后做些事情
 * <p/>
 * User: Asion
 * Date: 13-6-9
 * Time: 上午10:03
 */
public abstract class ResourceInterceptor {

    /**
     * 执行前,通过返回false来终止流程
     *
     * @param handler
     * @return
     */
    public abstract boolean pre(Handler handler);

    /**
     * 执行后
     *
     * @param handler
     * @param represent
     */
    public abstract void post(Handler handler, Represent represent);


    /**
     * 拦截路径，默认是全部，子类可覆盖
     *
     * @return
     */
    public Pattern getUrlMatchPattern() {
        return Pattern.compile(".*");
    }


}
