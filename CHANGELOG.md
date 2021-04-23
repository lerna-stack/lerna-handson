# 変更履歴

lerna-handson に関する注目すべき変更はこのファイルで文書化されます。

このファイルの書き方については [Keep a Changelog](https://keepachangelog.com/ja/1.0.0/) を参照してください。

## Unreleased

- サンプルアプリを Akka Typed に移行します
  - Classic Actor を使うコードは Typed Actor を使うように変更されます
  - イベントのタグ付けは、`EventAdapter` の代わりに `EventSourcedBehavior.withTagger` を使用します
  - イベントとスナップショットの永続化がテストされます
- パッシベーションは、独自実装の代わりに [Auto Passivation](https://doc.akka.io/docs/akka/2.6.14/typed/cluster-sharding.html#automatic-passivation) を使用します
- リードモデルの更新に Akka Projection を使用します
- サンプルアプリのパッケージ構造が Lerna 標準構造に準拠します
- [lerna-stack/lerna-app-library](https://github.com/lerna-stack/lerna-app-library) への依存を廃止します
- Akka 2.6.14 に更新します
- Akka HTTP 10.2.4 に更新します


## v1.0.0
初回リリース
