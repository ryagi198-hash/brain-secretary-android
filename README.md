# 脳内整理秘書 Android アプリ (BrainSecretary)

GAS WebApp版「脳内整理秘書サイト」を **WebView ラッパー** でAndroidネイティブアプリ化。
- ブラウザタブfaviconの制約を回避
- ホーム画面🧠アイコン
- スプラッシュスクリーン
- 戻るボタンで WebView 履歴遡行

## URL
https://script.google.com/a/macros/coop-s.co.jp/s/AKfycbxJeAF7J-4pbRJ--oZivwoWdo0L82oIQwzT-y9ESRno9v9cXL3nRpD7Gei0HWDd1qumHg/exec

## ビルド手順 (Android Studio)

1. Android Studio (Hedgehog 2023.1.1 以降) 開く
2. **Open an existing project** → `~/Desktop/claude_agent/secretary_brain_android/` 選択
3. Gradle Sync 待機 (初回 10〜20分・依存DL)
4. ツールバー右側で **app** module 選択 → 緑▶▷再生ボタンで Run
5. デバッグ用APK: `app/build/outputs/apk/debug/app-debug.apk` 生成
6. スマホ「設定 → セキュリティ → 不明なソースアプリ許可」→ APK ファイル開いてインストール

## アイコン

`app/src/main/res/mipmap-*/ic_launcher.png` を 🧠 画像で差し替え（現在は Android Studio デフォルト）.
- 推奨: 512x512 PNG `ic_launcher-playstore.png`
- adaptive icon: foreground 108dp + background 108dp

## OAuth 注意

WebView 内で Google OAuth (coop-s.co.jp Workspace) は Chrome Custom Tab が立ち上がる場合あり.
最初の起動時に GAS WebApp の権限承認が必要 (ブラウザ版で一度承認すれば WebView でもCookie共有可能性あり).

## 構成

```
secretary_brain_android/
├── app/
│   ├── build.gradle.kts          # アプリ build設定
│   └── src/main/
│       ├── AndroidManifest.xml   # INTERNET権限/Activity宣言
│       ├── java/com/coop/brainsecretary/MainActivity.kt  # WebView本体
│       └── res/
│           ├── layout/activity_main.xml   # WebView 1個
│           ├── values/strings.xml         # アプリ名
│           └── values/themes.xml          # コープ葬祭カラー (えんじ#A03E63)
├── build.gradle.kts              # rootビルド設定
├── settings.gradle.kts           # multi-module設定
├── gradle.properties             # Gradle設定
└── README.md
```

## 次セッション TODO

- [ ] gradle wrapper (gradlew) を生成 (Android Studio初回起動で自動生成)
- [ ] mipmap-* 配下に 🧠 アイコンを設置
- [ ] Splash screen で コープ葬祭ロゴ表示
- [ ] release APK 署名 (debug.keystoreで仮署名 → 本番は自分でkeystore作成)
- [ ] 共有Intent対応 (他アプリから秘書サイトのメモ即投入FABに送る)
- [ ] FCM プッシュ通知 (期限切れタスクの即時通知)

## デバッグ

WebView 内のコンソールログ確認:
```
adb logcat | grep -i "chromium\|webview\|console"
```
