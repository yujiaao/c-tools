package com.bixuebihui.util;

import com.bixuebihui.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;


public class PathTools {
    private static final Logger LOG = LoggerFactory.getLogger(PathTools.class);


    public static URL findFile(String fileName) {
        return Loader.getResource(fileName);
    }


    public static Resource loadResouceInClasspath(String fileName) {
        DefaultResourceLoader loader = new DefaultResourceLoader();

        Resource resource = loader.getResource(fileName);
        // If we have a non-null url, then delegate the rest of the
        // configuration to the OptionConverter.selectAndConfigure
        // method.
        if (resource != null) {
            URL url = null;
            try {
                url = resource.getURL();
            } catch (IOException e) {
               LOG.warn("", e);
            }
            LOG.debug("Using URL [" + url
                    + "] .");
        } else {
            LOG.debug("Could not find resource: [" + fileName
                    + "].");
        }
        return resource;
    }

    /**
     * 获取一个类的class文件所在的绝对路径。 这个类可以是JDK自身的类，也可以是用户自定义的类，或者是第三方开发包里的类。
     * 只要是在本程序中可以被加载的类，都可以定位到它的class文件的绝对路径。
     *
     * @param cls 一个对象的Class属性
     * @return 这个类的class文件位置的绝对路径。 如果没有这个类的定义，则返回null。
     */
    public static String getPathFromClass(Class cls) throws IOException {
        String path = null;
        if (cls == null) {
            throw new NullPointerException();
        }
        URL url = getClassLocationURL(cls);
        if (url != null) {
            path = url.getPath();
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                try {
                    path = new URL(path).getPath();
                } catch (MalformedURLException e) {
                    LOG.warn("", e);
                }
                int location = path.indexOf("!/");
                if (location != -1) {
                    path = path.substring(0, location);
                }
            }
            File file = new File(path);
            path = file.getCanonicalPath();
        }
        return path;
    }

    /**
     * 获取类的class文件位置的URL。这个方法是本类最基础的方法，供其它方法调用。
     */
    private static URL getClassLocationURL(final Class cls) {
        if (cls == null) {
            throw new IllegalArgumentException("null input: cls");
        }
        URL result = null;
        final String clsAsResource = cls.getName().replace('.', '/')
                .concat(".class");
        final ProtectionDomain pd = cls.getProtectionDomain();
        // java.lang.Class contract does not specify
        // if 'pd' can ever be null;
        // it is not the case for Sun's implementations,
        // but guard against null
        // just in case:
        if (pd != null) {
            final CodeSource cs = pd.getCodeSource();
            // 'cs' can be null depending on
            // the classloader behavior:
            if (cs != null) {
                result = cs.getLocation();
            }

            if (result != null) {
                // Convert a code source location into
                // a full class file location
                // for some common cases:
                if ("file".equals(result.getProtocol())) {
                    try {
                        if (result.toExternalForm().endsWith(".jar")
                                || result.toExternalForm().endsWith(".zip")) {
                            result = new URL("jar:"
                                    .concat(result.toExternalForm())
                                    .concat("!/").concat(clsAsResource));
                        } else if (new File(result.getFile()).isDirectory()) {
                            result = new URL(result, clsAsResource);
                        }
                    } catch (MalformedURLException ignore) {
                        LOG.warn("", ignore);
                    }
                }
            }
        }

        if (result == null) {
            // Try to find 'cls' definition as a resource;
            // this is not
            // document．d to be legal, but Sun's
            // implementations seem to //allow this:
            final ClassLoader clsLoader = cls.getClassLoader();
            result = clsLoader != null ? clsLoader.getResource(clsAsResource)
                    : ClassLoader.getSystemResource(clsAsResource);
        }
        return result;
    }

    /**
     * 这个方法可以通过与某个类的class文件的相对路径来获取文件或目录的绝对路径。 通常在程序中很难定位某个相对路径，特别是在B/S应用中。
     * 通过这个方法，我们可以根据我们程序自身的类文件的位置来定位某个相对路径。
     * 比如：某个txt文件相对于程序的Test类文件的路径是../../resource/test.txt，
     * 那么使用本方法Path.getFullPathRelateClass("../../resource/test.txt",Test.class)
     * 得到的结果是txt文件的在系统中的绝对路径。
     *
     * @param relatedPath 相对路径
     * @param cls         用来定位的类
     * @return 相对路径所对应的绝对路径
     * @throws IOException 因为本方法将查询文件系统，所以可能抛出IO异常
     */
    public static String getFullPathRelateClass(String relatedPath, Class cls)
            throws IOException {
        if (relatedPath == null) {
            throw new NullPointerException();
        }
        String clsPath = getPathFromClass(cls);
        File clsFile = new File(clsPath);
        String tempPath = clsFile.getParent() + File.separator + relatedPath;
        File file = new File(tempPath);
        return file.getCanonicalPath();
    }

    public static void main(String[] args) {
        try {
            System.out.println(getPathFromClass(PathTools.class));
            System.out.println(getFullPathRelateClass("../../..",
                    PathTools.class) + File.separator);
            System.out.println(getFullPathRelateClass(".", PathTools.class));
            System.out.println(new PathTools().getAppClassesPath());
        } catch (Exception e) {
            LOG.warn("", e);
        }
    }

    /** 返回给应用当前classes路径 */
    public String getAppClassesPath() {
            try {
                return getFullPathRelateClass("../../..",
                        PathTools.class) + File.separator;
            } catch (IOException e) {
               LOG.warn("Error when getAppClassesPath",e);
            }
            return null;
    }
}
