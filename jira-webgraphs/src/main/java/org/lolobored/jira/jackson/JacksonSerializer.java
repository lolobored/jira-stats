package org.lolobored.jira.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.lolobored.jira.model.Issue;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class JacksonSerializer {

	public static class UserJsonSerializer
		extends JsonSerializer<Issue> {

		@Override
		public void serialize(Issue issue, JsonGenerator jsonGenerator,
													SerializerProvider serializerProvider) throws IOException{

			ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			mapper.writeValueAsString(issue);

		}


	}

	public static class UserJsonDeserializer
		extends JsonDeserializer<Issue> {

		@Override
		public Issue deserialize(JsonParser jsonParser,
														DeserializationContext deserializationContext)
			throws IOException {
			ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.setSerializationInclusion(JsonInclude.Include.NON_NULL);


			return mapper.readValue(jsonParser.getValueAsString(), Issue.class);
		}
	}
}
