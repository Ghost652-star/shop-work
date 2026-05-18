package com.ecommerce.service.User;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.dto.CommentDTO;
import com.ecommerce.entity.Comment;
import com.ecommerce.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {
    /**
     * 发表评论
     * @param commentDTO 评论请求 DTO
     */
    void addComment(CommentDTO commentDTO);

    /**
     * 删除评论
     * @param id 评论 ID
     * @param userId 用户 ID（校验权限）
     */
    void deleteComment(Long id, Long userId);

    /**
     * 查询商品的评论列表
     * @param productId 商品 ID
     * @return 评论列表
     */
    List<CommentVO> getCommentListByProductId(Long productId);

    /**
     * 查询用户的评论列表
     * @param userId 用户 ID
     * @return 评论列表
     */
    List<CommentVO> getCommentListByUserId(Long userId);

    /**
     * 查询商品的评论统计（平均分、评论数）
     * @param productId 商品 ID
     * @return 统计数据 [平均分, 评论数]
     */
    Object[] getCommentStats(Long productId);
}
