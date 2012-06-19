package org.societies.thirdpartyservices.networking.webapp.json;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;



/**
 * A custom Jackson ObjectMapper that installs JSON serialization/deserialization support
 * for properties annotated with Spring format annotations such as @DateTimeFormat and @NumberFormat.
 */
public class ConversionServiceAwareObjectMapper extends ObjectMapper {

	@Autowired
	public ConversionServiceAwareObjectMapper(ConversionService conversionService) {
//		AnnotationIntrospector introspector = AnnotationIntrospector.pair(new AnnotationIntrospector(conversionService), DEFAULT_ANNOTATION_INTROSPECTOR);
//		SerializationConfig sconfig = new SerializationConfig(DEFAULT_INTROSPECTOR, introspector,  VisibilityChecker.Std.defaultInstance(), null, null, _jsonFactory, null);
//		DeserializationConfig dconfig = new DeserializationConfig(DEFAULT_INTROSPECTOR, introspector,  VisibilityChecker.Std.defaultInstance(), null, null, _jsonFactory, null);
//		setSerializationConfig(sconfig);
//		setDeserializationConfig(dconfig);
	}
}
