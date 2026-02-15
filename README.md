# 多层货位物流仓库调度优化系统

![Lines of Code](https://img.shields.io/endpoint?url=https://api.codetabs.com/v1/loc?github=Gloridust/multi-aisle-warehouse-scheduling)

![Top Languages](https://github-readme-stats.vercel.app/api/top-langs/?username=Gloridust&repo=multi-aisle-warehouse-scheduling&layout=donut&langs_count=8)

## 功能概述
- 仓库管理：配置层数、列数、托盘容积、堆垛机速度、入库口位置，自动生成货位
- 商品管理：SKU 维护与分类管理
- 货位管理：按层查看货位状态，支持锁定/解锁
- 入库订单：创建订单、执行策略分配、查看分配结果
- 策略仿真：对比三种策略的可视化与指标
- 仓库看板：实时查看货位占用与 SKU 信息

## 算法说明
- 分类存储策略（CATEGORY）：按商品分类分组，组内按顺序依次分配货位
- 就近原则策略（NEAREST）：按货位到入库口距离从近到远分配
- 遗传算法策略（GENETIC）：基于顺序编码、锦标赛选择、OX 交叉与交换变异优化总存取距离

## 编译与运行

### 后端（Spring Boot）
```bash
mvn -q -DskipTests package
mvn spring-boot:run
```
默认端口：8081

### 前端（Vue3 + Vite）
```bash
cd frontend
npm install
npm run dev
```

### 前端生产构建
```bash
cd frontend
npm run build
```
