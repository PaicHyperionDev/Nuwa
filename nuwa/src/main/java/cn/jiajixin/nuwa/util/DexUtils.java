package cn.jiajixin.nuwa.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by jixin.jia on 15/10/31.
 */
public class DexUtils {

    public static void injectDexAtFirst(Context ctx,String dexPath, String defaultDexOptPath) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        if (hasLexClassLoader()) {
            injectInAliyunOs(ctx, dexPath);
        }if (hasDexClassLoader()) {
            injectDexAtFirst14(dexPath, defaultDexOptPath);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            injectDexAtFirst10(dexPath, defaultDexOptPath);
        }
    }

    private static boolean hasLexClassLoader() {
        try {
            Class.forName("dalvik.system.LexClassLoader");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean hasDexClassLoader() {
        try {
            Class.forName("dalvik.system.BaseDexClassLoader");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static void injectInAliyunOs(Context ctx,String dexPath)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException, NoSuchFieldException {
        PathClassLoader obj = getPathClassLoader();
        String replaceAll = new File(dexPath).getName().replaceAll("\\.[a-zA-Z0-9]+", ".lex");
        Class cls = Class.forName("dalvik.system.LexClassLoader");
        Object newInstance =
                cls.getConstructor(new Class[] {String.class, String.class, String.class, ClassLoader.class}).newInstance(
                        new Object[] {ctx.getDir("dex", 0).getAbsolutePath() + File.separator + replaceAll,
                                ctx.getDir("dex", 0).getAbsolutePath(), dexPath, obj});
        cls.getMethod("loadClass", new Class[] {String.class}).invoke(newInstance, new Object[] {"cn.jiajixin.nuwa.Hack"});
        ReflectionUtils.setField(obj, PathClassLoader.class, "mPaths",
                appendArray(ReflectionUtils.getField(obj, PathClassLoader.class, "mPaths"), ReflectionUtils.getField(newInstance, cls, "mRawDexPath")));
        ReflectionUtils.setField(obj, PathClassLoader.class, "mFiles",
                combineArray(ReflectionUtils.getField(newInstance, cls, "mFiles"),ReflectionUtils.getField(obj, PathClassLoader.class, "mFiles")));
        ReflectionUtils.setField(obj, PathClassLoader.class, "mZips",
                combineArray(ReflectionUtils.getField(newInstance, cls, "mZips"),ReflectionUtils.getField(obj, PathClassLoader.class, "mZips")));
        ReflectionUtils.setField(obj, PathClassLoader.class, "mLexs",
                combineArray(ReflectionUtils.getField(newInstance, cls, "mDexs"),ReflectionUtils.getField(obj, PathClassLoader.class, "mLexs")));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void injectDexAtFirst14(String dexPath, String defaultDexOptPath) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, defaultDexOptPath, dexPath, getPathClassLoader());
        Object baseDexElements = getDexElements(getPathList(getPathClassLoader()));
        Object newDexElements = getDexElements(getPathList(dexClassLoader));
        Object allDexElements = combineArray(newDexElements, baseDexElements);
        Object pathList = getPathList(getPathClassLoader());
        ReflectionUtils.setField(pathList, pathList.getClass(), "dexElements", allDexElements);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static void injectDexAtFirst10(String dexPath, String defaultDexOptPath) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        PathClassLoader obj = getPathClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, defaultDexOptPath, dexPath, getPathClassLoader());
        dexClassLoader.loadClass("cn.jiajixin.nuwa.Hack");
        ReflectionUtils.setField(obj, PathClassLoader.class, "mPaths",appendArray(ReflectionUtils.getField(obj, PathClassLoader.class, "mPaths"),
                        ReflectionUtils.getField(dexClassLoader, DexClassLoader.class,
                        "mRawDexPath")));
        ReflectionUtils.setField(obj, PathClassLoader.class, "mFiles",combineArray(ReflectionUtils.getField(dexClassLoader, DexClassLoader.class,
                        "mFiles"),ReflectionUtils.getField(obj, PathClassLoader.class, "mFiles")));
        ReflectionUtils.setField(obj, PathClassLoader.class, "mZips", combineArray(ReflectionUtils.getField(dexClassLoader, DexClassLoader.class,
                        "mZips"),ReflectionUtils.getField(obj, PathClassLoader.class, "mZips")));
        ReflectionUtils.setField(obj, PathClassLoader.class, "mDexs", combineArray(ReflectionUtils.getField(dexClassLoader, DexClassLoader.class,
                        "mDexs"),ReflectionUtils.getField(obj, PathClassLoader.class, "mDexs")));
    }

    private static PathClassLoader getPathClassLoader() {
        return  (PathClassLoader) DexUtils.class.getClassLoader();
    }

    private static Object getDexElements(Object paramObject)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        return ReflectionUtils.getField(paramObject, paramObject.getClass(), "dexElements");
    }

    private static Object getPathList(Object baseDexClassLoader)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        return ReflectionUtils.getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static Object combineArray(Object firstArray, Object secondArray) {
        Class<?> localClass = firstArray.getClass().getComponentType();
        int firstArrayLength = Array.getLength(firstArray);
        int allLength = firstArrayLength + Array.getLength(secondArray);
        Object result = Array.newInstance(localClass, allLength);
        for (int k = 0; k < allLength; ++k) {
            if (k < firstArrayLength) {
                Array.set(result, k, Array.get(firstArray, k));
            } else {
                Array.set(result, k, Array.get(secondArray, k - firstArrayLength));
            }
        }
        return result;
    }

    private static Object appendArray(Object obj, Object obj2) {
        Class componentType = obj.getClass().getComponentType();
        int length = Array.getLength(obj);
        Object newInstance = Array.newInstance(componentType, length + 1);
        Array.set(newInstance, 0, obj2);
        for (int i = 1; i < length + 1; i++) {
            Array.set(newInstance, i, Array.get(obj, i - 1));
        }
        return newInstance;
    }
}
