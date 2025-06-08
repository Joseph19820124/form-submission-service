# API 使用示例

本文档提供了完整的 API 使用示例，包括请求和响应示例。

## 🚀 基础信息

- **Base URL**: `http://localhost:8080/api/v1/form-submissions`
- **Content-Type**: `application/json`
- **响应格式**: JSON

## 📝 表单提交 API

### 1. 创建新的表单提交

**请求:**
```http
POST /api/v1/form-submissions
Content-Type: application/json

{
  "name": "李小明",
  "email": "li.xiaoming@example.com",
  "address": "上海市浦东新区世纪大道100号",
  "phoneNumber": "+86-138-1234-5678",
  "comments": "我对贵公司的产品很感兴趣，希望能够详细了解一下。"
}
```

**响应 (201 Created):**
```json
{
  "id": 1,
  "name": "李小明",
  "email": "li.xiaoming@example.com",
  "address": "上海市浦东新区世纪大道100号",
  "phoneNumber": "+86-138-1234-5678",
  "comments": "我对贵公司的产品很感兴趣，希望能够详细了解一下。",
  "status": "PENDING",
  "createdAt": "2025-06-08T10:30:00Z",
  "updatedAt": "2025-06-08T10:30:00Z",
  "submittedByIp": "192.168.1.100"
}
```

### 2. 获取单个提交详情

**请求:**
```http
GET /api/v1/form-submissions/1
```

**响应 (200 OK):**
```json
{
  "id": 1,
  "name": "李小明",
  "email": "li.xiaoming@example.com",
  "address": "上海市浦东新区世纪大道100号",
  "phoneNumber": "+86-138-1234-5678",
  "comments": "我对贵公司的产品很感兴趣，希望能够详细了解一下。",
  "status": "PENDING",
  "createdAt": "2025-06-08T10:30:00Z",
  "updatedAt": "2025-06-08T10:30:00Z",
  "submittedByIp": "192.168.1.100"
}
```

## 📋 查询和筛选 API

### 3. 获取所有提交（分页）

**请求:**
```http
GET /api/v1/form-submissions?page=0&size=10&sortBy=createdAt&sortDirection=desc
```

**响应 (200 OK):**
```json
{
  "content": [
    {
      "id": 2,
      "name": "王小红",
      "email": "wang.xiaohong@example.com",
      "address": "北京市海淀区中关村大街1号",
      "phoneNumber": "+86-139-8765-4321",
      "comments": "希望了解更多技术细节",
      "status": "APPROVED",
      "createdAt": "2025-06-08T11:00:00Z",
      "updatedAt": "2025-06-08T11:30:00Z",
      "submittedByIp": "192.168.1.101"
    },
    {
      "id": 1,
      "name": "李小明",
      "email": "li.xiaoming@example.com",
      "address": "上海市浦东新区世纪大道100号",
      "phoneNumber": "+86-138-1234-5678",
      "comments": "我对贵公司的产品很感兴趣，希望能够详细了解一下。",
      "status": "PENDING",
      "createdAt": "2025-06-08T10:30:00Z",
      "updatedAt": "2025-06-08T10:30:00Z",
      "submittedByIp": "192.168.1.100"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 2
}
```

### 4. 按状态筛选

**请求:**
```http
GET /api/v1/form-submissions/status/PENDING?page=0&size=20
```

**响应:** 类似上面的分页响应，但只包含状态为 PENDING 的提交。

### 5. 搜索功能

#### 按姓名搜索
**请求:**
```http
GET /api/v1/form-submissions/search/name?name=李
```

#### 按地址搜索
**请求:**
```http
GET /api/v1/form-submissions/search/address?address=上海
```

#### 按电话号码搜索
**请求:**
```http
GET /api/v1/form-submissions/search/phone?phoneNumber=138-1234-5678
```

**搜索响应示例:**
```json
[
  {
    "id": 1,
    "name": "李小明",
    "email": "li.xiaoming@example.com",
    "address": "上海市浦东新区世纪大道100号",
    "phoneNumber": "+86-138-1234-5678",
    "comments": "我对贵公司的产品很感兴趣，希望能够详细了解一下。",
    "status": "PENDING",
    "createdAt": "2025-06-08T10:30:00Z",
    "updatedAt": "2025-06-08T10:30:00Z",
    "submittedByIp": "192.168.1.100"
  }
]
```

## ✏️ 更新和管理 API

### 6. 更新提交状态

**请求:**
```http
PATCH /api/v1/form-submissions/1/status
Content-Type: application/json

{
  "status": "APPROVED"
}
```

**响应 (200 OK):**
```json
{
  "id": 1,
  "name": "李小明",
  "email": "li.xiaoming@example.com",
  "address": "上海市浦东新区世纪大道100号",
  "phoneNumber": "+86-138-1234-5678",
  "comments": "我对贵公司的产品很感兴趣，希望能够详细了解一下。",
  "status": "APPROVED",
  "createdAt": "2025-06-08T10:30:00Z",
  "updatedAt": "2025-06-08T12:00:00Z",
  "submittedByIp": "192.168.1.100"
}
```

### 7. 更新整个提交

**请求:**
```http
PUT /api/v1/form-submissions/1
Content-Type: application/json

{
  "name": "李小明（已更新）",
  "email": "li.xiaoming@example.com",
  "address": "上海市浦东新区世纪大道200号",
  "phoneNumber": "+86-138-1234-5678",
  "comments": "已更新联系地址，请按新地址联系。"
}
```

### 8. 批量更新状态

**请求:**
```http
PATCH /api/v1/form-submissions/bulk-update
Content-Type: application/json

{
  "submissionIds": [1, 2, 3],
  "status": "PROCESSING"
}
```

**响应 (200 OK):**
```json
{
  "message": "Bulk update completed successfully"
}
```

### 9. 删除提交

**请求:**
```http
DELETE /api/v1/form-submissions/1
```

**响应 (204 No Content):** 无响应体

## 📊 统计和分析 API

### 10. 获取统计信息

**请求:**
```http
GET /api/v1/form-submissions/stats
```

**响应 (200 OK):**
```json
{
  "totalSubmissions": 150,
  "pendingSubmissions": 45,
  "approvedSubmissions": 80,
  "rejectedSubmissions": 20,
  "processingSubmissions": 5,
  "approvalRate": 53.33,
  "rejectionRate": 13.33
}
```

### 11. 获取最近提交

**请求:**
```http
GET /api/v1/form-submissions/recent?days=7
```

**响应:** 返回最近7天的提交数组。

## 🔄 Google Sheets 集成 API

### 12. 手动同步到 Google Sheets

**请求:**
```http
POST /api/v1/form-submissions/sync-to-sheets
```

**响应 (200 OK):**
```json
{
  "status": "success",
  "message": "所有数据已成功同步到 Google Sheets"
}
```

**错误响应 (500 Internal Server Error):**
```json
{
  "status": "error",
  "message": "同步失败: Connection timeout"
}
```

## 🚨 错误处理

### 验证错误 (400 Bad Request)

**请求:** (缺少必填字段)
```http
POST /api/v1/form-submissions
Content-Type: application/json

{
  "name": "",
  "email": "invalid-email"
}
```

**响应:**
```json
{
  "timestamp": "2025-06-08T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "name",
      "message": "Name is required"
    },
    {
      "field": "email",
      "message": "Invalid email format"
    },
    {
      "field": "address",
      "message": "Address is required"
    }
  ]
}
```

### 资源未找到 (404 Not Found)

**请求:**
```http
GET /api/v1/form-submissions/999
```

**响应:**
```json
{
  "timestamp": "2025-06-08T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Submission not found with id: 999"
}
```

### 邮箱重复 (400 Bad Request)

**请求:** (使用已存在的邮箱)
```http
POST /api/v1/form-submissions
Content-Type: application/json

{
  "name": "测试用户",
  "email": "li.xiaoming@example.com",
  "address": "测试地址"
}
```

**响应:**
```json
{
  "timestamp": "2025-06-08T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists: li.xiaoming@example.com"
}
```

## 💡 使用技巧

1. **分页查询**: 使用 `page` 和 `size` 参数控制返回数据量
2. **排序**: 使用 `sortBy` 和 `sortDirection` 自定义排序
3. **搜索**: 支持模糊搜索，自动忽略大小写
4. **状态管理**: 合理使用状态字段跟踪处理进度
5. **批量操作**: 对于大量数据操作，使用批量更新API提高效率

## 🔧 测试命令

使用 curl 命令测试 API：

```bash
# 创建提交
curl -X POST http://localhost:8080/api/v1/form-submissions \
  -H "Content-Type: application/json" \
  -d '{"name":"测试用户","email":"test@example.com","address":"测试地址"}'

# 获取所有提交
curl http://localhost:8080/api/v1/form-submissions

# 更新状态
curl -X PATCH http://localhost:8080/api/v1/form-submissions/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"APPROVED"}'

# 同步到 Google Sheets
curl -X POST http://localhost:8080/api/v1/form-submissions/sync-to-sheets
```

---

更多API使用问题，请参考项目文档或提交 Issue。