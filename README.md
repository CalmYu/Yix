# Yix
a light weight hotfix framework

## Usage
1. 添加JitPack仓库
<pre>
maven { url "https://jitpack.io" }
</pre>
2. 添加依赖
<pre>
implementation 'com.github.CalmYu:Yix:1.0.0'
</pre>
3. 初始化
<pre>
// 初始化越早越好
HotFix.getInstance().initialize(this, BuildConfig.VERSION_CODE, false); // 可配置是否检查补丁签名
HotFix.getInstance().loadPatchIfExist();
</pre>
4. 安装补丁（冷启动生效）
<pre>
HotFix.getInstance().installPatch(patchFile)
</pre>

## 补丁生成
1. 添加gradle脚本
<pre>
classpath 'com.github.CalmYu:YixPatchMaker:1.0.0'
</pre>
2. 配置脚本
<pre>
apply plugin: 'hotfix.patch-maker'
Yix {
    fixClasses = ['yu.rainash.yix.app.TextJava', 'yu.rainash.yix.app.TextNative'] // 配置要修复的class
    fixSos = ['libhello.so'] // 配置要修复的so，关联的java也需要配置在fixClasses内
    outputFileName = 'patch.apk' // 输出补丁文件名
}
</pre>
3. 打补丁包(使用发布app时的打包命令即可)
<pre>
./gradlew :app:assembleRelease 
</pre>
