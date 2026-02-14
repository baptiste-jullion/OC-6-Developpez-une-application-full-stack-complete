package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.topic.response.TopicResponse;
import com.openclassrooms.mddapi.entity.Topic;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    TopicResponse toResponse(Topic topic);

    List<TopicResponse> toResponseList(List<Topic> topics);
}
