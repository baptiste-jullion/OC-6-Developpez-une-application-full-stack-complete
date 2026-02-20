package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.comment.request.CommentRequest;
import com.openclassrooms.mddapi.dto.comment.response.CommentResponse;
import com.openclassrooms.mddapi.entity.Comment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "author", source = "author.username")
    @Mapping(target = "createdAt", source = "createdAt")
    CommentResponse toResponse(Comment comment);

    Comment toEntity(CommentRequest request);
}

