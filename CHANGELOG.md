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

### サブプロジェクト exercise-akka-persistence-basic の追加
Akka Persistence Typed の基礎に対応するコードサンプル、演習問題、解答を格納するため、  
サブプロジェクト `exercise-akka-persistence-basic` を追加します。

### ハンズオン資料の更新

- 章「Akka Actor の基礎」
  - 資料中の説明を Akka Typed に合わせて改修します
  - サンプル、演習、解答コード を Akka Typed に対応させます
  - tell, ask, 到達保証の説明を追加します
  - Akka Typed により適した演習問題に変更します
- 章「Akka Persistence の基礎」
  - Akka Persistence Typed に対応した説明資料、演習を追加します
- 章「Akka HTTP による HTTP 処理」
  - 資料中のサンプルコードを Akka HTTP 10.2.4, Akka Typed に対応させます
  - サンプルアプリの処理フロー図を `usecase` を使用しないシンプルなものに変更します
  - ファイルパスを Lerna 標準パッケージ構造に合うように更新します
  - サンプルコードや説明を改善します
  - 演習問題を新サンプルアプリに合うように更新します
- 章「Accord によるデータバリデーション」
  - ファイルパスを Lerna 標準パッケージ構造に合うように更新します
  - サンプルコードを改善します
- 章「Slick によるRDBアクセス」
  - リードモデルアップデータを Projection と呼ぶように変更します
  - サンプルアプリの処理フロー図を Akka Projection に対応するように更新します
  - Projection を担当するクラスを`ConcertRepository` から `ConcertProjectionRepository`に変更します
  - ファイルパスを Lerna 標準パッケージ構造に合うように更新します
  - サンプルコードや説明を改善します
  - 演習問題を新サンプルアプリに合うように更新します
  - 演習問題で使用するsbtコマンドを変更します

## v1.0.0
初回リリース
