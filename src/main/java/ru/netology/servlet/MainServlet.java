package ru.netology.servlet;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;
    static final String GET = "GET";
    static final String POST = "POST";
    static final String DELETE = "DELETE";

    @Override
    public void init() {

        final var factory = new DefaultListableBeanFactory();
        final var reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions("beans.xml");

        // получаем по имени бина
        controller = (PostController) factory.getBean("postController");

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого

        final var requestWithoutParameters = "/api/posts";
        final var requestWithParameters = "/api/posts/\\d+";

        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            // primitive routing
            if (method.equals(GET)) {
                if (path.equals(requestWithoutParameters)) {
                    controller.all(resp);
                    return;
                }
                if (path.matches(requestWithParameters)) {
                    // easy way
                    final var id = getIdFromPath(path);
                    controller.getById(id, resp);
                    return;
                }
            }
            if (method.equals(POST) && path.equals(requestWithoutParameters)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals(DELETE) && path.matches(requestWithParameters)) {
                // easy way
                final var id = getIdFromPath(path);
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected long getIdFromPath(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}

