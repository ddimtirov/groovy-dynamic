package com.example

import java.util.regex.Pattern

public class IsolatingClassLoader extends URLClassLoader {
    private static int nextId = 0
    private final int id = nextId++

    Pattern pattern
    def loaded = [:] as Map<String, Class>

    public IsolatingClassLoader(URL[] classpath, ClassLoader parent, Pattern pattern = ~/.*/) {
        super(classpath, parent)
        this.pattern = pattern
    }

    @Override @SuppressWarnings("GroovyUnusedCatchParameter")
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name ==~ ~/java\..*/) {
            return getSystemClassLoader().loadClass(name, resolve)
        }
        if (name in loaded) return loaded[name]
        if (!(name ==~ pattern)) return super.loadClass(name, resolve)

        Class<?> c
        try {
            c = findClass(name)
        } catch (ClassNotFoundException e) {
            c = super.loadClass(name, resolve)
        }
        if (resolve) resolveClass(c)
        loaded[name] = c
        return c
    }

    protected Class<?> findClass(String name) {
        String path = name.replace('.', '/').concat(".class")
        def bytes = getResource(path)?.bytes
        if (!bytes) throw new ClassNotFoundException(name);

        try {
            return defineClass(name, bytes, 0, bytes.length)
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e)
        }

    }


    @Override
    public URL getResource(String name) { return findResource(name) ?: super.getResource(name) }

    @Override
    public Enumeration<URL> getResources(String name) {
        def childUrls = findResources(name).toList()
        def parentUrls = parent.getResources(name).toList()
        return (childUrls + parentUrls) as Enumeration }

    @Override @SuppressWarnings("GroovyUnusedCatchParameter")
    public InputStream getResourceAsStream(String name) {
        try {
            return getResource(name)?.openStream()
        } catch (IOException e) {
            return null
        }
    }

    String toString() { "ICL-$id" }
}
