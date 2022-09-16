package com.ttw.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class WebFilter implements Filter {
//    (ResourceLoader resourceLoader)
    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public void doFilter(
            ServletRequest sreq,
            ServletResponse sres,
            FilterChain fc) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) sreq;
        String[] reservedPath = {
                "/api",
                "/assets",
                "/web"
        };
//        System.out.println("req -> "+req.getRequestURI());
//        chain.doFilter(request, response);
        if (checkReq(reservedPath, request)) {
            fc.doFilter(sreq, sres);
        } else {
            webAssetRedirect((HttpServletRequest) sreq, (HttpServletResponse) sres);
        }
    }

    private boolean checkReq(String[] arr, HttpServletRequest request){
        for (int i = 0; i< arr.length; i++){
            if(reqStartWith(arr[i], request)){
                return true;
            }
        }
        return false;
    }

    private void webAssetRedirect(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String fileName = reqPathNoBaseContext(req);
        Resource resource = resourceLoader.getResource("file:web/"+fileName);
        if (StringUtils.isNotEmpty(fileName) && resource.exists() && resource.isFile()) {
            res.sendRedirect("web/"+fileName);
        } else {
            res.setContentType(MediaType.TEXT_HTML_VALUE);
            InputStream ist = resourceLoader.getResource("file:web/index.html").getInputStream();
            byte[] indexHtml = ist.readAllBytes();
            res.getOutputStream().write(indexHtml);
            res.getOutputStream().flush();
            res.getOutputStream().close();
            ist.close();
        }

    }

    private String reqPathNoBaseContext(HttpServletRequest req){
        String[] arr = req.getRequestURL().toString().split("/");
        String name = "";

        for (int i = 0; i < arr.length; i++){
            if(i > 2){
                name += arr[i]+"/";
            }
        }
        if(StringUtils.isNotEmpty(name)){
            name = name.substring(0, name.length() - 1);
        }

        return name;
    }

    private boolean reqStartWith(String path, HttpServletRequest req) {
        String currentPath = "/"+reqPathNoBaseContext(req);
        return currentPath.toLowerCase().startsWith(path.toLowerCase());
    }
}