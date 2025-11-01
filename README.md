# ExamCountdown 高考倒计时插件

一个为Minecraft服务器设计的高考倒计时插件，会在玩家加入游戏时显示距离高考剩余的时间。

## 快速开始

1. 将插件JAR文件放入服务器的 `plugins` 文件夹
2. 重启服务器生成配置文件
3. 编辑 `plugins/ExamCountdown/config.yml` 进行个性化配置
4. 使用 `/ec reload` 重载配置或重启服务器

## 核心功能

- 🎯 **智能倒计时** - 自动计算距离高考的剩余时间
- 🎨 **高度可定制** - 支持自定义显示格式和颜色代码
- 🔌 **插件集成** - 完美支持PlaceholderAPI和LiteMotto
- 👤 **个人设置** - 允许玩家控制是否显示倒计时
- 🔄 **自动更新** - 定时刷新倒计时信息

## 命令与权限

### 命令
- `/examcountdown` 或 `/ec` - 主命令
  - `/ec on/off/toggle` - 控制倒计时显示
  - `/ec reload` - 重载配置（需要管理员权限）

### 权限
- `examcountdown.use` - 基础命令权限（默认所有玩家）
- `examcountdown.admin` - 管理员权限（默认仅OP）

## 配置说明

详细配置说明请查看生成的 `config.yml` 文件，其中包含了完整的占位符和PAPI变量说明。

## PlaceholderAPI支持

插件提供丰富的PAPI变量，具体变量列表和用法请参考配置文件中的详细注释。

## 构建与开发

```bash
# 构建插件
mvn clean package

# 运行测试
mvn test
```

## 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件