package com.ruoyi.pet.mapper;

import java.util.List;
import com.ruoyi.pet.domain.Rescue;

/**
 * 救助登记Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-06
 */
public interface RescueMapper 
{
    /**
     * 查询救助登记
     * 
     * @param rescueId 救助登记主键
     * @return 救助登记
     */
    public Rescue selectRescueByRescueId(Long rescueId);

    /**
     * 查询救助登记列表
     * 
     * @param rescue 救助登记
     * @return 救助登记集合
     */
    public List<Rescue> selectRescueList(Rescue rescue);

    /**
     * 新增救助登记
     * 
     * @param rescue 救助登记
     * @return 结果
     */
    public int insertRescue(Rescue rescue);

    /**
     * 修改救助登记
     * 
     * @param rescue 救助登记
     * @return 结果
     */
    public int updateRescue(Rescue rescue);

    /**
     * 删除救助登记
     * 
     * @param rescueId 救助登记主键
     * @return 结果
     */
    public int deleteRescueByRescueId(Long rescueId);

    /**
     * 批量删除救助登记
     * 
     * @param rescueIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRescueByRescueIds(Long[] rescueIds);
}
