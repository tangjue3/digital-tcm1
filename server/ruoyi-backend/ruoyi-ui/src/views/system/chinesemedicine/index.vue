<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="别名" prop="otherName">
        <el-input
          v-model="queryParams.otherName"
          placeholder="请输入别名"
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
          v-hasPermi="['system:chinesemedicine:add']"
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
          v-hasPermi="['system:chinesemedicine:edit']"
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
          v-hasPermi="['system:chinesemedicine:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:chinesemedicine:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="chinesemedicineList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="中药名" align="center" prop="name" />
      <el-table-column label="照片地址" align="center" prop="imageurl" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.imageurl" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="别名" align="center" prop="otherName" />
      <el-table-column label="来源" align="center" prop="source" />
      <el-table-column label="形态" align="center" prop="xingtai" />
      <el-table-column label="产地" align="center" prop="chandi" />
      <el-table-column label="形状" align="center" prop="xingzhuang" />
      <el-table-column label="性味归经" align="center" prop="xingweiguijing" />
      <el-table-column label="功效" align="center" prop="gongxiao" />
      <el-table-column label="临床作用" align="center" prop="linchuangzuoyong" />
      <el-table-column label="药理研究" align="center" prop="yaoliyanjiu" />
      <el-table-column label="化学组成" align="center" prop="huaxuezucheng" />
      <el-table-column label="使用禁忌" align="center" prop="jinji" />
      <el-table-column label="药物配方" align="center" prop="yaowupeifang" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:chinesemedicine:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:chinesemedicine:remove']"
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

    <!-- 添加或修改中药材对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="中药名" prop="name">
          <el-input v-model="form.name" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="照片地址">
          <image-upload v-model="form.imageurl"/>
        </el-form-item>
        <el-form-item label="别名" prop="otherName">
          <el-input v-model="form.otherName" placeholder="请输入别名" />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input v-model="form.source" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="形态" prop="xingtai">
          <el-input v-model="form.xingtai" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="产地" prop="chandi">
          <el-input v-model="form.chandi" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="形状" prop="xingzhuang">
          <el-input v-model="form.xingzhuang" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="性味归经" prop="xingweiguijing">
          <el-input v-model="form.xingweiguijing" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="功效" prop="gongxiao">
          <el-input v-model="form.gongxiao" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="临床作用" prop="linchuangzuoyong">
          <el-input v-model="form.linchuangzuoyong" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="药理研究" prop="yaoliyanjiu">
          <el-input v-model="form.yaoliyanjiu" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="化学组成" prop="huaxuezucheng">
          <el-input v-model="form.huaxuezucheng" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="使用禁忌" prop="jinji">
          <el-input v-model="form.jinji" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="药物配方" prop="yaowupeifang">
          <el-input v-model="form.yaowupeifang" type="textarea" placeholder="请输入内容" />
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
import { listChinesemedicine, getChinesemedicine, delChinesemedicine, addChinesemedicine, updateChinesemedicine } from "@/api/system/chinesemedicine";

export default {
  name: "Chinesemedicine",
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
      // 中药材表格数据
      chinesemedicineList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        imageurl: null,
        otherName: null,
        source: null,
        xingtai: null,
        chandi: null,
        xingzhuang: null,
        xingweiguijing: null,
        gongxiao: null,
        linchuangzuoyong: null,
        yaoliyanjiu: null,
        huaxuezucheng: null,
        jinji: null,
        yaowupeifang: null
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
    /** 查询中药材列表 */
    getList() {
      this.loading = true;
      listChinesemedicine(this.queryParams).then(response => {
        this.chinesemedicineList = response.rows;
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
        name: null,
        imageurl: null,
        otherName: null,
        source: null,
        xingtai: null,
        chandi: null,
        xingzhuang: null,
        xingweiguijing: null,
        gongxiao: null,
        linchuangzuoyong: null,
        yaoliyanjiu: null,
        huaxuezucheng: null,
        jinji: null,
        yaowupeifang: null
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
      this.title = "添加中药材";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getChinesemedicine(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改中药材";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateChinesemedicine(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addChinesemedicine(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除中药材编号为"' + ids + '"的数据项？').then(function() {
        return delChinesemedicine(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/chinesemedicine/export', {
        ...this.queryParams
      }, `chinesemedicine_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
