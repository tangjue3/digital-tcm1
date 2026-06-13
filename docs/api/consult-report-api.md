# 问诊报告接口文档

## 1. 模块说明

本模块用于打通 App 问诊报告提交、后端数据库持久化，以及管理端读取统计与列表数据的第一阶段链路。

当前阶段范围：

- App 端正式提交问诊报告走真实接口
- 后端保存报告主表、病种表、附件表
- 管理端通过接口读取 dashboard、紧急关注、分页列表、详情、状态更新
- 管理端页面 UI 暂不改动，仅提供 API 封装供前端队友对接

当前阶段不包含：

- AI 自动诊断
- 首次强制资料采集恢复
- 管理端页面重构
- 复杂工作流审批

## 2. 数据表说明

执行 SQL 文件：

- `database/sql/tcm_consult_report.sql`

新增数据表：

- `tcm_consult_report`：问诊报告主表
- `tcm_consult_report_disease`：问诊报告病种表
- `tcm_consult_report_attachment`：问诊报告附件表

表设计重点：

- `report_no` 唯一索引
- `report_id` 普通索引
- `status`、`highest_risk_level`、`create_time` 索引
- 使用 `InnoDB` + `utf8mb4`

## 3. 枚举说明

### 3.1 status

| 值 | 含义 |
| --- | --- |
| `pending` | 待处理 |
| `processing` | 处理中 |
| `supplement_required` | 需补充 |
| `completed` | 已完成 |
| `rejected` | 已驳回 |

### 3.2 riskLevel

| 值 | 含义 |
| --- | --- |
| `normal` | 普通 |
| `medium` | 建议关注 |
| `high` | 高风险 |
| `critical` | 极高风险 |

### 3.3 highRisk 统计规则

- `highest_risk_level in ('high', 'critical')`
- 且 `status in ('pending', 'processing', 'supplement_required')`

### 3.4 urgent 列表排序规则

1. `critical` 优先于 `high`
2. 同级风险按 `create_time desc`
3. 再按 `id desc`

## 4. App 接口列表

> App 端接口路径不自动加 `/system` 前缀，H5 下通过 `/api` 代理访问。

### 4.1 提交问诊报告

- URL：`POST /app/consult/report`
- 说明：App 用户正式提交问诊报告

请求示例：

```json
{
  "patientName": "张三",
  "gender": "男",
  "age": 21,
  "visitType": "初诊",
  "basicDisease": "无",
  "allergyHistoryType": "无",
  "allergyHistoryDetail": "",
  "pastHistoryType": "无",
  "pastHistoryDetail": "",
  "currentMedicationType": "无",
  "currentMedicationDetail": "",
  "selectedDiseases": [
    {
      "code": "A03.06.01.09",
      "name": "腕舟骨骨折",
      "path": "外伤/中毒/意外 > 创伤类病 > 骨折病 > 腕舟骨骨折",
      "riskLevel": "high",
      "riskReason": "骨折相关情况，建议由医生进一步评估，必要时尽快线下就医"
    }
  ],
  "chiefComplaint": "右手腕疼痛两周，活动时加重。",
  "duration": "2周",
  "severityLevel": 7,
  "suddenWorse": "否",
  "lifeImpact": ["工作/学习", "活动受限"],
  "attachments": []
}
```

校验规则：

- 必填：`patientName`、`gender`、`age`、`visitType`
- `selectedDiseases` 当前允许为空，但建议前端至少选 1 项
- `attachments` 当前可为空

后端处理规则：

- 自动生成 `reportNo`，格式：`TCM + yyyyMMddHHmmss + 3位随机数`
- 自动计算 `highestRiskLevel`
- 默认状态：`pending`

返回示例：

```json
{
  "code": 200,
  "msg": "提交成功",
  "data": {
    "reportId": 1,
    "reportNo": "TCM202604291830001",
    "status": "pending"
  }
}
```

### 4.2 我的报告列表

- URL：`GET /app/consult/report/my`
- 说明：返回当前登录用户的报告分页列表

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | number | 否 | 默认走 RuoYi 分页 |
| `pageSize` | number | 否 | 默认走 RuoYi 分页 |
| `status` | string | 否 | 按状态筛选 |

返回字段：

- `reportId`
- `reportNo`
- `patientName`
- `gender`
- `age`
- `chiefComplaint`
- `highestRiskLevel`
- `status`
- `createTime`
- `diseaseSummary`

返回示例：

```json
{
  "code": 200,
  "rows": [
    {
      "reportId": 1,
      "reportNo": "TCM202604291830001",
      "patientName": "张三",
      "gender": "男",
      "age": 21,
      "chiefComplaint": "右手腕疼痛两周，活动时加重。",
      "diseaseSummary": "右手腕疼痛两周，活动时加重。",
      "highestRiskLevel": "high",
      "status": "pending",
      "createTime": "2026-04-29 18:30:00"
    }
  ],
  "total": 1
}
```

### 4.3 报告详情

- URL：`GET /app/consult/report/{reportId}`
- 说明：返回当前登录用户自己的报告详情

权限说明：

- 当前后端已按 `userId` 做“仅本人可查看”的校验
- 如果历史数据缺少 `userId`，需后续按真实 App 用户体系补充兜底策略

返回字段：

- 主表信息
- `diseaseList`
- `attachmentList`

## 5. 管理端接口列表

> 管理端接口通过 RuoYi 权限注解保护，前端队友只需调用新增 API 文件，不需要改动页面布局。

### 5.1 dashboard 统计

- URL：`GET /system/consult/report/dashboard`
- 权限：`system:consultReport:list`

返回示例：

```json
{
  "code": 200,
  "data": {
    "pendingCount": 12,
    "todayVisitCount": 45,
    "highRiskCount": 3
  }
}
```

统计规则：

- `pendingCount`：`status = pending`
- `todayVisitCount`：`date(create_time) = curdate()`
- `highRiskCount`：高风险且未完成

### 5.2 紧急关注列表

- URL：`GET /system/consult/report/urgent`
- 权限：`system:consultReport:list`

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `limit` | number | 否 | 默认 5 |

返回示例：

```json
{
  "code": 200,
  "data": [
    {
      "reportId": 1,
      "reportNo": "TCM202604291830001",
      "patientName": "李四",
      "diseaseSummary": "肝火上炎证，血压偏高",
      "highestRiskLevel": "high",
      "createTime": "2026-04-29 18:30:00"
    }
  ]
}
```

summary 生成规则：

- 优先 `chiefComplaint`
- 否则取病种前 2 项拼接
- 都为空时返回“暂无补充说明”

### 5.3 报告分页列表

- URL：`GET /system/consult/report/list`
- 权限：`system:consultReport:list`

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | number | 否 | 页码 |
| `pageSize` | number | 否 | 每页条数 |
| `status` | string | 否 | 状态筛选 |
| `highestRiskLevel` | string | 否 | 风险等级筛选 |
| `patientName` | string | 否 | 患者姓名模糊搜索 |
| `startTime` | string | 否 | 开始时间 |
| `endTime` | string | 否 | 结束时间 |

返回为 RuoYi 标准分页格式：

```json
{
  "code": 200,
  "rows": [
    {
      "reportId": 1,
      "reportNo": "TCM202604291830001",
      "patientName": "张三",
      "gender": "男",
      "age": 21,
      "chiefComplaint": "右手腕疼痛两周，活动时加重。",
      "diseaseSummary": "右手腕疼痛两周，活动时加重。",
      "highestRiskLevel": "high",
      "status": "pending",
      "createTime": "2026-04-29 18:30:00"
    }
  ],
  "total": 1
}
```

### 5.4 报告详情

- URL：`GET /system/consult/report/{reportId}`
- 权限：`system:consultReport:query`

返回内容：

- 主表信息
- `diseaseList`
- `attachmentList`

### 5.5 更新报告状态

- URL：`PUT /system/consult/report/{reportId}/status`
- 权限：`system:consultReport:edit`

请求示例 1：

```json
{
  "status": "processing"
}
```

请求示例 2：

```json
{
  "status": "completed",
  "doctorAdvice": "建议线下进一步检查",
  "doctorSummary": "患者自述右手腕疼痛两周，活动时加重。",
  "suggestedDepartment": "骨伤科",
  "needOfflineVisit": true,
  "needSupplement": false
}
```

说明：

- 第一版仅实现状态和医生建议类字段更新
- 不包含复杂流转与审计链路

## 6. 管理端前端对接说明

新增 API 文件：

- `apps/admin-web/src/api/system/consultReport.js`

包含方法：

- `getConsultReportDashboard()`
- `getUrgentConsultReports(params)`
- `listConsultReports(params)`
- `getConsultReport(reportId)`
- `updateConsultReportStatus(reportId, data)`

建议前端队友只改页面里的数据源，不要重做布局。

## 7. App 端对接说明

App 端真实提交接口封装文件：

- `apps/mobile-app/common/consultReport.js`

已实现方法：

- `submitConsultReport(data)`
- `getMyConsultReports(params)`
- `getConsultReportDetail(reportId)`

说明：

- 提交前仍由 App 页面做必填校验
- 提交成功后使用后端返回的 `reportId / reportNo / status`
- 列表页与详情页已切换为后端接口读取
- 当前 App 提交模块已支持“若存在附件则先调用 `/common/upload` 自动上传，再把 `fileUrl` 带入报告提交体”
- 当前你们保留的 4 步 UI 流程没有重新展示附件步骤，因此默认仍可无附件提交
- 后续如恢复附件入口，无需改后端表结构，只需把页面表单里的 `attachments` 传入即可

## 8. 菜单权限说明

后续需要在 RuoYi 菜单/权限配置中补充：

- `system:consultReport:list`
- `system:consultReport:query`
- `system:consultReport:edit`

当前接口已加权限注解，但菜单与按钮权限需由后台配置或数据初始化脚本补齐。

## 9. 手工测试示例

### 9.1 App 提交普通报告

```bash
curl -X POST "http://localhost:8080/app/consult/report" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <APP_TOKEN>" \
  -d '{
    "patientName": "张三",
    "gender": "男",
    "age": 21,
    "visitType": "初诊",
    "selectedDiseases": [
      {
        "code": "A17.01",
        "name": "头痛",
        "path": "常见症状 > 头痛",
        "riskLevel": "normal",
        "riskReason": ""
      }
    ],
    "chiefComplaint": "近三天头痛，休息后略缓解。",
    "duration": "3天",
    "severityLevel": 4,
    "suddenWorse": "否",
    "lifeImpact": ["工作/学习"],
    "attachments": []
  }'
```

### 9.2 App 提交高风险报告

```bash
curl -X POST "http://localhost:8080/app/consult/report" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <APP_TOKEN>" \
  -d '{
    "patientName": "李四",
    "gender": "男",
    "age": 38,
    "visitType": "初诊",
    "selectedDiseases": [
      {
        "code": "A03.06.01.01",
        "name": "开放性骨折",
        "path": "外伤/中毒/意外 > 创伤类病 > 骨折病 > 开放性骨折",
        "riskLevel": "critical",
        "riskReason": "该类情况可能需要尽快线下就医，请及时联系医生。该提示仅用于分诊参考，不代表最终诊断。"
      }
    ],
    "chiefComplaint": "外伤后伤口出血并伴剧烈疼痛。",
    "duration": "今天",
    "severityLevel": 9,
    "suddenWorse": "是",
    "lifeImpact": ["活动受限"],
    "attachments": []
  }'
```

### 9.3 管理端 dashboard

```bash
curl -X GET "http://localhost:8080/system/consult/report/dashboard" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### 9.4 管理端 urgent

```bash
curl -X GET "http://localhost:8080/system/consult/report/urgent?limit=5" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### 9.5 管理端分页列表

```bash
curl -X GET "http://localhost:8080/system/consult/report/list?pageNum=1&pageSize=10&status=pending" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### 9.6 管理端详情

```bash
curl -X GET "http://localhost:8080/system/consult/report/1" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

## 10. 后续建议

- 附件上传恢复时直接复用 `/common/upload`
- 如果 App 用户体系后续拆分独立表，可把 `user_id` 校验逻辑同步收紧
- 管理端页面队友只需接入新增 API，无需改动现有 UI 结构
