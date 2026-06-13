package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Prescriptionofdrugs;

/**
 * 药方Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
public interface PrescriptionofdrugsMapper 
{
    /**
     * 查询药方
     * 
     * @param id 药方主键
     * @return 药方
     */
    public Prescriptionofdrugs selectPrescriptionofdrugsById(Long id);

    /**
     * 查询药方列表
     * 
     * @param prescriptionofdrugs 药方
     * @return 药方集合
     */
    public List<Prescriptionofdrugs> selectPrescriptionofdrugsList(Prescriptionofdrugs prescriptionofdrugs);

    /**
     * 新增药方
     * 
     * @param prescriptionofdrugs 药方
     * @return 结果
     */
    public int insertPrescriptionofdrugs(Prescriptionofdrugs prescriptionofdrugs);

    /**
     * 修改药方
     * 
     * @param prescriptionofdrugs 药方
     * @return 结果
     */
    public int updatePrescriptionofdrugs(Prescriptionofdrugs prescriptionofdrugs);

    /**
     * 删除药方
     * 
     * @param id 药方主键
     * @return 结果
     */
    public int deletePrescriptionofdrugsById(Long id);

    /**
     * 批量删除药方
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePrescriptionofdrugsByIds(Long[] ids);
}
