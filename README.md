# Form Submission Service

基于Google表单的Java Spring Boot后端服务，支持表单提交处理和Google Sheets集成。

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![Google Sheets](https://img.shields.io/badge/Google%20Sheets-集成-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🎯 项目简介

这是一个完整的表单提交处理系统，基于您的Google表单字段设计，提供：

- **表单数据管理**: 完整的CRUD操作
- **Google Sheets集成**: 实时数据同步
- **状态管理**: 提交状态跟踪和更新
- **数据验证**: 完整的输入验证
- **REST API**: 标准RESTful接口

## 📋 表单字段

系统支持以下字段（基于您的Google表单）：

- **Name** (必填) - 姓名
- **Email** (必填) - 邮箱地址
- **Address** (必填) - 地址
- **Phone Number** (可选) - 电话号码
- **Comments** (可选) - 评论/留言

## 🚀 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- 数据库 (H2/MySQL/PostgreSQL)

### 安装和运行

```bash
# 克隆项目
git clone https://github.com/Joseph19820124/form-submission-service.git
cd form-submission-service

# 编译和运行
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/form-submission-service-1.0.0.jar
```

应用将在 `http://localhost:8080` 启动

### 访问控制台

- **H2 数据库控制台**: http://localhost:8080/h2-console
- **健康检查**: http://localhost:8080/actuator/health
- **API文档**: http://localhost:8080/api/v1/form-submissions

## 📊 Google Sheets 集成

### 电子表格信息

- **表格名称**: Form Submissions Data
- **表格ID**: `18ea2cQeXOt96dGbssCP_ynsTP5AHEmQpFXE48-PtToo`
- **访问链接**: [点击打开Google Sheets](https://docs.google.com/spreadsheets/d/18ea2cQeXOt96dGbssCP_ynsTP5AHEmQpFXE48-PtToo/edit)

### 自动同步功能

- ✅ 新表单提交 → 自动写入Google Sheets
- ✅ 状态更新 → 实时同步状态
- ✅ 批量操作 → 批量状态更新
- ✅ 手动同步 → 一键全量同步

## 📚 API 文档

### 基础URL
```
http://localhost:8080/api/v1/form-submissions
```

### 主要端点

#### 1. 提交表单
```http
POST /api/v1/form-submissions
Content-Type: application/json

{
  "name": "张三",
  "email": "zhangsan@example.com",
  "address": "北京市朝阳区xx街道xx号",
  "phoneNumber": "+86-138-0000-0000",
  "comments": "希望能尽快联系我"
}
```

#### 2. 获取所有提交
```http
GET /api/v1/form-submissions?page=0&size=20&sortBy=createdAt&sortDirection=desc
```

#### 3. 根据ID获取提交
```http
GET /api/v1/form-submissions/{id}
```

#### 4. 更新提交状态
```http
PATCH /api/v1/form-submissions/{id}/status
Content-Type: application/json

{
  "status": "APPROVED"
}
```

#### 5. 搜索功能
```http
# 按姓名搜索
GET /api/v1/form-submissions/search/name?name=张三

# 按地址搜索
GET /api/v1/form-submissions/search/address?address=北京

# 按电话搜索
GET /api/v1/form-submissions/search/phone?phoneNumber=138-0000-0000
```

#### 6. 同步到Google Sheets
```http
POST /api/v1/form-submissions/sync-to-sheets
```

#### 7. 获取统计信息
```http
GET /api/v1/form-submissions/stats
```

### 响应状态

系统支持以下提交状态：

- `PENDING` - 待处理 (默认状态)
- `APPROVED` - 已批准
- `REJECTED` - 已拒绝
- `PROCESSING` - 处理中

## 🔧 配置说明

### 数据库配置

#### H2 数据库 (开发环境)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
```

#### MySQL 配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/form_submissions
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: your_username
    password: your_password
```

### Google Sheets 配置
```yaml
google:
  sheets:
    spreadsheet-id: 18ea2cQeXOt96dGbssCP_ynsTP5AHEmQpFXE48-PtToo
    worksheet-name: Sheet1
    auto-sync: true
    batch-size: 100
```

## 🏗️ 项目结构

```
src/
├── main/
│   ├── java/com/example/
│   │   ├── entity/              # JPA 实体类
│   │   ├── repository/          # 数据访问层
│   │   ├── service/            # 业务逻辑层
│   │   ├── controller/         # REST 控制器
│   │   ├── dto/                # 数据传输对象
│   │   └── mapper/             # 对象映射器
│   └── resources/
│       ├── application.yml     # 应用配置
│       ├── schema.sql         # 数据库脚本
│       └── data.sql           # 示例数据
└── test/                      # 测试代码
```

## 🧪 测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=FormSubmissionServiceTest

# 生成测试报告
mvn surefire-report:report
```

## 🚀 部署

### Docker 部署

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/form-submission-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# 构建 Docker 镜像
docker build -t form-submission-service .

# 运行容器
docker run -p 8080:8080 form-submission-service
```

### 生产环境部署

1. 修改 `application-prod.yml` 配置
2. 设置环境变量
3. 配置数据库连接
4. 部署到服务器

## 📈 监控和健康检查

应用包含Spring Boot Actuator端点：

```http
# 健康检查
GET /actuator/health

# 应用信息
GET /actuator/info

# 指标数据
GET /actuator/metrics
```

## 🔐 安全特性

- **输入验证**: Jakarta Validation 验证
- **SQL注入防护**: JPA 参数化查询
- **IP地址记录**: 自动记录提交者IP
- **数据完整性**: 数据库约束和验证

## 🛠️ 自定义和扩展

### 添加新字段

1. 在 `FormSubmissionEntity` 中添加新属性
2. 更新 DTO 类
3. 修改 Mapper 类
4. 更新数据库 schema
5. 更新 Google Sheets 表头

### 添加新的搜索功能

1. 在 `FormSubmissionRepository` 中添加查询方法
2. 在 `FormSubmissionService` 中添加业务逻辑
3. 在 `FormSubmissionController` 中添加 REST 端点

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证。详情请见 [LICENSE](LICENSE) 文件。

## 📞 支持

如果您在使用过程中遇到问题：

1. 查看 [Issues](https://github.com/Joseph19820124/form-submission-service/issues)
2. 提交新的 Issue
3. 查看项目文档
4. 联系项目维护者

## 🙏 致谢

- Spring Boot 团队
- Google Sheets API
- 所有贡献者

---

**Happy Coding!** 🎉