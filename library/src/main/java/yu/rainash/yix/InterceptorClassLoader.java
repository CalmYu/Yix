package yu.rainash.yix;

import dalvik.system.PathClassLoader;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class InterceptorClassLoader extends PathClassLoader {

    private ClassLoader mCurrent;

    public InterceptorClassLoader(String dexPath, String librarySearchPath, ClassLoader parent, ClassLoader current) {
        super(dexPath, librarySearchPath, parent);
        this.mCurrent = current;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clz = null;

        try {
            clz = findClass(name);
        } catch (ClassNotFoundException e) {
            // do nothing
        }

        if (clz == null) {
            try {
                clz = super.loadClass(name);
            } catch (ClassNotFoundException e) {
                // do nothing
            }
        }

        if (clz == null) {
            clz = mCurrent.loadClass(name);
        }

        if (clz == null) {
            throw new ClassNotFoundException();
        }

        return clz;

    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

}
