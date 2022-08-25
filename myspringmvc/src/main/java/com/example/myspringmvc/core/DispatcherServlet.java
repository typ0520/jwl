package com.example.myspringmvc.core;

import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author tong
 */
public class DispatcherServlet extends HttpServlet {
    private final Map<Class<?>, Object> controllerMap = new HashMap<>();
    private final Map<String, Map<String, Method>> fixedMapping = new HashMap<>();
    private static final Map<Class<?>, Method> primitiveTypeResolverMap = new HashMap<>();

    static {
        try {
            primitiveTypeResolverMap.put(int.class, Integer.class.getMethod("parseInt", String.class));
            primitiveTypeResolverMap.put(boolean.class, Boolean.class.getMethod("parseBoolean", String.class));
            primitiveTypeResolverMap.put(long.class, Long.class.getMethod("parseLong", String.class));

            primitiveTypeResolverMap.put(Integer.class, Integer.class.getMethod("parseInt", String.class));
            primitiveTypeResolverMap.put(Boolean.class, Boolean.class.getMethod("parseBoolean", String.class));
            primitiveTypeResolverMap.put(Long.class, Long.class.getMethod("parseLong", String.class));
            primitiveTypeResolverMap.put(String.class, String.class.getMethod("valueOf", Object.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
        //扫描代码
        init("com.example.myspringmvc");
    }

    public void init(String rootPackage) {
        Set<Class<?>> classes = ClassUtil.getClasses(rootPackage);

        for (Class clazz : classes) {
            Controller controller = (Controller) clazz.getAnnotation(Controller.class);
            if (controller == null) {
                continue;
            }
            try {
                controllerMap.put(clazz, clazz.newInstance());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            for (Method method : clazz.getMethods()) {
                GetMapping getMapping = (GetMapping) method.getAnnotation(GetMapping.class);
                if (getMapping == null) {
                    continue;
                }
                for (String path : getMapping.value()) {
                    String joinedPath = joinPath(controller.value(), path);
                    Map<String, Method> methodMapping = fixedMapping.computeIfAbsent("GET", k -> new HashMap<>());
                    if (methodMapping.containsKey(joinedPath)) {
                        throw new RuntimeException("重复了");
                    }
                    methodMapping.put(joinedPath, method);
                }
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("GET".equals(req.getMethod())) {
            Map<String, Method> pathBindMethodMap = fixedMapping.get(req.getMethod());
            String path = req.getRequestURI();
            Method bindMethod = pathBindMethodMap.get(path);
            if (bindMethod != null) {
                Object instance = controllerMap.get(bindMethod.getClass());
                Object[] args = new Object[bindMethod.getParameterCount()];
                for (int i = 0, size = bindMethod.getParameterCount(); i < size; i++) {
                    Parameter parameter = bindMethod.getParameters()[i];
                    Class<?> mType = parameter.getType();
                    if (mType == HttpServletRequest.class) {
                        args[i] = req;
                    } else if (mType == HttpServletResponse.class) {
                        args[i] = req;
                    } else {
                        Method typeResolver = primitiveTypeResolverMap.get(mType);
                        if (typeResolver == null) {
                            throw new RuntimeException("unsupported type: " + mType.getName());
                        }
                        RequestParam anno = parameter.getAnnotation(RequestParam.class);
                        String queryStringName = null;
                        boolean required = true;
                        String defaultValue = "";
                        if (anno == null) {
                            queryStringName = parameter.getName();
                        } else {
                            queryStringName = anno.value();
                            required = anno.required();
                            defaultValue = anno.defaultValue();
                            if (queryStringName == null || queryStringName.trim().length() == 0) {
                                queryStringName = parameter.getName();
                            }
                        }
                        String value = req.getParameter(queryStringName);
                        if (value == null && required) {
                            resp.sendError(400, "参数异常");
                            return;
                        }
                        try {
                            Object parsedValue = value == null ? defaultValue : typeResolver.invoke(null, value);
                            args[i] = parsedValue;
                        } catch (Throwable e) {
                            resp.sendError(400, "参数异常");
                            return;
                        }
                        PrintWriter writer = resp.getWriter();
                        try {
                            Object ret = bindMethod.invoke(instance, args);
                            if (ret == null || primitiveTypeResolverMap.containsKey(ret.getClass())) {
                                writer.write(String.valueOf(ret));
                                writer.flush();
                            } else {
                                writer.write(new Gson().toJson(ret));
                                writer.flush();
                            }
                        } catch (Throwable e) {
                            resp.sendError(500, "出错了");
                            return;
                        }
                    }
                }
            }
        } else {
            //暂不实现
            super.service(req, resp);
        }
    }

    private String joinPath(String prefix, String suffix) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null && prefix.trim().length() > 0) {
            sb.append(prefix.trim());
            if (sb.charAt(0) != '/') {
                sb.insert(0, '/');
            }
        }
        if (suffix == null || suffix.trim().length() == 0) {
            throw new IllegalArgumentException("suffix不能为空");
        }
        suffix = suffix.trim();
        sb.append(suffix.startsWith("/") ? suffix : ("/" + suffix));
        if (!suffix.endsWith("/")) {
            sb.append(suffix);
        }
        return sb.toString();
    }
}
