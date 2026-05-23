package com.ecommerce.Controller.User;

import com.ecommerce.dto.CommentDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.User.CommentService;
import com.ecommerce.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@RestController
@Slf4j
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 发表评论
     * @param commentDTO 评论请求 DTO
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> addComment(@RequestBody CommentDTO commentDTO) {
        log.info("发表评论请求: userId={}, productId={}", commentDTO.getUserId(), commentDTO.getProductId());
        commentService.addComment(commentDTO);
        log.info("评论发表成功");
        return Result.success();
    }

    /**
     * 删除评论
     * @param id 评论 ID
     * @param userId 用户 ID
     * @return 操作结果
     */
    @DeleteMapping
    public Result<Void> deleteComment(@RequestParam Long id, @RequestParam Long userId) {
        log.info("删除评论请求: id={}, userId={}", id, userId);
        commentService.deleteComment(id, userId);
        log.info("评论删除成功");
        return Result.success();
    }

    /**
     * 查询商品的评论列表
     * @param productId 商品 ID
     * @return 评论列表
     */
    @GetMapping("/list")
    public Result<Object> getCommentList(@RequestParam(required = false) String productId) {
        if (productId == null || productId.trim().isEmpty() || "null".equalsIgnoreCase(productId.trim())) {
            return Result.success(java.util.Collections.emptyList());
        }
        Long parsedProductId;
        try {
            parsedProductId = Long.parseLong(productId.trim());
        } catch (NumberFormatException e) {
            log.warn("productId 格式不正确: {}", productId);
            return Result.success(java.util.Collections.emptyList());
        }
        log.debug("查询商品评论列表请求: productId={}", parsedProductId);
        List<CommentVO> commentList = commentService.getCommentListByProductId(parsedProductId);
        log.debug("查询到评论数量: {}", commentList.size());
        return Result.success(commentList);
    }

    /**
     * 查询用户的评论列表
     * @param userId 用户 ID
     * @return 评论列表
     */
    @GetMapping("/user")
    public Result<List<CommentVO>> getUserComments(@RequestParam(required = false) String userId) {
        if (userId == null || userId.trim().isEmpty() || "null".equalsIgnoreCase(userId.trim())) {
            return Result.success(java.util.Collections.emptyList());
        }
        Long parsedUserId;
        try {
            parsedUserId = Long.parseLong(userId.trim());
        } catch (NumberFormatException e) {
            log.warn("userId 格式不正确: {}", userId);
            return Result.success(java.util.Collections.emptyList());
        }
        log.debug("查询用户评论列表请求: userId={}", parsedUserId);
        List<CommentVO> commentList = commentService.getCommentListByUserId(parsedUserId);
        log.debug("查询到评论数量: {}", commentList.size());
        return Result.success(commentList);
    }

    /**
     * 查询商品的评论统计
     * @param productId 商品 ID
     * @return 统计数据 {avgRating, count}
     */
    @GetMapping("/stats")
    public Result<Object> getCommentStats(@RequestParam(required = false) String productId) {
        if (productId == null || productId.trim().isEmpty() || "null".equalsIgnoreCase(productId.trim())) {
            return Result.success(new Object[]{0.0, 0});
        }
        Long parsedProductId;
        try {
            parsedProductId = Long.parseLong(productId.trim());
        } catch (NumberFormatException e) {
            log.warn("productId 格式不正确: {}", productId);
            return Result.success(new Object[]{0.0, 0});
        }
        log.debug("查询商品评论统计请求: productId={}", parsedProductId);
        Object[] stats = commentService.getCommentStats(parsedProductId);
        return Result.success(stats);
    }
}
