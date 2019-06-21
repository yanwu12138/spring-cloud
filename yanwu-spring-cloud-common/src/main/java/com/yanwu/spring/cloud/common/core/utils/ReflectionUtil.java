package com.yanwu.spring.cloud.common.core.utils;

import com.google.common.collect.Maps;
import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import com.yanwu.spring.cloud.common.mvc.vo.ValueObject;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public final class ReflectionUtil {

	private ReflectionUtil() {
	}

	private static final Set<Class<?>> PRIMITIVE_TYPES;
	private static final Set<Class<?>> PRIMITIVE_WRAPPER_TYPES;
	private static final Set<Class<?>> PRIMITIVE_ARRAY_TYPES;
	private static final Set<Class<?>> PRIMITIVE_WRAPPER_ARRAY_TYPES;

	static {
		PRIMITIVE_TYPES = new HashSet<>(9);
		PRIMITIVE_TYPES.add(Boolean.TYPE);
		PRIMITIVE_TYPES.add(Character.TYPE);
		PRIMITIVE_TYPES.add(Byte.TYPE);
		PRIMITIVE_TYPES.add(Short.TYPE);
		PRIMITIVE_TYPES.add(Integer.TYPE);
		PRIMITIVE_TYPES.add(Long.TYPE);
		PRIMITIVE_TYPES.add(Float.TYPE);
		PRIMITIVE_TYPES.add(Double.TYPE);
		PRIMITIVE_TYPES.add(Void.TYPE);

		PRIMITIVE_WRAPPER_TYPES = new HashSet<>(9);
		PRIMITIVE_WRAPPER_TYPES.add(Boolean.class);
		PRIMITIVE_WRAPPER_TYPES.add(Character.class);
		PRIMITIVE_WRAPPER_TYPES.add(Byte.class);
		PRIMITIVE_WRAPPER_TYPES.add(Short.class);
		PRIMITIVE_WRAPPER_TYPES.add(Integer.class);
		PRIMITIVE_WRAPPER_TYPES.add(Long.class);
		PRIMITIVE_WRAPPER_TYPES.add(Float.class);
		PRIMITIVE_WRAPPER_TYPES.add(Double.class);
		PRIMITIVE_WRAPPER_TYPES.add(Void.class);

		PRIMITIVE_ARRAY_TYPES = new HashSet<>(8);
		PRIMITIVE_ARRAY_TYPES.add(boolean[].class);
		PRIMITIVE_ARRAY_TYPES.add(char[].class);
		PRIMITIVE_ARRAY_TYPES.add(byte[].class);
		PRIMITIVE_ARRAY_TYPES.add(short[].class);
		PRIMITIVE_ARRAY_TYPES.add(int[].class);
		PRIMITIVE_ARRAY_TYPES.add(long[].class);
		PRIMITIVE_ARRAY_TYPES.add(float[].class);
		PRIMITIVE_ARRAY_TYPES.add(double[].class);

		PRIMITIVE_WRAPPER_ARRAY_TYPES = new HashSet<>(8);
		PRIMITIVE_WRAPPER_ARRAY_TYPES.add(Boolean[].class);
		PRIMITIVE_WRAPPER_ARRAY_TYPES.add(Character[].class);
		PRIMITIVE_WRAPPER_ARRAY_TYPES.add(Byte[].class);
		PRIMITIVE_WRAPPER_ARRAY_TYPES.add(Short[].class);
		PRIMITIVE_WRAPPER_ARRAY_TYPES.add(Integer[].class);
		PRIMITIVE_WRAPPER_ARRAY_TYPES.add(Long[].class);
		PRIMITIVE_WRAPPER_ARRAY_TYPES.add(Float[].class);
		PRIMITIVE_WRAPPER_ARRAY_TYPES.add(Double[].class);
	}

	public static List<Field> getAllFields(Class<?> clazz) {
		if (clazz == null) {
			return Collections.emptyList();
		}

		List<Field> list = new LinkedList<>();

		do {
			Collections.addAll(list, clazz.getDeclaredFields());
			clazz = clazz.getSuperclass();
		} while (clazz != null);

		return list;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Field> getAllFieldsAsMap(final Class<?> clazz) {
		return getAllFieldsAsMapWithoutAnnotations(clazz);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Field> getAllFieldsAsMapWithoutAnnotations(final Class<?> clazz,
			final Class<? extends Annotation>... annotationClasses) {
		if (clazz == null) {
			return Collections.emptyMap();
		}

		List<Field> fields = getAllFields(clazz);
		Map<String, Field> map = Maps.newHashMapWithExpectedSize(fields.size());

		if (ArrayUtils.isNotEmpty(annotationClasses)) {
			for (Field f : fields) {
				if (isAnyAnnotationPresent(f, annotationClasses)) {
					continue;
				}
				map.put(f.getName(), f);
			}
		} else {
			for (Field f : fields) {
				map.put(f.getName(), f);
			}
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public static boolean isAnyAnnotationPresent(final Field field,
			final Class<? extends Annotation>... annotationClasses) {
		for (Class<? extends Annotation> annotationClass : annotationClasses) {
			if (field.isAnnotationPresent(annotationClass)) {
				return true;
			}
		}
		return false;
	}

	public static Object invokeGet(final Object o, final String getMethodName) {
		Class<?> clazz = o.getClass();
		Method getter = getMethod(clazz, getMethodName, null);
		checkArgument(getter != null, "Method %s() not found in class %s", getMethodName, clazz.getName());
		return invokeGet(o, getter);
	}

	public static Object invokeGet(final Object o, final Method getter) {
		return invokeMethod(o, getter, (Object[]) null);
	}

	static void invokeSet(final Object o, final Method setter, final Object arg) {
		invokeMethod(o, setter, arg);
	}

	public static Object invokeMethod(final Object o, final Method m, final Object... args) {
		try {
			return m.invoke(o, args);
		} catch (InvocationTargetException | IllegalAccessException ex) {
			Throwable logEx = ex.getCause();
			if (logEx == null) {
				logEx = ex;
			}

			throw new RuntimeException(
					String.format("Failed to invoke method: %s.%s()", o.getClass().getSimpleName(), m.getName()),
					logEx);
		}
	}

	public static Class<?>[] getParameterTypes(final Object... args) {
		if (args == null) {
			return new Class<?>[0];
		}

		Class<?>[] paramTypes = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			paramTypes[i] = args[i] == null ? Object.class : args[i].getClass();
		}
		return paramTypes;
	}

	public static Object invokeMethod(final Object o, final String methodName, final Object... args) {
		Class<?>[] paramTypes = getParameterTypes(args);
		return invokeMethod(o, methodName, paramTypes, args);
	}

	public static Object invokeMethod(final Object o, final String methodName, final Class<?>[] paramTypes,
			final Object... args) {
		Method m = getMethodIncludeSuperclass(o.getClass(), methodName, paramTypes, true);
		if (m == null) {
			StringBuilder sb = new StringBuilder("Method not found: ").append(o.getClass().getName()).append(".")
					.append(methodName).append("(");
			if (paramTypes != null) {
				for (int i = 0, len = paramTypes.length; i < len; i++) {
					sb.append(paramTypes[i].getName());
					if (i < len - 1) {
						sb.append(", ");
					}
				}
			}
			sb.append(")");
			throw new IllegalArgumentException(sb.toString());
		}

		boolean originalAccessible = m.isAccessible();
		try {
			if (!originalAccessible) {
				m.setAccessible(true);
			}
			return invokeMethod(o, m, args);
		} finally {
			if (!originalAccessible) {
				m.setAccessible(originalAccessible);
			}
		}
	}

	public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>[] paramTypes) {
		return getMethod(clazz, methodName, paramTypes, false);
	}

	public static Method getMethodIncludeSuperclass(final Class<?> clazz, final String methodName,
			final Class<?>[] paramTypes, final boolean includePrivateMethods) {
		Class<?> curClass = clazz;

		while (curClass != null) {
			Method m = getMethod(curClass, methodName, paramTypes, includePrivateMethods);
			if (m != null) {
				return m;
			} else {
				curClass = curClass.getSuperclass();
			}
		}

		return null;
	}

	public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>[] paramTypes,
			final boolean includePrivateMethods) {
		Method[] methods = includePrivateMethods ? clazz.getDeclaredMethods() : clazz.getMethods();
		for (Method method : methods) {
			if (!method.getName().equals(methodName)) {
				continue;
			}

			Class<?>[] declaredParamTypes = method.getParameterTypes();

			if (ArrayUtils.isEmpty(declaredParamTypes)) {
				if (ArrayUtils.isEmpty(paramTypes)) {
					return method;
				}

				continue;
			}

			if (paramTypes == null || declaredParamTypes.length != paramTypes.length) {
				continue;
			}

			boolean paramTypesMatch = true;

			for (int j = 0; j < declaredParamTypes.length; j++) {
				if (paramTypes[j] == null) {
					if (declaredParamTypes[j].isPrimitive()) {
						paramTypesMatch = false;
						break;
					} else {
						continue;
					}
				}
				if (!declaredParamTypes[j].isAssignableFrom(paramTypes[j])) {
					paramTypesMatch = false;
					break;
				}
			}

			if (paramTypesMatch) {
				return method;
			}
		}

		return null;
	}

	public static List<Field> getFieldsOfType(final Class<?> clazz, final Class<?> type) {
		List<Field> result = new LinkedList<>();

		for (Field f : getAllFields(clazz)) {
			if (type.isAssignableFrom(f.getType())) {
				result.add(f);
			}
		}

		return result;
	}

	public static Field getField(Class<?> clazz, final String name) {
		do {
			try {
				return clazz.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		} while (clazz != null);

		return null;
	}

	public static Object getFieldValue(Object o, final String name) {
		o = DataUtil.unboxProxy(o);
		if (o == null) {
			return null;
		}
		Field field = getField(o.getClass(), name);
		if (field == null) {
			return null;
		}

		boolean originalAccessible = field.isAccessible();
		try {
			if (!originalAccessible) {
				field.setAccessible(true);
			}
			return DataUtil.unboxProxy(field.get(o));
		} catch (Exception e) {
			return null;
		} finally {
			if (!originalAccessible) {
				field.setAccessible(originalAccessible);
			}
		}
	}

	public static void setFieldValue(final Object o, final String name, final Object value) {
		Field field = getField(o.getClass(), name);
		checkArgument(field != null, "Field not found: %s.%s", o.getClass().getName(), name);
		setFieldValue(o, field, value);
	}

	public static void setFieldValue(final Object o, final Field field, Object value) {
		try {
			field.setAccessible(true);

			if (value == null) {
				field.set(o, null);
				return;
			}

			String convertedValue;

			if (value instanceof byte[]) {
				convertedValue = new String((byte[]) value, "UTF-8");
			} else if (value instanceof String) {
				convertedValue = (String) value;
			} else {
				convertedValue = value.toString();
			}

			if (field.getType() == int.class) {
				field.setInt(o, Integer.parseInt(convertedValue));
			} else if (field.getType() == short.class) {
				field.setShort(o, Short.parseShort(convertedValue));
			} else if (field.getType() == byte.class) {
				field.setByte(o, Byte.parseByte(convertedValue));
			} else if (field.getType() == long.class) {
				field.setLong(o, Long.parseLong(convertedValue));
			} else if (field.getType() == short.class) {
				field.setShort(o, Short.parseShort(convertedValue));
			} else if (field.getType() == float.class) {
				field.setFloat(o, Float.parseFloat(convertedValue));
			} else if (field.getType() == double.class) {
				field.setDouble(o, Double.parseDouble(convertedValue));
			} else if (field.getType() == boolean.class) {
				field.set(o, Boolean.parseBoolean(convertedValue));
			} else {
				if (field.getType() == Integer.class) {
					value = Integer.valueOf(convertedValue);
					field.set(o, value);
				} else if (field.getType() == Short.class) {
					value = Short.valueOf(convertedValue);
					field.set(o, value);
				} else if (field.getType() == Byte.class) {
					value = Byte.valueOf(convertedValue);
					field.set(o, value);
				} else if (field.getType() == Long.class) {
					value = Long.valueOf(convertedValue);
					field.set(o, value);
				} else if (field.getType() == Short.class) {
					value = Short.valueOf(convertedValue);
					field.set(o, value);
				} else if (field.getType() == BigInteger.class) {
					value = BigInteger.valueOf(Long.parseLong(convertedValue));
					field.set(o, value);
				} else if (field.getType() == Float.class) {
					value = Float.valueOf(convertedValue);
					field.set(o, value);
				} else if (field.getType() == Double.class) {
					value = Double.valueOf(convertedValue);
					field.set(o, value);
				} else if (field.getType() == Boolean.class) {
					value = Boolean.valueOf(convertedValue);
					field.set(o, value);
				} else if (field.getType() == Date.class) {
					if (Date.class.isAssignableFrom(value.getClass())) {
						field.set(o, value);
					} else {
						throw new RuntimeException("Can't copy Date type");
					}
				} else if (field.getType() == String.class) {
					field.set(o, convertedValue);
				} else if (field.getType().isAssignableFrom(value.getClass())) {
					field.set(o, value);
				} else {
					System.err.println("Failed to set value for field " + field.getName() + " on class "
							+ o.getClass().getSimpleName());
				}
			}
		} catch (Exception e) {
		} finally {
			field.setAccessible(false);
		}
	}

	public static boolean isPrimitiveType(final Field f) {
		return PRIMITIVE_TYPES.contains(f.getType());
	}

	public static boolean isPrimitiveType(final Class<?> clazz) {
		return PRIMITIVE_TYPES.contains(clazz);
	}

	public static boolean isPrimitiveArrayType(final Class<?> clazz) {
		return PRIMITIVE_ARRAY_TYPES.contains(clazz);
	}

	public static boolean isPrimitiveWrapperType(final Class<?> clazz) {
		return PRIMITIVE_WRAPPER_TYPES.contains(clazz);
	}

	public static boolean isPrimitiveWrapperArrayType(final Class<?> clazz) {
		return PRIMITIVE_WRAPPER_ARRAY_TYPES.contains(clazz);
	}

	public static <T> T newInstance(final Class<T> clazz) {
		Constructor<T> ctor = null;

		try {
			ctor = clazz.getDeclaredConstructor(new Class<?>[0]);
			ctor.setAccessible(true);
			T o = ctor.newInstance(new Object[0]);
			return o;
		} catch (Throwable t) {
			return null;
		} finally {
			if (ctor != null) {
				ctor.setAccessible(false);
			}
		}
	}

	public static Class<?> getClassGenericType(final Class<?> parentClass) {
		return getClassGenericType(parentClass, 0);
	}

	static public Class<?> getInterfaceGenericType(Class<?> parentInterface) {
		return getInterfaceGenericType(parentInterface, 0, 0);
	}

	/**
	 * @param position
	 *            position of the parameter in class definition, e.g. For
	 *            Class<A, B>, position = 0 for A, 1 for B
	 */
	public static Class<?> getClassGenericType(final Class<?> parentClass, final int position) {
		return (Class<?>) ((ParameterizedType) parentClass.getGenericSuperclass()).getActualTypeArguments()[position];
	}

	static private Class<?> getInterfaceGenericType(Class<?> parentInterface, int infPosition, int position) {
		return (Class<?>) ((ParameterizedType) parentInterface.getGenericInterfaces()[infPosition])
				.getActualTypeArguments()[position];
	}

	/**
	 * Retrieve the parameterized type of a field, e.g. List<String> -> String
	 */
	public static Class<?> getFieldGenericType(final Field field) {
		ParameterizedType elemType = (ParameterizedType) field.getGenericType();
		Type type = elemType.getActualTypeArguments()[0];
		Class<?> elemClass;

		if (type instanceof Class<?>) {
			elemClass = (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			elemClass = (Class<?>) ((ParameterizedType) type).getRawType();
		} else {
			throw new RuntimeException("To be implemented.");
		}

		return elemClass;
	}

	public static interface ObjectVisitor<TARGET> {
		public void visit(final Object parent, final TARGET o, final TraversalContext context);
	}

	public static <T> void traverseObject(final Object o, final Class<T> type, final ObjectVisitor<T> visitor) {
		traverseObject(o, type, visitor, new TraversalContext(o));
	}

	public static <T> void traverseObject(final Object o, final Class<T> type, final ObjectVisitor<T> visitor,
			final TraversalContext context) {
		traverseObject(o, type, visitor, context, 1, Integer.MAX_VALUE);
	}

	@SuppressWarnings("unchecked")
	public static <T> void traverseObject(final Object o, final Class<T> type, final ObjectVisitor<T> visitor,
			final TraversalContext context, final int level, final int maxLevel) {
		traverseObjectWithoutAnnotations(o, type, visitor, context, level, maxLevel);
	}

	@SuppressWarnings("unchecked")
	public static <T> void traverseObjectWithoutAnnotations(final Object o, final Class<T> type,
			final ObjectVisitor<T> visitor, final TraversalContext context,
			final Class<? extends Annotation>... annotations) {
		traverseObjectWithoutAnnotations(o, type, visitor, context, 1, Integer.MAX_VALUE, annotations);
	}

	@SuppressWarnings("unchecked")
	public static <T> void traverseObjectWithoutAnnotations(final Object o, final Class<T> type,
			final ObjectVisitor<T> visitor, final TraversalContext context, final int level, final int maxLevel,
			final Class<? extends Annotation>... annoClasses) {
		if (!isModelClass(type)) {
			throw new RuntimeException("Type not supported: " + type.getName());
		}
		if (level > maxLevel) {
			return;
		}
		for (Field f : getAllFieldsAsMapWithoutAnnotations(o.getClass(), annoClasses).values()) {
			String fieldName = f.getName();
			if (Collection.class.isAssignableFrom(f.getType())) {
				Collection<T> c = (Collection<T>) getFieldValue(o, f.getName());
				if (c == null) {
					continue;
				}
				int i = 0;
				for (T co : c) {
					context.getPathStack().add(fieldName + "[" + i + "]");
					if (type.isAssignableFrom(co.getClass())) {
						visitor.visit(c, co, context);
					}
					traverseObjectWithoutAnnotations(co, type, visitor, context, level + 1, maxLevel, annoClasses);
					context.getPathStack().remove(context.getPathStack().size() - 1);
					i++;
				}
			} else if (Map.class.isAssignableFrom(f.getType())) {
				Map<?, ?> m = (Map<?, ?>) getFieldValue(o, f.getName());
				if (m == null) {
					continue;
				}
				for (Map.Entry<?, ?> entry : m.entrySet()) {
					context.getPathStack().add(fieldName + "[" + entry.getKey().toString() + "]");
					T co = (T) entry.getValue();
					if (type.isAssignableFrom(co.getClass())) {
						visitor.visit(m, co, context);
					}
					traverseObjectWithoutAnnotations(co, type, visitor, context, level + 1, maxLevel, annoClasses);
					context.getPathStack().remove(context.getPathStack().size() - 1);
				}
			} else if (isModelClass(f.getType())) {
				T fo = (T) getFieldValue(o, f.getName());
				if (fo == null) {
					continue;
				}
				context.getPathStack().add(fieldName);

				if (type.isAssignableFrom(fo.getClass())) {
					visitor.visit(o, fo, context);
				}
				traverseObjectWithoutAnnotations(fo, type, visitor, context, level + 1, maxLevel, annoClasses);
				context.getPathStack().remove(context.getPathStack().size() - 1);
			}
		}
	}

	/**
	 * Get the first level associated objects including from collections
	 * (maxLevel = Integer.MAX_VALUE)
	 */
	public static <T> List<T> getAssociatedObjects(final Object o, final Class<T> type) {
		return getAssociatedObjects(o, type, Integer.MAX_VALUE);
	}

	/**
	 * Get associated objects including from collections up to maxLevel
	 */
	public static <T> List<T> getAssociatedObjects(final Object o, final Class<T> type, final int maxLevel) {
		final List<T> result = new ArrayList<>(5);
		traverseObject(o, type, new ObjectVisitor<T>() {
			@Override
			public void visit(final Object parent, final T o, final TraversalContext context) {
				addUniqueObjectToList(result, o);
			}
		}, new TraversalContext(o), 1, maxLevel);
		return result;
	}

	private static <T> void addUniqueObjectToList(final List<T> list, final T o) {
		for (T o2 : list) {
			if (o == o2) {
				return;
			}
		}

		list.add(0, o);
	}

	public static boolean isDoClass(final Class<?> clazz) {
		return BaseObject.class.isAssignableFrom(clazz);
	}

	public static boolean isVoClass(final Class<?> clazz) {
		return ValueObject.class.isAssignableFrom(clazz);
	}

	public static boolean isModelClass(final Class<?> clazz) {
		return isDoClass(clazz) || isVoClass(clazz);
	}

	public static boolean isAnnotationPresent(Class<?> clazz, final Class<? extends Annotation> annotationClass) {
		do {
			if (clazz.isAnnotationPresent(annotationClass)) {
				return true;
			}
		} while ((clazz = clazz.getSuperclass()) != Object.class);
		return false;
	}

	public static boolean isAnnotationPresent(Class<?> targetClass, final String methodName,
			final Class<?>[] paramTypes, final Class<? extends Annotation> annoClass) {
		do {
			try {
				Method method = targetClass.getMethod(methodName, paramTypes);
				if (method.isAnnotationPresent(annoClass)) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		} while ((targetClass = targetClass.getSuperclass()) != Object.class);
		return false;
	}

	public static <TARGET> List<TARGET> convertToObjectList(final Class<TARGET> voClass, final List<?> data) {
		List<TARGET> vos = new ArrayList<>();
		for (Object o : data) {
			vos.add(convertToObject(voClass, o));
		}
		return vos;
	}

	@SuppressWarnings("unchecked")
	public static <TARGET> TARGET convertToObject(final Class<TARGET> voClass, final Object o) {
		if (o == null) {
			return null;
		} else if (voClass.isAssignableFrom(o.getClass())) {
			return (TARGET) o;
		} else if (o instanceof Map) {
			String s = JsonUtil.toJsonString(o);
			return JsonUtil.toObject(s, voClass);
		} else {
			throw new RuntimeException(
					String.format("Can't convert type %s to %s", o.getClass().getName(), voClass.getName()));
		}
	}

}
