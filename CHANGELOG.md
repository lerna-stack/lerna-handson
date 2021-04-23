# 変更履歴

lerna-handson に関する注目すべき変更はこのファイルで文書化されます。

このファイルの書き方については [Keep a Changelog](https://keepachangelog.com/ja/1.0.0/) を参照してください。

## Unreleased

### サンプルアプリの変更
- Akka Typed に移行します
  - 型安全な Typed Actor を使うように変更します
  - イベントのタグ付けに `EventSourcedBehavior.withTagger` を使用します  
    `EventSourcedBehavior` では、`EventAdapter` を使用せずにタグ付けできます。  
    詳細は [Tagging](https://doc.akka.io/docs/akka/current/typed/persistence.html#tagging) を確認してください。
  - イベントとスナップショットの永続化をテストします  
    Akka Typed では、永続化のテストが比較的容易に実施できます。  
    詳細は [Testing • Akka Documentation](https://doc.akka.io/docs/akka/current/typed/persistence-testing.html) を確認してください。
- パッシベーションは、独自実装の代わりに Akka によって提供されている [Auto Passivation](https://doc.akka.io/docs/akka/2.6.14/typed/cluster-sharding.html#automatic-passivation) を使用します  
  ハンズオンをシンプルにできます。
- リードモデルの更新に Akka Projection を使用します  
  独自実装を廃止し、Akka を最大限活用します。
- サンプルアプリのパッケージ構造を Lerna 標準構造に準拠させます
  
### 依存ライブラリの更新&廃止
- Akka 2.6.14 に更新します
- Akka HTTP 10.2.4 に更新します
- [lerna-stack/lerna-app-library](https://github.com/lerna-stack/lerna-app-library) への依存を廃止します


## v1.0.0
初回リリース
