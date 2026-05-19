package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.dto.CommentDTO;
import com.ecommerce.entity.Comment;
import com.ecommerce.entity.User;
import com.ecommerce.common.exception.CommentException;
import com.ecommerce.mapper.CommentMapper;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.service.User.CommentService;
import com.ecommerce.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserMapper userMapper;

    CommentServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 发表评论
     * @param commentDTO 评论请求 DTO
     */
    @Override
    public void addComment(CommentDTO commentDTO) {
        log.info("发表评论：userId={}, productId={}, orderId={}", commentDTO.getUserId(), commentDTO.getProductId(), commentDTO.getOrderId());

        // 校验评分
        if (commentDTO.getRating() == null || commentDTO.getRating() < 1 || commentDTO.getRating() > 5) {
            throw new CommentException("评分必须在1-5之间");
        }

        // 检查是否已评论过该商品
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getUserId, commentDTO.getUserId())
                    .eq(Comment::getProductId, commentDTO.getProductId());
        if (count(queryWrapper) > 0) {
            throw new CommentException("您已评论过该商品");
        }

        Comment comment = Comment.builder()
                .userId(commentDTO.getUserId())
                .productId(commentDTO.getProductId())
                .orderId(commentDTO.getOrderId())
                .rating(commentDTO.getRating())
                .content(commentDTO.getContent())
                .images(commentDTO.getImages())
                .build();

        save(comment);
        log.info("评论发表成功：id={}", comment.getId());
    }

    /**
     * 删除评论
     * @param id 评论 ID
     * @param userId 用户 ID
     */
    @Override
    public void deleteComment(Long id, Long userId) {
        log.info("删除评论：id={}, userId={}", id, userId);

        Comment comment = getById(id);
        if (comment == null) {
            throw new CommentException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new CommentException("无权删除该评论");
        }

        removeById(id);
        log.info("评论删除成功：id={}", id);
    }

    /**
     * 查询商品的评论列表
     * @param productId 商品 ID
     * @return 评论列表
     */
    @Override
    public List<CommentVO> getCommentListByProductId(Long productId) {
        log.debug("查询商品评论列表：productId={}", productId);

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getProductId, productId)
                    .orderByDesc(Comment::getCreateTime);

        List<Comment> comments = list(queryWrapper);
        return convertToVOList(comments);
    }

    /**
     * 查询用户的评论列表
     * @param userId 用户 ID
     * @return 评论列表
     */
    @Override
    public List<CommentVO> getCommentListByUserId(Long userId) {
        log.debug("查询用户评论列表：userId={}", userId);

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getUserId, userId)
                    .orderByDesc(Comment::getCreateTime);

        List<Comment> comments = list(queryWrapper);
        return convertToVOList(comments);
    }

    /**
     * 查询商品的评论统计
     * @param productId 商品 ID
     * @return [平均分, 评论数]
     */
    @Override
    public Object[] getCommentStats(Long productId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getProductId, productId);

        List<Comment> comments = list(queryWrapper);
        if (comments.isEmpty()) {
            return new Object[]{0.0, 0};
        }

        double avgRating = comments.stream()
                .mapToInt(Comment::getRating)
                .average()
                .orElse(0.0);

        return new Object[]{Math.round(avgRating * 10) / 10.0, comments.size()};
    }

    /**
     * 批量获取用户名
     */
    private Map<Long, String> getUsernames(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        List<Integer> intIds = userIds.stream().map(Long::intValue).collect(Collectors.toList());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, intIds);
        return userMapper.selectList(queryWrapper).stream()
                .collect(Collectors.toMap(u -> u.getId().longValue(), User::getUsername));
    }

    /**
     * Entity 列转 VO 列
     */
    private List<CommentVO> convertToVOList(List<Comment> comments) {
        if (comments.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Long> userIds = comments.stream().map(Comment::getUserId).distinct().collect(Collectors.toList());
        Map<Long, String> usernameMap = getUsernames(userIds);

        return comments.stream()
                .map(c -> CommentVO.builder()
                        .id(c.getId())
                        .userId(c.getUserId())
                        .username(usernameMap.get(c.getUserId()))
                        .productId(c.getProductId())
                        .orderId(c.getOrderId())
                        .rating(c.getRating())
                        .content(c.getContent())
                        .images(c.getImages())
                        .createTime(c.getCreateTime() != null ? c.getCreateTime().format(DATE_TIME_FORMATTER) : null)
                        .build())
                .collect(Collectors.toList());
    }
}
