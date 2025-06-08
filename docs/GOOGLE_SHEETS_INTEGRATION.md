# Google Sheets 集成指南

本文档详细介绍如何配置和使用 Google Sheets 集成功能。

## 📊 电子表格信息

- **电子表格名称**: Form Submissions Data
- **电子表格 ID**: `18ea2cQeXOt96dGbssCP_ynsTP5AHEmQpFXE48-PtToo`
- **访问链接**: [点击打开 Google Sheets](https://docs.google.com/spreadsheets/d/18ea2cQeXOt96dGbssCP_ynsTP5AHEmQpFXE48-PtToo/edit)

## 📋 数据结构

Google Sheets 中的列结构：

| 列 | 字段名 | 描述 | 来源 |
|---|---|---|---|
| A | ID | 唯一标识符 | 自动生成 |
| B | Name | 姓名 | 表单字段 |
| C | Email | 邮箱地址 | 表单字段 |
| D | Address | 地址 | 表单字段 |
| E | Phone Number | 电话号码 | 表单字段 |
| F | Comments | 评论/留言 | 表单字段 |
| G | Status | 处理状态 | 系统管理 |
| H | Submitted Date | 提交日期 | 自动记录 |
| I | IP Address | 提交者IP | 自动记录 |

## 🔄 同步机制

### 自动同步
- **新提交**: 每当有新的表单提交时，数据会自动写入 Google Sheets
- **状态更新**: 当提交状态发生变化时，Google Sheets 中的状态也会自动更新
- **批量操作**: 批量状态更新也会同步到 Google Sheets

### 手动同步
```http
POST /api/v1/form-submissions/sync-to-sheets
```

**响应示例**:
```json
{
  "status": "success",
  "message": "所有数据已成功同步到 Google Sheets"
}
```

## ⚙️ 配置设置

在 `application.yml` 中配置 Google Sheets 参数：

```yaml
google:
  sheets:
    spreadsheet-id: 18ea2cQeXOt96dGbssCP_ynsTP5AHEmQpFXE48-PtToo
    worksheet-name: Sheet1
    auto-sync: true
    batch-size: 100
```

### 配置参数说明

- `spreadsheet-id`: Google Sheets 电子表格的唯一标识符
- `worksheet-name`: 工作表名称 (默认: Sheet1)
- `auto-sync`: 是否启用自动同步 (true/false)
- `batch-size`: 批量同步时的批次大小

## 🚀 使用示例

### 1. 提交新表单 (自动同步到 Google Sheets)

```bash
curl -X POST http://localhost:8080/api/v1/form-submissions \
  -H "Content-Type: application/json" \
  -d '{
    "name": "张三",
    "email": "zhangsan@example.com",
    "address": "北京市朝阳区某某街道123号",
    "phoneNumber": "+86-138-0000-0000",
    "comments": "请尽快联系我"
  }'
```

**结果**: 数据同时保存到数据库和 Google Sheets

### 2. 更新提交状态 (自动同步状态)

```bash
curl -X PATCH http://localhost:8080/api/v1/form-submissions/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "APPROVED"
  }'
```

**结果**: 数据库和 Google Sheets 中的状态都会更新

### 3. 手动全量同步

```bash
curl -X POST http://localhost:8080/api/v1/form-submissions/sync-to-sheets
```

**结果**: 将所有数据库中的数据同步到 Google Sheets

## 📈 实时数据监控

### 在 Google Sheets 中查看实时数据

1. 打开电子表格：https://docs.google.com/spreadsheets/d/18ea2cQeXOt96dGbssCP_ynsTP5AHEmQpFXE48-PtToo/edit
2. 查看所有提交的实时数据
3. 可以直接在 Google Sheets 中进行数据分析、筛选和图表制作

### 状态统计

Google Sheets 中可以使用公式来统计各种状态：

```
=COUNTIF(G:G,"PENDING")    // 统计待处理数量
=COUNTIF(G:G,"APPROVED")   // 统计已批准数量
=COUNTIF(G:G,"REJECTED")   // 统计已拒绝数量
```

## 🔧 高级功能

### 1. 数据验证和清理

在 Google Sheets 中可以设置：
- 数据验证规则
- 条件格式化
- 自动填充公式

### 2. 协作功能

- 多人实时编辑和查看
- 评论和建议功能
- 版本历史记录

### 3. 自动化工作流

使用 Google Apps Script 可以创建：
- 自动邮件通知
- 数据处理脚本
- 与其他 Google 服务集成

## 🛠️ 故障排除

### 常见问题

**Q1: 数据没有自动同步到 Google Sheets**
- 检查 `google.sheets.auto-sync` 配置是否为 `true`
- 查看应用日志是否有错误信息
- 确认 Google Sheets API 权限

**Q2: 同步速度较慢**
- 调整 `google.sheets.batch-size` 参数
- 检查网络连接
- 考虑使用批量同步而非实时同步

**Q3: 无法访问 Google Sheets**
- 确认电子表格链接是否正确
- 检查 Google 账户权限
- 确认电子表格未被删除

### 日志监控

查看同步相关日志：
```bash
# 查看应用日志
tail -f logs/application.log | grep "Google Sheets"

# 查看同步状态
curl http://localhost:8080/actuator/health
```

## 📊 数据分析建议

### 1. 在 Google Sheets 中创建仪表板

```
=SPARKLINE(H2:H100)  // 提交趋势图
=AVERAGE(...)        // 平均处理时间
=MAX(H:H)           // 最新提交时间
```

### 2. 导出和备份

- 定期下载 Excel 格式备份
- 使用 Google Drive API 自动备份
- 设置数据保留策略

### 3. 集成其他工具

- 连接到 Google Data Studio 创建报表
- 使用 Zapier 连接其他系统
- 通过 API 与 CRM 系统集成

## 🔐 安全注意事项

1. **访问权限**: 确保只有授权人员可以访问电子表格
2. **数据隐私**: 敏感信息考虑加密或脱敏处理
3. **备份策略**: 定期备份重要数据
4. **审计日志**: 记录所有数据访问和修改操作

## 📞 技术支持

如遇到问题，请：
1. 检查应用日志
2. 验证 Google Sheets 配置
3. 测试网络连接
4. 提交 GitHub Issue

---

**提示**: 这个集成系统让您可以同时享受数据库的高性能和 Google Sheets 的协作便利性！