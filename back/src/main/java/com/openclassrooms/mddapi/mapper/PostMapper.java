package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.post.request.PostRequest;
import com.openclassrooms.mddapi.dto.post.response.PostResponse;
import com.openclassrooms.mddapi.entity.Post;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface PostMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "author", source = "author.username")
    @Mapping(target = "topic", source = "topic.title")
    @Mapping(target = "comments", source = "comments")
    PostResponse toResponse(Post post);

    List<PostResponse> toResponseList(List<Post> posts);

    Post toEntity(PostRequest postRequest);
}
