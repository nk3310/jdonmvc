package com.jdon.mvc.represent;

import com.jdon.mvc.core.Env;
import com.jdon.mvc.core.FrameWorkContext;

import java.io.IOException;

/**
 * Author:oojdon
 */
public class Redirect implements Represent {

    private String url;

    public Redirect(String url) {
        this.url = url;
    }

    @Override
    public void render(FrameWorkContext fc) throws RepresentationRenderException {

        try {
            Env.res().sendRedirect(this.url);
        } catch (IOException e) {
            throw new RepresentationRenderException(
                    "can't transfer the resource", e);
        }
    }
}
