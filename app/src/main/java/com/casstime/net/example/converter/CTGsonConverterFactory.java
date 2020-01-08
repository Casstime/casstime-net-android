package com.casstime.net.example.converter;

import android.text.TextUtils;
import android.util.Log;

import com.casstime.net.example.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author maiwenchang
 * <p>
 * 自定义数据转化工厂类，检验数据实体是否实现Serializable 或 ICECBaseBean
 */
public class CTGsonConverterFactory extends Converter.Factory {

    private static final String TAG = CTGsonConverterFactory.class.getSimpleName();

    private final Map<String, Object> typeTokenCache = new ConcurrentHashMap<>();

    private static final String SKIP_NOTE = " skip verify Interface";

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static CTGsonConverterFactory create() {
        return create(new Gson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static CTGsonConverterFactory create(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        return new CTGsonConverterFactory(gson);
    }

    private final Gson gson;

    private CTGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type t, Annotation[] annotations, Retrofit retrofit) {
        TypeToken<?> type = TypeToken.get(t);
        verifyInterface(type, null);
        TypeAdapter<?> adapter = gson.getAdapter(type);
        return new CTGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new CTGsonRequestBodyConverter<>(gson, adapter);
    }

    /**
     * 检查该类及其成员变量、泛型参数类型是否实现 Serializable 或 ICECBaseBean 接口
     *
     * @param typeToken
     * @param name
     */
    private void verifyInterface(TypeToken<?> typeToken, String name) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (typeToken == null) {
            return;
        }
        Type type = typeToken.getType();
        Class<?> rawType = typeToken.getRawType();

        //处理带泛型的类
        if (type instanceof ParameterizedType) {
            Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
            verifyTypeArguments(arguments);
        }

        if (type == null) {
            return;
        }
        if (rawType == null) {
            return;
        }

        //忽略基本数据类型及其封装类、String类型
        if (isBaseType(rawType)) {
            return;
        }

        //忽略java类
        if (isJavaClass(rawType)) {
            return;
        }

        //跳过@Since并@Until 的类
        final boolean skipSerialize = Excluder.DEFAULT.excludeClass(rawType, true);
        final boolean skipDeserialize = Excluder.DEFAULT.excludeClass(rawType, false);
        if (skipSerialize || skipDeserialize) {
            Log.d(TAG, rawType.getName() + SKIP_NOTE);
            return;
        }

        //跳过检查过的类
        Object cached = typeTokenCache.get(rawType.getName());
        if (cached != null) {
//            Log.d(TAG, typeToken.getRawType().getName() + SKIP_NOTE);
            return;
        }

        //检查自己的接口实现
        verifySelfInterface(typeToken, name);

        //处理成员变量
        verifyFieldInterface(typeToken);
    }

    /**
     * 检查成员变量是否实现了 Serializable 或 ICECBaseBean 接口
     */
    private void verifyFieldInterface(TypeToken<?> typeToken) {
        Class<?> raw = typeToken.getRawType();
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            for (Field field : fields) {
                boolean serialize = Excluder.DEFAULT.excludeField(field, true);
                boolean deserialize = Excluder.DEFAULT.excludeField(field, false);
                if (serialize || deserialize) {
                    Log.d(TAG, typeToken.getRawType().getName() + "#@Transient " + field.getName() + SKIP_NOTE);
                    continue;
                }
                Type fieldType = $Gson$Types.resolve(typeToken.getType(), raw, field.getGenericType());
                TypeToken<?> fileTypeToken = TypeToken.get(fieldType);
                verifyInterface(fileTypeToken, typeToken.getRawType().getName() + "#" + field.getName());

            }
            Type resolve = $Gson$Types.resolve(typeToken.getType(), raw, raw.getGenericSuperclass());
            if (resolve == null) {
                break;
            }
            typeToken = TypeToken.get(resolve);
            raw = typeToken.getRawType();
        }
    }


    /**
     * 检查该类是否实现了 Serializable 或 ICECBaseBean 接口
     *
     * @param typeToken
     */
    private void verifySelfInterface(TypeToken<?> typeToken, String name) {
        boolean isImplemented = false;
        Class<?> rawType = typeToken.getRawType();
        while (rawType != null && rawType != Object.class) {
            Type[] interfaces = rawType.getGenericInterfaces();
            for (Type impl : interfaces) {
                if (impl instanceof Serializable || impl instanceof ICECBaseBean) {
                    isImplemented = true;
                    typeTokenCache.put(typeToken.getRawType().getName(), Object.class);
                    Log.d(TAG, typeToken.getRawType().getName() + " implemented " + impl.toString());
                    break;
                }
            }
            if (isImplemented) {
                break;
            }
            Type resolve = $Gson$Types.resolve(typeToken.getType(), rawType, rawType.getGenericSuperclass());
            if (resolve == null) {
                break;
            }
            typeToken = TypeToken.get(resolve);
            rawType = typeToken.getRawType();
        }

        if (!isImplemented) {
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException(typeToken.getRawType().getName() + " must be implemented Serializable or ICECBaseBean");
            } else {
                throw new IllegalArgumentException(name + " may be transient");
            }
        }
    }

    /**
     * 检查泛型参数是否实现了 Serializable 或 ICECBaseBean 接口
     *
     * @param arguments Type
     */
    private void verifyTypeArguments(Type[] arguments) {
        if (arguments.length <= 0) {
            return;
        }
        for (Type argument : arguments) {
            TypeToken<?> argumentToken = TypeToken.get(argument);
            if (argument instanceof Class) {
                //忽略基本数据类型及其封装类、String类型
                verifyInterface(argumentToken, null);
            } else if (argument instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) argument;
                //检验嵌套的泛型
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                verifyTypeArguments(actualTypeArguments);
            }
        }
    }

    /**
     * 判断是否基本数据类型及其封装类、String类型、Object类型
     *
     * @param type Type
     * @return boolean
     */
    private boolean isBaseType(Type type) {
        if (!(type instanceof Class)) {
            return false;
        }
        return type == Object.class
                || type == String.class
                || Primitives.isPrimitive(type)
                || Primitives.isWrapperType(type);
    }


    /**
     * 判断一个类是JAVA类型还是用户定义类型
     */
    private boolean isJavaClass(Class<?> clz) {
        return clz != null && clz.getPackage() != null && clz.getPackage().getName().startsWith("java");
    }

}
