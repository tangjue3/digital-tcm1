package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.PrescriptionofdrugsMapper;
import com.ruoyi.system.domain.Prescriptionofdrugs;
import com.ruoyi.system.service.IPrescriptionofdrugsService;

/**
 * 药方Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
@Service
public class PrescriptionofdrugsServiceImpl implements IPrescriptionofdrugsService 
{
    @Autowired
    private PrescriptionofdrugsMapper prescriptionofdrugsMapper;

    /**
     * 查询药方
     * 
     * @param id 药方主键
     * @return 药方
     */
    @Override
    public Prescriptionofdrugs selectPrescriptionofdrugsById(Long id)
    {
        return prescriptionofdrugsMapper.selectPrescriptionofdrugsById(id);
    }

    /**
     * 查询药方列表
     * 
     * @param prescriptionofdrugs 药方
     * @return 药方
     */
    @Override
    public List<Prescriptionofdrugs> selectPrescriptionofdrugsList(Prescriptionofdrugs prescriptionofdrugs)
    {
        return prescriptionofdrugsMapper.selectPrescriptionofdrugsList(prescriptionofdrugs);
    }

    /**
     * 新增药方
     * 
     * @param prescriptionofdrugs 药方
     * @return 结果
     */
    @Override
    public int insertPrescriptionofdrugs(Prescriptionofdrugs prescriptionofdrugs)
    {
        return prescriptionofdrugsMapper.insertPrescriptionofdrugs(prescriptionofdrugs);
    }

    /**
     * 修改药方
     * 
     * @param prescriptionofdrugs 药方
     * @return 结果
     */
    @Override
    public int updatePrescriptionofdrugs(Prescriptionofdrugs prescriptionofdrugs)
    {
        return prescriptionofdrugsMapper.updatePrescriptionofdrugs(prescriptionofdrugs);
    }

    /**
     * 批量删除药方
     * 
     * @param ids 需要删除的药方主键
     * @return 结果
     */
    @Override
    public int deletePrescriptionofdrugsByIds(Long[] ids)
    {
        return prescriptionofdrugsMapper.deletePrescriptionofdrugsByIds(ids);
    }

    /**
     * 删除药方信息
     * 
     * @param id 药方主键
     * @return 结果
     */
    @Override
    public int deletePrescriptionofdrugsById(Long id)
    {
        return prescriptionofdrugsMapper.deletePrescriptionofdrugsById(id);
    }
}
