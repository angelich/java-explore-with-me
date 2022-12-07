package ru.practicum.mainservice.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.comment.model.CommentDto;

import java.util.List;

@RestController
@Log4j2
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments/{commentId}")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    List<CommentDto> getCommentsForModeration(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get comments for moderation: from={}, size={}", from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return commentService.getCommentsForModeration(pageRequest);
    }

    @PatchMapping("/publish")
    @ResponseStatus(HttpStatus.OK)
    void publishComment(@PathVariable(name = "commentId") Long commentId) {
        log.info("Publish comment={}", commentId);
        commentService.changeCommentStatus(commentId, CommentStatus.PUBLISHED);
    }

    @PatchMapping("/reject")
    @ResponseStatus(HttpStatus.OK)
    void rejectComment(@PathVariable(name = "commentId") Long commentId) {
        log.info("Reject comment={}", commentId);
        commentService.changeCommentStatus(commentId, CommentStatus.REJECTED);
    }
}
