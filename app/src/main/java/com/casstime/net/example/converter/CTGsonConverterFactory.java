package com.casstime.net.example.converter;

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

    private final Map<TypeToken<?>, Object> typeTokenCache = new ConcurrentHashMap<>();

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
        Type[] interfaces = type.getRawType().getGenericInterfaces();
        verifyInterface(type, interfaces);
        TypeAdapter<?> adapter = gson.getAdapter(type);
        return new CTGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));

        return new CTGsonRequestBodyConverter<>(gson, adapter);
    }

    /**
     * 检查是否实现 Serializable 或 ICECBaseBean 接口
     *
     * @param type
     * @param interfaces
     */
    private void verifyInterface(TypeToken<?> type, Type[] interfaces) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (type == null) {
            return;
        }
        Object cached = typeTokenCache.get(type);
        if (cached != null) {
            Log.i(TAG, type.toString() + " skip verify Interface");
            return;
        }

        boolean isImplemented = false;
        for (Type impl : interfaces) {
            if (impl instanceof Serializable || impl instanceof ICECBaseBean) {
                isImplemented = true;
                typeTokenCache.put(type, Object.class);
                Log.i(TAG, type.toString() + " implemented " + impl.toString());
                break;
            }
        }

        if (!isImplemented) {
            throw new IllegalArgumentException(type.toString() + " must be implemented Serializable or ICECBaseBean");
        }

        //处理成员变量
        Class<?> raw = type.getRawType();
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            for (Field field : fields) {
                boolean serialize = Excluder.DEFAULT.excludeField(field, true);
                boolean deserialize = Excluder.DEFAULT.excludeField(field, false);
                if (serialize || deserialize) {
                    continue;
                }
                Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                if (fieldType instanceof Class) {
                    if (isBaseType(fieldType)) {
                        //忽略基本数据类型及其封装类、String类型
                        continue;
                    }
                    TypeToken<?> fileTypeToken = TypeToken.get(fieldType);
                    verifyInterface(fileTypeToken, fileTypeToken.getRawType().getGenericInterfaces());
                } else if (fieldType instanceof ParameterizedType) {
                    //带泛型的集合类
                    Type[] arguments = ((ParameterizedType) fieldType).getActualTypeArguments();
                    verifyTypeArguments(arguments);
                }
            }
            Type resolve = $Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass());
            if (resolve == null) {
                break;
            }
            type = TypeToken.get(resolve);
            raw = type.getRawType();
        }
    }

    /**
     * 检查泛型参数
     *
     * @param argumentType Type
     */
    private void verifyTypeArguments(Type[] argumentType) {
        if (argumentType.length <= 0) {
            return;
        }
        for (Type type : argumentType) {
            TypeToken<?> fileTypeToken = TypeToken.get(type);
            if (type instanceof Class) {
                //忽略基本数据类型及其封装类、String类型
                if (!isBaseType(type)) {
                    verifyInterface(fileTypeToken, fileTypeToken.getRawType().getInterfaces());
                }
                continue;
            }

            ParameterizedType parameterizedType = (ParameterizedType) type;

            //检验嵌套的泛型
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            verifyTypeArguments(actualTypeArguments);
        }
    }

    /**
     * 判断是否基本数据类型及其封装类、String类型
     *
     * @param type Type
     * @return boolean
     */
    private boolean isBaseType(Type type) {
        if (!(type instanceof Class)) {
            return false;
        }
        return type == String.class
                || Primitives.isPrimitive(type)
                || Primitives.isWrapperType(type);
    }

}
