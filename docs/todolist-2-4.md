一、评论 & 详情统计完善
1.1 详情里的评论数与关注状态
在 PostServiceImpl.getPostDetail 中：
[ ] 从 comment 表统计 commentCount，而不是写死 0。
[ ] 调用 FollowMapper，根据当前用户和作者 id，设置 vo.setIsFollowed(...)。
1.2 评论接口路径与分页
接口路由调整：
[ ] 新增（或改造）GET /api/post/{id}/comments，参数：page、size，调用 CommentService。
[ ] 新增（或改造）POST /api/post/{id}/comments，Body 使用 PublishCommentDTO（content, parentId）。
[ ] 保留或废弃旧的 /api/comment/{id} 路径，统一给前端用 /api/post/{id}/comments。
分页支持：
[ ] 在 CommentMapper.selectByPostId 支持 LIMIT + OFFSET，Service 层增加 page/size 入参，先做「根评论分页 + 子评论一并查询」的简单方案。
二、评论点赞（可选增强，但体验差距大）
2.1 数据层
[ ] 复用 interaction 表：约定评论的 target_type = 2。
[ ] 在 InteractionMapper 中增加：
[ ] insertInteraction / deleteInteraction 已有，直接复用。
2.2 Service + Controller
[ ] 在 CommentService 中增加 toggleLikeForComment(userId, commentId)。
[ ] 新增接口：POST /api/comment/{id}/like 或挂在 /api/post/{postId}/comments/{id}/like（二选一）：
[ ] 调互动表，改变评论点赞状态；
[ ] 返回当前是否已点赞 + 当前点赞数（可选）。
三、个人页三个列表（Profile 的三个 Tab）
3.1 接口
在 UserController 中新增：
[ ] GET /api/user/{userId}/posts?page=&size=：该用户发的帖子列表。
[ ] GET /api/user/{userId}/collects?page=&size=：该用户收藏的帖子列表。
[ ] GET /api/user/{userId}/likes?page=&size=：该用户点赞的帖子列表。
3.2 Service & Mapper
可以放在 PostService 或单独建 UserPostService：
[ ] getUserPosts(userId, page, size)：直接查 post 表，按 user_id 过滤，返回 List<PostCardVO>。
[ ] getUserCollects(userId, page, size)：用 interaction (type=2, target_type=1) 查出所有收藏的 postId，再去 post+user 拼 PostCardVO。
[ ] getUserLikes(userId, page, size)：用 interaction (type=1, target_type=1) 同样逻辑。
在 PostMapper.xml 中：
[ ] 新增对应的 SQL：按 user_id 查、按 post_id in (...) 查，resultType 仍用 PostCardVO。
四、通知中心接口（NotificationView）
4.1 设计 VO
[ ] 定义 NotificationVO（放在 vo 包）：
字段示例：id, type(comment/like/follow), actorName, actorAvatar, postTitle, time, message 等。
4.2 Service & Mapper
[ ] 新建 NotificationService 接口及实现：
[ ] getNotifications(userId, type, page, size)：
type=comment：从 comment 表查「别人给你的评论/回复」；
type=like/collect：从 interaction 表查「别人对你帖子/评论的点赞/收藏」；
type=follow：从 follow 表查「新增关注你的人」。
4.3 Controller
[ ] 新建 NotificationController：
[ ] GET /api/notification?type=comment|like|follow&page=&size=，返回 Result<List<NotificationVO>>。
> 注：这一块可以先实现「只查最近 N 条」的简单逻辑，不用一开始做已读状态。
五、接口风格 & REST 路径统一
5.1 统一前缀
[ ] 把 UserController 的 @RequestMapping("api/user") 改成 @RequestMapping("/api/user")，与其他 /api/... 保持一致。
5.2 评论接口路径
[ ] 对外只暴露 /api/post/{id}/comments；/api/comment/... 可以仅内部使用或废弃，避免前端混乱。
六、上传接口（如果你想加）
6.1 上传文件
[ ] 新建 UploadController：
[ ] POST /api/upload/image：接收 MultipartFile，保存到本地或对象存储，返回 URL。
6.2 与发布联动
[ ] 前端在 PublishPostView 中：先调用上传接口拿到 URL，再把 URL 列表传给 POST /api/post 的 images 字段。
七、登录鉴权（后续阶段）
这块你可以等接口都跑通后再做：
[ ] 引入登录方案（JWT 或 Session），实现登录接口。
[ ] 在各个 Controller 中替换 Long currentUserId = 1L，改为从登录上下文取当前用户 ID。
[ ] 可选：增加登录拦截器 / Spring Security 配置。
# 数据库：channel 字段从数字改为字符串 — 迁移说明

帖子频道已全链路统一为**字符串**：`adopt` | `knowledge` | `help`。数据库需要做如下变更。

---

## 一、需要改动的表与字段

### 1. `post` 表

| 原字段 | 原类型 | 新字段 | 新类型 | 说明 |
|--------|--------|--------|--------|------|
| category | TINYINT / INT | **channel** | **VARCHAR(32)** | 存 `'adopt'`、`'knowledge'`、`'help'` |

**取值对应关系**（迁移旧数据时用）：

- `1` → `'adopt'`
- `2` → `'knowledge'`
- `3` → `'help'`

---

## 二、推荐迁移步骤（MySQL）

### 方式 A：改列类型 + 改名列 + 迁移数据（原列叫 `category`）

```sql
-- 1. 新增字符串列
ALTER TABLE post ADD COLUMN channel VARCHAR(32) DEFAULT NULL COMMENT '频道: adopt | knowledge | help';

-- 2. 按旧数字迁移到新字符串
UPDATE post SET channel = CASE category
    WHEN 1 THEN 'adopt'
    WHEN 2 THEN 'knowledge'
    WHEN 3 THEN 'help'
    ELSE NULL
END WHERE category IS NOT NULL;

-- 3. 删除旧列
ALTER TABLE post DROP COLUMN category;

-- 4. （可选）为新列加索引，便于按频道筛选
-- CREATE INDEX idx_post_channel ON post(channel);
```

### 方式 B：直接改列类型（先转字符串再改取值）

若你希望保留列名 `category` 但存字符串，可以：

```sql
-- 1. 改为 VARCHAR
ALTER TABLE post MODIFY COLUMN category VARCHAR(32) NULL COMMENT '频道: adopt | knowledge | help';

-- 2. 把已有的 1/2/3 转成字符串
UPDATE post SET category = CASE category
    WHEN '1' THEN 'adopt'
    WHEN '2' THEN 'knowledge'
    WHEN '3' THEN 'help'
    ELSE category
END;
```

**注意**：当前后端与 Mapper 已使用列名 **`channel`**，若你采用方式 B 且保留列名为 `category`，需要在 **PostMapper.xml** 里把 `p.channel` 改回 `p.category`（即 SQL 里仍查 `category` 列，Java 实体和 VO 仍用 `channel` 字段名即可）。

**推荐**：采用**方式 A**，表结构与代码一致，列名为 `channel`。

---

## 三、迁移后约束建议

- 新写入只允许：`'adopt'`、`'knowledge'`、`'help'`（或 NULL，表示不按频道）。
- 可在应用层校验；若要在数据库层约束，可加 CHECK（MySQL 8.0.16+）：

```sql
ALTER TABLE post ADD CONSTRAINT chk_post_channel
CHECK (channel IS NULL OR channel IN ('adopt', 'knowledge', 'help'));
```

---

## 四、与代码的对应关系

| 层级 | 说明 |
|------|------|
| **数据库** | `post.channel` VARCHAR(32)，取值 `adopt` / `knowledge` / `help` |
| **后端 Entity** | `Post.channel`（String） |
| **后端 VO** | `PostCardVO.channel`（String），直接来自查询 |
| **后端 Service** | feed 筛选时传 `channel` 字符串，不再做数字转换 |
| **前端** | 请求参数 `channel`：`all` \| `adopt` \| `knowledge` \| `help`；列表项 `channel` 为上述三者之一，展示时用 `channelLabel` 转成中文 |

迁移完成后，重启后端即可；无需再改前后端代码（已按字符串实现）。
