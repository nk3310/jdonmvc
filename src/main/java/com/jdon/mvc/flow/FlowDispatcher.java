package com.jdon.mvc.flow;

import com.jdon.mvc.Constant;
import com.jdon.mvc.core.*;
import com.jdon.mvc.engine.DefaultFlowContext;
import com.jdon.mvc.represent.RepresentationRenderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;

/**
 * 流式分派
 *
 * @author oojdon
 */

public class FlowDispatcher implements Dispatcher {

    private final Log LOG = LogFactory.getLog(FlowDispatcher.class);

    private FrameWorkContext fc;

    public FlowDispatcher(FrameWorkContext fc) {
        this.fc = fc;
    }

    @Override
    public FrameWorkContext getFc() {
        return fc;
    }

    public void dispatch() throws ServletException {

        FlowContext ic = new DefaultFlowContext(fc);

        LOG.info(">>>Begin dispatch the request to JdonMVC's process flow");

        try {
            new RequestResponseFlow().begin(ic);
        } catch (ActionException e) {
            if (fc.getExceptionResolver() != null) {
                try {
                    fc.getExceptionResolver().resolveException(Env.req(), Env.res(), ic.flashMap().get(Constant.RESOURCE_INSTANCE), e).render(fc);
                } catch (RepresentationRenderException rrx) {
                    throw new ServletException(rrx);
                }
            }
        }

        LOG.info("<<<Finish process the request via JdonMVC's process flow");
    }

    public void destroy() {
        LOG.info("Destroy dispatcher,Destroy the framework context");
        fc = null;
    }
}