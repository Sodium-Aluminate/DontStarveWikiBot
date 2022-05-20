# WikiBot
一些用于饥荒 wiki 模块儿的工具。

## 如何食用？
### 获取 bot 用户密码
在目标 wiki 打开 Special:BotPasswords，然后根据提示创建 bot。注意，你应该找全域管理员给你此时登陆的账户加 bot 标签，这样近期更改页面才不会被垃圾填满。

然后在需要登陆的地方设置用户变量 `WIKI_USERNAME` 和 `WIKI_PASSWD`，使用 `export` 命令或者直接 `WIKI_USERNAME=NaAlOH4 WIKI_PASSWD=NaAlOH4@114514 java -jar 1919810.jar` 都可以，随你喜欢。

不是 unix 系统？没救了等死吧告辞。

### 更新模板 "SmallIconAndText"
（需要登陆）保持程序一直跑着就好，用 systemd 或者其他工具，你喜欢用啥就用啥。

### 更新（或生成） Po 模板
0. 确保安装了 sed、unzip、bash。（不会吧不会吧？不会有人这种东西都没安装吧？恐怕只有按照教程安装 arch 还只装 base 的用户才会没有吧？）而且能访问 /tmp
1. 在游戏安装目录找到 `scripts.zip`，然后修改 `preparePo` 脚本中的 `scripts_dist`。 
2. 执行 preparePo.
3. 把单人版饥荒的 chinese_s.po 复制到 /tmp/dstScripts/chinese_s.po
4. 执行 PoParser.PoSorter，它会把所有字串排序然后分装到 lua 里。
5. (需要管理员 bot 登陆)执行 UpdatePo，它会把分装的文件上传到 `模块:Map` 和 `模块:MapChunk_n` 里。
6. (第一次执行) 当个文抄公，把 https://dontstarve.fandom.com/zh/wiki/Module:FromCode 抄到你的 wiki。

#### 不是饥荒中文 wiki?
首先，很抱歉我这个脚本设计的时候就没考虑其他语言/其他 wiki，不过这东西实际上倒是很好改。

如果你要翻译的语言不是简体中文，找到所有 "chinese_s"，换掉。

如果目标不是饥荒中文 wiki，找到 url "https://dontstarve.fandom.com/zh/api.php"，换掉。

如果你当了文抄公，Module:FromCode 里面的 zh 是中文的语言代码，**建议**换成目标语言代码。

**如果目标语言是英语，你得先生成一个英语的 po 文件，也就是英“译”英。**

（真的有汉语母语的用户给其他wiki搭建模块的吗，如果有，雷锋你好）