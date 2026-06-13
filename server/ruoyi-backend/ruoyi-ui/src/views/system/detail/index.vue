<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="所属案例" prop="caseNo">
        <el-select v-model="queryParams.caseNo" placeholder="请选择所属案例" clearable>
          <el-option
            v-for="dict in dict.type.tcm_case_no"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属模块" prop="module">
        <el-select v-model="queryParams.module" placeholder="请选择所属模块" clearable>
          <el-option
            v-for="dict in dict.type.tcm_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="题目名称" prop="questionsName">
        <el-input
          v-model="queryParams.questionsName"
          placeholder="请输入题目名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="选项A" prop="optionA">
        <el-input
          v-model="queryParams.optionA"
          placeholder="请输入选项A"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="选项B" prop="optionB">
        <el-input
          v-model="queryParams.optionB"
          placeholder="请输入选项B"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="选项C" prop="optionC">
        <el-input
          v-model="queryParams.optionC"
          placeholder="请输入选项C"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="选项D" prop="optionD">
        <el-input
          v-model="queryParams.optionD"
          placeholder="请输入选项D"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="答案" prop="questionsAnswer">
        <el-input
          v-model="queryParams.questionsAnswer"
          placeholder="请输入答案"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="图片、视频、语言文件url" prop="imageUrl">
        <el-input
          v-model="queryParams.imageUrl"
          placeholder="请输入图片、视频、语言文件url"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="urlType">
        <el-select v-model="queryParams.urlType" placeholder="请选择类型" clearable>
          <el-option
            v-for="dict in dict.type.url_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="扩展字段1" prop="extend1">
        <el-input
          v-model="queryParams.extend1"
          placeholder="请输入扩展字段1"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="扩展字段2" prop="extend2">
        <el-input
          v-model="queryParams.extend2"
          placeholder="请输入扩展字段2"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学生答案" prop="answer">
        <el-input
          v-model="queryParams.answer"
          placeholder="请输入学生答案"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实验唯一编码" prop="questionNo">
        <el-input
          v-model="queryParams.questionNo"
          placeholder="请输入实验唯一编码"
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
          v-hasPermi="['system:detail:add']"
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
          v-hasPermi="['system:detail:edit']"
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
          v-hasPermi="['system:detail:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:detail:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="detailList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="所属案例" align="center" prop="caseNo">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.tcm_case_no" :value="scope.row.caseNo"/>
        </template>
      </el-table-column>
      <el-table-column label="所属模块" align="center" prop="module">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.tcm_type" :value="scope.row.module"/>
        </template>
      </el-table-column>
      <el-table-column label="题目名称" align="center" prop="questionsName" />
      <el-table-column label="选项A" align="center" prop="optionA" />
      <el-table-column label="选项B" align="center" prop="optionB" />
      <el-table-column label="选项C" align="center" prop="optionC" />
      <el-table-column label="选项D" align="center" prop="optionD" />
      <el-table-column label="答案" align="center" prop="questionsAnswer" />
      <el-table-column label="图片、视频、语言文件url" align="center" prop="imageUrl" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="类型" align="center" prop="urlType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.url_type" :value="scope.row.urlType"/>
        </template>
      </el-table-column>
      <el-table-column label="扩展字段1" align="center" prop="extend1" />
      <el-table-column label="扩展字段2" align="center" prop="extend2" />
      <el-table-column label="学生答案" align="center" prop="answer" />
      <el-table-column label="实验唯一编码" align="center" prop="questionNo" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:detail:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:detail:remove']"
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

    <!-- 添加或修改案例实验详细对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="所属案例" prop="caseNo">
          <el-select v-model="form.caseNo" placeholder="请选择所属案例">
            <el-option
              v-for="dict in dict.type.tcm_case_no"
              :key="dict.value"
              :label="dict.label"
:value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属模块" prop="module">
          <el-select v-model="form.module" placeholder="请选择所属模块">
            <el-option
              v-for="dict in dict.type.tcm_type"
              :key="dict.value"
              :label="dict.label"
:value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题目名称" prop="questionsName">
          <el-input v-model="form.questionsName" placeholder="请输入题目名称" />
        </el-form-item>
        <el-form-item label="选项A" prop="optionA">
          <el-input v-model="form.optionA" placeholder="请输入选项A" />
        </el-form-item>
        <el-form-item label="选项B" prop="optionB">
          <el-input v-model="form.optionB" placeholder="请输入选项B" />
        </el-form-item>
        <el-form-item label="选项C" prop="optionC">
          <el-input v-model="form.optionC" placeholder="请输入选项C" />
        </el-form-item>
        <el-form-item label="选项D" prop="optionD">
          <el-input v-model="form.optionD" placeholder="请输入选项D" />
        </el-form-item>
        <el-form-item label="答案" prop="questionsAnswer">
          <el-input v-model="form.questionsAnswer" placeholder="请输入答案" />
        </el-form-item>
        <el-form-item label="图片、视频、语言文件url" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入图片、视频、语言文件url" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="类型" prop="urlType">
          <el-select v-model="form.urlType" placeholder="请选择类型">
            <el-option
              v-for="dict in dict.type.url_type"
              :key="dict.value"
              :label="dict.label"
:value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="扩展字段1" prop="extend1">
          <el-input v-model="form.extend1" placeholder="请输入扩展字段1" />
        </el-form-item>
        <el-form-item label="扩展字段2" prop="extend2">
          <el-input v-model="form.extend2" placeholder="请输入扩展字段2" />
        </el-form-item>
        <el-form-item label="学生答案" prop="answer">
          <el-input v-model="form.answer" placeholder="请输入学生答案" />
        </el-form-item>
        <el-form-item label="实验唯一编码" prop="questionNo">
          <el-input v-model="form.questionNo" placeholder="请输入实验唯一编码" />
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
import { listDetail, getDetail, delDetail, addDetail, updateDetail } from "@/api/system/detail";

export default {
  name: "Detail",
  dicts: ['tcm_type', 'url_type', 'tcm_case_no'],
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
      // 案例实验详细表格数据
      detailList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        caseNo: null,
        module: null,
        questionsName: null,
        optionA: null,
        optionB: null,
        optionC: null,
        optionD: null,
        questionsAnswer: null,
        imageUrl: null,
        urlType: null,
        extend1: null,
        extend2: null,
        answer: null,
        questionNo: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询案例实验详细列表 */
    getList() {
      this.loading = true;
      listDetail(this.queryParams).then(response => {
        this.detailList = response.rows;
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
        caseNo: null,
        module: null,
        questionsName: null,
        optionA: null,
        optionB: null,
        optionC: null,
        optionD: null,
        questionsAnswer: null,
        imageUrl: null,
        remark: null,
        urlType: null,
        extend1: null,
        extend2: null,
        answer: null,
        questionNo: null
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
      this.title = "添加案例实验详细";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDetail(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改案例实验详细";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDetail(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDetail(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除案例实验详细编号为"' + ids + '"的数据项？').then(function() {
        return delDetail(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/detail/export', {
        ...this.queryParams
      }, `detail_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
