package demo.controller.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ViewMapper {

	private ObjectMapper mapper = new ObjectMapper();

	public <T extends Object> T map(Object object, Class<T> clazz) {

		return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).convertValue(object, clazz);
	}

	public <T extends Object> List<T> mapList(List<?> objects, Class<T> clazz) {

		if (objects == null) {
			return new ArrayList<>();
		}
		mapper = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<T> list = new ArrayList<>();
		for (Object object : objects) {
			list.add(mapper.convertValue(object, clazz));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public <K, V, T> Map<K, List<V>> mapMap(Map<K, List<T>> objects, Class<V> valueClass)
			throws InstantiationException, IllegalAccessException {

		if (objects == null) {
			return null;
		}
		Map<K, List<V>> map = objects.getClass().newInstance();
		for (Entry<K, List<T>> entry : objects.entrySet()) {
			map.put(entry.getKey(), mapList(entry.getValue(), valueClass));
		}
		return map;
	}
}
