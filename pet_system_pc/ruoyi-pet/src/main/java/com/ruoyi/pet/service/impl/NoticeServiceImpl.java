package com.ruoyi.pet.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pet.domain.UserNotice;
import com.ruoyi.pet.mapper.UserNoticeMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;
import com.ruoyi.pet.mapper.NoticeMapper;
import com.ruoyi.pet.domain.Notice;
import com.ruoyi.pet.service.INoticeService;

import javax.annotation.Resource;

/**
 * 通知Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-07
 */
@Service
@Slf4j
public class NoticeServiceImpl implements INoticeService {
    @Resource
    private NoticeMapper noticeMapper;

    @Resource
    private UserNoticeMapper userNoticeMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 查询通知
     *
     * @param noticeId 通知主键
     * @return 通知
     */
    @Override
    public Notice selectNoticeByNoticeId(Long noticeId) {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //获取当前用户的角色
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //如果是普通微信用户，那么需要根据user_id，以及notice_id 进行已读从操作
        if (StringUtils.isNotEmpty(loginUser.getOpenId())) {
            //微信用户
            //更新用户与通知表之间的中间表阅读状态
            //UPDATE tb_user_notice SET read_status = 1 WHERE user_id = 110 AND notice_id = 44
            UserNotice userNotice = new UserNotice();
            userNotice.setUserId(userId);
            userNotice.setNoticeId(noticeId);
            userNotice.setReadTime(DateUtils.getNowDate());
            userNoticeMapper.updateReadStatus(userNotice);
        }
        return noticeMapper.selectNoticeByNoticeId(noticeId);
    }

    /**
     * 查询通知列表
     *
     * @param notice 通知
     * @return 通知
     */
    @Override
    public List<Notice> selectNoticeList(Notice notice) {
        List<Notice> notices = noticeMapper.selectNoticeList(notice);
        notices.forEach(noticeItem -> {
            //获取通知id
            Long noticeId = noticeItem.getNoticeId();
            //查询当前通知的接收者信息
            UserNotice userNotice = userNoticeMapper.selectUserNoticeByNoticeId(noticeId);
            //获取用户id
            Long userId = userNotice.getUserId();
            if (userId == 0) {
                //发送给所有人
                noticeItem.setRecipientName("所有人");
            } else {
                //发送给某一具体用户
                SysUser user = sysUserMapper.selectUserById(userId);
                //获取用户名
                String userName = user.getUserName();
                noticeItem.setRecipientName(userName);
            }
        });
        return notices;
    }

    /**
     * 新增通知
     *
     * @param notice 通知
     * @return 结果
     */
    @Override
    public int insertNotice(Notice notice) {
        notice.setCreateTime(DateUtils.getNowDate());
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 修改通知
     *
     * @param notice 通知
     * @return 结果
     */
    @Override
    public int updateNotice(Notice notice) {
        notice.setUpdateTime(DateUtils.getNowDate());
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 批量删除通知
     *
     * @param noticeIds 需要删除的通知主键
     * @return 结果
     */
//    @Override
//    public int deleteNoticeByNoticeIds(Long[] noticeIds) {
//        return noticeMapper.deleteNoticeByNoticeIds(noticeIds);
//    }

    /**
     * 批量删除通知
     *
     * @param noticeIds 需要删除的通知主键
     * @return 结果
     */
    @Override
    public int deleteNoticeByNoticeIds(Long[] noticeIds) {
        //逻辑删除
        return noticeMapper.updateDeleteNoticeByNoticeIds(noticeIds);
    }

    /**
     * 删除通知信息
     *
     * @param noticeId 通知主键
     * @return 结果
     */
    @Override
    public int deleteNoticeByNoticeId(Long noticeId) {
        return noticeMapper.deleteNoticeByNoticeId(noticeId);
    }

    /**
     * 查询微信个人通知页面
     *
     * @return 结果
     */
    @Override
    public List<Notice> getPersonNoticeList() {
        //1、获取当前操作用户id
        Long userId = SecurityUtils.getUserId();
        /**
         * sql 语句
         * SELECT * FROM tb_notice n LEFT JOIN tb_user_notice
         * un ON un.notice_id = n.notice_id WHERE un.user_id = 1
         */
        return noticeMapper.selectPersonNoticeByUserId(userId);
    }

    /**
     * 返回未读通知数量
     *
     * @return 结果
     */
    @Override
    public AjaxResult getUnReadNoticeNumber() {
        //获取用户id
        Long userId = SecurityUtils.getUserId();
        //查询数量
        int count = noticeMapper.getNumberNoticeNotRead(userId);
        return AjaxResult.success(count);
    }
}
