<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label=" 穴位名称" prop="acupointName">
        <el-input
          v-model="queryParams.acupointName"
          placeholder="请输入 穴位名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属经络名称" prop="meridianName">
        <el-input
          v-model="queryParams.meridianName"
          placeholder="请输入所属经络名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="针刺深度" prop="acupunctureDepth">
        <el-input
          v-model="queryParams.acupunctureDepth"
          placeholder="请输入针刺深度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="艾灸时间" prop="moxibustionTime">
        <el-input
          v-model="queryParams.moxibustionTime"
          placeholder="请输入艾灸时间"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:acupoints:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:acupoints:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:acupoints:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:acupoints:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="acupointsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label=" 穴位名称" align="center" prop="acupointName" />
      <el-table-column label="所属经络名称" align="center" prop="meridianName" />
      <el-table-column label="穴位位置描述" align="center" prop="locationDescription" />
      <el-table-column label="穴位功能描述" align="center" prop="functionDescription" />
      <el-table-column label="常见用途" align="center" prop="commonUsage" />
      <el-table-column label="穴位相关图片的 URL" align="center" prop="pictureUrl" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.pictureUrl" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="针刺深度" align="center" prop="acupunctureDepth" />
      <el-table-column label="艾灸时间" align="center" prop="moxibustionTime" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:acupoints:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:acupoints:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改人体穴位对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label=" 穴位名称" prop="acupointName">
          <el-input v-model="form.acupointName" placeholder="请输入 穴位名称" />
        </el-form-item>
        <el-form-item label="所属经络名称" prop="meridianName">
          <el-input v-model="form.meridianName" placeholder="请输入所属经络名称" />
        </el-form-item>
        <el-form-item label="穴位位置描述">
          <editor v-model="form.locationDescription" :min-height="192"/>
        </el-form-item>
        <el-form-item label="穴位功能描述">
          <editor v-model="form.functionDescription" :min-height="192"/>
        </el-form-item>
        <el-form-item label="常见用途">
          <editor v-model="form.commonUsage" :min-height="192"/>
        </el-form-item>
        <el-form-item label="穴位相关图片的 URL">
          <image-upload v-model="form.pictureUrl"/>
        </el-form-item>
        <el-form-item label="针刺深度" prop="acupunctureDepth">
          <el-input v-model="form.acupunctureDepth" placeholder="请输入针刺深度" />
        </el-form-item>
        <el-form-item label="艾灸时间" prop="moxibustionTime">
          <el-input v-model="form.moxibustionTime" placeholder="请输入艾灸时间" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAcupoints, getAcupoints, delAcupoints, addAcupoints, updateAcupoints } from "@/api/system/acupoints";

export default {
  name: "Acupoints",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 人体穴位表格数据
      acupointsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        acupointName: null,
        meridianName: null,
        locationDescription: null,
        functionDescription: null,
        commonUsage: null,
        pictureUrl: null,
        acupunctureDepth: null,
        moxibustionTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        acupointName: [
          { required: true, message: " 穴位名称不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询人体穴位列表 */
    getList() {
      this.loading = true;
      listAcupoints(this.queryParams).then(response => {
        this.acupointsList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        acupointName: null,
        meridianName: null,
        locationDescription: null,
        functionDescription: null,
        commonUsage: null,
        pictureUrl: null,
        acupunctureDepth: null,
        moxibustionTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加人体穴位";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAcupoints(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改人体穴位";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAcupoints(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAcupoints(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除人体穴位编号为"' + ids + '"的数据项？').then(function() {
        return delAcupoints(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/acupoints/export', {
        ...this.queryParams
      }, `acupoints_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
