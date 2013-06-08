package com.jdon.mvc.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class JSONWriter {

	private static final Log LOG = LogFactory.getLog(AnnotationUtils.class);

	private final StringBuilder serialized;

	private final int maximumDepth;

	private int depth = 0;

	public JSONWriter(int maximum) {
		if (maximum <= 0) {
			throw new IllegalArgumentException("serialization depth must be > 0");
		}
		this.maximumDepth = maximum;
		this.serialized = new StringBuilder();
	}

	public String write(Object object) {
		delegateToProperSerializeMethod(object);
		return serialized.toString();
	}

	@SuppressWarnings("unchecked")
	private void delegateToProperSerializeMethod(Object object) {
		if (this.depth > this.maximumDepth) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("not writing object " + object + ", maximum deepth reached " + maximumDepth);
			}
			addAsString("{}");
			return;
		}
		this.depth++;

		if (object == null)
			addAsString("null");
		else if (object instanceof Class)
			addAsStringScapingCharacters(object);
		else if (object instanceof Number)
			addAsString(object);
		else if (object instanceof Boolean)
			addAsString(object);
		else if (object instanceof String)
			addAsStringScapingCharacters(object);
		else if (object instanceof Character)
			addAsStringScapingCharacters(object);
		else if (object instanceof Map)
			serializeMap((Map) object);
		else if (object.getClass().isArray())
			serializeArray(object);
		else if (object instanceof Iterable)
			serializeIterable(((Iterable) object));
		else if (object instanceof Calendar)
			addAsString(((Calendar)object).getTimeInMillis());
		else if (object instanceof Date)
			addAsString(((Date)object).getTime());
		else
			serializeBean(object);

		this.depth--;
	}

	private void serializeBean(Object object) {
		addAsString("{");
		try {

			Map<String, Method> methods = ReflectionUtil.getGetters(object.getClass());
			for (Entry<String, Method> entry : methods.entrySet()) {
				Method accessor = entry.getValue();
				Object value = accessor.invoke(object, (Object[]) null);
				writeProperty(entry.getKey(), value);
				addCharacter(',');
			}
			if (!methods.isEmpty()) {
				serialized.deleteCharAt(serialized.length() - 1);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot render object " + object + " as JSON", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Cannot render object " + object + " as JSON", e);
		}

		addAsString("}");

	}

	private void writeProperty(String name, Object value) {
		addCharacter('"');
		addAsString(name);
		addAsString("\":");
		delegateToProperSerializeMethod(value);
	}

	@SuppressWarnings("unchecked")
	private void serializeMap(Map map) {
		addAsString("{");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			delegateToProperSerializeMethod(key);
			addAsString(":");
			delegateToProperSerializeMethod(value);
			if (it.hasNext())
				addAsString(",");
		}
	
		addAsString("}");
	}

	@SuppressWarnings("unchecked")
	private void serializeIterable(Iterable iterable) {
		addAsString("[");
		Iterator it = iterable.iterator();
		while (it.hasNext()) {
			delegateToProperSerializeMethod(it.next());
			if (it.hasNext())
				addAsString(",");
		}
		addAsString("]");
	}

	private void serializeArray(Object object) {
		addAsString("[");
		int length = Array.getLength(object);
		for (int i = 0; i < length; ++i) {
			delegateToProperSerializeMethod(Array.get(object, i));
			if (i < length - 1)
				addCharacter(',');
		}
		addAsString("]");
	}

	private void addAsStringScapingCharacters(Object obj) {
		addCharacter('"');
		CharacterIterator it = new StringCharacterIterator(obj.toString());
		for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			if (c == '"')
				addAsString("\\\"");
			else if (c == '\\')
				addAsString("\\\\");
			else if (c == '/')
				addAsString("\\/");
			else if (c == '\b')
				addAsString("\\b");
			else if (c == '\f')
				addAsString("\\f");
			else if (c == '\n')
				addAsString("\\n");
			else if (c == '\r')
				addAsString("\\r");
			else if (c == '\t')
				addAsString("\\t");
			else {
				addCharacter(c);
			}
		}
		addCharacter('"');
	}

	private void addAsString(Object obj) {
		serialized.append(obj);
	}

	private void addCharacter(char c) {
		serialized.append(c);
	}
}